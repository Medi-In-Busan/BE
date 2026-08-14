package com.mediinbusan.app.feature.settings

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.BuildConfig
import com.mediinbusan.app.R
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.SettingsStrings
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
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BrandTopAppBar
import com.mediinbusan.app.core.ui.BrandSnackbarHost

@Composable
fun SettingsScreen(
    onNavigateToInfoDetail: (String) -> Unit,
    onNavigateToNotificationSettings: () -> Unit,
    onNavigateToFavoriteManage: () -> Unit,
    onNavigateToRecentlyViewed: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onLanguageSelected = viewModel::onLanguageSelected,
        onNavigateToInfoDetail = onNavigateToInfoDetail,
        onNavigateToNotificationSettings = onNavigateToNotificationSettings,
        onNavigateToFavoriteManage = onNavigateToFavoriteManage,
        onNavigateToRecentlyViewed = onNavigateToRecentlyViewed,
        onClearCacheConfirmed = viewModel::onClearCacheConfirmed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onLanguageSelected: (String) -> Unit,
    onNavigateToInfoDetail: (String) -> Unit,
    onNavigateToNotificationSettings: () -> Unit,
    onNavigateToFavoriteManage: () -> Unit,
    onNavigateToRecentlyViewed: () -> Unit,
    onClearCacheConfirmed: () -> Unit
) {
    val strings = LocalAppStrings.current.settings
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.cacheClearedEventId) {
        if (uiState.cacheClearedEventId > 0) {
            snackbarHostState.showSnackbar(strings.cacheClearedSnackbar)
        }
    }

    Scaffold(
        // Home과 동일한 탑바(로고+언어 드롭다운+설정 톱니)를 그대로 재사용한다. 이미 설정
        // 화면이라 톱니는 자기 자신으로의 이동이 되므로 no-op으로 둔다. 하단 탭바는
        // MediInBusanApp.kt의 shouldShowBottomBar에 Settings가 추가되어 노출된다.
        topBar = {
            BrandTopAppBar(
                onSettingsClick = {},
                currentLanguageCode = uiState.selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        },
        snackbarHost = { BrandSnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding() + BottomNavBarHeight)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = strings.screenTitle, style = SettingsTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.screenSubtitle,
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
            Text(text = strings.appSettingsSectionTitle, style = SettingsSectionTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(12.dp))
            SettingsCard {
                SettingsRows(
                    listOf(
                        SettingsRowItem(
                            Icons.Outlined.Notifications,
                            strings.notificationTitle,
                            strings.notificationDescription,
                            onClick = onNavigateToNotificationSettings
                        ),
                        SettingsRowItem(
                            Icons.Outlined.Favorite,
                            strings.favoriteManageTitle,
                            strings.favoriteManageDescription,
                            onClick = onNavigateToFavoriteManage
                        ),
                        SettingsRowItem(
                            Icons.Outlined.History,
                            strings.recentlyViewedTitle,
                            strings.recentlyViewedDescription,
                            onClick = onNavigateToRecentlyViewed
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = strings.infoSectionTitle, style = SettingsSectionTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(12.dp))
            SettingsCard {
                SettingsRows(
                    listOf(
                        SettingsRowItem(
                            Icons.Outlined.Info,
                            strings.usageGuideTitle,
                            strings.usageGuideDescription,
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.USAGE_GUIDE.infoId) }
                        ),
                        SettingsRowItem(
                            Icons.Outlined.PrivacyTip,
                            strings.privacyPolicyTitle,
                            strings.privacyPolicyDescription,
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.PRIVACY_POLICY.infoId) }
                        ),
                        SettingsRowItem(
                            Icons.Outlined.Description,
                            strings.termsTitle,
                            strings.termsDescription,
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.TERMS_OF_SERVICE.infoId) }
                        ),
                        // 데이터출처는 아직 별도 페이지 없이 설명 텍스트로만 두고, 분기가 없으니 화살표도 뺀다.
                        SettingsRowItem(
                            Icons.Outlined.Public,
                            strings.dataSourceTitle,
                            strings.dataSourceDescription,
                            onClick = null
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = strings.appInfoSectionTitle, style = SettingsSectionTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(12.dp))
            AppInfoCard(strings = strings, onClearCacheConfirmed = onClearCacheConfirmed)

            Spacer(modifier = Modifier.height(24.dp))
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
                val strings = LocalAppStrings.current.settings
                Column {
                    Text(text = strings.languageChangeTitle, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.languageChangeDescription,
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
private fun AppInfoCard(strings: SettingsStrings, onClearCacheConfirmed: () -> Unit) {
    var showClearCacheDialog by remember { mutableStateOf(false) }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            icon = {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(CoralPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(24.dp))
                }
            },
            title = {
                Text(
                    text = strings.clearCacheDialogTitle,
                    style = SettingsItemTitleStyle.copy(fontSize = 17.sp),
                    color = SettingsPrimaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = strings.clearCacheDialogBody,
                    style = SettingsDescriptionStyle,
                    color = SettingsSecondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            showClearCacheDialog = false
                            onClearCacheConfirmed()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = strings.deleteButton)
                    }
                    OutlinedButton(
                        onClick = { showClearCacheDialog = false },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, SettingsBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SettingsSecondaryText),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = strings.cancelButton)
                    }
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
                contentDescription = LocalAppStrings.current.common.logoContentDescription,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = strings.appName, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = strings.versionLabelFormat.format(BuildConfig.VERSION_NAME),
                    style = SettingsDescriptionStyle,
                    color = SettingsSecondaryText
                )
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
                Text(text = strings.customerSupportTitle, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
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
            Text(text = strings.clearCacheRowTitle, style = SettingsItemTitleStyle, color = SettingsPrimaryText, modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = { showClearCacheDialog = true },
                modifier = Modifier.height(34.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CoralPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralPrimary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text(text = strings.clearCacheRowButton, style = MaterialTheme.typography.labelMedium)
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

// feature/languageselect의 LanguageOption 라벨(한국어/ENGLISH/日本語/中文)과 표기를 통일한다.
// 언어 설정 카드의 세그먼트 버튼 전용. 언어 코드(ko/en/ja/zh) 자체는 그대로 두고 화면에
// 보여주는 문자열만 맞춘다.
private fun String.toShortLabel(): String = when (this) {
    "ko" -> "한국어"
    "en" -> "ENGLISH"
    "ja" -> "日本語"
    "zh" -> "中文"
    else -> this.uppercase()
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    MediInBusanTheme {
        SettingsContent(
            uiState = SettingsUiState(selectedLanguage = "ko"),
            onLanguageSelected = {},
            onNavigateToInfoDetail = {},
            onNavigateToNotificationSettings = {},
            onNavigateToFavoriteManage = {},
            onNavigateToRecentlyViewed = {},
            onClearCacheConfirmed = {}
        )
    }
}
