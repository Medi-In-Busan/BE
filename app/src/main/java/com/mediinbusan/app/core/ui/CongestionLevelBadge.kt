package com.mediinbusan.app.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * 혼잡도 4단계(매우 높음/높음/보통/여유) 배지. wellness_verybusy/busyhigh/busymiddle/busychill
 * 4종 이미지가 이미 "혼잡도 %s" 문구+아이콘까지 다 포함하고 있어서, 별도 Surface+Text 배지를 그리지
 * 않고 이 이미지를 그대로 쓴다. NearbyScreen(웰니스 핫플레이스)과 TourismCatalogScreen/
 * TourismHubScreen(부산 핫플레이스)이 같은 배지를 공유한다 — core/ui에 둔 이유.
 */
@Composable
fun CongestionLevelBadge(congestionRate: Double, height: Dp, modifier: Modifier = Modifier) {
    val iconRes = when {
        congestionRate >= 80.0 -> R.drawable.wellness_verybusy
        congestionRate >= 60.0 -> R.drawable.wellness_busyhigh
        congestionRate >= 40.0 -> R.drawable.wellness_busymiddle
        else -> R.drawable.wellness_busychill
    }
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = LocalAppStrings.current.nearby.crowdingLabelFormat.format(congestionRate.toCongestionLevelLabel()),
        modifier = modifier.height(height)
    )
}

@Composable
private fun Double.toCongestionLevelLabel(): String = when {
    this >= 80.0 -> LocalAppStrings.current.nearby.crowdingVeryHigh
    this >= 60.0 -> LocalAppStrings.current.nearby.crowdingHigh
    this >= 40.0 -> LocalAppStrings.current.nearby.crowdingNormal
    else -> LocalAppStrings.current.nearby.crowdingRelaxed
}
