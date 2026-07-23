package com.mediinbusan.app.core.designsystem

import androidx.compose.ui.graphics.Color

val MediBlue40 = Color(0xFF2F6690)
val MediBlue80 = Color(0xFFA9C7DC)
val MediTeal40 = Color(0xFF3A7D7B)
val MediTeal80 = Color(0xFFA6D2CF)
val MediSand40 = Color(0xFFB08968)
val MediSand80 = Color(0xFFE6CBA8)
val MediError40 = Color(0xFFBA1A1A)
val MediError80 = Color(0xFFFFB4AB)

// Home(S-03) 참고 디자인 스크린샷 기반 토큰. lightColorScheme/darkColorScheme에는 배선하지 않고
// 사용처(Home)에서 직접 참조한다 — MediBlue40이 이미 전역 primary로 쓰이고 있어
// colorScheme.primary를 교체하면 다른 모든 화면의 버튼 색까지 바뀌기 때문.
// TODO: 실제 브랜드 컬러로 확정되면 별도 리브랜딩 이슈에서 colorScheme 승격 검토.
val CoralPrimary = Color(0xFFFD6677)
val CoralPrimaryContainer = Color(0xFFFDECEC)

// 로고 워드마크 "BUSAN" 포인트 컬러
val SkyBlue = Color(0xFF0B84D2)

val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF8B8B9A)
val DividerColor = Color(0xFFEEEEEE)
val BadgeOutline = Color(0xFFDADADA)
val BadgeText = Color(0xFF666666)
val InactiveIcon = Color(0xFFD9D9D9)

// 하단 내비게이션 바 비활성 아이콘/라벨 전용. InactiveIcon(#D9D9D9)은 캐러셀 점 용도로
// 만들어져 텍스트/아이콘 대비가 부족해 별도 토큰으로 분리한다.
val InactiveTabColor = Color(0xFF9B9B9B)

// 언어선택(S-02) 안내 Card 배경. 옅은 스카이블루 틴트라 기존 CoralPrimaryContainer 계열과는
// 다른 별도 톤이 필요하다.
val InfoBackgroundBlue = Color(0xFFF3F8FF)

// 설정(S-10) 리디자인 스펙 전용 팔레트. 기존 TextPrimary/TextSecondary/DividerColor와
// 근소하게 값이 달라(디자인 스펙 원본 그대로) 다른 화면에 영향 주지 않도록 분리한다.
val SettingsPrimaryText = Color(0xFF1F2937)
val SettingsSecondaryText = Color(0xFF9CA3AF)
val SettingsDivider = Color(0xFFF3F4F6)
val SettingsBorder = Color(0xFFECECEC)
