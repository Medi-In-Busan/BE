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
import androidx.compose.foundation.layout.widthIn
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
    onSelectTourismItem: () -> Unit,
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
                    onSelectHotPlace = { hotPlace ->
                        viewModel.selectHotPlace(hotPlace)
                        onSelectTourismItem()
                    },
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
                    title = strings.tourism.recoveryScheduleTitle,
                    description = strings.tourism.recoveryScheduleDescription,
                    categories = uiState.recoveryCategories,
                    onSelectCategory = onSelectCategory
                )
            }
            item {
                JourneySection(
                    title = strings.tourism.beforeTripTitle,
                    description = strings.tourism.beforeTripDescription,
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
    onSelectHotPlace: (TourismHotPlace) -> Unit,
    onRetry: () -> Unit
) {
    HighlightSectionHeader(
        title = LocalAppStrings.current.tourism.currentHotPlacesTitle,
        description = LocalAppStrings.current.tourism.currentHotPlacesDescription,
        showSeeAll = hotPlaces.isNotEmpty(),
        onSeeAll = onSeeAll
    )
    Spacer(Modifier.height(12.dp))
    when {
        isLoading -> HighlightLoadingCard()
        errorMessage != null -> HighlightErrorCard(message = errorMessage, onRetry = onRetry)
        hotPlaces.isNotEmpty() -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            hotPlaces.take(HOT_PLACE_CARD_LIMIT).forEachIndexed { index, hotPlace ->
                HotPlaceCard(
                    rank = index + 1,
                    hotPlace = hotPlace,
                    onClick = { onSelectHotPlace(hotPlace) }
                )
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
                        text = LocalAppStrings.current.nearby.busanDistrictFormat.format(
                            hotPlace.district.translatedLabel(LocalAppStrings.current.language)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = LocalAppStrings.current.tourism.crowdingAdvice,
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
        title = LocalAppStrings.current.tourism.accessibleFeatureTitle,
        description = LocalAppStrings.current.tourism.accessibleFeatureDescription,
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
                    text = place.address ?: LocalAppStrings.current.tourism.accessibilityFallback,
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
            TextButton(
                onClick = onSeeAll,
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.widthIn(min = 76.dp)
            ) {
                Text(
                    LocalAppStrings.current.tourism.seeAllLabel,
                    color = CoralPrimary,
                    maxLines = 1,
                    softWrap = false
                )
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
            TextButton(onClick = onRetry) { Text(LocalAppStrings.current.tourism.retryLabel, color = CoralPrimary) }
        }
    }
}

@Composable
private fun Double.toDisplayRate(): String = LocalAppStrings.current.tourism.concentrationIndexFormat.format(
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)
)

@Composable
private fun Double.toCongestionLabel(): String = with(LocalAppStrings.current.nearby) {
    when {
        this@toCongestionLabel >= 80.0 -> crowdingVeryHigh
        this@toCongestionLabel >= 60.0 -> crowdingHigh
        this@toCongestionLabel >= 40.0 -> crowdingNormal
        else -> crowdingRelaxed
    }
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
                text = LocalAppStrings.current.tourism.featuredHeroTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = LocalAppStrings.current.tourism.hubLanguageDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = LocalAppStrings.current.tourism.languageTourismFormat.format(languageName),
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
                Text(category.translatedLabel(LocalAppStrings.current.language), fontWeight = FontWeight.SemiBold)
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
            Text(category.translatedLabel(LocalAppStrings.current.language), style = CardTitleStyle, color = TextPrimary)
            Text(category.translatedDescription(LocalAppStrings.current.language), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
            text = LocalAppStrings.current.tourism.publicDataCredit,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = LocalAppStrings.current.tourism.visitDisclaimer,
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
