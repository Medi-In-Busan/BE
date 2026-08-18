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

// Home(S-03) 페이지 맨 뒤 배경 전용 — 흰색에 아주 가까운 옅은 코랄핑크. colorScheme.background는
// 건드리지 않는다(위 TODO와 같은 이유 — 다른 화면 배경까지 같이 바뀌는 걸 피한다). Home의
// Scaffold containerColor로만 직접 참조한다.
val HomeBackgroundPink = Color(0xFFFFFAFA)

// Home 히어로 배너(S-03) 텍스트 전용 코랄 — CoralPrimary보다 살짝 차분하되 화사함은
// 유지한다(너무 어두운 톤은 피드백으로 제외됨).
val CoralMuted = Color(0xFFF4707F)

// Home 히어로 배너 서브텍스트 전용. TextSecondary(보라 기가 도는 회색)보다 검정을 살짝 더 섞어
// 명암 없는 밝은 사진 위에서도 또렷하게 읽히도록 하되, 주텍스트(검정)보다는 확실히 옅게 둔다.
val HeroBodyGray = Color(0xFF757580)

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

// 병원 상세페이지 영업 상태 배지("영업 중"/"영업 종료") 전용.
val StatusOpenGreen = Color(0xFF2E9E44)
val StatusClosedGray = Color(0xFF9B9B9B)

// 자가진단(SelfDiagnosis) 화면 색상 토큰.
val BorderColor = Color(0xFFE5E7EB)
val PageBackground = Color(0xFFF8FAFC)
val InfoBackground = Color(0xFFEAF5FF)
val WarningBackground = Color(0xFFFFF4E5)

// 이용 가이드(S-06) STEP 카드 강조색 6종.
val GuideStepBlue = Color(0xFF4A90D9)
val GuideStepRed = Color(0xFFEF6B6B)
val GuideStepPurple = Color(0xFF8B7FE8)
val GuideStepTeal = Color(0xFF2FB6B6)
val GuideStepOrange = Color(0xFFF0A93A)
val GuideStepGreen = Color(0xFF4CAF50)

// 이용 가이드(S-06) STEP03 카드 배지·상황별 카드 배경 톤.
val GuideBadgeGreenBackground = Color(0xFFE3F6EA)
val GuideBadgePurpleBackground = Color(0xFFEFEBFB)
val GuideCardPeachBackground = Color(0xFFFDF0E1)
val GuideCardLavenderBackground = Color(0xFFEDEAFB)

// 이용 가이드(S-06) STEP05 카드 배지 톤.
val GuideBadgeOrangeBackground = Color(0xFFFDF0DD)

