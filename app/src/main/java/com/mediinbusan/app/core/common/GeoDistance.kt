package com.mediinbusan.app.core.common

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 서면(부전동) 좌표. 시드 데이터 병원 대다수가 서면 일대에 몰려 있어 사용자의 실제 GPS 위치 없이
 * "가까운순" 정렬/지도 기본 중심점의 기준으로 삼는다(CLAUDE.md §1 — 위치 권한을 쓰지 않는다).
 * core/ui/KakaoMapView.kt의 BusanDefaultCenter와 같은 좌표를 가리키는 단일 소스.
 */
object DefaultSearchOrigin {
    const val LATITUDE = 35.1579
    const val LONGITUDE = 129.0599
}

private const val EARTH_RADIUS_METERS = 6_371_000.0

/** 두 좌표 사이의 대권거리(m). GPS 없이 서면 기준점 등 고정 좌표로부터의 거리 정렬에 쓴다. */
fun haversineDistanceMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_METERS * c
}

/** 1km 미만은 "123m", 그 이상은 "1.2km"로 — 의료기관 리스트/즐겨찾기/최근 본 항목 카드가 공용으로 쓴다. */
fun Double.toDistanceLabel(): String =
    if (this < 1000.0) "${toInt()}m" else String.format("%.1fkm", this / 1000.0)
