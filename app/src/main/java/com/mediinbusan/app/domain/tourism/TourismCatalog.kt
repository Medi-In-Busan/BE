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
    // TourAPI contenttypeid(12=관광지, 14=문화시설, 25=여행코스, 28=레포츠, 32=숙박, 38=쇼핑,
    // 39=음식점). core/i18n/TourismCategoryStrings.kt의 translatedLabel로 화면에 그린다.
    val categoryCode: String? = null,
    val details: Map<String, String>
)

// TourismCatalogItem.categoryCode(contenttypeid)를 부산관광 슬라이드 태그 3종으로 묶는다 —
// 관광지/문화시설/레포츠/쇼핑/여행코스는 SPOT 하나로, 숙박은 LODGING, 음식점은 FOOD.
// NearbyScreen.kt(태그 이미지 표시)와 NearbyViewModel.kt(미리보기 항목 균형 배분)가 같이 쓴다.
// TourAPI는 국문 서비스(PLACES_KO)와 외국어 서비스(PLACES_EN/JA/ZH)가 완전히 다른 contenttypeid
// 체계를 쓴다 — 실제 라이브 API로 확인한 값: 국문 12=관광지/14=문화시설/25=여행코스/28=레포츠/
// 32=숙박/38=쇼핑/39=음식점, 외국어 75=레포츠/76=관광지/78=문화시설/79=쇼핑/80=숙박/82=음식점/
// 85=축제행사. 코드 값 자체가 두 체계 사이에서 겹치지 않아 하나의 when으로 같이 처리해도 된다.
enum class TourismTagGroup { SPOT, LODGING, FOOD }

fun String.toTourismTagGroup(): TourismTagGroup? = when (this) {
    "12", "14", "25", "28", "38" -> TourismTagGroup.SPOT
    "32" -> TourismTagGroup.LODGING
    "39" -> TourismTagGroup.FOOD
    "75", "76", "78", "79", "85" -> TourismTagGroup.SPOT
    "80" -> TourismTagGroup.LODGING
    "82" -> TourismTagGroup.FOOD
    else -> null
}

// "부산 관광지" 리스트업 화면의 카테고리 필터 3종(관광지/숙박/맛집)이 실제로 봐야 하는 단일
// contenttypeid — PLACES_KO만 국문 서비스 코드를 쓰고, PLACES_EN/JA/ZH는 외국어 서비스 코드를 쓴다.
data class TourismPlaceCategoryCodes(val spot: String, val lodging: String, val food: String)

fun TourismCatalogCategory.placeCategoryCodes(): TourismPlaceCategoryCodes =
    if (this == TourismCatalogCategory.PLACES_KO) {
        TourismPlaceCategoryCodes(spot = "12", lodging = "32", food = "39")
    } else {
        TourismPlaceCategoryCodes(spot = "76", lodging = "80", food = "82")
    }

enum class TourismCatalogGroup(val label: String, val description: String) {
    PLACES("관광지 탐색", "부산의 장소와 이동 편의 정보를 확인해요."),
    ROUTES("여행 동선", "함께 둘러볼 곳과 걷기 코스를 찾아요."),
    INSIGHTS("여행 데이터", "관광지 혼잡도를 참고해요.")
}

enum class TourismCatalogCategory(
    val label: String,
    val shortDescription: String,
    val group: TourismCatalogGroup,
    val supportsDistrict: Boolean
) {
    PLACES_KO("부산 관광지", "관광지·음식점·숙박·쇼핑", TourismCatalogGroup.PLACES, true),
    ACCESSIBLE("무장애 관광", "이동 편의 정보를 포함한 관광지", TourismCatalogGroup.PLACES, true),
    PHOTOS("부산 관광사진", "한국관광공사 관광사진", TourismCatalogGroup.PLACES, false),
    PLACES_EN("Busan in English", "영문 관광정보", TourismCatalogGroup.PLACES, true),
    PLACES_JA("日本語の釜山観光", "일문 관광정보", TourismCatalogGroup.PLACES, true),
    PLACES_ZH("中文釜山旅游", "중문 관광정보", TourismCatalogGroup.PLACES, true),
    RELATED("함께 둘러보기", "선택한 지역과 함께 찾는 관광지", TourismCatalogGroup.ROUTES, true),
    HUBS("지역 관광 허브", "구·군별 방문 중심 관광지", TourismCatalogGroup.ROUTES, true),
    WALKING("부산 걷기 코스", "두루누비 부산 걷기 여행길", TourismCatalogGroup.ROUTES, false),
    AUDIO("오디오 관광", "부산 중심부 오디오 콘텐츠", TourismCatalogGroup.ROUTES, false),
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

/**
 * 혼잡도 지수(관광지 혼잡도 = TatsCnctrRateService)를 항목에서 뽑아낸다.
 *
 * 필드명이 한 군데로 정해지지 않는다 — 원본 응답은 `cnctrRate`(문서엔 `tatsCnctrRate`로 적혀 있다),
 * 상세 화면으로 넘길 때는 `congestionRate`로 옮겨 담고, 아무 키도 없으면 subtitle에 지수 문자열만
 * 들어온다. 그래서 후보를 전부 보고 첫 숫자를 집는다.
 *
 * 핫플레이스 랭킹(RankTourismHotPlacesUseCase)·혼잡도 카드(TourismCatalogScreen)·상세 진입 정규화
 * (core/common/PendingTourismCatalogItem)가 같은 규칙을 봐야 해서 여기 한 곳에 둔다.
 */
fun TourismCatalogItem.congestionRateOrNull(): Double? {
    val rawValue = details.entries.firstOrNull { (key, _) ->
        key.equals("tatsCnctrRate", ignoreCase = true) ||
            key.equals("cnctrRate", ignoreCase = true) ||
            key.equals("congestionRate", ignoreCase = true)
    }?.value ?: subtitle
    return rawValue
        ?.replace(",", "")
        ?.let { CONGESTION_NUMBER_PATTERN.find(it)?.value }
        ?.toDoubleOrNull()
}

/**
 * 항목이 어느 구·군 것인지 이름 필드/주소로 판정한다. 판정할 근거가 없으면 null.
 *
 * 혼잡도 항목엔 좌표가 없어서(구·군 코드와 관광지 이름만 온다) 상세 화면이 관광공사 상세를 이름으로
 * 재조회할 때(TourismCatalogRepository.findMatchingPlace) 구·군을 꼭 같이 넘겨야 한다 — 핫플레이스
 * 카드에서 들어오든 혼잡도 리스트에서 들어오든 같은 구·군으로 판정되어야 같은 상세가 나온다.
 */
fun TourismCatalogItem.busanDistrictOrNull(): BusanDistrict? {
    val districtText = listOfNotNull(
        details["signguNm"],
        details["signguName"],
        address
    ).joinToString(" ")

    return BusanDistrict.entries.firstOrNull { districtText.contains(it.label) }
}

private val CONGESTION_NUMBER_PATTERN = Regex("-?[0-9]+(?:[.][0-9]+)?")
