package com.mediinbusan.app.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush as ComposeBrush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.CoralInk
import com.mediinbusan.app.data.place.PlaceCategory
import com.mediinbusan.app.data.place.PlaceType

/**
 * 장소 종류를 화면에 그릴 때 쓰는 아이콘과 색 한 쌍.
 *
 * 예전엔 같은 성격의 표가 두 벌로 갈려 있었다 — `MapScreen`의 `fallbackTint`/`fallbackIcon`(7종,
 * SkyBlue/CoralPrimary/MediBlue40 팔레트)과 `PlaceDetailScreen`의 `tint`/`icon`(지도 핀 색 2종).
 * 같은 장소가 목록 썸네일에서는 하늘색, 상세에서는 파란색으로 나오던 이유다. 여기 하나로 모은다.
 */
/**
 * @param color 채우기용 색 — 지도 핀, 옅은 배지 배경, 히어로 그라데이션처럼 **면**으로 쓰는 자리.
 * @param ink 같은 계열의 진한 색 — 흰 배경 위 **글자와 아이콘**용. 채우기 색을 그대로 글자에 쓰면
 *   음식(#FAA85C, 약 2:1)·쇼핑(#0FA3B1, 약 2.9:1)처럼 대비가 모자라 읽기 어려웠다. 면과 글자를
 *   나눠 두면 같은 종류라는 인상은 유지하면서 대비만 확보할 수 있다(모두 흰색 대비 4.5:1 이상).
 */
data class PlaceKindVisual(val icon: ImageVector, val color: Color, val ink: Color)

/**
 * [type]과 [category]로 아이콘·색을 고른다. 세부 분류를 아는 장소는 그쪽이 이긴다 —
 * 백화점/전통시장/면세점은 같은 "쇼핑"이라도 서로 다른 아이콘을 받는다.
 *
 * @param type 장소 종류. null이면 병원(장소가 아님)으로 본다.
 */
fun placeKindVisual(type: PlaceType?, category: PlaceCategory = PlaceCategory.OTHER): PlaceKindVisual {
    if (type == null) return PlaceKindVisual(Icons.Default.LocalHospital, HospitalKindColor, HospitalKindInk)
    // 세부 분류를 아는 경우(현재는 쇼핑 하위만)가 먼저다. OTHER면 장소 종류로 내려간다.
    //
    // 반드시 SHOPPING일 때만 본다 — placeType과 placeCategory는 백엔드가 각각 따로 내려주고,
    // WellnessPlace.updateFrom은 새 categoryCode가 없으면 이전 값을 그대로 두므로 타입이 바뀐
    // 장소에 쇼핑 분류가 남아 있을 수 있다. 그때 이 분기가 먼저 걸리면 음식점이 쇼핑백 아이콘을 달았다.
    if (type == PlaceType.SHOPPING) {
        shoppingVisual(category)?.let { return it }
    }
    // 음식도 같은 규칙 — 한식/일식/중식/양식/카페는 색(FoodKindColor)을 공유하고 아이콘만 나뉜다.
    if (type == PlaceType.RESTAURANT) {
        restaurantVisual(category)?.let { return it }
    }
    return when (type) {
        PlaceType.TOURIST_ATTRACTION -> PlaceKindVisual(Icons.Default.PhotoCamera, TouristKindColor, TouristKindInk)
        PlaceType.RESTAURANT -> PlaceKindVisual(Icons.Default.Restaurant, FoodKindColor, FoodKindInk)
        PlaceType.SHOPPING -> PlaceKindVisual(Icons.Default.ShoppingBag, ShoppingKindColor, ShoppingKindInk)
        PlaceType.LODGING -> PlaceKindVisual(Icons.Default.Hotel, LodgingKindColor, LodgingKindInk)
        PlaceType.SPA -> PlaceKindVisual(Icons.Default.Spa, SpaKindColor, SpaKindInk)
        PlaceType.WALK -> PlaceKindVisual(Icons.AutoMirrored.Filled.DirectionsWalk, WalkKindColor, WalkKindInk)
        PlaceType.OTHER -> PlaceKindVisual(Icons.Default.Place, TouristKindColor, TouristKindInk)
    }
}

/**
 * 쇼핑 하위 분류는 **아이콘만 나뉘고 색은 [ShoppingKindColor] 하나를 공유한다.**
 *
 * 백화점·시장·면세점마다 색까지 다르게 주면 목록이 무지개가 되고, 정작 "이건 쇼핑이구나"라는
 * 상위 묶음이 안 읽힌다. 색은 묶음(쇼핑/관광지/숙소/음식/병원)을, 아이콘은 그 안의 종류를
 * 담당하게 나눠 둔다 — 지도 앱들이 쓰는 방식이다.
 */
private fun shoppingVisual(category: PlaceCategory): PlaceKindVisual? {
    val icon = when (category) {
        PlaceCategory.DEPARTMENT_STORE -> Icons.Default.LocalMall
        PlaceCategory.TRADITIONAL_MARKET -> Icons.Default.Storefront
        PlaceCategory.DUTY_FREE -> Icons.Default.CardGiftcard
        PlaceCategory.LARGE_MART -> Icons.Default.ShoppingCart
        PlaceCategory.SPECIALTY_STORE -> Icons.Default.ShoppingBag
        PlaceCategory.LOCAL_PRODUCTS -> Icons.Default.Redeem
        PlaceCategory.CRAFT_WORKSHOP -> Icons.Default.Brush
        // 음식 하위 분류와 OTHER는 쇼핑이 아니다 — 아래 restaurantVisual/장소 종류로 내려보낸다.
        PlaceCategory.KOREAN_FOOD,
        PlaceCategory.WESTERN_FOOD,
        PlaceCategory.JAPANESE_FOOD,
        PlaceCategory.CHINESE_FOOD,
        PlaceCategory.FUSION_FOOD,
        PlaceCategory.CAFE,
        PlaceCategory.OTHER -> return null
    }
    return PlaceKindVisual(icon, ShoppingKindColor, ShoppingKindInk)
}

/** [shoppingVisual]의 음식판 — 색은 [FoodKindColor] 하나로 묶고 아이콘만 요리 종류로 나눈다. */
private fun restaurantVisual(category: PlaceCategory): PlaceKindVisual? {
    val icon = when (category) {
        PlaceCategory.KOREAN_FOOD -> Icons.Default.RiceBowl
        PlaceCategory.WESTERN_FOOD -> Icons.Default.LocalPizza
        PlaceCategory.JAPANESE_FOOD -> Icons.Default.SetMeal
        PlaceCategory.CHINESE_FOOD -> Icons.Default.RamenDining
        PlaceCategory.FUSION_FOOD -> Icons.Default.DinnerDining
        PlaceCategory.CAFE -> Icons.Default.LocalCafe
        else -> return null
    }
    return PlaceKindVisual(icon, FoodKindColor, FoodKindInk)
}

// 색은 지도 핀 세 가지(KakaoMapView의 clusterColor)를 기준으로 삼고, 핀 하나에 여러 종류가 묶이는
// "관광" 쪽만 같은 계열 안에서 갈라 쓴다. 따뜻한 색(주황) = 먹는 곳, 빨강 = 의료, 차가운 색 =
// 볼거리·살거리 — 이 큰 구분이 먼저 읽히고, 그 안의 종류는 아이콘이 맡는다.
private val HospitalKindColor = Color(0xFFFB5364) // 병원 핀과 같은 값
private val FoodKindColor = Color(0xFFFAA85C) // 음식 핀과 같은 값
private val TouristKindColor = Color(0xFF326BF6) // 관광 핀과 같은 값
private val LodgingKindColor = Color(0xFF6C5CE7) // 관광 핀의 파랑에서 보라 쪽으로
private val ShoppingKindColor = Color(0xFF0FA3B1) // 관광 핀의 파랑에서 청록 쪽으로
private val SpaKindColor = Color(0xFFE8749B)
private val WalkKindColor = Color(0xFF2FA36B)

// 위 색들을 그대로 흰 배경 위 글자로 쓰면 특히 음식(주황)·쇼핑(청록)이 흐릿하게 뜬다 — 같은 계열을
// 어둡게 낮춘 짝을 따로 둔다(PlaceKindVisual.ink 주석 참고). 면 색은 그대로 두므로 지도 핀·배지
// 배경의 인상은 바뀌지 않고, 글자만 또렷해진다.
private val HospitalKindInk = CoralInk // 값이 같다 — 코랄 글자색 토큰을 그대로 쓴다
private val FoodKindInk = Color(0xFFA85C00)
private val TouristKindInk = Color(0xFF1E4FC8)
private val LodgingKindInk = Color(0xFF5A49D6)
private val ShoppingKindInk = Color(0xFF0B7C87)
private val SpaKindInk = Color(0xFFB23A64)
private val WalkKindInk = Color(0xFF1B7A4B)

// 사진이 없는 장소의 대체 썸네일 — 종류별 색 그라데이션 + 아이콘 배지. Map 화면(MapPlaceListRow)과
// 부산관광 홈 슬라이더(NearbyScreen) 등 여러 feature가 같은 톤을 쓰도록 core/ui에 공유해 둔다.
// 상세 하단 "주변 ○○" 카드(NearbyPlacesSection)도 같은 걸 쓴다 — 목록에서 보던 자리표시자가
// 상세로 들어갔다고 다른 그림으로 바뀌면 같은 장소로 읽히지 않는다.
//
// @param iconSize 아이콘 지름. 목록 썸네일(77~104dp)과 카드를 통째로 채우는 자리(164dp)는 박스
//   크기가 두 배 넘게 차이나서, 같은 26dp로 두면 큰 자리에서는 점처럼 보인다. 그림 자체(색·아이콘·
//   그라데이션)는 그대로 두고 크기만 호출부가 맞춘다.
@Composable
fun PlaceFallbackThumbnail(
    visual: PlaceKindVisual,
    modifier: Modifier = Modifier,
    iconSize: Dp = 26.dp
) {
    Box(
        modifier = modifier.background(
            ComposeBrush.verticalGradient(listOf(visual.color.copy(alpha = 0.20f), visual.color.copy(alpha = 0.06f)))
        ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.color,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * 관광 데이터 항목(TourismCatalogItem)의 `categoryCode`(TourAPI contenttypeid)로 [placeKindVisual]을
 * 고른다 — 웰니스 장소(Place)와 달리 관광 항목에는 PlaceType이 없어서, 같은 "부산 내호냉면"이
 * 장소 목록에서는 음식 아이콘, 추천 코스에서는 무관한 풍경 사진으로 나오던 걸 한쪽으로 맞춘다.
 *
 * 코드 체계는 [com.mediinbusan.app.domain.tourism.toTourismTagGroup]과 같다 — 국문 서비스
 * (12/14/25/28/32/38/39)와 외국어 서비스(75/76/78/79/80/82/85)가 서로 다른 값을 쓰지만 값이
 * 겹치지 않아 하나의 when으로 처리한다. 모르는 코드·null은 OTHER(지도 마커)로 떨어진다.
 */
fun tourismKindVisual(categoryCode: String?): PlaceKindVisual = placeKindVisual(tourismPlaceType(categoryCode))

/**
 * [tourismKindVisual]이 쓰는 `categoryCode` → [PlaceType] 표. 색·아이콘 말고 **종류 자체**가
 * 필요한 자리(상세 히어로의 마스코트 선택 — [PlaceFallbackCharacterHero])를 위해 따로 노출한다.
 * 모르는 코드·null은 [PlaceType.OTHER]다.
 */
fun tourismPlaceType(categoryCode: String?): PlaceType = when (categoryCode) {
    "12", "76" -> PlaceType.TOURIST_ATTRACTION // 관광지
    "14", "78" -> PlaceType.TOURIST_ATTRACTION // 문화시설
    "85" -> PlaceType.TOURIST_ATTRACTION // 축제·행사
    "25" -> PlaceType.WALK // 여행코스
    "28", "75" -> PlaceType.WALK // 레포츠
    "32", "80" -> PlaceType.LODGING // 숙박
    "38", "79" -> PlaceType.SHOPPING // 쇼핑
    "39", "82" -> PlaceType.RESTAURANT // 음식점
    else -> PlaceType.OTHER
}

/**
 * 사진이 없는 **상세 화면 히어로**에 세우는 종류별 마스코트 — 종류 색 그라데이션 위에 캐릭터를
 * 얹는다. 웰니스 장소 상세(`PlaceDetailScreen`)와 관광 항목 상세
 * (`TourismCatalogItemDetailScreen`)가 같이 쓴다.
 *
 * 목록·지도 썸네일의 [PlaceFallbackThumbnail](색+아이콘)과 역할이 다르다 — 작은 썸네일에 캐릭터를
 * 넣으면 뭉개져서 안 읽히고, 260dp 히어로에 26dp 아이콘만 얹으면 빈 화면처럼 보인다. 그래서
 * **썸네일은 아이콘, 상세 히어로는 마스코트**로 나눈다.
 *
 * 마스코트는 두 장뿐이라 앱 전체가 쓰는 같은 기준으로 고른다: [PlaceType.RESTAURANT]만 "식사",
 * 나머지(관광지·숙박·쇼핑·스파·산책·OTHER)는 전부 "관광"이다(`Place.toMapPin`,
 * `MapUiState.visiblePlaces`, 지도 핀 2종과 동일한 기준).
 */
@Composable
fun PlaceFallbackCharacterHero(
    type: PlaceType,
    kindColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            ComposeBrush.verticalGradient(listOf(kindColor.copy(alpha = 0.22f), CharacterHeroGroundColor))
        ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = if (type == PlaceType.RESTAURANT) {
                    R.drawable.travel_character_food
                } else {
                    R.drawable.travel_character
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/** 마스코트가 서는 바닥 — 종류 색에서 옅은 회색으로 떨어지는 그라데이션의 아래쪽 끝. */
private val CharacterHeroGroundColor = Color(0xFFEDEDF2)

/**
 * 관광 데이터 항목(TourismCatalogItem)에 사진이 없을 때 그 자리에 그리는 자리표시자 —
 * **지도(feature/map)의 장소 자리표시자와 같은 그림이다.** 무장애 관광·부산관광 핫플레이스의 목록
 * 썸네일과 상세 히어로가 전부 이걸 쓰므로, 같은 장소를 지도에서 보든 목록에서 보든 상세에서 보든
 * 사진 없음 표시가 같은 모양으로 나온다.
 *
 * [PlaceFallbackThumbnail]을 그대로 쓰지 않고 한 겹 감싸는 이유는 **불투명한 흰 밑색** 때문이다.
 * 그 그라데이션은 반투명(0.20~0.06)이라 지도처럼 흰 카드 위에 놓일 때를 기준으로 색이 맞춰져 있는데,
 * 관광 카드들은 밑에 옅은 핑크(CoralPrimaryContainer)나 카테고리 accent 그라데이션을 깔고 있어서
 * 그냥 얹으면 숙박(보라)·음식(주황)이 전부 그 밑색으로 물든다. 흰 밑색을 같이 깔아 어느 카드에
 * 놓이든 지도에서 보는 색 그대로 나오게 한다.
 *
 * `categoryCode`가 null이거나 표에 없으면(혼잡도 기반 핫플레이스가 그렇다 — TourAPI 혼잡도 응답에는
 * contenttypeid가 없다) 지도와 똑같이 `OTHER`(파란 지도 마커)로 떨어진다.
 */
@Composable
fun TourismFallbackThumbnail(
    categoryCode: String?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 26.dp
) {
    PlaceFallbackThumbnail(
        visual = tourismKindVisual(categoryCode),
        modifier = modifier.background(Color.White),
        iconSize = iconSize
    )
}
