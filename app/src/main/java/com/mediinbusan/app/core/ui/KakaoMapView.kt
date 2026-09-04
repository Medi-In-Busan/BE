package com.mediinbusan.app.core.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
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
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import kotlin.math.floor
import kotlin.math.pow

enum class MapPinType { HOSPITAL, TOURIST, FOOD }

// 코스 동선(RecommendedCourseScreen)의 첫/마지막 정거장 전용 마커 — 번호 배지 대신 출발·도착
// 전용 이미지(cource_detail_start/end)로 표시한다.
enum class RouteEndpointKind { START, END }

data class MapPin(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val type: MapPinType,
    val selected: Boolean = false,
    val sequenceNumber: Int? = null,
    val endpointKind: RouteEndpointKind? = null,
    // clusterPins=true인 화면에서 여러 핀이 한 셀로 묶였을 때의 개수. null이면 낱개 핀이다.
    // 이 값이 있으면 아이콘 대신 개수를 쓴 원형 배지로 그리고(clusterPinBitmap), 누르면 확대된다.
    val clusterCount: Int? = null
)

data class MapRoutePoint(val latitude: Double, val longitude: Double)

data class MapRoutePath(
    val id: String,
    val points: List<MapRoutePoint>,
    val color: Int = CoralRouteColor
)

/**
 * 지도를 좌표 없이 열 때 쓰는 화면 기본 중심점 — 부산광역시 부산진구 가야대로 767(부전동,
 * 정근안과병원). 시드 데이터(V2__seed_hospitals.sql / hospitals.json의 regNo 111)에 있는 그 병원의
 * 좌표를 그대로 쓴다. 시드 병원 대다수가 이 서면 일대에 몰려 있어 첫 화면 기준점으로 적합하다.
 * 사용자의 실제 위치가 아니다 — 이 앱은 위치 권한을 요청하지 않는다(CLAUDE.md §1 참고).
 *
 * core/common/GeoDistance.kt의 DefaultSearchOrigin(검색 결과 "가까운순" 정렬 기준점)과는 이제
 * 400m쯤 떨어진 다른 좌표다 — 정렬 기준까지 같이 흔들지 않으려고 일부러 분리했다.
 */
val BusanDefaultCenter: LatLng = LatLng.from(35.158010742858025, 129.05550558993514)

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
    zoomInRequestId: Int = 0,
    zoomOutRequestId: Int = 0,
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
    interactive: Boolean = true,
    onMapInteractionChange: (Boolean) -> Unit = {},
    // true면 배율이 낮아 핀이 서로 겹칠 때 가까운 핀들을 하나의 "개수 배지" 마커로 묶는다
    // (지도 브라우징 화면 전용 — 핀이 몇 개뿐인 상세/코스 지도는 기본값 false 그대로 둔다).
    clusterPins: Boolean = false,
    // 카메라가 멈출 때마다(그리고 최초 위치 이동 직후) 화면 중심 좌표를 알려준다 — 하단 목록을
    // "지금 보고 있는 지점"에서 가까운 순으로 정렬하는 데 쓴다.
    onCameraMove: (latitude: Double, longitude: Double) -> Unit = { _, _ -> }
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
    val currentInteractive by rememberUpdatedState(interactive)
    val currentOnMapInteractionChange by rememberUpdatedState(onMapInteractionChange)
    val mapView = remember {
        MapView(context).apply {
            setOnTouchListener { view, event ->
                val isTouchingMap = currentInteractive &&
                    event.actionMasked != MotionEvent.ACTION_UP &&
                    event.actionMasked != MotionEvent.ACTION_CANCEL
                view.parent?.requestDisallowInterceptTouchEvent(isTouchingMap)
                currentOnMapInteractionChange(isTouchingMap)
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
    // 현재 카메라 배율. 클러스터링은 "지금 얼마나 축소돼 있는지"에 따라 묶는 범위가 달라져서
    // 이 값이 바뀔 때마다 다시 계산해야 한다. 카메라가 멈출 때마다 갱신된다.
    var zoomLevel by remember { mutableIntStateOf(DEFAULT_ZOOM_LEVEL) }
    // 콜백은 매 recomposition마다 새 람다라 LaunchedEffect 키로 쓰면 리스너를 계속 다시 등록하게
    // 된다 — 최신 값만 참조하도록 감싸둔다(currentOnMapInteractionChange와 같은 이유).
    val currentOnCameraMove by rememberUpdatedState(onCameraMove)
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
            currentOnMapInteractionChange(false)
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
    Box(
        modifier = modifier.pointerInput(currentInteractive) {
            if (!currentInteractive) return@pointerInput
            awaitEachGesture {
                awaitFirstDown(
                    requireUnconsumed = false,
                    pass = PointerEventPass.Initial
                )
                currentOnMapInteractionChange(true)
                try {
                    do {
                        val event = awaitPointerEvent(PointerEventPass.Final)
                    } while (event.changes.any { it.pressed })
                } finally {
                    currentOnMapInteractionChange(false)
                }
            }
        }
    ) {
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
                                listOf(
                                    GestureType.Pan,
                                    GestureType.Zoom,
                                    GestureType.RotateZoom,
                                    GestureType.OneFingerDoubleTap,
                                    GestureType.TwoFingerSingleTap,
                                    GestureType.OneFingerZoom
                                ).forEach { gesture -> map.setGestureEnable(gesture, true) }
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
    // 같은 리스너에서 배율(클러스터링 기준)과 화면 중심(목록 거리순 정렬 기준)도 같이 갱신한다 —
    // 리스너는 하나만 등록할 수 있어서 세 가지 용도를 여기 모은다.
    LaunchedEffect(kakaoMap) {
        val map = kakaoMap ?: return@LaunchedEffect
        map.setOnCameraMoveEndListener { _, cameraPosition, _ ->
            isMapVisuallyReady = true
            zoomLevel = cameraPosition.zoomLevel
            currentOnCameraMove(cameraPosition.position.latitude, cameraPosition.position.longitude)
        }
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

    // clusterPins=true면 배율이 낮을 때 겹치는 핀들을 묶어서 그린다 — 배율이 바뀔 때마다 묶음이
    // 달라지므로 zoomLevel도 키에 넣는다(false면 zoomLevel이 바뀌어도 다시 그리지 않는다).
    val renderedPins = remember(pins, clusterPins, zoomLevel) {
        if (clusterPins) clusterPins(pins, zoomLevel) else pins
    }
    LaunchedEffect(kakaoMap, renderedPins) {
        val map = kakaoMap ?: return@LaunchedEffect
        renderPins(context, map, renderedPins, onPinClick, fitCameraToPins, trackedLabels)
    }

    LaunchedEffect(kakaoMap, routePaths) {
        val map = kakaoMap ?: return@LaunchedEffect
        renderRoutePaths(context, map, routePaths)
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

    LaunchedEffect(kakaoMap, zoomInRequestId) {
        val map = kakaoMap ?: return@LaunchedEffect
        if (zoomInRequestId == 0) return@LaunchedEffect
        map.moveCamera(CameraUpdateFactory.zoomIn())
    }

    LaunchedEffect(kakaoMap, zoomOutRequestId) {
        val map = kakaoMap ?: return@LaunchedEffect
        if (zoomOutRequestId == 0) return@LaunchedEffect
        map.moveCamera(CameraUpdateFactory.zoomOut())
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

    map.setOnLabelClickListener { clickedMap, _, label ->
        // 묶음 마커는 상세로 보낼 대상이 하나로 정해지지 않는다 — 대신 그 자리를 두 단계 확대해서
        // 묶음이 풀리게 한다(지도 앱들의 일반적인 클러스터 동작).
        if (label.labelId.startsWith(CLUSTER_ID_PREFIX)) {
            val target = label.position
            val nextZoom = ((clickedMap.cameraPosition?.zoomLevel ?: DEFAULT_ZOOM_LEVEL) + CLUSTER_ZOOM_IN_STEP)
                .coerceAtMost(MAX_ZOOM_LEVEL)
            clickedMap.moveCamera(CameraUpdateFactory.newCenterPosition(target, nextZoom))
        } else {
            onPinClick(label.labelId)
        }
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

// 코스 경로선(renderRoutePaths) 전용 축소 화살촉 — ic_route_arrow_course.xml 참고. routeStops용
// routeArrowBitmap()과는 별도 캐시로 둬서 서로의 크기에 영향을 주지 않는다.
private var courseRouteArrowBitmapCache: Bitmap? = null

private fun Context.courseRouteArrowBitmap(): Bitmap = courseRouteArrowBitmapCache ?: run {
    val drawable = requireNotNull(ContextCompat.getDrawable(this, R.drawable.ic_route_arrow_course)) { "drawable not found: ic_route_arrow_course" }
    val width = drawable.intrinsicWidth.coerceAtLeast(1)
    val height = drawable.intrinsicHeight.coerceAtLeast(1)
    Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
    }.also { courseRouteArrowBitmapCache = it }
}

// 선택/비선택 전용 애셋이 따로 없는 단일 아이콘(map_hospitalmaker/map_travelmaker/map_foodmaker)이라,
// 선택 여부는 아이콘을 바꾸는 게 아니라 pinIconBitmap()에서 크기만 다르게 그려서 표현한다.
private fun MapPin.iconRes(): Int = when (type) {
    MapPinType.HOSPITAL -> R.drawable.map_hospitalmaker
    MapPinType.TOURIST -> R.drawable.map_travelmaker
    MapPinType.FOOD -> R.drawable.map_foodmaker
}

private fun MapPin.toLabelStyle(context: Context): LabelStyle =
    LabelStyle.from(
        when {
            endpointKind == RouteEndpointKind.START -> context.routeEndpointBitmap(R.drawable.cource_detail_start)
            endpointKind == RouteEndpointKind.END -> context.routeEndpointBitmap(R.drawable.cource_detail_end)
            clusterCount != null -> context.clusterPinBitmap(clusterCount, type)
            sequenceNumber != null -> context.numberedPinBitmap(sequenceNumber, selected)
            else -> context.pinIconBitmap(iconRes(), selected)
        }
    ).setIconTransition(PinSelectTransition)

/**
 * 배율이 낮아 핀이 서로 겹칠 때 가까운 핀들을 하나의 개수 배지 마커로 묶는다.
 *
 * 화면 픽셀 기준으로 일정 크기(약 [CLUSTER_CELL_TILE_RATIO] × 256px)가 되는 위경도 격자를 만들어
 * 같은 칸에 들어간 핀을 한 묶음으로 본다. 배율이 [CLUSTER_MIN_ZOOM_LEVEL] 이상이면(충분히 확대돼
 * 겹치지 않으면) 원본을 그대로 돌려준다. 선택된 핀이 들어있는 칸도 묶지 않는다 — 선택한 마커는
 * 항상 낱개로 보여야 하단 카드와 짝이 맞는다.
 */
private fun clusterPins(pins: List<MapPin>, zoomLevel: Int): List<MapPin> {
    if (zoomLevel >= CLUSTER_MIN_ZOOM_LEVEL || pins.size < 2) return pins
    val cellSize = 360.0 / 2.0.pow(zoomLevel.toDouble()) * CLUSTER_CELL_TILE_RATIO
    if (cellSize <= 0.0) return pins
    return pins
        .groupBy { pin -> floor(pin.latitude / cellSize).toInt() to floor(pin.longitude / cellSize).toInt() }
        .flatMap { (cell, group) ->
            if (group.size < 2 || group.any { it.selected }) {
                group
            } else {
                val (cellY, cellX) = cell
                // "전체" 탭에서는 한 칸에 병원·관광·음식이 섞일 수 있다 — 가장 많은 종류의 색을
                // 따라가서, 묶음이 그 동네에서 주로 뭐가 모여 있는지를 색으로 먼저 알려주게 한다.
                val dominantType = group.groupingBy { it.type }.eachCount().maxBy { it.value }.key
                listOf(
                    MapPin(
                        // 개수·종류까지 id에 넣어야 묶음이 바뀔 때 renderPins의 diff가 새 라벨로 인식한다.
                        id = "$CLUSTER_ID_PREFIX$cellY:$cellX:${group.size}:$dominantType",
                        latitude = group.sumOf { it.latitude } / group.size,
                        longitude = group.sumOf { it.longitude } / group.size,
                        type = dominantType,
                        clusterCount = group.size
                    )
                )
            }
        }
}

// RouteLineStyle 자체는 선-두께 + 테두리 한 겹만 지원해서, 노드(numberedPinBitmap)처럼 "코랄
// 코어 + 흰 여백 + 회색 경계선" 3겹을 내려면 같은 좌표를 폭만 다르게 겹쳐 그려야 한다 — 가장 넓은
// 회색 선(경계선)을 맨 아래에, 그보다 좁은 흰 선(여백)을 가운데, 원래 두께의 코랄 선(+화살표 패턴)을
// 맨 위에 순서대로 쌓는다(zOrder로 순서 고정, 위층이 아래층 중앙을 덮어 테두리처럼 보이게 만든다).
private fun renderRoutePaths(context: Context, map: KakaoMap, paths: List<MapRoutePath>) {
    val manager = map.routeLineManager ?: return
    if (paths.isEmpty()) {
        manager.getLayer(COURSE_ROUTE_LAYER_ID)?.removeAll()
        return
    }
    val layer = manager.getLayer(COURSE_ROUTE_LAYER_ID)
        ?: manager.addLayer(COURSE_ROUTE_LAYER_ID, COURSE_ROUTE_Z_ORDER)
    layer.removeAll()
    val pattern = RouteLinePattern.from(context.courseRouteArrowBitmap(), COURSE_ROUTE_ARROW_PATTERN_DISTANCE_PX)
    val marginWidth = COURSE_LINE_WIDTH_PX + 2 * COURSE_LINE_MARGIN_WIDTH_PX
    val borderWidth = marginWidth + 2 * COURSE_LINE_BORDER_WIDTH_PX
    paths.forEach { path ->
        if (path.points.size < 2) return@forEach
        val points = path.points.map { LatLng.from(it.latitude, it.longitude) }

        val borderSegment = RouteLineSegment.from(points, RouteLineStyle.from(borderWidth, COURSE_LINE_BORDER_COLOR))
        layer.addRouteLine(RouteLineOptions.from("${path.id}-border", borderSegment).setZOrder(0))

        val marginSegment = RouteLineSegment.from(points, RouteLineStyle.from(marginWidth, android.graphics.Color.WHITE))
        layer.addRouteLine(RouteLineOptions.from("${path.id}-margin", marginSegment).setZOrder(1))

        val coreSegment = RouteLineSegment.from(points, RouteLineStyle.from(COURSE_LINE_WIDTH_PX, path.color, pattern))
        layer.addRouteLine(RouteLineOptions.from("${path.id}-core", coreSegment).setZOrder(2))
    }
}

// LabelStyle.from(Context, Int)는 내부적으로 BitmapFactory.decodeResource()를 쓰는 것으로 보이는데,
// 이 API는 래스터 이미지(PNG/WebP)만 디코딩하고 우리 핀 아이콘 같은 VectorDrawable(XML)에는 null을
// 반환한다 — 그 결과 K3fAApi가 "ImageAsset is invalid"를 찍으며 라벨은 추가되지만 아이콘 없이 안 보인다.
// VectorDrawable을 직접 Bitmap으로 래스터화해 LabelStyle.from(Bitmap)에 넘기면 정상 동작한다.
// 아이콘 종류가 3개뿐이라 리소스 ID 기준으로 캐싱해 재렌더링마다 다시 그리지 않게 한다.
private val pinIconBitmapCache = mutableMapOf<Pair<Int, Boolean>, Bitmap>()
private val numberedPinBitmapCache = mutableMapOf<Pair<Int, Boolean>, Bitmap>()

// 단일 아이콘 애셋을 선택 여부에 따라 24dp/34dp로 스케일링해서 그린다(비율은 원본 유지, 크기는 그대로).
private const val PIN_ICON_SIZE_DP = 24
private const val PIN_ICON_SIZE_SELECTED_DP = 34

private fun Context.pinIconBitmap(@DrawableRes resId: Int, selected: Boolean): Bitmap =
    pinIconBitmapCache.getOrPut(resId to selected) {
        val drawable = requireNotNull(ContextCompat.getDrawable(this, resId)) { "drawable not found: $resId" }
        val density = resources.displayMetrics.density
        val targetPx = ((if (selected) PIN_ICON_SIZE_SELECTED_DP else PIN_ICON_SIZE_DP) * density).toInt().coerceAtLeast(1)
        val intrinsicWidth = drawable.intrinsicWidth.coerceAtLeast(1)
        val intrinsicHeight = drawable.intrinsicHeight.coerceAtLeast(1)
        val scale = targetPx.toFloat() / maxOf(intrinsicWidth, intrinsicHeight)
        val width = (intrinsicWidth * scale).toInt().coerceAtLeast(1)
        val height = (intrinsicHeight * scale).toInt().coerceAtLeast(1)
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            // 아이콘 자체가 핀 모양(위쪽 원형 머리 + 아래쪽 꼬리)이라, 흰 배경 원은 꼬리까지 덮지
            // 않도록 이미지 최상단(원형 머리 = 가로 폭과 같은 지름)에만 맞춰 그린다(테두리 없음).
            val radius = width / 2f
            val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawCircle(width / 2f, radius, radius, circlePaint)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

// 경유지 번호 핀 — 코랄 코어 + 흰 여백 + 회색 경계선, 코스 경로선(renderRoutePaths)과 같은 3겹
// 구성을 원 마커에 그대로 옮긴 것이다(선은 폭을 겹쳐 쌓았지만, 원은 반지름을 줄여가며 채운 원을
// 겹쳐 그리는 것으로 같은 효과를 낸다).
private fun Context.numberedPinBitmap(number: Int, selected: Boolean): Bitmap =
    numberedPinBitmapCache.getOrPut(number to selected) {
        val density = resources.displayMetrics.density
        val size = (if (selected) 29 else 25) * density
        val bitmapSize = size.toInt().coerceAtLeast(1)
        val borderWidth = PIN_BORDER_WIDTH_DP * density
        val marginWidth = PIN_MARGIN_WIDTH_DP * density
        Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            val center = bitmapSize / 2f
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COURSE_LINE_BORDER_COLOR
                style = Paint.Style.FILL
            }
            val marginPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                style = Paint.Style.FILL
            }
            val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (selected) CoralRouteColorSelected else CoralRouteColor
                style = Paint.Style.FILL
            }
            canvas.drawCircle(center, center, center, borderPaint)
            canvas.drawCircle(center, center, center - borderWidth, marginPaint)
            canvas.drawCircle(center, center, center - borderWidth - marginWidth, corePaint)
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

/**
 * 묶음 마커. 낱개 핀 애셋(map_*maker.webp)이 "색 링 + 흰 속 + 색 글리프" 구성이라, 묶음도 같은
 * 문법으로 그린다 — 흰 속에 그 카테고리 색으로 개수를 쓰고 같은 색 링을 두른다. 색을 카테고리에서
 * 가져오는 게 핵심이다(예전엔 관광·음식 묶음까지 전부 코랄이라 파란/주황 핀들 사이에서 혼자 튀었다).
 *
 * 개수에 따라 지름이 세 단계로 커져서, 숫자를 읽기 전에 밀집도가 먼저 눈에 들어온다.
 */
private val clusterPinBitmapCache = mutableMapOf<Pair<Int, MapPinType>, Bitmap>()

private fun Context.clusterPinBitmap(count: Int, type: MapPinType): Bitmap =
    clusterPinBitmapCache.getOrPut(count to type) {
        val density = resources.displayMetrics.density
        val accent = type.clusterColor()
        // 100개가 넘어가면 "99+"로 줄여서 원이 계속 커지지 않게 한다.
        val label = if (count > CLUSTER_MAX_DISPLAY_COUNT) "$CLUSTER_MAX_DISPLAY_COUNT+" else count.toString()
        val diameterDp = when {
            count >= CLUSTER_LARGE_THRESHOLD -> CLUSTER_DIAMETER_LARGE_DP
            count >= CLUSTER_MEDIUM_THRESHOLD -> CLUSTER_DIAMETER_MEDIUM_DP
            else -> CLUSTER_DIAMETER_SMALL_DP
        } + (label.length - 1) * CLUSTER_SIZE_PER_EXTRA_CHAR_DP
        // 그림자와 바깥 링이 잘리지 않도록 비트맵을 지름보다 조금 크게 잡는다.
        val padding = CLUSTER_HALO_PADDING_DP * density
        val diameter = diameterDp * density
        val size = (diameter + padding * 2).toInt().coerceAtLeast(1)
        Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            val center = size / 2f
            val radius = diameter / 2f

            // 1) 같은 색 옅은 헤일로 — 지도 타일 위에서 묶음이 "번져 보이게" 해서 낱개 핀과 구분된다.
            canvas.drawCircle(
                center,
                center,
                radius + padding * 0.8f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = (accent and 0x00FFFFFF) or CLUSTER_HALO_ALPHA
                    style = Paint.Style.FILL
                }
            )
            // 2) 흰 원 + 부드러운 그림자 — 이 앱의 떠 있는 컨트롤(검색바/버튼)과 같은 톤.
            canvas.drawCircle(
                center,
                center,
                radius,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.WHITE
                    style = Paint.Style.FILL
                    setShadowLayer(3f * density, 0f, 1.5f * density, 0x40000000)
                }
            )
            // 3) 카테고리 색 링 — 낱개 핀의 색 테두리와 같은 역할.
            canvas.drawCircle(
                center,
                center,
                radius - CLUSTER_RING_WIDTH_DP * density / 2f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = accent
                    style = Paint.Style.STROKE
                    strokeWidth = CLUSTER_RING_WIDTH_DP * density
                }
            )
            // 4) 개수 — 흰 속에 카테고리 색으로 쓴다(핀의 색 글리프와 같은 자리).
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = accent
                textAlign = Paint.Align.CENTER
                textSize = diameterDp * CLUSTER_TEXT_SIZE_RATIO * density
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            }
            val baseline = center - (textPaint.ascent() + textPaint.descent()) / 2f
            canvas.drawText(label, center, baseline, textPaint)
        }
    }

// 낱개 핀 애셋(map_hospitalmaker/map_travelmaker/map_foodmaker.webp)에서 실제로 쓰이는 색과
// 같은 값 — 묶음이 그 핀들 사이에 섞였을 때 같은 종류로 읽히게 한다.
private fun MapPinType.clusterColor(): Int = when (this) {
    MapPinType.HOSPITAL -> 0xFFFB5364.toInt()
    MapPinType.TOURIST -> 0xFF326BF6.toInt()
    MapPinType.FOOD -> 0xFFFAA85C.toInt()
}

// 코스 시작/도착 전용 마커(cource_detail_start/end) — 원본 PNG는 코랄 원 + 흰 화살표 아이콘뿐이라,
// 번호 핀과 같은 흰 여백/회색 경계선 링을 직접 덧그리고 그 안쪽에 원본 이미지를 줄여서 앉힌다.
// 전체 마커 크기(ROUTE_ENDPOINT_ICON_SIZE_DP)는 그대로 두고 사진만 안쪽으로 줄어든다.
private val routeEndpointBitmapCache = mutableMapOf<Int, Bitmap>()
private const val ROUTE_ENDPOINT_ICON_SIZE_DP = 29
private const val ROUTE_ENDPOINT_MARGIN_WIDTH_DP = 0.6f

private fun Context.routeEndpointBitmap(@DrawableRes resId: Int): Bitmap =
    routeEndpointBitmapCache.getOrPut(resId) {
        val drawable = requireNotNull(ContextCompat.getDrawable(this, resId)) { "drawable not found: $resId" }
        val density = resources.displayMetrics.density
        val size = (ROUTE_ENDPOINT_ICON_SIZE_DP * density).toInt().coerceAtLeast(1)
        val borderWidth = PIN_BORDER_WIDTH_DP * density
        // 번호 핀(PIN_MARGIN_WIDTH_DP)의 절반 — 출발/도착 마커만 흰 여백을 더 얇게.
        val marginWidth = ROUTE_ENDPOINT_MARGIN_WIDTH_DP * density
        Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            val center = size / 2f
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COURSE_LINE_BORDER_COLOR
                style = Paint.Style.FILL
            }
            val marginPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawCircle(center, center, center, borderPaint)
            canvas.drawCircle(center, center, center - borderWidth, marginPaint)

            val photoRadius = center - borderWidth - marginWidth
            val photoSize = (photoRadius * 2f).toInt().coerceAtLeast(1)
            val photoOffset = ((size - photoSize) / 2f).toInt()
            canvas.save()
            canvas.translate(photoOffset.toFloat(), photoOffset.toFloat())
            drawable.setBounds(0, 0, photoSize, photoSize)
            drawable.draw(canvas)
            canvas.restore()
        }
    }

// 번호 핀/시작·도착 마커가 공유하는 흰 여백·회색 경계선 두께 — 코스 경로선(COURSE_LINE_MARGIN_WIDTH_PX
// 등)과 같은 비율감을 원 마커 크기(22~36dp)에 맞게 줄인 값이다.
private const val PIN_BORDER_WIDTH_DP = 1.5f
private const val PIN_MARGIN_WIDTH_DP = 2f

// 12는 부산 전역이 한눈에 들어오는 대신 개별 건물이 안 보이는 배율이었다 — 첫 화면에서
// BusanDefaultCenter(정근안과병원)가 실제로 식별되는 배율까지 당긴다(값이 클수록 확대).
private const val DEFAULT_ZOOM_LEVEL = 17
// 이 배율 이상으로 확대돼 있으면 핀이 서로 겹치지 않아 묶지 않는다.
private const val CLUSTER_MIN_ZOOM_LEVEL = 16
// 격자 한 칸을 타일(256px)의 몇 배로 잡을지 — 0.38이면 화면에서 약 97px 간격으로, 묶음 마커
// 지름(최대 40dp)보다 넉넉해서 이웃한 묶음끼리 겹쳐 보이지 않는다.
private const val CLUSTER_CELL_TILE_RATIO = 0.38
private const val CLUSTER_ID_PREFIX = "cluster:"
private const val CLUSTER_ZOOM_IN_STEP = 3
private const val MAX_ZOOM_LEVEL = 20
private const val CLUSTER_MAX_DISPLAY_COUNT = 99
// 개수 구간별 지름(dp) — 숫자를 읽기 전에 밀집도가 먼저 보이게 하는 3단계.
private const val CLUSTER_MEDIUM_THRESHOLD = 10
private const val CLUSTER_LARGE_THRESHOLD = 50
// 낱개 핀이 24dp(PIN_ICON_SIZE_DP)라 묶음은 그보다 한 단계씩만 크게 잡는다 — 더 키우면 지도를
// 덮어버려서 정작 어디가 밀집 지역인지 안 보인다(실기기에서 확인하고 줄인 값).
private const val CLUSTER_DIAMETER_SMALL_DP = 26
private const val CLUSTER_DIAMETER_MEDIUM_DP = 30
private const val CLUSTER_DIAMETER_LARGE_DP = 34
// 자릿수가 늘면 숫자가 링에 닿지 않게 지름을 조금씩 넓힌다.
private const val CLUSTER_SIZE_PER_EXTRA_CHAR_DP = 3
private const val CLUSTER_RING_WIDTH_DP = 2.5f
private const val CLUSTER_HALO_PADDING_DP = 3f
private const val CLUSTER_HALO_ALPHA = 0x1F000000
private const val CLUSTER_TEXT_SIZE_RATIO = 0.44f
private const val SINGLE_PIN_ZOOM_LEVEL = 16
private const val FIT_PADDING_PX = 140
private const val ROUTE_ARROW_PATTERN_DISTANCE_PX = 48f
private const val ROUTE_LINE_WIDTH_PX = 10f
private const val ROUTE_LINE_COLOR = 0xFFFF6F61.toInt()
private const val COURSE_ROUTE_LAYER_ID = "recommended-tourism-course"
private const val COURSE_ROUTE_Z_ORDER = 20_000
private const val COURSE_LINE_WIDTH_PX = 16f
// 코랄 코어 양옆 흰색 여백대의 (한쪽) 두께 — 10f의 약 75%.
private const val COURSE_LINE_MARGIN_WIDTH_PX = 7.5f
// 흰 여백 바깥쪽에 얇게 두르는 경계선의 (한쪽) 두께.
private const val COURSE_LINE_BORDER_WIDTH_PX = 1.5f
private val COURSE_LINE_BORDER_COLOR = android.graphics.Color.parseColor("#9E9E9E")
// 화살촉 아이콘(16dp)보다 좁으면 아이콘끼리 겹쳐 "군데군데 두 개씩 붙어 보이는" 불균일한 느낌이
// 났다 — 아이콘 폭보다 확실히 넓게 잡아 겹침 없이 고른 간격을 유지한다.
private const val COURSE_ROUTE_ARROW_PATTERN_DISTANCE_PX = 30f

// core/designsystem/Color.kt의 CoralPrimary와 같은 색 — 코스 경로선/번호 노드 모두 이 값을 쓴다.
private val CoralRouteColor = CoralPrimary.toArgb()
private val CoralRouteColorSelected = Color(0xFFE36A6D).toArgb()
