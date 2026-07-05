package com.mediinbusan.app.data.hospital

fun HospitalDto.toDomain(): Hospital = Hospital(
    id = contentId.orEmpty(),
    name = name.orEmpty(),
    specialties = specialties.orEmpty(),
    address = address.orEmpty(),
    latitude = latitude,
    longitude = longitude,
    phoneNumber = phoneNumber,
    homepageUrl = homepageUrl,
    supportedLanguages = supportedLanguages.orEmpty(),
    description = description,
    imageUrl = imageUrl,
    lastModified = modifiedDate
)
