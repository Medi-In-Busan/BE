package com.mediinbusan.app.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer

/**
 * 뒤로가기 화살표가 있던 이전 버전 대신, Home 탑바와 완전히 동일한 구성(로고+언어 드롭다운+설정
 * 톱니)으로 통일한 공용 탑바. 하위 화면 전용 "뒤로가기"라는 개념 자체를 없애고, 대신 이 탑바를 쓰는
 * 화면들도 전부 공용 하단 탭바(BottomNavBar)를 노출해 그쪽으로 이동하게 한다
 * (MediInBusanApp.kt의 shouldShowBottomBar 참고). Home은 팀원의 미머지 PR이 같이 건드리고 있어
 * 별도 로컬 사본(HomeScreen.kt의 HomeTopAppBar)을 두지만, 시각적으로는 항상 같은 디자인을 유지한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandTopAppBar(
    onSettingsClick: () -> Unit,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.home_logo),
                contentDescription = LocalAppStrings.current.common.logoContentDescription,
                // Home 탑바와 동일한 로고/사이즈로 통일.
                modifier = Modifier.height(42.dp)
            )
        },
        actions = {
            BrandLanguageDropdown(
                currentLanguageCode = currentLanguageCode,
                onLanguageSelected = onLanguageSelected
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = LocalAppStrings.current.common.settingsMenuContentDescription,
                    tint = CoralPrimary
                )
            }
        },
        // 아이콘/텍스트 크기는 그대로 두고, 상태바 인셋만큼 생기는 탑바 위쪽 여백이 답답해
        // 보여서 줄인다. 완전히 없애면(top=0) 바가 상태바에 바로 붙어버리니 일부만 뺀다.
        windowInsets = WindowInsets.statusBars.exclude(WindowInsets(top = 14.dp))
    )
}

// HomeScreen.kt의 LanguageDropdown과 같은 톤(다른 팀원 PR과의 충돌 때문에 Home은 로컬 사본을
// 따로 둔다). 닫혀있을 때 트리거를 코랄 색으로 칠해 "현재 활성 언어"임을 드러내고, 펼쳤을 때는
// 목록을 1.5배 크기 + 알약(pill) 모양 선택 표시 + 체크 아이콘으로 활성화 상태를 보여준다.
// DropdownMenuItem 대신 직접 Row를 그린다 — DropdownMenuItem은 내부적으로 최소 112dp 폭을
// 강제해서 contentPadding/모디파이어로는 줄일 수 없었다. 직접 그리면 텍스트 크기만큼만 차지한다.
@Composable
private fun BrandLanguageDropdown(currentLanguageCode: String, onLanguageSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val expandedItemTextStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = MaterialTheme.typography.labelSmall.fontSize * 1.2f,
        lineHeight = MaterialTheme.typography.labelSmall.lineHeight * 1.2f
    )
    val pillShape = RoundedCornerShape(percent = 50)

    Box(modifier = Modifier.padding(end = 12.dp)) {
        Box(
            modifier = Modifier
                .clip(pillShape)
                .border(width = 1.dp, color = CoralPrimary, shape = pillShape)
                .background(CoralPrimaryContainer)
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${currentLanguageCode.toLanguageBadgeLabel()} ▾",
                style = MaterialTheme.typography.labelSmall,
                color = CoralPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.large,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SupportedLanguage.CODES.forEach { code ->
                    val selected = code == currentLanguageCode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(pillShape)
                            .clickable {
                                onLanguageSelected(code)
                                expanded = false
                            }
                            .background(if (selected) CoralPrimaryContainer else Color.Transparent)
                            .padding(horizontal = 10.dp * 1.5f, vertical = 6.dp * 1.5f)
                    ) {
                        Text(
                            text = code.toLanguageBadgeLabel(),
                            style = expandedItemTextStyle,
                            color = if (selected) CoralPrimary else BadgeText,
                            fontWeight = if (selected) FontWeight.Bold else null
                        )
                        if (selected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CoralPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
