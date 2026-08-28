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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsCar
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
import androidx.compose.runtime.remember
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
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
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
    LaunchedEffect(categoryName, districtName) { viewModel.load(categoryName, districtName) }

    Scaffold(
        containerColor = HomeBackgroundPink,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
                title = { Text(strings.topBarTitle) }
            )
        }
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
                course = requireNotNull(uiState.course),
                route = requireNotNull(uiState.route),
                districtLabel = uiState.district?.label,
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
    course: RecommendedTourismCourse,
    route: DrivingRoute,
    districtLabel: String?,
    selectedStopId: String?,
    travelMode: TravelMode,
    isRouteRefreshing: Boolean,
    routeErrorMessage: String?,
    strings: CourseStrings,
    onSelectStop: (String) -> Unit,
    onTravelModeSelect: (TravelMode) -> Unit
) {
    val pins = remember(course, selectedStopId) {
        course.stops.map { stop ->
            MapPin(
                id = stop.item.id,
                latitude = requireNotNull(stop.item.latitude),
                longitude = requireNotNull(stop.item.longitude),
                type = MapPinType.TOURIST,
                selected = stop.item.id == selectedStopId,
                sequenceNumber = stop.order
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
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFE7E9), Color(0xFFFFF8F8), Color(0xFFEAF5FF))
                        )
                    )
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text(
                        strings.courseTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(strings.subtitle(districtLabel), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.9f)) {
                        Text(
                            text = strings.summary(course.stops.size, route, travelMode),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = CoralPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
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
                    modifier = Modifier.fillMaxSize()
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
            text = strings.transfer(
                kotlin.math.ceil(section.durationSeconds / 60.0).toInt().coerceAtLeast(1),
                section.distanceMeters / 1_000.0
            ),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
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
    val topBarTitle: String,
    val courseTitle: String,
    val notEnoughPlaces: String,
    val subtitle: (String?) -> String,
    val summary: (Int, DrivingRoute, TravelMode) -> String,
    val transfer: (Int, Double) -> String,
    val modeLabel: (TravelMode) -> String,
    val routeDisclaimer: (TravelMode) -> String
)

private fun SupportedLanguage.courseStrings(): CourseStrings = when (this) {
    SupportedLanguage.KO -> CourseStrings(
        back = "뒤로가기",
        topBarTitle = "추천 웰니스 코스",
        courseTitle = "나를 위한 부산 회복 코스",
        notEnoughPlaces = "코스를 만들 수 있는 위치 정보가 충분하지 않습니다.",
        subtitle = { district -> "${district ?: "부산"}에서 추천 점수와 이동 부담을 함께 고려했어요." },
        summary = { stops, route, mode -> "${stops}곳 · ${if (mode == TravelMode.DRIVING) "차량" else "도보"} 이동 약 ${durationKo(route.durationMinutes())} · ${formatKm(route.distanceKm())}" },
        transfer = { minutes, km -> "약 ${minutes}분 · ${formatKm(km)}" },
        modeLabel = { if (it == TravelMode.DRIVING) "자동차" else "도보" },
        routeDisclaimer = { if (it == TravelMode.DRIVING) "Kakao Mobility 추천 경로 기준이며 교통 상황에 따라 이동 시간이 달라질 수 있습니다." else "Kakao 도보 편안한 길 기준이며 현장 보행 환경에 따라 이동 시간이 달라질 수 있습니다." }
    )
    SupportedLanguage.EN -> CourseStrings(
        back = "Back",
        topBarTitle = "Recommended route",
        courseTitle = "Your Busan recovery course",
        notEnoughPlaces = "There are not enough places with location data to build a course.",
        subtitle = { district -> "Balanced for recommendation fit and travel effort around ${district ?: "Busan"}." },
        summary = { stops, route, mode -> "$stops stops · ${if (mode == TravelMode.DRIVING) "driving" else "walking"} about ${durationEn(route.durationMinutes())} · ${formatKm(route.distanceKm())}" },
        transfer = { minutes, km -> "About $minutes min · ${formatKm(km)}" },
        modeLabel = { if (it == TravelMode.DRIVING) "Car" else "Walk" },
        routeDisclaimer = { if (it == TravelMode.DRIVING) "Based on a Kakao Mobility recommended route; traffic may affect travel time." else "Based on Kakao's comfortable walking route; actual walking conditions may vary." }
    )
    SupportedLanguage.JA -> CourseStrings(
        back = "戻る",
        topBarTitle = "おすすめルート",
        courseTitle = "釜山リカバリーコース",
        notEnoughPlaces = "コース作成に必要な位置情報が不足しています。",
        subtitle = { district -> "${district ?: "釜山"}でおすすめ度と移動負担を考慮しました。" },
        summary = { stops, route, mode -> "$stops\u304b\u6240 \u00b7 ${if (mode == TravelMode.DRIVING) "\u8eca" else "\u5f92\u6b69"}\u3067\u7d04${durationJa(route.durationMinutes())} \u00b7 ${formatKm(route.distanceKm())}" },
        transfer = { minutes, km -> "\u7d04${minutes}\u5206 \u00b7 ${formatKm(km)}" },
        modeLabel = { if (it == TravelMode.DRIVING) "\u81ea\u52d5\u8eca" else "\u5f92\u6b69" },
        routeDisclaimer = { if (it == TravelMode.DRIVING) "Kakao Mobility\u306e\u63a8\u5968\u30eb\u30fc\u30c8\u3067\u3001\u4ea4\u901a\u72b6\u6cc1\u306b\u3088\u308a\u6240\u8981\u6642\u9593\u304c\u5909\u308f\u308b\u5834\u5408\u304c\u3042\u308a\u307e\u3059\u3002" else "Kakao\u306e\u6b69\u884c\u30eb\u30fc\u30c8\u3067\u3001\u73fe\u5730\u306e\u6b69\u884c\u74b0\u5883\u306b\u3088\u308a\u7570\u306a\u308b\u5834\u5408\u304c\u3042\u308a\u307e\u3059\u3002" }
    )
    SupportedLanguage.ZH -> CourseStrings(
        back = "返回",
        topBarTitle = "推荐路线",
        courseTitle = "釜山疗愈路线",
        notEnoughPlaces = "没有足够的地点位置信息来生成路线。",
        subtitle = { district -> "综合考虑了${district ?: "釜山"}的推荐度和移动距离。" },
        summary = { stops, route, mode -> "$stops\u5904 \u00b7 ${if (mode == TravelMode.DRIVING) "\u9a7e\u8f66" else "\u6b65\u884c"}\u7ea6${durationZh(route.durationMinutes())} \u00b7 ${formatKm(route.distanceKm())}" },
        transfer = { minutes, km -> "\u7ea6${minutes}\u5206\u949f \u00b7 ${formatKm(km)}" },
        modeLabel = { if (it == TravelMode.DRIVING) "\u6c7d\u8f66" else "\u6b65\u884c" },
        routeDisclaimer = { if (it == TravelMode.DRIVING) "\u57fa\u4e8e Kakao Mobility \u63a8\u8350\u8def\u7ebf\uff0c\u4ea4\u901a\u72b6\u51b5\u53ef\u80fd\u5f71\u54cd\u65f6\u95f4\u3002" else "\u57fa\u4e8e Kakao \u8212\u9002\u6b65\u884c\u8def\u7ebf\uff0c\u5b9e\u9645\u6b65\u884c\u73af\u5883\u53ef\u80fd\u6709\u6240\u4e0d\u540c\u3002" }
    )
}

private fun durationKo(minutes: Int): String = if (minutes >= 60) "${minutes / 60}시간 ${minutes % 60}분" else "${minutes}분"
private fun durationEn(minutes: Int): String = if (minutes >= 60) "${minutes / 60}h ${minutes % 60}m" else "${minutes} min"
private fun durationJa(minutes: Int): String = if (minutes >= 60) "${minutes / 60}時間${minutes % 60}分" else "${minutes}分"
private fun durationZh(minutes: Int): String = if (minutes >= 60) "${minutes / 60}小时${minutes % 60}分钟" else "${minutes}分钟"
private fun formatKm(distanceKm: Double): String = String.format(Locale.US, "%.1fkm", distanceKm)
private fun DrivingRoute.durationMinutes(): Int = kotlin.math.ceil(durationSeconds / 60.0).toInt().coerceAtLeast(1)
private fun DrivingRoute.distanceKm(): Double = distanceMeters / 1_000.0
