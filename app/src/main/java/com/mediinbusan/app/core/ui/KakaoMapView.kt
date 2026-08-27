package com.mediinbusan.app.core.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
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
import com.kakao.vectormap.GestureType
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
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLinePattern
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet
import com.mediinbusan.app.R
import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.designsystem.TextSecondary

enum class MapPinType { HOSPITAL, TOURIST, FOOD }

data class MapPin(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val type: MapPinType,
    val selected: Boolean = false,
    val sequenceNumber: Int? = null
)

data class MapRoutePoint(val latitude: Double, val longitude: Double)

data class MapRoutePath(
    val id: String,
    val points: List<MapRoutePoint>,
    val color: Int = 0xFFFF6F61.toInt()
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
 *
 * [routeStops]가 2개 이상이면 웰니스 코스 동선(F-014)의 방문 순서를 화살표 패턴이 반복되는 경로선으로
 * 그린다(카카오맵 RouteLine API, [renderRoute] 참고). 좌표를 그대로 직선으로 이은 것이라 실제
 * 도로/보행로를 정확히 따라가지는 않는다 — 다만 이건 실제 길찾기 경로 대신 "이 순서로 이동한다"는
 * 방향성만 우리 지도 안에서 보여주려는 의도적 선택이다(외부 카카오맵 앱으로 내보내는 길찾기 연동은
 * 쓰지 않는다). [MapPin.sequenceNumber] 번호 배지와 함께 방문 순서를 이중으로 안내한다.
 */
@Composable
fun KakaoMapView(
    pins: List<MapPin>,
    routePaths: List<MapRoutePath> = emptyList(),
    modifier: Modifier = Modifier,
    onPinClick: (String) -> Unit = {},
    onMapClick: () -> Unit = {},
    recenterRequestId: Int = 0,
    searchAreaRequestId: Int = 0,
    onSearchArea: (latitude: Double, longitude: Double) -> Unit = { _, _ -> },
    fitCameraToPins: Boolean = true,
    // 웰니스 코스 동선(F-014)의 방문 순서. 2개 미만이면 아무것도 그리지 않는다 — 기존 호출부
    // (HospitalDetail 단일 핀, MapScreen의 병원/장소 브라우징)는 전부 빈 목록 기본값을 그대로 쓰므로
    // 기존 동작에 영향이 없다.
    routeStops: List<RouteStop> = emptyList(),
    // false면 팬/줌/회전/틸트 등 모든 카메라 제스처를 비활성화한다. 상세 화면 안에 작게 박힌
    // "미리보기" 지도(PlaceDetailScreen/HospitalDetailScreen의 LocationMiniMap처럼 탭하면 다른
    // 동작(길찾기/지도 화면 이동)으로 이어지는 지도)에서 쓴다 — 그런 자리에서까지 기본 팬/핀치
    // 제스처가 살아있으면 사용자가 실수로 카메라를 옮겨버릴 수 있다(코드리뷰 지적).
    interactive: Boolean = true
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
    val mapView = remember {
        MapView(context).apply {
            setOnTouchListener { view, event ->
                val isTouchingMap = event.actionMasked != MotionEvent.ACTION_UP &&
                    event.actionMasked != MotionEvent.ACTION_CANCEL
                view.parent?.requestDisallowInterceptTouchEvent(isTouchingMap)
                false
            }
        }
    }
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    // onMapReady는 KakaoMap 객체가 준비됐다는 뜻이지 타일/마커가 화면에 그려졌다는 뜻이 아니다 —
    // 그 사이에도 실제 지도 이미지는 비동기로 계속 내려받아진다(KakaoMapAvailability 문서의 로딩
    // 스피너 설명 참고). 예전엔 kakaoMap != null 하나로 스피너를 끄고 AndroidView를 곧장 보여줘서,
    // 그 찰나에 타일 없는 빈 서페이스가 스피너 대신 노출될 수 있었다(코드리뷰 지적). 이 SDK 공개
    // API에는 "타일 로딩 완료" 콜백이 없어(KakaoMap/MapView 공개 메서드 전수 확인) 완벽한 신호는
    // 못 쓰지만, onMapReady 직후 항상 한 번 실행되는 초기 moveCamera가 끝나는 시점
    // (setOnCameraMoveEndListener)을 그 대체 신호로 삼는다 — onMapReady 단독보다는 실제 렌더링
    // 시점에 더 가깝다.
    var isMapVisuallyReady by remember { mutableStateOf(false) }
    var mapErrorMessage by remember { mutableStateOf<String?>(null) }
    // pinId -> 현재 그 자리에 떠 있는 Label과, 그 Label이 마지막으로 반영한 MapPin 상태.
    // 선택 여부만 바뀐 마커를 매번 layer.removeAll()+addLabels()로 다시 그리면 관련 없는 다른
    // 라벨까지 전부 깜빡여 부자연스럽다 — 대신 바뀐 라벨만 Label.changeStyles(..., animate=true)로
    // 애니메이션과 함께 스타일만 갈아끼운다.
    val trackedLabels = remember { mutableMapOf<String, TrackedPinLabel>() }
    // 코스 동선 화살표 경로선(RouteLine)은 라벨과 달리 매번 새로 그려도 저렴하고(경유지 몇 개짜리
    // 선 하나) 부분 갱신할 이유가 없어, 라벨처럼 diff하지 않고 이전 것을 지우고 다시 그린다.
    var trackedRouteLine by remember { mutableStateOf<RouteLine?>(null) }

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
            mapView.setOnTouchListener(null)
            mapView.finish()
        }
    }

    // 실기기 화면 녹화로 원인을 다시 확인한 결과, "화면이 흑백으로 보였다가 지도가 뜬다"는 현상은
    // 렌더링 버그가 아니라 카카오 지도 SDK가 onMapReady 이후 실제 타일/마커 데이터를 네트워크로
    // 내려받는 동안 아무 안내 없이 무채색 배경만 잠깐 노출되는 것이었다(진입 순간엔 화면 전체가
    // 이 배경으로 덮여 있어 유채색 대비가 커서 더 도드라져 보인다). SDK 내부 로딩이라 우리 쪽에서
    // 더 앞당길 수는 없으므로, 그 구간에 스피너를 띄워 "정상적으로 불러오는 중"이라는 걸 명확히
    // 알려준다 — View.INVISIBLE로 지도 서페이스를 가려두는 것도 유지해 그 구간 동안 준비 안 된
    // 프레임이 새어 나오지 않게 한다.
    Box(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize().background(MapPlaceholderBackground)) {
            if (!isMapVisuallyReady) {
                LoadingState(modifier = Modifier.fillMaxSize())
            }
        }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.apply {
                    visibility = android.view.View.INVISIBLE
                    start(
                        object : MapLifeCycleCallback() {
                            override fun onMapDestroy() = Unit

                            override fun onMapError(error: Exception) {
                                android.util.Log.e("KakaoMapView", "Kakao 지도 렌더링 실패", error)
                                mapErrorMessage = if (error.message?.contains("401") == true) {
                                    "Kakao 지도 인증에 실패했습니다.\n패키지명과 디버그 키 해시 등록을 확인해 주세요."
                                } else {
                                    "Kakao 지도를 불러오지 못했습니다.\n네트워크 연결과 앱 키 설정을 확인해 주세요."
                                }
                            }
                        },
                        object : KakaoMapReadyCallback() {
                            override fun onMapReady(map: KakaoMap) {
                                mapErrorMessage = null
                                kakaoMap = map
                            }
                        }
                    )
                }
            },
            update = { view ->
                view.visibility = if (isMapVisuallyReady) android.view.View.VISIBLE else android.view.View.INVISIBLE
            }
        )
        mapErrorMessage?.let { message ->
            MapUnavailableFallback(modifier = Modifier.fillMaxSize(), message = message)
        }
    }

    // 지도가 처음 준비됐을 때 서면으로 카메라를 맞춰둔다. fitCameraToPins=true인 화면(예: 병원 상세의
    // "지도에서 보기")은 바로 아래 LaunchedEffect(kakaoMap, pins)가 pins 도착 즉시 알맞은 위치로
    // 다시 옮기므로 여기서는 초기 프레임이 빈 지도로 안 보이게 하는 역할만 한다.
    LaunchedEffect(kakaoMap) {
        val map = kakaoMap ?: return@LaunchedEffect
        map.moveCamera(CameraUpdateFactory.newCenterPosition(BusanDefaultCenter, DEFAULT_ZOOM_LEVEL))
    }

    // isMapVisuallyReady 문서 참고 — 이 초기 moveCamera가 끝나는 시점을 스피너를 끄는 신호로 쓴다.
    // 한 번 true가 되면 이후 recenter 등으로 다시 불려도(값 그대로 true) 상관없다.
    LaunchedEffect(kakaoMap) {
        val map = kakaoMap ?: return@LaunchedEffect
        map.setOnCameraMoveEndListener { _, _, _ -> isMapVisuallyReady = true }
    }

    // interactive=false(미니 미리보기 지도)면 팬/줌/회전/틸트 등 카메라를 움직이는 제스처를 전부
    // 막는다. MapView.setOnTouchListener(위 remember 블록)는 이벤트를 소비하지 않고 그대로
    // 흘려보내 SDK 기본 제스처가 계속 동작했다 — 여기서 SDK 쪽 제스처 자체를 꺼서 막는다.
    LaunchedEffect(kakaoMap, interactive) {
        val map = kakaoMap ?: return@LaunchedEffect
        // GestureType은 Kotlin enum이 아니라 SDK가 내려주는 Java enum이라 .entries 대신
        // .values()를 쓴다.
        GestureType.values().forEach { gesture -> map.setGestureEnable(gesture, interactive) }
    }

    LaunchedEffect(kakaoMap, pins) {
        val map = kakaoMap ?: return@LaunchedEffect
        renderPins(context, map, pins, onPinClick, fitCameraToPins, trackedLabels)
    }

    LaunchedEffect(kakaoMap, routePaths) {
        val map = kakaoMap ?: return@LaunchedEffect
        renderRoutePaths(map, routePaths)
    }

    LaunchedEffect(kakaoMap, routeStops) {
        val map = kakaoMap ?: return@LaunchedEffect
        trackedRouteLine = renderRoute(context, map, routeStops, trackedRouteLine)
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

// MapUnavailableFallback의 회색(0xFFE9E9EE)과는 별개로, 지도가 준비되기 전 짧게 노출되는
// 중립색이다 — 같은 색을 쓰면 "정상 로딩 중"과 "지도를 아예 못 씀" 상태가 시각적으로
// 구분되지 않는다.
private val MapPlaceholderBackground = Color(0xFFF4F4F6)

@Composable
private fun MapUnavailableFallback(
    modifier: Modifier = Modifier,
    message: String = KakaoMapAvailability.unavailableReason
) {
    Box(
        modifier = modifier.fillMaxSize().background(Color(0xFFE9E9EE)).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
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
        if (
            entry.pin.selected != pin.selected ||
            entry.pin.type != pin.type ||
            entry.pin.sequenceNumber != pin.sequenceNumber
        ) {
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

// 웰니스 코스 동선(F-014)의 방문 순서를 화살표 패턴이 반복되는 경로선으로 그린다. stops를 순서대로
// 이은 직선일 뿐 실제 도로/보행로 좌표가 아니다 — 정확한 길찾기 경로가 아니라 "이 순서로 이동한다"는
// 방향성 표시가 목적이므로(KakaoMapView 문서 참고) 좌표 몇 개를 직선으로 잇는 것으로 충분하다.
// stops가 바뀔 때마다 previous를 지우고 새로 그린다(라벨과 달리 diff할 이유가 없다 — renderPins 주석 참고).
private fun renderRoute(context: Context, map: KakaoMap, stops: List<RouteStop>, previous: RouteLine?): RouteLine? {
    val layer = map.routeLineManager?.layer
    previous?.let { layer?.remove(it) }
    if (layer == null || stops.size < 2) return null

    val points = stops.map { LatLng.from(it.latitude, it.longitude) }
    val pattern = RouteLinePattern.from(context.routeArrowBitmap(), ROUTE_ARROW_PATTERN_DISTANCE_PX)
    val style = RouteLineStyle.from(ROUTE_LINE_WIDTH_PX, ROUTE_LINE_COLOR, pattern)
    val stylesSet = RouteLineStylesSet.from(RouteLineStyles.from(style))
    val segment = RouteLineSegment.from(points, stylesSet.getStyles(0))
    val options = RouteLineOptions.from(segment).setStylesSet(stylesSet)
    return layer.addRouteLine(options)
}

// ic_route_arrow도 pinIconBitmap과 같은 이유로(VectorDrawable은 LabelStyle/RouteLinePattern의
// BitmapFactory 기반 리소스ID 오버로드에서 아이콘 없이 비어 보인다) Bitmap으로 직접 래스터화해서
// 넘긴다. 아이콘 자체는 컨텍스트와 무관하게 항상 같은 모양이라 라벨 아이콘과 달리 단일 캐시 하나면 된다.
private var routeArrowBitmapCache: Bitmap? = null

private fun Context.routeArrowBitmap(): Bitmap = routeArrowBitmapCache ?: run {
    val drawable = requireNotNull(ContextCompat.getDrawable(this, R.drawable.ic_route_arrow)) { "drawable not found: ic_route_arrow" }
    val width = drawable.intrinsicWidth.coerceAtLeast(1)
    val height = drawable.intrinsicHeight.coerceAtLeast(1)
    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
    }.also { routeArrowBitmapCache = it }
}

private fun MapPin.iconRes(): Int = when (type) {
    MapPinType.HOSPITAL -> if (selected) R.drawable.ic_map_pin_hospital_selected else R.drawable.ic_map_pin_hospital
    MapPinType.TOURIST -> if (selected) R.drawable.ic_map_pin_tourist_selected else R.drawable.ic_map_pin_tourist
    MapPinType.FOOD -> if (selected) R.drawable.ic_map_pin_food_selected else R.drawable.ic_map_pin_food
}

private fun MapPin.toLabelStyle(context: Context): LabelStyle =
    LabelStyle.from(
        sequenceNumber?.let { context.numberedPinBitmap(it, selected) } ?: context.pinIconBitmap(iconRes())
    ).setIconTransition(PinSelectTransition)

private fun renderRoutePaths(map: KakaoMap, paths: List<MapRoutePath>) {
    val manager = map.routeLineManager ?: return
    if (paths.isEmpty()) {
        manager.getLayer(COURSE_ROUTE_LAYER_ID)?.removeAll()
        return
    }
    val layer = manager.getLayer(COURSE_ROUTE_LAYER_ID)
        ?: manager.addLayer(COURSE_ROUTE_LAYER_ID, COURSE_ROUTE_Z_ORDER)
    layer.removeAll()
    paths.forEach { path ->
        if (path.points.size < 2) return@forEach
        val points = path.points.map { LatLng.from(it.latitude, it.longitude) }
        val style = RouteLineStyle.from(
            COURSE_LINE_WIDTH_PX,
            path.color,
            COURSE_LINE_STROKE_WIDTH_PX,
            android.graphics.Color.WHITE
        )
        val segment = RouteLineSegment.from(points, style)
        layer.addRouteLine(RouteLineOptions.from(path.id, segment))
    }
}

// LabelStyle.from(Context, Int)는 내부적으로 BitmapFactory.decodeResource()를 쓰는 것으로 보이는데,
// 이 API는 래스터 이미지(PNG/WebP)만 디코딩하고 우리 핀 아이콘 같은 VectorDrawable(XML)에는 null을
// 반환한다 — 그 결과 K3fAApi가 "ImageAsset is invalid"를 찍으며 라벨은 추가되지만 아이콘 없이 안 보인다.
// VectorDrawable을 직접 Bitmap으로 래스터화해 LabelStyle.from(Bitmap)에 넘기면 정상 동작한다.
// 아이콘 종류가 3개뿐이라 리소스 ID 기준으로 캐싱해 재렌더링마다 다시 그리지 않게 한다.
private val pinIconBitmapCache = mutableMapOf<Int, Bitmap>()
private val numberedPinBitmapCache = mutableMapOf<Pair<Int, Boolean>, Bitmap>()

private fun Context.pinIconBitmap(@DrawableRes resId: Int): Bitmap =
    pinIconBitmapCache.getOrPut(resId) {
        val drawable = requireNotNull(ContextCompat.getDrawable(this, resId)) { "drawable not found: $resId" }
        // 이전에는 리소스가 선언한 실제 크기(ic_map_pin_*_selected.xml은 34dp로 선택 시 더 크게
        // 보이도록 디자인됐고, 그 외는 24dp)를 무시하고 항상 32dp 고정 캔버스로 래스터화했다 —
        // setBounds()가 캔버스 크기를 그대로 그리기 영역으로 쓰기 때문에 리소스 고유의
        // android:width/height는 사실상 무시됐다. 그 결과 마커를 선택해도 아이콘이 커지지
        // 않았다. intrinsicWidth/Height(리소스에 선언된 dp가 이미 기기 density로 환산된 값)를
        // 그대로 캔버스 크기로 써서 선택 시 실제로 더 크게 그려지게 한다.
        val width = drawable.intrinsicWidth.coerceAtLeast(1)
        val height = drawable.intrinsicHeight.coerceAtLeast(1)
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

private fun Context.numberedPinBitmap(number: Int, selected: Boolean): Bitmap =
    numberedPinBitmapCache.getOrPut(number to selected) {
        val density = resources.displayMetrics.density
        val size = (if (selected) 32 else 28) * density
        val bitmapSize = size.toInt().coerceAtLeast(1)
        Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            val center = bitmapSize / 2f
            val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (selected) 0xFFE9564F.toInt() else 0xFFFF6F61.toInt()
                style = Paint.Style.FILL
            }
            val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                style = Paint.Style.STROKE
                strokeWidth = 2f * density
            }
            canvas.drawCircle(center, center, center - border.strokeWidth, fill)
            canvas.drawCircle(center, center, center - border.strokeWidth, border)
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = 11f * density
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val baseline = center - (textPaint.ascent() + textPaint.descent()) / 2f
            canvas.drawText(number.toString(), center, baseline, textPaint)
        }
    }

private const val DEFAULT_ZOOM_LEVEL = 12
private const val SINGLE_PIN_ZOOM_LEVEL = 16
private const val FIT_PADDING_PX = 140
private const val ROUTE_ARROW_PATTERN_DISTANCE_PX = 48f
private const val ROUTE_LINE_WIDTH_PX = 10f
private const val ROUTE_LINE_COLOR = 0xFFFF6F61.toInt()
private const val COURSE_ROUTE_LAYER_ID = "recommended-tourism-course"
private const val COURSE_ROUTE_Z_ORDER = 20_000
private const val COURSE_LINE_WIDTH_PX = 5f
private const val COURSE_LINE_STROKE_WIDTH_PX = 1.5f
