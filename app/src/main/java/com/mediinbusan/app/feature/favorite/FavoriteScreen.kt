package com.mediinbusan.app.feature.favorite

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
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
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
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType

@Composable
fun FavoriteScreen(
    onSelectHospital: (String) -> Unit,
    onSelectPlace: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    FavoriteContent(
        uiState = uiState,
        onBack = onBack,
        onSelectFavorite = { favorite ->
            when (favorite.itemType) {
                FavoriteItemType.HOSPITAL -> onSelectHospital(favorite.itemId)
                FavoriteItemType.PLACE -> onSelectPlace(favorite.itemId)
            }
        },
        onRemove = viewModel::onRemove,
        onRemoveAll = viewModel::onRemoveAll
    )
}

@Composable
private fun FavoriteContent(
    uiState: FavoriteUiState,
    onBack: () -> Unit,
    onSelectFavorite: (Favorite) -> Unit,
    onRemove: (Favorite) -> Unit,
    onRemoveAll: () -> Unit
) {
    val favorites = uiState.displayedFavorites
    val strings = LocalAppStrings.current.favorite

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
                        contentDescription = LocalAppStrings.current.common.backContentDescription,
                        tint = SettingsPrimaryText
                    )
                }
                // 아이콘-타이틀 간격도 원래(24dp)의 절반으로 줄인다.
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = strings.screenTitle, style = SettingsTitleStyle, color = SettingsPrimaryText)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FavoriteTotalCountLabel(count = favorites.size)
                    if (favorites.isNotEmpty()) {
                        Text(
                            text = strings.deleteAllLabel,
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
                if (favorites.isEmpty()) {
                    EmptyState(message = strings.emptyMessage)
                } else {
                    // favorites가 새 리스트로 바뀔 때마다(최초 로딩/삭제 등) 앞쪽 카드부터 순차 공개한다.
                    val revealedCount = rememberRevealedCount(itemsKey = favorites, itemCount = favorites.size)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 4.dp,
                            bottom = 4.dp + innerPadding.calculateBottomPadding()
                        )
                    ) {
                        itemsIndexed(favorites, key = { _, favorite -> favorite.itemId }) { index, favorite ->
                            FavoriteRow(
                                favorite = favorite,
                                onClick = { onSelectFavorite(favorite) },
                                onRemove = { onRemove(favorite) },
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
private fun FavoriteTotalCountLabel(count: Int) {
    val strings = LocalAppStrings.current.favorite
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
// 4요소 + 직사각 사진+그림자)과 같은 등장 애니메이션(순차 페이드인+슬라이드업+스켈레톤)을 그대로
// 쓴다. 하트 버튼 대신 우측 상단에 X 아이콘을 얹어서 눌러 바로 삭제할 수 있게 한다.
@Composable
private fun FavoriteRow(
    favorite: Favorite,
    onClick: () -> Unit,
    onRemove: () -> Unit,
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
            if (favorite.imageUrl != null) {
                AsyncImageBox(
                    model = favorite.imageUrl,
                    contentDescription = favorite.name,
                    modifier = Modifier
                        .padding(vertical = 14.dp)
                        .width(96.dp)
                        .height(112.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Image(
                    painter = painterResource(id = fallbackBannerImageFor(favorite.itemId)),
                    contentDescription = favorite.name,
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
                    text = favorite.subtitle,
                    style = SettingsDescriptionStyle,
                    color = SettingsSecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = favorite.name,
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
                        text = favorite.address,
                        style = SettingsDescriptionStyle.copy(fontSize = 13.sp),
                        color = SettingsSecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val latitude = favorite.latitude
                val longitude = favorite.longitude
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
                contentDescription = LocalAppStrings.current.favorite.removeItemContentDescription,
                tint = SettingsSecondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteContentPreview() {
    MediInBusanTheme {
        FavoriteContent(
            uiState = FavoriteUiState(
                favorites = listOf(
                    Favorite(
                        itemId = "1",
                        itemType = FavoriteItemType.HOSPITAL,
                        name = "부산대학교병원",
                        imageUrl = null,
                        savedAt = System.currentTimeMillis()
                    )
                )
            ),
            onBack = {},
            onSelectFavorite = {},
            onRemove = {},
            onRemoveAll = {}
        )
    }
}
