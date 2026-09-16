package com.mediinbusan.backend.guide.controller;

import com.mediinbusan.backend.guide.dto.TreatmentBriefingTranslateRequest;
import com.mediinbusan.backend.guide.dto.TreatmentBriefingTranslateResponse;
import com.mediinbusan.backend.guide.service.TreatmentBriefingTranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Guide", description = "F-008 의료 이용 가이드 - 진료 브리핑 카드 번역")
@RestController
@RequestMapping("/api/v1/guide/treatment-briefing")
public class TreatmentBriefingTranslationController {

    private final TreatmentBriefingTranslationService translationService;

    public TreatmentBriefingTranslationController(TreatmentBriefingTranslationService translationService) {
        this.translationService = translationService;
    }

    @Operation(
        summary = "내 진료 카드 한국어 번역",
        description = "STEP04 진료 브리핑 카드 필드(방문 목적/증상/알레르기/복용약/메모)를 sourceLang(ko/en/zh/ja)에서 "
            + "한국어로 번역한다. sourceLang이 ko면 Papago를 호출하지 않고 원문을 그대로 반환한다. "
            + "번역 API 호출이 실패한 필드는 원문을 그대로 반환한다(그레이스풀 디그레이드). "
            + "returnDate(귀국·체류 일정)는 날짜 데이터라 번역 대상이 아니다."
    )
    @PostMapping("/translate")
    public TreatmentBriefingTranslateResponse translate(@RequestBody TreatmentBriefingTranslateRequest request) {
        return translationService.translate(request);
    }
}
