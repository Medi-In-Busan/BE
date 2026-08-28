package com.mediinbusan.app.feature.nearby

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediBlue40
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.domain.course.HospitalWellnessRoute
import com.mediinbusan.app.domain.tourism.TourismHotPlace

@Composable
fun NearbyScreen(
    hospitalId: String,
    onSelectPlace: (String) -> Unit,
    onSelectTourismItem: () -> Unit,
    onNavigateToNearbyMap: () -> Unit,
    onNavigateToCourseMap: (Int) -> Unit,
    onExploreTourism: () -> Unit,
    onBack: () -> Unit,
    viewModel: NearbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(hospitalId) {
        viewModel.load(hospitalId)
    }

    NearbyContent(
        uiState = uiState,
        onSelectPlace = onSelectPlace,
        onSelectHotPlace = { hotPlace ->
            viewModel.selectHotPlace(hotPlace)
            onSelectTourismItem()
        },
        onNavigateToNearbyMap = onNavigateToNearbyMap,
        onNavigateToCourseMap = onNavigateToCourseMap,
        onExploreTourism = onExploreTourism,
        onBack = onBack,
        onRetry = { viewModel.load(hospitalId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NearbyContent(
    uiState: NearbyUiState,
    onSelectPlace: (String) -> Unit,
    onSelectHotPlace: (TourismHotPlace) -> Unit,
    onNavigateToNearbyMap: () -> Unit,
    onNavigateToCourseMap: (Int) -> Unit,
    onExploreTourism: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        containerColor = HomeBackgroundPink,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = { Text(text = "관광·웰니스") },
                actions = {
                    IconButton(onClick = onNavigateToNearbyMap) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = "병원 주변 지도 보기")
                    }
                    IconButton(onClick = onExploreTourism) {
                        Icon(imageVector = Icons.Default.Explore, contentDescription = "부산 관광 데이터")
                    }
                }
            )
        }
    ) { innerPadding ->
        val errorMessage = uiState.errorMessage
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(innerPadding))
            errorMessage != null -> ErrorState(
                message = errorMessage,
                modifier = Modifier.padding(innerPadding),
                onRetry = onRetry
            )
            uiState.places.isEmpty() -> EmptyState(
                message = "주변 장소 정보가 없습니다.",
                modifier = Modifier.padding(innerPadding)
            )
            else -> NearbyLoadedContent(
                uiState = uiState,
                onSelectPlace = onSelectPlace,
                onSelectHotPlace = onSelectHotPlace,
                onNavigateToCourseMap = onNavigateToCourseMap,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun NearbyLoadedContent(
    uiState: NearbyUiState,
    onSelectPlace: (String) -> Unit,
    onSelectHotPlace: (TourismHotPlace) -> Unit,
    onNavigateToCourseMap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf<PlaceType?>(null) }
    val types = uiState.places.map { it.type }.distinct()
    val filteredPlaces = selectedType?.let { selected ->
        uiState.places.filter { it.type == selected }
    } ?: uiState.places

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            WellnessTourHero(
                hospitalName = uiState.recommendedRoutes.firstOrNull()?.hospital?.name,
                placeCount = uiState.places.size,
                courseCount = uiState.recommendedRoutes.size
            )
        }

        item {
            HotTourismTopFiveSection(
                hotPlaces = uiState.hotPlaces,
                isLoading = uiState.isHotPlacesLoading,
                errorMessage = uiState.hotPlacesError,
                onSelectHotPlace = onSelectHotPlace
            )
        }

        if (uiState.recommendedRoutes.isNotEmpty()) {
            item {
                RecommendedRouteSection(routes = uiState.recommendedRoutes, onClick = onNavigateToCourseMap)
            }
        }

        item {
            PlaceFilterSection(
                types = types,
                selectedType = selectedType,
                onTypeSelected = { selectedType = it }
            )
        }

        item {
            Text(
                text = "${filteredPlaces.size}곳 둘러보기",
                style = SectionTitleStyle,
                color = TextPrimary
            )
        }

        if (filteredPlaces.isEmpty()) {
            item {
                EmptyPlaceFilter(onReset = {
                    selectedType = null
                })
            }
        } else {
            items(filteredPlaces, key = { it.id }) { place ->
                PlaceRecommendationCard(place = place, onClick = { onSelectPlace(place.id) })
            }
        }

    }
}

@Composable
private fun WellnessTourHero(hospitalName: String?, placeCount: Int, courseCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFE7E9), Color(0xFFFFF8F8), Color(0xFFEAF5FF))
                )
            )
            .padding(22.dp)
    ) {
        Box(
            modifier = Modifier
                .size(118.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.48f))
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(23.dp))
            }
            Text(
                text = "진료 전후,\n부산에서 편안하게",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = hospitalName?.let { "$it 주변의 회복 친화적인 장소와 동선을 추천해요." }
                    ?: "병원 주변의 회복 친화적인 장소와 동선을 추천해요.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(0.82f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroBadge("추천 ${placeCount}곳")
                if (courseCount > 0) HeroBadge("맞춤 코스 ${courseCount}개")
            }
        }
    }
}

@Composable
private fun HeroBadge(text: String) {
    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.9f)) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = CoralPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun WellnessSectionCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.16f),
                spotColor = Color.Black.copy(alpha = 0.16f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Box(modifier = Modifier.padding(18.dp)) { content() }
    }
}

@Composable
private fun HotTourismTopFiveSection(
    hotPlaces: List<TourismHotPlace>,
    isLoading: Boolean,
    errorMessage: String?,
    onSelectHotPlace: (TourismHotPlace) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "부산 핫 관광지 TOP 5", style = SectionTitleStyle, color = TextPrimary)
            Text(
                text = "향후 30일 예상 집중도 지수가 높은 순서예요.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        when {
            isLoading -> WellnessSectionCard {
                Box(modifier = Modifier.fillMaxWidth().height(96.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp), color = CoralPrimary, strokeWidth = 3.dp)
                }
            }
            errorMessage != null -> WellnessSectionCard {
                Text(errorMessage, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                hotPlaces.take(5).forEachIndexed { index, hotPlace ->
                    HotTourismRankRow(index + 1, hotPlace) { onSelectHotPlace(hotPlace) }
                }
            }
        }
    }
}

@Composable
private fun HotTourismRankRow(rank: Int, hotPlace: TourismHotPlace, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 64.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color(0xFFF0F8FF)))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleMedium,
                color = CoralPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = hotPlace.item.title,
                style = CardTitleStyle,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("부산 ${hotPlace.district.label}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = hotPlace.congestionRate.toHotPlaceRate(),
                style = MaterialTheme.typography.titleMedium,
                color = CoralPrimary,
                fontWeight = FontWeight.Bold
            )
            Text("집중도", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = CoralPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

private fun Double.toHotPlaceRate(): String = if (this % 1.0 == 0.0) {
    toInt().toString()
} else {
    "%.1f".format(this)
}

@Composable
private fun EmptyPlaceFilter(onReset: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, DividerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "선택한 조건의 장소가 없습니다.", style = CardTitleStyle, color = TextPrimary)
            OutlinedButton(onClick = onReset) {
                Text(text = "전체 장소 보기")
            }
        }
    }
}

@Composable
private fun RecommendedRouteSection(routes: List<HospitalWellnessRoute>, onClick: (Int) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { routes.size })
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "추천 웰니스 코스", style = SectionTitleStyle, color = TextPrimary)
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(end = 28.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth().height(198.dp)
        ) { page ->
            RecommendedRouteCard(
                route = routes[page],
                title = courseTitle(page),
                onClick = { onClick(page) }
            )
        }
        if (routes.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                routes.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (pagerState.currentPage == index) 18.dp else 7.dp, 7.dp)
                            .clip(CircleShape)
                            .background(if (pagerState.currentPage == index) CoralPrimary else DividerColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedRouteCard(route: HospitalWellnessRoute, title: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.18f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            RouteThumbnail(route = route)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = route.hospital.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = CoralPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    style = CardTitleStyle,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = route.stops.joinToString(" · ") { it.place.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    RouteBadge(text = "${route.stops.size}곳")
                    RouteBadge(text = durationLabel(route.estimatedDurationMinutes))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${route.totalDistanceKm.formatDistance()}km · 동선 보기",
                        style = MaterialTheme.typography.labelSmall,
                        color = CoralPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = CoralPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private fun courseTitle(index: Int): String = when (index) {
    0 -> "나를 위한 부산 회복 코스"
    1 -> "가볍게 쉬어가는 웰니스 코스"
    2 -> "산책과 관광을 잇는 회복 코스"
    else -> "부산의 맛과 휴식을 담은 코스"
}

@Composable
private fun RouteThumbnail(route: HospitalWellnessRoute) {
    Box(
        modifier = Modifier
            .size(width = 106.dp, height = 160.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(CoralPrimaryContainer, Color(0xFFEAF7FF))
                )
            )
    ) {
        val imageUrl = route.stops.firstNotNullOfOrNull { it.place.imageUrl }
        if (imageUrl != null) {
            AsyncImageBox(imageUrl, "${route.hospital.name} 추천 코스", Modifier.fillMaxSize())
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.58f)))
                )
            )
        } else {
            Box(
                modifier = Modifier.size(54.dp).align(Alignment.Center).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Map, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(28.dp))
            }
        }
        Text(
            text = "${route.stops.size} STOPS",
            style = MaterialTheme.typography.labelSmall,
            color = if (imageUrl != null) Color.White else CoralPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
        )
    }
}

@Composable
private fun RouteBadge(text: String) {
    Surface(shape = RoundedCornerShape(999.dp), color = CoralPrimaryContainer) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = CoralPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PlaceFilterSection(
    types: List<PlaceType>,
    selectedType: PlaceType?,
    onTypeSelected: (PlaceType?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "주변 추천 장소", style = SectionTitleStyle, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { onTypeSelected(null) },
                    label = { Text(text = "전체") },
                    colors = nearbyFilterChipColors()
                )
            }
            items(types, key = { it.name }) { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(text = type.label) },
                    colors = nearbyFilterChipColors()
                )
            }
        }
    }
}

@Composable
private fun PlaceRecommendationCard(place: Place, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            PlaceThumbnail(place = place)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(104.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Text(
                            text = place.name,
                            style = CardTitleStyle,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = place.distanceFromHospitalMeters?.toDistanceLabel().orEmpty(),
                            style = MaterialTheme.typography.labelMedium,
                            color = CoralPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(place.type.tint.copy(alpha = 0.12f))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(text = place.type.label, style = MaterialTheme.typography.labelSmall, color = place.type.tint, fontWeight = FontWeight.SemiBold)
                    }
                    Text(
                        text = place.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = place.description?.takeUnless { it.startsWith("http") } ?: "상세 정보를 확인해 방문 동선을 조정하세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PlaceThumbnail(place: Place, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 104.dp, height = 112.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(CoralPrimaryContainer, Color(0xFFF1F8FF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (place.imageUrl != null) {
            AsyncImageBox(
                model = place.imageUrl,
                contentDescription = place.name,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.48f))
                        )
                    )
            )
            Text(
                text = place.type.shortLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 10.dp, bottom = 10.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = place.type.tint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = place.type.shortLabel,
                style = MaterialTheme.typography.labelSmall,
                color = place.type.tint,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun nearbyFilterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = CoralPrimaryContainer,
    selectedLabelColor = CoralPrimary,
    labelColor = BadgeText,
    containerColor = Color.White
)

private val PlaceType.label: String
    get() = when (this) {
        PlaceType.TOURIST_ATTRACTION -> "관광지"
        PlaceType.RESTAURANT -> "카페·맛집"
        PlaceType.SHOPPING -> "쇼핑"
        PlaceType.LODGING -> "숙소"
        PlaceType.SPA -> "스파"
        PlaceType.WALK -> "산책"
        PlaceType.OTHER -> "기타"
    }

private val PlaceType.shortLabel: String
    get() = when (this) {
        PlaceType.TOURIST_ATTRACTION -> "TOUR"
        PlaceType.RESTAURANT -> "FOOD"
        PlaceType.SHOPPING -> "SHOP"
        PlaceType.LODGING -> "STAY"
        PlaceType.SPA -> "SPA"
        PlaceType.WALK -> "WALK"
        PlaceType.OTHER -> "ETC"
    }

private val PlaceType.tint: Color
    get() = when (this) {
        PlaceType.TOURIST_ATTRACTION -> SkyBlue
        PlaceType.RESTAURANT -> MediBlue40
        PlaceType.SHOPPING -> CoralPrimary
        PlaceType.LODGING -> MediBlue40
        PlaceType.SPA -> CoralPrimary
        PlaceType.WALK -> SkyBlue
        PlaceType.OTHER -> Color(0xFF667085)
    }

private fun Double.toDistanceLabel(): String =
    if (this < 1000.0) "${toInt()}m" else String.format("%.1fkm", this / 1000.0)

private fun Double.formatDistance(): String =
    if (this % 1.0 == 0.0) toInt().toString() else String.format("%.1f", this)

private fun durationLabel(minutes: Int): String =
    if (minutes >= 60) "${minutes / 60}시간 ${minutes % 60}분" else "${minutes}분"
