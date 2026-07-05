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
    val lastModified: String?
)
