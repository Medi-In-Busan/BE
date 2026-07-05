package com.mediinbusan.app.data.place

fun PlaceDto.toDomain(): Place = Place(
    id = contentId.orEmpty(),
    name = name.orEmpty(),
    type = contentTypeId.toPlaceType(),
    address = address.orEmpty(),
    latitude = latitude,
    longitude = longitude,
    imageUrl = imageUrl,
    description = description,
    phoneNumber = phoneNumber,
    lastModified = modifiedDate
)

// TODO: 실제 관광정보서비스 contentTypeId 코드값 확인 후 매핑을 정확히 채운다.
private fun String?.toPlaceType(): PlaceType = when (this) {
    "12" -> PlaceType.TOURIST_ATTRACTION
    "39" -> PlaceType.RESTAURANT
    "38" -> PlaceType.SHOPPING
    "32" -> PlaceType.LODGING
    else -> PlaceType.OTHER
}
