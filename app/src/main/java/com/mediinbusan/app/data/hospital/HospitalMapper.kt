package com.mediinbusan.app.data.hospital

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.datastore.SupportedLanguage

// 백엔드는 specialties를 MedicalSpecialty enum 상수명(SKIN_BEAUTY 등) 그대로 내려준다.
// 화면에는 항상 한글 라벨로 보여줘야 해서, 도메인으로 넘어오는 시점에 여기서 한 번만 변환한다.
// 인식 못 하는 값(백엔드가 먼저 새 카테고리를 추가한 경우 등)은 원본 그대로 통과시켜 최소한 안 깨지게 한다.
private fun List<String>.toSpecialtyLabels(): List<String> =
    map { raw -> MedicalCategory.entries.find { it.name == raw }?.label ?: raw }

fun HospitalListItemDto.toDomain(): Hospital = Hospital(
    id = regNo,
    name = name,
    specialties = specialties.toSpecialtyLabels(),
    address = address,
    latitude = latitude,
    longitude = longitude,
    phoneNumber = phone,
    homepageUrl = null,
    supportedLanguages = emptyList(),
    description = null,
    imageUrl = null,
    lastModified = null
)

// targetCountries는 언어 코드(ko/en/zh/ja)와 한글 국가명이 섞여 있어서, 그중 실제 언어 코드로 인식되는
// 것만 골라 배지 표시에 쓴다. 나머지(국가명)는 지금 도메인 모델에 대응 필드가 없어 버려진다.
fun HospitalDetailDto.toDomain(): Hospital = Hospital(
    id = regNo,
    name = name,
    specialties = specialties.toSpecialtyLabels(),
    address = address,
    latitude = latitude,
    longitude = longitude,
    phoneNumber = phone,
    homepageUrl = website,
    supportedLanguages = targetCountries.filter { it in SupportedLanguage.CODES },
    description = descriptionKo,
    imageUrl = null,
    lastModified = null,
    openingHours = businessHours
)
