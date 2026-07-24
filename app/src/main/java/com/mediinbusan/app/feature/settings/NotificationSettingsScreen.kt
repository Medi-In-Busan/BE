package com.mediinbusan.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.InfoBackgroundBlue
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsDivider
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.SettingsTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.ui.BrandBackTopAppBar

@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    NotificationSettingsContent(
        notificationsEnabled = uiState.notificationsEnabled,
        selectedLanguage = uiState.selectedLanguage,
        onBack = onBack,
        onToggle = viewModel::onToggleNotifications,
        onLanguageSelected = viewModel::onLanguageSelected
    )
}

@Composable
private fun NotificationSettingsContent(
    notificationsEnabled: Boolean,
    selectedLanguage: String,
    onBack: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    Scaffold(
        topBar = {
            BrandBackTopAppBar(
                onBack = onBack,
                currentLanguageCode = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "알림 설정", style = SettingsTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(20.dp))
            NotificationCard(enabled = notificationsEnabled, onToggle = onToggle)

            Spacer(modifier = Modifier.height(16.dp))
            NoticeCard()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NotificationCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
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
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(CoralPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Outlined.Campaign, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "이벤트 및 소식 알림", style = SettingsItemTitleStyle, color = SettingsPrimaryText)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "새로운 소식과 이벤트 알림을 받아보세요",
                    style = SettingsDescriptionStyle,
                    color = SettingsSecondaryText
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedTrackColor = CoralPrimary)
            )
        }
        HorizontalDivider(color = SettingsDivider, modifier = Modifier.padding(horizontal = 22.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 14.dp)) {
            Text(
                text = if (enabled) "현재 알림이 켜져 있어요." else "현재 알림이 꺼져 있어요.",
                style = SettingsDescriptionStyle,
                color = if (enabled) CoralPrimary else SettingsSecondaryText
            )
        }
    }
}

@Composable
private fun NoticeCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InfoBackgroundBlue)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(22.dp).clip(CircleShape).background(SkyBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Outlined.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "실제 알림 발송 기능은 준비 중이에요. 여기서 설정한 값은 추후 알림 기능이 제공될 때 그대로 반영됩니다.",
            style = SettingsDescriptionStyle,
            color = SettingsSecondaryText
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationSettingsContentPreview() {
    MediInBusanTheme {
        NotificationSettingsContent(
            notificationsEnabled = true,
            selectedLanguage = "ko",
            onBack = {},
            onToggle = {},
            onLanguageSelected = {}
        )
    }
}
