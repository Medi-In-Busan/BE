package com.mediinbusan.app.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.SettingsBorder
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.SkyBlue

/**
 * Settings(S-10) 계열 화면(설정/알림 설정/즐겨찾기 관리/최근 본 항목) 공용 탑바.
 * Home의 탑바와 시각적으로 동일하지만(워드마크+언어 드롭다운), 좌측 아이콘이 뒤로가기라는 점만 다르다.
 * Home은 팀원의 미머지 PR이 같이 건드리고 있어 별도 로컬 사본을 두고, 이 탑바를 쓰는 화면들끼리만 공유한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandBackTopAppBar(
    onBack: () -> Unit,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = navigationIcon, contentDescription = "뒤로가기")
            }
        },
        title = { BrandWordmark() },
        actions = {
            BrandLanguageDropdown(
                currentLanguageCode = currentLanguageCode,
                onLanguageSelected = onLanguageSelected
            )
        }
    )
}

@Composable
private fun BrandWordmark() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "메디인부산 로고",
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = CoralPrimary, fontWeight = FontWeight.Bold)) { append("MEDIN") }
                append(" ")
                withStyle(SpanStyle(color = SkyBlue, fontWeight = FontWeight.Bold)) { append("BUSAN") }
            },
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun BrandLanguageDropdown(currentLanguageCode: String, onLanguageSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(end = 12.dp)) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .border(width = 1.dp, color = SettingsBorder, shape = MaterialTheme.shapes.medium)
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${currentLanguageCode.toTopBarLanguageLabel()} ▾",
                style = MaterialTheme.typography.labelSmall,
                color = SettingsSecondaryText
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("ko", "en", "ja", "zh").forEach { code ->
                DropdownMenuItem(
                    text = { Text(text = code.toTopBarLanguageLabel()) },
                    onClick = {
                        onLanguageSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

// HomeScreen.kt의 toLanguageBadgeLabel()과 표기를 맞춘다(ko/en/zh/ja → KO/EN/CN/JP).
fun String.toTopBarLanguageLabel(): String = when (this) {
    "ko" -> "KO"
    "en" -> "EN"
    "zh" -> "CN"
    "ja" -> "JP"
    else -> this.uppercase()
}
