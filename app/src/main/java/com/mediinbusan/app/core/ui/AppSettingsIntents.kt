package com.mediinbusan.app.core.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * 접근권한 철회 안내(방송미디어통신위원회 "앱 접근권한 동의 가이드라인")에서 요구하는 "철회 경로
 * 제공" — 시스템 설정의 이 앱 상세 화면(권한 항목이 있는 곳)을 연다.
 *
 * ACTION_APPLICATION_DETAILS_SETTINGS는 모든 단말이 보장하는 화면이지만, 일부 커스텀 ROM에서
 * 액티비티가 없을 수 있어 실패하면 전체 앱 목록 설정으로 폴백한다.
 */
fun Context.openAppPermissionSettings() {
    val detailsIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (startActivityOrFalse(detailsIntent)) return
    startActivityOrFalse(
        Intent(Settings.ACTION_APPLICATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

private fun Context.startActivityOrFalse(intent: Intent): Boolean = try {
    startActivity(intent)
    true
} catch (e: Exception) {
    false
}
