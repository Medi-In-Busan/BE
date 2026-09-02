package com.mediinbusan.app.feature.map

import com.mediinbusan.app.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.FavoriteHeartButton
import com.mediinbusan.app.core.ui.FilterChipPill
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LanguageBadge
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.RouteStop
import com.mediinbusan.app.core.ui.RoundIconButton
import com.mediinbusan.app.core.ui.toLanguageBadgeLabel
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
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
            onSelectHospital = onSelectHospital,
            onSelectPlace = onSelectPlace,
            onSearchThisArea = viewModel::searchThisArea,
            onSpecialtyFilterToggled = viewModel::onSpecialtyFilterToggled,
            onSpecialtyFiltersCleared = viewModel::onSpecialtyFiltersCleared,
            onLanguageFilterToggled = viewModel::onLanguageFilterToggled
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
private val ListRowFixedHeight = 96.dp
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
    onSelectHospital: (String) -> Unit,
    onSelectPlace: (String) -> Unit,
    onSearchThisArea: (latitude: Double, longitude: Double) -> Unit,
    onSpecialtyFilterToggled: (String) -> Unit,
    onSpecialtyFiltersCleared: () -> Unit,
    onLanguageFilterToggled: () -> Unit
) {
    val mapStrings = LocalAppStrings.current.map
    val language = LocalAppStrings.current.language
    // "필터" 버튼을 누르면 진료과목 칩 줄을 펼쳤다 접었다 한다 — 예전엔 버튼만 있고 실제 필터
    // 기능이 없는 자리표시자였다.
    var showSpecialtyFilters by remember { mutableStateOf(false) }
    // 카드영역: 기본은 "미리보기"(손잡이 + 리스트 첫 항목만 66% 노출) 상태로 접혀있고, 손잡이를
    // 위로 드래그(또는 탭)하면 검색바까지 덮는 전체 리스트 페이지로 펼쳐진다. 마커를 새로 선택하면
    // 그 항목을 미리보기로 보여주면 되므로, 펼쳐져 있었어도 미리보기로 되돌아간다.
    var isListExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.selectedMarkerId) {
        if (uiState.selectedMarkerId != null) isListExpanded = false
    }
    val hospitalPins = remember(uiState.visibleHospitals, uiState.selectedMarkerId) {
        uiState.visibleHospitals.mapNotNull { it.toMapPin(uiState.selectedMarkerId) }
    }
    val placePins = remember(uiState.visiblePlaces, uiState.selectedMarkerId) {
        uiState.visiblePlaces.mapNotNull { it.toMapPin(uiState.selectedMarkerId) }
    }
    // 첫 진입 시 이미 로드돼있는 병원/장소가 한 번에 다 마커로 쏟아져서 지도를 뒤덮는 문제 —
    // "이 위치에서 검색" 또는 카테고리 탭(전체/병원/관광/음식, 아무거나) 중 하나를 직접 누르기
    // 전까지는 마커를 아예 안 그리고, 카메라만 기본 위치로 이동한 채 가만히 있게 한다. 그 외
    // 기존 로직(목록/필터/카테고리 계산)은 그대로.
    var markersActivated by remember { mutableStateOf(false) }
    val pins = if (markersActivated) {
        when (uiState.selectedCategory) {
            MapCategory.ALL -> hospitalPins + placePins
            MapCategory.HOSPITAL -> hospitalPins
            else -> placePins
        }
    } else {
        emptyList()
    }
    // 0은 "아직 요청 없음"을 의미하는 초기값이라 KakaoMapView가 무시한다 — 버튼 클릭마다 증가시켜 트리거한다.
    var recenterRequestId by remember { mutableIntStateOf(0) }
    var searchAreaRequestId by remember { mutableIntStateOf(0) }

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
    val recenterBottomPadding by animateDpAsState(
        targetValue = if (isSelectionActive) {
            navigationBarInset + selectedCardHeight + 4.dp
        } else {
            navigationBarInset + ListRowFixedHeight + PeekHandleAreaHeight + 4.dp
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
            fitCameraToPins = false
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
                Spacer(modifier = Modifier.width(8.dp))
                FilterPillButton(
                    contentDescription = mapStrings.filterLabel,
                    active = showSpecialtyFilters || uiState.selectedSpecialties.isNotEmpty(),
                    badgeCount = uiState.selectedSpecialties.size,
                    onClick = { showSpecialtyFilters = !showSpecialtyFilters }
                )
            }
            AnimatedVisibility(visible = showSpecialtyFilters) {
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
                onSelected = { category ->
                    // 병원 탭만 활성화시키던 게 버그였다 — 관광/음식 탭을 눌러도 미리보기 내용만
                    // 바뀌고 지도엔 마커가 안 뜨는 문제로 이어졌다. 어떤 탭을 누르든 활성화한다.
                    markersActivated = true
                    onCategorySelected(category)
                }
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
            if (uiState.selectedCategory == MapCategory.HOSPITAL || uiState.selectedCategory == MapCategory.ALL) {
                Spacer(modifier = Modifier.height(8.dp))
                SearchThisAreaButton(
                    isLoading = uiState.isSearchingArea,
                    label = mapStrings.searchThisAreaLabel,
                    loadingLabel = mapStrings.searchingThisAreaLabel,
                    onClick = { searchAreaRequestId++; markersActivated = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // "전체" 탭에서는 마커가 병원/장소 둘 중 무엇이든 선택될 수 있어, 카테고리로 분기하는
        // 대신 uiState.categoryHospitals(현재 탭에서 병원을 보여줘야 하는지 이미 판단됨)/
        // visiblePlaces 두 목록에서 직접 선택된 항목을 찾는다.
        val selectedHospital = uiState.categoryHospitals.firstOrNull { it.id == uiState.selectedMarkerId }
        val selectedPlace = if (selectedHospital == null) {
            uiState.visiblePlaces.firstOrNull { it.id == uiState.selectedMarkerId }
        } else {
            null
        }
        val entries = uiState.categoryHospitals.map { it.toCardEntry() } + uiState.visiblePlaces.map { it.toCardEntry() }
        // 리스트업(펼침) 페이지에서 행을 눌렀을 때 병원/장소 중 어느 상세화면으로 보낼지 판단하는 데 쓴다.
        val hospitalIdSet = remember(uiState.categoryHospitals) { uiState.categoryHospitals.map { it.id }.toSet() }
        // 미리보기(접힌 상태)에 한 줄만 보여줄 항목 — 선택된 마커가 있으면 그 항목, 없으면 목록 맨 앞.
        val peekEntry = when {
            selectedHospital != null -> selectedHospital.toCardEntry()
            selectedPlace != null -> selectedPlace.toCardEntry()
            else -> entries.firstOrNull()
        }

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
            RoundIconButton(
                icon = Icons.Default.MyLocation,
                contentDescription = mapStrings.recenterContentDescription,
                onClick = {
                    onMarkerSelected(null)
                    recenterRequestId++
                },
                background = CoralPrimary,
                tint = Color.White,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(bottom = recenterBottomPadding, end = 16.dp)
            )
        }

        // 카드영역: 접힘(미리보기 한 줄) / 펼침(검색바까지 덮는 전체 리스트) 두 상태를 손잡이
        // 드래그(또는 탭)로 전환한다. HospitalSearchListScreen과 같은 느낌의 가로형 리스트 행을
        // 쓰되, feature 패키지 간 직접 import는 하지 않는 규칙(CLAUDE.md)이라 이 파일 안에 로컬로
        // 새로 둔다(ListRowEntry).
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                // 펼쳤을 때도 화면 전체가 아니라 "검색바 블록 바로 아래"까지만 — 그 위 공간은
                // 계속 지도가 보인다. 검색바 블록 높이가 필터 펼침 등으로 달라질 수 있어 실측값을 쓴다.
                .then(if (isListExpanded) Modifier.height((screenHeight - topBarHeight).coerceAtLeast(0.dp)) else Modifier)
                .shadow(
                    elevation = if (isListExpanded) 0.dp else 10.dp,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.2f),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                // 시트 배경은 흰색이 아니라 Home/HospitalSearchList와 같은 앱 기본 배경색(연분홍) —
                // 그 위에 얹히는 리스트 항목(ListRowEntry)이 각자 흰 카드+그림자로 구분돼 보인다.
                .background(HomeBackgroundPink)
                // 미리보기 콘텐츠(손잡이+리스트 행)는 탭바 높이를 피하지 않고 제스처 인셋만 남긴다 —
                // 손잡이는 그래도 행 위에 있어서 탭바보다 위, 즉 계속 터치 가능하고, 리스트 행만
                // 자연스럽게 아랫부분이 탭바에 가려진다(클리핑 없이 원래 크기 그대로 그림).
                // 펼침(리스트업) 상태에서는 반대로 탭바를 완전히 피해야 한다 — 마지막 항목이
                // 탭바 뒤에 가려지지 않고 그 바로 위에 오도록 bottomSafePadding(탭바 높이 포함)을 쓴다.
                .padding(bottom = if (isListExpanded) bottomSafePadding else navigationBarInset)
                .animateContentSize(animationSpec = tween(220))
        ) {
            var dragAccumPx by remember { mutableStateOf(0f) }
            // 마커를 직접 선택했을 때는 리스트업으로 끌어올리는 손잡이 자체가 의미 없다(예전
            // 사진 카드만 단독으로 뜨던 모양으로 돌아간다) — 미리보기/펼침 상태에서만 보여준다.
            if (selectedHospital == null && selectedPlace == null) {
                // 알약 모양(양옆이 완전히 둥근) 드래그 손잡이. 영역 맨 위에 바짝 붙이고(위 여백 없음),
                // 위/아래로 끌거나 탭해서 펼침·접힘을 토글한다.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 10.dp)
                        .pointerInput(isListExpanded) {
                            detectVerticalDragGestures(
                                onDragEnd = { dragAccumPx = 0f },
                                onDragCancel = { dragAccumPx = 0f },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    dragAccumPx += dragAmount
                                    if (!isListExpanded && dragAccumPx < -dragThresholdPx) {
                                        isListExpanded = true
                                        dragAccumPx = 0f
                                    } else if (isListExpanded && dragAccumPx > dragThresholdPx) {
                                        isListExpanded = false
                                        dragAccumPx = 0f
                                    }
                                }
                            )
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { isListExpanded = !isListExpanded }
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

            if (isListExpanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Text(
                        text = mapStrings.listPageTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoryTabsRow(
                        strings = mapStrings,
                        selected = uiState.selectedCategory,
                        onSelected = { category ->
                            if (category == MapCategory.HOSPITAL) markersActivated = true
                            onCategorySelected(category)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (entries.isEmpty()) {
                    EmptyResultCard(
                        message = if (uiState.selectedCategory == MapCategory.HOSPITAL) {
                            mapStrings.emptyHospitalMessage
                        } else {
                            mapStrings.emptyPlaceMessage
                        }
                    )
                } else {
                    // 4개씩 한 그룹 — 한 페이지 안에 리스트처럼 세로로 4개 쌓는다. 옆으로 넘기면
                    // 다음 4개 그룹이 나오고, 페이지 가장자리가 살짝 미리보이는 게(contentPadding)
                    // "다음 그룹 미리보기" 역할을 한다(Home HeroBannerSection과 같은 피킹 패턴).
                    // 페이지 영역은 타이틀 바로 아래부터 시트 하단(탭바 위)까지 남는 공간을 그대로
                    // 채운다(weight) — 4개가 균등 분할해서 채우니 마지막 항목이 자연스럽게 맨 아래,
                    // 탭바 바로 위에 온다.
                    val groupedEntries = remember(entries) { entries.chunked(4) }
                    val pagerState = rememberPagerState(pageCount = { groupedEntries.size })
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 12.dp,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) { page ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            groupedEntries[page].forEach { entry ->
                                ListRowEntry(
                                    entry = entry,
                                    // 리스트업 페이지에서는 눌렀을 때 바로 상세화면으로 이동한다
                                    // (선택 상태로만 바꾸는 미리보기 쪽과 다르다).
                                    onClick = {
                                        if (entry.id in hospitalIdSet) onSelectHospital(entry.id) else onSelectPlace(entry.id)
                                    },
                                    modifier = Modifier.weight(1f),
                                    // 행 높이에 맞춰 늘어나는 세로형 사진 — 가로 폭을 1.5배 키워서
                                    // 비율을 0.5(1:2)에서 0.75로 늘렸다.
                                    thumbnailModifier = Modifier.fillMaxHeight().aspectRatio(0.75f)
                                )
                            }
                        }
                    }
                }
            } else if (selectedHospital != null || selectedPlace != null) {
                // 마커를 직접 눌러 선택한 경우: 리스트 미리보기 행이 아니라 예전에 쓰던 사진 위주
                // 카드로 보여준다(사진+뱃지+상세보기 버튼). 내용에 따라 높이가 달라지므로 실측해서
                // 내 위치 버튼(recenterBottomPadding)이 그 높이를 따라오게 한다.
                Box(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        selectedCardHeight = with(density) { coordinates.size.height.toDp() }
                    }
                ) {
                    if (selectedHospital != null) {
                        SelectedHospitalCard(
                            hospital = selectedHospital,
                            isFavorite = selectedHospital.id in uiState.favoriteHospitalIds,
                            detailButtonLabel = mapStrings.detailButtonLabel,
                            onFavoriteClick = { onToggleFavorite(selectedHospital.id) },
                            onDetailClick = { onSelectHospital(selectedHospital.id) }
                        )
                    } else if (selectedPlace != null) {
                        SelectedPlaceCard(
                            place = selectedPlace,
                            detailButtonLabel = mapStrings.detailButtonLabel,
                            onDetailClick = { onSelectPlace(selectedPlace.id) }
                        )
                    }
                }
            } else {
                // 미리보기: 리스트 행을 원래 크기(96dp) 그대로, 그림자도 온전히 살려서 그린다.
                // 억지로 클리핑하지 않고, 아랫부분은 그 위에 그려지는 하단 탭바가 자연스럽게 가린다.
                if (peekEntry != null) {
                    ListRowEntry(
                        entry = peekEntry,
                        onClick = { onMarkerSelected(peekEntry.id) },
                        modifier = Modifier.padding(horizontal = 20.dp).height(ListRowFixedHeight)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(ListRowFixedHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.selectedCategory == MapCategory.HOSPITAL) {
                                mapStrings.emptyHospitalMessage
                            } else {
                                mapStrings.emptyPlaceMessage
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
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
    val fallbackImageRes: Int? = null
)

private fun Hospital.toCardEntry() = CardEntry(
    id = id,
    title = name,
    subtitle = districtLabel(),
    languages = supportedLanguages,
    imageUrl = imageUrl,
    fallbackImageRes = resolveHospitalThumbnailRes(name, specialties)
)

private fun Place.toCardEntry() =
    CardEntry(id = id, title = name, subtitle = address, languages = emptyList(), imageUrl = imageUrl)

// HospitalSearchListScreen의 SearchResultCard와 같은 느낌(왼쪽 썸네일 + 오른쪽 텍스트, 같은
// 그림자 톤으로 흰 카드가 배경 위에 붕 떠 보임)의 가로형 리스트 행. feature 패키지끼리는 서로
// import하지 않는 규칙이라 그 컴포저블을 직접 가져다 쓰는 대신, CardEntry(병원/장소 통합 모델)
// 기준으로 이 파일 안에 로컬로 새로 둔다. 카드영역 배경이 흰색이 아니라 연분홍(HomeBackgroundPink)
// 이라, 각 행이 이 자체 그림자+흰 배경으로 서로 구분돼 보인다.
@Composable
private fun ListRowEntry(
    entry: CardEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // 미리보기(고정 96dp 행)에서는 기존 정사각形 썸네일을 쓰고, 리스트업 그룹(4개, 행이 위아래로
    // 늘어남)에서는 가로1:세로2 비율 썸네일을 쓴다 — 호출부에서 다르게 넘긴다.
    thumbnailModifier: Modifier = Modifier.size(72.dp)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (entry.imageUrl != null) {
            AsyncImageBox(
                model = entry.imageUrl,
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = thumbnailModifier.clip(RoundedCornerShape(12.dp)).background(DividerColor)
            )
        } else if (entry.fallbackImageRes != null) {
            Image(
                painter = painterResource(id = entry.fallbackImageRes),
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = thumbnailModifier.clip(RoundedCornerShape(12.dp)).background(DividerColor)
            )
        } else {
            AsyncImageBox(
                model = null,
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = thumbnailModifier.clip(RoundedCornerShape(12.dp)).background(DividerColor)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = entry.title, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = entry.subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (entry.languages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    entry.languages.take(3).forEach { lang -> LanguageBadge(text = lang.toLanguageBadgeLabel()) }
                }
            }
        }
    }
}

// 마커를 직접 눌러 선택했을 때 뜨는 사진 위주 카드. 리스트업 미리보기 행(ListRowEntry)과 달리
// 썸네일이 크고, 즐겨찾기·상세보기 버튼이 붙는다 — 리스트 리디자인 전부터 쓰던 컴포넌트를 그대로 되살렸다.
@Composable
private fun SelectedHospitalCard(
    hospital: Hospital,
    isFavorite: Boolean,
    detailButtonLabel: String,
    onFavoriteClick: () -> Unit,
    onDetailClick: () -> Unit
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
    detailButtonLabel: String,
    onDetailClick: () -> Unit
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
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDetailClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
            ) {
                Text(text = detailButtonLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// HospitalThumbnail과 같은 역할 — place.imageUrl이 없는 장소도 많아 실패해도 크래시 없이
// 빈 배경만 남는 AsyncImageBox(model=null)로 그냥 둔다.
@Composable
private fun PlaceThumbnail(place: Place, modifier: Modifier = Modifier) {
    AsyncImageBox(model = place.imageUrl, contentDescription = place.name, modifier = modifier)
}

@Composable
private fun EmptyResultCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
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
