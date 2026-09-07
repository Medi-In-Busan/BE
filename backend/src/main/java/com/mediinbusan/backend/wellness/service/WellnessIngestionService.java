package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediinbusan.backend.common.GeoDistance;
import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlace;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceType;
import com.mediinbusan.backend.wellness.dto.WellnessIngestionResponse;
import com.mediinbusan.backend.wellness.repository.WellnessPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class WellnessIngestionService {

    private static final Logger log = LoggerFactory.getLogger(WellnessIngestionService.class);
    private static final DateTimeFormatter TOUR_API_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    // 부산맛집정보 서비스는 totalCount 위치가 응답 포맷에 따라 달라질 수 있어, 대신 "이번 페이지가
    // numOfRows보다 적게 왔으면 마지막 페이지"로 판단한다. 이 상한은 무한 루프를 막기 위한 안전장치.
    private static final int BUSAN_FOOD_MAX_PAGES = 50;
    // areaBasedList2도 같은 방식으로 끝까지 페이지네이션한다 — 부산 기준 실측 최대(쇼핑 980건)보다
    // 훨씬 넉넉한 상한이다(기본 페이지당 100건 기준 5,000건까지 커버).
    private static final int TOUR_API_MAX_PAGES = 50;
    // 관광공사/부산맛집/카카오 세 소스가 같은 매장을 각자 다른 contentId로 들고 있는 경우가 흔하다
    // (예: "톤쇼우"가 세 소스 모두에서 잡힘). 이름이 같고 좌표가 이 반경 이내면 같은 물리적 장소로
    // 본다 — 프랜차이즈 지점은 보통 반경보다 훨씬 멀리 떨어져 있어 오탐(false merge) 위험이 낮다.
    private static final double DUPLICATE_RADIUS_METERS = 50.0;
    // TourAPI homepage/부산맛집 홈페이지 값에서 실제 URL만 건져내는 데 쓴다(extractUrl 참고).
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\s\"'<>]+");

    private final WellnessPlaceRepository wellnessPlaceRepository;
    private final WellnessIngestionProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WellnessIngestionService(
        WellnessPlaceRepository wellnessPlaceRepository,
        WellnessIngestionProperties properties
    ) {
        this.wellnessPlaceRepository = wellnessPlaceRepository;
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    @Transactional
    public WellnessIngestionResponse ingest() {
        if (!properties.hasTourApiKey() && !properties.hasBusanFoodApiKey()) {
            throw new ResponseStatusException(BAD_REQUEST, "TOURISM_API_SERVICE_KEY 또는 BUSAN_FOOD_API_SERVICE_KEY 환경변수가 필요합니다.");
        }
        // 키 원문은 절대 로그에 남기지 않는다(수집기로 시크릿이 유출될 수 있음) — 주입 여부/길이/접두
        // 4자만 찍어서 "환경변수가 비어있는지" vs "값은 있는데 API가 거부하는지"만 구분한다.
        log.info(
            "wellness ingest 시작 — tourApiKey={}, busanFoodKey={}",
            maskKey(properties.tourApiServiceKey()),
            maskKey(properties.busanFoodApiServiceKey())
        );

        // 카카오 로컬 키워드 검색(kakao-*)은 상세 내용(설명·이미지 등)이 거의 없어 더 이상 수집하지
        // 않는다 — 과거 ingest로 이미 들어간 행도 매번 정리해서, 이 배치가 한 번이라도 다시 돌면
        // 화면에서 빈 상세 카드가 사라지게 한다.
        int kakaoPlacesRemoved = (int) wellnessPlaceRepository.deleteByContentIdStartingWith("kakao-");

        List<WellnessPlaceCandidate> tourCandidates = properties.hasTourApiKey()
            ? fetchTourApiCandidates()
            : List.of();
        List<WellnessPlaceCandidate> busanFoodCandidates = properties.hasBusanFoodApiKey()
            ? fetchBusanFoodCandidates()
            : List.of();

        Map<String, WellnessPlaceCandidate> candidatesById = new LinkedHashMap<>();
        tourCandidates.forEach(candidate -> candidatesById.put(candidate.contentId(), candidate));
        busanFoodCandidates.forEach(candidate -> candidatesById.putIfAbsent(candidate.contentId(), candidate));

        // 이번에 새로 받아온 후보들 안에서 소스 간 중복(같은 이름 + 근접 좌표)을 먼저 걷어낸다.
        List<WellnessPlaceCandidate> mergedCandidates = dedupeCandidates(candidatesById.values());

        // 과거(이 중복 제거 로직이 없던 시절) ingest로 이미 DB에 들어가 있는 소스 간 중복도 정리한다.
        // 정리 이후의 상태를 스냅샷으로 들고 있다가, 아래 루프에서 "같은 물리적 장소인데 처음 보는
        // contentId"인 후보를 새로 insert하는 대신 이 스냅샷에서 찾아 그 기존 행을 갱신한다 — 이 폴백이
        // 없으면 이번 배치의 승자 contentId가 지난 ingest 때와 달라졌을 때 중복이 다시 생길 수 있다.
        int duplicatesRemoved = removeDuplicatePlaces();
        List<WellnessPlace> existingPlaces = wellnessPlaceRepository.findAll();

        int inserted = 0;
        int updated = 0;
        int skipped = 0;

        for (WellnessPlaceCandidate candidate : mergedCandidates) {
            if (!candidate.isValid()) {
                skipped++;
                continue;
            }

            WellnessPlace target = wellnessPlaceRepository.findByContentId(candidate.contentId())
                .orElseGet(() -> findNearDuplicate(existingPlaces, candidate));

            WellnessPlace place;
            if (target != null) {
                target.updateFrom(
                    candidate.name(),
                    candidate.placeType(),
                    candidate.categoryCode(),
                    candidate.address(),
                    candidate.coordinates(),
                    candidate.imageUrl(),
                    candidate.description(),
                    candidate.phoneNumber(),
                    candidate.modifiedDate()
                );
                place = target;
                updated++;
            } else {
                place = wellnessPlaceRepository.save(new WellnessPlace(
                    candidate.contentId(),
                    candidate.name(),
                    candidate.placeType(),
                    candidate.categoryCode(),
                    candidate.address(),
                    candidate.coordinates(),
                    candidate.imageUrl(),
                    candidate.description(),
                    candidate.phoneNumber(),
                    candidate.modifiedDate()
                ));
                existingPlaces.add(place);
                inserted++;
            }

            // 방문 정보만 updateFrom이 아니라 별도 메서드로 넣는다 — 빈 값을 무시해야 하기 때문이다.
            // 상세 조회 한도(enrichTourDetails) 밖으로 밀려난 장소가 지난 ingest에서 받아둔 영업시간·
            // 대표메뉴를 매번 잃지 않게 하려는 것이다.
            WellnessVisitInfo visitInfo = candidate.visitInfo();
            place.applyVisitInfo(
                visitInfo.businessHours(),
                visitInfo.restDate(),
                visitInfo.signatureMenu(),
                visitInfo.usageFee(),
                visitInfo.parkingInfo(),
                visitInfo.homepageUrl()
            );
        }

        // getFoodKr(위 루프)로 이미 저장된 busanfood-* 행에, 언어별 미러 API(getFoodEn/Ja/Zhs)의
        // 이름·주소·설명만 덧붙인다 — 좌표·이미지·전화번호는 언어 무관이라 Kr 값을 그대로 쓴다.
        int foodTranslationsApplied = 0;
        if (properties.hasBusanFoodApiKey()) {
            Map<String, WellnessPlace> placesByContentId = existingPlaces.stream()
                .collect(Collectors.toMap(WellnessPlace::getContentId, place -> place, (a, b) -> a));
            foodTranslationsApplied += applyBusanFoodTranslations("en", "/getFoodEn", placesByContentId);
            foodTranslationsApplied += applyBusanFoodTranslations("ja", "/getFoodJa", placesByContentId);
            // 앱이 지원하는 중국어는 하나(ZH)뿐이라 간체(Zhs)만 받는다 — 번체(getFoodZht)는 대응하는
            // 언어 슬롯이 없어 호출하지 않는다.
            foodTranslationsApplied += applyBusanFoodTranslations("zh", "/getFoodZhs", placesByContentId);
        }

        // tour-* 행에는 언어별 미러 서비스(EngService2/JpnService2/ChsService2)를 위치기반으로 조회해
        // 붙인다 — contentId로는 매칭이 안 되는 게 실측으로 확인돼 있어(applyTourTranslationsByLocation
        // 문서 참고) 부산맛집과는 다른 방식(이름+좌표 매칭)을 쓴다.
        int tourTranslationsApplied = 0;
        if (properties.hasTourApiKey()) {
            tourTranslationsApplied += applyTourTranslationsByLocation("en", properties.englishTourismBaseUrl(), existingPlaces);
            tourTranslationsApplied += applyTourTranslationsByLocation("ja", properties.japaneseTourismBaseUrl(), existingPlaces);
            tourTranslationsApplied += applyTourTranslationsByLocation("zh", properties.chineseTourismBaseUrl(), existingPlaces);
        }

        return new WellnessIngestionResponse(
            tourCandidates.size(),
            busanFoodCandidates.size(),
            inserted,
            updated,
            skipped,
            duplicatesRemoved,
            kakaoPlacesRemoved,
            foodTranslationsApplied,
            tourTranslationsApplied,
            wellnessPlaceRepository.count()
        );
    }

    private List<WellnessPlaceCandidate> fetchTourApiCandidates() {
        List<WellnessPlaceCandidate> candidates = new ArrayList<>();
        fetchTourApiList("12", WellnessPlaceType.TOURIST_ATTRACTION, candidates);
        fetchTourApiList("39", WellnessPlaceType.RESTAURANT, candidates);
        fetchTourApiList("32", WellnessPlaceType.LODGING, candidates);
        fetchTourApiList("38", WellnessPlaceType.SHOPPING, candidates);
        logCategoryCodeDistribution(candidates);
        return enrichTourDetails(candidates);
    }

    /**
     * 이번 수집에서 실제로 내려온 cat3 코드 분포를 한 줄로 남긴다.
     *
     * WellnessDtoMapper.categoryOf의 코드 표는 TourAPI 문서를 근거로 적었을 뿐 실제 응답으로
     * 검증된 적이 없다 — 표에 없는 코드는 조용히 OTHER로 떨어져서, 로그가 없으면 "세분화가 왜 안
     * 되지"를 추적할 방법이 없다. 이 로그와 categoryOf를 대조하면 틀린 코드를 바로 잡을 수 있다.
     */
    private void logCategoryCodeDistribution(List<WellnessPlaceCandidate> candidates) {
        Map<String, Long> distribution = candidates.stream()
            .filter(candidate -> candidate.categoryCode() != null && !candidate.categoryCode().isBlank())
            .collect(Collectors.groupingBy(WellnessPlaceCandidate::categoryCode, TreeMap::new, Collectors.counting()));
        if (distribution.isEmpty()) {
            log.warn("TourAPI 수집 결과에 cat3 분류 코드가 하나도 없다 — 응답 필드명이 바뀌었는지 확인할 것");
            return;
        }
        log.info("TourAPI cat3 분류 코드 분포(WellnessDtoMapper.categoryOf 표와 대조할 것): {}", distribution);
    }

    private void fetchTourApiList(String contentTypeId, WellnessPlaceType placeType, List<WellnessPlaceCandidate> candidates) {
        int numOfRows = properties.tourApiRowsPerPage();
        for (int pageNo = 1; pageNo <= TOUR_API_MAX_PAGES; pageNo++) {
            URI uri = UriComponentsBuilder
                .fromUriString(properties.tourApiBaseUrl())
                .path("/areaBasedList2")
                .queryParam("serviceKey", properties.tourApiServiceKey())
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "MediInBusan")
                .queryParam("_type", "json")
                // 2026-01 이후 일반 TourAPI 권장 지역 필터는 areaCode가 아닌 법정동 코드다.
                .queryParam("lDongRegnCd", BusanTourismCodes.LDONG_REGN_CD)
                .queryParam("contentTypeId", contentTypeId)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .build(true)
                .toUri();

            JsonNode items = readJson(uri, null).at("/response/body/items/item");
            List<JsonNode> pageItems = asArray(items);
            pageItems.forEach(item -> candidates.add(toTourCandidate(item, placeType)));
            if (pageItems.size() < numOfRows) {
                break;
            }
        }
    }

    /**
     * areaBasedList2 응답엔 전화번호(tel)·상세설명·방문 정보가 실려오지 않는다(부산 기준 실측:
     * 전화번호 0%, 설명은 코드에서 애초에 null로 저장) — 콘텐츠ID별로 상세 오퍼레이션을 추가로
     * 호출해야 채워진다.
     *
     * <p>호출하는 오퍼레이션은 <b>둘</b>이고 각각 별개의 일일 트래픽 한도를 쓴다:
     * <ul>
     *   <li>{@code detailCommon2} — tel / overview / homepage
     *   <li>{@code detailIntro2} — 영업시간·휴무일·대표메뉴·주차 등 방문 정보(콘텐츠 타입마다 필드명이
     *       다르다 — {@link #fetchTourIntro} 참고). 이쪽은 한 번도 부르지 않고 있어서, 상세 화면이
     *       "사진 + 이름 + 지도"에서 더 나아가지 못하던 원인이었다.
     * </ul>
     *
     * <p>그래서 한도도 오퍼레이션별로 따로 둔다({@link WellnessIngestionProperties#tourApiDetailFetchLimit()},
     * {@link WellnessIngestionProperties#tourApiIntroFetchLimit()}) — 한쪽 트래픽만 소진됐을 때 다른
     * 쪽까지 같이 멈추지 않게 하고, 0으로 두면 그 보강만 통째로 건너뛴다.
     *
     * <p><b>한도를 누구에게 쓸지</b>는 {@link #enrichmentOrder}(유형 라운드로빈)와
     * {@link #selectTargets}(아직 안 채워진 것 우선)가 정한다. 예전엔 후보 리스트 순서를 앞에서부터
     * 그냥 잘라 썼는데, 그 순서가 관광지→음식점→숙박→쇼핑 고정이라 300건이 관광지에서 전부 소진되고
     * 음식점·쇼핑·숙박은 매번 한 건도 못 받았다(실측: 관광지 351건 중 300건만 설명이 있고 나머지
     * 세 유형은 전부 0건). 유형을 번갈아 호출해 첫 실행부터 네 유형이 고르게 채워지게 한다.
     *
     * <p><b>호출은 병렬로, 저장은 순차로.</b> 이 메서드에서 하는 일은 HTTP 호출과 불변 record 생성뿐이라
     * 워커 스레드에 나눠 줄 수 있다({@link #runFetchTasks}) — 600건 기준 3~10분이 1~2분으로 줄고,
     * 그만큼 ingest 트랜잭션이 열려 있는 시간도 짧아진다. 반대로 엔티티를 만지는 코드(updateFrom /
     * applyVisitInfo / applyTranslation / save)는 <b>절대</b> 병렬화하면 안 된다 — ingest 전체가 하나의
     * {@code @Transactional}이고 영속성 컨텍스트는 호출 스레드에 묶여 있다.
     *
     * <p>총 호출 수는 동시성과 무관하게 위 두 한도 그대로다 — 병렬화는 <b>같은 양을 더 빨리</b>
     * 가져올 뿐이고, 하루에 채울 수 있는 장소 수는 늘지 않는다.
     */
    private List<WellnessPlaceCandidate> enrichTourDetails(List<WellnessPlaceCandidate> candidates) {
        int detailLimit = properties.tourApiDetailFetchLimit();
        int introLimit = properties.tourApiIntroFetchLimit();
        if (detailLimit <= 0 && introLimit <= 0) {
            return candidates;
        }
        Map<String, WellnessPlace> stored = wellnessPlaceRepository.findAll().stream()
            .collect(Collectors.toMap(WellnessPlace::getContentId, place -> place, (left, right) -> left));
        List<WellnessPlaceCandidate> order = enrichmentOrder(candidates);
        Set<String> detailTargets = selectTargets(order, detailLimit, c -> needsDetail(stored.get(c.contentId())));
        Set<String> introTargets = selectTargets(order, introLimit, c -> needsIntro(stored.get(c.contentId())));

        // 어떤 후보에 무엇을 호출할지 인덱스별로 먼저 확정한다 — 호출하면서 세면 스레드 실행 순서에
        // 따라 건수가 흔들려 한도를 넘길 수 있다. 여기서 정해두면 동시성을 몇으로 두든 호출 대상과
        // 건수가 항상 똑같다(같은 contentId가 후보에 두 번 들어와도 한 번만 부른다).
        boolean[] callDetail = new boolean[candidates.size()];
        boolean[] callIntro = new boolean[candidates.size()];
        Set<String> detailPlanned = new HashSet<>();
        Set<String> introPlanned = new HashSet<>();
        int detailCalls = 0;
        int introCalls = 0;
        for (int index = 0; index < candidates.size(); index++) {
            String contentId = candidates.get(index).contentId();
            if (detailCalls < detailLimit && detailTargets.contains(contentId) && detailPlanned.add(contentId)) {
                callDetail[index] = true;
                detailCalls++;
            }
            if (introCalls < introLimit && introTargets.contains(contentId) && introPlanned.add(contentId)) {
                callIntro[index] = true;
                introCalls++;
            }
        }

        // 결과는 후보와 같은 자리에 써넣는다 — 뒤따르는 중복 제거(dedupeCandidates)와 upsert가
        // 후보 순서를 그대로 쓰므로 순서가 흔들리면 안 된다. 스레드마다 다른 인덱스만 건드리고,
        // 아래 runFetchTasks가 전부 끝날 때까지 기다린 뒤에야 읽는다.
        WellnessPlaceCandidate[] enriched = candidates.toArray(new WellnessPlaceCandidate[0]);
        AtomicInteger detailFailures = new AtomicInteger();
        AtomicInteger introFailures = new AtomicInteger();
        List<Runnable> tasks = new ArrayList<>();
        for (int index = 0; index < candidates.size(); index++) {
            if (!callDetail[index] && !callIntro[index]) {
                continue;
            }
            int slot = index;
            tasks.add(() -> {
                enriched[slot] = enrichOne(
                    candidates.get(slot), callDetail[slot], callIntro[slot], detailFailures, introFailures
                );
            });
        }
        int concurrency = runFetchTasks(tasks, properties.tourApiFetchConcurrency());
        long detailPending = order.stream().filter(c -> needsDetail(stored.get(c.contentId()))).count() - detailCalls;
        long introPending = order.stream().filter(c -> needsIntro(stored.get(c.contentId()))).count() - introCalls;
        if (detailPending > 0 || introPending > 0) {
            // 남았다는 건 실패가 아니라 "이번 몫을 다 썼다"는 뜻이다 — 저장된 값이 이제 지워지지
            // 않으므로(WellnessPlace.updateFrom) ingest를 다시 돌리면 그 다음 몫이 이어서 채워진다.
            log.warn(
                "TourAPI 상세 보강 한도 도달 — 설명 미보강 {}건, 방문정보 미보강 {}건 남음(ingest를 다시 실행하면 이어서 채워진다)",
                Math.max(detailPending, 0), Math.max(introPending, 0)
            );
        }
        log.info(
            "TourAPI 상세 보강 호출 — 동시성 {}, detailCommon2 {}/{}건(실패 {}), detailIntro2 {}/{}건(실패 {})",
            concurrency, detailCalls, detailLimit, detailFailures.get(), introCalls, introLimit, introFailures.get()
        );
        if (detailFailures.get() + introFailures.get() > 0) {
            // 개별 실패는 삼키고 지나가므로(그 장소만 다음 실행에서 다시 시도된다) 여기서 합계를
            // 남기지 않으면 "왜 안 채워지지"를 추적할 방법이 없다 — 특히 동시 호출을 늘린 뒤
            // 거부당하기 시작한 경우가 그렇다.
            log.warn(
                "TourAPI 상세 보강 실패 {}건 — 동시 호출이 많아 거부됐을 수 있다."
                    + " 계속 늘어나면 wellness.ingestion.tour-api-fetch-concurrency를 낮출 것(현재 {})",
                detailFailures.get() + introFailures.get(), concurrency
            );
        }
        return List.of(enriched);
    }

    /**
     * 한 후보의 상세 보강 호출. 워커 스레드에서 실행되므로 <b>JPA 엔티티를 절대 건드리지 않는다</b> —
     * ingest 전체가 하나의 트랜잭션이고 영속성 컨텍스트는 호출 스레드에 묶여 있어서, 다른 스레드에서
     * 엔티티를 만지면 조용히 깨진다. 여기서 하는 일은 HTTP 호출과 불변 record 생성뿐이다.
     */
    private WellnessPlaceCandidate enrichOne(
        WellnessPlaceCandidate candidate,
        boolean fetchDetail,
        boolean fetchIntro,
        AtomicInteger detailFailures,
        AtomicInteger introFailures
    ) {
        String tel = null;
        String overview = null;
        WellnessVisitInfo visitInfo = WellnessVisitInfo.EMPTY;

        if (fetchDetail) {
            TourDetail detail = fetchTourDetail(properties.tourApiBaseUrl(), candidate.contentId());
            if (detail == null) {
                detailFailures.incrementAndGet();
            } else {
                tel = detail.tel();
                overview = detail.overview();
                visitInfo = visitInfo.merge(
                    new WellnessVisitInfo(null, null, null, null, null, detail.homepage())
                );
            }
        }
        if (fetchIntro) {
            WellnessVisitInfo intro = fetchTourIntro(candidate);
            if (intro == null) {
                introFailures.incrementAndGet();
            } else {
                visitInfo = visitInfo.merge(intro);
            }
        }

        return tel == null && overview == null && visitInfo.isEmpty()
            ? candidate
            : candidate.withDetail(tel, overview, visitInfo);
    }

    /**
     * 상세 보강 호출들을 실행하고 실제로 쓴 동시성을 돌려준다.
     *
     * <p>고정 크기 풀을 쓴다 — 가상 스레드나 {@code parallelStream()}은 동시 호출 수의 상한을 정할
     * 수 없어서, 공공 API가 초당·동시 호출 제한으로 거부하기 시작하면 손쓸 방법이 없다. 여기서는
     * 설정값 하나로 상한을 조일 수 있고, 1로 두면 풀을 아예 만들지 않고 순차 실행한다.
     *
     * <p>{@code ExecutorService.close()}(Java 19+)가 제출된 작업이 전부 끝날 때까지 기다린 뒤 풀을
     * 정리한다 — 이 메서드가 반환된 시점에는 모든 결과가 배열에 다 쓰여 있다.
     */
    static int runFetchTasks(List<Runnable> tasks, int requestedConcurrency) {
        int concurrency = Math.max(requestedConcurrency, 1);
        if (concurrency == 1 || tasks.size() <= 1) {
            tasks.forEach(Runnable::run);
            return 1;
        }
        int poolSize = Math.min(concurrency, tasks.size());
        try (ExecutorService pool = Executors.newFixedThreadPool(poolSize)) {
            tasks.forEach(pool::execute);
        }
        return poolSize;
    }

    /**
     * 보강 호출을 배정할 순서 — 유형별로 나눠 한 건씩 번갈아 뽑는다(관광지1, 음식점1, 숙박1, 쇼핑1,
     * 관광지2, ...). 유형별 보유량 차이가 커서(부산 기준 쇼핑 976 / 음식점 420 / 관광지 351 /
     * 숙박 119) 리스트 순서대로 자르면 앞 유형 하나가 한도를 전부 먹는다.
     *
     * <p>유형별 정액 할당(예: 각 75건씩)이 아니라 라운드로빈인 이유: 보유량이 적은 유형은 일찍
     * 소진되고 남는 몫이 자동으로 다른 유형에 흘러간다 — 유형 수나 보유량이 바뀌어도 손댈 게 없다.
     */
    static List<WellnessPlaceCandidate> enrichmentOrder(List<WellnessPlaceCandidate> candidates) {
        Map<WellnessPlaceType, List<WellnessPlaceCandidate>> byType = candidates.stream()
            .collect(Collectors.groupingBy(
                WellnessPlaceCandidate::placeType, LinkedHashMap::new, Collectors.toList()
            ));
        List<WellnessPlaceCandidate> order = new ArrayList<>(candidates.size());
        int longestGroup = byType.values().stream().mapToInt(List::size).max().orElse(0);
        for (int index = 0; index < longestGroup; index++) {
            for (List<WellnessPlaceCandidate> group : byType.values()) {
                if (index < group.size()) {
                    order.add(group.get(index));
                }
            }
        }
        return order;
    }

    /**
     * 이번 ingest에서 실제로 호출할 대상 — 아직 값이 없는 장소부터 한도만큼 고른다.
     *
     * 저장된 값이 더 이상 지워지지 않으므로(WellnessPlace.updateFrom / applyVisitInfo), 이렇게 하면
     * ingest를 여러 번 돌릴수록 채워진 장소가 누적된다 — 부산 기준 tour 1,866건이면 한도 300으로
     * 일곱 번쯤 돌리면 전부 채워진다. 예전에는 매번 같은 앞쪽 300건만 다시 불렀다.
     *
     * <p>한계: "아직 안 불러봤다"와 "불러봤는데 API가 빈 값을 줬다"를 구분하지 못해, 원래 값이 없는
     * 장소는 매 실행마다 다시 호출된다. 마지막 시도 시각을 컬럼으로 두면 해결되지만, 실측상
     * detailCommon2는 호출한 300건 전부가 설명을 돌려줘서 빈 응답 비율이 낮다고 보고 그대로 둔다.
     *
     * <p>전부 채워졌으면 남는 한도로 앞에서부터 다시 호출해 갱신한다 — 한도를 놀리지 않는다.
     */
    private static Set<String> selectTargets(
        List<WellnessPlaceCandidate> order,
        int limit,
        Predicate<WellnessPlaceCandidate> needsFill
    ) {
        if (limit <= 0) {
            return Set.of();
        }
        Set<String> targets = new LinkedHashSet<>();
        for (WellnessPlaceCandidate candidate : order) {
            if (targets.size() >= limit) {
                return targets;
            }
            if (needsFill.test(candidate)) {
                targets.add(candidate.contentId());
            }
        }
        for (WellnessPlaceCandidate candidate : order) {
            if (targets.size() >= limit) {
                break;
            }
            targets.add(candidate.contentId());
        }
        return targets;
    }

    /**
     * 설명(overview) 기준으로 판단한다 — 전화번호가 아니다.
     *
     * 관광지는 detailCommon2를 불러도 tel이 거의 항상 비어 있어서(부산 기준 실측 0%), tel을 기준으로
     * 삼으면 이미 부른 장소를 매번 "아직 안 채워졌다"로 오판해 같은 300건만 무한히 다시 부르게 된다.
     */
    private static boolean needsDetail(WellnessPlace stored) {
        return stored == null || stored.getDescription() == null;
    }

    /** 방문 정보는 여섯 칸 중 하나라도 차 있으면 채워진 것으로 본다(유형마다 주는 칸이 다르다). */
    private static boolean needsIntro(WellnessPlace stored) {
        return stored == null
            || (stored.getBusinessHours() == null
            && stored.getRestDate() == null
            && stored.getSignatureMenu() == null
            && stored.getUsageFee() == null
            && stored.getParkingInfo() == null);
    }

    private record TourDetail(String tel, String overview, String homepage) {
    }

    private TourDetail fetchTourDetail(String baseUrl, String prefixedContentId) {
        String contentId = rawContentId(prefixedContentId);
        try {
            // KorService2는 defaultYN/overviewYN 같은 상세 필터 파라미터를 더 이상 받지 않는다
            // (실제 호출 시 INVALID_REQUEST_PARAMETER_ERROR) — contentId만 넘기면 tel/overview를
            // 포함한 전체 필드가 기본으로 내려온다. EngService2/JpnService2/ChsService2도 같은
            // 파라미터 계약을 쓴다(실측 확인).
            URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/detailCommon2")
                .queryParam("serviceKey", properties.tourApiServiceKey())
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "MediInBusan")
                .queryParam("_type", "json")
                .queryParam("contentId", contentId)
                .build(true)
                .toUri();

            JsonNode item = readJson(uri, null).at("/response/body/items/item");
            JsonNode first = item.isArray() ? item.path(0) : item;
            return new TourDetail(
                text(first, "tel"),
                cleanOverview(text(first, "overview")),
                // homepage는 순수 URL이 아니라 <a href="...">...</a> 앵커 태그로 온다 — 그대로
                // 저장하면 앱이 링크로 열 수 없다.
                extractUrl(text(first, "homepage"))
            );
        } catch (RuntimeException e) {
            // 상세 조회 하나가 실패했다고 ingest 전체를 막지 않는다 — 그 항목만 기존 값 그대로 저장된다.
            log.warn("TourAPI detailCommon2 조회 실패(contentId={})", contentId, e);
            return null;
        }
    }

    /**
     * detailIntro2로 방문 정보(영업시간·휴무일·대표메뉴·이용요금·주차)를 받아온다.
     *
     * <p>이 오퍼레이션은 <b>contentTypeId가 필수</b>이고, 같은 뜻의 값을 타입마다 다른 필드명으로
     * 내려준다(관광지 {@code usetime} / 음식점 {@code opentimefood} / 쇼핑 {@code opentime} ...).
     * 타입별로 분기하는 대신 후보 필드명을 우선순위대로 훑는다 — 한 응답에 다른 타입의 필드가
     * 같이 들어있는 경우가 없어서 결과가 같고, 나중에 수집 타입을 늘려도 이 표만 늘리면 된다.
     *
     * <p>{@code usageFee}는 우리가 수집하는 4개 타입(12/39/32/38)에는 아예 없는 필드다 — 문화시설(14)·
     * 레포츠(28)에만 있어서, 그 타입이 들어오는 관광 카탈로그 상세 경로(TourismPlaceMatchService)에서만
     * 실제로 채워진다. 여기서도 같이 훑는 건 두 경로가 같은 6칸 모델을 쓰게 하려는 것이다.
     *
     * @return 호출 실패면 {@code null}, 성공했지만 줄 값이 없으면 {@link WellnessVisitInfo#EMPTY}.
     *         둘을 구분하는 이유는 아래 catch 블록 주석 참고.
     */
    private WellnessVisitInfo fetchTourIntro(WellnessPlaceCandidate candidate) {
        String contentTypeId = contentTypeIdOf(candidate.placeType());
        if (contentTypeId == null) {
            return WellnessVisitInfo.EMPTY;
        }
        String contentId = rawContentId(candidate.contentId());
        try {
            URI uri = UriComponentsBuilder
                .fromUriString(properties.tourApiBaseUrl())
                .path("/detailIntro2")
                .queryParam("serviceKey", properties.tourApiServiceKey())
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "MediInBusan")
                .queryParam("_type", "json")
                .queryParam("contentId", contentId)
                .queryParam("contentTypeId", contentTypeId)
                .build(true)
                .toUri();

            JsonNode item = readJson(uri, null).at("/response/body/items/item");
            JsonNode first = item.isArray() ? item.path(0) : item;
            return toVisitInfo(first);
        } catch (RuntimeException e) {
            // detailCommon2와 같은 이유로 개별 실패는 삼킨다 — 그 장소만 방문 정보 없이 저장된다.
            // 다만 "실패"와 "성공했는데 줄 게 없었다"(EMPTY)는 구분해서 돌려준다 — 호출부가 실패
            // 건수를 세야 동시 호출을 늘렸을 때 거부당하기 시작한 걸 알아챌 수 있다.
            log.warn("TourAPI detailIntro2 조회 실패(contentId={}, contentTypeId={})", contentId, contentTypeId, e);
            return null;
        }
    }

    /**
     * detailIntro2 응답 한 건을 6칸 방문 정보로 옮긴다.
     *
     * 관광 카탈로그 상세(TourismPlaceMatchService)도 같은 필드명 표를 써야 두 상세 화면이 같은
     * 장소에서 같은 값을 보여주므로 package-private static으로 열어둔다.
     */
    static WellnessVisitInfo toVisitInfo(JsonNode intro) {
        if (intro == null || !intro.isObject()) {
            return WellnessVisitInfo.EMPTY;
        }
        return new WellnessVisitInfo(
            introText(intro, "usetime", "opentimefood", "opentime", "usetimeculture", "usetimeleports", "checkintime"),
            introText(intro, "restdate", "restdatefood", "restdateshopping", "restdateculture", "restdateleports"),
            // 대표메뉴가 비어 있으면 취급메뉴로 내려간다 — 둘 다 "무엇을 파는 집인가"를 말한다.
            introText(intro, "firstmenu", "treatmenu"),
            introText(intro, "usefee", "usefeeleports"),
            introText(intro, "parking", "parkingfood", "parkingshopping", "parkingculture", "parkingleports", "parkinglodging"),
            null
        );
    }

    /** detailIntro2 값에도 &lt;br&gt; 태그와 HTML 엔티티가 섞여 온다 — overview와 같은 규칙으로 정리한다. */
    private static String introText(JsonNode intro, String... fields) {
        for (String field : fields) {
            String value = cleanOverview(text(intro, field));
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * TourAPI contentTypeId. {@link #fetchTourApiCandidates}가 수집하는 4개 타입의 역매핑이다 —
     * 그 목록을 늘리면 여기도 같이 늘려야 detailIntro2를 부를 수 있다(모르는 타입은 호출을 건너뛴다).
     */
    private static String contentTypeIdOf(WellnessPlaceType placeType) {
        return switch (placeType) {
            case TOURIST_ATTRACTION -> "12";
            case RESTAURANT -> "39";
            case LODGING -> "32";
            case SHOPPING -> "38";
            default -> null;
        };
    }

    private static String rawContentId(String prefixedContentId) {
        return prefixedContentId.startsWith("tour-")
            ? prefixedContentId.substring("tour-".length())
            : prefixedContentId;
    }

    /**
     * 문자열에서 첫 번째 http(s) URL만 뽑는다.
     *
     * TourAPI homepage는 {@code <a href="http://..." target="_blank">http://...</a>} 앵커로 오고,
     * 부산맛집정보 홈페이지도 소스에 따라 태그가 섞여 있다 — 앱은 이 값을 그대로 인텐트로 열기
     * 때문에 URL이 아닌 문자열이 들어가면 링크 버튼이 조용히 아무 동작도 안 하게 된다.
     */
    static String extractUrl(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        Matcher matcher = URL_PATTERN.matcher(HtmlUtils.htmlUnescape(raw));
        if (!matcher.find()) {
            return null;
        }
        // 앵커 태그 안에서 뽑으면 닫는 따옴표나 '>'가 뒤에 붙는다.
        return matcher.group().replaceAll("[\"'<>]+$", "");
    }

    /**
     * detailCommon2의 overview는 HTML 엔티티(&amp;rsquo; 등)와 &amp;lt;br /&amp;gt; 태그가 섞여 오는데,
     * 그나마도 일관되지 않다 — 같은 문자열 안에 진짜 태그(&lt;br /&gt;)와 이중 이스케이프된 태그
     * (&amp;lt;br /&amp;gt;)가 같이 나오는 경우가 실측으로 확인됐다(특히 ZH). 그래서 먼저
     * 언이스케이프해서 이중 이스케이프를 한 번 풀어준 다음, br을 줄바꿈으로 바꾸고 나머지 태그를
     * 지운다 — TourismPlaceMatchService가 같은 문제를 이미 이렇게 정리하고 있어 순서만 맞춰 재사용.
     */
    private static String cleanOverview(String raw) {
        if (raw == null) {
            return null;
        }
        String unescaped = HtmlUtils.htmlUnescape(raw);
        String cleaned = unescaped.replaceAll("(?i)<br\\s*/?>", "\n").replaceAll("<[^>]+>", "").strip();
        return cleaned.isBlank() ? null : cleaned;
    }

    // 부산시청 기준 반경 — 부산 16개 구·군 전부를 넉넉히 덮는다(가덕도·기장 외곽 포함, 실측으로
    // 확인). EngService2/JpnService2/ChsService2엔 KorService2 같은 lDongRegnCd 지역 필터가 안 먹어서
    // (실측: lDongRegnCd로 걸면 항상 0건) 위치 기반 조회(locationBasedList2)로 대신한다.
    private static final double BUSAN_CENTER_LATITUDE = 35.1796;
    private static final double BUSAN_CENTER_LONGITUDE = 129.0756;
    private static final int BUSAN_COVERAGE_RADIUS_METERS = 25_000;
    // "Haeundae Beach (해운대해수욕장)"처럼 언어별 미러 서비스가 제목 끝에 원문 한글명을 괄호로
    // 붙여주는 관행을 이용한다(EN/JA/ZH 전부 실측 확인 — JA/ZH는 전각 괄호 （）도 쓴다).
    private static final Pattern TRAILING_KOREAN_NAME = Pattern.compile("[（(]([^（）()]+)[）)]\\s*$");

    private record TourMirrorItem(String contentId, String title, String koreanName, String address, Double latitude, Double longitude) {
    }

    /**
     * TourAPI 언어별 미러 서비스(EngService2/JpnService2/ChsService2)로 이미 저장된 {@code tour-*}
     * 웰니스 장소에 이름·주소·설명 번역을 덧붙인다. {@code contentId}로 직접 매칭할 수 없다는 게
     * 실측으로 확인됐다 — KorService2가 areaBasedList2(contentTypeId 12/32/38/39)로 수집한 부산
     * 1,968건과 EngService2의 부산 반경 173건이 {@code contentId} 기준으로는 단 한 건도 안 겹쳤다
     * (언어별 서비스가 KorService2와 다른 콘텐츠 분류 체계·ID 공간을 쓰는 것으로 보임 — 아마 문서에
     * 언급된 "신규 상세기능(신분류코드)" 전환과 관련). 그래서 이름(제목 끝 괄호 안 원문 한글) +
     * 좌표 50m 이내(기존 소스 간 중복 제거와 같은 기준, {@link #DUPLICATE_RADIUS_METERS})가 둘 다
     * 맞을 때만 "확실히 같은 장소"로 보고 번역을 적용한다 — 이름만으로는 매칭하지 않는다(오탐 방지).
     * 이 방식의 실측 매칭률은 100%가 아니다(부산시청 반경 25km EN 173건 중 58건 ≈ 33%)지만, 해운대
     * 해수욕장·자갈치시장·태종대·용두산공원 등 외국인 관광객이 실제로 찾을 만한 주요 관광지 위주로
     * 잘 매칭된다.
     */
    private int applyTourTranslationsByLocation(String lang, String baseUrl, List<WellnessPlace> existingPlaces) {
        try {
            List<TourMirrorItem> mirrorItems = fetchTourMirrorItems(baseUrl);
            int applied = 0;
            for (TourMirrorItem mirrorItem : mirrorItems) {
                if (mirrorItem.koreanName() == null || mirrorItem.latitude() == null || mirrorItem.longitude() == null) {
                    continue;
                }
                String normalizedName = normalizePlaceName(mirrorItem.koreanName());
                if (normalizedName == null) {
                    continue;
                }
                WellnessPlace matched = findByNameAndLocation(existingPlaces, normalizedName, mirrorItem.latitude(), mirrorItem.longitude());
                if (matched == null) {
                    continue;
                }
                // 확실히 같은 장소로 확인된 항목만 detailCommon2를 추가로 불러 설명(overview)까지 채운다 —
                // 매칭 안 된 항목까지 다 부르지 않으므로 tourApiDetailFetchLimit 같은 별도 한도가 없어도
                // 호출 수가 자연스럽게 매칭 건수만큼만 늘어난다.
                TourDetail detail = fetchTourDetail(baseUrl, mirrorItem.contentId());
                matched.applyTranslation(lang, mirrorItem.title(), mirrorItem.address(), detail != null ? detail.overview() : null);
                applied++;
            }
            return applied;
        } catch (RuntimeException e) {
            log.warn("TourAPI {} 위치기반 번역 매칭 실패 — 원문(한국어) 데이터로 폴백합니다.", baseUrl, e);
            return 0;
        }
    }

    private WellnessPlace findByNameAndLocation(List<WellnessPlace> places, String normalizedName, double latitude, double longitude) {
        Coordinates origin = new Coordinates(latitude, longitude);
        for (WellnessPlace place : places) {
            if (place.getCoordinates() == null || !normalizedName.equals(normalizePlaceName(place.getName()))) {
                continue;
            }
            Double distanceMeters = GeoDistance.meters(origin, place.getCoordinates());
            if (distanceMeters != null && distanceMeters <= DUPLICATE_RADIUS_METERS) {
                return place;
            }
        }
        return null;
    }

    private List<TourMirrorItem> fetchTourMirrorItems(String baseUrl) {
        List<TourMirrorItem> items = new ArrayList<>();
        int numOfRows = properties.tourApiRowsPerPage();
        for (int pageNo = 1; pageNo <= TOUR_API_MAX_PAGES; pageNo++) {
            URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/locationBasedList2")
                .queryParam("serviceKey", properties.tourApiServiceKey())
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "MediInBusan")
                .queryParam("_type", "json")
                .queryParam("mapX", BUSAN_CENTER_LONGITUDE)
                .queryParam("mapY", BUSAN_CENTER_LATITUDE)
                .queryParam("radius", BUSAN_COVERAGE_RADIUS_METERS)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .build(true)
                .toUri();

            JsonNode itemsNode = readJson(uri, null).at("/response/body/items/item");
            List<JsonNode> pageItems = asArray(itemsNode);
            pageItems.forEach(item -> items.add(toTourMirrorItem(item)));
            if (pageItems.size() < numOfRows) {
                break;
            }
        }
        return items;
    }

    private TourMirrorItem toTourMirrorItem(JsonNode item) {
        String title = text(item, "title");
        return new TourMirrorItem(
            text(item, "contentid"),
            title,
            extractKoreanName(title),
            firstNonBlank(text(item, "addr1"), text(item, "addr2")),
            doubleValue(item, "mapy"),
            doubleValue(item, "mapx")
        );
    }

    private static String extractKoreanName(String title) {
        if (title == null) {
            return null;
        }
        Matcher matcher = TRAILING_KOREAN_NAME.matcher(title);
        if (!matcher.find()) {
            return null;
        }
        String candidate = matcher.group(1).strip();
        return containsHangul(candidate) ? candidate : null;
    }

    private static boolean containsHangul(String value) {
        return value.chars().anyMatch(cp -> cp >= 0xAC00 && cp <= 0xD7A3);
    }

    private WellnessPlaceCandidate toTourCandidate(JsonNode item, WellnessPlaceType placeType) {
        String contentId = text(item, "contentid");
        return new WellnessPlaceCandidate(
            "tour-" + contentId,
            text(item, "title"),
            placeType,
            // areaBasedList2 응답의 분류 코드. cat1(A04)/cat2(A0401)는 cat3의 접두사라 cat3만 받는다.
            text(item, "cat3"),
            firstNonBlank(text(item, "addr1"), text(item, "addr2")),
            coordinates(doubleValue(item, "mapy"), doubleValue(item, "mapx")),
            firstNonBlank(text(item, "firstimage"), text(item, "firstimage2")),
            null,
            text(item, "tel"),
            parseTourApiDate(text(item, "modifiedtime")),
            // areaBasedList2엔 방문 정보가 아예 없다 — detailIntro2를 따로 부르는 enrichTourDetails가 채운다.
            WellnessVisitInfo.EMPTY
        );
    }

    private List<WellnessPlaceCandidate> fetchBusanFoodCandidates() {
        List<WellnessPlaceCandidate> candidates = new ArrayList<>();
        int numOfRows = properties.busanFoodRowsPerPage();
        for (int pageNo = 1; pageNo <= BUSAN_FOOD_MAX_PAGES; pageNo++) {
            URI uri = UriComponentsBuilder
                .fromUriString(properties.busanFoodApiBaseUrl())
                .path("/getFoodKr")
                .queryParam("serviceKey", properties.busanFoodApiServiceKey())
                .queryParam("resultType", "json")
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .build(true)
                .toUri();

            JsonNode root = readJson(uri, null);
            List<JsonNode> pageItems = asArray(locateBusanFoodItems(root));
            pageItems.forEach(item -> candidates.add(toBusanFoodCandidate(item)));
            if (pageItems.size() < numOfRows) {
                break;
            }
        }
        return candidates;
    }

    private WellnessPlaceCandidate toBusanFoodCandidate(JsonNode item) {
        String contentId = text(item, "UC_SEQ");
        return new WellnessPlaceCandidate(
            "busanfood-" + contentId,
            text(item, "MAIN_TITLE"),
            WellnessPlaceType.RESTAURANT,
            // 부산맛집정보는 TourAPI 분류 체계를 쓰지 않는다 — 세부 분류 없음.
            null,
            firstNonBlank(text(item, "ADDR1"), text(item, "ADDR2")),
            coordinates(doubleValue(item, "LAT"), doubleValue(item, "LNG")),
            firstNonBlank(text(item, "MAIN_IMG_NORMAL"), text(item, "MAIN_IMG_THUMB")),
            // 대표메뉴는 더 이상 여기 문자열로 이어붙이지 않는다 — 아래 visitInfo의 제 칸으로 간다.
            text(item, "ITEMCNTNTS"),
            text(item, "CNTCT_TEL"),
            LocalDate.now(),
            busanFoodVisitInfo(item)
        );
    }

    /**
     * 부산맛집정보(getFoodKr) 응답에서 방문 정보를 뽑는다.
     *
     * 예전엔 대표메뉴(RPRSNTV_MENU)를 설명 앞에 "대표메뉴: ..."로 이어붙이고 영업시간·홈페이지는
     * 아예 읽지도 않았다 — 접합된 문자열은 화면에서 다시 분리할 수 없어서 "대표메뉴" 칩 하나를
     * 만들려 해도 방법이 없었다.
     *
     * <p>필드명은 이 서비스의 응답 스키마 기준인데, 언어별 미러(getFoodEn 등)와 마찬가지로 실제
     * 호출로 전수 검증하지는 못했다. 이름이 틀리면 해당 칸만 조용히 null이 될 뿐 수집 자체는
     * 멀쩡히 돌아간다 — 그래서 영업시간·홈페이지는 흔한 표기 변형까지 같이 시도한다.
     */
    private WellnessVisitInfo busanFoodVisitInfo(JsonNode item) {
        return new WellnessVisitInfo(
            firstNonBlank(text(item, "USAGE_DAY_WEEK_AND_TIME"), text(item, "USAGE_DAY_WEEK_AND_TIME_")),
            null,
            text(item, "RPRSNTV_MENU"),
            null,
            null,
            extractUrl(firstNonBlank(text(item, "HOMEPAGE_URL"), text(item, "HOMPAGE_URL")))
        );
    }

    /**
     * 부산맛집정보 언어별 미러 API(getFoodEn/getFoodJa/getFoodZhs) 하나를 받아 이미 저장된 장소에
     * 이름·주소·설명을 덧붙인다. 실패해도(스키마 추정이 어긋났거나, 이 서비스키가 해당 언어
     * 오퍼레이션은 아직 승인받지 못한 경우 등) 예외를 밖으로 던지지 않는다 — 번역은 부가 정보라
     * 실패해도 원문(한국어) 데이터로 서비스는 계속되어야 한다(ingest() 전체가 실패하면 안 됨).
     */
    private int applyBusanFoodTranslations(String lang, String operation, Map<String, WellnessPlace> placesByContentId) {
        try {
            List<WellnessPlaceTranslation> translations = fetchBusanFoodTranslations(operation);
            int applied = 0;
            for (WellnessPlaceTranslation translation : translations) {
                WellnessPlace place = placesByContentId.get(translation.contentId());
                if (place == null) {
                    // Kr 패스에서 안 잡힌 항목(신규/철수 매장 등 언어 미러 간 목록 차이) — 건너뛴다.
                    continue;
                }
                place.applyTranslation(lang, translation.name(), translation.address(), translation.description());
                applied++;
            }
            return applied;
        } catch (RuntimeException e) {
            log.warn("부산맛집정보 {} 번역 적재 실패 — 원문(한국어) 데이터로 폴백합니다.", operation, e);
            return 0;
        }
    }

    /**
     * getFoodEn/getFoodJa/getFoodZhs가 getFoodKr(fetchBusanFoodCandidates)과 같은 필드명
     * (UC_SEQ/MAIN_TITLE/ADDR1/ADDR2/RPRSNTV_MENU/ITEMCNTNTS)을 쓰고, UC_SEQ가 언어 간에 같은
     * 매장을 가리키는 동일 키라는 가정 하에 동작한다 — 공식 문서에 언어별 응답 스키마가 없어
     * 실제 호출로 검증하지 못했다(locateBusanFoodItems의 같은 문제와 동일). 가정이 틀리면
     * applyBusanFoodTranslations가 0건으로 조용히 건너뛸 뿐 ingest 전체는 깨지지 않는다.
     */
    private List<WellnessPlaceTranslation> fetchBusanFoodTranslations(String operation) {
        List<WellnessPlaceTranslation> translations = new ArrayList<>();
        int numOfRows = properties.busanFoodRowsPerPage();
        for (int pageNo = 1; pageNo <= BUSAN_FOOD_MAX_PAGES; pageNo++) {
            URI uri = UriComponentsBuilder
                .fromUriString(properties.busanFoodApiBaseUrl())
                .path(operation)
                .queryParam("serviceKey", properties.busanFoodApiServiceKey())
                .queryParam("resultType", "json")
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .build(true)
                .toUri();

            JsonNode root = readJson(uri, null);
            List<JsonNode> pageItems = asArray(locateBusanFoodItems(root));
            pageItems.forEach(item -> translations.add(toBusanFoodTranslation(item)));
            if (pageItems.size() < numOfRows) {
                break;
            }
        }
        return translations;
    }

    private WellnessPlaceTranslation toBusanFoodTranslation(JsonNode item) {
        String contentId = text(item, "UC_SEQ");
        return new WellnessPlaceTranslation(
            "busanfood-" + contentId,
            text(item, "MAIN_TITLE"),
            firstNonBlank(text(item, "ADDR1"), text(item, "ADDR2")),
            // 한국어 원문(toBusanFoodCandidate)과 같은 필드만 쓴다 — 대표메뉴는 설명이 아니라
            // 방문 정보 칸으로 갔으므로 여기서도 이어붙이지 않는다.
            text(item, "ITEMCNTNTS")
        );
    }

    /**
     * 이번에 새로 받아온 후보들 중 같은 물리적 장소(정규화한 이름 + {@link #DUPLICATE_RADIUS_METERS}
     * 이내 좌표)를 가리키는 것들을 하나로 합친다. 여러 후보가 겹치면 정보(이미지·설명·전화번호)가
     * 더 많은 쪽을 남긴다.
     */
    private List<WellnessPlaceCandidate> dedupeCandidates(Collection<WellnessPlaceCandidate> source) {
        List<WellnessPlaceCandidate> kept = new ArrayList<>();
        for (WellnessPlaceCandidate candidate : source) {
            int duplicateIndex = indexOfDuplicateCandidate(kept, candidate);
            if (duplicateIndex < 0) {
                kept.add(candidate);
            } else if (richness(candidate) > richness(kept.get(duplicateIndex))) {
                kept.set(duplicateIndex, candidate);
            }
        }
        return kept;
    }

    private int indexOfDuplicateCandidate(List<WellnessPlaceCandidate> kept, WellnessPlaceCandidate target) {
        String normalizedName = normalizePlaceName(target.name());
        if (normalizedName == null || target.coordinates() == null) {
            return -1;
        }
        for (int i = 0; i < kept.size(); i++) {
            WellnessPlaceCandidate existing = kept.get(i);
            if (existing.coordinates() == null || !normalizedName.equals(normalizePlaceName(existing.name()))) {
                continue;
            }
            Double distanceMeters = GeoDistance.meters(target.coordinates(), existing.coordinates());
            if (distanceMeters != null && distanceMeters <= DUPLICATE_RADIUS_METERS) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 이번 배치의 승자 contentId가 지난 ingest 때와 다를 수 있어(소스 응답이 매번 똑같지 않음),
     * findByContentId로 못 찾은 후보라도 바로 insert하지 않고 이미 저장된 장소 중 같은 물리적
     * 장소가 있는지 한 번 더 찾아본다 — 이걸 생략하면 이전 ingest가 골랐던 행은 그대로 남은 채
     * 이번엔 다른 contentId로 새 행이 또 insert되어 중복이 재발한다.
     */
    private WellnessPlace findNearDuplicate(List<WellnessPlace> existingPlaces, WellnessPlaceCandidate candidate) {
        String normalizedName = normalizePlaceName(candidate.name());
        if (normalizedName == null || candidate.coordinates() == null) {
            return null;
        }
        for (WellnessPlace place : existingPlaces) {
            if (place.getCoordinates() == null || !normalizedName.equals(normalizePlaceName(place.getName()))) {
                continue;
            }
            Double distanceMeters = GeoDistance.meters(candidate.coordinates(), place.getCoordinates());
            if (distanceMeters != null && distanceMeters <= DUPLICATE_RADIUS_METERS) {
                return place;
            }
        }
        return null;
    }

    /**
     * 이 중복 제거 로직이 생기기 전에 이미 DB에 들어간 소스 간 중복(같은 장소, 다른 contentId)을
     * 정리한다. 매번 전체 테이블을 메모리로 읽어와 비교하는 건 {@link WellnessService}가 이미 쓰는
     * 방식과 같다 — wellness_place는 admin 배치 규모라 O(n²) 비교도 충분히 빠르다.
     */
    private int removeDuplicatePlaces() {
        List<WellnessPlace> allPlaces = wellnessPlaceRepository.findAll();
        Set<WellnessPlace> toDelete = new LinkedHashSet<>();

        for (int i = 0; i < allPlaces.size(); i++) {
            WellnessPlace current = allPlaces.get(i);
            if (toDelete.contains(current) || current.getCoordinates() == null) {
                continue;
            }
            String normalizedName = normalizePlaceName(current.getName());
            if (normalizedName == null) {
                continue;
            }
            for (int j = i + 1; j < allPlaces.size(); j++) {
                WellnessPlace other = allPlaces.get(j);
                if (toDelete.contains(other) || other.getCoordinates() == null) {
                    continue;
                }
                if (!normalizedName.equals(normalizePlaceName(other.getName()))) {
                    continue;
                }
                Double distanceMeters = GeoDistance.meters(current.getCoordinates(), other.getCoordinates());
                if (distanceMeters != null && distanceMeters <= DUPLICATE_RADIUS_METERS) {
                    toDelete.add(richness(other) > richness(current) ? current : other);
                }
            }
        }

        if (!toDelete.isEmpty()) {
            wellnessPlaceRepository.deleteAll(toDelete);
        }
        return toDelete.size();
    }

    private static int richness(WellnessPlaceCandidate candidate) {
        int score = 0;
        if (candidate.imageUrl() != null) score++;
        if (candidate.description() != null) score++;
        if (candidate.phoneNumber() != null) score++;
        return score;
    }

    private static int richness(WellnessPlace place) {
        int score = 0;
        if (place.getImageUrl() != null) score++;
        if (place.getDescription() != null) score++;
        if (place.getPhoneNumber() != null) score++;
        return score;
    }

    /** 소스마다 공백/괄호/기호 표기가 달라 같은 곳이 다르게 보일 수 있어, 비교용으로만 정규화한다. */
    private static String normalizePlaceName(String name) {
        if (name == null) {
            return null;
        }
        String normalized = name.replaceAll("[\\s()\\[\\]·ㆍ\\-]", "");
        return normalized.isBlank() ? null : normalized;
    }

    /**
     * 부산광역시_부산맛집정보 서비스는 관광공사 TourAPI(response/body/items/item)와 응답 포맷이 다르고,
     * 문서에 명시된 봉투(envelope) 구조를 실제 호출로 검증하지 못했다 — 그래서 고정 경로 대신 "UC_SEQ
     * 필드를 가진 객체(또는 그 객체들의 배열)"를 트리에서 재귀 탐색해 어떤 포맷으로 와도 항목을 찾는다.
     */
    private JsonNode locateBusanFoodItems(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isObject() && node.has("UC_SEQ")) {
            return node;
        }
        if (node.isArray()) {
            for (JsonNode element : node) {
                if (element.isObject() && element.has("UC_SEQ")) {
                    return node;
                }
            }
            return null;
        }
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                JsonNode found = locateBusanFoodItems(node.path(fieldNames.next()));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private JsonNode readJson(URI uri, String authorization) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(uri).GET();
            if (authorization != null) {
                builder.header("Authorization", authorization);
            }
            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                // data.go.kr류 공공 API는 403 본문에 SERVICE_ACCESS_DENIED_ERROR 등 실제 사유를 담아 보낸다 —
                // 상태코드만으로는 서비스키 미승인/오타/IP제한을 구분할 수 없어 본문을 그대로 노출한다.
                throw new IllegalStateException(
                    "외부 API 응답 실패: HTTP " + response.statusCode() + " body=" + truncate(response.body(), 500)
                );
            }
            return objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new IllegalStateException("외부 API 응답을 파싱하지 못했습니다.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("외부 API 호출이 중단되었습니다.", e);
        }
    }

    private List<JsonNode> asArray(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return List.of();
        }
        if (node.isArray()) {
            List<JsonNode> values = new ArrayList<>();
            node.forEach(values::add);
            return values;
        }
        return List.of(node);
    }

    private Coordinates coordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        return new Coordinates(latitude, longitude);
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        String text = value.asText();
        return text.isBlank() ? null : text;
    }

    private static Double doubleValue(JsonNode node, String field) {
        String value = text(node, field);
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static LocalDate parseTourApiDate(String value) {
        if (value == null || value.length() < 8) {
            return LocalDate.now();
        }
        return LocalDate.parse(value.substring(0, 8), TOUR_API_DATE);
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength) + "...";
    }

    private static String maskKey(String value) {
        if (value == null || value.isBlank()) {
            return "(empty)";
        }
        String prefix = value.substring(0, Math.min(4, value.length()));
        return prefix + "***(len=" + value.length() + ")";
    }
}
