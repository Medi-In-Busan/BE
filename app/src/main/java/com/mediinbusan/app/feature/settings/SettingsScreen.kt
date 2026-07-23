package com.mediinbusan.app.feature.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.BuildConfig
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsBorder
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsDivider
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.SettingsSectionTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToInfoDetail: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onBack = onBack,
        onLanguageSelected = viewModel::onLanguageSelected,
        onNavigateToInfoDetail = onNavigateToInfoDetail,
        onClearCacheConfirmed = viewModel::onClearCacheConfirmed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onNavigateToInfoDetail: (String) -> Unit,
    onClearCacheConfirmed: () -> Unit
) {
    Scaffold(
        // Home과 동일한 탑바(워드마크+언어 드롭다운)를 그대로 재사용하고, 좌측 아이콘만
        // Home의 "설정으로 이동" 햄버거 대신 뒤로가기 화살표로 바뀐다. 하단 탭바는
        // MediInBusanApp.kt의 shouldShowBottomBar에서 Settings는 제외되어 있어 노출되지 않는다.
        topBar = {
            SettingsTopAppBar(
                onBack = onBack,
                currentLanguageCode = uiState.selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "설정", style = SettingsTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "앱 사용 환경을 설정하고 관리하세요.",
                style = SettingsDescriptionStyle,
                color = SettingsSecondaryText
            )

            Spacer(modifier = Modifier.height(24.dp))
            LanguageSettingCard(
                availableLanguages = uiState.availableLanguages,
                selectedCode = uiState.selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "앱 설정", style = SettingsSectionTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(12.dp))
            SettingsCard {
                SettingsRows(
                    listOf(
                        SettingsRowItem(Icons.Outlined.Notifications, "알림 설정", "새로운 소식과 이벤트 알림을 받아보세요"),
                        SettingsRowItem(Icons.Outlined.Favorite, "즐겨찾기 관리", "저장한 병원과 장소를 확인하고 정리하세요"),
                        SettingsRowItem(Icons.Outlined.History, "최근 본 항목", "최근에 확인한 병원과 정보를 다시 볼 수 있어요")
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "정보", style = SettingsSectionTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(12.dp))
            SettingsCard {
                SettingsRows(
                    listOf(
                        SettingsRowItem(
                            Icons.Outlined.Info,
                            "이용 안내",
                            "메디인부산 이용 방법을 안내해 드려요",
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.USAGE_GUIDE.infoId) }
                        ),
                        SettingsRowItem(
                            Icons.Outlined.PrivacyTip,
                            "개인정보 처리방침",
                            "개인정보 수집 및 이용에 대한 안내입니다",
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.PRIVACY_POLICY.infoId) }
                        ),
                        SettingsRowItem(
                            Icons.Outlined.Description,
                            "이용약관",
                            "서비스 이용에 관한 약관을 확인하세요",
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.TERMS_OF_SERVICE.infoId) }
                        ),
                        // 데이터출처는 아직 별도 페이지 없이 설명 텍스트로만 두고, 분기가 없으니 화살표도 뺀다.
                        SettingsRowItem(
                            Icons.Outlined.Public,
                            "데이터 출처",
                            "한국관광공사 공공데이터를 기반으로 제공됩니다",
                            onClick = null
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "앱 정보", style = SettingsSectionTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(12.dp))
            AppInfoCard(
                cacheClearedEventId = uiState.cacheClearedEventId,
                onClearCacheConfirmed = onClearCacheConfirmed
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(
    onBack: () -> Unit,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
        },
        title = { SettingsWordmark() },
        actions = {
            SettingsLanguageDropdown(
                currentLanguageCode = currentLanguageCode,
                onLanguageSelected = onLanguageSelected
            )
        }
    )
}

@Composable
private fun SettingsWordmark() {
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
private fun SettingsLanguageDropdown(currentLanguageCode: String, onLanguageSelected: (String) -> Unit) {
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
                text = "${currentLanguageCode.toDropdownLabel()} ▾",
                style = MaterialTheme.typography.labelSmall,
                color = SettingsSecondaryText
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("ko", "en", "ja", "zh").forEach { code ->
                DropdownMenuItem(
                    text = { Text(text = code.toDropdownLabel()) },
                    onClick = {
                        onLanguageSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

// SECTION 1: 언어 설정 — 큰 카드 하나. 좌측 아이콘, 가운데 제목/설명, 우측 끝 세그먼트 버튼(KO/EN/JP/CN).
// SupportedLanguage.CODES 순서(ko,en,zh,ja)와 무관하게 읽기 좋은 고정 순서로 보여준다.
private val LanguageSegmentOrder = listOf("ko", "en", "ja", "zh")

@Composable
private fun LanguageSettingCard(
    availableLanguages: List<String>,
    selectedCode: String,
    onLanguageSelected: (String) -> Unit
) {
    SettingsCard {
        Column(modifier = Modifier.fillMaxWidth().padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RowIcon(icon = Icons.Outlined.Language)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "언어 변경", style = SettingsItemTitleStyle, color = SettingsPrimaryText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "앱이 표시되는 언어를 변경할 수 있습니다.",
                        style = SettingsDescriptionStyle,
                        color = SettingsSecondaryText
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageSegmentOrder.filter { it in availableLanguages }.forEach { code ->
                    LanguageSegmentButton(
                        label = code.toShortLabel(),
                        selected = code == selectedCode,
                        onClick = { onLanguageSelected(code) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageSegmentButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) CoralPrimaryContainer else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) CoralPrimaryContainer else SettingsBorder,
                shape = RoundedCornerShape(percent = 50)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) CoralPrimary else SettingsSecondaryText
        )
    }
}

// SECTION 2/3: 카드 안에 여러 Row + Divider. onClick이 null이면 분기 페이지가 없다는 뜻으로,
// 행을 클릭 불가능하게 두고 우측 화살표(chevron)도 표시하지 않는다(데이터 출처 등).
private data class SettingsRowItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: (() -> Unit)? = {}
)

@Composable
private fun SettingsRows(items: List<SettingsRowItem>) {
    items.forEachIndexed { index, item ->
        SettingsRow(icon = item.icon, title = item.title, description = item.description, onClick = item.onClick)
        if (index != items.lastIndex) {
            HorizontalDivider(color = SettingsDivider, modifier = Modifier.padding(horizontal = 20.dp))
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: (() -> Unit)? = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(icon = icon)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = description, style = SettingsDescriptionStyle, color = SettingsSecondaryText)
        }
        if (onClick != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SettingsSecondaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AppInfoCard(cacheClearedEventId: Int, onClearCacheConfirmed: () -> Unit) {
    var showClearCacheDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(cacheClearedEventId) {
        if (cacheClearedEventId > 0) {
            Toast.makeText(context, "캐시를 삭제했어요", Toast.LENGTH_SHORT).show()
        }
    }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text(text = "캐시를 삭제할까요?") },
            text = { Text(text = "저장된 이미지 캐시가 삭제됩니다. 즐겨찾기와 설정은 그대로 유지돼요.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearCacheDialog = false
                    onClearCacheConfirmed()
                }) {
                    Text(text = "삭제", color = CoralPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text(text = "취소")
                }
            }
        )
    }

    SettingsCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.favicon),
                contentDescription = "메디인부산 로고",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "MEDIN BUSAN", style = SettingsItemTitleStyle, color = SettingsPrimaryText)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "Version ${BuildConfig.VERSION_NAME}", style = SettingsDescriptionStyle, color = SettingsSecondaryText)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = SettingsSecondaryText,
                modifier = Modifier.size(16.dp)
            )
        }
        HorizontalDivider(color = SettingsDivider, modifier = Modifier.padding(horizontal = 20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RowIcon(icon = Icons.Outlined.Headset)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "고객센터", style = SettingsItemTitleStyle, color = SettingsPrimaryText)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "support@medinbusan.kr", style = SettingsDescriptionStyle, color = SkyBlue)
            }
        }
        HorizontalDivider(color = SettingsDivider, modifier = Modifier.padding(horizontal = 20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RowIcon(icon = Icons.Outlined.Delete)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "캐시 삭제", style = SettingsItemTitleStyle, color = SettingsPrimaryText, modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = { showClearCacheDialog = true },
                modifier = Modifier.height(34.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CoralPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralPrimary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text(text = "삭제하기", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun RowIcon(icon: ImageVector) {
    Box(
        modifier = Modifier.size(40.dp).clip(CircleShape).background(CoralPrimaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White),
        content = content
    )
}

// feature/onboarding의 LanguageOption 라벨(한국어/ENGLISH/日本語/中文)과 표기를 통일한다.
// 언어 설정 카드의 세그먼트 버튼 전용. 언어 코드(ko/en/ja/zh) 자체는 그대로 두고 화면에
// 보여주는 문자열만 맞춘다.
private fun String.toShortLabel(): String = when (this) {
    "ko" -> "한국어"
    "en" -> "ENGLISH"
    "ja" -> "日本語"
    "zh" -> "中文"
    else -> this.uppercase()
}

// 탑바 언어 드롭다운 전용. HomeScreen.kt의 toLanguageBadgeLabel()과 표기를 맞춘다
// (ko/en/zh/ja → KO/EN/CN/JP). Home은 팀원의 미머지 PR이 같이 건드리고 있어 이 화면에
// 로컬로 별도 함수를 둔다.
private fun String.toDropdownLabel(): String = when (this) {
    "ko" -> "KO"
    "en" -> "EN"
    "zh" -> "CN"
    "ja" -> "JP"
    else -> this.uppercase()
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    MediInBusanTheme {
        SettingsContent(
            uiState = SettingsUiState(selectedLanguage = "ko"),
            onBack = {},
            onLanguageSelected = {},
            onNavigateToInfoDetail = {},
            onClearCacheConfirmed = {}
        )
    }
}
