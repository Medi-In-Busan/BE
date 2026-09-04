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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
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
import com.mediinbusan.app.core.ui.BrandSnackbarHost

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToInfoDetail: (String) -> Unit,
    onNavigateToNotificationSettings: () -> Unit,
    onNavigateToFavoriteManage: () -> Unit,
    onNavigateToRecentlyViewed: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onBack = onBack,
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
    onBack: () -> Unit,
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
        // 탑바(로고+언어+설정 톱니)와 하단 탭바를 없애고 일반 push 화면(뒤로가기 버튼)으로 바꿨다 —
        // 탑바가 사라진 만큼 "설정" 타이틀이 상태바 바로 아래까지 올라와 윗여백을 채운다. 하단 탭바도
        // 안 보이므로(MediInBusanApp.kt shouldShowBottomBar에서 Settings 제거) BottomNavBarHeight
        // 보정도 더는 필요 없다.
        containerColor = HomeBackgroundPink,
        snackbarHost = { BrandSnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                // 상태바 인셋을 원래의 절반만 먹여서 뒤로가기 아이콘을 원래 위치에서 절반 정도 위로 당긴다.
                .padding(
                    top = innerPadding.calculateTopPadding() * 0.5f,
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = LocalAppStrings.current.common.backContentDescription,
                    tint = SettingsPrimaryText
                )
            }
            // 아이콘-타이틀 간격도 원래(24dp)의 절반으로 줄인다.
            Spacer(modifier = Modifier.height(12.dp))
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
                            R.drawable.setting_notification,
                            strings.notificationTitle,
                            strings.notificationDescription,
                            onClick = onNavigateToNotificationSettings
                        ),
                        SettingsRowItem(
                            R.drawable.setting_favorite,
                            strings.favoriteManageTitle,
                            strings.favoriteManageDescription,
                            onClick = onNavigateToFavoriteManage
                        ),
                        SettingsRowItem(
                            R.drawable.setting_recently,
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
                            R.drawable.setting_guide,
                            strings.usageGuideTitle,
                            strings.usageGuideDescription,
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.USAGE_GUIDE.infoId) }
                        ),
                        SettingsRowItem(
                            R.drawable.setting_privacy,
                            strings.privacyPolicyTitle,
                            strings.privacyPolicyDescription,
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.PRIVACY_POLICY.infoId) }
                        ),
                        SettingsRowItem(
                            R.drawable.setting_condition,
                            strings.termsTitle,
                            strings.termsDescription,
                            onClick = { onNavigateToInfoDetail(SettingsInfoType.TERMS_OF_SERVICE.infoId) }
                        ),
                        // 데이터출처는 아직 별도 페이지 없이 설명 텍스트로만 두고, 분기가 없으니 화살표도 뺀다.
                        SettingsRowItem(
                            R.drawable.setting_datainfo,
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
                RowIconImage(imageRes = R.drawable.home_languege)
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
    val iconRes: Int,
    val title: String,
    val description: String,
    val onClick: (() -> Unit)? = {}
)

@Composable
private fun SettingsRows(items: List<SettingsRowItem>) {
    items.forEachIndexed { index, item ->
        SettingsRow(iconRes = item.iconRes, title = item.title, description = item.description, onClick = item.onClick)
        if (index != items.lastIndex) {
            HorizontalDivider(color = SettingsDivider, modifier = Modifier.padding(horizontal = 20.dp))
        }
    }
}

@Composable
private fun SettingsRow(
    iconRes: Int,
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
        RowIconImage(imageRes = iconRes)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(3.dp))
            // 데이터출처 설명이 길어도 줄바꿈 없이 한 줄로 잘리게(ellipsis) — 다른 행은 원래 짧아 영향 없다.
            Text(
                text = description,
                style = SettingsDescriptionStyle,
                color = SettingsSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
            // 분기용 화살표(">") 대신 캐시삭제 버튼과 같은 양식의 "업데이트" 버튼. 배포 전이라
            // 아직 실제 업데이트 라우팅은 걸지 않는다(onClick 비워둠).
            Button(
                onClick = {},
                modifier = Modifier.height(34.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text(text = strings.appUpdateButton, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
            RowIconImage(imageRes = R.drawable.setting_qa)
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
            RowIconImage(imageRes = R.drawable.setting_delete)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = strings.clearCacheRowTitle, style = SettingsItemTitleStyle, color = SettingsPrimaryText, modifier = Modifier.weight(1f))
            Button(
                onClick = { showClearCacheDialog = true },
                modifier = Modifier.height(34.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text(text = strings.clearCacheRowButton, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Home 카테고리 원(CategoryCircleItem)과 같은 톤 — 흰 배경 + 경계선 원 안에 이미지.
// 크기(40dp 원)는 기존에 쓰던 코랄톤 원형 아이콘과 동일하게 유지한다.
@Composable
private fun RowIconImage(imageRes: Int) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // 의료기관 리스트 유닛 카드(SearchResultCard)와 같은 톤 — 옅은 분홍 배경 위에서도
            // 카드 경계가 확실히 보이도록 진하게 준다.
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White),
        content = content
    )
}

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
            onBack = {},
            onLanguageSelected = {},
            onNavigateToInfoDetail = {},
            onNavigateToNotificationSettings = {},
            onNavigateToFavoriteManage = {},
            onNavigateToRecentlyViewed = {},
            onClearCacheConfirmed = {}
        )
    }
}
