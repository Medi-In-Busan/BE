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

        return joinFields(image.fields());
    }

    // CLOVA는 fields[]를 단어/줄 단위로 쪼개 내려주고, 각 field의 lineBreak로 다음 줄바꿈 여부를 알려준다.
    private static String joinFields(List<ClovaOcrResponse.Field> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (ClovaOcrResponse.Field field : fields) {
            if (field.inferText() != null) {
                text.append(field.inferText());
            }
            text.append(Boolean.TRUE.equals(field.lineBreak()) ? "\n" : " ");
        }
        return text.toString().strip();
    }
}
