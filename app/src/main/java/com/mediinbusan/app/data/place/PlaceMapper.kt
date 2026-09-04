package com.mediinbusan.app.data.place

fun PlaceDto.toDomain(): Place = Place(
    id = contentId.orEmpty(),
    name = name.orEmpty(),
    type = contentTypeId.toPlaceType(),
    category = placeCategory.toPlaceCategory(),
    address = address.orEmpty(),
    latitude = latitude,
    longitude = longitude,
    imageUrl = imageUrl,
    description = description,
    phoneNumber = phoneNumber,
    distanceFromHospitalMeters = distanceFromHospitalMeters,
    lastModified = modifiedDate,
    isTranslated = translated
)

private fun String?.toPlaceType(): PlaceType = when (this) {
    "12", "TOURIST_ATTRACTION" -> PlaceType.TOURIST_ATTRACTION
    "39", "RESTAURANT" -> PlaceType.RESTAURANT
    "38", "SHOPPING" -> PlaceType.SHOPPING
    "32", "LODGING" -> PlaceType.LODGING
    "SPA" -> PlaceType.SPA
    "WALK" -> PlaceType.WALK
    else -> PlaceType.OTHER
}
