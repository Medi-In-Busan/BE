package com.mediinbusan.app.feature.onboarding

import androidx.annotation.DrawableRes
import com.mediinbusan.app.R
import com.mediinbusan.app.core.datastore.SupportedLanguage

data class LanguageOption(
    val code: String,
    // 카드에 굵게 표시되는 단일 라벨. 각 언어의 자체 표기(한국어/ENGLISH/日本語/中文)를 그대로 쓴다.
    val label: String,
    @param:DrawableRes val flagRes: Int
) {
    companion object {
        val DEFAULTS = listOf(
            LanguageOption(code = "ko", label = "한국어", flagRes = R.drawable.lang_korea),
            LanguageOption(code = "en", label = "ENGLISH", flagRes = R.drawable.lang_usa),
            LanguageOption(code = "ja", label = "日本語", flagRes = R.drawable.lang_japan),
            LanguageOption(code = "zh", label = "中文", flagRes = R.drawable.lang_chino)
        )
    }
}

data class LanguageSelectUiState(
    val options: List<LanguageOption> = LanguageOption.DEFAULTS,
    val selectedCode: String = SupportedLanguage.DEFAULT.code
)
