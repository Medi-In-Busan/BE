package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.document.client.PapagoTranslationApiException;
import com.mediinbusan.backend.document.client.PapagoTranslationAuthenticationException;
import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceTranslation;
import com.mediinbusan.backend.wellness.dto.WellnessPlaceResponse;
import com.mediinbusan.backend.wellness.repository.WellnessPlaceTranslationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WellnessPlaceTranslationService {
    private static final Logger log = LoggerFactory.getLogger(WellnessPlaceTranslationService.class);
    // Papago는 임의 특수문자를 제거할 수 있지만 필드 사이의 줄바꿈은 유지한다.
    private static final String LINE_SEPARATOR = "\n";
    private static final String EMPTY_FIELD = "__MIB_EMPTY_FIELD__";
    private static final int MAX_BATCH_CHARACTERS = 4_000;
    // 번역 캐시를 읽을 때 IN 절에 한 번에 넣는 contentId 개수. 전체를 한 방에 넣으면 DB마다 다른
    // 파라미터 개수 제한에 걸릴 수 있어 묶어서 나눈다.
    private static final int CACHE_LOOKUP_CHUNK = 500;
    private final WellnessPlaceTranslationRepository repository;
    private final PapagoTranslationClient papago;
    private final PapagoDailyQuotaGuard quotaGuard;

    public WellnessPlaceTranslationService(WellnessPlaceTranslationRepository repository, PapagoTranslationClient papago,
                                           PapagoDailyQuotaGuard quotaGuard) {
        this.repository = repository;
        this.papago = papago;
        this.quotaGuard = quotaGuard;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public WellnessPlaceResponse localize(WellnessPlaceResponse source, String requestedLanguage) {
        return localizeAll(List.of(source), requestedLanguage).getFirst();
    }

    /**
     * 이미 캐시에 있는 번역만 입히고 **Papago는 부르지 않는다** — 장소 목록 응답용이다.
     *
     * 목록 엔드포인트(특히 좌표 없이 전체를 주는 `GET /api/wellness/places`, 현재 2,267건)에서
     * {@link #localizeAll}을 쓰면 캐시에 없는 장소 전부를 요청 처리 중에 번역하려 든다. 4,000자
     * 단위로 쪼개도 Papago 호출이 수백 번 직렬로 나가서 응답이 분 단위가 되고, 지도 한 번 여는
     * 것으로 일일 번역 한도를 태운다. 대량 번역은 읽기 경로가 아니라 적재 배치가 할 일이다
     * (`WellnessIngestionService.applyTourTranslationsByLocation`가 TourAPI 다국어 서비스에서
     * 이름·주소를 받아 `wellness_place`의 name_en/ja/zh 컬럼에 채운다 — 목록의 주 번역 소스는 그쪽이다).
     * 여기서 채우는 Papago 캐시는 상세 화면을 열 때 그 장소 하나씩 쌓인다({@link #localize}).
     */
    @Transactional(readOnly = true)
    public List<WellnessPlaceResponse> localizeAllFromCache(
        List<WellnessPlaceResponse> sources,
        String requestedLanguage
    ) {
        String language = normalizeLanguage(requestedLanguage);
        if (sources.isEmpty() || language.equals("ko")) return sources;

        Map<String, WellnessPlaceTranslation> cachedByContentId = loadCache(sources, language);
        return sources.stream()
            .map(source -> {
                WellnessPlaceTranslation cached = cachedByContentId.get(source.contentId());
                return cached != null && cached.sourceHash().equals(sourceHash(source))
                    ? translatedResponse(source, cached)
                    : source;
            })
            .toList();
    }

    /**
     * 요청에 실린 장소들의 번역 캐시를 한 번에(정확히는 IN 절 길이 제한을 피해 묶음 단위로) 읽는다.
     * 장소마다 한 건씩 조회하면 목록 요청 한 번에 쿼리가 장소 수만큼 나간다.
     */
    private Map<String, WellnessPlaceTranslation> loadCache(List<WellnessPlaceResponse> sources, String language) {
        List<String> contentIds = sources.stream()
            .map(WellnessPlaceResponse::contentId)
            .filter(contentId -> contentId != null && !contentId.isBlank())
            .distinct()
            .toList();
        Map<String, WellnessPlaceTranslation> cachedByContentId = new LinkedHashMap<>();
        for (int start = 0; start < contentIds.size(); start += CACHE_LOOKUP_CHUNK) {
            List<String> chunk = contentIds.subList(start, Math.min(start + CACHE_LOOKUP_CHUNK, contentIds.size()));
            repository.findByLanguageCodeAndContentIdIn(language, chunk)
                .forEach(translation -> cachedByContentId.put(translation.contentId(), translation));
        }
        return cachedByContentId;
    }

    // WellnessService는 readOnly=true 트랜잭션에서 이 메서드를 호출한다.
    // REQUIRED(기본값)로 두면 그 읽기전용 트랜잭션에 그대로 합류해 캐시 미스 시의
    // insert/update가 "Connection is read-only"로 실패한다(MySQL에서만 강제됨, H2는 무시함) —
    // 항상 새로운 쓰기 가능한 트랜잭션을 열도록 REQUIRES_NEW로 분리한다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized List<WellnessPlaceResponse> localizeAll(
        List<WellnessPlaceResponse> sources,
        String requestedLanguage
    ) {
        String language = normalizeLanguage(requestedLanguage);
        if (sources.isEmpty() || language.equals("ko") || quotaGuard.isBlockedToday()) return sources;

        Map<String, WellnessPlaceResponse> localizedById = new LinkedHashMap<>();
        List<PendingTranslation> pending = new ArrayList<>();
        Map<String, WellnessPlaceTranslation> cachedByContentId = loadCache(sources, language);
        for (WellnessPlaceResponse source : sources) {
            String hash = sourceHash(source);
            WellnessPlaceTranslation cached = cachedByContentId.get(source.contentId());
            if (cached != null && cached.sourceHash().equals(hash)) {
                localizedById.put(source.contentId(), translatedResponse(source, cached));
            } else {
                pending.add(new PendingTranslation(source, hash, cached));
            }
        }

        for (List<PendingTranslation> batch : batches(pending)) {
            if (quotaGuard.isBlockedToday()) break;
            try {
                List<String> originalFields = batch.stream()
                    .flatMap(item -> java.util.stream.Stream.of(
                        sanitize(item.source().name()),
                        sanitize(item.source().address()),
                        sanitize(item.source().description())
                    ))
                    .toList();
                String[] translatedFields = papago.translate(
                    String.join(LINE_SEPARATOR, originalFields),
                    papagoLanguage(language)
                ).split("\\R", -1);
                if (translatedFields.length != originalFields.size()) {
                    log.warn("Papago 웰니스 일괄 번역 필드 수가 맞지 않아 {}개 장소를 한국어로 반환합니다.", batch.size());
                    continue;
                }
                for (int index = 0; index < batch.size(); index++) {
                    PendingTranslation item = batch.get(index);
                    WellnessPlaceResponse source = item.source();
                    int fieldIndex = index * 3;
                    String name = translatedOrOriginal(translatedFields[fieldIndex], source.name());
                    String address = translatedOrOriginal(translatedFields[fieldIndex + 1], source.address());
                    String description = emptyToNull(translatedFields[fieldIndex + 2]);
                    WellnessPlaceTranslation translation = item.cached() != null
                        ? item.cached()
                        : new WellnessPlaceTranslation(
                            source.contentId(), language, item.sourceHash(), name, address, description
                        );
                    translation.refresh(source.contentId(), language, item.sourceHash(), name, address, description);
                    repository.save(translation);
                    localizedById.put(source.contentId(), translatedResponse(source, translation));
                }
            } catch (PapagoTranslationAuthenticationException | PapagoTranslationApiException exception) {
                handleTranslationFailure(exception);
            }
        }

        return sources.stream()
            .map(source -> localizedById.getOrDefault(source.contentId(), source))
            .toList();
    }

    private void handleTranslationFailure(RuntimeException exception) {
        if (exception.getMessage() != null && exception.getMessage().contains("429")) {
            quotaGuard.blockToday();
            log.warn("Papago 일일 한도 초과: 오늘 남은 웰니스 번역은 한국어로 폴백합니다.");
        } else {
            log.warn("웰니스 번역 실패로 한국어 원문을 반환합니다: {}", exception.getMessage());
        }
    }

    private static List<List<PendingTranslation>> batches(List<PendingTranslation> pending) {
        List<List<PendingTranslation>> result = new ArrayList<>();
        List<PendingTranslation> current = new ArrayList<>();
        int currentLength = 0;
        for (PendingTranslation item : pending) {
            int itemLength = nullToEmpty(item.source().name()).length()
                + nullToEmpty(item.source().address()).length()
                + nullToEmpty(item.source().description()).length()
                + 3;
            if (!current.isEmpty() && currentLength + itemLength > MAX_BATCH_CHARACTERS) {
                result.add(List.copyOf(current));
                current.clear();
                currentLength = 0;
            }
            current.add(item);
            currentLength += itemLength;
        }
        if (!current.isEmpty()) result.add(List.copyOf(current));
        return result;
    }

    private static String normalizeLanguage(String language) {
        if (language == null) return "ko";
        return switch (language.toLowerCase()) { case "en", "ja", "zh" -> language.toLowerCase(); default -> "ko"; };
    }

    private static String papagoLanguage(String language) { return language.equals("zh") ? "zh-CN" : language; }
    private static String sanitize(String value) {
        String sanitized = nullToEmpty(value)
            .replace(EMPTY_FIELD, " ")
            .replace('\r', ' ')
            .replace('\n', ' ');
        return sanitized.isBlank() ? EMPTY_FIELD : sanitized;
    }
    private static String nullToEmpty(String value) { return value == null ? "" : value; }
    private static String emptyToNull(String value) {
        return value == null || value.isBlank() || value.equals(EMPTY_FIELD) ? null : value;
    }
    private static String translatedOrOriginal(String translated, String original) {
        return translated == null || translated.isBlank() || translated.equals(EMPTY_FIELD)
            ? nullToEmpty(original)
            : translated;
    }

    private static String sourceHash(WellnessPlaceResponse source) {
        try {
            String value = source.name() + "\u0000" + source.address() + "\u0000" + nullToEmpty(source.description());
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("웰니스 원문 해시 생성 실패", exception);
        }
    }

    private static WellnessPlaceResponse translatedResponse(WellnessPlaceResponse source, WellnessPlaceTranslation translation) {
        return new WellnessPlaceResponse(source.contentId(), translation.name(), source.contentTypeId(), translation.address(),
            source.latitude(), source.longitude(), source.imageUrl(), translation.description(), source.phoneNumber(),
            source.modifiedDate(), source.distanceFromHospitalMeters(), true, source.placeCategory(),
            // 방문 정보는 번역 대상이 아니다 — 원문(한국어)을 그대로 통과시킨다(WellnessVisitInfo 주석 참고).
            source.businessHours(), source.restDate(), source.signatureMenu(),
            source.usageFee(), source.parkingInfo(), source.homepageUrl());
    }

    private record PendingTranslation(
        WellnessPlaceResponse source,
        String sourceHash,
        WellnessPlaceTranslation cached
    ) {}
}
