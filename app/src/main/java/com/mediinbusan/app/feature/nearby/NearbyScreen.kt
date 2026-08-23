package com.mediinbusan.app.feature.nearby

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
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
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.data.place.WellnessWalkingCourse
import com.mediinbusan.app.domain.course.WellnessCourse

@Composable
fun NearbyScreen(
    hospitalId: String,
    onSelectPlace: (String) -> Unit,
    onNavigateToMap: () -> Unit,
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
        onNavigateToMap = onNavigateToMap,
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
    onNavigateToMap: () -> Unit,
    onExploreTourism: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    val strings = LocalAppStrings.current
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
                    IconButton(onClick = onExploreTourism) {
                        Icon(imageVector = Icons.Default.Explore, contentDescription = strings.tourism.exploreTourismContentDescription)
                    }
                    IconButton(onClick = onNavigateToMap) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = "지도에서 보기")
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
                onNavigateToMap = onNavigateToMap,
                onExploreTourism = onExploreTourism,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun NearbyLoadedContent(
    uiState: NearbyUiState,
    onSelectPlace: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    onExploreTourism: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
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
            NearbySummaryCard(
                placeCount = uiState.places.size,
                nearestDistanceLabel = uiState.places.minOfOrNull { it.distanceFromHospitalMeters ?: Double.MAX_VALUE }
                    ?.takeIf { it != Double.MAX_VALUE }
                    ?.toDistanceLabel()
                    ?: "거리 정보 없음",
                onNavigateToMap = onNavigateToMap,
                onExploreTourism = onExploreTourism
            )
        }

        item {
            WellnessFocusSection(
                selectedFocus = selectedFocus,
                onFocusSelected = {
                    selectedFocus = it
                    selectedType = null
                }
            )
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
                text = String.format(strings.tourism.placesSummaryFormat, filteredPlaces.size),
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

        if (uiState.courses.isNotEmpty()) {
            item {
                WellnessCourseSection(courses = uiState.courses, onSelectPlace = onSelectPlace)
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
private fun NearbySummaryCard(
    placeCount: Int,
    nearestDistanceLabel: String,
    onNavigateToMap: () -> Unit,
    onExploreTourism: () -> Unit
) {
    val strings = LocalAppStrings.current
    Surface(
        shape = MaterialTheme.shapes.large,
        color = CoralPrimaryContainer,
        border = BorderStroke(1.dp, Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(CoralPrimaryContainer, Color.White, Color(0xFFEAF7FF))
                    )
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(118.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 42.dp, y = (-34).dp)
                    .clip(CircleShape)
                    .background(CoralPrimary.copy(alpha = 0.10f))
            )
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(text = "진료 전후 무리 없는 동선", style = SectionTitleStyle, color = TextPrimary)
                Text(
                    text = "병원 근처에서 바로 갈 수 있는 휴식·산책·스파·식사 장소를 거리순으로 정리했습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    InfoPill(text = "${placeCount}곳 추천")
                    InfoPill(text = "가장 가까운 곳 $nearestDistanceLabel")
                }
                Button(
                    onClick = onNavigateToMap,
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "지도에서 동선 보기")
                }
                OutlinedButton(onClick = onExploreTourism, modifier = Modifier.fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = strings.tourism.exploreTourismLabel)
                }
            }
        }
    }
}

@Composable
private fun WellnessCourseSection(courses: List<WellnessCourse>, onSelectPlace: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "웰니스 회복 코스", style = SectionTitleStyle, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(courses, key = { it.id }) { course ->
                WellnessCourseCard(course = course, onSelectPlace = onSelectPlace)
            }
        }
    }
}

@Composable
private fun WellnessCourseCard(course: WellnessCourse, onSelectPlace: (String) -> Unit) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = course.name,
                    style = CardTitleStyle,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${course.estimatedDurationMinutes / 60}h",
                    style = MaterialTheme.typography.labelMedium,
                    color = CoralPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = course.recommendationReason,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                course.places.forEachIndexed { index, place ->
                    CourseStopRow(index = index + 1, place = place, onClick = { onSelectPlace(place.id) })
                }
            }
            course.caution?.let {
                Text(text = it, style = MaterialTheme.typography.labelSmall, color = MediBlue40)
            }
        }
    }
}

@Composable
private fun CourseStopRow(index: Int, place: Place, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(CoralPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = index.toString(), style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
        Text(
            text = place.name,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
private fun PlaceThumbnail(place: Place) {
    Box(
        modifier = Modifier
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
private fun WalkingCourseSection(courses: List<WellnessWalkingCourse>) {
    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(text = strings.tourism.walkingSectionTitle, style = SectionTitleStyle, color = TextPrimary)
            Text(
                text = strings.tourism.walkingSectionSubtitle,
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
    val strings = LocalAppStrings.current
    val context = LocalContext.current
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
                course.durationMinutes?.let {
                    InfoPill(text = "${it / 60}${strings.tourism.walkingHourUnit} ${it % 60}${strings.tourism.walkingMinuteUnit}")
                }
                course.difficulty?.let { InfoPill(text = String.format(strings.tourism.walkingDifficultyFormat, it)) }
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
            course.gpxUrl?.let { gpxUrl ->
                OutlinedButton(
                    onClick = { context.launchIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse(gpxUrl))) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = strings.tourism.openGpxLabel)
                }
            }
        }
    }
}

@Composable
private fun EmptyPlaceFilter(onReset: () -> Unit) {
    val strings = LocalAppStrings.current
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
            Text(text = strings.tourism.emptyPlaceFilterMessage, style = CardTitleStyle, color = TextPrimary)
            OutlinedButton(onClick = onReset) {
                Text(text = strings.tourism.resetFilterLabel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WellnessFocusSection(selectedFocus: WellnessFocus, onFocusSelected: (WellnessFocus) -> Unit) {
    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = strings.tourism.todayFocusSectionTitle, style = SectionTitleStyle, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(WellnessFocus.entries, key = { it.name }) { focus ->
                FilterChip(
                    selected = selectedFocus == focus,
                    onClick = { onFocusSelected(focus) },
                    label = { Text(text = focus.label(strings.tourism)) },
                    colors = nearbyFilterChipColors()
                )
            }
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

private enum class WellnessFocus {
    ALL, REST, WALK, FOOD, STAY;

    fun label(strings: com.mediinbusan.app.core.i18n.TourismStrings): String = when (this) {
        ALL -> strings.focusAll
        REST -> strings.focusRest
        WALK -> strings.focusWalk
        FOOD -> strings.focusFood
        STAY -> strings.focusStay
    }

    fun filter(places: List<Place>): List<Place> = when (this) {
        ALL -> places
        REST -> places.filter { it.type in setOf(PlaceType.SPA, PlaceType.TOURIST_ATTRACTION) }
        WALK -> places.filter { it.type == PlaceType.WALK }
        FOOD -> places.filter { it.type == PlaceType.RESTAURANT }
        STAY -> places.filter { it.type == PlaceType.LODGING }
    }
}

private val WellnessCanvas = Color(0xFFFFFAFF)
