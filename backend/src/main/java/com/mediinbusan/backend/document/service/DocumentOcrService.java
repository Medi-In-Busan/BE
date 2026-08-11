package com.mediinbusan.backend.document.service;

import com.mediinbusan.backend.document.client.ClovaOcrApiException;
import com.mediinbusan.backend.document.client.ClovaOcrAuthenticationException;
import com.mediinbusan.backend.document.client.ClovaOcrClient;
import com.mediinbusan.backend.document.client.ClovaOcrResponse;
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

    public DocumentOcrService(DocumentImageValidator imageValidator, ClovaOcrClient clovaOcrClient) {
        this.imageValidator = imageValidator;
        this.clovaOcrClient = clovaOcrClient;
    }

    public DocumentOcrResponse extractText(MultipartFile image) {
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

        DocumentOcrResponse response = DocumentOcrDtoMapper.toResponse(clovaResponse);
        log.info("문서 OCR 처리 완료: textLength={}", response.text().length());
        return response;
    }

    private byte[] readBytes(MultipartFile image) {
        try {
            return image.getBytes();
        } catch (IOException e) {
            throw new DocumentOcrFailedException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
