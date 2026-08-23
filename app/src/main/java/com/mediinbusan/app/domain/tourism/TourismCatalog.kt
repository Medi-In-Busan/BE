package com.mediinbusan.app.domain.tourism

data class TourismCatalog(
    val category: TourismCatalogCategory,
    val title: String,
    val description: String,
    val source: String,
    val retrievedAt: String,
    val items: List<TourismCatalogItem>
)

data class TourismCatalogItem(
    val id: String,
    val title: String,
    val subtitle: String?,
    val address: String?,
    val imageUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val details: Map<String, String>
)

enum class TourismCatalogGroup {
    PLACES, ROUTES, INSIGHTS
}

enum class TourismCatalogCategory(
    val label: String,
    val group: TourismCatalogGroup,
    val supportsDistrict: Boolean
) {
    PLACES_KO("부산 관광지", TourismCatalogGroup.PLACES, true),
    ACCESSIBLE("무장애 관광", TourismCatalogGroup.PLACES, true),
    PHOTOS("부산 관광사진", TourismCatalogGroup.PLACES, false),
    PLACES_EN("Busan in English", TourismCatalogGroup.PLACES, true),
    PLACES_JA("日本語の釜山観光", TourismCatalogGroup.PLACES, true),
    PLACES_ZH("中文釜山旅游", TourismCatalogGroup.PLACES, true),
    RELATED("함께 찾는 관광지", TourismCatalogGroup.ROUTES, true),
    HUBS("지역 관광 허브", TourismCatalogGroup.ROUTES, true),
    WALKING("부산 걷기 코스", TourismCatalogGroup.ROUTES, false),
    AUDIO("오디오 관광", TourismCatalogGroup.ROUTES, false),
    CROWDING("관광지 혼잡도", TourismCatalogGroup.INSIGHTS, true)
}

val TourismCatalogCategory.isLanguageVariant: Boolean
    get() = this in setOf(
        TourismCatalogCategory.PLACES_KO,
        TourismCatalogCategory.PLACES_EN,
        TourismCatalogCategory.PLACES_JA,
        TourismCatalogCategory.PLACES_ZH
    )

fun tourismCategoryForLanguage(languageCode: String): TourismCatalogCategory = when (languageCode.lowercase()) {
    "en" -> TourismCatalogCategory.PLACES_EN
    "ja" -> TourismCatalogCategory.PLACES_JA
    "zh" -> TourismCatalogCategory.PLACES_ZH
    else -> TourismCatalogCategory.PLACES_KO
}

enum class BusanDistrict(val label: String) {
    JUNG("중구"), SEO("서구"), DONG("동구"), YEONGDO("영도구"),
    BUSANJIN("부산진구"), DONGNAE("동래구"), NAM("남구"), BUK("북구"),
    HAEUNDAE("해운대구"), SAHA("사하구"), GEUMJEONG("금정구"), GANGSEO("강서구"),
    YEONJE("연제구"), SUYEONG("수영구"), SASANG("사상구"), GIJANG("기장군")
}
