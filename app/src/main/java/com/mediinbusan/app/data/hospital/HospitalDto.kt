package com.mediinbusan.app.data.hospital

import kotlinx.serialization.Serializable

@Serializable
data class HospitalPageDto(
    val content: List<HospitalListItemDto> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0
)

/** GET /api/hospitals 리스트 항목. 백엔드 HospitalListItemResponse와 1:1. */
@Serializable
data class HospitalListItemDto(
    val regNo: String,
    val name: String,
    val institutionType: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val specialties: List<String> = emptyList()
)

/**
 * GET /api/hospitals/{regNo} 상세. 백엔드 HospitalDetailResponse와 1:1.
 * description/businessHours는 요청한 lang 쿼리 파라미터에 맞는 값 하나만 내려온다(백엔드가 ko로 폴백 처리).
 */
@Serializable
data class HospitalDetailDto(
    val regNo: String,
    val name: String,
    val institutionType: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val website: String? = null,
    val businessHours: String? = null,
    val description: String? = null,
    val specialties: List<String> = emptyList(),
    val targetCountries: List<String> = emptyList()
)
