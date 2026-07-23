package com.mediinbusan.app.feature.settings

import com.mediinbusan.app.core.datastore.SupportedLanguage

data class SettingsUiState(
    val availableLanguages: List<String> = SupportedLanguage.CODES,
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code,
    // 0이면 아직 캐시 삭제가 없었다는 뜻. 삭제될 때마다 증가시켜 Toast를 한 번씩만 띄우는 트리거로 쓴다.
    val cacheClearedEventId: Int = 0
)
