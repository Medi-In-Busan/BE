package com.mediinbusan.app.core.datastore

/** 앱 전체가 지원하는 언어 코드의 단일 출처. Home/Onboarding/Settings가 모두 이 값을 참조한다. */
object SupportedLanguages {
    val CODES = listOf("ko", "en", "zh", "ja")
    const val DEFAULT = "ko"
}
