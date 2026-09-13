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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
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
        // 아이콘·부제("이 목록이 무엇을 기준으로 뽑혔는지") 없이 굵은 제목 한 줄만 — 참고 디자인
        // (guide_tourism_place_detail.png)이 이 자리를 가볍게 쓴다.
        Text(
            text = strings.nearbySameTypeTitleFormat.format(typeLabel),
            style = SectionTitleStyle,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
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

/** 썸네일 + 이름 + 거리 한 줄짜리 가벼운 카드(guide_tourism_place_detail.png 기준 — 종류 배지는 뺐다). */
@Composable
private fun NearbyPlaceCard(place: Place, onClick: () -> Unit) {
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
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            // 거리는 서버가 기준 좌표로부터 계산해 내려준 값이라, 없으면 아예 안 보여준다.
            place.distanceFromHospitalMeters?.let { meters ->
                Spacer(modifier = Modifier.width(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = meters.toNearbyDistanceLabel(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun Double.toNearbyDistanceLabel(): String =
    if (this < 1000.0) "${toInt()}m" else String.format(Locale.US, "%.1fkm", this / 1000.0)
