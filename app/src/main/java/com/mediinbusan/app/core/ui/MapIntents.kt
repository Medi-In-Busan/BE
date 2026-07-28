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
