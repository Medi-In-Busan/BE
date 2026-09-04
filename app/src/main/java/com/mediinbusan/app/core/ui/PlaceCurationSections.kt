package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.common.ActivityLevel
import com.mediinbusan.app.core.common.PlaceCareProfile
import com.mediinbusan.app.core.common.PlaceCautionKey
import com.mediinbusan.app.core.common.PlaceSetting
import com.mediinbusan.app.core.designsystem.CoralInk
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.BusanHighlightCopy
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * 웰니스 장소 상세(feature/nearby)와 부산 관광 카탈로그 상세(feature/tourism)가 함께 쓰는
 * 큐레이션 섹션 콘텐츠.
 *
 * feature 패키지끼리는 서로 import할 수 없으므로(CLAUDE.md §4) 여기 core/ui에 둔다. 다만 두
 * 화면의 카드 껍데기가 서로 다르므로(전자는 흰 SectionCard, 후자는 DetailSurface) **껍데기는
 * 각 화면이 씌우고 여기서는 안에 들어갈 내용만** 제공한다 — 그래야 각 화면의 기존 톤이 유지된다.
 *
 * 색은 여기서 정한다 — 예전엔 호출 화면이 [accent]로 액센트 색을 넘겼는데, 장소 상세는 장소 종류
 * 색(파랑/주황)을, 관광 상세는 코랄을 넘겨서 같은 카드가 화면마다 다른 색으로 보였다. 이 안의
 * 내용은 장소 종류와 무관한 공통 안내(케어 프로필·공공 안내번호)라 앱 브랜드색(코랄)과 중립색으로
 * 고정한다. 장소 종류 색은 "이게 무슨 장소인가"를 말하는 자리(배지·히어로·섹션 아이콘)에만 남는다.
 */

/**
 * 방문 시기·활동 강도·환경·권장 체류를 네 개의 pill로 보여주고, 그 아래에 **출처 각주**를 붙인다.
 *
 * [PlaceCareProfile]이 모든 PlaceType을 덮으므로 이 줄은 **어떤 장소에서도 항상 채워진다**.
 * 전화·거리·소개가 전부 비어 상세 화면이 텅 비던 문제의 1차 방어선이다.
 *
 * ⚠️ 여기 값은 한국관광공사 TourAPI가 주는 데이터가 **아니다** — 장소 유형만 보고 앱이 정한
 * 참고 안내다(core/common/PlaceCareProfile.kt). 실제 API가 주는 건 이름·주소·좌표·사진·소개·
 * 전화·갱신일뿐이라, 공식 통계처럼 읽히면 곤란하다. 그래서 pill 바로 아래 각주
 * (PlaceCurationStrings.atAGlanceSourceNote)를 이 컴포저블이 직접 붙여, 이걸 쓰는 두 화면
 * (장소 상세·관광 카탈로그 상세) 어디서도 각주가 빠질 수 없게 한다.
 *
 * pill은 색을 쓰지 않는다 — 예전엔 장소 종류 색을 옅게 깔았는데, 네 개가 나란히 서면 화면에서
 * 색이 가장 튀는 덩어리가 되면서 정작 그 아래 진짜 내용보다 시선을 먼저 가져갔다.
 *
 * 줄바꿈은 [WrapRow]로 한다 — FlowRow는 이 프로젝트에서 실기기 크래시를 낸다(CLAUDE.md §6-6).
 */
@Composable
fun AtAGlanceRow(
    profile: PlaceCareProfile,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.placeCuration
    val stayValue = if (profile.stayMinutesMax <= 0) {
        strings.stayTimeFlexible
    } else {
        strings.stayTimeRangeFormat.format(profile.stayMinutesMin, profile.stayMinutesMax)
    }
    Column(modifier = modifier.fillMaxWidth()) {
        WrapRow(modifier = Modifier.fillMaxWidth(), horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
            GlancePill(
                icon = Icons.Default.EventAvailable,
                label = strings.recoveryFitLabel,
                value = strings.recoveryFitLabels[profile.recoveryFit.name].orEmpty()
            )
            GlancePill(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                label = strings.activityLevelLabel,
                value = strings.activityLevelLabels[profile.activityLevel.name].orEmpty()
            )
            GlancePill(
                icon = profile.setting.icon(),
                label = strings.settingLabel,
                value = strings.settingLabels[profile.setting.name].orEmpty()
            )
            GlancePill(
                icon = Icons.Default.Schedule,
                label = strings.stayTimeLabel,
                value = stayValue
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = strings.atAGlanceSourceNote,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun GlancePill(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(GlancePillSurface)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        // 라벨(무엇)을 값(얼마) 위 작은 글씨로 올린다 — 장소 상세의 BasicInfoRow와 같은 위계다.
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// pill 바탕. 흰 카드 위에 살짝 눌러 앉은 중립 회색 — 색으로 강조하는 대신 면으로만 묶는다.
private val GlancePillSurface = Color(0xFFF4F4F7)

private fun PlaceSetting.icon(): ImageVector = when (this) {
    PlaceSetting.INDOOR -> Icons.Default.MeetingRoom
    PlaceSetting.OUTDOOR -> Icons.Default.WbSunny
    PlaceSetting.MIXED -> Icons.Default.Landscape
}

/**
 * "메디인부산 팁" 카드 내용 — 진료 전후 관점의 큐레이션 한 단락과 추천 시간대.
 * 부산 대표 명소로 매칭된 장소에서만 호출한다(매칭이 없으면 카드 자체를 그리지 않는다).
 */
@Composable
fun MediTipContent(
    copy: BusanHighlightCopy,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.placeCuration
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = copy.tip, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        if (copy.bestTime.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = CoralPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.bestTimeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = copy.bestTime,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * 유형별 주의 항목 목록. "진료 전후 체크" 카드 안에서 기존 안내 문구 아래에 붙는다.
 * 어떤 항목도 의료 자문이 아니며, 카드에는 항상 면책 문구가 함께 남는다.
 */
@Composable
fun CautionList(
    cautions: List<PlaceCautionKey>,
    modifier: Modifier = Modifier
) {
    if (cautions.isEmpty()) return
    val labels = LocalAppStrings.current.placeCuration.cautionLabels
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        cautions.forEach { caution ->
            val label = labels[caution.name] ?: return@forEach
            Row(verticalAlignment = Alignment.Top) {
                // 아이콘 대신 작은 점을 쓴다 — 항목이 3개까지 이어지는데 경고 아이콘을 반복하면
                // 안내가 아니라 경고문 나열처럼 읽힌다.
                Box(
                    modifier = Modifier
                        .padding(top = 7.dp)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(CoralPrimary)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            }
        }
    }
}

/**
 * "여행자 편의" 카드 내용 — 공공 안내 번호 두 줄과, 그 아래 결제·이동 각주 한 줄.
 *
 * 앱이 상담이나 통역사를 연결하는 게 아니라 **공개된 번호를 안내만** 한다(CLAUDE.md §1 MVP
 * 하드 제약: 실시간 상담/통역사 매칭 없음). 번호 행을 누르면 [onDial]로 기기 다이얼러만 연다.
 *
 * 예전엔 1330·119와 "결제·이동"이 같은 모양의 세 줄이었다 — 앞의 둘만 눌리는데 생김새가 같아서
 * 셋 다 누를 수 있는 것처럼 보였다. 누르면 전화가 걸리는 두 줄만 번호를 드러낸 행으로 남기고,
 * 장소와 무관한 일반 안내인 결제·이동은 카드 맨 아래 각주로 위계를 낮췄다.
 */
@Composable
fun TravelerHelpContent(
    onDial: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.placeCuration
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TravelerHelpCallRow(
            icon = Icons.Default.SupportAgent,
            title = strings.travelerHelpTourLineLabel,
            number = strings.travelerHelpTourLineNumber,
            description = strings.travelerHelpTourLineDescription,
            onCall = { onDial(strings.travelerHelpTourLineNumber) }
        )
        TravelerHelpCallRow(
            icon = Icons.Default.LocalHospital,
            title = strings.travelerHelpEmergencyLabel,
            number = strings.travelerHelpEmergencyNumber,
            description = strings.travelerHelpEmergencyDescription,
            onCall = { onDial(strings.travelerHelpEmergencyNumber) }
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = strings.travelerHelpPaymentNote,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

/**
 * 안내 번호 한 줄. 오른쪽 끝에 번호와 수화기 아이콘을 담은 알약을 둬서, 이 행이 "읽는 정보"가
 * 아니라 "누르면 전화가 걸리는 것"임을 생김새만으로 알 수 있게 한다.
 */
@Composable
private fun TravelerHelpCallRow(
    icon: ImageVector,
    title: String,
    number: String,
    description: String,
    onCall: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onCall)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(CoralPrimary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(CoralPrimary.copy(alpha = 0.10f))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 옅은 코랄 알약 위에 얹히는 글자·아이콘이라 면 색(CoralPrimary)이 아니라 글자용
            // 짝(CoralInk)을 쓴다 — 같은 색으로 두면 번호가 배경에 묻힌다.
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = null,
                tint = CoralInk,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = number,
                style = MaterialTheme.typography.labelLarge,
                color = CoralInk,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
