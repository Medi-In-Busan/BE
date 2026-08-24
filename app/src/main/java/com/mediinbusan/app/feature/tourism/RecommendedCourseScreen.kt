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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.CenterAlignedTopAppBar
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
        containerColor = TourismCanvas,
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
            uiState.course == null -> EmptyState(
                message = strings.notEnoughPlaces,
                modifier = Modifier.padding(innerPadding)
            )
            else -> CourseContent(
                modifier = Modifier.padding(innerPadding),
                course = requireNotNull(uiState.course),
                districtLabel = uiState.district?.label,
                selectedStopId = uiState.selectedStopId,
                strings = strings,
                onSelectStop = viewModel::selectStop
            )
        }
    }
}

@Composable
private fun CourseContent(
    modifier: Modifier,
    course: RecommendedTourismCourse,
    districtLabel: String?,
    selectedStopId: String?,
    strings: CourseStrings,
    onSelectStop: (String) -> Unit
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
    val paths = remember(course) {
        listOf(
            MapRoutePath(
                id = "recommended-course",
                points = course.stops.map { stop ->
                    MapRoutePoint(requireNotNull(stop.item.latitude), requireNotNull(stop.item.longitude))
                }
            )
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        item {
            Column(
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(strings.courseTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    strings.subtitle(districtLabel),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Surface(shape = CircleShape, color = CoralPrimaryContainer) {
                    Text(
                        text = strings.summary(course),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = CoralPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                KakaoMapView(
                    pins = pins,
                    routePaths = paths,
                    onPinClick = onSelectStop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.94f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Route, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(17.dp))
                        Text(strings.mapHint, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                course.stops.forEachIndexed { index, stop ->
                    if (index > 0) TransferLeg(stop = stop, strings = strings)
                    CourseStopRow(
                        stop = stop,
                        selected = stop.item.id == selectedStopId,
                        onClick = { onSelectStop(stop.item.id) }
                    )
                }
                Text(
                    text = strings.routeDisclaimer,
                    modifier = Modifier.padding(top = 18.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun TransferLeg(stop: RecommendedTourismStop, strings: CourseStrings) {
    Row(
        modifier = Modifier.padding(start = 19.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(2.dp).height(34.dp).background(CoralPrimary.copy(alpha = 0.35f)))
        Spacer(Modifier.width(18.dp))
        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(7.dp))
        Text(
            text = strings.transfer(stop.transferMinutes ?: 0, stop.distanceFromPreviousKm ?: 0.0),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun CourseStopRow(stop: RecommendedTourismStop, selected: Boolean, onClick: () -> Unit) {
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
    val mapHint: String,
    val routeDisclaimer: String,
    val notEnoughPlaces: String,
    val subtitle: (String?) -> String,
    val summary: (RecommendedTourismCourse) -> String,
    val transfer: (Int, Double) -> String
)

private fun SupportedLanguage.courseStrings(): CourseStrings = when (this) {
    SupportedLanguage.KO -> CourseStrings(
        back = "뒤로가기",
        topBarTitle = "추천 동선",
        courseTitle = "나를 위한 부산 회복 코스",
        mapHint = "번호 순서대로 둘러보세요",
        routeDisclaimer = "지도 선과 이동 시간은 장소 좌표를 기준으로 계산한 예상 동선입니다. 실제 도로 상황과 다를 수 있습니다.",
        notEnoughPlaces = "코스를 만들 수 있는 위치 정보가 충분하지 않습니다.",
        subtitle = { district -> "${district ?: "부산"}에서 추천 점수와 이동 부담을 함께 고려했어요." },
        summary = { course -> "${course.stops.size}곳 · 약 ${durationKo(course.estimatedDurationMinutes)} · ${formatKm(course.totalDistanceKm)}" },
        transfer = { minutes, km -> "약 ${minutes}분 · ${formatKm(km)}" }
    )
    SupportedLanguage.EN -> CourseStrings(
        back = "Back",
        topBarTitle = "Recommended route",
        courseTitle = "Your Busan recovery course",
        mapHint = "Follow the numbered stops",
        routeDisclaimer = "The route and travel times are estimates based on place coordinates and may differ from actual roads.",
        notEnoughPlaces = "There are not enough places with location data to build a course.",
        subtitle = { district -> "Balanced for recommendation fit and travel effort around ${district ?: "Busan"}." },
        summary = { course -> "${course.stops.size} stops · about ${durationEn(course.estimatedDurationMinutes)} · ${formatKm(course.totalDistanceKm)}" },
        transfer = { minutes, km -> "About $minutes min · ${formatKm(km)}" }
    )
    SupportedLanguage.JA -> CourseStrings(
        back = "戻る",
        topBarTitle = "おすすめルート",
        courseTitle = "釜山リカバリーコース",
        mapHint = "番号順に巡ってください",
        routeDisclaimer = "ルートと移動時間は座標を基準にした目安で、実際の道路状況とは異なる場合があります。",
        notEnoughPlaces = "コース作成に必要な位置情報が不足しています。",
        subtitle = { district -> "${district ?: "釜山"}でおすすめ度と移動負担を考慮しました。" },
        summary = { course -> "${course.stops.size}か所 · 約${durationJa(course.estimatedDurationMinutes)} · ${formatKm(course.totalDistanceKm)}" },
        transfer = { minutes, km -> "約${minutes}分 · ${formatKm(km)}" }
    )
    SupportedLanguage.ZH -> CourseStrings(
        back = "返回",
        topBarTitle = "推荐路线",
        courseTitle = "釜山疗愈路线",
        mapHint = "请按编号顺序游览",
        routeDisclaimer = "路线和移动时间根据地点坐标估算，可能与实际道路情况不同。",
        notEnoughPlaces = "没有足够的地点位置信息来生成路线。",
        subtitle = { district -> "综合考虑了${district ?: "釜山"}的推荐度和移动距离。" },
        summary = { course -> "${course.stops.size}处 · 约${durationZh(course.estimatedDurationMinutes)} · ${formatKm(course.totalDistanceKm)}" },
        transfer = { minutes, km -> "约${minutes}分钟 · ${formatKm(km)}" }
    )
}

private fun durationKo(minutes: Int): String = if (minutes >= 60) "${minutes / 60}시간 ${minutes % 60}분" else "${minutes}분"
private fun durationEn(minutes: Int): String = if (minutes >= 60) "${minutes / 60}h ${minutes % 60}m" else "${minutes} min"
private fun durationJa(minutes: Int): String = if (minutes >= 60) "${minutes / 60}時間${minutes % 60}分" else "${minutes}分"
private fun durationZh(minutes: Int): String = if (minutes >= 60) "${minutes / 60}小时${minutes % 60}分钟" else "${minutes}分钟"
private fun formatKm(distanceKm: Double): String = String.format(Locale.US, "%.1fkm", distanceKm)
