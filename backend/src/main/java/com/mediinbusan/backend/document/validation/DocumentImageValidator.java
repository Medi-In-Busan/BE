package com.mediinbusan.backend.document.validation;

import com.mediinbusan.backend.document.exception.InvalidDocumentImageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Map;

/** CLOVA OCR 호출 전 이미지 파일에 대한 최소한의 검증을 수행한다. */
@Component
public class DocumentImageValidator {

    private static final Map<String, String> CLOVA_FORMAT_BY_CONTENT_TYPE = Map.of(
        "image/jpeg", "jpg",
        "image/png", "png",
        "image/webp", "webp"
    );

    private final DocumentOcrProperties properties;

    public DocumentImageValidator(DocumentOcrProperties properties) {
        this.properties = properties;
    }

    /** 검증을 통과하면 CLOVA 요청에 실어보낼 이미지 포맷 코드(jpg/png/webp)를 반환한다. */
    public String validateAndResolveFormat(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new InvalidDocumentImageException("업로드된 이미지가 없습니다.");
        }

        String contentType = image.getContentType() == null
            ? ""
            : image.getContentType().toLowerCase(Locale.ROOT);

        if (!properties.allowedContentTypes().contains(contentType)) {
            throw new InvalidDocumentImageException("지원하지 않는 이미지 형식입니다. (jpeg, png, webp만 허용)");
        }

        String format = CLOVA_FORMAT_BY_CONTENT_TYPE.get(contentType);
        if (format == null) {
            throw new InvalidDocumentImageException("지원하지 않는 이미지 형식입니다. (jpeg, png, webp만 허용)");
        }

        if (image.getSize() > properties.maxImageSizeBytes()) {
            throw new InvalidDocumentImageException("이미지 파일 용량이 제한을 초과했습니다.");
        }

        return format;
    }
}
