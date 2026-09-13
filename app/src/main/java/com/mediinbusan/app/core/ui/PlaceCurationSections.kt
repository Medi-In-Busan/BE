package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.common.ActivityLevel
import com.mediinbusan.app.core.common.PlaceCareProfile
import com.mediinbusan.app.core.common.PlaceCautionKey
import com.mediinbusan.app.core.common.PlaceSetting
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.designsystem.TourismAccentPink
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
 * 네 칸은 한 줄에 다 들어가는 하나의 회색 카드를 4등분한 것이다 — 예전엔 칸마다 독립된 pill이라
 * 값이 조금만 길어도(예: "회복 2~3일 후") 4개가 한 줄에 안 맞고 다음 줄로 밀렸다. 폭을 4등분
 * (weight(1f))으로 고정해 항상 한 줄을 유지하고, 칸 사이는 가는 세로선으로만 구분한다.
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f),
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(GlancePillSurface)
        ) {
            GlanceColumn(
                icon = Icons.Default.EventAvailable,
                label = strings.recoveryFitLabel,
                value = strings.recoveryFitLabels[profile.recoveryFit.name].orEmpty(),
                modifier = Modifier.weight(1f)
            )
            GlanceDivider()
            GlanceColumn(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                label = strings.activityLevelLabel,
                value = strings.activityLevelLabels[profile.activityLevel.name].orEmpty(),
                modifier = Modifier.weight(1f)
            )
            GlanceDivider()
            GlanceColumn(
                icon = profile.setting.icon(),
                label = strings.settingLabel,
                value = strings.settingLabels[profile.setting.name].orEmpty(),
                modifier = Modifier.weight(1f)
            )
            GlanceDivider()
            // 마지막 칸은 오른쪽 끝에 붙여(alignEnd) 줄 전체가 좌우로 여백 없이 꽉 차 보이게 한다
            // — "60~120분"처럼 짧은 값도 왼쪽 정렬로 두면 칸 가운데에 어정쩡하게 뜬다.
            GlanceColumn(
                icon = Icons.Default.Schedule,
                label = strings.stayTimeLabel,
                value = stayValue,
                modifier = Modifier.weight(1f),
                alignEnd = true
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

// 아이콘+라벨 줄은 네 칸 모두 왼쪽 정렬로 같다 — 값 줄만 맨 오른쪽 칸([alignEnd])에서 오른쪽
// 끝에 붙는다("60~120분"처럼 짧은 값이 칸 가운데 어중간하게 뜨는 대신, 칸 전체 폭을 한 줄
// 확보하는 데 쓸 수 있다). 나머지 칸의 값은 라벨과 같은 시작선(아이콘 폭만큼 들여쓰기)에 맞춘다.
@Composable
private fun GlanceColumn(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false
) {
    Column(modifier = modifier.padding(horizontal = 6.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = TourismAccentPink, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = if (alignEnd) 1 else 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
            // 나머지 칸은 아이콘(14dp) + 간격(4dp)만큼 들여써 라벨의 시작선과 맞춘다. 오른쪽 정렬
            // 칸은 대신 폭을 전부 차지해야 textAlign.End가 실제로 오른쪽 끝에 붙는다.
            modifier = if (alignEnd) Modifier.fillMaxWidth() else Modifier.padding(start = 18.dp)
        )
    }
}

@Composable
private fun GlanceDivider() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(DividerColor)
    )
}

// pill 바탕. 흰 카드 위에 살짝 눌러 앉은 중립 회색 — 색으로 강조하는 대신 면으로만 묶는다.
// 그림자를 진하게 올린 뒤로 원래 값(#F4F4F7)은 그림자와 맞물려 더 진해 보여서 한 톤 더 밝혔다.
private val GlancePillSurface = Color(0xFFF7F7F9)

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
 * 유형별 주의 항목 목록. "진료 전후 체크" 섹션의 본문이다. 최대 3개(core/common/PlaceCareProfile
 * 데이터가 항목마다 1~3개를 준다)라 가로로 나란히 두는 열이 항상 자연스럽게 채워진다 — 세로로
 * 쌓던 예전 목록보다 한눈에 훑기 좋다. 어떤 항목도 의료 자문이 아니며, 호출부가 면책 문구를 붙인다.
 */
@Composable
fun CautionList(
    cautions: List<PlaceCautionKey>,
    modifier: Modifier = Modifier
) {
    if (cautions.isEmpty()) return
    val labels = LocalAppStrings.current.placeCuration.cautionLabels
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        cautions.forEach { caution ->
            val label = labels[caution.name] ?: return@forEach
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(TourismAccentPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            }
        }
    }
}

/**
 * "여행자 편의" 카드 내용 — 공공 안내 번호 두 개를 나란한 박스 두 개로 보여주고, 그 아래
 * 결제·이동 각주 한 줄을 남긴다.
 *
 * 앱이 상담이나 통역사를 연결하는 게 아니라 **공개된 번호를 안내만** 한다(CLAUDE.md §1 MVP
 * 하드 제약: 실시간 상담/통역사 매칭 없음). 박스를 누르면 [onDial]로 기기 다이얼러만 연다.
 *
 * 결제·이동은 두 박스와 생김새가 달라야 "이건 안 눌린다"가 바로 전달된다 — 카드 맨 아래 각주로
 * 위계를 낮춘 채로 둔다.
 */
@Composable
fun TravelerHelpContent(
    onDial: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.placeCuration
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TravelerHelpCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.SupportAgent,
                title = strings.travelerHelpTourLineLabel,
                description = strings.travelerHelpTourLineDescription,
                number = strings.travelerHelpTourLineNumber,
                onCall = { onDial(strings.travelerHelpTourLineNumber) }
            )
            TravelerHelpCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalHospital,
                title = strings.travelerHelpEmergencyLabel,
                description = strings.travelerHelpEmergencyDescription,
                number = strings.travelerHelpEmergencyNumber,
                onCall = { onDial(strings.travelerHelpEmergencyNumber) }
            )
        }
        Text(
            text = strings.travelerHelpPaymentNote,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

/**
 * 안내 번호 박스 하나 — 아이콘+제목 -> 설명 -> 번호를 세로로 쌓는다. 처음엔 번호를 오른쪽에
 * 따로 두는 가로 배치였는데, 박스 폭 안에서 넷을 한 줄에 욱여넣으니 설명 문구가 잘렸다.
 */
@Composable
private fun TravelerHelpCard(
    icon: ImageVector,
    title: String,
    description: String,
    number: String,
    onCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(GlancePillSurface)
            .clickable(onClick = onCall)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = TourismAccentPink, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        // 설명·번호는 제목 글자 시작선(24dp)보다 살짝 왼쪽(14dp)으로 뺐다 — 정확히 맞추면 폭이
        // 좁아져 설명이 말줄임(…)으로 잘렸다. 완전히 안 맞느니 살짝 어긋나더라도 안 잘리는 쪽.
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 14.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.padding(start = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = null,
                tint = TourismAccentPink,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = number,
                style = MaterialTheme.typography.labelLarge,
                color = TourismAccentPink,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
