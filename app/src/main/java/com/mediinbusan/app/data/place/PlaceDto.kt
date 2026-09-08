package com.mediinbusan.app.data.place

import kotlinx.serialization.Serializable

// name/address/description은 요청한 lang 쿼리 파라미터에 맞는 값 하나만 내려온다(백엔드가 ko로
// 폴백 처리 — HospitalDto와 같은 규칙, WellnessDtoMapper 참고).
@Serializable
data class PlaceDto(
    val contentId: String? = null,
    val name: String? = null,
    val contentTypeId: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val phoneNumber: String? = null,
    val modifiedDate: String? = null,
    val distanceFromHospitalMeters: Double? = null,
    // 요청한 lang의 이름 번역이 실제로 있는지(ko는 항상 true) — 지도 "번역된 장소만" 필터가 쓴다.
    // 구버전 백엔드 대비 기본값은 true(필터 없이 항상 보이던 예전 동작과 동일하게 폴백).
    val translated: Boolean = true,
    // WellnessPlaceCategory 이름(백엔드). contentTypeId보다 한 단계 자세한 분류로 "쇼핑" 안의
    // 백화점/전통시장/면세점을 가른다. 이 필드를 안 내려주는 구버전 백엔드에서는 null이 되고,
    // toPlaceCategory()가 OTHER로 떨어뜨려 예전처럼 PlaceType 라벨만 쓰이게 된다.
    val placeCategory: String? = null,
    // 상세 화면 "방문 정보" 6칸. 백엔드가 TourAPI detailIntro2와 부산맛집정보에서 모아 내려주는
    // 원문(한국어)이고, 소스가 안 주는 칸은 null이다 — 이름·주소·설명과 달리 언어별 값이 없다
    // (백엔드 WellnessVisitInfo 주석 참고). 이 필드를 모르는 구버전 백엔드에서는 전부 null이 되고
    // 화면에서 해당 행이 빠질 뿐이다.
    val businessHours: String? = null,
    val restDate: String? = null,
    val signatureMenu: String? = null,
    val usageFee: String? = null,
    val parkingInfo: String? = null,
    val homepageUrl: String? = null
)
