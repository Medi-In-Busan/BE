package com.mediinbusan.backend.wellness.dto;

public record WellnessIngestionResponse(
    int tourApiCandidates,
    int busanFoodApiCandidates,
    int inserted,
    int updated,
    int skipped,
    int duplicatesRemoved,
    // 카카오 로컬 검색 소스(kakao-*)는 상세 내용(설명·이미지)이 빈약해 더 이상 수집하지 않고, 과거
    // ingest로 이미 들어간 행도 매번 정리한다 — WellnessIngestionService.ingest() 참고.
    int kakaoPlacesRemoved,
    int foodTranslationsApplied,
    // tour-* 소스는 contentId로 언어 미러와 매칭이 안 돼(WellnessIngestionService의
    // applyTourTranslationsByLocation 참고) 이름+좌표로 확인된 것만 반영한다 — 실측 기준 매칭률은
    // 100%가 아니다(부산시청 반경 25km EN 173건 중 58건 ≈ 33%, 대신 주요 관광지 위주로 매칭됨).
    int tourTranslationsApplied,
    long totalPlaces
) {
}
