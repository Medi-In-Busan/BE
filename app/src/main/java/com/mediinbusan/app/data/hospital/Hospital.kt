package com.mediinbusan.app.data.hospital

data class Hospital(
    val id: String,
    val name: String,
    val specialties: List<String>,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val phoneNumber: String?,
    val homepageUrl: String?,
    val supportedLanguages: List<String>,
    val description: String?,
    val imageUrl: String?,
    val lastModified: String?,
    // 아래 3개는 API 필드가 아직 미확정이라 도메인 모델에만 존재하는 상세페이지 전용 표시값이다.
    // 실제 연동 시 HospitalDto/HospitalMapper에서 채워 넣도록 교체한다.
    val imageUrls: List<String> = emptyList(),
    val openingHours: String? = null,
    val isOpen: Boolean? = null
)
