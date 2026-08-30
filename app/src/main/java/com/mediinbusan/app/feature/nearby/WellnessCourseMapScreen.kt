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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BackOnlyNavigationBar
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.MapRoutePath
import com.mediinbusan.app.core.ui.MapRoutePoint
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.data.route.TravelMode
import com.mediinbusan.app.domain.course.HospitalWellnessRoute
import com.mediinbusan.app.domain.course.HospitalWellnessStop
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessCourseMapScreen(
    hospitalId: String,
    courseIndex: Int = 0,
    onBack: () -> Unit,
    viewModel: WellnessCourseMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mapFocusRequestId by remember { mutableIntStateOf(0) }
    LaunchedEffect(hospitalId, courseIndex) { viewModel.load(hospitalId, courseIndex) }

    Scaffold(
        containerColor = WellnessCourseCanvas,
        topBar = {
            BackOnlyNavigationBar(
                onBack = onBack,
                background = WellnessCourseCanvas,
                onMapDetailsClick = uiState.route?.let { { mapFocusRequestId++ } }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(innerPadding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage.orEmpty(),
                modifier = Modifier.padding(innerPadding),
                onRetry = { viewModel.load(hospitalId, courseIndex) }
            )
            uiState.route != null -> WellnessRouteContent(
                route = requireNotNull(uiState.route),
                selectedId = uiState.selectedId,
                travelMode = uiState.travelMode,
                isRouteRefreshing = uiState.isRouteRefreshing,
                routeErrorMessage = uiState.routeErrorMessage,
                mapFocusRequestId = mapFocusRequestId,
                onSelect = viewModel::select,
                onTravelModeSelect = viewModel::selectTravelMode,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun WellnessRouteContent(
    route: HospitalWellnessRoute,
    selectedId: String?,
    travelMode: TravelMode,
    isRouteRefreshing: Boolean,
    routeErrorMessage: String?,
    mapFocusRequestId: Int,
    onSelect: (String) -> Unit,
    onTravelModeSelect: (TravelMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var isMapInteractionActive by remember { mutableStateOf(false) }
    val pins = remember(route, selectedId) {
        buildList {
            add(
                MapPin(
                    id = route.hospital.id,
                    latitude = requireNotNull(route.hospital.latitude),
                    longitude = requireNotNull(route.hospital.longitude),
                    type = MapPinType.HOSPITAL,
                    selected = selectedId == route.hospital.id
                )
            )
            route.stops.forEach { stop ->
                add(
                    MapPin(
                        id = stop.place.id,
                        latitude = requireNotNull(stop.place.latitude),
                        longitude = requireNotNull(stop.place.longitude),
                        type = if (stop.place.type == PlaceType.RESTAURANT) MapPinType.FOOD else MapPinType.TOURIST,
                        selected = selectedId == stop.place.id,
                        sequenceNumber = stop.order
                    )
                )
            }
        }
    }
    val routePath = remember(route) {
        listOf(
            MapRoutePath(
                id = "hospital-wellness-${route.hospital.id}",
                points = route.roadPath.map { MapRoutePoint(it.latitude, it.longitude) }
            )
        )
    }

    LaunchedEffect(mapFocusRequestId) {
        if (mapFocusRequestId > 0) listState.animateScrollToItem(COURSE_MAP_ITEM_INDEX)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = !isMapInteractionActive,
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${route.hospital.name}에서 시작하는 코스",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = LocalAppStrings.current.nearby.courseMapDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Surface(shape = CircleShape, color = CoralPrimaryContainer) {
                    Text(
                        text = "${LocalAppStrings.current.nearby.stopCountFormat.format(route.stops.size)} · ${travelMode.summaryLabel()} ${durationLabel(route.estimatedDurationMinutes)} · ${distanceLabel(route.totalDistanceKm)}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = CoralPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        item {
            TravelModeSelector(
                selectedMode = travelMode,
                isLoading = isRouteRefreshing,
                onSelect = onTravelModeSelect,
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
                    .clip(RoundedCornerShape(24.dp))
            ) {
                KakaoMapView(
                    pins = pins,
                    routePaths = routePath,
                    onPinClick = onSelect,
                    onMapInteractionChange = { isMapInteractionActive = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                HospitalOriginRow(
                    route = route,
                    selected = selectedId == route.hospital.id,
                    onClick = { onSelect(route.hospital.id) }
                )
                route.stops.forEach { stop ->
                    TransferRow(stop, travelMode)
                    WellnessStopRow(
                        stop = stop,
                        selected = selectedId == stop.place.id,
                        onClick = { onSelect(stop.place.id) }
                    )
                }
                Text(
                    text = travelMode.disclaimer(),
                    modifier = Modifier.padding(top = 18.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

private const val COURSE_MAP_ITEM_INDEX = 2

@Composable
private fun TravelModeSelector(
    selectedMode: TravelMode,
    isLoading: Boolean,
    onSelect: (TravelMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
                            text = mode.buttonLabel(),
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
private fun HospitalOriginRow(route: HospitalWellnessRoute, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
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
                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White, modifier = Modifier.size(21.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    LocalAppStrings.current.nearby.departureFormat.format(route.hospital.name),
                    style = CardTitleStyle,
                    color = TextPrimary
                )
                Text(route.hospital.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun TransferRow(stop: HospitalWellnessStop, travelMode: TravelMode) {
    Row(
        modifier = Modifier.padding(start = 19.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(2.dp).height(34.dp).background(CoralPrimary.copy(alpha = 0.35f)))
        Spacer(Modifier.width(18.dp))
        Icon(
            if (travelMode == TravelMode.DRIVING) Icons.Default.DirectionsCar else Icons.AutoMirrored.Filled.DirectionsWalk,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(7.dp))
        Text(
            LocalAppStrings.current.nearby.transferFormat.format(
                stop.transferMinutes,
                distanceLabel(stop.distanceFromPreviousKm)
            ),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun WellnessStopRow(stop: HospitalWellnessStop, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
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
                Text(stop.place.name, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(stop.place.type.label(), style = MaterialTheme.typography.bodySmall, color = CoralPrimary)
                if (stop.place.address.isNotBlank()) {
                    Text(stop.place.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            stop.place.imageUrl?.let { imageUrl ->
                AsyncImageBox(
                    model = imageUrl,
                    contentDescription = stop.place.name,
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(14.dp))
                )
            }
        }
    }
}

@Composable
private fun PlaceType.label(): String = LocalAppStrings.current.nearby.placeTypeLabels[name].orEmpty()

@Composable
private fun durationLabel(minutes: Int): String {
    val strings = LocalAppStrings.current.tourism
    return if (minutes >= 60) "${minutes / 60}${strings.walkingHourUnit} ${minutes % 60}${strings.walkingMinuteUnit}"
    else "${minutes}${strings.walkingMinuteUnit}"
}

private fun distanceLabel(distanceKm: Double): String = String.format(Locale.US, "%.1fkm", distanceKm)

@Composable
private fun TravelMode.buttonLabel(): String = if (this == TravelMode.DRIVING) LocalAppStrings.current.nearby.drivingLabel else LocalAppStrings.current.nearby.walkingLabel

@Composable
private fun TravelMode.summaryLabel(): String = if (this == TravelMode.DRIVING) LocalAppStrings.current.nearby.drivingSummaryLabel else LocalAppStrings.current.nearby.walkingSummaryLabel

@Composable
private fun TravelMode.disclaimer(): String = if (this == TravelMode.DRIVING) LocalAppStrings.current.nearby.drivingDisclaimer else LocalAppStrings.current.nearby.walkingDisclaimer

private val WellnessCourseCanvas = Color(0xFFFFFAF7)
