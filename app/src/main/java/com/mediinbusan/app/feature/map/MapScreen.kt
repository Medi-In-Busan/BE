package com.mediinbusan.app.feature.map

import com.mediinbusan.app.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animate
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.haversineDistanceMeters
import com.mediinbusan.app.core.common.resolveHospitalThumbnailRes
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.InactiveIcon
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.MapStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BusanDefaultCenter
import com.mediinbusan.app.core.ui.DetailPullCommitDurationMs
import com.mediinbusan.app.core.ui.DetailPullMaxFade
import com.mediinbusan.app.core.ui.DetailPullResistance
import com.mediinbusan.app.core.ui.DetailPullSettleSpec
import com.mediinbusan.app.core.ui.DetailPullThreshold
import com.mediinbusan.app.core.ui.DetailPullTravel
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.FavoriteHeartButton
import com.mediinbusan.app.core.ui.FilterChipPill
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LanguageBadge
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.PlaceKindVisual
import com.mediinbusan.app.core.ui.placeKindVisual
import com.mediinbusan.app.core.ui.RouteStop
import com.mediinbusan.app.core.ui.RoundIconButton
import com.mediinbusan.app.core.ui.toLanguageBadgeLabel
import kotlinx.coroutines.launch
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceCategory
import com.mediinbusan.app.data.place.PlaceType
import java.util.Locale
import com.mediinbusan.app.domain.course.WellnessCourse

/**
 * S-08. hospitalId+courseId가 둘 다 있으면 웰니스 코스 동선(F-014 지도 연동) 모드,
 * hospitalId만 있으면 상세페이지 '지도에서 보기'로 진입한 "특정 병원 지도" 모드,
 * 둘 다 없으면 하단 탭 '지도'로 진입한 "전체 병원 브라우징" 모드다(Route.MapView 문서 참고).
 */
@Composable
fun MapScreen(
    hospitalId: String?,
    courseId: String? = null,
    onSelectHospital: (String) -> Unit,
    onSelectPlace: (String) -> Unit = {},
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    LaunchedEffect(hospitalId, courseId) {
        viewModel.load(hospitalId, courseId)
    }

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(hospitalId, courseId) })
        hospitalId != null && courseId != null -> {
            val hospital = uiState.focusedHospital
            val course = uiState.activeCourse
            if (hospital != null && course != null) {
                CourseRouteMap(hospital = hospital, course = course, onSelectPlace = onSelectPlace, onBack = onBack)
            }
        }
        hospitalId != null -> {
            val hospital = uiState.focusedHospital
            if (hospital != null) {
                FocusedHospitalMap(hospital = hospital, nearbyPlaces = uiState.nearbyPlaces, onBack = onBack)
            }
        }
        else -> BrowseMap(
            uiState = uiState,
            onCategorySelected = viewModel::onCategorySelected,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onMarkerSelected = viewModel::onMarkerSelected,
            onToggleFavorite = viewModel::onToggleFavorite,
            onTogglePlaceFavorite = viewModel::onTogglePlaceFavorite,
            onSelectHospital = onSelectHospital,
            onSelectPlace = onSelectPlace,
            onSearchThisArea = viewModel::searchThisArea,
            onSpecialtyFilterToggled = viewModel::onSpecialtyFilterToggled,
            onSpecialtyFiltersCleared = viewModel::onSpecialtyFiltersCleared,
            onLanguageFilterToggled = viewModel::onLanguageFilterToggled,
            onListExpandedChange = viewModel::onListExpandedChange
        )
    }
}

@Composable
private fun FocusedHospitalMap(hospital: Hospital, nearbyPlaces: List<Place>, onBack: () -> Unit) {
    // "뒤로가기"/"길찾기"는 다른 화면과 뜻이 겹치는 공용 문구라 core/i18n의 기존 값을 그대로 쓴다
    // (common.backContentDescription, hospitalDetail.directionsButton) — MapStrings에 중복 정의하지 않는다.
    val strings = LocalAppStrings.current
    val pins = remember(hospital, nearbyPlaces) {
        buildList {
            hospital.toMapPin(selectedId = hospital.id)?.let(::add)
            nearbyPlaces.forEach { place -> place.toMapPin(selectedId = null)?.let(::add) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KakaoMapView(pins = pins, modifier = Modifier.fillMaxSize())

        RoundIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = strings.common.backContentDescription,
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            // HospitalDetailScreen의 BottomActionBar와 같은 패턴: 이 화면(하단 탭바 없는 "특정 병원
            // 지도" 모드)은 MediInBusanApp의 루트 Scaffold가 인셋을 하나도 소비하지 않아(contentWindowInsets
            // = WindowInsets(0.dp)) 이 카드가 직접 제스처/내비게이션 바 인셋을 처리해야 한다 —
            // 빠져 있던 걸 추가한다(전에는 제스처 네비게이션 기기에서 카드 하단이 시스템 바에 가려질 수 있었다).
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.medium).background(Color(0xFFE9E9EE))
                ) {
                    HospitalThumbnail(hospital = hospital, modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = hospital.name, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = hospital.districtLabel(), style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                }
                val context = LocalContext.current
                RoundIconButton(
                    icon = Icons.Default.Navigation,
                    contentDescription = strings.hospitalDetail.directionsButton,
                    onClick = {
                        context.launchExternalDirections(
                            latitude = hospital.latitude,
                            longitude = hospital.longitude,
                            label = hospital.name,
                            fallbackAddress = hospital.address
                        )
                    },
                    background = CoralPrimary,
                    tint = Color.White
                )
            }
        }
    }
}

// F-014 웰니스 코스 동선: feature/nearby의 WellnessCourseCard "이 코스 동선 보기"에서 진입한다.
// FocusedHospitalMap과 같은 시각적 뼈대(전체화면 지도 + 좌상단 뒤로가기 + 하단 흰색 라운드 카드)를
// 그대로 따르되, 하단 카드는 병원 정보 대신 코스 이름/소요시간 + 번호 매긴 스탑 칩 목록을 보여준다.
@Composable
private fun CourseRouteMap(hospital: Hospital, course: WellnessCourse, onSelectPlace: (String) -> Unit, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    val mapStrings = strings.map
    // 병원(출발점, 배지 없음) + 코스 장소들(1부터 시작하는 방문 순서 배지) 순서로 pins를 구성한다.
    // routeStops는 같은 순서의 좌표만 뽑아 KakaoMapView(routeStops=...)에 넘기고, 그 지도 위에
    // 화살표 패턴이 반복되는 경로선으로 그려진다 — 외부 카카오맵 앱으로 내보내는 길찾기 연동 없이
    // 우리 지도 안에서 방향을 보여준다(core/ui/KakaoMapView.kt의 renderRoute 참고).
    val pins = remember(hospital, course) {
        buildList {
            hospital.toMapPin(selectedId = null)?.let(::add)
            course.places.forEachIndexed { index, place ->
                place.toMapPin(selectedId = null)?.copy(sequenceNumber = index + 1)?.let(::add)
            }
        }
    }
    val routeStops = remember(hospital, course) {
        buildList {
            val hospitalLat = hospital.latitude
            val hospitalLng = hospital.longitude
            if (hospitalLat != null && hospitalLng != null) add(RouteStop(hospital.name, hospitalLat, hospitalLng))
            course.places.forEach { place ->
                val lat = place.latitude
                val lng = place.longitude
                if (lat != null && lng != null) add(RouteStop(place.name, lat, lng))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KakaoMapView(pins = pins, routeStops = routeStops, modifier = Modifier.fillMaxSize())

        RoundIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = strings.common.backContentDescription,
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(modifier = Modifier.navigationBarsPadding().padding(16.dp)) {
                Text(text = course.name, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${mapStrings.courseDurationPrefix}${course.estimatedDurationMinutes / 60}h",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (routeStops.size >= 2) {
                    Spacer(modifier = Modifier.height(10.dp))
                    RouteArrowHint(label = mapStrings.routeArrowHintLabel)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(course.places, key = { _, place -> place.id }) { index, place ->
                        CourseStopChip(index = index + 1, place = place, onClick = { onSelectPlace(place.id) })
                    }
                }
            }
        }
    }
}

// 지도 위 화살표 경로선(core/ui/KakaoMapView.kt의 renderRoute)이 뭘 뜻하는지 짧게 짚어주는 안내 pill.
// SpecialtyFilterRow/FilterPillButton과 같은 톤(코랄 컨테이너 배경 + 진한 텍스트)을 재사용해 이
// 화면만 튀지 않게 한다.
@Composable
private fun RouteArrowHint(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CoralPrimaryContainer)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = CoralPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
    }
}

// feature/nearby의 CourseStopRow와 시각적으로 비슷하지만 이 파일 로컬 private composable이다 —
// CLAUDE.md의 "feature 패키지는 서로를 직접 import하지 않는다" 규칙에 따라 작은 중복을 허용한다.
@Composable
private fun CourseStopChip(index: Int, place: Place, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(Color(0xFFF5F5F7))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(18.dp).clip(CircleShape).background(CoralPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = index.toString(), style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = place.name, style = MaterialTheme.typography.labelMedium, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// 하단 카드영역(리스트업 바텀시트) 관련 고정 치수.
// 접힌(미리보기) 시트에 남는 요약 헤더 한 줄의 높이 — 제목 + 결과 개수. 펼쳐도 이 줄은 같은
// 자리에 그대로 있고 아래로 목록만 자라난다(아래 BrowseMap의 시트 구성 주석 참고).
private val SheetHeaderHeight = 52.dp
// 손잡이 영역의 실제 높이(top padding 6dp + 알약 바 5dp + bottom padding 10dp) — 내 위치 버튼을
// 이 손잡이 바로 위에 정확히 세우는 데 쓴다(실측 없이 고정값으로 계산).
private val PeekHandleAreaHeight = 21.dp

@Composable
private fun BrowseMap(
    uiState: MapUiState,
    onCategorySelected: (MapCategory) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onMarkerSelected: (String?) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onTogglePlaceFavorite: (String) -> Unit,
    onSelectHospital: (String) -> Unit,
    onSelectPlace: (String) -> Unit,
    onSearchThisArea: (latitude: Double, longitude: Double) -> Unit,
    onSpecialtyFilterToggled: (String) -> Unit,
    onSpecialtyFiltersCleared: () -> Unit,
    onLanguageFilterToggled: () -> Unit,
    onListExpandedChange: (Boolean) -> Unit
) {
    val mapStrings = LocalAppStrings.current.map
    val language = LocalAppStrings.current.language
    // "필터" 버튼을 누르면 진료과목 칩 줄을 펼쳤다 접었다 한다 — 예전엔 버튼만 있고 실제 필터
    // 기능이 없는 자리표시자였다.
    var showSpecialtyFilters by remember { mutableStateOf(false) }
    // 진료과목 필터가 실제로 뭔가를 거르는 탭인지 — 병원이 목록에 나오는 탭(병원/전체)뿐이다.
    val specialtyFilterApplies = uiState.selectedCategory == MapCategory.HOSPITAL ||
        uiState.selectedCategory == MapCategory.ALL
    // 카드영역: 기본은 "미리보기"(손잡이 + 리스트 첫 항목만 66% 노출) 상태로 접혀있고, 손잡이를
    // 위로 드래그(또는 탭)하면 검색바까지 덮는 전체 리스트 페이지로 펼쳐진다. 마커를 새로 선택하면
    // 그 항목을 미리보기로 보여주면 되므로, 펼쳐져 있었어도 미리보기로 되돌아간다.
    // 펼침 여부는 ViewModel이 들고 있다(MapUiState.isListExpanded) — 목록에서 항목을 골라 상세로
    // 갔다 뒤로 돌아와도 그 상태 그대로 복귀해야 하기 때문이다. 화면 로컬 상태로 두면 재진입 때
    // BrowseMap이 잠깐 사라지면서 같이 날아갔다.
    val isListExpanded = uiState.isListExpanded
    LaunchedEffect(uiState.selectedMarkerId) {
        if (uiState.selectedMarkerId != null) onListExpandedChange(false)
    }
    // visibleHospitals/visiblePlaces/categoryHospitals는 MapUiState의 계산 프로퍼티라 읽을 때마다
    // 전체 목록을 다시 거른다 — 한 번의 recomposition에서 핀/목록/선택 조회로 네댓 번씩 반복됐고,
    // 시트를 드래그하는 동안엔 매 프레임 그 비용을 다시 냈다. uiState가 바뀔 때만 한 번 계산해서
    // 아래 전부가 같은 결과를 나눠 쓰게 한다.
    val categoryHospitals = remember(uiState) { uiState.categoryHospitals }
    val visibleHospitals = remember(uiState) { uiState.visibleHospitals }
    val visiblePlaces = remember(uiState) { uiState.visiblePlaces }
    val hospitalPins = remember(visibleHospitals, uiState.selectedMarkerId) {
        visibleHospitals.mapNotNull { it.toMapPin(uiState.selectedMarkerId) }
    }
    val placePins = remember(visiblePlaces, uiState.selectedMarkerId) {
        visiblePlaces.mapNotNull { it.toMapPin(uiState.selectedMarkerId) }
    }
    // 지도 카메라가 멈출 때마다 갱신되는 화면 중심 — 하단 목록을 "지금 보고 있는 지점"에서
    // 가까운 순으로 정렬하는 기준이다(초기값은 지도가 처음 열리는 자리와 같다).
    var mapCenter by remember { mutableStateOf(MapPoint(BusanDefaultCenter.latitude, BusanDefaultCenter.longitude)) }
    // 마커를 그릴지 여부는 MapUiState가 들고 있다(MapUiState.markersActivated 주석 참고) — 화면
    // 로컬 상태로 두면 상세화면에 갔다 돌아올 때마다 초기화돼서 골라둔 카테고리가 풀렸다.
    val markersActivated = uiState.markersActivated
    val pins = if (markersActivated) {
        when (uiState.selectedCategory) {
            MapCategory.ALL -> hospitalPins + placePins
            MapCategory.HOSPITAL -> hospitalPins
            else -> placePins
        }
    } else {
        emptyList()
    }
    // 카테고리 탭은 토글이다(켜진 탭을 다시 누르면 마커가 사라진다) — 그 판단은 ViewModel의
    // onCategorySelected가 하고, 화면은 꺼질 때 펼쳐둔 리스트만 같이 접는다.
    val onCategoryTapped: (MapCategory) -> Unit = { category ->
        if (markersActivated && uiState.selectedCategory == category) onListExpandedChange(false)
        onCategorySelected(category)
    }
    // 0은 "아직 요청 없음"을 의미하는 초기값이라 KakaoMapView가 무시한다 — 버튼 클릭마다 증가시켜 트리거한다.
    var recenterRequestId by remember { mutableIntStateOf(0) }
    var searchAreaRequestId by remember { mutableIntStateOf(0) }
    var zoomInRequestId by remember { mutableIntStateOf(0) }
    var zoomOutRequestId by remember { mutableIntStateOf(0) }

    // BottomNavBar 자신은 navigationBarsPadding()으로 제스처/내비게이션 바 인셋을 소비해 화면 진짜
    // 하단에서 인셋만큼 띄워 그려진다(core/ui/BottomNavBar.kt 참고) — 즉 바 뒤쪽은 원래 뚫려 있어야
    // 그 유리 블러 효과(hazeEffect)가 지도를 비춰 보여준다. 예전엔 지도를 담은 Box 전체에 하단
    // 패딩을 줘서 지도 자체가 바 아래에서 잘려 있었고, 그 결과 바 뒤로 지도 대신 빈 배경만 비쳐
    // 보였다 — 지도(KakaoMapView)는 화면 전체를 꽉 채우게 두고, 바와 안 겹쳐야 하는 "떠 있는"
    // 컨트롤(버튼/카드)에만 이 여백을 개별로 준다.
    //
    // 마커/카드를 선택하면 MapViewModel이 BottomBarVisibilityController로 신호를 보내 하단 탭바
    // 자체가 사라지므로(MediInBusanApp.kt 참고), 이 화면도 그 순간엔 탭바 몫으로 비워뒀던 자리를
    // 접어서 선택 카드가 그 자리까지 올라오게 한다 — 흰 카드가 탭바 위에 떠서 그 사이로 지도가
    // 비치는 "안 어울리는" 레이어링 대신, 탭바가 있던 자리를 카드가 그대로 이어받는 모양이 된다.
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isSelectionActive = uiState.selectedMarkerId != null
    val bottomSafePadding by animateDpAsState(
        targetValue = navigationBarInset + if (isSelectionActive) 0.dp else BottomNavBarHeight,
        label = "mapBottomSafePadding"
    )
    val density = LocalDensity.current
    // 드래그 제스처 판정용 임계값(약 40dp). 손잡이를 이만큼 위/아래로 끌면 펼침/접힘이 토글된다.
    val dragThresholdPx = with(density) { 40.dp.toPx() }
    // 펼쳤을 때 화면 전체가 아니라 "검색바 영역 바로 아래"까지만 리스트가 올라오게 하려면
    // 검색바 블록(상단 Column)의 실제 높이를 알아야 한다 — 필터 줄 펼침 여부 등에 따라 그 높이가
    // 달라지므로 실측한다. BoxWithConstraints로 화면 전체 높이(maxHeight)도 같이 얻는다.
    var topBarHeight by remember { mutableStateOf(0.dp) }
    // 마커 선택 카드(SelectedHospitalCard/SelectedPlaceCard)는 내용에 따라 높이가 크게 달라져서
    // (사진+뱃지+버튼 유무 등) 고정값을 못 쓴다 — 실측해서 내 위치 버튼이 그 높이를 따라오게 한다.
    // 선택이 없을 때(리스트 미리보기)는 행 높이가 고정값이라 이 실측이 필요 없다.
    var selectedCardHeight by remember { mutableStateOf(0.dp) }
    // 마커 선택 중이면 실측한 선택 카드 높이를, 아니면(리스트 미리보기) 고정값을 따라간다 —
    // 두 경우를 하나의 애니메이션 값으로 합쳐서 내 위치 버튼이 항상 부드럽게 따라오게 한다.
    // 미리보기 시트는 이제 탭바에 가리지 않고 그 위에 통째로 떠 있으므로(아래 시트 padding 참고),
    // 버튼도 탭바 높이(BottomNavBarHeight)만큼 같이 올라와야 시트를 안 덮는다.
    val recenterBottomPadding by animateDpAsState(
        targetValue = when {
            isSelectionActive -> navigationBarInset + selectedCardHeight + 4.dp
            // 카테고리를 껐을 땐 시트가 통째로 사라지므로 버튼도 탭바 바로 위까지 내려온다.
            !markersActivated -> navigationBarInset + BottomNavBarHeight + 4.dp
            else -> navigationBarInset + BottomNavBarHeight + SheetHeaderHeight + PeekHandleAreaHeight + 4.dp
        },
        label = "mapRecenterBottomPadding"
    )
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = maxHeight
        KakaoMapView(
            pins = pins,
            modifier = Modifier.fillMaxSize(),
            onPinClick = onMarkerSelected,
            onMapClick = { onMarkerSelected(null) },
            recenterRequestId = recenterRequestId,
            searchAreaRequestId = searchAreaRequestId,
            onSearchArea = onSearchThisArea,
            // 병원 전체(366개)가 부산 전역에 흩어져 있어 fitMapPoints를 쓰면 서면 클러스터가
            // 화면에서 작아진다. 서면 기본 중심으로 고정하고, "이 위치에서 검색" 이후에도
            // 사용자가 옮긴 카메라 위치를 결과 목록 때문에 다시 튕겨내지 않는다.
            fitCameraToPins = false,
            zoomInRequestId = zoomInRequestId,
            zoomOutRequestId = zoomOutRequestId,
            // 축소하면 핀 수백 개가 서로 겹쳐 몇 개인지도 안 보였다 — 가까운 것끼리 개수 배지로 묶는다.
            clusterPins = true,
            onCameraMove = { latitude, longitude -> mapCenter = MapPoint(latitude, longitude) }
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 펼친 리스트 시트가 이 검색바 줄 바로 아래까지만 올라오게(카테고리 탭 등 그 밑의
            // 나머지는 시트가 덮어서 가림) 이 Row 하나만 실측한다 — 전체 Column을 재면 카테고리
            // 탭/필터칩/이 위치에서 검색 버튼까지 다 포함돼 시트가 너무 조금만 올라왔었다.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    // 이 Column의 위쪽 padding(16dp)만큼 검색바 줄이 화면 맨 위에서 내려와 있다.
                    topBarHeight = 16.dp + with(density) { coordinates.size.height.toDp() }
                }
            ) {
                MapSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    placeholder = mapStrings.searchPlaceholder,
                    modifier = Modifier.weight(1f)
                )
                // 진료과목 필터는 병원에만 걸린다(MapUiState.visibleHospitals) — 관광/음식 탭에서는
                // 눌러도 아무 일이 없는데 배지 숫자만 남아 "걸려 있는데 왜 안 걸러지지?"로 읽혔다.
                // 장소 탭에서는 버튼 자체를 숨긴다.
                if (specialtyFilterApplies) {
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterPillButton(
                        contentDescription = mapStrings.filterLabel,
                        active = showSpecialtyFilters || uiState.selectedSpecialties.isNotEmpty(),
                        badgeCount = uiState.selectedSpecialties.size,
                        onClick = { showSpecialtyFilters = !showSpecialtyFilters }
                    )
                }
            }
            AnimatedVisibility(visible = showSpecialtyFilters && specialtyFilterApplies) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    SpecialtyFilterRow(
                        language = language,
                        selectedSpecialties = uiState.selectedSpecialties,
                        resetLabel = LocalAppStrings.current.search.resetFiltersButton,
                        onSpecialtyToggled = onSpecialtyFilterToggled,
                        onCleared = onSpecialtyFiltersCleared
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            CategoryTabsRow(
                strings = mapStrings,
                // 마커가 아직 활성화되지 않았으면(첫 로드 직후) 어떤 탭도 활성 표시 없이 둔다 —
                // 실제 uiState.selectedCategory 기본값은 그대로 HOSPITAL이지만(필터링 로직 등 다른
                // 곳에서 계속 씀), 여기 시각적 하이라이트만 markersActivated로 가린다.
                selected = if (markersActivated) uiState.selectedCategory else null,
                onSelected = onCategoryTapped
            )
            // 한국어 UI에서는 모든 장소가 원문(한국어)이라 이 필터가 아무것도 안 걸러 의미가 없다 —
            // 다른 언어일 때만, 그리고 장소(Place)가 나오는 카테고리에서만 보여준다(병원 이름·주소는
            // 번역 대상이 아니라 항상 그대로 나오므로 HOSPITAL 탭에는 안 띄운다).
            if (language != SupportedLanguage.KO && uiState.selectedCategory != MapCategory.HOSPITAL) {
                Spacer(modifier = Modifier.height(10.dp))
                FilterChipPill(
                    label = mapStrings.languageFilterLabel,
                    selected = uiState.languageFilterEnabled,
                    onClick = onLanguageFilterToggled
                )
            }
            // 예전엔 병원/전체 탭에서만 떠서 관광·음식으로 바꾸면 버튼이 사라졌다 — 어느 탭에서든
            // "이 주변만 보기"라는 같은 뜻으로 동작하게 통일했다(병원은 서버 재조회, 장소는 이미
            // 받아둔 목록을 그 좌표 반경으로 필터 — MapUiState.areaCenter 참고).
            run {
                Spacer(modifier = Modifier.height(8.dp))
                SearchThisAreaButton(
                    isLoading = uiState.isSearchingArea,
                    label = mapStrings.searchThisAreaLabel,
                    loadingLabel = mapStrings.searchingThisAreaLabel,
                    // 마커 활성화는 이 요청의 결과를 받는 MapViewModel.searchThisArea가 켜준다.
                    onClick = { searchAreaRequestId++ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // "전체" 탭에서는 마커가 병원/장소 둘 중 무엇이든 선택될 수 있어, 카테고리로 분기하는
        // 대신 uiState.categoryHospitals(현재 탭에서 병원을 보여줘야 하는지 이미 판단됨)/
        // visiblePlaces 두 목록에서 직접 선택된 항목을 찾는다.
        val selectedHospital = categoryHospitals.firstOrNull { it.id == uiState.selectedMarkerId }
        val selectedPlace = if (selectedHospital == null) {
            visiblePlaces.firstOrNull { it.id == uiState.selectedMarkerId }
        } else {
            null
        }
        // 목록은 백엔드가 준 순서가 아니라 "지금 지도에서 보고 있는 지점"에서 가까운 순이다 —
        // 화면 중앙에 있지도 않은 병원이 미리보기 카드에 뜨던 문제(지도와 목록이 따로 놀던 것)를
        // 없앤다. 좌표가 없는 항목은 정렬 기준이 없어 맨 뒤로 보낸다.
        val entries = remember(categoryHospitals, visiblePlaces, mapCenter) {
            (categoryHospitals.map { it.toCardEntry(mapCenter) } + visiblePlaces.map { it.toCardEntry(mapCenter) })
                .sortedBy { it.distanceMeters ?: Double.MAX_VALUE }
        }
        // 리스트업(펼침) 페이지에서 행을 눌렀을 때 병원/장소 중 어느 상세화면으로 보낼지 판단하는 데 쓴다.
        val hospitalIdSet = remember(categoryHospitals) { categoryHospitals.map { it.id }.toSet() }
        // 실제 GPS 위치가 아닌 고정된 부산 기본 좌표로만 이동한다 — 위치 권한을 쓰지 않는다.
        // (예전엔 이 옆에 "레이어" 버튼이 더 있었지만 onClick이 비어 있는 미구현 자리표시자라
        // 제거했다 — 실제 동작하는 버튼만 남긴다.) 리스트가 화면을 다 덮는 펼침 상태에선 어차피
        // 안 보이는 자리라 그냥 숨긴다.
        AnimatedVisibility(
            visible = !isListExpanded,
            modifier = Modifier.align(Alignment.BottomEnd),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = recenterBottomPadding, end = 16.dp)
            ) {
                // 기본 배율이 건물이 보일 만큼 확대돼 있어(KakaoMapView의 DEFAULT_ZOOM_LEVEL),
                // 부산 전역을 보려면 축소 수단이 필요하다 — 핀치 제스처 말고 버튼으로도 준다.
                RoundIconButton(
                    icon = Icons.Default.Add,
                    contentDescription = mapStrings.zoomInContentDescription,
                    onClick = { zoomInRequestId++ },
                    background = Color.White,
                    tint = TextPrimary,
                    shape = MaterialTheme.shapes.medium
                )
                RoundIconButton(
                    icon = Icons.Default.Remove,
                    contentDescription = mapStrings.zoomOutContentDescription,
                    onClick = { zoomOutRequestId++ },
                    background = Color.White,
                    tint = TextPrimary,
                    shape = MaterialTheme.shapes.medium
                )
                // 아이콘이 MyLocation(GPS 조준선)이면 "내 위치로 간다"는 뜻으로 읽히는데, 이 앱은
                // 위치 권한을 아예 쓰지 않고 고정된 기본 좌표로만 이동한다(CLAUDE.md §1) —
                // 중립적인 "중심 맞추기" 아이콘으로 바꿔 오해를 없앤다.
                RoundIconButton(
                    icon = Icons.Default.CenterFocusStrong,
                    contentDescription = mapStrings.recenterContentDescription,
                    onClick = {
                        onMarkerSelected(null)
                        recenterRequestId++
                    },
                    background = CoralPrimary,
                    tint = Color.White,
                    shape = MaterialTheme.shapes.medium
                )
            }
        }

        // 마커를 골라 선택 카드가 떠 있는 동안에는 같은 "위로 끌기"가 리스트 펼치기가 아니라
        // 그 항목의 상세화면으로 넘어가는 동작이 된다 — 선택 상태에서는 어차피 리스트를 펼쳐도
        // 카드가 계속 그 자리를 차지해 펼침이 의미가 없었다(panelKey가 선택 카드를 우선한다).
        // 카드가 손가락을 따라 올라가다 임계값을 넘기면 남은 거리를 마저 올린 뒤 상세로 넘어가고,
        // 상세화면은 같은 방향으로 밀려 올라와 한 동작처럼 이어진다(core/ui/DetailPullTransition.kt).
        //
        // 아래 pointerInput은 "선택 중인가"(isSelectionActive)로만 다시 걸리므로, 카드가 떠 있는
        // 채로 다른 마커를 눌러 선택만 바뀌는 경우엔 제스처 블록이 그대로 남는다 — 이 람다를 그냥
        // 캡처하면 먼저 골랐던 항목의 상세로 가버린다. 늘 최신 선택을 가리키도록 감싸 둔다
        // (KakaoMapView가 콜백을 다루는 방식과 같다).
        val openSelectedDetail by rememberUpdatedState<() -> Unit>({
            when {
                selectedHospital != null -> onSelectHospital(selectedHospital.id)
                selectedPlace != null -> onSelectPlace(selectedPlace.id)
                else -> Unit
            }
        })
        val pullThresholdPx = with(density) { DetailPullThreshold.toPx() }
        val pullTravelPx = with(density) { DetailPullTravel.toPx() }
        // 지금 카드가 손가락을 따라 올라와 있는 거리(px, 양수가 위쪽).
        var cardLiftPx by remember { mutableFloatStateOf(0f) }
        // 전환이 확정된 뒤 들어오는 드래그는 무시한다(같은 상세화면으로 두 번 navigate 방지).
        var isOpeningDetail by remember { mutableStateOf(false) }
        val liftScope = rememberCoroutineScope()
        LaunchedEffect(uiState.selectedMarkerId) {
            cardLiftPx = 0f
            isOpeningDetail = false
        }
        val settleCardLift: () -> Unit = {
            liftScope.launch {
                animate(cardLiftPx, 0f, animationSpec = DetailPullSettleSpec) { value, _ -> cardLiftPx = value }
            }
        }

        // 시트 배경색 — 목록(접힘 요약 줄/펼침 목록 페이지)은 같은 흰 면이고, 마커 선택 카드가
        // 뜰 때만 연분홍 트레이로 바뀐다(그 위에 흰 사진 카드가 떠 보여야 하므로).
        val sheetBackgroundColor by animateColorAsState(
            targetValue = if (isSelectionActive) HomeBackgroundPink else Color.White,
            animationSpec = tween(220),
            label = "mapSheetBackground"
        )

        // 시트 높이는 접힘/펼침 두 값 사이를 하나의 애니메이션으로 오간다. 예전엔 콘텐츠 크기에
        // 맡겼는데(animateContentSize + 상태별로 다른 자식), 접을 때는 안쪽 목록이 먼저 사라지고
        // 컨테이너 높이만 뒤늦게 줄어들어 뚝 끊겨 보였다 — 펼칠 때만 자연스러웠던 이유가 이것이다.
        // 목표 높이를 직접 계산해 같은 스펙으로 양방향을 굴리면 올릴 때와 내릴 때가 대칭이 된다.
        val collapsedSheetHeight = PeekHandleAreaHeight + SheetHeaderHeight + bottomSafePadding
        val expandedSheetHeight = (screenHeight - topBarHeight).coerceAtLeast(collapsedSheetHeight)
        val sheetHeight by animateDpAsState(
            targetValue = if (isListExpanded) expandedSheetHeight else collapsedSheetHeight,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            label = "mapSheetHeight"
        )
        // 접히는 동안에도 목록은 펼쳤을 때의 높이를 그대로 유지한 채 시트 밖으로 밀려나며 잘린다 —
        // 매 프레임 LazyColumn을 다시 재는 대신 그리기만 잘라내므로 미끄러지듯 내려간다.
        // (Modifier.height는 부모 제약에 맞춰 줄어들지만 requiredHeight는 무시하고 고정한다 — 이게 핵심.)
        val expandedListHeight = (expandedSheetHeight - bottomSafePadding - PeekHandleAreaHeight - SheetHeaderHeight)
            .coerceAtLeast(0.dp)
        // 다 접히고 나서야 목록을 구성 해제한다 — 접히는 도중엔 계속 그려져 있어야 하기 때문이다.
        val isListContentVisible = isListExpanded || sheetHeight > collapsedSheetHeight + 1.dp
        // 그림자도 같은 길이로 따라간다. 접힘 상태에서만 시트가 지도 위에 떠 있으므로 그림자가
        // 필요한데, 예전엔 이 값이 토글 순간 한 프레임에 0 ↔ 10dp로 튀어 접히기 시작할 때 테두리가
        // 툭 나타났다.
        val sheetElevation by animateDpAsState(
            targetValue = if (isListExpanded) 0.dp else 10.dp,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            label = "mapSheetElevation"
        )

        // 손잡이 하나에만 걸려 있던 드래그를 시트 전체가 공유한다 — 미리보기 행(리스트) 위를
        // 위로 쓸어올려도 펼쳐지고, 펼친 상태에서 제목/탭 줄을 아래로 끌면 다시 접힌다.
        val dragAccumPx = remember { mutableFloatStateOf(0f) }
        val sheetDragModifier = Modifier.pointerInput(isListExpanded, isSelectionActive) {
            detectVerticalDragGestures(
                onDragEnd = {
                    dragAccumPx.floatValue = 0f
                    if (!isSelectionActive || isOpeningDetail) return@detectVerticalDragGestures
                    if (cardLiftPx >= pullThresholdPx) {
                        isOpeningDetail = true
                        liftScope.launch {
                            // 남은 거리를 마저 올려 카드를 화면 밖으로 보낸 뒤 넘어간다 —
                            // 올라가던 움직임을 상세화면 등장 애니메이션이 그대로 이어받는다.
                            animate(
                                cardLiftPx,
                                pullTravelPx,
                                animationSpec = tween(DetailPullCommitDurationMs)
                            ) { value, _ -> cardLiftPx = value }
                            openSelectedDetail()
                        }
                    } else {
                        settleCardLift()
                    }
                },
                onDragCancel = {
                    dragAccumPx.floatValue = 0f
                    if (isSelectionActive && !isOpeningDetail) settleCardLift()
                },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    if (isSelectionActive) {
                        // 아래로 끌어 내리는 건 0에서 막는다 — 카드를 닫는 건 X 버튼과 지도 탭이 맡는다.
                        if (!isOpeningDetail) {
                            cardLiftPx = (cardLiftPx - dragAmount * DetailPullResistance)
                                .coerceIn(0f, pullTravelPx)
                        }
                        return@detectVerticalDragGestures
                    }
                    dragAccumPx.floatValue += dragAmount
                    if (!isListExpanded && dragAccumPx.floatValue < -dragThresholdPx) {
                        onListExpandedChange(true)
                        dragAccumPx.floatValue = 0f
                    } else if (isListExpanded && dragAccumPx.floatValue > dragThresholdPx) {
                        onListExpandedChange(false)
                        dragAccumPx.floatValue = 0f
                    }
                }
            )
        }
        // 펼친 리스트(LazyColumn) 위에서는 스크롤 제스처를 리스트가 먼저 가져가므로 위 드래그
        // 제스처가 안 걸린다 — 리스트가 맨 위에 닿은 뒤에도 계속 아래로 끌면(onPostScroll에
        // 남은 available.y) 그 양을 모아서 시트를 접는다. 반대로 위로 스크롤하면 누적을 지운다.
        val sheetNestedScroll = remember(isListExpanded) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (available.y < 0f) dragAccumPx.floatValue = 0f
                    return Offset.Zero
                }

                override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                    if (isListExpanded && available.y > 0f) {
                        dragAccumPx.floatValue += available.y
                        if (dragAccumPx.floatValue > dragThresholdPx) {
                            onListExpandedChange(false)
                            dragAccumPx.floatValue = 0f
                        }
                    }
                    return Offset.Zero
                }
            }
        }

        // 카드영역: 접힘(제목+개수 요약 줄) / 펼침(검색바까지 덮는 전체 리스트) 두 상태를 시트를
        // 위아래로 끌거나(어디든) 손잡이·요약 줄을 탭해서 전환한다. HospitalSearchListScreen과 같은
        // 느낌의 가로형 리스트 행을 쓰되, feature 패키지 간 직접 import는 하지 않는 규칙(CLAUDE.md)
        // 이라 이 파일 안에 로컬로 새로 둔다(MapPlaceListRow).
        //
        // 카테고리를 끈 상태(markersActivated=false — 첫 진입이거나 켜져 있던 탭을 한 번 더 누른
        // 경우)에는 마커도 없고 보여줄 목록도 의미가 없어, 시트를 아래로 밀어 화면 밖으로 내린다.
        AnimatedVisibility(
            visible = markersActivated || isSelectionActive,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(animationSpec = tween(240)) { height -> height } + fadeIn(tween(240)),
            exit = slideOutVertically(animationSpec = tween(200)) { height -> height } + fadeOut(tween(200))
        ) {
            Column(
                modifier = Modifier
                    // 선택 카드를 위로 끌어올리는 동안 시트 전체가 손가락을 따라 올라가며 옅어진다.
                    // 레이아웃(높이·여백)은 건드리지 않고 그리기만 옮기므로, 도중에 손을 떼고
                    // 되돌아와도 아래 내 위치 버튼 등이 따라 흔들리지 않는다.
                    .graphicsLayer {
                        translationY = -cardLiftPx
                        alpha = 1f - (cardLiftPx / pullTravelPx).coerceIn(0f, 1f) * DetailPullMaxFade
                    }
                    .fillMaxWidth()
                    // 펼쳤을 때도 화면 전체가 아니라 "검색바 블록 바로 아래"까지만 — 그 위 공간은
                    // 계속 지도가 보인다. 검색바 블록 높이가 필터 펼침 등으로 달라질 수 있어 실측값을 쓴다.
                    // 목록 상태(접힘 요약 줄 ↔ 펼침)에서는 위에서 계산한 sheetHeight가 그 사이를
                    // 대칭으로 오가고, 마커 선택 카드는 내용 높이가 제각각이라 그대로 감싸게 둔다.
                    .then(if (isSelectionActive) Modifier else Modifier.height(sheetHeight))
                    .shadow(
                        elevation = sheetElevation,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.2f),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    // 목록 시트는 접혀 있든 펼쳐져 있든 같은 흰 면이다 — 행마다 카드를 띄우는 대신
                    // 구분선으로만 나누는 플랫 구조라(MapPlaceListRow) 바닥이 흰색이어야 행 사이
                    // 경계가 그림자 없이도 분명하고, 접었을 때 남는 요약 줄도 그 목록의 머리글로
                    // 읽힌다. 마커 선택 카드일 때만 Home/HospitalSearchList와 같은 앱 기본 배경색
                    // (연분홍) 트레이로 바뀐다 — 그 위에 흰 사진 카드가 그림자와 함께 떠 보인다.
                    .background(sheetBackgroundColor)
                    .nestedScroll(sheetNestedScroll)
                    .then(sheetDragModifier)
                    // 세 상태 모두 하단 탭바를 피한다 — 예전엔 미리보기일 때만 탭바 높이를 안 비워서
                    // 리스트 행 아랫부분이 탭바 뒤에 잘려 보였다. bottomSafePadding은 탭바가 보이는
                    // 동안엔 탭바 높이를, 마커 선택으로 탭바가 사라진 동안엔 제스처 인셋만 반영한다.
                    .padding(bottom = bottomSafePadding)
                    // 선택 카드일 때만 내용 크기를 따라간다(목록 쪽은 sheetHeight가 직접 몬다).
                    .then(if (isSelectionActive) Modifier.animateContentSize(animationSpec = tween(220)) else Modifier)
            ) {
                // 마커를 직접 선택했을 때는 리스트업으로 끌어올리는 손잡이 자체가 의미 없다(예전
                // 사진 카드만 단독으로 뜨던 모양으로 돌아간다) — 미리보기/펼침 상태에서만 보여준다.
                if (!isSelectionActive) {
                    // 알약 모양(양옆이 완전히 둥근) 드래그 손잡이. 영역 맨 위에 바짝 붙이고(위 여백 없음),
                    // 탭해서 펼침·접힘을 토글한다(끄는 동작은 시트 전체가 받는다).
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 10.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onListExpandedChange(!isListExpanded) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 40.dp, height = 5.dp)
                                .clip(RoundedCornerShape(percent = 50))
                                .background(DividerColor)
                        )
                    }
                }

                // 목록(browse) / 마커 선택 카드(hospital·place)가 서로 바뀔 때만 슬라이드 + 페이드로
                // 전환한다. 펼침·접힘은 더 이상 여기서 콘텐츠를 갈아끼우지 않는다 — 같은 "browse"
                // 콘텐츠가 그대로 있고 시트 높이(sheetHeight)만 자라거나 줄어든다. 예전엔 이 전환과
                // 시트 높이 애니메이션이 서로 다른 스펙으로 동시에 돌아 접을 때 끊겨 보였다.
                val panelKey = when {
                    selectedHospital != null -> "hospital:${selectedHospital.id}"
                    selectedPlace != null -> "place:${selectedPlace.id}"
                    else -> "browse"
                }
                AnimatedContent(
                    targetState = panelKey,
                    // 목록 상태에서는 시트에 남은 높이를 정확히 차지하고, 그보다 큰 내용(접히는 중인
                    // 목록)은 잘라낸다. 선택 카드는 내용 크기 그대로 둔다(weight를 주면 0이 된다).
                    modifier = Modifier.fillMaxWidth().then(
                        if (isSelectionActive) Modifier else Modifier.weight(1f).clipToBounds()
                    ),
                    transitionSpec = {
                        (slideInVertically(animationSpec = tween(220)) { height -> height / 4 } + fadeIn(tween(220)))
                            .togetherWith(slideOutVertically(animationSpec = tween(180)) { height -> height / 4 } + fadeOut(tween(180)))
                    },
                    label = "mapBottomPanel"
                ) { state ->
                    when {
                        state == "browse" -> Column(modifier = Modifier.fillMaxWidth()) {
                            // 접힘/펼침 공통 헤더 — 접었을 때 시트에 남는 건 손잡이와 이 한 줄뿐이다.
                            // 예전에는 여기에 목록 첫 항목을 흰 카드 한 줄로 띄웠는데,
                            // 펼친 목록이 그림자 없는 플랫한 흰 면으로 바뀐 뒤로는 그 카드만 혼자
                            // 옛 톤으로 떠 있어 어긋나 보였다. 요약 줄로 바꾸면 펼쳤을 때 이 줄이
                            // 제자리에 그대로 남고 아래로 목록만 자라나, 두 상태가 한 화면으로 이어진다.
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .requiredHeight(SheetHeaderHeight)
                                    // 손잡이뿐 아니라 이 줄 어디를 눌러도 펼침/접힘이 토글된다.
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onListExpandedChange(!isListExpanded) }
                                    )
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = mapStrings.listPageTitle,
                                        // 펼친 상태는 지도 위에 살짝 뜬 트레이가 아니라 하나의
                                        // "목록 페이지"다 — 제목을 한 단계 키워 그 위계를 준다.
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // 몇 곳이 잡혔는지 — "이 위치에서 검색"으로 범위를 좁혔을 때
                                    // 결과가 실제로 줄었는지 확인하는 신호도 된다. 제목과 같은 줄
                                    // 아래쪽(Alignment.Bottom)에 붙어 부제처럼 읽힌다.
                                    Text(
                                        text = "${entries.size}${mapStrings.resultCountSuffix}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CoralPrimary,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }
                            // 펼쳤을 때만 붙는 아래쪽(탭 + 목록). 높이를 requiredHeight로 못 박아
                            // 접히는 동안에도 다시 재지 않고, 줄어드는 시트 밖으로 밀려나며 잘린다.
                            if (isListContentVisible) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .requiredHeight(expandedListHeight)
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        CategoryTabsRow(
                                            strings = mapStrings,
                                            selected = if (markersActivated) uiState.selectedCategory else null,
                                            onSelected = onCategoryTapped
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                    }
                                    // 헤더(제목+탭)와 목록을 가르는 선. 아래 목록이 흰 면 위에 구분선으로만
                                    // 나뉘는 플랫 구조라, 헤더도 같은 문법으로 끊어줘야 스크롤될 때 탭 줄이
                                    // 목록 위에 떠 있는 고정 영역이라는 게 읽힌다.
                                    HorizontalDivider(color = DividerColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (entries.isEmpty()) {
                                        EmptyResultCard(
                                            message = if (uiState.selectedCategory == MapCategory.HOSPITAL) {
                                                mapStrings.emptyHospitalMessage
                                            } else {
                                                mapStrings.emptyPlaceMessage
                                            }
                                        )
                                    } else {
                                        // 옆으로 넘기는 페이지가 아니라 세로로 자연스럽게 스크롤되는 리스트.
                                        // uiState.selectedCategory로 key를 걸어, 카테고리 탭을 바꾸면(병원→관광 등)
                                        // 이전 스크롤 위치가 남지 않고 항상 맨 위부터 다시 보이게 한다.
                                        key(uiState.selectedCategory) {
                                            LazyColumn(
                                                modifier = Modifier.fillMaxWidth().weight(1f),
                                                contentPadding = PaddingValues(bottom = 8.dp)
                                            ) {
                                                itemsIndexed(entries, key = { _, entry -> entry.id }) { index, entry ->
                                                    // 행 사이 구분선. 첫 행 위에는 두지 않는다(탭 줄 아래
                                                    // 구분선이 이미 그 경계를 만든다).
                                                    if (index > 0) {
                                                        HorizontalDivider(
                                                            color = DividerColor,
                                                            // 썸네일 오른쪽 끝까지 긋지 않고 텍스트 시작선에
                                                            // 맞춰 들여쓴다 — 행이 하나씩 끊겨 보이는 대신
                                                            // 목록 전체가 한 덩어리로 이어져 읽힌다.
                                                            modifier = Modifier.padding(start = 20.dp)
                                                        )
                                                    }
                                                    MapPlaceListRow(
                                                        entry = entry,
                                                        categoryLabel = entry.categoryLabel(language, mapStrings),
                                                        visual = placeKindVisual(entry.placeType, entry.placeCategory),
                                                        // 리스트업 페이지에서는 눌렀을 때 바로 상세화면으로 이동한다
                                                        // (지도 마커를 눌러 선택 카드를 띄우는 쪽과 다르다).
                                                        onClick = {
                                                            if (entry.id in hospitalIdSet) onSelectHospital(entry.id) else onSelectPlace(entry.id)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // 마커를 직접 눌러 선택한 경우: 목록 대신 사진 위주 카드로 보여준다(사진+뱃지+
                        // 상세보기 버튼). 선택이 풀려 카드가 빠져나가는 동안에도 계속 그려져야 하므로,
                        // 지금 선택값(selectedHospital)이 아니라 이 전환이 들고 있는 key로 항목을 다시
                        // 찾는다 — 예전엔 그 순간 selectedHospital이 null이 되면서 나가던 카드가 미리보기
                        // 행으로 뒤바뀐 채 사라졌다.
                        state.startsWith("hospital:") -> {
                            val hospital = categoryHospitals.firstOrNull { it.id == state.removePrefix("hospital:") }
                            if (hospital != null) {
                                // 내용에 따라 높이가 달라지므로 실측해서 내 위치 버튼
                                // (recenterBottomPadding)이 그 높이를 따라오게 한다.
                                Box(
                                    modifier = Modifier.onGloballyPositioned { coordinates ->
                                        selectedCardHeight = with(density) { coordinates.size.height.toDp() }
                                    }
                                ) {
                                    SelectedHospitalCard(
                                        hospital = hospital,
                                        isFavorite = hospital.id in uiState.favoriteHospitalIds,
                                        detailButtonLabel = mapStrings.detailButtonLabel,
                                        closeContentDescription = mapStrings.closeSelectionContentDescription,
                                        onFavoriteClick = { onToggleFavorite(hospital.id) },
                                        onDetailClick = { onSelectHospital(hospital.id) },
                                        onClose = { onMarkerSelected(null) }
                                    )
                                }
                            }
                        }
                        state.startsWith("place:") -> {
                            val place = visiblePlaces.firstOrNull { it.id == state.removePrefix("place:") }
                            if (place != null) {
                                Box(
                                    modifier = Modifier.onGloballyPositioned { coordinates ->
                                        selectedCardHeight = with(density) { coordinates.size.height.toDp() }
                                    }
                                ) {
                                    SelectedPlaceCard(
                                        place = place,
                                        isFavorite = place.id in uiState.favoritePlaceIds,
                                        detailButtonLabel = mapStrings.detailButtonLabel,
                                        closeContentDescription = mapStrings.closeSelectionContentDescription,
                                        onFavoriteClick = { onTogglePlaceFavorite(place.id) },
                                        onDetailClick = { onSelectPlace(place.id) },
                                        onClose = { onMarkerSelected(null) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// HospitalSearchListScreen의 진료과목 필터와 같은 MedicalCategory 10종 + 재사용 컴포넌트(FilterChipPill)를
// 써서, 지도에서도 같은 기준으로 병원을 거를 수 있게 한다. 서버 재요청 없이 이미 받아둔 목록을
// 클라이언트에서만 거른다(MapUiState.visibleHospitals 참고) — HospitalSearchList와 달리 이 지도
// 화면은 좌표 기준 조회 API를 쓰고 있어 specialties 파라미터를 추가하는 건 별도 작업으로 남겨둔다.
@Composable
private fun SpecialtyFilterRow(
    language: SupportedLanguage,
    selectedSpecialties: Set<String>,
    resetLabel: String,
    onSpecialtyToggled: (String) -> Unit,
    onCleared: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(MedicalCategory.entries.toList(), key = { it.label }) { category ->
            FilterChipPill(
                label = category.translatedLabel(language),
                selected = category.label in selectedSpecialties,
                onClick = { onSpecialtyToggled(category.label) }
            )
        }
        if (selectedSpecialties.isNotEmpty()) {
            item(key = "reset") {
                FilterChipPill(label = resetLabel, selected = false, onClick = onCleared)
            }
        }
    }
}

@Composable
private fun MapSearchBar(query: String, onQueryChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(48.dp)
            // 지도 위에 떠 있는 컨트롤인데 그림자가 없어 색이 진한 지도 타일 위에서 밋밋하게
            // 붙어 보였다 — 옅은 그림자로 "떠 있다"는 걸 분명히 한다.
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(percent = 50), ambientColor = Color.Black.copy(alpha = 0.15f), spotColor = Color.Black.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.favicon),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(text = placeholder, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                cursorBrush = SolidColor(CoralPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// 예전엔 onClick이 없는 장식용 버튼이었다 — 진료과목 필터 줄을 여닫는 실제 동작을 붙였다.
// 필터가 하나라도 선택돼 있으면(펼쳐져 있지 않아도) 배지 숫자로 몇 개가 걸려있는지 보여준다.
// 텍스트 라벨 없이 아이콘만 있는 원형 버튼이라, 그 라벨을 접근성 트리에서 뺀 아이콘 대신 이제
// 버튼 자체의 contentDescription으로 옮겼다(더 이상 옆에서 같은 말을 읽어주는 Text가 없어서).
@Composable
private fun FilterPillButton(
    contentDescription: String,
    active: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(elevation = 4.dp, shape = CircleShape, ambientColor = Color.Black.copy(alpha = 0.15f), spotColor = Color.Black.copy(alpha = 0.15f))
                .clip(CircleShape)
                .background(if (active) CoralPrimary else Color.White)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = contentDescription,
                tint = if (active) Color.White else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (active) Color.White else CoralPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                // CoralPrimary 글자를 White/CoralPrimaryContainer 배경에 얹으면 명암비가 각각
                // 약 2.9:1, 2.5:1로 WCAG AA 본문 기준(4.5:1)에 못 미친다(HospitalDetailScreen의
                // CategoryAndStatusRow와 같은 이유) — 배지 숫자는 짙은 색으로 확실히 읽히게 한다.
                Text(
                    text = badgeCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
            }
        }
    }
}

// "이 위치에서 검색": 지도를 옮긴 뒤 눌러야 그 시점의 카메라 중심 좌표로 GET /api/hospitals/nearby를 호출한다.
// 기기 GPS를 쓰지 않고 사용자가 명시적으로 요청했을 때만 좌표를 서버로 보낸다(CLAUDE.md §1).
@Composable
private fun SearchThisAreaButton(
    isLoading: Boolean,
    label: String,
    loadingLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = RoundedCornerShape(percent = 50))
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (isLoading) loadingLabel else label,
            style = MaterialTheme.typography.labelMedium,
            color = CoralPrimary
        )
    }
}

@Composable
private fun CategoryTabsRow(strings: MapStrings, selected: MapCategory?, onSelected: (MapCategory) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryTab(label = strings.categoryAllLabel, icon = Icons.Default.Apps, selected = selected == MapCategory.ALL, onClick = { onSelected(MapCategory.ALL) })
        CategoryTab(label = strings.categoryHospitalLabel, icon = Icons.Default.LocalHospital, selected = selected == MapCategory.HOSPITAL, onClick = { onSelected(MapCategory.HOSPITAL) })
        // 산/나무 대신 카메라 아이콘 — 이 탭이 커버하는 "관광"은 관광지 외에도 쇼핑/숙박/스파처럼
        // 자연과 무관한 곳이 훨씬 많아, 지도 마커(ic_map_pin_tourist.xml)와 같은 이유로 바꿨다.
        CategoryTab(label = strings.categoryTouristLabel, icon = Icons.Default.PhotoCamera, selected = selected == MapCategory.TOURIST, onClick = { onSelected(MapCategory.TOURIST) })
        CategoryTab(label = strings.categoryFoodLabel, icon = Icons.Default.Restaurant, selected = selected == MapCategory.FOOD, onClick = { onSelected(MapCategory.FOOD) })
    }
}

@Composable
private fun CategoryTab(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(percent = 50), ambientColor = Color.Black.copy(alpha = 0.12f), spotColor = Color.Black.copy(alpha = 0.12f))
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) CoralPrimary else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // FilterPillButton과 같은 이유로 장식용 처리 — 바로 옆 Text(label)가 이미 같은 문구를 읽어준다.
        Icon(imageVector = icon, contentDescription = null, tint = if (selected) Color.White else TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = if (selected) Color.White else TextPrimary)
    }
}

// "전체" 탭에서 병원과 장소가 한 줄에 섞여 나올 수 있어, 카드에 필요한 필드만 뽑아
// Hospital/Place 구분 없이 하나의 목록으로 다룬다.
// hospital.imageUrl은 백엔드가 실제 사진을 내려주지 않아 항상 null이다(HospitalMapper 참고) —
// 병원 카드는 Home/HospitalSearchListScreen과 동일하게 resolveHospitalThumbnailRes로 고른 진료과
// 사진을 폴백으로 들고 다니고, 실제 사진이 있을 수 있는 장소(Place)는 폴백 없이 그대로 둔다.
private data class CardEntry(
    val id: String,
    val title: String,
    val subtitle: String,
    val languages: List<String>,
    val imageUrl: String?,
    val fallbackImageRes: Int? = null,
    // 장소(Place)일 때만 채워진다 — 관광/음식 API 응답에 사진이 없는 경우가 많아, 그때 회색 빈
    // 박스 대신 이 종류에 맞는 색+아이콘 썸네일(PlaceFallbackThumbnail)을 그리는 데 쓴다.
    val placeType: PlaceType? = null,
    // placeType보다 한 단계 자세한 분류(백화점/전통시장/면세점 등). 대다수는 OTHER이고, 그때는
    // 칩이 placeType 라벨로 되돌아간다 — PlaceCategory.translatedLabel 주석 참고.
    val placeCategory: PlaceCategory = PlaceCategory.OTHER,
    // 지도 화면 중심에서의 거리(m). 목록 정렬 기준이자 행에 "350m"로 표시된다. 좌표가 없으면 null.
    val distanceMeters: Double? = null
)

private fun Hospital.toCardEntry(origin: MapPoint) = CardEntry(
    id = id,
    title = name,
    subtitle = districtLabel(),
    languages = supportedLanguages,
    imageUrl = imageUrl,
    fallbackImageRes = resolveHospitalThumbnailRes(name, specialties),
    distanceMeters = distanceFrom(origin, latitude, longitude)
)

private fun Place.toCardEntry(origin: MapPoint) = CardEntry(
    id = id,
    title = name,
    subtitle = address,
    languages = emptyList(),
    imageUrl = imageUrl,
    placeType = type,
    placeCategory = category,
    distanceMeters = distanceFrom(origin, latitude, longitude)
)

private fun distanceFrom(origin: MapPoint, latitude: Double?, longitude: Double?): Double? {
    if (latitude == null || longitude == null) return null
    return haversineDistanceMeters(origin.latitude, origin.longitude, latitude, longitude)
}

/**
 * 1km 미만은 10m 단위 m로, 그 이상은 소수 한 자리 km로 — 숫자만 쓰므로 언어별 문구가 필요 없다.
 *
 * 소수점 구분자는 [Locale.US]로 못 박는다. 앱 언어는 SupportedLanguage/AppStrings가 따로 들고 있는데
 * String.format은 **기기** 기본 로케일을 따르므로, 그대로 두면 같은 앱 언어에서도 기기 설정에 따라
 * `1.2km`와 `1,2km`가 섞여 나온다(PlaceDetailScreen의 같은 함수도 Locale.US를 쓴다).
 */
private fun Double.toDistanceLabel(): String =
    if (this < 1_000.0) "${(this / 10).toInt() * 10}m" else String.format(Locale.US, "%.1fkm", this / 1_000.0)

/**
 * 칩에 쓸 글자. 자세한 쪽부터 고른다 — 세부 분류(백화점/전통시장/면세점) → 장소 종류(관광지/쇼핑/
 * 숙소/카페·맛집) → 병원.
 *
 * 세부 분류는 백엔드가 TourAPI cat3를 아는 장소에만 붙는다(대부분은 PlaceCategory.OTHER =
 * 빈 문자열). 그래서 "있으면 쓰고 없으면 한 단계 위로 되돌아가는" 이 순서가 필요하다 — 목록에
 * 절반은 "백화점", 절반은 "기타"가 뜨는 대신 "백화점 / 쇼핑"처럼 아는 만큼만 자세해진다.
 */
private fun CardEntry.categoryLabel(language: SupportedLanguage, strings: MapStrings): String {
    val type = placeType ?: return strings.categoryHospitalLabel
    return placeCategory.translatedLabel(language).ifBlank { type.translatedLabel(language) }
}

/**
 * 행 왼쪽 아래에 붙는 종류 표시. 그 종류의 색을 옅게 깔고, 같은 계열의 진한 색으로 아이콘과 글자를 얹는다.
 *
 * 아이콘이 있어야 "백화점"과 "전통시장"처럼 색이 같은(둘 다 쇼핑 계열) 항목이 글자를 읽기 전에도
 * 구분된다 — 색은 묶음, 아이콘은 종류를 맡는다(core/ui/PlaceKindVisuals.kt).
 *
 * 배경은 면 색(visual.color), 글자·아이콘은 ink다 — 11sp짜리 글자를 면 색 그대로 쓰면 음식(주황)·
 * 쇼핑(청록) 칩이 옅은 배경 위에서 거의 안 읽혔다.
 */
@Composable
private fun CategoryChip(label: String, visual: PlaceKindVisual) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(visual.color.copy(alpha = 0.12f))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.ink,
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = visual.ink
        )
    }
}

/**
 * 펼친 목록("목적별로 찾는 장소")의 한 행. 텍스트가 왼쪽, 썸네일이 오른쪽인 플랫 리스트 행이다.
 *
 * 예전에는 이 자리에도 흰 카드 행(+ 6dp 그림자)을 썼는데, 시트 배경(HomeBackgroundPink,
 * #FFFAFA)과 카드(순백)의 명도차가 거의 없어서 행 구분을 전적으로 그림자에 기대고 있었다 — 행이
 * 열 개씩 쌓이면 그림자가 서로 번져 회색 얼룩처럼 보였다. 시트를 통째로 흰 면으로 두고 행 사이를
 * 얇은 구분선으로만 나누면, 그림자 없이도 경계가 분명하고 목록이 훨씬 조용해진다.
 *
 * 접힌(미리보기) 시트도 이제 카드 한 줄이 아니라 제목+개수 요약 줄이라, 이 파일에서 흰 카드
 * 행(ListRowEntry)을 쓰던 자리는 모두 없어졌다 — 지도 위에 뜨는 카드는 마커 선택 카드뿐이다.
 */
@Composable
private fun MapPlaceListRow(
    entry: CardEntry,
    categoryLabel: String,
    visual: PlaceKindVisual,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 지도 화면 중심에서의 거리를 주소 앞에 둔다 — 목록이 이 거리순으로 정렬돼 있어
                // "왜 이 순서인지"가 바로 읽힌다.
                entry.distanceMeters?.let { distance ->
                    Text(
                        text = distance.toDistanceLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = CoralPrimary,
                        maxLines = 1
                    )
                    Text(text = " · ", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Text(
                    text = entry.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // 종류 칩과 지원 언어 배지를 한 줄로 묶는다 — 병원은 "병원 EN JP", 장소는 칩만 남는다.
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(label = categoryLabel, visual = visual)
                entry.languages.take(3).forEach { lang -> LanguageBadge(text = lang.toLanguageBadgeLabel()) }
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        // 세로형(0.75)에서 가로형에 가까운 정사각에 가깝게 — 가로 행에서 세로로 긴 사진은 행 높이를
        // 끌어올리기만 하고 정작 무엇이 찍혔는지는 덜 보였다.
        val thumbnail = Modifier.size(width = 88.dp, height = 72.dp).clip(RoundedCornerShape(12.dp))
        when {
            entry.imageUrl != null -> AsyncImageBox(
                model = entry.imageUrl,
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = thumbnail.background(DividerColor)
            )
            entry.fallbackImageRes != null -> Image(
                painter = painterResource(id = entry.fallbackImageRes),
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = thumbnail.background(DividerColor)
            )
            entry.placeType != null -> PlaceFallbackThumbnail(visual = visual, modifier = thumbnail)
            else -> AsyncImageBox(
                model = null,
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = thumbnail.background(DividerColor)
            )
        }
    }
}

// 마커를 직접 눌러 선택했을 때 뜨는 사진 위주 카드. 펼친 목록의 플랫 행(MapPlaceListRow)과 달리
// 썸네일이 크고, 즐겨찾기·상세보기 버튼이 붙는다 — 지도 위에 떠야 해서 그림자가 있는 흰 카드다.
@Composable
private fun SelectedHospitalCard(
    hospital: Hospital,
    isFavorite: Boolean,
    detailButtonLabel: String,
    closeContentDescription: String,
    onFavoriteClick: () -> Unit,
    onDetailClick: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HospitalThumbnail(
                    hospital = hospital,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DividerColor)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = hospital.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = hospital.districtLabel(), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                // 예전엔 지도 빈 곳을 눌러야만 닫혔다 — 눈에 보이는 닫기 수단을 카드 안에 둔다.
                SelectionCloseButton(contentDescription = closeContentDescription, onClick = onClose)
            }
            if (hospital.supportedLanguages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    hospital.supportedLanguages.take(3).forEach { lang -> LanguageBadge(text = lang.toLanguageBadgeLabel()) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
                ) {
                    Text(text = detailButtonLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).border(width = 1.dp, color = DividerColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    FavoriteHeartButton(isFavorite = isFavorite, onClick = onFavoriteClick, size = 32.dp)
                }
            }
        }
    }
}

// SelectedHospitalCard와 완전히 같은 구성으로 맞춘다: 썸네일 + 이름 + 주소(메타 한 줄)만 보여주고,
// "상세보기"를 눌러야 설명 등 나머지 정보를 보게 한다 — 마커를 눌렀을 때 병원과 장소가 같은 느낌으로 보이도록.
@Composable
private fun SelectedPlaceCard(
    place: Place,
    isFavorite: Boolean,
    detailButtonLabel: String,
    closeContentDescription: String,
    onFavoriteClick: () -> Unit,
    onDetailClick: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlaceThumbnail(
                    place = place,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DividerColor)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = place.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                SelectionCloseButton(contentDescription = closeContentDescription, onClick = onClose)
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 장소도 상세화면·즐겨찾기 화면에서 이미 즐겨찾기 대상인데 이 카드에만 없어서, 마커를
            // 눌렀을 때 병원과 장소가 서로 다른 카드처럼 보였다 — 병원 카드와 같은 구성으로 맞춘다.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
                ) {
                    Text(text = detailButtonLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).border(width = 1.dp, color = DividerColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    FavoriteHeartButton(isFavorite = isFavorite, onClick = onFavoriteClick, size = 32.dp)
                }
            }
        }
    }
}

// 선택 카드 오른쪽 위 X. RoundIconButton(44dp 기본)과 같은 터치 크기를 유지하되, 카드 안에
// 얹히는 보조 버튼이라 배경 없이 아이콘만 둔다.
@Composable
private fun SelectionCloseButton(contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = contentDescription,
            tint = TextSecondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

// HospitalThumbnail과 같은 역할 — 관광/음식 API 응답에 사진이 없는 장소가 많아, 그 경우엔
// 빈 회색 박스 대신 장소 종류에 맞춘 대체 썸네일을 그린다.
@Composable
private fun PlaceThumbnail(place: Place, modifier: Modifier = Modifier) {
    if (place.imageUrl != null) {
        AsyncImageBox(model = place.imageUrl, contentDescription = place.name, modifier = modifier)
    } else {
        PlaceFallbackThumbnail(visual = placeKindVisual(place.type, place.category), modifier = modifier)
    }
}

// 사진이 없는 장소의 대체 썸네일 — 종류별 색 그라데이션 + 아이콘 배지를 작은 카드 썸네일 크기에
// 맞춰 줄인 것. 아이콘·색은 core/ui/PlaceKindVisuals.kt 한 곳에서 받아 쓴다(예전엔 이 파일과
// PlaceDetailScreen이 각자 다른 팔레트를 들고 있어 같은 장소가 화면마다 다른 색으로 보였다).
@Composable
private fun PlaceFallbackThumbnail(visual: PlaceKindVisual, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(visual.color.copy(alpha = 0.20f), visual.color.copy(alpha = 0.06f))
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.color,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun EmptyResultCard(message: String) {
    // 펼친 목록이 그림자 없는 흰 면이 되면서(MapPlaceListRow) 이 자리도 같이 평평해졌다 —
    // 예전엔 연분홍 배경 위에 떠야 해서 흰 Surface + 16dp 그림자를 썼는데, 흰 바탕 위에서는
    // 그 그림자가 회색 얼룩으로만 남는다.
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = InactiveIcon,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

// hospital.imageUrl은 백엔드가 실제 사진을 내려주지 않아 항상 null이다(HospitalMapper 참고) — 이걸
// 그대로 AsyncImageBox에 넘기면(예전 코드) 빈 회색 박스만 보인다. Home/HospitalSearchListScreen이
// 이미 쓰는 것과 같은 폴백(resolveHospitalThumbnailRes: 진료과 태그·이름 기반으로 고른 대표 사진)을
// 여기서도 재사용해, 지도 위 병원 썸네일(하단 카드/목록/선택 카드)이 더 이상 빈 채로 뜨지 않게 한다.
@Composable
private fun HospitalThumbnail(hospital: Hospital, modifier: Modifier = Modifier) {
    if (hospital.imageUrl != null) {
        AsyncImageBox(model = hospital.imageUrl, contentDescription = hospital.name, modifier = modifier)
    } else {
        Image(
            painter = painterResource(id = resolveHospitalThumbnailRes(hospital.name, hospital.specialties)),
            contentDescription = hospital.name,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

private fun Hospital.districtLabel(): String {
    val district = address.split(" ").firstOrNull { it.endsWith("구") || it.endsWith("군") || it.endsWith("시") }
    val category = specialties.firstOrNull()
    return listOfNotNull(category, district).joinToString(" · ").ifBlank { address }
}

private fun Hospital.toMapPin(selectedId: String?): MapPin? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null
    return MapPin(id = id, latitude = lat, longitude = lng, type = MapPinType.HOSPITAL, selected = id == selectedId)
}

private fun Place.toMapPin(selectedId: String?): MapPin? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null
    // TOURIST_ATTRACTION 외 나머지(SHOPPING/LODGING/SPA/WALK/OTHER)는 지도 카테고리 탭(전체/병원/관광/음식)에
    // 아직 전용 탭이 없어 일단 "관광" 마커로 묶는다.
    val pinType = if (type == PlaceType.RESTAURANT) MapPinType.FOOD else MapPinType.TOURIST
    return MapPin(id = id, latitude = lat, longitude = lng, type = pinType, selected = id == selectedId)
}
