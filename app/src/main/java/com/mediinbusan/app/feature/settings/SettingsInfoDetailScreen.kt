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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText

/** Settings(S-10) "정보" 섹션의 정적 텍스트 하위 화면 3종. 데이터출처는 분기 없이 리스트에 설명으로만 남아 여기 포함되지 않는다. */
enum class SettingsInfoType(val infoId: String) {
    USAGE_GUIDE("usage_guide"),
    PRIVACY_POLICY("privacy_policy"),
    TERMS_OF_SERVICE("terms");

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

@Composable
fun SettingsInfoDetailScreen(infoId: String, onBack: () -> Unit) {
    val type = SettingsInfoType.fromId(infoId)
    val appStrings = LocalAppStrings.current
    val title = titleFor(type, appStrings.settings)
    val content = contentFor(type, appStrings.settingsInfoDetail)

    // Settings와 동일한 톤: 공용 탑바 없이, 배경은 Home과 같은 HomeBackgroundPink, 뒤로가기는
    // 원형 배경 없는 화살표 아이콘을 콘텐츠 맨 위에 직접 배치한다.
    Scaffold(containerColor = HomeBackgroundPink) { innerPadding ->
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
                    contentDescription = appStrings.common.backContentDescription,
                    tint = SettingsPrimaryText
                )
            }
            // 아이콘-타이틀 간격도 원래(24dp)의 절반으로 줄인다.
            Spacer(modifier = Modifier.height(12.dp))
            // 아이콘/파란 안내 영역/소개 문단을 전부 빼고 타이틀(좌측 정렬)과 섹션 카드만 남긴다.
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
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
private fun InfoSectionCard(section: InfoSectionText) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // 의료기관 리스트 유닛카드/설정 카드와 같은 톤 — 옅은 분홍 배경 위에서도 확실히 보이게.
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
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
