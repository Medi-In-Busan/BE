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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import com.mediinbusan.app.data.place.WellnessWalkingCourse
import com.mediinbusan.app.domain.course.HospitalWellnessRoute

@Composable
fun NearbyScreen(
    hospitalId: String,
    onSelectPlace: (String) -> Unit,
    onNavigateToNearbyMap: () -> Unit,
    onNavigateToCourseMap: () -> Unit,
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
    onNavigateToNearbyMap: () -> Unit,
    onNavigateToCourseMap: () -> Unit,
    onExploreTourism: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        containerColor = WellnessCanvas,
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
    onNavigateToCourseMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf<PlaceType?>(null) }
    var selectedFocus by remember { mutableStateOf(WellnessFocus.ALL) }
    val types = uiState.places.map { it.type }.distinct()
    val typeFilteredPlaces = selectedType?.let { selected -> uiState.places.filter { it.type == selected } } ?: uiState.places
    val filteredPlaces = selectedFocus.filter(typeFilteredPlaces)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            WellnessFocusSection(
                selectedFocus = selectedFocus,
                onFocusSelected = {
                    selectedFocus = it
                    selectedType = null
                }
            )
        }

        uiState.recommendedRoute?.let { route ->
            item {
                RecommendedRouteSection(route = route, onClick = onNavigateToCourseMap)
            }
        }

        item {
            PlaceFilterSection(
                types = types,
                selectedType = selectedType,
                onTypeSelected = {
                    selectedType = it
                    selectedFocus = WellnessFocus.ALL
                }
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
                    selectedFocus = WellnessFocus.ALL
                    selectedType = null
                })
            }
        } else {
            items(filteredPlaces, key = { it.id }) { place ->
                PlaceRecommendationCard(place = place, onClick = { onSelectPlace(place.id) })
            }
        }

        if (uiState.walkingCourses.isNotEmpty()) {
            item {
                WalkingCourseSection(courses = uiState.walkingCourses)
            }
        }
    }
}

@Composable
private fun WalkingCourseSection(courses: List<WellnessWalkingCourse>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(text = "부산 걷기 여행", style = SectionTitleStyle, color = TextPrimary)
            Text(
                text = "두루누비 걷기여행길 중 부산 코스",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(courses, key = { it.id }) { course ->
                WalkingCourseCard(course = course)
            }
        }
    }
}

@Composable
private fun WalkingCourseCard(course: WellnessWalkingCourse) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = course.name,
                style = CardTitleStyle,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = course.district,
                style = MaterialTheme.typography.labelMedium,
                color = SkyBlue
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                course.distanceKm?.let { InfoPill(text = "${it.formatDistance()}km") }
                course.durationMinutes?.let { InfoPill(text = "${it / 60}시간 ${it % 60}분") }
                course.difficulty?.let { InfoPill(text = "난이도 $it") }
            }
            course.summary?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WellnessFocusSection(selectedFocus: WellnessFocus, onFocusSelected: (WellnessFocus) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "오늘의 목적", style = SectionTitleStyle, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(WellnessFocus.entries, key = { it.name }) { focus ->
                FilterChip(
                    selected = selectedFocus == focus,
                    onClick = { onFocusSelected(focus) },
                    label = { Text(text = focus.label) },
                    colors = nearbyFilterChipColors()
                )
            }
        }
    }
}

@Composable
private fun RecommendedRouteSection(route: HospitalWellnessRoute, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "추천 웰니스 코스", style = SectionTitleStyle, color = TextPrimary)
        RecommendedRouteCard(route = route, onClick = onClick)
    }
}

@Composable
private fun RecommendedRouteCard(route: HospitalWellnessRoute, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            RouteThumbnail(route = route)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "나를 위한 부산 회복 코스",
                        style = CardTitleStyle,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
                    RouteBadge(text = "${route.totalDistanceKm.formatDistance()}km")
                }
                Text(
                    text = "코스를 눌러 지도에서 실제 동선을 확인하세요.",
                    style = MaterialTheme.typography.labelSmall,
                    color = CoralPrimary
                )
            }
        }
    }
}

@Composable
private fun RouteThumbnail(route: HospitalWellnessRoute) {
    Box(
        modifier = Modifier
            .size(width = 104.dp, height = 136.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(CoralPrimaryContainer, Color(0xFFEAF7FF))
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Map, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(28.dp))
        }
        Text(
            text = "${route.stops.size} STOPS",
            style = MaterialTheme.typography.labelSmall,
            color = CoralPrimary,
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
private fun InfoPill(text: String) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(Color.White)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
    }
}

@Composable
private fun nearbyFilterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = CoralPrimaryContainer,
    selectedLabelColor = CoralPrimary,
    labelColor = BadgeText,
    containerColor = Color.White
)

private enum class WellnessFocus(val label: String) {
    ALL("전체"),
    REST("휴식"),
    WALK("산책"),
    FOOD("식사"),
    STAY("숙소") ;

    fun filter(places: List<Place>): List<Place> = when (this) {
        ALL -> places
        REST -> places.filter { it.type in setOf(PlaceType.SPA, PlaceType.TOURIST_ATTRACTION) }
        WALK -> places.filter { it.type == PlaceType.WALK }
        FOOD -> places.filter { it.type == PlaceType.RESTAURANT }
        STAY -> places.filter { it.type == PlaceType.LODGING }
    }
}

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

private val WellnessCanvas = Color(0xFFFFFAFF)
