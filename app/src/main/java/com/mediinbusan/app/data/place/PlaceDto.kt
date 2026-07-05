package com.mediinbusan.app.data.place

import kotlinx.serialization.Serializable

/**
 * 한국관광공사 관광정보서비스(국문/영문) 응답 DTO.
 * TODO: 실제 API 문서 확인 후 정확한 필드명/구조로 교체한다 (현재는 플레이스홀더 형태).
 */
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
    val modifiedDate: String? = null
)
