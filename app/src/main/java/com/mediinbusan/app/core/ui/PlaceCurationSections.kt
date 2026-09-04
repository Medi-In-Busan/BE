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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Schedule
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
 * [accent]는 호출 화면의 액센트 색이다(장소 상세는 지도 마커 색, 관광 상세는 코랄).
 */

/**
 * "한눈에 보기" — 방문 시기·활동 강도·환경·권장 체류를 네 개의 pill로 보여준다.
 *
 * [PlaceCareProfile]이 모든 PlaceType을 덮으므로 이 줄은 **어떤 장소에서도 항상 채워진다**.
 * 전화·거리·소개가 전부 비어 상세 화면이 텅 비던 문제의 1차 방어선이다.
 *
 * 줄바꿈은 [WrapRow]로 한다 — FlowRow는 이 프로젝트에서 실기기 크래시를 낸다(CLAUDE.md §6-6).
 */
@Composable
fun AtAGlanceRow(
    profile: PlaceCareProfile,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.placeCuration
    val stayValue = if (profile.stayMinutesMax <= 0) {
        strings.stayTimeFlexible
    } else {
        strings.stayTimeRangeFormat.format(profile.stayMinutesMin, profile.stayMinutesMax)
    }
    WrapRow(modifier = modifier, horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
        GlancePill(
            icon = Icons.Default.EventAvailable,
            label = strings.recoveryFitLabel,
            value = strings.recoveryFitLabels[profile.recoveryFit.name].orEmpty(),
            accent = accent
        )
        GlancePill(
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            label = strings.activityLevelLabel,
            value = strings.activityLevelLabels[profile.activityLevel.name].orEmpty(),
            accent = accent
        )
        GlancePill(
            icon = profile.setting.icon(),
            label = strings.settingLabel,
            value = strings.settingLabels[profile.setting.name].orEmpty(),
            accent = accent
        )
        GlancePill(
            icon = Icons.Default.Schedule,
            label = strings.stayTimeLabel,
            value = stayValue,
            accent = accent
        )
    }
}

@Composable
private fun GlancePill(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = 0.09f))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
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
    accent: Color,
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
                    tint = accent,
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
    accent: Color,
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
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            }
        }
    }
}

/**
 * "여행자 편의" 카드 내용 — 공공 안내 번호와 결제·이동 안내.
 *
 * 앱이 상담이나 통역사를 연결하는 게 아니라 **공개된 번호를 안내만** 한다(CLAUDE.md §1 MVP
 * 하드 제약: 실시간 상담/통역사 매칭 없음). 번호 행을 누르면 [onDial]로 기기 다이얼러만 연다.
 */
@Composable
fun TravelerHelpContent(
    accent: Color,
    onDial: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.placeCuration
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TravelerHelpRow(
            icon = Icons.Default.Call,
            title = strings.travelerHelpTourLineLabel,
            description = strings.travelerHelpTourLineDescription,
            accent = accent,
            onClick = { onDial(strings.travelerHelpTourLineNumber) }
        )
        TravelerHelpRow(
            icon = Icons.Default.LocalHospital,
            title = strings.travelerHelpEmergencyLabel,
            description = strings.travelerHelpEmergencyDescription,
            accent = accent,
            onClick = { onDial(strings.travelerHelpEmergencyNumber) }
        )
        TravelerHelpRow(
            icon = Icons.Default.CreditCard,
            title = strings.travelerHelpPaymentLabel,
            description = strings.travelerHelpPaymentDescription,
            accent = accent,
            onClick = null
        )
    }
}

@Composable
private fun TravelerHelpRow(
    icon: ImageVector,
    title: String,
    description: String,
    accent: Color,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                // 누를 수 있는 행(전화번호)만 액센트 색을 준다 — 누를 수 없는 안내 행까지 같은
                // 색이면 전부 탭 가능한 것처럼 보인다.
                color = if (onClick != null) accent else TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}
