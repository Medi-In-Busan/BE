package com.mediinbusan.app.feature.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsBorder
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.openAppPermissionSettings

/**
 * 앱 접근권한 사전 고지 화면 (정보통신망법 제22조의2 / 방송미디어통신위원회 "앱 접근권한 동의
 * 가이드라인"). 원스토어 검증 의견(OA01008717)에서 지적된 "접근권한 사전 고지·동의 절차 부재"에
 * 대한 대응이다.
 *
 * 가이드라인이 요구하는 네 가지를 한 화면에 담는다: ① 필수/선택 접근권한의 구분, ② 각 권한이
 * 필요한 구체적 기능과 목적, ③ 선택 권한은 동의하지 않아도 서비스 이용이 가능하다는 사실,
 * ④ 철회 방법과 그 경로.
 *
 * [fromSplash]가 true면 최초 실행 흐름이라 뒤로가기 없이 "확인했습니다"로만 빠져나간다(그때
 * 확인 사실을 저장해 다음 실행부터는 뜨지 않는다). false면 설정에서 다시 열어본 것이라
 * 일반 push 화면처럼 뒤로가기 버튼만 둔다.
 */
@Composable
fun AppPermissionNoticeScreen(
    fromSplash: Boolean,
    onAcknowledged: () -> Unit,
    onBack: () -> Unit,
    viewModel: AppPermissionNoticeViewModel = hiltViewModel()
) {
    AppPermissionNoticeContent(
        fromSplash = fromSplash,
        onConfirm = {
            viewModel.onAcknowledged()
            onAcknowledged()
        },
        onBack = onBack
    )
}

@Composable
private fun AppPermissionNoticeContent(
    fromSplash: Boolean,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val appStrings = LocalAppStrings.current
    val strings = appStrings.permissionNotice
    val context = LocalContext.current

    // Settings/정보 상세와 같은 톤(HomeBackgroundPink 배경 + 흰 카드). 이 화면은 최초 실행 때
    // 스플래시 다음에 바로 오므로 하단 탭바는 노출하지 않는다(MediInBusanApp.shouldShowBottomBar).
    Scaffold(containerColor = HomeBackgroundPink) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding() * 0.5f,
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth().height(32.dp)) {
                // 최초 실행 흐름에서는 고지를 건너뛰는 출구를 두지 않는다 — 확인 버튼이 유일한 진행 경로다.
                if (!fromSplash) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterStart).size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = appStrings.common.backContentDescription,
                            tint = SettingsPrimaryText
                        )
                    }
                }
                Text(
                    text = strings.screenTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SettingsPrimaryText,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(text = strings.intro, style = SettingsDescriptionStyle, color = SettingsSecondaryText)

            Spacer(modifier = Modifier.height(20.dp))
            NoticeCard(
                icon = Icons.Outlined.VerifiedUser,
                heading = strings.requiredSectionTitle,
                body = strings.requiredBody
            )

            Spacer(modifier = Modifier.height(14.dp))
            NoticeCard(
                icon = Icons.Outlined.PhotoCamera,
                heading = strings.optionalSectionTitle,
                body = strings.cameraBody,
                itemLabel = strings.cameraTitle,
                footnote = strings.optionalNotice
            )

            Spacer(modifier = Modifier.height(14.dp))
            NoticeCard(
                icon = Icons.Outlined.Settings,
                heading = strings.withdrawSectionTitle,
                body = strings.withdrawBody
            ) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = { context.openAppPermissionSettings() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralPrimary)
                ) {
                    Text(text = strings.openSystemSettingsButton, style = SettingsItemTitleStyle)
                }
            }

            if (fromSplash) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
                ) {
                    Text(text = strings.confirmButton, style = SettingsItemTitleStyle, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * 고지 항목 카드 하나. [itemLabel]이 있으면 본문 위에 권한 이름(예: 카메라)을 배지처럼 한 줄 얹고,
 * [footnote]는 "동의하지 않아도 이용 가능"처럼 본문과 구분해 강조해야 하는 문장에 쓴다.
 */
@Composable
private fun NoticeCard(
    icon: ImageVector,
    heading: String,
    body: String,
    itemLabel: String? = null,
    footnote: String? = null,
    extraContent: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                modifier = Modifier.size(36.dp).clip(CircleShape).background(CoralPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = heading, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
        }

        if (itemLabel != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = itemLabel,
                style = SettingsItemTitleStyle,
                color = CoralPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(CoralPrimaryContainer)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = body, style = SettingsDescriptionStyle, color = SettingsSecondaryText)

        if (footnote != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = footnote,
                style = SettingsDescriptionStyle,
                color = SettingsPrimaryText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HomeBackgroundPink)
                    .border(width = 1.dp, color = SettingsBorder, shape = RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }

        extraContent?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
private fun AppPermissionNoticeContentPreview() {
    MediInBusanTheme {
        AppPermissionNoticeContent(fromSplash = true, onConfirm = {}, onBack = {})
    }
}
