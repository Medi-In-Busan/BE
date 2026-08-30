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
    private final WellnessPlaceTranslationRepository repository;
    private final PapagoTranslationClient papago;
    private final PapagoDailyQuotaGuard quotaGuard;

    public WellnessPlaceTranslationService(WellnessPlaceTranslationRepository repository, PapagoTranslationClient papago,
                                           PapagoDailyQuotaGuard quotaGuard) {
        this.repository = repository;
        this.papago = papago;
        this.quotaGuard = quotaGuard;
    }

    @Transactional
    public WellnessPlaceResponse localize(WellnessPlaceResponse source, String requestedLanguage) {
        return localizeAll(List.of(source), requestedLanguage).getFirst();
    }

    @Transactional
    public synchronized List<WellnessPlaceResponse> localizeAll(
        List<WellnessPlaceResponse> sources,
        String requestedLanguage
    ) {
        String language = normalizeLanguage(requestedLanguage);
        if (sources.isEmpty() || language.equals("ko") || quotaGuard.isBlockedToday()) return sources;

        Map<String, WellnessPlaceResponse> localizedById = new LinkedHashMap<>();
        List<PendingTranslation> pending = new ArrayList<>();
        for (WellnessPlaceResponse source : sources) {
            String hash = sourceHash(source);
            var cached = repository.findByContentIdAndLanguageCode(source.contentId(), language);
            if (cached.isPresent() && cached.get().sourceHash().equals(hash)) {
                localizedById.put(source.contentId(), translatedResponse(source, cached.get()));
            } else {
                pending.add(new PendingTranslation(source, hash, cached.orElse(null)));
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
            source.modifiedDate(), source.distanceFromHospitalMeters(), true);
    }

    private record PendingTranslation(
        WellnessPlaceResponse source,
        String sourceHash,
        WellnessPlaceTranslation cached
    ) {}
}
