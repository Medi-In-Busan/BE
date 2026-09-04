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

/**
 * TourAPI contenttypeid(12=관광지, 32=숙박, 38=쇼핑, 39=음식점) 또는 백엔드가 그대로 내려주는
 * enum 이름을 [PlaceType]으로 옮긴다.
 *
 * 부산 관광 카탈로그 상세(feature/tourism)도 TourismCatalogItem.categoryCode를 같은 규칙으로
 * 변환해 장소 유형별 케어 프로필(core/common/PlaceCareProfile.kt)을 재사용하므로 public이다 —
 * 매핑 규칙을 두 벌로 두면 두 상세 화면이 같은 장소를 다른 유형으로 판정하게 된다.
 *
 * 여기 없는 코드(14=문화시설, 25=여행코스, 28=레포츠 등)는 [PlaceType.OTHER]로 떨어진다.
 */
fun String?.toPlaceType(): PlaceType = when (this) {
    "12", "TOURIST_ATTRACTION" -> PlaceType.TOURIST_ATTRACTION
    "39", "RESTAURANT" -> PlaceType.RESTAURANT
    "38", "SHOPPING" -> PlaceType.SHOPPING
    "32", "LODGING" -> PlaceType.LODGING
    "SPA" -> PlaceType.SPA
    "WALK" -> PlaceType.WALK
    else -> PlaceType.OTHER
}
