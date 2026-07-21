package com.mediinbusan.app.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// res/font에 Pretendard 파일이 아직 없어 시스템 기본 폰트를 사용한다.
// TODO: Pretendard 파일이 추가되면 FontFamily로 정의해 아래 스타일들에 배선한다.
val MediInBusanTypography = Typography()

// Home(S-03) 등에서 쓰는 커스텀 스타일. 기존 Typography() 슬롯(headlineSmall, titleMedium 등)은
// 다른 화면에서 이미 쓰고 있어 값을 바꾸면 그 화면들도 같이 바뀌므로 덮어쓰지 않고 별도로 둔다.
// Category Label(12sp/Medium)·Card Subtext(12sp/Regular)·Bottom Nav Label(11sp/Medium)은
// 각각 labelMedium/bodySmall/labelSmall과 정확히 일치해 기존 슬롯을 그대로 재사용한다.
// fontWeight는 스펙대로 Bold/Normal(Regular)/SemiBold로 맞춰져 있다.
val HeroTitleStyle = TextStyle(fontSize = 21.sp, fontWeight = FontWeight.Bold)
val HeroSubtitleStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
val SectionTitleStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
val CardTitleStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
