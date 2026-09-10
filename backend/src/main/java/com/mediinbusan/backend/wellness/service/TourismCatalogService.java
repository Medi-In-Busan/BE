package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mediinbusan.backend.wellness.domain.WellnessExternalSnapshot;
import com.mediinbusan.backend.wellness.domain.TourismCatalogCategory;
import com.mediinbusan.backend.wellness.dto.TourismCatalogItemResponse;
import com.mediinbusan.backend.wellness.dto.TourismCatalogResponse;
import com.mediinbusan.backend.wellness.dto.TourismExternalResponse;
import com.mediinbusan.backend.wellness.repository.WellnessExternalSnapshotRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.LocalDate;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

@Service
public class TourismCatalogService {

    private static final double BUSAN_CENTER_LATITUDE = 35.1796;
    private static final double BUSAN_CENTER_LONGITUDE = 129.0756;
    /**
     * details 맵에 담는 스칼라 필드 수 상한.
     *
     * 8칸이던 시절엔 응답 필드 순서에 그대로 끌려다녔다 — 앞쪽에 내부 코드(areacode/mlevel/
     * lclsSystm* 등)가 몰려 있는 응답에서는 정작 화면에 그릴 값(거리·소요시간·난이도)이 잘려나가
     * "방문 정보" 카드가 통째로 비었다. 칸을 늘리고, 그래도 넘칠 때를 대비해 아래
     * {@link #PRIORITY_DETAIL_FIELDS}를 먼저 담는다.
     */
    private static final int MAX_DETAILS = 16;

    /**
     * 화면에 라벨이 있는 필드 — 이 순서대로 먼저 담는다.
     *
     * Android {@code TourismStrings.detailFieldLabels}와 짝이다. 앱은 라벨이 없는 키를 조용히 버리므로
     * (라벨 없는 값을 원문 필드명 그대로 보여줄 순 없다), 한쪽만 늘리면 늘린 필드가 화면에 안 나온다 —
     * 새 필드를 노출하려면 <b>양쪽을 같이</b> 고쳐야 한다.
     */
    private static final List<String> PRIORITY_DETAIL_FIELDS = List.of(
        // detailIntro2 방문 정보(TourismPlaceMatchService가 붙인다)
        "businessHours", "restDate", "signatureMenu", "usageFee", "parkingInfo",
        "tel", "distance", "requiredTime", "leadTime",
        "baseYmd", "baseYm", "signguNm", "rlteSignguNm",
        "crsDstnc", "crsTotlRqrmHour", "crsLevel",
        "rlteCtgryMclsNm", "hubCtgryMclsNm", "themeCategory", "daywkDivNm"
    );
    private static final String CROWDING_CACHE_SOURCE = "crowding-catalog";
    private static final String CROWDING_CACHE_SCOPE = "BUSAN";
    private static final int HOT_PLACE_LIMIT = 5;
    /**
     * 한 요청에서 사진을 새로 찾아볼 최대 개수.
     *
     * 예전엔 상위 {@link #HOT_PLACE_LIMIT}개만 찾아봐서, 추천 웰니스의 TOP5는 사진이 붙는데
     * "전체보기"로 들어간 혼잡도 목록(오늘 기준 30여 개)은 나머지가 전부 사진 없이 남았다.
     * 그렇다고 한 요청에서 전부 찾으면 그 요청 하나가 외부 API를 수십 번 때려 응답이 크게 느려진다.
     * 그래서 요청마다 아직 안 찾아본 것 위주로 이만큼씩만 채우고, 결과는 그때그때 오늘치 스냅샷에
     * 저장한다 — 몇 번 오가는 사이 목록 전체가 채워지고, 그 뒤로는 캐시라 공짜다.
     */
    private static final int IMAGE_LOOKUP_BATCH = 8;
    private static final String IMAGE_LOOKUP_ATTEMPTED = "imageLookupAttempted";
    /**
     * 사진 보강을 시도했다는 표시의 값(= 그때 쓴 매칭 로직의 세대).
     *
     * 표시가 있으면 같은 장소를 매 요청마다 다시 찾지 않는데, 예전엔 값이 "true" 하나뿐이라
     * <b>매칭 로직을 개선해도 이미 실패로 표시된 장소는 영영 다시 시도하지 않았다</b> — 오늘치
     * 스냅샷에 "true"로 남은 장소들이 사진 없이 그대로 굳었다. 로직을 고칠 때 이 값을 올리면
     * 예전 세대로 표시된 장소만 딱 한 번 다시 시도한다(캐시를 손으로 지울 필요가 없다).
     *
     * v2: 간이 검색이 실패하면 상세 화면과 같은 매칭기(TourismPlaceMatchService)로 재시도.
     */
    private static final String IMAGE_LOOKUP_GENERATION = "2";

    private final WellnessTourismGatewayService gateway;
    private final WellnessExternalSnapshotRepository snapshotRepository;
    private final TourismCatalogTranslationService translationService;
    private final ObjectProvider<TourismPlaceMatchService> placeMatchService;
    private final ObjectMapper objectMapper;

    public TourismCatalogService(
        WellnessTourismGatewayService gateway,
        WellnessExternalSnapshotRepository snapshotRepository,
        TourismCatalogTranslationService translationService,
        // ObjectProvider로 받는 이유: TourismPlaceMatchService가 이 서비스(normalizeItems)를 생성자로
        // 주입받고 있어 서로 참조하면 빈 생성 단계에서 순환이 된다. 지연 조회로 끊는다 — 실제 호출은
        // 아래 enrichCrowdingImages에서 하루 최대 몇 번뿐이다(결과는 혼잡도 스냅샷에 캐시된다).
        ObjectProvider<TourismPlaceMatchService> placeMatchService
    ) {
        this.gateway = gateway;
        this.snapshotRepository = snapshotRepository;
        this.translationService = translationService;
        this.placeMatchService = placeMatchService;
        this.objectMapper = new ObjectMapper();
    }

    public TourismCatalogResponse getCatalog(
        TourismCatalogCategory category,
        BusanTourismCodes.District district,
        String baseYm
    ) {
        return getCatalog(category, district, baseYm, 1, 50, "ko");
    }

    public TourismCatalogResponse getCatalog(
        TourismCatalogCategory category,
        BusanTourismCodes.District district,
        String baseYm,
        int page,
        int pageSize,
        String language
    ) {
        if (category == TourismCatalogCategory.CROWDING) {
            TourismCatalogResponse crowding = getBusanCrowdingCatalog();
            if (district != null) {
                List<TourismCatalogItemResponse> districtItems = crowding.items().stream()
                    .filter(item -> district.bigdataSignguCd().equals(item.details().get("signguCd")))
                    .toList();
                crowding = withCrowdingItems(crowding, districtItems, crowding.retrievedAt());
            }
            return translationService.localize(crowding, language);
        }
        BusanTourismCodes.District resolvedDistrict = district == null ? BusanTourismCodes.District.HAEUNDAE : district;
        String resolvedBaseYm = hasText(baseYm) ? baseYm : YearMonth.now().minusMonths(2).format(DateTimeFormatter.ofPattern("yyyyMM"));
        int resolvedPage = Math.max(page, 1);
        int resolvedPageSize = Math.max(1, Math.min(pageSize, 50));

        TourismExternalResponse external = switch (category) {
            case PLACES_KO -> gateway.places(WellnessTourismGatewayService.Language.KO, district, null, resolvedPage, resolvedPageSize);
            case PLACES_EN -> gateway.places(WellnessTourismGatewayService.Language.EN, district, null, resolvedPage, resolvedPageSize);
            case PLACES_JA -> gateway.places(WellnessTourismGatewayService.Language.JA, district, null, resolvedPage, resolvedPageSize);
            case PLACES_ZH -> gateway.places(WellnessTourismGatewayService.Language.ZH, district, null, resolvedPage, resolvedPageSize);
            case ACCESSIBLE -> gateway.accessibility(district, resolvedPage, resolvedPageSize);
            case RELATED -> gateway.related(resolvedDistrict, resolvedBaseYm);
            case HUBS -> gateway.hubs(resolvedDistrict, resolvedBaseYm);
            case CROWDING -> gateway.crowding(resolvedDistrict);
            case PHOTOS -> gateway.photos("부산");
            case WALKING -> gateway.walkingCourses();
            case AUDIO -> gateway.audio(BUSAN_CENTER_LATITUDE, BUSAN_CENTER_LONGITUDE);
        };

        List<TourismCatalogItemResponse> items = normalizeItems(objectMapper.valueToTree(external.data()));
        if (category == TourismCatalogCategory.CROWDING) {
            items = todayCrowdingItems(items, LocalDate.now());
        }
        return translationService.localize(new TourismCatalogResponse(
            category,
            category.title(),
            category.description(),
            external.source(),
            external.retrievedAt(),
            items
        ), language);
    }

    private synchronized TourismCatalogResponse getBusanCrowdingCatalog() {
        LocalDate today = LocalDate.now();
        String snapshotKey = crowdingSnapshotKey(today);
        var todaySnapshot = snapshotRepository.findBySnapshotKey(snapshotKey);
        if (todaySnapshot.isPresent()) {
            TourismCatalogResponse cached = cachedCrowdingResponse(todaySnapshot.get());
            List<TourismCatalogItemResponse> enrichedItems = enrichCrowdingImages(cached.items());
            if (!enrichedItems.equals(cached.items())) {
                todaySnapshot.get().refresh(
                    TourismCatalogCategory.CROWDING.title(),
                    null,
                    null,
                    serializeCrowdingItems(enrichedItems)
                );
                snapshotRepository.save(todaySnapshot.get());
                return withCrowdingItems(cached, enrichedItems, todaySnapshot.get().syncedAt());
            }
            return cached;
        }

        List<TourismCatalogItemResponse> items = new ArrayList<>();
        RuntimeException firstFailure = null;
        Instant retrievedAt = null;
        for (BusanTourismCodes.District district : BusanTourismCodes.districts()) {
            try {
                TourismExternalResponse external = gateway.crowding(district);
                items.addAll(normalizeItems(objectMapper.valueToTree(external.data())));
                if (retrievedAt == null) {
                    retrievedAt = external.retrievedAt();
                }
            } catch (RuntimeException exception) {
                firstFailure = exception;
                break;
            }
        }

        if (firstFailure == null && !items.isEmpty()) {
            items = enrichCrowdingImages(todayCrowdingItems(items, today));
            TourismCatalogResponse response = new TourismCatalogResponse(
                TourismCatalogCategory.CROWDING,
                TourismCatalogCategory.CROWDING.title(),
                TourismCatalogCategory.CROWDING.description(),
                "crowding-forecast",
                retrievedAt,
                items
            );
            snapshotRepository.save(new WellnessExternalSnapshot(
                snapshotKey,
                CROWDING_CACHE_SOURCE,
                CROWDING_CACHE_SCOPE,
                CROWDING_CACHE_SCOPE,
                today.toString(),
                TourismCatalogCategory.CROWDING.title(),
                null,
                null,
                serializeCrowdingItems(items)
            ));
            return response;
        }

        var latestSnapshot = snapshotRepository
            .findTopBySourceAndScopeOrderBySyncedAtDesc(CROWDING_CACHE_SOURCE, CROWDING_CACHE_SCOPE);
        if (latestSnapshot.isPresent()) {
            return cachedCrowdingResponse(latestSnapshot.get());
        }
        if (firstFailure != null) {
            throw firstFailure;
        }
        return new TourismCatalogResponse(
            TourismCatalogCategory.CROWDING,
            TourismCatalogCategory.CROWDING.title(),
            TourismCatalogCategory.CROWDING.description(),
            "crowding-forecast",
            retrievedAt,
            List.of()
        );
    }

    private String serializeCrowdingItems(List<TourismCatalogItemResponse> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (Exception exception) {
            throw new IllegalStateException("혼잡도 캐시를 직렬화하지 못했습니다.", exception);
        }
    }

    private TourismCatalogResponse cachedCrowdingResponse(WellnessExternalSnapshot snapshot) {
        try {
            List<TourismCatalogItemResponse> cachedItems = objectMapper.readValue(
                snapshot.payload(),
                new TypeReference<>() {}
            );
            List<TourismCatalogItemResponse> items = cachedItems.stream()
                .map(item -> new TourismCatalogItemResponse(
                    item.id(),
                    item.title(),
                    item.subtitle(),
                    item.address(),
                    secureImageUrl(item.imageUrl()),
                    item.latitude(),
                    item.longitude(),
                    item.categoryCode(),
                    item.details()
                ))
                .toList();
            items = todayCrowdingItems(items, LocalDate.now());
            return new TourismCatalogResponse(
                TourismCatalogCategory.CROWDING,
                TourismCatalogCategory.CROWDING.title(),
                TourismCatalogCategory.CROWDING.description(),
                "crowding-forecast",
                snapshot.syncedAt(),
                items
            );
        } catch (Exception exception) {
            throw new IllegalStateException("저장된 혼잡도 캐시를 읽지 못했습니다.", exception);
        }
    }

    private TourismCatalogResponse withCrowdingItems(
        TourismCatalogResponse response,
        List<TourismCatalogItemResponse> items,
        Instant retrievedAt
    ) {
        return new TourismCatalogResponse(
            response.category(),
            response.title(),
            response.description(),
            response.source(),
            retrievedAt,
            items
        );
    }

    /**
     * 혼잡도 항목에 관광공사 사진을 붙인다. 혼잡도 응답 자체에는 관광지 이름과 지수밖에 없어서,
     * 이걸 안 하면 목록 카드가 전부 사진 없는 회색 자리표시자가 된다.
     *
     * 혼잡도 높은 순으로 훑되 아직 안 찾아본 것만 한 번에 {@link #IMAGE_LOOKUP_BATCH}개까지만 찾는다
     * (그 상수 주석 참고). 상위 {@link #HOT_PLACE_LIMIT}개는 추천 웰니스 첫 화면에 큰 카드로 걸리는
     * 자리라, 간이 검색이 빗나가면 상세 화면과 같은 정밀 매칭까지 한 번 더 간다.
     */
    private List<TourismCatalogItemResponse> enrichCrowdingImages(List<TourismCatalogItemResponse> items) {
        List<TourismCatalogItemResponse> ranked = items.stream()
            .collect(java.util.stream.Collectors.toMap(
                item -> canonicalTitle(item.title()),
                item -> item,
                (left, right) -> crowdingRate(left) >= crowdingRate(right) ? left : right,
                LinkedHashMap::new
            ))
            .values()
            .stream()
            .sorted(Comparator.comparingDouble(TourismCatalogService::crowdingRate).reversed())
            .toList();
        // 정밀 매칭(외부 호출이 여러 번)까지 쓸 상위 항목들.
        Set<String> hotPlaceKeys = ranked.stream()
            .limit(HOT_PLACE_LIMIT)
            .map(item -> canonicalTitle(item.title()))
            .collect(java.util.stream.Collectors.toSet());
        List<TourismCatalogItemResponse> candidates = ranked.stream()
            .filter(item -> item.imageUrl() == null &&
                !IMAGE_LOOKUP_GENERATION.equals(item.details().get(IMAGE_LOOKUP_ATTEMPTED)))
            .limit(IMAGE_LOOKUP_BATCH)
            .toList();

        Map<String, TourismCatalogItemResponse> matches = new HashMap<>();
        Map<String, Boolean> attemptedTitles = new HashMap<>();
        for (TourismCatalogItemResponse candidate : candidates) {
            String titleKey = canonicalTitle(candidate.title());
            attemptedTitles.put(titleKey, true);
            BusanTourismCodes.District district = districtForCrowdingItem(candidate);
            try {
                String keyword = tourismSearchKeyword(candidate.title());
                TourismExternalResponse search = gateway.searchPlaces(keyword, district, 1);
                List<TourismCatalogItemResponse> searchItems = normalizeItems(objectMapper.valueToTree(search.data()));
                TourismCatalogItemResponse match = searchItems.stream()
                    .filter(item -> canonicalTitle(item.title()).equals(canonicalTitle(keyword)))
                    .max(Comparator.comparing(item -> item.imageUrl() != null))
                    .orElseGet(() -> searchItems.stream()
                        .filter(item -> item.imageUrl() != null)
                        .filter(item -> canonicalTitle(item.title()).contains(canonicalTitle(keyword)) ||
                            canonicalTitle(keyword).contains(canonicalTitle(item.title())))
                        .findFirst()
                        .orElse(null));
                // 위 한 페이지짜리 검색은 이름이 거의 그대로 걸릴 때만 맞는다. 못 찾으면 상세
                // 화면이 쓰는 것과 같은 매칭(TourismPlaceMatchService — 키워드 변형·여러 페이지·
                // 상세 재확인까지 한다)으로 한 번 더 시도한다. 이게 없으면 같은 장소인데 목록에는
                // 썸네일이 없고 상세로 들어가면 사진이 나오는 어긋남이 생긴다.
                // 간이 검색이 사진 있는 항목을 못 집었을 때(아예 못 찾았거나, 찾았어도 사진이 없는
                // 항목일 때) 상세와 같은 매칭기로 한 번 더 간다 — 상세에는 사진이 나오는데 목록만
                // 비어 있는 어긋남을 없애는 게 목적이라, 사진이 붙는 결과가 나오면 그쪽을 쓴다.
                if ((match == null || match.imageUrl() == null) && hotPlaceKeys.contains(titleKey)) {
                    // 상세 화면이 넘기는 것과 똑같이 원본 제목을 그대로 준다(간이 검색용으로 다듬은
                    // keyword가 아니다) — 같은 입력·같은 매칭기여야 목록과 상세가 같은 결과를 본다.
                    TourismCatalogItemResponse matched = matchPlaceForImage(candidate.title(), district);
                    if (matched != null) {
                        match = matched;
                    }
                }
                if (match != null) {
                    matches.put(titleKey, match);
                }
            } catch (RuntimeException ignored) {
                // 혼잡도 자체는 유효하므로 사진 보강 실패가 TOP 5 응답을 막지 않게 한다.
            }
        }

        if (attemptedTitles.isEmpty()) {
            return items;
        }
        return items.stream().map(item -> {
            String titleKey = canonicalTitle(item.title());
            if (!attemptedTitles.containsKey(titleKey)) {
                return item;
            }
            TourismCatalogItemResponse match = matches.get(titleKey);
            Map<String, String> details = new LinkedHashMap<>(item.details());
            details.put(IMAGE_LOOKUP_ATTEMPTED, IMAGE_LOOKUP_GENERATION);
            if (match == null) {
                return new TourismCatalogItemResponse(
                    item.id(), item.title(), item.subtitle(), item.address(), item.imageUrl(),
                    item.latitude(), item.longitude(), item.categoryCode(), details
                );
            }
            return new TourismCatalogItemResponse(
                item.id(),
                item.title(),
                item.subtitle(),
                match.address(),
                match.imageUrl(),
                match.latitude(),
                match.longitude(),
                item.categoryCode(),
                details
            );
        }).toList();
    }

    /**
     * 상세 화면과 같은 매칭기로 관광공사 상세를 찾아 사진을 얻는다. 실패(매칭 없음/외부 API 오류)는
     * null로 삼킨다 — 혼잡도 목록 자체는 사진 없이도 유효하다.
     */
    private TourismCatalogItemResponse matchPlaceForImage(String keyword, BusanTourismCodes.District district) {
        try {
            TourismPlaceMatchService matcher = placeMatchService.getIfAvailable();
            if (matcher == null) {
                return null;
            }
            var matched = matcher.find(keyword, district);
            return matched.matched() && matched.item() != null && matched.item().imageUrl() != null
                ? matched.item()
                : null;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static List<TourismCatalogItemResponse> todayCrowdingItems(
        List<TourismCatalogItemResponse> items,
        LocalDate today
    ) {
        String targetDate = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        return items.stream()
            .filter(item -> targetDate.equals(normalizeDate(item.details().get("baseYmd"))))
            .collect(java.util.stream.Collectors.toMap(
                item -> canonicalTitle(item.title()),
                item -> item,
                (left, right) -> crowdingRate(left) >= crowdingRate(right) ? left : right,
                LinkedHashMap::new
            ))
            .values()
            .stream()
            .sorted(Comparator.comparingDouble(TourismCatalogService::crowdingRate).reversed())
            .toList();
    }

    private static String normalizeDate(String rawDate) {
        return rawDate == null ? "" : rawDate.replaceAll("[^0-9]", "");
    }

    private static BusanTourismCodes.District districtForCrowdingItem(TourismCatalogItemResponse item) {
        String signguCode = item.details().get("signguCd");
        for (BusanTourismCodes.District district : BusanTourismCodes.District.values()) {
            if (district.bigdataSignguCd().equals(signguCode)) {
                return district;
            }
        }
        return BusanTourismCodes.District.HAEUNDAE;
    }

    private static String tourismSearchKeyword(String title) {
        if (title.toUpperCase().contains("SEA LIFE")) {
            return "씨라이프부산아쿠아리움";
        }
        return title.replaceAll("\\s*\\([^)]*\\)\\s*", "").trim();
    }

    private static String canonicalTitle(String title) {
        return title == null ? "" : title
            .toLowerCase()
            .replaceAll("\\([^)]*\\)", "")
            .replaceAll("[^0-9a-z가-힣]", "");
    }

    private static double crowdingRate(TourismCatalogItemResponse item) {
        String raw = item.details().getOrDefault("cnctrRate", item.subtitle());
        if (!hasText(raw)) {
            return Double.NEGATIVE_INFINITY;
        }
        try {
            return Double.parseDouble(raw.replace(",", ""));
        } catch (NumberFormatException ignored) {
            return Double.NEGATIVE_INFINITY;
        }
    }

    private static String crowdingSnapshotKey(LocalDate date) {
        return CROWDING_CACHE_SOURCE + ":" + CROWDING_CACHE_SCOPE + ":" + date;
    }

    List<TourismCatalogItemResponse> normalizeItems(JsonNode body) {
        JsonNode itemNode = body.path("items").path("item");
        List<JsonNode> rawItems = new ArrayList<>();
        if (itemNode.isArray()) {
            itemNode.forEach(rawItems::add);
        } else if (itemNode.isObject()) {
            rawItems.add(itemNode);
        }

        List<TourismCatalogItemResponse> result = new ArrayList<>();
        Map<String, Integer> idOccurrences = new LinkedHashMap<>();
        for (int index = 0; index < rawItems.size(); index++) {
            TourismCatalogItemResponse item = normalizeItem(rawItems.get(index), index);
            int occurrence = idOccurrences.merge(item.id(), 1, Integer::sum);
            if (occurrence > 1) {
                item = withId(item, item.id() + "-" + occurrence);
            }
            result.add(item);
        }
        return result;
    }

    private static TourismCatalogItemResponse withId(TourismCatalogItemResponse item, String id) {
        return new TourismCatalogItemResponse(
            id,
            item.title(),
            item.subtitle(),
            item.address(),
            item.imageUrl(),
            item.latitude(),
            item.longitude(),
            item.categoryCode(),
            item.details()
        );
    }

    private TourismCatalogItemResponse normalizeItem(JsonNode item, int index) {
        // title과 같은 이유로 id도 rlteTatsCd(실제 연관 관광지 코드)를 tAtsCd(기준 관광지 코드)보다
        // 먼저 봐야 한다 — 그렇지 않으면 RELATED의 모든 row가 같은 id(+dedup suffix)로 뭉뚱그려진다.
        // crsIdx는 WALKING(Durunubi courseList)의 실제 코스 id 필드 — "courseNo"는 어느 API에도
        // 없는 필드라 항상 미스매치였다.
        String id = first(item, "contentid", "contentId", "rlteTatsCd", "tAtsCd", "hubTatsCd", "crsIdx", "themeId", "storyId", "galContentId");
        if (!hasText(id)) {
            id = Integer.toHexString(item.toString().hashCode());
        }
        // RELATED(TarRlteTarService1) 응답엔 기준 관광지 이름(tAtsNm)과 실제 연관 관광지 이름
        // (rlteTatsNm)이 한 row에 같이 들어있다 — tAtsNm을 먼저 보면 매 row가 전부 기준 관광지
        // 이름 하나로만 보여서 "같은 데이터만 반복된다"로 보인다. rlteTatsNm을 먼저 확인한다
        // (다른 카테고리 응답엔 이 필드가 아예 없어서 순서를 바꿔도 영향 없다).
        // crsKorNm은 WALKING의 실제 코스명 필드 — "courseName"은 실존하지 않는 필드라 매번
        // 미스매치되어 WALKING 카테고리가 전부 "관광 데이터 N" 플레이스홀더로만 나오고 있었다.
        String title = first(item, "title", "rlteTatsNm", "tAtsNm", "hubTatsNm", "crsKorNm", "galTitle", "themeName", "storyTitle", "name");
        if (!hasText(title)) {
            title = "관광 데이터 " + (index + 1);
        }

        return new TourismCatalogItemResponse(
            id,
            title,
            // CROWDING(TatsCnctrRateService) 응답의 실제 혼잡도 필드명은 tatsCnctrRate가 아니라
            // cnctrRate다 — 오타 때문에 subtitle이 항상 비어서 날짜별로 다른 카드인데도 구분이 안 됐다.
            // crsSummary는 WALKING의 코스 요약 필드 — "courseBrf"는 실존하지 않는 필드였다.
            first(item, "overview", "crsSummary", "courseBrf", "galSearchKeyword", "cat3", "cnctrRate", "tatsCnctrRate", "daywkDivNm"),
            // sigun은 WALKING의 시/군 필드 — 지금까지 주소 후보에 없어서 항상 null이었다.
            first(item, "addr1", "baseAddr", "address", "roadAddr", "sigun"),
            secureImageUrl(first(item, "firstimage", "firstimage2", "galWebImageUrl", "imageUrl")),
            number(item, "mapy", "mapY", "latitude", "lat"),
            number(item, "mapx", "mapX", "longitude", "lng"),
            // contenttypeid(12=관광지, 14=문화시설, 25=여행코스, 28=레포츠, 32=숙박, 38=쇼핑,
            // 39=음식점) — PLACES_KO/ACCESSIBLE 카테고리 필터 칩에 쓴다. scalarDetails()의 개수
            // 캡에 걸려 누락될 수 있어 별도 필드로 명시적으로 뽑는다.
            first(item, "contenttypeid"),
            scalarDetails(item)
        );
    }

    private static String secureImageUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("http://")) {
            return imageUrl;
        }
        return "https://" + imageUrl.substring("http://".length());
    }

    private Map<String, String> scalarDetails(JsonNode item) {
        Map<String, String> details = new LinkedHashMap<>();
        // 라벨이 있는 필드부터 자리를 잡는다 — 남는 칸은 아래에서 응답 순서대로 채운다.
        for (String field : PRIORITY_DETAIL_FIELDS) {
            if (details.size() >= MAX_DETAILS) {
                break;
            }
            JsonNode value = item.path(field);
            if (value.isValueNode() && hasText(value.asText()) && !isPresentationField(field)) {
                details.put(field, value.asText());
            }
        }
        Iterator<Map.Entry<String, JsonNode>> fields = item.fields();
        while (fields.hasNext() && details.size() < MAX_DETAILS) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode value = field.getValue();
            if (!value.isValueNode() || !hasText(value.asText()) || isPresentationField(field.getKey())
                || details.containsKey(field.getKey())) {
                continue;
            }
            details.put(field.getKey(), value.asText());
        }
        return details;
    }

    private static boolean isPresentationField(String field) {
        return switch (field) {
            case "contentid", "contentId", "title", "tAtsNm", "hubTatsNm", "rlteTatsNm", "courseName",
                "galTitle", "name", "addr1", "baseAddr", "address", "roadAddr", "firstimage", "firstimage2",
                "galWebImageUrl", "imageUrl", "mapx", "mapX", "mapy", "mapY", "latitude", "longitude",
                "cnctrRate", "contenttypeid",
                // WALKING(Durunubi courseList) 전용: crsIdx/crsKorNm/crsSummary/sigun은 이미
                // id/title/subtitle/address로 뽑혀서 details에 또 나올 필요가 없고, crsContents·
                // crsTourInfo·travelerinfo·routeIdx·brdDiv는 장문 텍스트/내부 코드라 한정된 details
                // 슬롯을 이걸로 채우면 정작 거리·소요시간·난이도·GPX 링크가 밀려서 안 보인다.
                "crsIdx", "crsKorNm", "crsSummary", "sigun",
                "crsContents", "crsTourInfo", "travelerinfo", "routeIdx", "brdDiv" -> true;
            default -> false;
        };
    }

    private static String first(JsonNode item, String... fields) {
        for (String field : fields) {
            JsonNode value = item.path(field);
            if (value.isValueNode() && hasText(value.asText())) {
                return value.asText();
            }
        }
        return null;
    }

    private static Double number(JsonNode item, String... fields) {
        String value = first(item, fields);
        if (!hasText(value)) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
