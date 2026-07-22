package com.mediinbusan.app.core.ui

import android.content.Context
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.TextSecondary

enum class MapPinType { HOSPITAL, PLACE }

data class MapPin(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val type: MapPinType,
    val selected: Boolean = false
)

/**
 * 부산 시청 좌표. 좌표 하나 없이 지도를 열어야 할 때 쓰는 화면 기본 중심점이며,
 * 사용자의 실제 위치가 아니다 — 이 앱은 위치 권한을 요청하지 않는다(CLAUDE.md §1 참고).
 */
val BusanDefaultCenter: LatLng = LatLng.from(35.1796, 129.0756)

/**
 * 실제 com.kakao.vectormap SDK를 감싼 공용 지도 컴포저블.
 * KAKAO_NATIVE_APP_KEY가 실제 키로 채워지기 전에는 [MapLifeCycleCallback.onMapError]가 호출되어
 * 타일이 비어 보이는 게 정상이다 — 코드/마커/카메라 로직 자체는 실제 키가 들어오면 그대로 동작한다.
 */
@Composable
fun KakaoMapView(
    pins: List<MapPin>,
    modifier: Modifier = Modifier,
    onPinClick: (String) -> Unit = {}
) {
    // libK3fAndroid.so는 arm64-v8a/armeabi-v7a로만 배포되어 x86_64 에뮬레이터에서는
    // KakaoMapSdk.init()이 실패한다(MediInBusanApp.onCreate() 참고). 그 경우 MapView를 만들지
    // 않고 폴백을 보여준다 — 실기기/ARM 에뮬레이터에서는 이 분기를 타지 않는다.
    val kakaoMapUsable = remember { runCatching { KakaoMapSdk.isInitialized() }.getOrDefault(false) }
    if (!kakaoMapUsable) {
        MapUnavailableFallback(modifier)
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }

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

    LaunchedEffect(kakaoMap, pins) {
        val map = kakaoMap ?: return@LaunchedEffect
        renderPins(context, map, pins, onPinClick)
    }
}

@Composable
private fun MapUnavailableFallback(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(Color(0xFFE9E9EE)).padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "이 기기에서는 지도를 표시할 수 없습니다",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

private fun renderPins(context: Context, map: KakaoMap, pins: List<MapPin>, onPinClick: (String) -> Unit) {
    val layer = map.labelManager?.layer ?: return
    layer.removeAll()

    if (pins.isEmpty()) {
        map.moveCamera(CameraUpdateFactory.newCenterPosition(BusanDefaultCenter, DEFAULT_ZOOM_LEVEL))
        return
    }

    val options = pins.map { pin ->
        val iconRes = when {
            pin.type == MapPinType.HOSPITAL && pin.selected -> R.drawable.ic_map_pin_hospital_selected
            pin.type == MapPinType.HOSPITAL -> R.drawable.ic_map_pin_hospital
            else -> R.drawable.ic_map_pin_place
        }
        LabelOptions.from(pin.id, LatLng.from(pin.latitude, pin.longitude))
            .setStyles(LabelStyle.from(context, iconRes))
    }
    layer.addLabels(options)
    map.setOnLabelClickListener { _, _, label ->
        onPinClick(label.labelId)
        true
    }

    if (pins.size == 1) {
        map.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(pins[0].latitude, pins[0].longitude), SINGLE_PIN_ZOOM_LEVEL))
    } else {
        val points = pins.map { LatLng.from(it.latitude, it.longitude) }.toTypedArray()
        map.moveCamera(CameraUpdateFactory.fitMapPoints(points, FIT_PADDING_PX))
    }
}

private const val DEFAULT_ZOOM_LEVEL = 12
private const val SINGLE_PIN_ZOOM_LEVEL = 16
private const val FIT_PADDING_PX = 140
