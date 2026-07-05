package com.mediinbusan.app.data.hospital

import kotlinx.serialization.Serializable

/**
 * 한국관광공사 의료관광정보 서비스 응답 DTO.
 * TODO: 실제 API 문서 확인 후 정확한 필드명/구조로 교체한다 (현재는 플레이스홀더 형태).
 */
@Serializable
data class HospitalDto(
    val contentId: String? = null,
    val name: String? = null,
    val specialties: List<String>? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phoneNumber: String? = null,
    val homepageUrl: String? = null,
    val supportedLanguages: List<String>? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val modifiedDate: String? = null
)
