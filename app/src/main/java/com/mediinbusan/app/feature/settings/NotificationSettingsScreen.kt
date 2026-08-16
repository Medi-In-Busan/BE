package com.mediinbusan.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.NotificationSettingsStrings
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.SettingsTitleStyle

@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    NotificationSettingsContent(
        notificationsEnabled = uiState.notificationsEnabled,
        onBack = onBack,
        onToggle = viewModel::onToggleNotifications
    )
}

@Composable
private fun NotificationSettingsContent(
    notificationsEnabled: Boolean,
    onBack: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    val appStrings = LocalAppStrings.current
    // Settings와 동일한 톤: 공용 탑바/하단 탭바 없이, 배경은 Home과 같은 HomeBackgroundPink,
    // 뒤로가기는 원형 배경 없는 화살표 아이콘을 타이틀 위에 직접 배치한다.
    Scaffold(containerColor = HomeBackgroundPink) { innerPadding ->
        Column(
            modifier = Modifier
                // 상태바 인셋을 원래의 절반만 먹여서 뒤로가기 아이콘을 원래 위치에서 절반 정도 위로 당긴다.
                .padding(
                    top = innerPadding.calculateTopPadding() * 0.5f,
                    bottom = innerPadding.calculateBottomPadding()
                )
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = appStrings.common.backContentDescription,
                    tint = SettingsPrimaryText
                )
            }
            // 아이콘-타이틀 간격도 원래(24dp)의 절반으로 줄인다.
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = appStrings.settings.notificationTitle, style = SettingsTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(20.dp))
            NotificationCard(enabled = notificationsEnabled, onToggle = onToggle, strings = appStrings.notificationSettings)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// 주 텍스트/서브텍스트/on-off 스위치만 남긴다 — 아이콘, 상단-하단 구분선, 하단 상태 텍스트,
// 파란 안내 카드(NoticeCard)는 전부 뺐다.
@Composable
private fun NotificationCard(enabled: Boolean, onToggle: (Boolean) -> Unit, strings: NotificationSettingsStrings) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // 의료기관 리스트 유닛카드/설정 카드와 같은 톤 — 옅은 분홍 배경 위에서도 확실히 보이게.
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // 사용자 요청으로 이 카드만 텍스트를 다른 화면 공용 스타일보다 적당히 크게 키운다.
            // 2배(28/24sp)는 서브텍스트가 두 줄로 줄바꿈돼서 너무 컸다는 피드백으로 축소하고,
            // maxLines=1 + ellipsis로 어떤 화면 폭에서도 줄바꿈 없이 한 줄을 보장한다.
            Text(
                text = strings.cardTitle,
                style = SettingsItemTitleStyle.copy(fontSize = 16.sp),
                color = SettingsPrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.cardDescription,
                style = SettingsDescriptionStyle.copy(fontSize = 13.sp),
                color = SettingsSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedTrackColor = CoralPrimary)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationSettingsContentPreview() {
    MediInBusanTheme {
        NotificationSettingsContent(
            notificationsEnabled = true,
            onBack = {},
            onToggle = {}
        )
    }
}
