package com.mediinbusan.app.feature.tourism

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedDescription
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismHotPlace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourismHubScreen(
    onSelectCategory: (TourismCatalogCategory) -> Unit,
    onBack: () -> Unit,
    viewModel: TourismHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current

    Scaffold(
        containerColor = TourismCanvas,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.tourism.backContentDescription)
                    }
                },
                title = { Text(strings.tourism.hubTitle) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            item {
                HotPlacesSection(
                    hotPlaces = uiState.hotPlaces,
                    isLoading = uiState.isHighlightsLoading,
                    errorMessage = uiState.highlightsError,
                    onSeeAll = { onSelectCategory(TourismCatalogCategory.CROWDING) },
                    onRetry = viewModel::retryHighlights
                )
            }
            item {
                FeaturedExploreBanner(
                    languageName = uiState.language.displayName,
                    category = uiState.featuredCategory,
                    onClick = { onSelectCategory(uiState.featuredCategory) }
                )
            }
            item {
                JourneySection(
                    title = "회복 일정에 맞춰",
                    description = "이동 부담과 활동 강도를 고려해 골라보세요.",
                    categories = uiState.recoveryCategories,
                    onSelectCategory = onSelectCategory
                )
            }
            item {
                JourneySection(
                    title = "여행 전에 확인",
                    description = "함께 둘러볼 곳과 예상 혼잡도를 확인해요.",
                    categories = uiState.planningCategories,
                    onSelectCategory = onSelectCategory
                )
            }
            item {
                AccessibleTourismSection(
                    places = uiState.accessiblePlaces,
                    isLoading = uiState.isHighlightsLoading,
                    onSeeAll = { onSelectCategory(TourismCatalogCategory.ACCESSIBLE) }
                )
            }
            item { TourismSourceNotice() }
        }
    }
}

@Composable
private fun HotPlacesSection(
    hotPlaces: List<TourismHotPlace>,
    isLoading: Boolean,
    errorMessage: String?,
    onSeeAll: () -> Unit,
    onRetry: () -> Unit
) {
    HighlightSectionHeader(
        title = "현재 부산 핫 플레이스",
        description = "관광 혼잡도 지수가 높은 장소를 먼저 보여드려요.",
        showSeeAll = hotPlaces.isNotEmpty(),
        onSeeAll = onSeeAll
    )
    Spacer(Modifier.height(12.dp))
    when {
        isLoading -> HighlightLoadingCard()
        errorMessage != null -> HighlightErrorCard(message = errorMessage, onRetry = onRetry)
        hotPlaces.isNotEmpty() -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            hotPlaces.take(HOT_PLACE_CARD_LIMIT).forEachIndexed { index, hotPlace ->
                HotPlaceCard(rank = index + 1, hotPlace = hotPlace, onClick = onSeeAll)
            }
        }
    }
}

@Composable
private fun HotPlaceCard(rank: Int, hotPlace: TourismHotPlace, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HotPlaceThumbnail(rank = rank)
            Column(
                modifier = Modifier.weight(1f).height(112.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = hotPlace.item.title,
                            style = CardTitleStyle,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = hotPlace.congestionRate.toDisplayRate(),
                            style = MaterialTheme.typography.titleMedium,
                            color = CoralPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(shape = CircleShape, color = CoralPrimary.copy(alpha = 0.11f)) {
                        Text(
                            text = hotPlace.congestionRate.toCongestionLabel(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = CoralPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "부산 ${hotPlace.district.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "혼잡도를 확인하고 방문 시간을 조정해 보세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HotPlaceThumbnail(rank: Int) {
    Box(
        modifier = Modifier
            .size(width = 104.dp, height = 112.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFE2DA), Color(0xFFFFF5F0), Color(0xFFEAF7FF))
                )
            )
    ) {
        Box(
            modifier = Modifier.size(54.dp).align(Alignment.Center).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.BarChart,
                contentDescription = null,
                tint = CoralPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = "HOT $rank",
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
            style = MaterialTheme.typography.labelSmall,
            color = CoralPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AccessibleTourismSection(
    places: List<TourismCatalogItem>,
    isLoading: Boolean,
    onSeeAll: () -> Unit
) {
    HighlightSectionHeader(
        title = "편안하게 즐기는 무장애 관광",
        description = "이동 편의 정보가 제공되는 부산 관광지를 모았어요.",
        showSeeAll = places.isNotEmpty(),
        onSeeAll = onSeeAll
    )
    Spacer(Modifier.height(12.dp))
    when {
        isLoading -> HighlightLoadingCard()
        places.isNotEmpty() -> LazyRow(
            contentPadding = PaddingValues(end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(places, key = { "accessible-${it.id}" }) { place ->
                AccessiblePlaceCard(place = place, onClick = onSeeAll)
            }
        }
    }
}

@Composable
private fun AccessiblePlaceCard(place: TourismCatalogItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(224.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column {
            if (place.imageUrl != null) {
                AsyncImageBox(
                    model = place.imageUrl,
                    contentDescription = place.title,
                    modifier = Modifier.fillMaxWidth().height(128.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .background(SkyBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Accessible,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = place.title,
                    style = CardTitleStyle,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = place.address ?: "이동 편의 정보 제공",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HighlightSectionHeader(
    title: String,
    description: String,
    showSeeAll: Boolean,
    onSeeAll: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = SectionTitleStyle, color = TextPrimary)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        if (showSeeAll) {
            TextButton(onClick = onSeeAll, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text("전체보기", color = CoralPrimary)
            }
        }
    }
}

@Composable
private fun HighlightLoadingCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(112.dp),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp), color = CoralPrimary, strokeWidth = 3.dp)
        }
    }
}

@Composable
private fun HighlightErrorCard(message: String, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(message, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            TextButton(onClick = onRetry) { Text("다시 시도", color = CoralPrimary) }
        }
    }
}

private fun Double.toDisplayRate(): String = if (this % 1.0 == 0.0) {
    "지수 ${toInt()}"
} else {
    "지수 ${"%.1f".format(this)}"
}

private fun Double.toCongestionLabel(): String = when {
    this >= 80.0 -> "매우 혼잡"
    this >= 60.0 -> "혼잡"
    this >= 40.0 -> "보통"
    else -> "여유"
}

private const val HOT_PLACE_CARD_LIMIT = 5

@Composable
private fun FeaturedExploreBanner(
    languageName: String,
    category: TourismCatalogCategory,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFE5DA), Color(0xFFFFF8F4), Color(0xFFE4F5FC))
                )
            )
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(148.dp)
                .align(Alignment.TopEnd)
                .padding(start = 34.dp, bottom = 34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.48f))
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(shape = CircleShape, color = SkyBlue.copy(alpha = 0.14f)) {
                Icon(
                    Icons.Default.Explore,
                    contentDescription = null,
                    tint = SkyBlue,
                    modifier = Modifier.padding(10.dp).size(24.dp)
                )
            }
            Text(
                text = "회복 사이,\n부산 한 걸음",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "지금 설정된 언어로 부산의 관광지와 쉬어가기 좋은 장소를 찾아보세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = "$languageName 관광정보",
                style = MaterialTheme.typography.labelLarge,
                color = CoralPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoralPrimary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(category.label, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun JourneySection(
    title: String,
    description: String,
    categories: List<TourismCatalogCategory>,
    onSelectCategory: (TourismCatalogCategory) -> Unit
) {
    if (categories.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = SectionTitleStyle, color = TextPrimary)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.dp, DividerColor)
        ) {
            Column {
                categories.forEachIndexed { index, category ->
                    TourismJourneyRow(
                        category = category,
                        onClick = { onSelectCategory(category) }
                    )
                    if (index < categories.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 18.dp),
                            color = DividerColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TourismJourneyRow(
    category: TourismCatalogCategory,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(shape = CircleShape, color = category.tint.copy(alpha = 0.12f)) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = category.tint,
                modifier = Modifier.padding(10.dp).size(21.dp)
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(category.label, style = CardTitleStyle, color = TextPrimary)
            Text(category.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun TourismSourceNotice() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "한국관광공사 공공데이터 활용",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "운영 정보와 혼잡도는 실제 방문 전에 공식 안내를 함께 확인해 주세요.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

private val TourismCatalogCategory.icon: ImageVector
    get() = when (this) {
        TourismCatalogCategory.ACCESSIBLE -> Icons.AutoMirrored.Filled.Accessible
        TourismCatalogCategory.WALKING -> Icons.Default.Route
        TourismCatalogCategory.RELATED -> Icons.Default.Explore
        TourismCatalogCategory.CROWDING -> Icons.Default.BarChart
        else -> Icons.Default.Explore
    }

private val TourismCatalogCategory.tint: Color
    get() = when (this) {
        TourismCatalogCategory.ACCESSIBLE -> SkyBlue
        TourismCatalogCategory.WALKING -> Color(0xFF3A7D7B)
        TourismCatalogCategory.RELATED -> CoralPrimary
        TourismCatalogCategory.CROWDING -> Color(0xFFCB6D3D)
        else -> SkyBlue
    }

internal val TourismCanvas = Color(0xFFFFFAF7)
