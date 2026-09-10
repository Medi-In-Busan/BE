package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * 혼잡도 4단계(매우 높음/높음/보통/여유) 배지. 예전엔 wellness_verybusy/busyhigh/busymiddle/busychill
 * 4종 이미지에 "혼잡도 매우 높음" 같은 문구가 통째로 그려져 있어 언어를 바꿔도 한국어 그대로
 * 남았다 — 배경/아이콘/문구를 전부 Compose로 직접 그려서 문구가 LocalAppStrings를 따라가게 한다.
 * NearbyScreen(웰니스 핫플레이스)과 TourismCatalogScreen/TourismHubScreen(부산 핫플레이스)이
 * 같은 배지를 공유한다 — core/ui에 둔 이유.
 */
@Composable
fun CongestionLevelBadge(congestionRate: Double, height: Dp, modifier: Modifier = Modifier) {
    val strings = LocalAppStrings.current.nearby
    val (accentColor, emoji, levelLabel) = when {
        congestionRate >= 80.0 -> Triple(Color(0xFFE53935), "🔥", strings.crowdingVeryHigh)
        congestionRate >= 60.0 -> Triple(Color(0xFFFF8F1F), "🔥", strings.crowdingHigh)
        congestionRate >= 40.0 -> Triple(Color(0xFF43A047), "🍃", strings.crowdingNormal)
        else -> Triple(Color(0xFF42A5F5), "💨", strings.crowdingRelaxed)
    }
    // 알파 투명 배경은 이 배지가 사진 위에 얹힐 때(핫플레이스 카드) 뒤 이미지가 비쳐 보이는 문제가
    // 있어, 흰색과 accentColor를 미리 섞은 불투명 파스텔 색을 쓴다 — 어떤 배경 위에서도 같아 보인다.
    val fillColor = lerp(Color.White, accentColor, 0.16f)
    val density = LocalDensity.current
    Row(
        modifier = modifier
            .height(height)
            .clip(CircleShape)
            .background(fillColor)
            .padding(horizontal = height * 0.3f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(height * 0.14f)
    ) {
        Text(text = emoji, fontSize = with(density) { (height * 0.5f).toSp() })
        Text(
            text = strings.crowdingLabelFormat.format(levelLabel),
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = with(density) { (height * 0.4f).toSp() },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
