package com.mediinbusan.backend.document.service;

import com.mediinbusan.backend.document.client.ClovaOcrApiException;
import com.mediinbusan.backend.document.client.ClovaOcrAuthenticationException;
import com.mediinbusan.backend.document.client.ClovaOcrClient;
import com.mediinbusan.backend.document.client.ClovaOcrResponse;
import com.mediinbusan.backend.document.client.PapagoTranslationApiException;
import com.mediinbusan.backend.document.client.PapagoTranslationAuthenticationException;
import com.mediinbusan.backend.document.client.PapagoTranslationClient;
import com.mediinbusan.backend.document.dto.DocumentOcrDtoMapper;
import com.mediinbusan.backend.document.dto.DocumentOcrResponse;
import com.mediinbusan.backend.document.exception.DocumentOcrFailedException;
import com.mediinbusan.backend.document.validation.DocumentImageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentOcrService {

    private static final Logger log = LoggerFactory.getLogger(DocumentOcrService.class);

    private final DocumentImageValidator imageValidator;
    private final ClovaOcrClient clovaOcrClient;
    private final PapagoTranslationClient papagoTranslationClient;

    public DocumentOcrService(
        DocumentImageValidator imageValidator,
        ClovaOcrClient clovaOcrClient,
        PapagoTranslationClient papagoTranslationClient
    ) {
        this.imageValidator = imageValidator;
        this.clovaOcrClient = clovaOcrClient;
        this.papagoTranslationClient = papagoTranslationClient;
    }

    /**
     * @param targetLanguage 번역 대상 언어(Papago 언어 코드, 예: en/ja/zh-CN). null/blank면 번역을 시도하지 않는다.
     *                        번역 API 키 미설정/인증 실패/호출 실패 시에도 OCR 자체는 실패시키지 않고
     *                        translatedText/targetLanguage만 null로 반환한다.
     */
    public DocumentOcrResponse extractText(MultipartFile image, String targetLanguage) {
        String format = imageValidator.validateAndResolveFormat(image);
        byte[] imageBytes = readBytes(image);

        ClovaOcrResponse clovaResponse;
        try {
            clovaResponse = clovaOcrClient.recognizeText(imageBytes, format);
        } catch (ClovaOcrAuthenticationException e) {
            log.error("CLOVA OCR 인증에 실패했습니다. CLOVA_OCR_SECRET_KEY 설정을 확인하세요.");
            throw new DocumentOcrFailedException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        } catch (ClovaOcrApiException e) {
            log.warn("CLOVA OCR 호출에 실패했습니다: {}", e.getMessage());
            throw new DocumentOcrFailedException(HttpStatus.BAD_GATEWAY, e);
        }

        String text = DocumentOcrDtoMapper.extractText(clovaResponse);
        log.info("문서 OCR 처리 완료: textLength={}", text.length());

        String translatedText = translate(text, targetLanguage);
        String appliedTargetLanguage = translatedText != null ? targetLanguage : null;
        return new DocumentOcrResponse(text, translatedText, appliedTargetLanguage);
    }

    /**
     * 번역은 원문을 제3자(Papago, NAVER Cloud)로 내보내므로 고유식별번호를 가린 뒤 보낸다.
     * 응답의 원문(text)은 촬영한 본인에게 TLS로 돌려주는 것이라 가리지 않는다 — 앱이 화면에
     * 그릴 때 다시 마스킹하고, 사용자가 눈 아이콘으로 직접 해제할 수 있어야 한다.
     */
    private String translate(String text, String targetLanguage) {
        if (targetLanguage == null || targetLanguage.isBlank()) {
            return null;
        }

        try {
            return papagoTranslationClient.translate(SensitiveTextMasker.mask(text), targetLanguage);
        } catch (PapagoTranslationAuthenticationException e) {
            log.error("Papago 번역 인증에 실패했습니다. PAPAGO_TRANSLATION_CLIENT_ID/SECRET 설정을 확인하세요. 원문만 반환합니다.");
            return null;
        } catch (PapagoTranslationApiException e) {
            log.warn("Papago 번역 호출에 실패해 원문만 반환합니다: {}", e.getMessage());
            return null;
        }
    }

    private byte[] readBytes(MultipartFile image) {
        try {
            return image.getBytes();
        } catch (IOException e) {
            throw new DocumentOcrFailedException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
