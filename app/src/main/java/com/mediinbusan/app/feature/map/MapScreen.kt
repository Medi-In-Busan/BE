package com.mediinbusan.app.feature.map

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
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
            onSearchThisArea = viewModel::searchThisArea,
            onSpecialtyFilterToggled = viewModel::onSpecialtyFilterToggled,
            onSpecialtyFiltersCleared = viewModel::onSpecialtyFiltersCleared
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
                    AsyncImageBox(model = hospital.imageUrl, contentDescription = hospital.name, modifier = Modifier.fillMaxSize())
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
                place.toMapPin(selectedId = null)?.copy(routeIndex = index + 1)?.let(::add)
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

@Composable
private fun BrowseMap(
    uiState: MapUiState,
    onCategorySelected: (MapCategory) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onMarkerSelected: (String?) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSelectHospital: (String) -> Unit,
    onSearchThisArea: (latitude: Double, longitude: Double) -> Unit,
    onSpecialtyFilterToggled: (String) -> Unit,
    onSpecialtyFiltersCleared: () -> Unit
) {
    val mapStrings = LocalAppStrings.current.map
    val language = LocalAppStrings.current.language
    // "필터" 버튼을 누르면 진료과목 칩 줄을 펼쳤다 접었다 한다 — 예전엔 버튼만 있고 실제 필터
    // 기능이 없는 자리표시자였다.
    var showSpecialtyFilters by remember { mutableStateOf(false) }
    // 카드 패널이 지도를 계속 가리는 게 불편할 수 있어 숨겼다 다시 꺼낼 수 있게 한다. 마커/카드를
    // 새로 선택하면 그 정보를 보러 온 것이므로 숨겨져 있었더라도 자동으로 다시 펼친다.
    var isPanelVisible by remember { mutableStateOf(true) }
    LaunchedEffect(uiState.selectedMarkerId) {
        if (uiState.selectedMarkerId != null) isPanelVisible = true
    }
    val hospitalPins = remember(uiState.visibleHospitals, uiState.selectedMarkerId) {
        uiState.visibleHospitals.mapNotNull { it.toMapPin(uiState.selectedMarkerId) }
    }
    val placePins = remember(uiState.visiblePlaces, uiState.selectedMarkerId) {
        uiState.visiblePlaces.mapNotNull { it.toMapPin(uiState.selectedMarkerId) }
    }
    val pins = when (uiState.selectedCategory) {
        MapCategory.ALL -> hospitalPins + placePins
        MapCategory.HOSPITAL -> hospitalPins
        else -> placePins
    }
    // 0은 "아직 요청 없음"을 의미하는 초기값이라 KakaoMapView가 무시한다 — 버튼 클릭마다 증가시켜 트리거한다.
    var recenterRequestId by remember { mutableIntStateOf(0) }
    var searchAreaRequestId by remember { mutableIntStateOf(0) }

    // 하단 카드 패널(목록/선택된 병원/선택된 장소/빈 상태)은 내용에 따라 높이가 크게 달라진다
    // (썸네일이 붙은 목록 카드는 선택 카드보다 훨씬 높다). 고정값으로 우측 버튼을 띄워두면 패널이
    // 커질 때마다 버튼이 파묻히므로, 패널을 직접 실측해 그 높이만큼만 정확히 띄운다.
    val density = LocalDensity.current
    var bottomPanelHeight by remember { mutableStateOf(0.dp) }

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
    Box(modifier = Modifier.fillMaxSize()) {
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
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MapSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    placeholder = mapStrings.searchPlaceholder,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterPillButton(
                    label = mapStrings.filterLabel,
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
            CategoryTabsRow(strings = mapStrings, selected = uiState.selectedCategory, onSelected = onCategorySelected)
            if (uiState.selectedCategory == MapCategory.HOSPITAL || uiState.selectedCategory == MapCategory.ALL) {
                Spacer(modifier = Modifier.height(8.dp))
                SearchThisAreaButton(
                    isLoading = uiState.isSearchingArea,
                    label = mapStrings.searchThisAreaLabel,
                    loadingLabel = mapStrings.searchingThisAreaLabel,
                    onClick = { searchAreaRequestId++ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // 실제 GPS 위치가 아닌 고정된 부산 기본 좌표로만 이동한다 — 위치 권한을 쓰지 않는다.
        // (예전엔 이 옆에 "레이어" 버튼이 더 있었지만 onClick이 비어 있는 미구현 자리표시자라
        // 제거했다 — 실제 동작하는 버튼만 남긴다.)
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
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = bottomSafePadding + bottomPanelHeight + 16.dp, end = 16.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = bottomSafePadding)
                .onGloballyPositioned { coordinates ->
                    bottomPanelHeight = with(density) { coordinates.size.height.toDp() }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 카드 패널을 숨겼다/꺼냈다 할 수 있는 손잡이. 패널 유무와 무관하게 항상 떠 있어서
            // 숨긴 뒤에도 다시 꺼낼 방법이 사라지지 않는다.
            PanelVisibilityHandle(
                isVisible = isPanelVisible,
                onToggle = { isPanelVisible = !isPanelVisible },
                hideContentDescription = mapStrings.hidePanelContentDescription,
                showContentDescription = mapStrings.showPanelContentDescription,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            AnimatedVisibility(
                visible = isPanelVisible,
                enter = slideInVertically(animationSpec = tween(220)) { height -> height / 2 } + fadeIn(tween(220)),
                exit = slideOutVertically(animationSpec = tween(180)) { height -> height / 2 } + fadeOut(tween(180))
            ) {
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
                // 병원을 누르면(마커든 목록 카드든) 이 패널이 뭘 보여줄지가 바뀐다 — 예전엔 when{}이
                // 그 순간 내용을 뚝 끊어 바꿔서 눌렀다는 반응이 잘 안 느껴졌다. panelKey로 "지금 보여줄
                // 패널이 뭔지"를 문자열 하나로 나타내고 AnimatedContent로 감싸, 선택될 때마다 위로
                // 슬라이드 + 페이드 되며 들어오게 한다.
                val panelKey = when {
                    selectedHospital != null -> "hospital:${selectedHospital.id}"
                    selectedPlace != null -> "place:${selectedPlace.id}"
                    entries.isNotEmpty() -> "list"
                    uiState.selectedCategory == MapCategory.HOSPITAL -> "empty_hospital"
                    else -> "empty_place"
                }
                AnimatedContent(
                    targetState = panelKey,
                    transitionSpec = {
                        (slideInVertically(animationSpec = tween(220)) { height -> height / 4 } + fadeIn(tween(220)))
                            .togetherWith(slideOutVertically(animationSpec = tween(180)) { height -> height / 4 } + fadeOut(tween(180)))
                    },
                    label = "mapBottomPanel"
                ) { key ->
                    when {
                        key.startsWith("hospital:") && selectedHospital != null -> SelectedHospitalCard(
                            hospital = selectedHospital,
                            isFavorite = selectedHospital.id in uiState.favoriteHospitalIds,
                            detailButtonLabel = mapStrings.detailButtonLabel,
                            onFavoriteClick = { onToggleFavorite(selectedHospital.id) },
                            onDetailClick = { onSelectHospital(selectedHospital.id) }
                        )
                        key.startsWith("place:") && selectedPlace != null -> SelectedPlaceCard(place = selectedPlace)
                        key == "list" -> EntryCardRow(entries = entries, onCardClick = { onMarkerSelected(it) })
                        key == "empty_hospital" -> EmptyResultCard(message = mapStrings.emptyHospitalMessage)
                        else -> EmptyResultCard(message = mapStrings.emptyPlaceMessage)
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelVisibilityHandle(
    isVisible: Boolean,
    onToggle: () -> Unit,
    hideContentDescription: String,
    showContentDescription: String,
    modifier: Modifier = Modifier
) {
    // 세로 패딩 6dp였을 땐 터치 영역이 32dp(아이콘 20dp + 6dp*2)로, 이 앱의 다른 아이콘 버튼
    // 기준(RoundIconButton 기본 44dp)보다 눈에 띄게 작았다 — UI/UX Pro Max 스킬의 Android 터치
    // 타깃 가이드(최소 48dp, 최소한 이 앱 관례인 44dp에는 맞춘다)에 맞춰 12dp로 키웠다.
    Row(
        modifier = modifier
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(percent = 50), ambientColor = Color.Black.copy(alpha = 0.2f), spotColor = Color.Black.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
            contentDescription = if (isVisible) hideContentDescription else showContentDescription,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
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
        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
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
@Composable
private fun FilterPillButton(label: String, active: Boolean, badgeCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(48.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(percent = 50), ambientColor = Color.Black.copy(alpha = 0.15f), spotColor = Color.Black.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(percent = 50))
            .background(if (active) CoralPrimary else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 바로 옆에 label을 보여주는 Text가 있어 아이콘까지 contentDescription을 달면 스크린
        // 리더가 "필터, 필터"처럼 같은 말을 두 번 읽는다 — 장식용으로 접근성 트리에서 뺀다
        // (UI/UX Pro Max 스킬의 "decorative icon aria hidden" 규칙).
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = null,
            tint = if (active) Color.White else TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = if (active) Color.White else TextPrimary)
        if (badgeCount > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
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
        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (isLoading) loadingLabel else label,
            style = MaterialTheme.typography.labelMedium,
            color = CoralPrimary
        )
    }
}

@Composable
private fun CategoryTabsRow(strings: MapStrings, selected: MapCategory, onSelected: (MapCategory) -> Unit) {
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
private data class CardEntry(
    val id: String,
    val title: String,
    val subtitle: String,
    val languages: List<String>,
    val imageUrl: String?
)

private fun Hospital.toCardEntry() =
    CardEntry(id = id, title = name, subtitle = districtLabel(), languages = supportedLanguages, imageUrl = imageUrl)

private fun Place.toCardEntry() =
    CardEntry(id = id, title = name, subtitle = address, languages = emptyList(), imageUrl = imageUrl)

@Composable
private fun EntryCardRow(entries: List<CardEntry>, onCardClick: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(entries, key = { it.id }) { entry ->
            InfoMiniCard(entry = entry, onClick = { onCardClick(entry.id) })
        }
    }
}

// HospitalSearchListScreen의 SearchResultCard와 같은 그림자 톤(옅은 배경 위에서도 카드가 붕 떠
// 보이도록 alpha를 진하게 줌)을 재사용해 앱 전체에서 "카드"가 같은 느낌으로 보이게 한다. 다만
// 지도 위에 가로로 나열되는 좁은 카드라 썸네일을 왼쪽이 아니라 위쪽에 둔 세로 레이아웃으로 바꿨다.
@Composable
private fun InfoMiniCard(entry: CardEntry, onClick: () -> Unit) {
    // 누르는 동안 살짝 줄어들었다 떼면 되돌아오는 눌림 반응 — 하단 탭바 아이템과 같은 방식으로
    // "눌렀다"는 게 손끝에 바로 느껴지게 한다(디폴트 리플만으로는 지도 위에서 잘 안 보였다).
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "mapCardPressScale")

    Column(
        modifier = Modifier
            .width(196.dp)
            .scale(scale)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        AsyncImageBox(
            model = entry.imageUrl,
            contentDescription = entry.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .background(DividerColor)
        )
        Column(modifier = Modifier.padding(12.dp)) {
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
            // 목록 카드(InfoMiniCard)엔 썸네일이 있는데 마커를 눌러 뜨는 이 카드엔 텍스트뿐이라
            // 같은 화면 안에서 불일치했다 — 이미지를 붙여 맞춘다.
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImageBox(
                    model = hospital.imageUrl,
                    contentDescription = hospital.name,
                    contentScale = ContentScale.Crop,
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

// Place는 아직 전용 상세 진입 동선이 지도에 배선되어 있지 않아, 병원 카드와 달리 정보 표시만 한다.
@Composable
private fun SelectedPlaceCard(place: Place) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = place.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            place.description?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            }
        }
    }
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
