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

enum class TourismCatalogGroup(val label: String, val description: String) {
    PLACES("관광지 탐색", "부산의 장소와 이동 편의 정보를 확인해요."),
    ROUTES("여행 동선", "함께 둘러볼 곳과 걷기 코스를 찾아요."),
    INSIGHTS("여행 데이터", "관광지 혼잡도를 참고해요.")
}

enum class TourismCatalogCategory(
    val label: String,
    val group: TourismCatalogGroup,
    val supportsDistrict: Boolean
) {
    PLACES_KO("부산 관광지", "관광지·음식점·숙박·쇼핑", TourismCatalogGroup.PLACES, true),
    ACCESSIBLE("무장애 관광", "이동 편의 정보를 포함한 관광지", TourismCatalogGroup.PLACES, true),
    PLACES_EN("Busan in English", "영문 관광정보", TourismCatalogGroup.PLACES, true),
    PLACES_JA("日本語の釜山観光", "일문 관광정보", TourismCatalogGroup.PLACES, true),
    PLACES_ZH("中文釜山旅游", "중문 관광정보", TourismCatalogGroup.PLACES, true),
    RELATED("함께 둘러보기", "선택한 지역과 함께 찾는 관광지", TourismCatalogGroup.ROUTES, true),
    WALKING("부산 걷기 코스", "두루누비 부산 걷기 여행길", TourismCatalogGroup.ROUTES, false),
    CROWDING("관광지 혼잡도", "관광지별 예상 혼잡 정보", TourismCatalogGroup.INSIGHTS, true)
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

fun tourismHubCategories(languageCode: String): List<TourismCatalogCategory> = listOf(
    tourismCategoryForLanguage(languageCode),
    TourismCatalogCategory.ACCESSIBLE,
    TourismCatalogCategory.WALKING,
    TourismCatalogCategory.RELATED,
    TourismCatalogCategory.CROWDING
)

enum class BusanDistrict(val label: String) {
    JUNG("중구"), SEO("서구"), DONG("동구"), YEONGDO("영도구"),
    BUSANJIN("부산진구"), DONGNAE("동래구"), NAM("남구"), BUK("북구"),
    HAEUNDAE("해운대구"), SAHA("사하구"), GEUMJEONG("금정구"), GANGSEO("강서구"),
    YEONJE("연제구"), SUYEONG("수영구"), SASANG("사상구"), GIJANG("기장군")
}
