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
enum class SettingsInfoType(val infoId: String, val title: String, val icon: ImageVector) {
    USAGE_GUIDE("usage_guide", "이용 안내", Icons.Outlined.Info),
    PRIVACY_POLICY("privacy_policy", "개인정보 처리방침", Icons.Outlined.PrivacyTip),
    TERMS_OF_SERVICE("terms", "이용약관", Icons.Outlined.Description);

    companion object {
        fun fromId(infoId: String): SettingsInfoType = entries.firstOrNull { it.infoId == infoId } ?: USAGE_GUIDE
    }
}

private data class InfoSection(val heading: String, val body: String)
private data class InfoContent(val intro: String, val sections: List<InfoSection>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsInfoDetailScreen(infoId: String, onBack: () -> Unit) {
    val type = SettingsInfoType.fromId(infoId)
    val content = contentFor(type)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = { Text(text = type.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
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
            InfoHeader(type = type)

            Spacer(modifier = Modifier.height(16.dp))
            DraftNoticeCard()

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
private fun InfoHeader(type: SettingsInfoType) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(CoralPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = type.icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = type.title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = SettingsPrimaryText)
    }
}

@Composable
private fun DraftNoticeCard() {
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
            text = "본 내용은 정식 서비스 오픈 전 작성된 초안이며, 정식 법률 검토를 거쳐 대체될 수 있습니다.",
            style = SettingsDescriptionStyle,
            color = SettingsSecondaryText
        )
    }
}

@Composable
private fun InfoSectionCard(section: InfoSection) {
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

private fun contentFor(type: SettingsInfoType): InfoContent = when (type) {
    SettingsInfoType.USAGE_GUIDE -> InfoContent(
        intro = "메디인부산(MediIn Busan)은 부산을 방문하는 외국인 의료관광객이 자신의 의료 목적에 맞는 부산 의료기관을 탐색하고, 의료 이용 절차와 병원 주변 관광·웰니스 정보를 함께 확인할 수 있도록 돕는 정보 제공형 서비스입니다.",
        sections = listOf(
            InfoSection(
                "1. 의료기관 찾기",
                "홈 화면에서 의료 목적(피부·미용, 건강검진, 치과, 한방, 재활 등)을 선택하면 관련 의료기관 목록을 볼 수 있습니다. 목록에서 병원을 선택하면 위치, 진료과목, 지원 언어, 이용 가능 시간 등 상세 정보를 확인할 수 있습니다."
            ),
            InfoSection(
                "2. 의료 이용 가이드",
                "의료기관 상세 화면에서 '의료 이용 가이드'를 통해 예약 문의부터 진료, 귀국까지 일반적인 이용 절차 안내를 확인할 수 있습니다."
            ),
            InfoSection(
                "3. 주변 관광·웰니스 정보",
                "의료기관 주변의 관광지, 음식점, 웰니스 장소 정보를 함께 확인하여 의료 목적 외 일정도 계획할 수 있습니다."
            ),
            InfoSection(
                "4. 즐겨찾기",
                "관심 있는 의료기관이나 장소를 즐겨찾기에 저장해 두고 나중에 다시 확인할 수 있습니다."
            ),
            InfoSection(
                "5. 제공하지 않는 기능",
                "메디인부산은 정보 제공에 집중하는 서비스로, 병원 예약 대행·진료비 결제·실시간 상담 또는 통역사 매칭·사용자의 실시간 위치를 이용한 위치기반 추천은 제공하지 않습니다.\n\n실제 진료 예약, 비용, 통역 서비스는 각 의료기관의 공식 채널을 통해 직접 문의해 주시기 바랍니다."
            )
        )
    )
    SettingsInfoType.PRIVACY_POLICY -> InfoContent(
        intro = "메디인부산(이하 '서비스')은 이용자의 개인정보를 소중히 다루며, 관련 법령을 준수하기 위해 노력합니다.",
        sections = listOf(
            InfoSection(
                "수집하는 개인정보 항목",
                "서비스는 회원가입 절차 없이 이용 가능하며, 선택한 언어 설정·의료 목적 카테고리·즐겨찾기 및 최근 본 항목 목록만을 기기 내부에 저장합니다. 위 정보는 이용자의 기기에만 저장되며, 서비스 서버로 전송되지 않습니다."
            ),
            InfoSection(
                "수집하지 않는 정보",
                "서비스는 이용자의 실시간 위치 정보(GPS), 결제 정보, 이름·연락처 등 본인 식별 정보를 수집하지 않습니다."
            ),
            InfoSection(
                "개인정보의 이용 목적",
                "저장된 언어·의료목적 설정은 다음 방문 시 동일한 환경을 제공하기 위한 목적으로만 사용됩니다."
            ),
            InfoSection(
                "개인정보의 보유 및 파기",
                "기기 내부에 저장된 정보는 이용자가 앱을 삭제하거나 설정에서 직접 초기화할 때까지 보관되며, 별도의 서버에 보관되지 않으므로 앱 삭제 시 함께 파기됩니다."
            ),
            InfoSection(
                "제3자 제공",
                "서비스는 이용자의 정보를 제3자에게 제공하지 않습니다. 다만 의료기관·관광 정보는 한국관광공사가 제공하는 공공데이터(OpenAPI)를 통해 조회됩니다."
            ),
            InfoSection(
                "문의처",
                "개인정보 관련 문의사항은 고객센터(support@medinbusan.kr)로 연락해 주시기 바랍니다."
            )
        )
    )
    SettingsInfoType.TERMS_OF_SERVICE -> InfoContent(
        intro = "이 약관은 메디인부산(이하 '서비스')이 제공하는 정보 제공 서비스의 이용과 관련하여 서비스와 이용자 간의 권리, 의무 및 책임사항을 정함을 목적으로 합니다.",
        sections = listOf(
            InfoSection(
                "제1조 (서비스의 내용)",
                "서비스는 부산 지역 의료기관 및 주변 관광·웰니스 정보를 제공하는 정보 제공형 서비스입니다. 서비스는 의료 자문, 진단 또는 처방을 제공하지 않으며, 병원 예약 대행이나 결제 기능을 포함하지 않습니다."
            ),
            InfoSection(
                "제2조 (이용자의 의무)",
                "이용자는 서비스에서 제공하는 정보를 참고 자료로만 활용해야 하며, 실제 의료기관 방문 전 반드시 해당 기관의 공식 채널을 통해 최신 정보를 확인해야 합니다."
            ),
            InfoSection(
                "제3조 (면책조항)",
                "서비스가 제공하는 의료기관·관광 정보는 공공데이터를 기반으로 하며, 정보의 변경 시점에 따라 실제와 차이가 있을 수 있습니다. 서비스는 정보의 정확성을 위해 노력하지만, 정보의 최신성이나 완전성을 보증하지 않으며 이용자가 정보를 신뢰하여 발생한 손해에 대해 책임을 지지 않습니다."
            ),
            InfoSection(
                "제4조 (저작권)",
                "서비스에서 제공하는 콘텐츠에 대한 저작권은 서비스 또는 각 정보 제공기관(한국관광공사 등)에 있으며, 이용자는 이를 무단으로 복제, 배포할 수 없습니다."
            ),
            InfoSection(
                "제5조 (약관의 변경)",
                "서비스는 필요한 경우 관련 법령을 준수하는 범위 내에서 이 약관을 변경할 수 있으며, 변경된 약관은 서비스 내 공지를 통해 안내합니다."
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsInfoDetailScreenPreview() {
    MediInBusanTheme {
        SettingsInfoDetailScreen(infoId = "usage_guide", onBack = {})
    }
}
