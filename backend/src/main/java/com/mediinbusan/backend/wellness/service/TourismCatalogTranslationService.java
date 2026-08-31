package com.mediinbusan.backend.wellness.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediinbusan.backend.document.client.PapagoTranslationApiException;
import com.mediinbusan.backend.document.client.PapagoTranslationAuthenticationException;
import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.wellness.domain.TourismCatalogCategory;
import com.mediinbusan.backend.wellness.domain.TourismCatalogTranslation;
import com.mediinbusan.backend.wellness.dto.TourismCatalogItemResponse;
import com.mediinbusan.backend.wellness.dto.TourismCatalogResponse;
import com.mediinbusan.backend.wellness.repository.TourismCatalogTranslationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TourismCatalogTranslationService {
    private static final Logger log = LoggerFactory.getLogger(TourismCatalogTranslationService.class);
    private static final String LINE_SEPARATOR = "\n";
    private static final String EMPTY_FIELD = "__MIB_EMPTY_FIELD__";
    private static final int MAX_BATCH_CHARACTERS = 4_000;

    private final TourismCatalogTranslationRepository repository;
    private final PapagoTranslationClient papago;
    private final PapagoDailyQuotaGuard quotaGuard;
    private final ObjectMapper objectMapper;

    public TourismCatalogTranslationService(TourismCatalogTranslationRepository repository,
                                            PapagoTranslationClient papago,
                                            PapagoDailyQuotaGuard quotaGuard) {
        this.repository = repository;
        this.papago = papago;
        this.quotaGuard = quotaGuard;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public TourismCatalogResponse localize(TourismCatalogResponse source, String requestedLanguage) {
        String language = normalizeLanguage(requestedLanguage);
        if (language.equals("ko") || isOfficialLanguageCatalog(source.category()) || quotaGuard.isBlockedToday()) {
            return source;
        }
        Map<String, TourismCatalogItemResponse> localizedById = new LinkedHashMap<>();
        List<PendingTranslation> pending = new ArrayList<>();
        for (TourismCatalogItemResponse item : source.items()) {
            String hash = sourceHash(item);
            var cached = repository.findByCategoryAndItemIdAndLanguageCode(
                source.category().name(), item.id(), language
            );
            if (cached.isPresent() && cached.get().sourceHash().equals(hash)) {
                localizedById.put(item.id(), translatedItem(item, cached.get()));
            } else {
                List<String> detailKeys = item.details().entrySet().stream()
                    .filter(entry -> shouldTranslateDetail(entry.getKey(), entry.getValue()))
                    .map(Map.Entry::getKey)
                    .toList();
                pending.add(new PendingTranslation(item, hash, cached.orElse(null), detailKeys));
            }
        }

        for (List<PendingTranslation> batch : batches(pending)) {
            if (quotaGuard.isBlockedToday()) break;
            translateBatch(source.category(), language, batch, localizedById);
        }

        List<TourismCatalogItemResponse> translated = source.items().stream()
            .map(item -> localizedById.getOrDefault(item.id(), item))
            .toList();
        return new TourismCatalogResponse(source.category(), source.title(), source.description(), source.source(),
            source.retrievedAt(), translated);
    }

    private void translateBatch(TourismCatalogCategory category,
                                String language,
                                List<PendingTranslation> batch,
                                Map<String, TourismCatalogItemResponse> localizedById) {
        try {
            List<String> fields = batch.stream().flatMap(item -> item.fields().stream()).toList();
            String[] translated = papago.translate(
                String.join(LINE_SEPARATOR, fields.stream().map(TourismCatalogTranslationService::sanitize).toList()),
                papagoLanguage(language)
            ).split("\\R", -1);
            if (translated.length != fields.size()) {
                log.warn("Papago 관광 일괄 번역 필드 수가 맞지 않아 {}개 항목을 원문으로 반환합니다.", batch.size());
                return;
            }
            int cursor = 0;
            for (PendingTranslation item : batch) {
                TourismCatalogItemResponse source = item.source();
                String title = translatedOrOriginal(translated[cursor++], source.title());
                String subtitle = emptyToNull(translated[cursor++]);
                String address = emptyToNull(translated[cursor++]);
                Map<String, String> details = new LinkedHashMap<>(source.details());
                for (String detailKey : item.detailKeys()) {
                    details.put(detailKey, translatedOrOriginal(translated[cursor++], details.get(detailKey)));
                }
                String detailsJson = objectMapper.writeValueAsString(details);
                TourismCatalogTranslation entity = item.cached() != null
                    ? item.cached()
                    : new TourismCatalogTranslation(
                        category.name(), source.id(), language, item.sourceHash(), title, subtitle, address, detailsJson
                    );
                entity.refresh(category.name(), source.id(), language, item.sourceHash(), title, subtitle, address, detailsJson);
                repository.save(entity);
                localizedById.put(source.id(), translatedItem(source, entity));
            }
        } catch (PapagoTranslationAuthenticationException | PapagoTranslationApiException exception) {
            if (exception.getMessage() != null && exception.getMessage().contains("429")) {
                quotaGuard.blockToday();
                log.warn("Papago 일일 한도 초과: 오늘 남은 관광 번역은 한국어로 폴백합니다.");
            } else {
                log.warn("관광 데이터 번역 실패로 한국어 원문을 반환합니다: {}", exception.getMessage());
            }
        } catch (Exception exception) {
            log.warn("관광 번역 캐시 처리 실패로 한국어 원문을 반환합니다: {}", exception.getMessage());
        }
    }

    private static List<List<PendingTranslation>> batches(List<PendingTranslation> pending) {
        List<List<PendingTranslation>> result = new ArrayList<>();
        List<PendingTranslation> current = new ArrayList<>();
        int currentLength = 0;
        for (PendingTranslation item : pending) {
            int itemLength = item.fields().stream().mapToInt(String::length).sum() + item.fields().size();
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

    private TourismCatalogItemResponse translatedItem(TourismCatalogItemResponse source,
                                                        TourismCatalogTranslation translation) {
        try {
            Map<String, String> details = objectMapper.readValue(translation.detailsJson(), new TypeReference<>() {});
            return new TourismCatalogItemResponse(source.id(), translation.title(), translation.subtitle(),
                translation.address(), source.imageUrl(), source.latitude(), source.longitude(), source.categoryCode(), details);
        } catch (Exception exception) {
            return source;
        }
    }

    private static boolean isOfficialLanguageCatalog(TourismCatalogCategory category) {
        return category == TourismCatalogCategory.PLACES_KO || category == TourismCatalogCategory.PLACES_EN
            || category == TourismCatalogCategory.PLACES_JA || category == TourismCatalogCategory.PLACES_ZH;
    }

    private static boolean shouldTranslateDetail(String key, String value) {
        if (value == null || value.isBlank() || value.startsWith("http")) return false;
        if (key.matches("(?i).*(id|code|ymd|ym|date|rate|latitude|longitude|mapx|mapy).*") ||
            key.equalsIgnoreCase("signguNm") || key.equalsIgnoreCase("signguName")) return false;
        return !value.matches("[0-9.,:+\\-_/ ]+");
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

    private static String sourceHash(TourismCatalogItemResponse source) {
        try {
            String value = "v2\u0000" + source.title() + "\u0000" + nullToEmpty(source.subtitle()) + "\u0000"
                + nullToEmpty(source.address()) + "\u0000" + source.details();
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("관광 원문 해시 생성 실패", exception);
        }
    }

    private record PendingTranslation(
        TourismCatalogItemResponse source,
        String sourceHash,
        TourismCatalogTranslation cached,
        List<String> detailKeys
    ) {
        List<String> fields() {
            List<String> result = new ArrayList<>();
            result.add(nullToEmpty(source.title()));
            result.add(nullToEmpty(source.subtitle()));
            result.add(nullToEmpty(source.address()));
            detailKeys.forEach(key -> result.add(nullToEmpty(source.details().get(key))));
            return result;
        }
    }
}
