package com.mediinbusan.backend.document.controller;

import com.mediinbusan.backend.document.dto.DocumentOcrResponse;
import com.mediinbusan.backend.document.service.DocumentOcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Document", description = "진단서/처방전 문서 OCR")
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentOcrController {

    private final DocumentOcrService documentOcrService;

    public DocumentOcrController(DocumentOcrService documentOcrService) {
        this.documentOcrService = documentOcrService;
    }

    @Operation(
        summary = "문서 이미지 OCR",
        description = "진단서/처방전 이미지를 CLOVA OCR로 인식해 추출된 텍스트만 반환한다. "
            + "업로드된 이미지는 요청 처리 동안만 메모리에 두며 서버에 저장하지 않는다."
    )
    @PostMapping(value = "/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentOcrResponse ocr(@RequestPart("image") MultipartFile image) {
        return documentOcrService.extractText(image);
    }
}
