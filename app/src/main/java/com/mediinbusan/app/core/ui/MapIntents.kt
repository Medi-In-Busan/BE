package com.mediinbusan.app.core.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * F-017 외부 지도 앱 연결(길찾기).
 *
 * 예전엔 표준 `geo:` 인텐트(`geo:lat,lng?q=lat,lng(이름)`)를 썼는데, 카카오맵이 이 스킴을 받으면
 * `q` 값을 **검색어**로 해석한다 — 그래서 길찾기가 아니라 "35.158,129.055" 같은 좌표 문자열이
 * 검색창에 그대로 박힌 검색 결과 화면이 떴다. 카카오맵 공식 URL 스킴의 길찾기(`kakaomap://route`)를
 * 먼저 쓰고, 앱이 없을 때만 단계적으로 폴백한다.
 *
 * 출발지(`sp`)는 넘기지 않는다 — 이 앱은 위치 권한을 쓰지 않으므로(CLAUDE.md §1) 현재 위치는
 * 카카오맵이 자기 권한으로 잡게 둔다.
 */
fun Context.launchExternalDirections(latitude: Double?, longitude: Double?, label: String, fallbackAddress: String) {
    if (latitude != null && longitude != null) {
        // 1) 카카오맵 앱의 길찾기 화면. 외국인 의료관광객이 주 사용자라 대중교통을 기본 수단으로 둔다.
        val routeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://route?ep=$latitude,$longitude&by=PUBLICTRANSIT"))
        if (startActivitySafely(routeIntent)) return

        // 2) 앱이 없으면 웹 길찾기 링크 — 브라우저에서 열리고, 앱이 있는 기기에선 앱으로 이어진다.
        val webRoute = Uri.parse("https://map.kakao.com/link/to/${Uri.encode(label)},$latitude,$longitude")
        if (startActivitySafely(Intent(Intent.ACTION_VIEW, webRoute))) return

        // 3) 마지막 폴백: 표준 geo:(구글맵 등 다른 지도 앱). 길찾기까진 아니어도 위치는 찍힌다.
        startActivitySafely(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(label)})")))
    } else {
        // 좌표가 없으면 길찾기 자체가 성립하지 않는다 — 이때만 주소로 장소를 찾아주는 검색으로 넘긴다.
        val webSearch = Uri.parse("https://map.kakao.com/link/search/${Uri.encode(fallbackAddress)}")
        if (startActivitySafely(Intent(Intent.ACTION_VIEW, webSearch))) return
        startActivitySafely(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(fallbackAddress)}")))
    }
}

fun Context.launchIntentSafely(intent: Intent) {
    startActivitySafely(intent)
}

/** 대상 앱이 없으면(에뮬레이터 등) 조용히 false를 돌려준다 — 호출부가 다음 폴백으로 넘어갈 수 있게. */
private fun Context.startActivitySafely(intent: Intent): Boolean = try {
    startActivity(intent)
    true
} catch (e: ActivityNotFoundException) {
    false
}

/**
 * F-014 웰니스 코스 동선의 경유지 하나. name은 지도 위 화살표 경로선을 그릴 때는 쓰이지 않고(좌표만
 * 사용), 향후 다른 표시 용도를 위해 남겨둔다. core/ui/KakaoMapView.kt의 KakaoMapView(routeStops=...)에
 * 넘겨 방문 순서대로 화살표 패턴이 반복되는 경로선을 그리는 데 쓴다 — 외부 지도 앱으로 나가는 대신
 * 우리 지도 안에서 방향을 보여주기 위한 용도라 실제 도로/보행로를 정확히 따라가지는 않는다(그
 * 문서의 renderRoute 주석 참고).
 */
data class RouteStop(val name: String, val latitude: Double, val longitude: Double)
