package com.mediinbusan.app.core.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import com.mediinbusan.app.core.datastore.SupportedLanguage

/**
 * Home/LanguageSelect/Settings/Search(HospitalSearchList)/HospitalDetail 5개 화면의 정적 UI
 * 문구를 언어별로 묶은 루트. 병원 데이터 등 API 응답 기반 동적 텍스트는 다루지 않는다.
 */
data class AppStrings(
    val language: SupportedLanguage,
    val common: CommonStrings,
    val home: HomeStrings,
    val languageSelect: LanguageSelectStrings,
    val settings: SettingsStrings,
    val search: SearchStrings,
    val hospitalDetail: HospitalDetailStrings,
    val settingsInfoDetail: SettingsInfoDetailStrings,
    val notificationSettings: NotificationSettingsStrings,
    val favorite: FavoriteStrings,
    val recentlyViewed: RecentlyViewedStrings,
    val guide: GuideStrings,
    val selfDiagnosis: SelfDiagnosisStrings,
    val documentScan: DocumentScanStrings,
    val map: MapStrings,
    val nearby: NearbyStrings
)

private val KoreanAppStrings = AppStrings(
    language = SupportedLanguage.KO,
    common = CommonStrings.Ko,
    home = HomeStrings.Ko,
    languageSelect = LanguageSelectStrings.Ko,
    settings = SettingsStrings.Ko,
    search = SearchStrings.Ko,
    hospitalDetail = HospitalDetailStrings.Ko,
    settingsInfoDetail = SettingsInfoDetailStrings.Ko,
    notificationSettings = NotificationSettingsStrings.Ko,
    favorite = FavoriteStrings.Ko,
    recentlyViewed = RecentlyViewedStrings.Ko,
    guide = GuideStrings.Ko,
    selfDiagnosis = SelfDiagnosisStrings.Ko,
    documentScan = DocumentScanStrings.Ko,
    map = MapStrings.Ko,
    nearby = NearbyStrings.Ko
)

private val EnglishAppStrings = AppStrings(
    language = SupportedLanguage.EN,
    common = CommonStrings.En,
    home = HomeStrings.En,
    languageSelect = LanguageSelectStrings.En,
    settings = SettingsStrings.En,
    search = SearchStrings.En,
    hospitalDetail = HospitalDetailStrings.En,
    settingsInfoDetail = SettingsInfoDetailStrings.En,
    notificationSettings = NotificationSettingsStrings.En,
    favorite = FavoriteStrings.En,
    recentlyViewed = RecentlyViewedStrings.En,
    guide = GuideStrings.En,
    selfDiagnosis = SelfDiagnosisStrings.En,
    documentScan = DocumentScanStrings.En,
    map = MapStrings.En,
    nearby = NearbyStrings.En
)

private val ChineseAppStrings = AppStrings(
    language = SupportedLanguage.ZH,
    common = CommonStrings.Zh,
    home = HomeStrings.Zh,
    languageSelect = LanguageSelectStrings.Zh,
    settings = SettingsStrings.Zh,
    search = SearchStrings.Zh,
    hospitalDetail = HospitalDetailStrings.Zh,
    settingsInfoDetail = SettingsInfoDetailStrings.Zh,
    notificationSettings = NotificationSettingsStrings.Zh,
    favorite = FavoriteStrings.Zh,
    recentlyViewed = RecentlyViewedStrings.Zh,
    guide = GuideStrings.Zh,
    selfDiagnosis = SelfDiagnosisStrings.Zh,
    documentScan = DocumentScanStrings.Zh,
    map = MapStrings.Zh,
    nearby = NearbyStrings.Zh
)

private val JapaneseAppStrings = AppStrings(
    language = SupportedLanguage.JA,
    common = CommonStrings.Ja,
    home = HomeStrings.Ja,
    languageSelect = LanguageSelectStrings.Ja,
    settings = SettingsStrings.Ja,
    search = SearchStrings.Ja,
    hospitalDetail = HospitalDetailStrings.Ja,
    settingsInfoDetail = SettingsInfoDetailStrings.Ja,
    notificationSettings = NotificationSettingsStrings.Ja,
    favorite = FavoriteStrings.Ja,
    recentlyViewed = RecentlyViewedStrings.Ja,
    guide = GuideStrings.Ja,
    selfDiagnosis = SelfDiagnosisStrings.Ja,
    documentScan = DocumentScanStrings.Ja,
    map = MapStrings.Ja,
    nearby = NearbyStrings.Ja
)

fun appStringsFor(language: SupportedLanguage): AppStrings = when (language) {
    SupportedLanguage.KO -> KoreanAppStrings
    SupportedLanguage.EN -> EnglishAppStrings
    SupportedLanguage.ZH -> ChineseAppStrings
    SupportedLanguage.JA -> JapaneseAppStrings
}

fun appStringsFor(languageCode: String): AppStrings =
    appStringsFor(SupportedLanguage.entries.find { it.code == languageCode } ?: SupportedLanguage.DEFAULT)

/** 앱 루트(MediInBusanApp)가 채워주는 현재 언어의 정적 문구. 각 화면은 이 값만 읽으면 된다. */
val LocalAppStrings = staticCompositionLocalOf { KoreanAppStrings }
