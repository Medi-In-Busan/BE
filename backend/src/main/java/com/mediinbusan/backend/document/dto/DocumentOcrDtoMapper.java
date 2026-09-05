package com.mediinbusan.backend.document.dto;

import com.mediinbusan.backend.document.client.ClovaOcrResponse;
import com.mediinbusan.backend.document.exception.DocumentOcrFailedException;
import org.springframework.http.HttpStatus;

import java.util.List;

/** CLOVA OCR 원본 응답을 그대로 넘기지 않고, 서비스에서 필요한 추출 텍스트만 뽑아낸다. */
public final class DocumentOcrDtoMapper {

    private DocumentOcrDtoMapper() {
    }

    public static String extractText(ClovaOcrResponse clovaResponse) {
        List<ClovaOcrResponse.ImageResult> images = clovaResponse != null ? clovaResponse.images() : null;
        if (images == null || images.isEmpty()) {
            throw new DocumentOcrFailedException(HttpStatus.BAD_GATEWAY);
        }

        ClovaOcrResponse.ImageResult image = images.get(0);
        if (!image.isSuccess()) {
            throw new DocumentOcrFailedException(HttpStatus.BAD_GATEWAY);
        }

        // 단순히 fields를 이어붙이면 표(처방전 약품 목록)의 열이 서로 섞이므로, 실제 배치 복원은
        // DocumentTextLayoutBuilder에 맡긴다. 좌표가 없는 응답에서는 거기서 예전 방식으로 폴백한다.
        return DocumentTextLayoutBuilder.build(image.fields(), image.tables());
    }
}
