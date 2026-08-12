package com.mediinbusan.app.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mediinbusan.app.R

// res/font의 정적(static) 파일 4종만 매핑한다 — ExtraBold 등 그 외 굵기를 쓰는 곳(GuideScreen 등)은
// 안드로이드가 자동으로 가장 가까운 굵기(Bold)로 대체해 렌더링한다.
val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold)
)

// Typography()의 크기·줄간격·굵기 등 기존 값은 그대로 두고 fontFamily만 15개 슬롯 전부에 배선한다.
private val DefaultTypography = Typography()
val MediInBusanTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = Pretendard),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = Pretendard),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = Pretendard),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = Pretendard),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = Pretendard),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = Pretendard),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = Pretendard),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = Pretendard),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = Pretendard),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = Pretendard),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = Pretendard),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = Pretendard),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = Pretendard),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = Pretendard),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = Pretendard)
)

// Home(S-03) 등에서 쓰는 커스텀 스타일. 기존 Typography() 슬롯(headlineSmall, titleMedium 등)은
// 다른 화면에서 이미 쓰고 있어 값을 바꾸면 그 화면들도 같이 바뀌므로 덮어쓰지 않고 별도로 둔다.
// Category Label(12sp/Medium)·Card Subtext(12sp/Regular)·Bottom Nav Label(11sp/Medium)은
// 각각 labelMedium/bodySmall/labelSmall과 정확히 일치해 기존 슬롯을 그대로 재사용한다.
// fontWeight는 스펙대로 Bold/Normal(Regular)/SemiBold로 맞춰져 있다.
val HeroTitleStyle = TextStyle(fontFamily = Pretendard, fontSize = 21.sp, fontWeight = FontWeight.Bold)
val HeroSubtitleStyle = TextStyle(fontFamily = Pretendard, fontSize = 13.sp, fontWeight = FontWeight.Normal)
val SectionTitleStyle = TextStyle(fontFamily = Pretendard, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
val CardTitleStyle = TextStyle(fontFamily = Pretendard, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)

// 언어선택(S-02) 타이틀/설명 전용. 타이틀은 스펙상 36 Bold였으나 실기기 프리뷰 피드백으로 30% 축소했다.
val DisplayTitleStyle = TextStyle(fontFamily = Pretendard, fontSize = 25.sp, fontWeight = FontWeight.Bold)
val BodyRegularStyle = TextStyle(fontFamily = Pretendard, fontSize = 15.sp, fontWeight = FontWeight.Normal)

// 설정(S-10) 리디자인 스펙 전용. Item Title은 스펙상 CardTitleStyle(15 SemiBold)과 같은 값이었으나
// 카드가 빽빽해 보인다는 피드백으로 한 단계 낮춰 별도 스타일로 뺐다.
val SettingsTitleStyle = TextStyle(fontFamily = Pretendard, fontSize = 28.sp, fontWeight = FontWeight.Bold)
val SettingsSectionTitleStyle = TextStyle(fontFamily = Pretendard, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
val SettingsItemTitleStyle = TextStyle(fontFamily = Pretendard, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
val SettingsDescriptionStyle = TextStyle(fontFamily = Pretendard, fontSize = 12.sp, fontWeight = FontWeight.Normal)
