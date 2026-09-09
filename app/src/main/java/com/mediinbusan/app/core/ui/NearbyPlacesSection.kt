package com.mediinbusan.app.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralInk
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import java.util.Locale

/**
 * "주변 같은 종류의 장소" 가로 스크롤 섹션. 병원 상세(S-05)의 NearbyHospitalsSection과 같은 구성이다
 * — 목록이 아니라 곁들이는 추천이라 세로로 쌓지 않고 한 줄로 흘리고, 카드가 화면 오른쪽 끝을 넘어가
 * 잘려 보여야 "더 있다"가 전달되므로 흰 카드로 감싸지 않고 캔버스 위에 직접 얹는다.
 *
 * 원래는 장소 상세(feature/nearby/PlaceDetailScreen)의 private 컴포넌트였는데, 관광 데이터 허브
 * 상세(feature/tourism)도 같은 섹션을 쓰게 되면서 여기로 옮겼다 — feature 패키지끼리는 서로를
 * import하지 않는다(CLAUDE.md §4). 두 화면 모두 본문 제일 아래에 같은 구성으로 붙인다.
 */
@Composable
fun NearbyPlacesSection(
    anchorType: PlaceType,
    places: List<Place>,
    onSelectPlace: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.nearby
    // 제목은 "주변 관광지" / "Nearby: Cafe & dining"처럼 지금 보고 있는 장소의 종류를 그대로 넣는다.
    val typeLabel = strings.placeTypeLabels[anchorType.name] ?: strings.allLabel
    Column(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = CoralPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.nearbySameTypeTitleFormat.format(typeLabel),
                    style = SectionTitleStyle,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // 이 목록이 무엇을 기준으로 뽑힌 건지 한 줄로 밝힌다 — 근거 없는 추천처럼 보이지 않게.
            Text(
                text = strings.nearbySameTypeSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = places, key = { it.id }) { nearby ->
                NearbyPlaceCard(place = nearby, onClick = { onSelectPlace(nearby.id) })
            }
        }
    }
}

/** 썸네일 + 거리 배지 + 이름 + 세부 분류 한 줄짜리 카드. */
@Composable
private fun NearbyPlaceCard(place: Place, onClick: () -> Unit) {
    val language = LocalAppStrings.current.language
    val visual = remember(place.type, place.category) { placeKindVisual(place.type, place.category) }
    Column(
        modifier = Modifier
            .width(164.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(DividerColor)
        ) {
            // 사진이 없는 장소가 많다. 상세 히어로의 마스코트 일러스트가 아니라 목록·카드용 폴백
            // 배너를 쓴다 — 같은 캐릭터를 카드마다 반복하면 카드가 전부 똑같아 보여 구분이 안 된다.
            if (place.imageUrl != null) {
                AsyncImageBox(
                    model = place.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = fallbackBannerImageFor(place.id)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // 거리는 서버가 기준 좌표로부터 계산해 내려준 값이라, 없으면 배지를 아예 안 단다.
            place.distanceFromHospitalMeters?.let { meters ->
                Text(
                    text = meters.toNearbyDistanceLabel(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = CoralInk,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                // 이름이 한 줄인 카드와 두 줄인 카드가 섞이면 아래 분류 줄의 높이가 어긋난다 —
                // 두 줄 자리를 항상 잡아 카드들의 바닥선을 맞춘다.
                minLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.ink,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = place.category.translatedLabel(language),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun Double.toNearbyDistanceLabel(): String =
    if (this < 1000.0) "${toInt()}m" else String.format(Locale.US, "%.1fkm", this / 1000.0)
