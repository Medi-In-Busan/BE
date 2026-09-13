package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralInk
import com.mediinbusan.app.core.designsystem.DividerColor

/**
 * "주변 ○○"(주변 병원 / 주변 관광지 / 주변 쇼핑…) 가로 스크롤 섹션들이 공유하는 사진 카드.
 *
 * 원래는 흰 카드 위쪽에만 썸네일을 얹고 이름·분류는 그 아래 흰 바탕에 두는 모양이었는데, 사진이
 * 카드의 절반밖에 안 돼 "어떤 곳인지"가 눈에 들어오지 않았다. 웰니스(S-07) 핫플레이스 카드
 * (feature/nearby/NearbyScreen.kt의 HotPlaceGridCard)와 같은 처리로 통일한다 — 사진이 카드를
 * 통째로 채우고, 이름·분류·거리는 그 위에 얹는다. 그라데이션은 사진 전체가 아니라 글씨가 앉는
 * 하단 구간에만 걸어서 위쪽 사진은 원본 밝기 그대로 보이게 한다.
 *
 * 병원 상세(S-05)의 주변 병원 섹션과 장소·관광 상세의 주변 장소 섹션은 원래도 서로를 "같은 구성"
 * 이라고 주석으로 가리키며 각자 같은 카드를 복제해 갖고 있었다 — 한쪽만 손대면 두 섹션의 리듬이
 * 어긋나므로 카드 몸통은 여기 core/ui에 한 벌만 두고, 사진과 분류 줄만 슬롯으로 받는다.
 *
 * @param image 카드를 채울 사진. 호출부가 `Modifier.fillMaxSize()`로 넣는다(사진이 없을 때의
 *   폴백 이미지 규칙이 병원/장소마다 달라서 여기서 정하지 않는다).
 * @param meta 제목 아래 한 줄(대표 진료과목 / 장소 세부 분류). 어두운 그라데이션 위라 색은
 *   흰 계열로 넣어야 한다.
 */
@Composable
fun NearbyPhotoCard(
    title: String,
    distanceLabel: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    image: @Composable BoxScope.() -> Unit,
    meta: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .width(NearbyPhotoCardWidth)
            .height(NearbyPhotoCardHeight)
            .shadow(
                elevation = 6.dp,
                shape = NearbyPhotoCardShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(NearbyPhotoCardShape)
            // 사진을 아직 못 받았거나 실패했을 때 카드가 투명하게 비지 않도록 바닥색을 깔아둔다.
            .background(DividerColor)
            .clickable(onClick = onClick)
    ) {
        image()
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(112.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.80f))))
        )
        // 거리는 기준 좌표로부터 계산된 값이라, 없으면 배지를 아예 안 단다. 아래쪽은 이름이
        // 차지하므로 그라데이션이 닿지 않는 위쪽 모서리에 흰 알약으로 띄운다.
        if (distanceLabel != null) {
            Text(
                text = distanceLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = CoralInk,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 예전 카드는 이름이 한 줄일 때와 두 줄일 때 아래 분류 줄이 어긋나 minLines=2로 자리를
            // 잡아뒀었다 — 이제 글씨 블록이 카드 바닥에 붙어 자라므로 그 보정이 필요 없다.
            Text(
                text = title,
                style = CardTitleStyle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                content = meta
            )
        }
    }
}

/** 두 섹션(주변 병원 / 주변 장소)이 같은 치수로 보이도록 카드 크기는 여기서만 정한다. */
private val NearbyPhotoCardWidth = 210.dp
private val NearbyPhotoCardHeight = 200.dp
private val NearbyPhotoCardShape = RoundedCornerShape(20.dp)
