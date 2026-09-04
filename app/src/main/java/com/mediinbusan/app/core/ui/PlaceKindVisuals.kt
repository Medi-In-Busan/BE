package com.mediinbusan.app.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
        PlaceCategory.OTHER -> return null
    }
    return PlaceKindVisual(icon, ShoppingKindColor, ShoppingKindInk)
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
