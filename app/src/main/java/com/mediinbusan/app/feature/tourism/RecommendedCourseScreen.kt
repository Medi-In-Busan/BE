package com.mediinbusan.app.feature.tourism

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.RouteEndpointKind
import com.mediinbusan.app.core.ui.MapRoutePath
import com.mediinbusan.app.core.ui.MapRoutePoint
import com.mediinbusan.app.data.route.DrivingRoute
import com.mediinbusan.app.data.route.DrivingRouteSection
import com.mediinbusan.app.data.route.TravelMode
import com.mediinbusan.app.domain.tourism.RecommendedTourismCourse
import com.mediinbusan.app.domain.tourism.RecommendedTourismStop
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedCourseScreen(
    categoryName: String,
    districtName: String?,
    onBack: () -> Unit,
    viewModel: RecommendedCourseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = uiState.language.courseStrings()
    val currentLanguage = LocalAppStrings.current.language
    LaunchedEffect(categoryName, districtName, currentLanguage) {
        viewModel.load(categoryName, districtName)
    }

    Scaffold(
        containerColor = Color.White,
        // 탑바를 아예 없애 히어로 사진이 화면 맨 위(상태바 아래까지)까지 올라오게 하고, 뒤로가기는
        // TourismCourseHero 안에서 사진 위에 떠 있는 아이콘으로 대신한다.
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(innerPadding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage.orEmpty(),
                modifier = Modifier.padding(innerPadding),
                onRetry = { viewModel.load(categoryName, districtName) }
            )
            uiState.course == null || uiState.route == null -> EmptyState(
                message = strings.notEnoughPlaces,
                modifier = Modifier.padding(innerPadding)
            )
            else -> CourseContent(
                modifier = Modifier.padding(innerPadding),
                onBack = onBack,
                course = requireNotNull(uiState.course),
                route = requireNotNull(uiState.route),
                courseTitle = recommendedCourseTitle(
                    course = requireNotNull(uiState.course),
                    districtLabel = uiState.district?.translatedLabel(uiState.language),
                    language = uiState.language
                ),
                districtLabel = uiState.district?.translatedLabel(uiState.language),
                selectedStopId = uiState.selectedStopId,
                travelMode = uiState.travelMode,
                isRouteRefreshing = uiState.isRouteRefreshing,
                routeErrorMessage = uiState.routeErrorMessage,
                strings = strings,
                onSelectStop = viewModel::selectStop,
                onTravelModeSelect = viewModel::selectTravelMode
            )
        }
    }
}

@Composable
private fun CourseContent(
    modifier: Modifier,
    onBack: () -> Unit,
    course: RecommendedTourismCourse,
    route: DrivingRoute,
    courseTitle: String,
    districtLabel: String?,
    selectedStopId: String?,
    travelMode: TravelMode,
    isRouteRefreshing: Boolean,
    routeErrorMessage: String?,
    strings: CourseStrings,
    onSelectStop: (String) -> Unit,
    onTravelModeSelect: (TravelMode) -> Unit
) {
    val listState = rememberLazyListState()
    var zoomInRequestId by remember { mutableIntStateOf(0) }
    var zoomOutRequestId by remember { mutableIntStateOf(0) }
    var isMapInteractionActive by remember { mutableStateOf(false) }
    // 첫/마지막 정거장은 번호 대신 출발·도착 전용 마커(course_detail_start/end)로 바꾸고, 그 사이
    // 경유지들은 1번부터 다시 순서를 매긴다(예: 5개 코스면 1=시작, 2·3·4→1·2·3, 5=도착).
    val pins = remember(course, selectedStopId) {
        val lastIndex = course.stops.lastIndex
        course.stops.mapIndexed { index, stop ->
            val endpointKind = when (index) {
                0 -> RouteEndpointKind.START
                lastIndex -> RouteEndpointKind.END
                else -> null
            }
            MapPin(
                id = stop.item.id,
                latitude = requireNotNull(stop.item.latitude),
                longitude = requireNotNull(stop.item.longitude),
                type = MapPinType.TOURIST,
                selected = stop.item.id == selectedStopId,
                sequenceNumber = if (endpointKind == null) index else null,
                endpointKind = endpointKind
            )
        }
    }
    val paths = remember(route) {
        listOf(
            MapRoutePath(
                id = "recommended-course",
                points = route.path.map { MapRoutePoint(it.latitude, it.longitude) }
            )
        )
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = !isMapInteractionActive,
        contentPadding = PaddingValues(bottom = BottomNavBarHeight + 36.dp)
    ) {
        item {
            TourismCourseHero(
                course = course,
                route = route,
                courseTitle = courseTitle,
                districtLabel = districtLabel,
                selectedStopId = selectedStopId,
                travelMode = travelMode,
                strings = strings,
                onSelectStop = onSelectStop,
                onBack = onBack
            )
        }
        item {
            TravelModeSelector(
                selectedMode = travelMode,
                isLoading = isRouteRefreshing,
                onSelect = onTravelModeSelect,
                strings = strings,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
            routeErrorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 20.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.18f),
                        spotColor = Color.Black.copy(alpha = 0.18f)
                    )
                    .clip(RoundedCornerShape(20.dp))
            ) {
                KakaoMapView(
                    pins = pins,
                    routePaths = paths,
                    onPinClick = onSelectStop,
                    zoomInRequestId = zoomInRequestId,
                    zoomOutRequestId = zoomOutRequestId,
                    onMapInteractionChange = { isMapInteractionActive = it },
                    modifier = Modifier.fillMaxSize()
                )
                MapZoomControls(
                    onZoomIn = { zoomInRequestId++ },
                    onZoomOut = { zoomOutRequestId++ },
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                )
            }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                course.stops.forEachIndexed { index, stop ->
                    if (index > 0) {
                        TransferLeg(
                            section = route.sections[index - 1],
                            travelMode = travelMode,
                            strings = strings
                        )
                    }
                    CourseStopRow(
                        stop = stop,
                        selected = stop.item.id == selectedStopId,
                        onClick = { onSelectStop(stop.item.id) }
                    )
                }
                Text(
                    text = strings.routeDisclaimer(travelMode),
                    modifier = Modifier.padding(top = 18.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun TourismCourseHero(
    course: RecommendedTourismCourse,
    route: DrivingRoute,
    courseTitle: String,
    districtLabel: String?,
    selectedStopId: String?,
    travelMode: TravelMode,
    strings: CourseStrings,
    onSelectStop: (String) -> Unit,
    onBack: () -> Unit
) {
    val selectedStop = course.stops.firstOrNull { it.item.id == selectedStopId }
        ?: course.stops.first()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = Color.Black.copy(alpha = 0.16f),
                spotColor = Color.Black.copy(alpha = 0.16f)
            ),
        shape = RoundedCornerShape(26.dp),
        color = Color.White
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(217.dp)
                    .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                    .background(CoralPrimaryContainer)
            ) {
                AnimatedContent(
                    targetState = selectedStop.item,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tourism-course-stop-image",
                    modifier = Modifier.fillMaxSize()
                ) { item ->
                    if (item.imageUrl != null) {
                        AsyncImageBox(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(CoralPrimaryContainer, Color(0xFFEAF7FF))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.12f),
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    districtLabel?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Text(
                        text = courseTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${selectedStop.order}. ${selectedStop.item.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.94f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    selectedStop.item.address?.takeIf { it.isNotBlank() }?.let { address ->
                        Text(
                            text = address,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.72f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // 탑바가 없어진 자리를 대신해 사진 좌측 상단에 떠 있는 뒤로가기. 위/왼쪽 여백을
                // 똑같이 4dp로 맞추기 위해 statusBarsPadding은 쓰지 않는다(상태바 높이만큼 위쪽
                // 여백이 왼쪽보다 훨씬 커지는 걸 막기 위해 대칭을 우선한다).
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = LocalAppStrings.current.common.backContentDescription,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = strings.summary(course.stops.size, route, travelMode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                TourismCourseStopSelector(
                    stops = course.stops,
                    selectedStopId = selectedStop.item.id,
                    onSelectStop = onSelectStop
                )
            }
        }
    }
}

@Composable
private fun TourismCourseStopSelector(
    stops: List<RecommendedTourismStop>,
    selectedStopId: String,
    onSelectStop: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(DividerColor)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            stops.forEach { stop ->
                val selected = stop.item.id == selectedStopId
                Box(
                    modifier = Modifier
                        .size(if (selected) 36.dp else 30.dp)
                        .clip(CircleShape)
                        .background(if (selected) CoralPrimary else Color(0xFFF1EFEC))
                        .clickable { onSelectStop(stop.item.id) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stop.order.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Color.White else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MapZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.language.courseStrings()
    Surface(
        modifier = modifier.shadow(
            elevation = 5.dp,
            shape = RoundedCornerShape(14.dp),
            ambientColor = Color.Black.copy(alpha = 0.22f),
            spotColor = Color.Black.copy(alpha = 0.22f)
        ),
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.96f)
    ) {
        Column {
            IconButton(onClick = onZoomIn, modifier = Modifier.size(42.dp)) {
                Icon(Icons.Default.Add, contentDescription = strings.zoomIn, tint = TextPrimary)
            }
            Box(modifier = Modifier.width(42.dp).height(1.dp).background(DividerColor))
            IconButton(onClick = onZoomOut, modifier = Modifier.size(42.dp)) {
                Icon(Icons.Default.Remove, contentDescription = strings.zoomOut, tint = TextPrimary)
            }
        }
    }
}

@Composable
private fun TravelModeSelector(
    selectedMode: TravelMode,
    isLoading: Boolean,
    onSelect: (TravelMode) -> Unit,
    strings: CourseStrings,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF3F1F0)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            TravelMode.entries.forEach { mode ->
                val selected = selectedMode == mode
                Surface(
                    modifier = Modifier.weight(1f).clickable(enabled = !isLoading) { onSelect(mode) },
                    shape = RoundedCornerShape(13.dp),
                    color = if (selected) Color.White else Color.Transparent,
                    shadowElevation = if (selected) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoading && !selected) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(17.dp),
                                strokeWidth = 2.dp,
                                color = CoralPrimary
                            )
                        } else {
                            Icon(
                                imageVector = if (mode == TravelMode.DRIVING) Icons.Default.DirectionsCar else Icons.AutoMirrored.Filled.DirectionsWalk,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (selected) CoralPrimary else TextSecondary
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = strings.modeLabel(mode),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) TextPrimary else TextSecondary,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransferLeg(section: DrivingRouteSection, travelMode: TravelMode, strings: CourseStrings) {
    Row(
        modifier = Modifier.padding(start = 19.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DottedTransferLine(modifier = Modifier.width(4.dp).height(34.dp))
        Spacer(Modifier.width(18.dp))
        Icon(
            if (travelMode == TravelMode.DRIVING) Icons.Default.DirectionsCar else Icons.AutoMirrored.Filled.DirectionsWalk,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = strings.transfer(
                kotlin.math.ceil(section.durationSeconds / 60.0).toInt().coerceAtLeast(1),
                section.distanceMeters / 1_000.0
            ),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

// 정거장 사이 이동 구간을 잇는 세로선을 원형 점 5개로 표시한다. dash 패턴 대신 높이를 5등분해서
// 각 구간 중앙에 점을 하나씩 찍어 개수를 정확히 고정한다.
@Composable
private fun DottedTransferLine(modifier: Modifier = Modifier, dotCount: Int = 5) {
    val dotColor = CoralPrimary.copy(alpha = 0.5f)
    Canvas(modifier = modifier) {
        val radius = size.width / 2f
        val centerX = size.width / 2f
        repeat(dotCount) { index ->
            val centerY = size.height * (index + 0.5f) / dotCount
            drawCircle(color = dotColor, radius = radius, center = Offset(centerX, centerY))
        }
    }
}

@Composable
private fun CourseStopRow(stop: RecommendedTourismStop, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (selected) 5.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) CoralPrimaryContainer else Color.White,
        border = BorderStroke(1.dp, if (selected) CoralPrimary.copy(alpha = 0.35f) else DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape).background(CoralPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(stop.order.toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stop.item.title, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val detail = stop.item.subtitle ?: stop.item.address
                if (!detail.isNullOrBlank()) {
                    Text(detail, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            stop.item.imageUrl?.let { imageUrl ->
                AsyncImageBox(
                    model = imageUrl,
                    contentDescription = stop.item.title,
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(14.dp))
                )
            }
        }
    }
}

private data class CourseStrings(
    val back: String,
    val notEnoughPlaces: String,
    val summary: (Int, DrivingRoute, TravelMode) -> String,
    val transfer: (Int, Double) -> String,
    val modeLabel: (TravelMode) -> String,
    val routeDisclaimer: (TravelMode) -> String,
    val zoomIn: String,
    val zoomOut: String
)

private fun SupportedLanguage.courseStrings(): CourseStrings = when (this) {
    SupportedLanguage.KO -> CourseStrings(
        back = "뒤로가기",
        notEnoughPlaces = "코스를 만들 수 있는 위치 정보가 충분하지 않습니다.",
        summary = { stops, route, mode -> "${stops}곳 · ${if (mode == TravelMode.DRIVING) "차량" else "도보"} 이동 약 ${durationKo(route.durationMinutes())} · ${formatKm(route.distanceKm())}" },
        transfer = { minutes, km -> "약 ${minutes}분 · ${formatKm(km)}" },
        modeLabel = { if (it == TravelMode.DRIVING) "자동차" else "도보" },
        routeDisclaimer = { if (it == TravelMode.DRIVING) "Kakao Mobility 추천 경로 기준이며 교통 상황에 따라 이동 시간이 달라질 수 있습니다." else "Kakao 도보 편안한 길 기준이며 현장 보행 환경에 따라 이동 시간이 달라질 수 있습니다." },
        zoomIn = "지도 확대", zoomOut = "지도 축소"
    )
    SupportedLanguage.EN -> CourseStrings(
        back = "Back",
        notEnoughPlaces = "There are not enough places with location data to build a course.",
        summary = { stops, route, mode -> "$stops stops · ${if (mode == TravelMode.DRIVING) "driving" else "walking"} about ${durationEn(route.durationMinutes())} · ${formatKm(route.distanceKm())}" },
        transfer = { minutes, km -> "About $minutes min · ${formatKm(km)}" },
        modeLabel = { if (it == TravelMode.DRIVING) "Car" else "Walk" },
        routeDisclaimer = { if (it == TravelMode.DRIVING) "Based on a Kakao Mobility recommended route; traffic may affect travel time." else "Based on Kakao's comfortable walking route; actual walking conditions may vary." },
        zoomIn = "Zoom in", zoomOut = "Zoom out"
    )
    SupportedLanguage.JA -> CourseStrings(
        back = "戻る",
        notEnoughPlaces = "コース作成に必要な位置情報が不足しています。",
        summary = { stops, route, mode -> "$stops\u304b\u6240 \u00b7 ${if (mode == TravelMode.DRIVING) "\u8eca" else "\u5f92\u6b69"}\u3067\u7d04${durationJa(route.durationMinutes())} \u00b7 ${formatKm(route.distanceKm())}" },
        transfer = { minutes, km -> "\u7d04${minutes}\u5206 \u00b7 ${formatKm(km)}" },
        modeLabel = { if (it == TravelMode.DRIVING) "\u81ea\u52d5\u8eca" else "\u5f92\u6b69" },
        routeDisclaimer = { if (it == TravelMode.DRIVING) "Kakao Mobility\u306e\u63a8\u5968\u30eb\u30fc\u30c8\u3067\u3001\u4ea4\u901a\u72b6\u6cc1\u306b\u3088\u308a\u6240\u8981\u6642\u9593\u304c\u5909\u308f\u308b\u5834\u5408\u304c\u3042\u308a\u307e\u3059\u3002" else "Kakao\u306e\u6b69\u884c\u30eb\u30fc\u30c8\u3067\u3001\u73fe\u5730\u306e\u6b69\u884c\u74b0\u5883\u306b\u3088\u308a\u7570\u306a\u308b\u5834\u5408\u304c\u3042\u308a\u307e\u3059\u3002" },
        zoomIn = "地図を拡大", zoomOut = "地図を縮小"
    )
    SupportedLanguage.ZH -> CourseStrings(
        back = "返回",
        notEnoughPlaces = "没有足够的地点位置信息来生成路线。",
        summary = { stops, route, mode -> "$stops\u5904 \u00b7 ${if (mode == TravelMode.DRIVING) "\u9a7e\u8f66" else "\u6b65\u884c"}\u7ea6${durationZh(route.durationMinutes())} \u00b7 ${formatKm(route.distanceKm())}" },
        transfer = { minutes, km -> "\u7ea6${minutes}\u5206\u949f \u00b7 ${formatKm(km)}" },
        modeLabel = { if (it == TravelMode.DRIVING) "\u6c7d\u8f66" else "\u6b65\u884c" },
        routeDisclaimer = { if (it == TravelMode.DRIVING) "\u57fa\u4e8e Kakao Mobility \u63a8\u8350\u8def\u7ebf\uff0c\u4ea4\u901a\u72b6\u51b5\u53ef\u80fd\u5f71\u54cd\u65f6\u95f4\u3002" else "\u57fa\u4e8e Kakao \u8212\u9002\u6b65\u884c\u8def\u7ebf\uff0c\u5b9e\u9645\u6b65\u884c\u73af\u5883\u53ef\u80fd\u6709\u6240\u4e0d\u540c\u3002" },
        zoomIn = "放大地图", zoomOut = "缩小地图"
    )
}

private fun durationKo(minutes: Int): String = if (minutes >= 60) "${minutes / 60}시간 ${minutes % 60}분" else "${minutes}분"
private fun durationEn(minutes: Int): String = if (minutes >= 60) "${minutes / 60}h ${minutes % 60}m" else "${minutes} min"
private fun durationJa(minutes: Int): String = if (minutes >= 60) "${minutes / 60}時間${minutes % 60}分" else "${minutes}分"
private fun durationZh(minutes: Int): String = if (minutes >= 60) "${minutes / 60}小时${minutes % 60}分钟" else "${minutes}分钟"
private fun formatKm(distanceKm: Double): String = String.format(Locale.US, "%.1fkm", distanceKm)
private fun DrivingRoute.durationMinutes(): Int = kotlin.math.ceil(durationSeconds / 60.0).toInt().coerceAtLeast(1)
private fun DrivingRoute.distanceKm(): Double = distanceMeters / 1_000.0

private fun recommendedCourseTitle(
    course: RecommendedTourismCourse,
    districtLabel: String?,
    language: SupportedLanguage
): String {
    val region = districtLabel ?: when (language) {
        SupportedLanguage.KO -> "부산"
        SupportedLanguage.EN -> "Busan"
        SupportedLanguage.JA -> "釜山"
        SupportedLanguage.ZH -> "釜山"
    }
    val themes = course.stops
        .mapNotNull { stop -> courseTheme(stop.item.categoryCode, stop.item.title, language) }
        .distinct()
        .take(2)
    if (themes.isEmpty()) {
        val firstPlace = course.stops.first().item.title
        return when (language) {
            SupportedLanguage.KO -> "$region $firstPlace 중심 코스"
            SupportedLanguage.EN -> "$region route featuring $firstPlace"
            SupportedLanguage.JA -> "$region・${firstPlace}中心コース"
            SupportedLanguage.ZH -> "$region · 以${firstPlace}为中心的路线"
        }
    }
    return when (language) {
        SupportedLanguage.KO -> "$region ${themes.joinToString("·")} 코스"
        SupportedLanguage.EN -> "$region ${themes.joinToString(" & ")} route"
        SupportedLanguage.JA -> "$region・${themes.joinToString("・")}コース"
        SupportedLanguage.ZH -> "$region · ${themes.joinToString("·")}路线"
    }
}

private fun courseTheme(
    categoryCode: String?,
    title: String,
    language: SupportedLanguage
): String? {
    val theme = when (categoryCode) {
        "12" -> attractionTheme(title)
        "14" -> CourseTheme.CULTURE
        "15" -> CourseTheme.FESTIVAL
        "25" -> CourseTheme.WALK
        "28" -> CourseTheme.ACTIVITY
        "32" -> CourseTheme.REST
        "38" -> CourseTheme.SHOPPING
        "39" -> CourseTheme.FOOD
        else -> null
    } ?: return null
    return when (language) {
        SupportedLanguage.KO -> theme.ko
        SupportedLanguage.EN -> theme.en
        SupportedLanguage.JA -> theme.ja
        SupportedLanguage.ZH -> theme.zh
    }
}

private fun attractionTheme(title: String): CourseTheme {
    val normalized = title.lowercase()
    return when {
        COAST_KEYWORDS.any(normalized::contains) -> CourseTheme.COAST
        WALK_KEYWORDS.any(normalized::contains) -> CourseTheme.WALK
        CULTURE_KEYWORDS.any(normalized::contains) -> CourseTheme.CULTURE
        else -> CourseTheme.LANDMARK
    }
}

private enum class CourseTheme(
    val ko: String,
    val en: String,
    val ja: String,
    val zh: String
) {
    COAST("바다", "coast", "海辺", "海滨"),
    WALK("산책", "walking", "散策", "漫步"),
    CULTURE("문화", "culture", "文化", "文化"),
    FESTIVAL("축제", "festival", "祭り", "节庆"),
    ACTIVITY("액티비티", "activities", "アクティビティ", "体验"),
    REST("휴식", "relaxation", "休息", "休闲"),
    SHOPPING("쇼핑", "shopping", "ショッピング", "购物"),
    FOOD("미식", "food", "グルメ", "美食"),
    LANDMARK("명소", "landmarks", "名所", "名胜")
}

private val COAST_KEYWORDS = listOf(
    "해수욕장", "해변", "바다", "beach", "coast", "海水浴場", "海辺", "海滩", "海滨"
)
private val WALK_KEYWORDS = listOf(
    "공원", "산", "숲", "둘레길", "산책", "park", "mountain", "forest", "trail", "公園", "公园", "森林", "散策"
)
private val CULTURE_KEYWORDS = listOf(
    "박물관", "미술관", "문화", "책방", "기념관", "사(", "museum", "gallery", "temple", "文化", "博物館", "博物馆", "美術館", "美术馆"
)
