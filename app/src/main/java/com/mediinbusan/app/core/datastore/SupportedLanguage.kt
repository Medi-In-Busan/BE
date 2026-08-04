package com.mediinbusan.app.core.datastore

/** 앱 전체가 지원하는 언어의 단일 출처. Home/Onboarding/Settings가 모두 이 값을 참조한다. */
enum class SupportedLanguage(val code: String, val badgeLabel: String, val displayName: String) {
    KO("ko", "KO", "한국어"),
    EN("en", "EN", "English"),
    ZH("zh", "CN", "中文"),
    JA("ja", "JP", "日本語");

    companion object {
        val DEFAULT = KO
        val CODES = entries.map { it.code }
    }
}
