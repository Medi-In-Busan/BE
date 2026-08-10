package com.mediinbusan.app.core.i18n

data class LanguageSelectStrings(
    val title: String,
    val subtitle: String,
    val infoNote: String,
    val nextButton: String
) {
    companion object {
        val Ko = LanguageSelectStrings(
            title = "언어를 선택해주세요",
            subtitle = "앱에서 사용할 언어를 선택할 수 있습니다.",
            infoNote = "언어는 설정 화면에서 언제든 변경 가능합니다.",
            nextButton = "다음"
        )
        val En = LanguageSelectStrings(
            title = "Please choose a language",
            subtitle = "You can choose the language used in the app.",
            infoNote = "You can change the language anytime in Settings.",
            nextButton = "Next"
        )
        val Zh = LanguageSelectStrings(
            title = "请选择语言",
            subtitle = "您可以选择在应用中使用的语言。",
            infoNote = "您可以随时在设置中更改语言。",
            nextButton = "下一步"
        )
        val Ja = LanguageSelectStrings(
            title = "言語を選択してください",
            subtitle = "アプリで使用する言語を選択できます。",
            infoNote = "言語は設定画面でいつでも変更できます。",
            nextButton = "次へ"
        )
    }
}
