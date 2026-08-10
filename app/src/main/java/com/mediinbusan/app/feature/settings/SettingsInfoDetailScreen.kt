package com.mediinbusan.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediinbusan.app.core.i18n.InfoContentText
import com.mediinbusan.app.core.i18n.InfoSectionText
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.SettingsInfoDetailStrings
import com.mediinbusan.app.core.i18n.SettingsStrings
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.InfoBackgroundBlue
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsDivider
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.SkyBlue

/** Settings(S-10) "정보" 섹션의 정적 텍스트 하위 화면 3종. 데이터출처는 분기 없이 리스트에 설명으로만 남아 여기 포함되지 않는다. */
enum class SettingsInfoType(val infoId: String, val icon: ImageVector) {
    USAGE_GUIDE("usage_guide", Icons.Outlined.Info),
    PRIVACY_POLICY("privacy_policy", Icons.Outlined.PrivacyTip),
    TERMS_OF_SERVICE("terms", Icons.Outlined.Description);

    companion object {
        fun fromId(infoId: String): SettingsInfoType = entries.firstOrNull { it.infoId == infoId } ?: USAGE_GUIDE
    }
}

private fun titleFor(type: SettingsInfoType, settings: SettingsStrings): String = when (type) {
    SettingsInfoType.USAGE_GUIDE -> settings.usageGuideTitle
    SettingsInfoType.PRIVACY_POLICY -> settings.privacyPolicyTitle
    SettingsInfoType.TERMS_OF_SERVICE -> settings.termsTitle
}

private fun contentFor(type: SettingsInfoType, strings: SettingsInfoDetailStrings): InfoContentText = when (type) {
    SettingsInfoType.USAGE_GUIDE -> strings.usageGuide
    SettingsInfoType.PRIVACY_POLICY -> strings.privacyPolicy
    SettingsInfoType.TERMS_OF_SERVICE -> strings.terms
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsInfoDetailScreen(infoId: String, onBack: () -> Unit) {
    val type = SettingsInfoType.fromId(infoId)
    val appStrings = LocalAppStrings.current
    val title = titleFor(type, appStrings.settings)
    val content = contentFor(type, appStrings.settingsInfoDetail)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = appStrings.common.backContentDescription)
                    }
                },
                title = { Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
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
            InfoHeader(type = type, title = title)

            Spacer(modifier = Modifier.height(16.dp))
            DraftNoticeCard(text = appStrings.settingsInfoDetail.draftNotice)

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = content.intro,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = SettingsPrimaryText
            )

            Spacer(modifier = Modifier.height(20.dp))
            content.sections.forEachIndexed { index, section ->
                InfoSectionCard(section = section)
                if (index != content.sections.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoHeader(type: SettingsInfoType, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(CoralPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = type.icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = SettingsPrimaryText)
    }
}

@Composable
private fun DraftNoticeCard(text: String) {
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
            text = text,
            style = SettingsDescriptionStyle,
            color = SettingsSecondaryText
        )
    }
}

@Composable
private fun InfoSectionCard(section: InfoSectionText) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(CoralPrimary)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = section.heading, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = section.body,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
            color = SettingsSecondaryText
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsInfoDetailScreenPreview() {
    MediInBusanTheme {
        SettingsInfoDetailScreen(infoId = "usage_guide", onBack = {})
    }
}
