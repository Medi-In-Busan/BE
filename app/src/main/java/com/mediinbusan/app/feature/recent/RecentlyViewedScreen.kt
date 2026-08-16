package com.mediinbusan.app.feature.recent

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.common.haversineDistanceMeters
import com.mediinbusan.app.core.common.toDistanceLabel
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.RecentlyViewedStrings
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.SettingsTitleStyle
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.InitialCardRevealCount
import com.mediinbusan.app.core.ui.ShimmerSkeleton
import com.mediinbusan.app.core.ui.fallbackBannerImageFor
import com.mediinbusan.app.core.ui.rememberCardRevealProgress
import com.mediinbusan.app.core.ui.rememberCountUpValue
import com.mediinbusan.app.core.ui.rememberRevealedCount
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.recent.RecentlyViewed
import java.util.concurrent.TimeUnit

@Composable
fun RecentlyViewedScreen(
    onSelectHospital: (String) -> Unit,
    onSelectPlace: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RecentlyViewedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RecentlyViewedContent(
        items = uiState.items,
        onBack = onBack,
        onSelectItem = { item ->
            when (item.itemType) {
                FavoriteItemType.HOSPITAL -> onSelectHospital(item.itemId)
                FavoriteItemType.PLACE -> onSelectPlace(item.itemId)
            }
        },
        onRemove = { viewModel.onRemove(it.itemId) },
        onRemoveAll = viewModel::onRemoveAll
    )
}

@Composable
private fun RecentlyViewedContent(
    items: List<RecentlyViewed>,
    onBack: () -> Unit,
    onSelectItem: (RecentlyViewed) -> Unit,
    onRemove: (RecentlyViewed) -> Unit,
    onRemoveAll: () -> Unit
) {
    val appStrings = LocalAppStrings.current
    // Settings와 동일한 톤: 공용 탑바/하단 탭바 없이, 배경은 Home과 같은 HomeBackgroundPink,
    // 뒤로가기는 원형 배경 없는 화살표 아이콘을 타이틀 위에 직접 배치한다.
    Scaffold(containerColor = HomeBackgroundPink) { innerPadding ->
        // 상태바 인셋을 원래의 절반만 먹여서 뒤로가기 아이콘을 원래 위치에서 절반 정도 위로 당긴다.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding() * 0.5f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = appStrings.common.backContentDescription,
                        tint = SettingsPrimaryText
                    )
                }
                // 아이콘-타이틀 간격도 원래(24dp)의 절반으로 줄인다.
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = appStrings.settings.recentlyViewedTitle, style = SettingsTitleStyle, color = SettingsPrimaryText)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RecentlyViewedTotalCountLabel(count = items.size)
                    if (items.isNotEmpty()) {
                        Text(
                            text = appStrings.recentlyViewed.deleteAllLabel,
                            style = SettingsDescriptionStyle,
                            color = SettingsPrimaryText,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onRemoveAll)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (items.isEmpty()) {
                    EmptyState(message = appStrings.recentlyViewed.emptyMessage)
                } else {
                    // items가 새 리스트로 바뀔 때마다(최초 로딩/삭제 등) 앞쪽 카드부터 순차 공개한다.
                    val revealedCount = rememberRevealedCount(itemsKey = items, itemCount = items.size)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 4.dp,
                            bottom = 4.dp + innerPadding.calculateBottomPadding()
                        )
                    ) {
                        itemsIndexed(items, key = { _, item -> item.itemId }) { index, item ->
                            RecentlyViewedRow(
                                item = item,
                                onClick = { onSelectItem(item) },
                                onRemove = { onRemove(item) },
                                strings = appStrings.recentlyViewed,
                                isRevealAnimated = index < InitialCardRevealCount,
                                isRevealed = index < revealedCount
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

// HospitalSearchListScreen의 검색결과 N건 라벨과 같은 톤 — 접두어는 진한 텍스트, 건수는 코랄핑크.
@Composable
private fun RecentlyViewedTotalCountLabel(count: Int) {
    val strings = LocalAppStrings.current.recentlyViewed
    val animatedCount = rememberCountUpValue(count)
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = SettingsPrimaryText, fontWeight = FontWeight.Bold)) {
            append(strings.totalCountPrefix)
        }
        withStyle(SpanStyle(color = CoralPrimary, fontWeight = FontWeight.Bold)) {
            append(strings.totalCountSuffixFormat.format(animatedCount))
        }
    }
    Text(text = text, style = SettingsDescriptionStyle)
}

// HospitalSearchListScreen의 SearchResultCard와 완전히 같은 카드 양식(태그, 타이틀, 위치, 거리
// 4요소 + 직사각 사진+그림자)과 같은 등장 애니메이션(순차 페이드인+슬라이드업+스켈레톤)에, 최근 본
// 항목 전용으로 언제 봤는지(N분/시간 전)를 코랄블루로 한 줄 더 붙인다. 우측 상단 X 아이콘으로 개별 삭제.
@Composable
private fun RecentlyViewedRow(
    item: RecentlyViewed,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    strings: RecentlyViewedStrings,
    isRevealAnimated: Boolean,
    isRevealed: Boolean
) {
    val revealProgress = rememberCardRevealProgress(isRevealAnimated, isRevealed)

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = revealProgress
                    translationY = (1f - revealProgress) * 10.dp.toPx()
                }
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp)
        ) {
            if (item.imageUrl != null) {
                AsyncImageBox(
                    model = item.imageUrl,
                    contentDescription = item.itemName,
                    modifier = Modifier
                        .padding(vertical = 14.dp)
                        .width(96.dp)
                        .height(112.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Image(
                    painter = painterResource(id = fallbackBannerImageFor(item.itemId)),
                    contentDescription = item.itemName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(vertical = 14.dp)
                        .width(96.dp)
                        .height(112.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f).padding(vertical = 20.dp).padding(end = 32.dp)) {
                Text(
                    text = item.subtitle,
                    style = SettingsDescriptionStyle,
                    color = SettingsSecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.itemName,
                    style = SettingsItemTitleStyle,
                    color = SettingsPrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SettingsSecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.address,
                        style = SettingsDescriptionStyle.copy(fontSize = 13.sp),
                        color = SettingsSecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val latitude = item.latitude
                val longitude = item.longitude
                if (latitude != null && longitude != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = haversineDistanceMeters(
                            DefaultSearchOrigin.LATITUDE,
                            DefaultSearchOrigin.LONGITUDE,
                            latitude,
                            longitude
                        ).toDistanceLabel(),
                        style = SettingsDescriptionStyle,
                        color = CoralPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.viewedAt.toRelativeTimeLabel(strings),
                    style = SettingsDescriptionStyle,
                    color = SkyBlue
                )
            }
        }
        if (isRevealAnimated && revealProgress < 1f) {
            ShimmerSkeleton(
                alpha = 1f - revealProgress,
                modifier = Modifier.matchParentSize()
            )
        }
        // 텍스트 첫 줄(태그) 라인선상에 오도록 위로 살짝 내린다 — 카드 맨 꼭짓점에 딱 붙지 않게.
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 6.dp)
                .size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = strings.removeItemContentDescription,
                tint = SettingsSecondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun Long.toRelativeTimeLabel(strings: RecentlyViewedStrings): String {
    val elapsedMillis = System.currentTimeMillis() - this
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
    val days = TimeUnit.MILLISECONDS.toDays(elapsedMillis)
    return when {
        minutes < 1 -> strings.relativeJustNow
        minutes < 60 -> strings.relativeMinutesFormat.format(minutes)
        hours < 24 -> strings.relativeHoursFormat.format(hours)
        else -> strings.relativeDaysFormat.format(days)
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentlyViewedContentPreview() {
    MediInBusanTheme {
        RecentlyViewedContent(
            items = listOf(
                RecentlyViewed(
                    itemId = "1",
                    itemName = "부산대학교병원",
                    itemType = FavoriteItemType.HOSPITAL,
                    imageUrl = null,
                    viewedAt = System.currentTimeMillis()
                )
            ),
            onBack = {},
            onSelectItem = {},
            onRemove = {},
            onRemoveAll = {}
        )
    }
}
