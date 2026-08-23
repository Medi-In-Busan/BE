package com.mediinbusan.app.core.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelTransition
import com.kakao.vectormap.label.Transition
import com.mediinbusan.app.R
import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.designsystem.TextSecondary

enum class MapPinType { HOSPITAL, TOURIST, FOOD }

data class MapPin(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val type: MapPinType,
    val selected: Boolean = false
)

/**
 * 서면(부전동) 좌표. 시드 데이터(V2__seed_hospitals.sql)의 병원 대다수가 서면 일대에 몰려 있어
 * 좌표 하나 없이 지도를 열어야 할 때 쓰는 화면 기본 중심점으로 삼는다.
 * 사용자의 실제 위치가 아니다 — 이 앱은 위치 권한을 요청하지 않는다(CLAUDE.md §1 참고).
 * core/common/GeoDistance.kt의 DefaultSearchOrigin과 같은 좌표(검색 결과 "가까운순" 정렬 기준점)를 가리키는 단일 소스.
 */
val BusanDefaultCenter: LatLng = LatLng.from(DefaultSearchOrigin.LATITUDE, DefaultSearchOrigin.LONGITUDE)

/**
 * KakaoMapSdk.init() 성공 여부를 기록하는 플래그. Android Vector Map SDK v2의 공개 API에는
 * 초기화 여부를 조회하는 메서드가 없어 MediInBusanApp.onCreate()에서 init() 성공 시 직접 세팅한다.
 */
object KakaoMapAvailability {
    @Volatile
    var isAvailable: Boolean = false

    @Volatile
    var unavailableReason: String = "이 기기에서는 지도를 표시할 수 없습니다"
}

/**
 * 실제 com.kakao.vectormap SDK를 감싼 공용 지도 컴포저블.
 * KAKAO_NATIVE_APP_KEY가 실제 키로 채워지기 전에는 [MapLifeCycleCallback.onMapError]가 호출되어
 * 타일이 비어 보이는 게 정상이다 — 코드/마커/카메라 로직 자체는 실제 키가 들어오면 그대로 동작한다.
 *
 * [recenterRequestId]가 바뀔 때마다(0은 최초 무시) pins 유무와 무관하게 카메라를 [BusanDefaultCenter]로
 * 이동한다 — "기본 위치로 이동" 버튼처럼 사용자가 명시적으로 재중심을 요청했을 때만 쓰는 트리거다.
 *
 * [searchAreaRequestId]가 바뀔 때마다(0은 최초 무시) 그 시점의 지도 카메라 중심 좌표로 [onSearchArea]를 호출한다
 * — "이 위치에서 검색" 버튼용 트리거다. 기기 GPS가 아니라 사용자가 지도를 움직여서 만든 화면 중심 좌표라는 점에
 * 주의: CLAUDE.md §1의 "GPS 위치를 서버로 전송하지 않는다" 제약과는 무관하다.
 *
 * [fitCameraToPins]가 true(기본값)면 pins 변경 시마다 카메라가 그 pins 전체를 담도록 자동으로
 * 움직인다(병원 상세의 "지도에서 보기"처럼 소수의 좌표를 한 화면에 보여줘야 할 때 적합).
 * false면 지도가 처음 뜰 때 [BusanDefaultCenter](서면)로 한 번만 이동하고, 이후 pins가 바뀌어도
 * 카메라를 건드리지 않는다 — 병원 전체 브라우징처럼 핀이 부산 전역에 흩어져 있어 fitMapPoints를 쓰면
 * 서면 클러스터가 화면에서 작아져 버리는 경우, 그리고 "이 위치에서 검색" 이후 사용자가 이동시킨
 * 카메라 위치를 결과 목록 갱신 때문에 다시 튕겨내고 싶지 않은 경우에 쓴다.
 */
@Composable
fun KakaoMapView(
    pins: List<MapPin>,
    modifier: Modifier = Modifier,
    onPinClick: (String) -> Unit = {},
    onMapClick: () -> Unit = {},
    recenterRequestId: Int = 0,
    searchAreaRequestId: Int = 0,
    onSearchArea: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
    fitCameraToPins: Boolean = true
) {
    // libK3fAndroid.so는 arm64-v8a/armeabi-v7a로만 배포되어 x86_64 에뮬레이터에서는
    // KakaoMapSdk.init()이 실패한다(MediInBusanApp.onCreate() 참고). 그 경우 MapView를 만들지
    // 않고 폴백을 보여준다 — 실기기/ARM 에뮬레이터에서는 이 분기를 타지 않는다.
    if (!KakaoMapAvailability.isAvailable) {
        MapUnavailableFallback(modifier)
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    // pinId -> 현재 그 자리에 떠 있는 Label과, 그 Label이 마지막으로 반영한 MapPin 상태.
    // 선택 여부만 바뀐 마커를 매번 layer.removeAll()+addLabels()로 다시 그리면 관련 없는 다른
    // 라벨까지 전부 깜빡여 부자연스럽다 — 대신 바뀐 라벨만 Label.changeStyles(..., animate=true)로
    // 애니메이션과 함께 스타일만 갈아끼운다.
    val trackedLabels = remember { mutableMapOf<String, TrackedPinLabel>() }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.finish()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.apply {
                start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() = Unit
                        override fun onMapError(error: Exception) = Unit
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(map: KakaoMap) {
                            kakaoMap = map
                        }
                    }
                )
            }
        }
    )

    // 지도가 처음 준비됐을 때 서면으로 카메라를 맞춰둔다. fitCameraToPins=true인 화면(예: 병원 상세의
    // "지도에서 보기")은 바로 아래 LaunchedEffect(kakaoMap, pins)가 pins 도착 즉시 알맞은 위치로
    // 다시 옮기므로 여기서는 초기 프레임이 빈 지도로 안 보이게 하는 역할만 한다.
    LaunchedEffect(kakaoMap) {
        val map = kakaoMap ?: return@LaunchedEffect
        map.moveCamera(CameraUpdateFactory.newCenterPosition(BusanDefaultCenter, DEFAULT_ZOOM_LEVEL))
    }

    LaunchedEffect(kakaoMap, pins) {
        val map = kakaoMap ?: return@LaunchedEffect
        renderPins(context, map, pins, onPinClick, fitCameraToPins, trackedLabels)
    }

    // 마커가 아닌 빈 지도를 눌렀을 때의 신호 — BrowseMap이 이걸로 선택을 해제한다
    // (setOnLabelClickListener는 마커를 눌렀을 때만 불리고, 빈 곳을 누르면 이쪽만 불린다).
    LaunchedEffect(kakaoMap, onMapClick) {
        val map = kakaoMap ?: return@LaunchedEffect
        map.setOnMapClickListener { _, _, _, _ -> onMapClick() }
    }

    LaunchedEffect(kakaoMap, recenterRequestId) {
        val map = kakaoMap ?: return@LaunchedEffect
        if (recenterRequestId == 0) return@LaunchedEffect
        map.moveCamera(CameraUpdateFactory.newCenterPosition(BusanDefaultCenter, DEFAULT_ZOOM_LEVEL))
    }

    LaunchedEffect(kakaoMap, searchAreaRequestId) {
        val map = kakaoMap ?: return@LaunchedEffect
        if (searchAreaRequestId == 0) return@LaunchedEffect
        val center = map.cameraPosition?.position ?: return@LaunchedEffect
        onSearchArea(center.latitude, center.longitude)
    }
}

@Composable
private fun MapUnavailableFallback(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color(0xFFE9E9EE)).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = KakaoMapAvailability.unavailableReason,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

private data class TrackedPinLabel(val label: Label, val pin: MapPin)

// 라벨 아이콘 전환에 쓰는 트랜지션. entrance/exit(라벨 추가/제거 시 애니메이션)는 그대로 두고
// enableTransitionWhenChange만 켜서, Label.changeStyles() 호출(마커 선택/해제) 때만 자연스럽게
// 스케일 인/아웃되게 한다 — 카테고리 전환처럼 라벨이 통째로 추가/삭제될 때는 애니메이션 없이 즉시 반영된다.
private val PinSelectTransition: LabelTransition =
    LabelTransition.from(Transition.Scale, Transition.Scale).enableTransitionWhenChange(true, true)

private fun renderPins(
    context: Context,
    map: KakaoMap,
    pins: List<MapPin>,
    onPinClick: (String) -> Unit,
    fitCameraToPins: Boolean,
    tracked: MutableMap<String, TrackedPinLabel>
) {
    val layer = map.labelManager?.layer ?: return

    if (pins.isEmpty()) {
        layer.removeAll()
        tracked.clear()
        if (fitCameraToPins) {
            map.moveCamera(CameraUpdateFactory.newCenterPosition(BusanDefaultCenter, DEFAULT_ZOOM_LEVEL))
        }
        return
    }

    // 화면에서 사라진 핀(카테고리 전환 등)만 제거한다 — 그대로 남아있는 핀의 라벨은 건드리지 않는다.
    val incomingIds = pins.mapTo(HashSet()) { it.id }
    val staleIds = tracked.keys - incomingIds
    if (staleIds.isNotEmpty()) {
        layer.remove(*staleIds.mapNotNull { tracked[it]?.label }.toTypedArray())
        staleIds.forEach { tracked.remove(it) }
    }

    // 새로 등장한 핀만 addLabels로 생성한다.
    val newPins = pins.filter { it.id !in tracked }
    if (newPins.isNotEmpty()) {
        val options = newPins.map { pin ->
            LabelOptions.from(pin.id, LatLng.from(pin.latitude, pin.longitude)).setStyles(pin.toLabelStyle(context))
        }
        val created = layer.addLabels(options)
        newPins.forEachIndexed { index, pin ->
            created.getOrNull(index)?.let { tracked[pin.id] = TrackedPinLabel(it, pin) }
        }
    }

    map.setOnLabelClickListener { _, _, label ->
        onPinClick(label.labelId)
        true
    }

    // 이미 떠 있던 핀 중 선택 상태/타입이 바뀐 라벨만 애니메이션과 함께 스타일을 교체한다.
    pins.forEach { pin ->
        val entry = tracked[pin.id] ?: return@forEach
        if (entry.pin.selected != pin.selected || entry.pin.type != pin.type) {
            entry.label.changeStyles(LabelStyles.from(pin.toLabelStyle(context)), true)
            tracked[pin.id] = entry.copy(pin = pin)
        }
    }

    if (!fitCameraToPins) return

    if (pins.size == 1) {
        map.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(pins[0].latitude, pins[0].longitude), SINGLE_PIN_ZOOM_LEVEL))
    } else {
        val points = pins.map { LatLng.from(it.latitude, it.longitude) }.toTypedArray()
        map.moveCamera(CameraUpdateFactory.fitMapPoints(points, FIT_PADDING_PX))
    }
}

private fun MapPin.iconRes(): Int = when (type) {
    MapPinType.HOSPITAL -> if (selected) R.drawable.ic_map_pin_hospital_selected else R.drawable.ic_map_pin_hospital
    MapPinType.TOURIST -> if (selected) R.drawable.ic_map_pin_tourist_selected else R.drawable.ic_map_pin_tourist
    MapPinType.FOOD -> if (selected) R.drawable.ic_map_pin_food_selected else R.drawable.ic_map_pin_food
}

private fun MapPin.toLabelStyle(context: Context): LabelStyle =
    LabelStyle.from(context.pinIconBitmap(iconRes())).setIconTransition(PinSelectTransition)

// LabelStyle.from(Context, Int)는 내부적으로 BitmapFactory.decodeResource()를 쓰는 것으로 보이는데,
// 이 API는 래스터 이미지(PNG/WebP)만 디코딩하고 우리 핀 아이콘 같은 VectorDrawable(XML)에는 null을
// 반환한다 — 그 결과 K3fAApi가 "ImageAsset is invalid"를 찍으며 라벨은 추가되지만 아이콘 없이 안 보인다.
// VectorDrawable을 직접 Bitmap으로 래스터화해 LabelStyle.from(Bitmap)에 넘기면 정상 동작한다.
// 아이콘 종류가 3개뿐이라 리소스 ID 기준으로 캐싱해 재렌더링마다 다시 그리지 않게 한다.
private val pinIconBitmapCache = mutableMapOf<Int, Bitmap>()

private fun Context.pinIconBitmap(@DrawableRes resId: Int): Bitmap =
    pinIconBitmapCache.getOrPut(resId) {
        val drawable = requireNotNull(ContextCompat.getDrawable(this, resId)) { "drawable not found: $resId" }
        val width = drawable.intrinsicWidth.coerceAtLeast(1)
        val height = drawable.intrinsicHeight.coerceAtLeast(1)
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

private const val DEFAULT_ZOOM_LEVEL = 12
private const val SINGLE_PIN_ZOOM_LEVEL = 16
private const val FIT_PADDING_PX = 140
