package com.mediinbusan.app.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
    val MEDICAL_PURPOSE = stringPreferencesKey("medical_purpose")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

    // 앱 접근권한 사전 고지(feature/permission)를 이미 확인했는지. 최초 실행 때 한 번만
    // 고지 화면을 띄우기 위한 플래그다 — 설정 > 앱 접근권한에서는 언제든 다시 볼 수 있다.
    val PERMISSION_NOTICE_ACKNOWLEDGED = booleanPreferencesKey("permission_notice_acknowledged")

    // 카메라 권한을 시스템에 한 번이라도 요청했는지. shouldShowRequestPermissionRationale은
    // "아직 안 물어봄"과 "영구 거부"를 둘 다 false로 돌려주므로, 이 플래그가 있어야 두 상태를
    // 구분해 사전 고지 다이얼로그와 설정 안내 다이얼로그 중 맞는 쪽을 띄울 수 있다.
    val CAMERA_PERMISSION_REQUESTED = booleanPreferencesKey("camera_permission_requested")
}
