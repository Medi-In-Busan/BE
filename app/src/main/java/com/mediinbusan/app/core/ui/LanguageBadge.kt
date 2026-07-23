package com.mediinbusan.app.core.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.BadgeOutline
import com.mediinbusan.app.core.designsystem.BadgeText

/** 병원/장소 카드 전반에서 쓰는 언어 지원 배지(EN/JP/CN 등). Home의 원안 스타일을 그대로 공용화한다. */
@Composable
fun LanguageBadge(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(width = 1.dp, color = BadgeOutline, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, color = BadgeText)
    }
}

/** Hospital.supportedLanguages의 ISO 계열 코드(en/ja/zh)를 배지 표시용 라벨로 매핑한다. */
fun String.toLanguageBadgeLabel(): String = when (this.lowercase()) {
    "ko" -> "KO"
    "en" -> "EN"
    "zh" -> "CN"
    "ja" -> "JP"
    else -> this.uppercase()
}

/** 기본 정보 섹션 등에서 쓰는 언어 코드의 사람이 읽는 전체 이름(예: en → English). */
fun String.toLanguageDisplayName(): String = when (this.lowercase()) {
    "ko" -> "한국어"
    "en" -> "English"
    "zh" -> "中文"
    "ja" -> "日本語"
    else -> this.uppercase()
}
