package com.mediinbusan.app.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
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

/** 사진이 카드를 채우고, 거리 배지·이름·세부 분류가 그 위에 얹히는 카드(core/ui/NearbyPhotoCard.kt). */
@Composable
private fun NearbyPlaceCard(place: Place, onClick: () -> Unit) {
    val language = LocalAppStrings.current.language
    val visual = remember(place.type, place.category) { placeKindVisual(place.type, place.category) }
    NearbyPhotoCard(
        title = place.name,
        distanceLabel = place.distanceFromHospitalMeters?.toNearbyDistanceLabel(),
        onClick = onClick,
        image = {
            // 웰니스 API 원문에 사진이 비어 있는 장소가 많다 — 예전엔 항목과 무관한 부산 풍경
            // 배너(fallbackBannerImageFor)를 끌어다 썼는데, 사진이 카드를 통째로 채우게 바뀌면서
            // "이 장소의 사진"으로 읽혀 오해를 준다. 목록에서 이 장소를 볼 때와 같은 자리표시자
            // (PlaceFallbackThumbnail: 종류별 색 그라데이션 + 종류 아이콘)를 그대로 쓴다 — 지도
            // 관광·음식 목록에서 보던 그림이 상세로 들어갔다고 달라지면 같은 곳으로 안 읽힌다.
            if (place.imageUrl != null) {
                AsyncImageBox(
                    model = place.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                PlaceFallbackThumbnail(visual = visual, modifier = Modifier.fillMaxSize(), iconSize = 44.dp)
            }
        },
        meta = {
            // 어두운 그라데이션 위라 분류 아이콘도 종류별 색(visual.ink) 대신 흰색으로 낸다 —
            // 옅은 색 아이콘은 사진 위에서 사실상 안 보인다.
            Icon(
                imageVector = visual.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.82f),
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = place.category.translatedLabel(language),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.82f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

private fun Double.toNearbyDistanceLabel(): String =
    if (this < 1000.0) "${toInt()}m" else String.format(Locale.US, "%.1fkm", this / 1000.0)
