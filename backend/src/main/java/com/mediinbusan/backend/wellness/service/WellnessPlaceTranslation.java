package com.mediinbusan.backend.wellness.service;

/**
 * 부산맛집정보 getFoodEn/getFoodJa/getFoodZhs 응답 한 건 — WellnessPlaceCandidate와 달리 좌표·이미지·
 * 전화번호는 담지 않는다(이 값들은 언어와 무관하게 getFoodKr/WellnessPlaceCandidate가 이미 갖고 있고,
 * 번역 API는 텍스트 필드만 다시 받아오는 용도라서다).
 */
record WellnessPlaceTranslation(String contentId, String name, String address, String description) {
}
