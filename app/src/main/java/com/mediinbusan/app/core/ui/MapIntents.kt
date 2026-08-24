package com.mediinbusan.app.core.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * F-017 외부 지도 앱 연결. 위치 권한 없이도 좌표만으로 동작하는 표준 geo: 인텐트를 사용한다.
 * 좌표가 없으면 주소 텍스트만으로 검색되는 geo: 쿼리로 폴백한다.
 */
fun Context.launchExternalDirections(latitude: Double?, longitude: Double?, label: String, fallbackAddress: String) {
    val uri = if (latitude != null && longitude != null) {
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})")
    } else {
        Uri.parse("geo:0,0?q=${Uri.encode(fallbackAddress)}")
    }
    launchIntentSafely(Intent(Intent.ACTION_VIEW, uri))
}

fun Context.launchIntentSafely(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // 대상 앱(다이얼러/지도/문자/공유)이 없는 환경(에뮬레이터 등)에서는 조용히 무시한다.
    }
}

/**
 * F-014 웰니스 코스 동선의 경유지 하나. name은 지도 위 화살표 경로선을 그릴 때는 쓰이지 않고(좌표만
 * 사용), 향후 다른 표시 용도를 위해 남겨둔다. core/ui/KakaoMapView.kt의 KakaoMapView(routeStops=...)에
 * 넘겨 방문 순서대로 화살표 패턴이 반복되는 경로선을 그리는 데 쓴다 — 외부 지도 앱으로 나가는 대신
 * 우리 지도 안에서 방향을 보여주기 위한 용도라 실제 도로/보행로를 정확히 따라가지는 않는다(그
 * 문서의 renderRoute 주석 참고).
 */
data class RouteStop(val name: String, val latitude: Double, val longitude: Double)
