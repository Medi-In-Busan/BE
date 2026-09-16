package com.mediinbusan.backend.guide.service;

import com.mediinbusan.backend.document.client.PapagoTranslationApiException;
import com.mediinbusan.backend.document.client.PapagoTranslationAuthenticationException;
import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.document.service.SensitiveTextMasker;
import com.mediinbusan.backend.guide.dto.TreatmentBriefingTranslateRequest;
import com.mediinbusan.backend.guide.dto.TreatmentBriefingTranslateResponse;
import com.mediinbusan.backend.guide.exception.UnsupportedTranslationLanguageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * S-06 STEP04 "내 진료 카드" 필드를 사용자가 입력한 언어(source)에서 한국어(target 고정)로 번역한다.
 * 문서 OCR(한국어 → 외국어)과 반대 방향이라 {@link PapagoTranslationClient}의 source 파라미터화
 * 오버로드({@code translate(text, sourceLanguage, targetLanguage)})를 사용한다.
 *
 * <p>사용자가 화면에서 즉석으로 입력/수정하는 값이라 DB 번역 캐시는 두지 않고, 필드마다 개별로
 * Papago를 호출한다(웰니스/관광지 번역처럼 여러 필드를 한 번에 합쳐 보내는 배치 방식은 쓰지 않는다 —
 * 필드 5개뿐이라 배치의 이득보다 줄바꿈 분리 실패 리스크가 크다).
 */
@Service
public class TreatmentBriefingTranslationService {

    private static final Logger log = LoggerFactory.getLogger(TreatmentBriefingTranslationService.class);
    private static final String TARGET_LANGUAGE = "ko";
    // Android core/datastore/SupportedLanguage.CODES와 동일하게 유지한다 — 한쪽만 늘리면 조용히 어긋난다.
    private static final Set<String> SUPPORTED_SOURCE_LANGUAGES = Set.of("ko", "en", "zh", "ja");

    private final PapagoTranslationClient papago;

    public TreatmentBriefingTranslationService(PapagoTranslationClient papago) {
        this.papago = papago;
    }

    public TreatmentBriefingTranslateResponse translate(TreatmentBriefingTranslateRequest request) {
        String sourceLanguage = normalizeSourceLanguage(request.sourceLang());

        // 이미 한국어로 입력한 사용자는 번역할 필요가 없다 — Papago를 호출하지 않고 원문 그대로 반환한다.
        if (sourceLanguage.equals(TARGET_LANGUAGE)) {
            return new TreatmentBriefingTranslateResponse(
                request.visitPurpose(), request.symptoms(), request.allergy(), request.medication(), request.memo()
            );
        }

        String papagoSourceLanguage = papagoLanguage(sourceLanguage);
        return new TreatmentBriefingTranslateResponse(
            translateField(request.visitPurpose(), papagoSourceLanguage),
            translateField(request.symptoms(), papagoSourceLanguage),
            translateField(request.allergy(), papagoSourceLanguage),
            translateField(request.medication(), papagoSourceLanguage),
            translateField(request.memo(), papagoSourceLanguage)
        );
    }

    /**
     * 번역은 원문을 제3자(Papago, NAVER Cloud)로 내보내므로 고유식별번호를 가린 뒤 보낸다
     * (DocumentOcrService.translate와 동일 정책). 실패해도 전체 응답을 실패시키지 않고 해당
     * 필드만 원문으로 폴백한다 — 원문/번역문 자체는 로그에 남기지 않는다.
     */
    private String translateField(String value, String papagoSourceLanguage) {
        if (value == null || value.isBlank()) {
            return value;
        }
        try {
            return papago.translate(SensitiveTextMasker.mask(value), papagoSourceLanguage, TARGET_LANGUAGE);
        } catch (PapagoTranslationAuthenticationException e) {
            log.error("Papago 번역 인증에 실패했습니다. PAPAGO_TRANSLATION_CLIENT_ID/SECRET 설정을 확인하세요. 원문을 반환합니다.");
            return value;
        } catch (PapagoTranslationApiException e) {
            log.warn("진료 브리핑 번역 호출에 실패해 원문을 반환합니다: {}", e.getMessage());
            return value;
        }
    }

    private static String normalizeSourceLanguage(String sourceLang) {
        if (sourceLang == null || sourceLang.isBlank()) {
            throw new UnsupportedTranslationLanguageException("sourceLang은 필수입니다.");
        }
        String normalized = sourceLang.toLowerCase();
        if (!SUPPORTED_SOURCE_LANGUAGES.contains(normalized)) {
            throw new UnsupportedTranslationLanguageException("지원하지 않는 sourceLang입니다: " + sourceLang);
        }
        return normalized;
    }

    private static String papagoLanguage(String language) {
        return language.equals("zh") ? "zh-CN" : language;
    }
}
