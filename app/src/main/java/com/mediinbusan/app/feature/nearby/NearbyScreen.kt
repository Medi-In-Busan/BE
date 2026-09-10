package com.mediinbusan.app.feature.nearby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralInk
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.MediBlue40
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsDivider
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.i18n.translatedTourismItemCategoryLabel
import com.mediinbusan.app.core.navigation.BottomBarScaleController
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BrandTopAppBar
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.CongestionLevelBadge
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapMarkerFallbackThumbnail
import com.mediinbusan.app.core.ui.PlaceKindVisual
import com.mediinbusan.app.core.ui.placeKindVisual
import com.mediinbusan.app.core.ui.InitialCardRevealCount
import com.mediinbusan.app.core.ui.ShimmerSkeleton
import com.mediinbusan.app.core.ui.rememberCardRevealProgress
import com.mediinbusan.app.core.ui.rememberRevealedCount
import com.mediinbusan.app.core.ui.rememberScrollShrinkAnimation
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.domain.course.HospitalWellnessRoute
import com.mediinbusan.app.domain.tourism.TourismHotPlace
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismTagGroup
import com.mediinbusan.app.domain.tourism.placeCategoryCodes
import com.mediinbusan.app.domain.tourism.toTourismTagGroup
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage

@Composable
fun NearbyScreen(
    hospitalId: String,
    onSelectTourismItem: () -> Unit,
    onNavigateToTourismCatalog: (TourismCatalogCategory, String?) -> Unit,
    onSearchTourism: (TourismCatalogCategory, String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: NearbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val languageCode = LocalAppStrings.current.language.code

    LaunchedEffect(hospitalId, languageCode) {
        viewModel.load(hospitalId, languageCode)
    }

    NearbyContent(
        uiState = uiState,
        onSelectHotPlace = { hotPlace ->
            viewModel.selectHotPlace(hotPlace)
            onSelectTourismItem()
        },
        onSelectCatalogItem = { category, item ->
            viewModel.selectTourismItem(category, item)
            onSelectTourismItem()
        },
        onNavigateToTourismCatalog = onNavigateToTourismCatalog,
        onSearchTourism = onSearchTourism,
        onNavigateToSettings = onNavigateToSettings,
        onLanguageSelected = viewModel::onLanguageSelected,
        onRetry = { viewModel.load(hospitalId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NearbyContent(
    uiState: NearbyUiState,
    onSelectHotPlace: (TourismHotPlace) -> Unit,
    onSelectCatalogItem: (TourismCatalogCategory, TourismCatalogItem) -> Unit,
    onNavigateToTourismCatalog: (TourismCatalogCategory, String?) -> Unit,
    onSearchTourism: (TourismCatalogCategory, String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onRetry: () -> Unit
) {
    // Home/병원목록과 같은 하단 탭바 축소 연출 — feature끼리 직접 참조 못 하는 규칙(CLAUDE.md §4)
    // 때문에 BottomBarScaleController 싱글턴으로만 scale을 전달한다(core/ui/BottomBarScrollShrink.kt).
    val listState = rememberLazyListState()
    val (bottomBarScrollConnection, bottomBarScale) = rememberScrollShrinkAnimation(listState)
    LaunchedEffect(bottomBarScale) { BottomBarScaleController.setScale(bottomBarScale) }
    DisposableEffect(Unit) { onDispose { BottomBarScaleController.setScale(1f) } }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            BrandTopAppBar(
                onSettingsClick = onNavigateToSettings,
                currentLanguageCode = uiState.selectedLanguage,
                onLanguageSelected = onLanguageSelected,
                containerColor = Color.White
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
            // 서버가 빈 목록을 준 경우다. 필터로 좁혀서 빈 게 아니라 받은 게 없는 것이라,
            // 되돌릴 필터가 없고 다시 불러오는 것 말고 할 수 있는 일이 없다.
            uiState.places.isEmpty() -> EmptyState(
                message = LocalAppStrings.current.nearby.emptyNearbyMessage,
                modifier = Modifier.padding(innerPadding),
                actionLabel = LocalAppStrings.current.common.retryButtonLabel,
                onAction = onRetry
            )
            else -> NearbyLoadedContent(
                uiState = uiState,
                onSelectHotPlace = onSelectHotPlace,
                onSelectCatalogItem = onSelectCatalogItem,
                onNavigateToTourismCatalog = onNavigateToTourismCatalog,
                onSearchTourism = onSearchTourism,
                listState = listState,
                scrollConnection = bottomBarScrollConnection,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun NearbyLoadedContent(
    uiState: NearbyUiState,
    onSelectHotPlace: (TourismHotPlace) -> Unit,
    onSelectCatalogItem: (TourismCatalogCategory, TourismCatalogItem) -> Unit,
    onNavigateToTourismCatalog: (TourismCatalogCategory, String?) -> Unit,
    onSearchTourism: (TourismCatalogCategory, String) -> Unit,
    listState: LazyListState,
    scrollConnection: NestedScrollConnection,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize().nestedScroll(scrollConnection),
        // 좌우 여백은 아이템마다 직접 준다 — 핫플레이스 TOP5(1/2/3등 사진 영역)만 다른 섹션보다
        // 좁은 여백(10dp)을 둬서 가로로 더 넓게 보이게 하기 위함.
        contentPadding = PaddingValues(top = 14.dp, bottom = BottomNavBarHeight + 32.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            WellnessSearchAndFilterSection(onSelectCategory = onNavigateToTourismCatalog, onSearch = onSearchTourism)
        }

        item {
            HotTourismTopFiveSection(
                hotPlaces = uiState.hotPlaces,
                isLoading = uiState.isHotPlacesLoading,
                errorMessage = uiState.hotPlacesError,
                onSelectHotPlace = onSelectHotPlace,
                onSeeAll = { onNavigateToTourismCatalog(TourismCatalogCategory.CROWDING, null) },
                // 위 필터 원형 버튼들과 이 섹션 사이가 너무 붙어 보여서 위쪽에만 여백을 더 준다.
                modifier = Modifier.padding(start = 10.dp, top = 20.dp, end = 10.dp)
            )
        }

        item {
            TourismCatalogEntrySection(
                tourismPreviews = uiState.tourismPreviews,
                accessiblePreviews = uiState.accessiblePreviews,
                onSelectItem = onSelectCatalogItem,
                onNavigate = { category -> onNavigateToTourismCatalog(category, null) },
                // 위 핫플레이스 랭킹 영역과의 간격도 필터↔랭킹 영역과 같은 폭(위쪽 20dp 추가)으로 맞춘다.
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp)
            )
        }

    }
}

// F-011/F-014 웰니스 서치+필터 배치. 필터 4종(관광지/숙박/맛집/무장애)은 각각 부산 관광지·무장애
// 관광 리스트업으로 바로 던지고, 서치바는 검색 실행(키보드 검색/엔터) 시 부산 관광지 리스트업으로
// 쿼리를 넘긴다 — 핫플레이스·무장애 관광까지 아우르는 통합검색은 없다(그 둘은 각자 화면에 자체
// 검색바가 있다). 부산 관광지가 관광지/숙박/맛집 필터의 도착지와 같아 일관성이 있고, 백엔드에
// 3개 도메인을 합치는 통합 검색 API가 없어 이 이상은 서버 작업이 필요하다.
@Composable
private fun WellnessSearchAndFilterSection(
    onSelectCategory: (TourismCatalogCategory, String?) -> Unit,
    onSearch: (TourismCatalogCategory, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 요청: 서치바는 필터 원형 버튼(20dp 여백 유지)보다 살짝 더 넓게 — 좌우 여백을 1dp씩 줄여
        // 총 2dp 더 길어 보이게 한다.
        WellnessCatalogSearchBar(onSearch = onSearch, modifier = Modifier.padding(horizontal = 19.dp))
        WellnessCategoryFilterRow(onSelectCategory = onSelectCategory, modifier = Modifier.padding(horizontal = 20.dp))
    }
}

// 요청: 코랄 외곽선 대신 회색 베이스(테두리 없음)로, 돋보기 아이콘은 왼쪽에 회색으로 배치.
@Composable
private fun WellnessCatalogSearchBar(onSearch: (TourismCatalogCategory, String) -> Unit, modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    val strings = LocalAppStrings.current
    val tourismCategory = tourismCategoryForLanguage(strings.language.code)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(SettingsDivider)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(imageVector = Icons.Default.Search, contentDescription = strings.common.searchContentDescription, tint = TextSecondary)
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = strings.nearby.wellnessSearchPlaceholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = TextStyle(color = TextPrimary, fontSize = MaterialTheme.typography.bodyMedium.fontSize),
                cursorBrush = Brush.verticalGradient(listOf(CoralPrimary, CoralPrimary)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        val trimmed = query.trim()
                        if (trimmed.isNotEmpty()) onSearch(tourismCategory, trimmed)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private data class WellnessFilterOption(
    val key: String,
    val label: String,
    @param:androidx.annotation.DrawableRes val iconRes: Int,
    val category: TourismCatalogCategory,
    val categoryCode: String?
)

@Composable
private fun WellnessCategoryFilterRow(
    onSelectCategory: (TourismCatalogCategory, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.nearby
    val language = LocalAppStrings.current.language
    // 언어별로 TourAPI contenttypeid 체계가 달라(PLACES_KO=12/32/39, EN/JA/ZH=76/80/82 — placeCategoryCodes
    // 참고) 코드를 하드코딩하지 않고 현재 언어에 맞는 부산 관광지 카테고리에서 직접 가져온다.
    val tourismCategory = tourismCategoryForLanguage(language.code)
    val placeCodes = tourismCategory.placeCategoryCodes()
    val options = listOf(
        WellnessFilterOption("12", "12".translatedTourismItemCategoryLabel(language).orEmpty(), R.drawable.wellness_tour, tourismCategory, placeCodes.spot),
        WellnessFilterOption("32", "32".translatedTourismItemCategoryLabel(language).orEmpty(), R.drawable.wellness_rest, tourismCategory, placeCodes.lodging),
        WellnessFilterOption("39", strings.wellnessFilterFoodLabel, R.drawable.wellness_food, tourismCategory, placeCodes.food),
        WellnessFilterOption(TourismCatalogCategory.ACCESSIBLE.name, strings.wellnessFilterAccessibleLabel, R.drawable.wellness_mujange, TourismCatalogCategory.ACCESSIBLE, null)
    )
    var selectedKey by remember { mutableStateOf<String?>(null) }
    // 4dp로는 시각적으로 서치바 끝선과 거의 같아 보였다 — 확실히 그 안쪽이라고 보이도록 살짝만 더 당긴다.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEach { option ->
            WellnessFilterCircleItem(
                iconRes = option.iconRes,
                label = option.label,
                selected = option.key == selectedKey,
                onClick = {
                    selectedKey = if (selectedKey == option.key) null else option.key
                    onSelectCategory(option.category, option.categoryCode)
                }
            )
        }
    }
}

@Composable
private fun WellnessFilterCircleItem(iconRes: Int, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            // 사진 자체가 이미 여백 없이 잘려있어서 별도 확대 없이 원 크기에 맞춰 배치만 한다.
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TourismCatalogEntrySection(
    tourismPreviews: List<TourismCatalogItem>,
    accessiblePreviews: List<TourismCatalogItem>,
    onSelectItem: (TourismCatalogCategory, TourismCatalogItem) -> Unit,
    onNavigate: (TourismCatalogCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val tourismCategory = tourismCategoryForLanguage(LocalAppStrings.current.language.code)
    // 부산 관광 ↔ 무장애 관광 슬라이더 간격도 위 필터↔랭킹, 랭킹↔부산관광 간격과 같게 맞춘다.
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(42.dp)) {
        TourismPlaceSlider(
            logoRes = R.drawable.wellness_tour,
            title = LocalAppStrings.current.nearby.busanTourismTitle,
            description = LocalAppStrings.current.nearby.busanTourismDescription,
            category = tourismCategory,
            items = tourismPreviews,
            accent = SkyBlue,
            tagFor = { item -> item.categoryCode?.toTourismPlaceTag() },
            onSelectItem = onSelectItem,
            onSeeAll = onNavigate
        )
        TourismPlaceSlider(
            logoRes = R.drawable.wellness_mujange,
            title = LocalAppStrings.current.nearby.accessibleTourismTitle,
            description = LocalAppStrings.current.nearby.accessibleTourismDescription,
            category = TourismCatalogCategory.ACCESSIBLE,
            items = accessiblePreviews,
            accent = CoralPrimary,
            // 무장애관광은 세분화하지 않고 항목 전부 같은 태그를 붙인다.
            tagFor = { TourismPlaceTag.ACCESSIBLE },
            onSelectItem = onSelectItem,
            onSeeAll = onNavigate
        )
    }
}

// 홈의 "이런 코스는 어떠세요"(RecommendedCourseCard)와 같은 미리보기/사이즈(가로 스크롤 LazyRow,
// 고정폭 카드로 다음 카드가 살짝 보이는 방식)를 따르되, 카드 자체는 핫플레이스 2·3등 카드
// (HotPlaceGridCard)처럼 사진으로 전체를 채우고 하단 그라데이션 위에 흰 글씨로 타이틀/위치를 얹는다.
@Composable
private fun TourismPlaceSlider(
    logoRes: Int,
    title: String,
    description: String,
    category: TourismCatalogCategory,
    items: List<TourismCatalogItem>,
    accent: Color,
    tagFor: (TourismCatalogItem) -> TourismPlaceTag?,
    onSelectItem: (TourismCatalogCategory, TourismCatalogItem) -> Unit,
    onSeeAll: (TourismCatalogCategory) -> Unit
) {
    val revealedCount = rememberRevealedCount(itemsKey = items, itemCount = items.size)
    // 요청: 헤더↔카드 목록 여백을 거의 붙을 정도(2dp)로 좁힌다. 실제 시각적 간격은 아래
    // LazyRow 카드 래퍼의 top padding(2dp)이 만들어주므로 여기 자체 간격은 0으로 둔다.
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Column(modifier = Modifier.padding(horizontal = 2.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, style = SectionTitleStyle, color = TextPrimary)
            }
            // 요청: 전체보기를 타이틀이 아니라 서브타이틀(설명 텍스트)과 같은 줄에 나란히 배치한다.
            // 서브타이틀은 왼쪽 정렬 유지(타이틀 텍스트 시작 위치에 맞추는 들여쓰기는 넣지 않는다).
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                // 핫플레이스 TOP5 헤더가 쓰는 회색 텍스트 + ">" 아이콘 패턴으로 통일. 글자 크기(labelMedium)는 그대로 유지.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onSeeAll(category) }
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = LocalAppStrings.current.nearby.allRanksLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        softWrap = false
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        if (items.isEmpty()) {
            TourismPlaceEmptyCard()
        } else {
            // LazyRow의 contentPadding만으로는 카드 그림자가 안 보였다 — TourismRevealContent가 카드를
            // graphicsLayer(alpha) Box로 한 번 더 감싸는데, 그 Box의 레이아웃 크기가 카드(140x180)와
            // 정확히 같아서 밖으로 번지는 그림자가 이 Box 경계에서 잘렸다. 카드를 감싸는 이 Box 자체를
            // 그림자만큼 여유 있게 키워야(패딩) 확실히 보인다 — 좌우 6dp씩(=기존 12dp 간격 유지,
            // spacedBy는 0으로), 위 2dp/아래 10dp(하단 스팟 그림자가 더 진해서 더 크게).
            LazyRow(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                    TourismRevealContent(
                        index = index,
                        revealedCount = revealedCount,
                        skeletonPadding = PaddingValues(start = 6.dp, top = 2.dp, end = 6.dp, bottom = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(start = 6.dp, top = 2.dp, end = 6.dp, bottom = 10.dp)
                        ) {
                            TourismPlaceCard(
                                item = item,
                                accent = accent,
                                tag = tagFor(item),
                                onClick = { onSelectItem(category, item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TourismPlaceCard(item: TourismCatalogItem, accent: Color, tag: TourismPlaceTag?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            // 화면에 카드가 1.5개 정도 보이던 걸 2.5개 정도 보이게 폭을 줄인다(210dp -> 140dp).
            .width(140.dp)
            .height(180.dp)
            // 카드마다 개별로 그림자를 건다(핫플 4·5위 리스트와 같은 방식). 14dp/alpha 0.5는
            // 블러가 너무 넓게 퍼져 옆 카드(12dp 간격)와 이어져 하나의 영역처럼 보였다 — 카드
            // 사이 간격 안에서 각자 그림자로 분리돼 보이도록 더 얇고 옅게 낮춘다.
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(accent.copy(alpha = 0.16f), CoralPrimaryContainer)))
            .clickable(onClick = onClick)
    ) {
        val imageUrl = item.imageUrl
        if (imageUrl != null) {
            AsyncImageBox(imageUrl, item.title, Modifier.fillMaxSize())
        } else {
            // 사진이 없으면 태그 종류(관광지/숙박/음식)에 맞는 디폴트를 쓴다. Map의 PlaceFallbackThumbnail은
            // 흰 배경 위 작은 인라인 썸네일 전용이라(6~20% 저채도 틴트) 이 카드 자체 배경(대각선
            // 그라데이션) 위에 얹으면 반투명이 겹쳐 탁한 사각형처럼 보인다 — 그래서 여기 전용으로
            // 카드 배경을 완전히 덮는 불투명 파스텔 그라데이션 버전을 따로 쓴다(Map 쪽은 그대로 둔다).
            when (tag) {
                TourismPlaceTag.LODGING -> TourismCardFallbackThumbnail(placeKindVisual(PlaceType.LODGING), Modifier.fillMaxSize())
                TourismPlaceTag.FOOD -> TourismCardFallbackThumbnail(placeKindVisual(PlaceType.RESTAURANT), Modifier.fillMaxSize())
                else -> MapMarkerFallbackThumbnail(Modifier.fillMaxSize(), iconSize = 32.dp)
            }
        }
        // 요청: 카드 전체를 덮던 그림자를 원래의 40% 수준으로 옅게 낮춘다.
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.03f), Color.Black.copy(alpha = 0.30f)))
            )
        )
        // 핫플레이스 1/2/3등 배지(HotPlaceRankBadge)와 같은 위치·같은 렌더링 방식으로, 카테고리
        // 태그(관광지/숙소/맛집/무장애)를 좌상단에 얹는다.
        if (tag != null) {
            TourismTagBadge(
                tag = tag,
                height = 24.dp,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            )
        }
        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                item.title,
                style = CardTitleStyle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.78f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    item.address ?: item.subtitle ?: LocalAppStrings.current.nearby.tourismFallbackLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// TourismPlaceCard(140x180 포토카드) 전용 폴백 — Map의 PlaceFallbackThumbnail(작은 인라인
// 썸네일용, 6~20% 저채도 틴트)과 달리 카드 배경을 완전히 덮어야 해서, 종류 색을 흰색과
// 섞어 만든 불투명 파스텔로 그라데이션을 건다(MapMarkerFallbackThumbnail과 같은 방식).
@Composable
private fun TourismCardFallbackThumbnail(visual: PlaceKindVisual, modifier: Modifier = Modifier) {
    val pastel = visual.color.copy(alpha = 0.18f).compositeOver(Color.White)
    Box(
        modifier = modifier.background(Brush.linearGradient(listOf(pastel, Color.White))),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.color,
            modifier = Modifier.size(32.dp)
        )
    }
}

// 관광지/숙소/맛집/무장애 태그 배지 종류. 예전엔 wellness_busantour/busanrest/busaneat/
// busanmujange 4종 이미지에 문구가 통째로 그려져 있어 언어를 바꿔도 한국어 그대로 남았다.
private enum class TourismPlaceTag { TOUR, LODGING, FOOD, ACCESSIBLE }

// 그룹(domain/tourism/TourismCatalog.kt의 TourismTagGroup)을 태그 종류로 매핑한다.
// 그룹 분류 자체는 NearbyViewModel.kt의 미리보기 균형 배분과 공유한다(같은 기준 유지).
private fun String.toTourismPlaceTag(): TourismPlaceTag? = when (toTourismTagGroup()) {
    TourismTagGroup.SPOT -> TourismPlaceTag.TOUR
    TourismTagGroup.LODGING -> TourismPlaceTag.LODGING
    TourismTagGroup.FOOD -> TourismPlaceTag.FOOD
    null -> null
}

// 핫플레이스 순위 배지(HotPlaceRankBadge)·혼잡도 배지(CongestionLevelBadge)와 같은 방식 —
// 배경/아이콘/문구를 전부 Compose로 직접 그려서 문구가 LocalAppStrings를 따라가게 한다.
@Composable
private fun TourismTagBadge(tag: TourismPlaceTag, height: Dp, modifier: Modifier = Modifier) {
    val strings = LocalAppStrings.current.nearby
    val (accentColor, icon, label) = when (tag) {
        TourismPlaceTag.TOUR -> Triple(CoralInk, Icons.Default.Place, strings.placeTypeLabels["TOURIST_ATTRACTION"].orEmpty())
        TourismPlaceTag.LODGING -> Triple(SkyBlue, Icons.Default.Hotel, strings.placeTypeLabels["LODGING"].orEmpty())
        TourismPlaceTag.FOOD -> Triple(Color(0xFFB2650A), Icons.Default.Restaurant, strings.wellnessFilterFoodLabel)
        TourismPlaceTag.ACCESSIBLE -> Triple(Color(0xFF1E9E63), Icons.AutoMirrored.Filled.Accessible, strings.wellnessFilterAccessibleLabel)
    }
    val fillColor = lerp(Color.White, accentColor, 0.16f)
    val density = LocalDensity.current
    Row(
        modifier = modifier
            .height(height)
            .clip(CircleShape)
            .background(fillColor)
            .padding(start = height * 0.14f, end = height * 0.32f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(height * 0.16f)
    ) {
        // 원본 태그 사진은 동그라미가 배지 안에서 여백을 두고 떠 있다 — 꽉 채우지 않는다.
        Box(
            modifier = Modifier
                .size(height * 0.72f)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(height * 0.4f)
            )
        }
        Text(
            text = label,
            color = accentColor,
            fontWeight = FontWeight.Bold,
            fontSize = with(density) { (height * 0.42f).toSp() },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TourismPlaceEmptyCard() {
    Surface(
        modifier = Modifier.width(140.dp).height(180.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(LocalAppStrings.current.nearby.tourismLoadingMessage, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
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
                text = LocalAppStrings.current.nearby.heroTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = hospitalName?.let { LocalAppStrings.current.nearby.nearbyHospitalHeroFormat.format(it) }
                    ?: LocalAppStrings.current.nearby.nearbyHeroDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(0.82f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroBadge(LocalAppStrings.current.nearby.recommendedCountFormat.format(placeCount))
                if (courseCount > 0) {
                    HeroBadge(LocalAppStrings.current.nearby.personalizedCourseCountFormat.format(courseCount))
                }
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
    onSelectHotPlace: (TourismHotPlace) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val revealedCount = rememberRevealedCount(itemsKey = hotPlaces, itemCount = hotPlaces.size)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // 요청: LIVE/타이틀/서브텍스트가 1st 카드 내부 콘텐츠(버튼/텍스트 시작점, 16dp 들여쓰기)보다
            // 왼쪽에 있어 헤더 전체가 왼쪽으로 치우쳐 보였다 — 전체보기는 그대로 두고 이 세 요소만
            // 1st 카드 내부 시작 위치에 맞춰 오른쪽으로 16dp 밀어준다.
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = CoralPrimaryContainer,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "LIVE",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = CoralPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            // 요청: 타이틀 크기를 headlineMedium 대비 15% 줄이고 옆에 불 이모지를 붙인다.
            Text(
                text = "${LocalAppStrings.current.nearby.hotPlaceTopFiveTitle} 🔥",
                style = MaterialTheme.typography.headlineMedium.let { it.copy(fontSize = it.fontSize * 0.85f) },
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 16.dp)
            )
            // 요청: 전체보기를 타이틀이 아니라 서브텍스트와 같은 줄에 나란히 배치(부산관광/무장애관광과 동일한 패턴).
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = LocalAppStrings.current.nearby.crowdingForecastLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 16.dp).weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onSeeAll)
                ) {
                    Text(
                        text = LocalAppStrings.current.nearby.allRanksLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
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
            hotPlaces.isEmpty() -> WellnessSectionCard {
                Text(LocalAppStrings.current.nearby.emptyHotPlaceMessage, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TourismRevealContent(index = 0, revealedCount = revealedCount) {
                    HotPlaceFeaturedCard(
                        hotPlace = hotPlaces.first(),
                        onClick = { onSelectHotPlace(hotPlaces.first()) }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    hotPlaces.drop(1).take(2).forEachIndexed { index, hotPlace ->
                        TourismRevealContent(
                            index = index + 1,
                            revealedCount = revealedCount,
                            modifier = Modifier.weight(1f)
                        ) {
                            HotPlaceGridCard(
                                rank = index + 2,
                                hotPlace = hotPlace,
                                onClick = { onSelectHotPlace(hotPlace) }
                            )
                        }
                    }
                }

                val compactPlaces = hotPlaces.drop(3).take(2)
                if (compactPlaces.isNotEmpty()) {
                    // 요청: 4·5위가 하나의 Surface 안에 구분선으로만 붙어 있던 걸, 의료기관 목록처럼
                    // 각각 독립된 카드로 떼어놓는다. 크기·구성 요소(순위/썸네일/제목/혼잡도 등)는 그대로.
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        compactPlaces.forEachIndexed { index, hotPlace ->
                            TourismRevealContent(index = index + 3, revealedCount = revealedCount) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    shadowElevation = 2.dp
                                ) {
                                    HotPlaceCompactRow(
                                        rank = index + 4,
                                        hotPlace = hotPlace,
                                        onClick = { onSelectHotPlace(hotPlace) }
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

@Composable
private fun TourismRevealContent(
    index: Int,
    revealedCount: Int,
    modifier: Modifier = Modifier,
    // TourismPlaceSlider처럼 content() 안쪽에 그림자용 여유 패딩(6/2/6/10dp)이 들어있는 경우, 그
    // 패딩까지 matchParentSize로 그대로 덮으면 스켈레톤이 옆 카드 스켈레톤과 여백 없이 붙어 보인다
    // — 실제 카드가 앉는 자리만큼만 스켈레톤도 안쪽으로 밀어 넣는다(같은 값을 그대로 전달).
    skeletonPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit
) {
    val isAnimated = index < InitialCardRevealCount
    val revealProgress = rememberCardRevealProgress(isAnimated, index < revealedCount)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = revealProgress
                    translationY = (1f - revealProgress) * 10.dp.toPx()
                }
        ) {
            content()
        }
        if (isAnimated && revealProgress < 1f) {
            ShimmerSkeleton(alpha = 1f - revealProgress, modifier = Modifier.matchParentSize().padding(skeletonPadding))
        }
    }
}

// 1/2/3위 전용 순위 배지. 예전엔 wellness_1st/2nd/3rd 이미지에 "1위 · 지금 가장 핫함" 같은 문구가
// 통째로 그려져 있어 언어를 바꿔도 한국어 그대로 남았다 — 배경/아이콘/문구를 전부 Compose로 직접
// 그려서 문구가 LocalAppStrings를 따라가게 한다.
@Composable
private fun HotPlaceRankBadge(rank: Int, height: Dp, modifier: Modifier = Modifier) {
    val strings = LocalAppStrings.current.nearby
    val (background, emoji, label) = when (rank) {
        1 -> Triple(Brush.linearGradient(listOf(Color(0xFF232329), Color(0xFF17171B))), "🔥", strings.hotPlaceRank1Label)
        2 -> Triple(Brush.linearGradient(listOf(Color(0xFFEE4C86), Color(0xFFFF9472))), "✨", strings.hotPlaceRank2Label)
        else -> Triple(Brush.linearGradient(listOf(Color(0xFF2FC08C), Color(0xFF8CEFC0))), "🌱", strings.hotPlaceRank3Label)
    }
    val density = LocalDensity.current
    Row(
        modifier = modifier
            .height(height)
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = height * 0.32f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(height * 0.12f)
    ) {
        Text(text = emoji, fontSize = with(density) { (height * 0.5f).toSp() })
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = with(density) { (height * 0.42f).toSp() },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HotPlaceThumbnailImage(hotPlace: TourismHotPlace, modifier: Modifier = Modifier, iconSize: Dp = 28.dp) {
    val imageUrl = hotPlace.item.imageUrl
    if (imageUrl != null) {
        AsyncImageBox(imageUrl, hotPlace.item.title, modifier)
    } else {
        MapMarkerFallbackThumbnail(modifier, iconSize)
    }
}

@Composable
private fun HotPlaceFeaturedCard(hotPlace: TourismHotPlace, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // 200dp였을 때는 2/3위(고정 180dp)와 육안으로 거의 구분이 안 됐다 — 1위임이 분명히 보이도록 더 키운다.
            .heightIn(min = 260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color.White)))
            .clickable(onClick = onClick)
    ) {
        // heightIn(min=...)는 최댓값이 없어 카드 실제 높이가 사진/제목 줄바꿈에 따라 늘어난다 —
        // fillMaxSize()는 이 "최종 높이"보다 먼저 스스로의 콘텐츠 크기로 측정돼 위쪽에 붙어버리므로,
        // 다른 형제(뱃지/텍스트)까지 다 정해진 뒤의 실제 박스 크기에 맞춰지는 matchParentSize를 쓴다.
        HotPlaceThumbnailImage(hotPlace, Modifier.matchParentSize(), iconSize = 44.dp)
        // 2·3등과 같은 패턴 — 사진 전체를 덮지 않고 텍스트가 앉는 하단 구간에만 그라데이션을 건다.
        Box(
            Modifier.align(Alignment.BottomStart).fillMaxWidth().height(120.dp).background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f)))
            )
        )
        HotPlaceRankBadge(
            rank = 1,
            height = 34.dp,
            modifier = Modifier.align(Alignment.TopStart).padding(14.dp)
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = hotPlace.item.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.78f),
                    modifier = Modifier.size(14.dp)
                )
                Text(LocalAppStrings.current.nearby.busanDistrictFormat.format(hotPlace.district.translatedLabel(LocalAppStrings.current.language)), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.78f))
            }
            // 혼잡도 배지와 ">" 아이콘은 위치 고정 — 항상 맨 아래 Row에 둬서 수평 위치가 자동으로 맞게 한다.
            Row(verticalAlignment = Alignment.CenterVertically) {
                CongestionLevelBadge(congestionRate = hotPlace.congestionRate, height = 28.dp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = LocalAppStrings.current.nearby.detailContentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

// 1위 카드(HotPlaceFeaturedCard)와 같은 톤 — 흰 카드 위쪽에만 사진을 두는 대신 사진으로 배경을
// 전부 채우고, 제목/위치/혼잡도 배지는 하단 그라데이션(홈 히어로 배너1과 같은 처리) 위에 흰 글씨로 얹는다.
@Composable
private fun HotPlaceGridCard(
    rank: Int,
    hotPlace: TourismHotPlace,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            // heightIn(min=...)는 최솟값만 줄 뿐 위쪽 경계가 없어서, Row 안에서 높이가 무제한으로
            // 측정될 때 사진의 fillMaxSize()가 채울 목표 크기를 못 정하고 원본 크기로 그려지는
            // 문제가 있었다 — 고정 height로 바꿔서 사진이 실제로 카드 전체를 채우게 한다.
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color.White)))
            .clickable(onClick = onClick)
    ) {
        HotPlaceThumbnailImage(hotPlace, Modifier.fillMaxSize(), iconSize = 36.dp)
        // 사진 전체를 어둡게 덮지 않고 텍스트가 앉는 하단 구간에만 그라데이션을 건다 — 위쪽 사진은 원본 밝기 그대로 보인다.
        Box(
            Modifier.align(Alignment.BottomStart).fillMaxWidth().height(90.dp).background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f)))
            )
        )
        HotPlaceRankBadge(
            rank = rank,
            height = 22.dp,
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                hotPlace.item.title,
                style = CardTitleStyle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.78f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    LocalAppStrings.current.nearby.busanDistrictFormat.format(hotPlace.district.translatedLabel(LocalAppStrings.current.language)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.78f)
                )
            }
            // 혼잡도 배지와 ">" 아이콘을 같은 Row에 둬서 수평 위치가 자동으로 맞게 한다.
            Row(verticalAlignment = Alignment.CenterVertically) {
                CongestionLevelBadge(congestionRate = hotPlace.congestionRate, height = 24.dp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = LocalAppStrings.current.nearby.detailContentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun HotPlaceCompactRow(rank: Int, hotPlace: TourismHotPlace, onClick: () -> Unit) {
    // 요청: 4·5위를 각각 독립 카드로 분리하면서 사라진 좌우 여백(원래 병합 Surface의
    // horizontal=14dp)을 다시 넣어, 내용이 카드 가장자리에 붙지 않고 안쪽으로 모이게 한다.
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(rank.toString(), style = MaterialTheme.typography.titleMedium, color = TextSecondary, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(CoralPrimaryContainer)
        ) {
            HotPlaceThumbnailImage(hotPlace, Modifier.fillMaxSize(), iconSize = 20.dp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(hotPlace.item.title, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Place, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                Text(LocalAppStrings.current.nearby.busanDistrictFormat.format(hotPlace.district.translatedLabel(LocalAppStrings.current.language)), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        CongestionLevelBadge(congestionRate = hotPlace.congestionRate, height = 27.dp)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextSecondary, modifier = Modifier.size(24.dp))
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
            Text(text = LocalAppStrings.current.nearby.emptyFilterMessage, style = CardTitleStyle, color = TextPrimary)
            OutlinedButton(onClick = onReset) {
                Text(text = LocalAppStrings.current.nearby.resetPlacesLabel)
            }
        }
    }
}

@Composable
private fun RecommendedRouteSection(routes: List<HospitalWellnessRoute>, onClick: (Int) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { routes.size })
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = LocalAppStrings.current.nearby.recommendedCourseTitle, style = SectionTitleStyle, color = TextPrimary)
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
                    RouteBadge(text = LocalAppStrings.current.nearby.stopCountFormat.format(route.stops.size))
                    RouteBadge(text = durationLabel(route.estimatedDurationMinutes))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = LocalAppStrings.current.nearby.routeDistanceSummaryFormat
                            .format(route.totalDistanceKm.formatDistance()),
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

@Composable
private fun courseTitle(index: Int): String = LocalAppStrings.current.nearby.courseTitles.getOrElse(index) {
    LocalAppStrings.current.nearby.courseTitles.last()
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
        Text(text = LocalAppStrings.current.nearby.nearbyPlacesTitle, style = SectionTitleStyle, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { onTypeSelected(null) },
                    label = { Text(text = LocalAppStrings.current.nearby.allLabel) },
                    colors = nearbyFilterChipColors()
                )
            }
            items(types, key = { it.name }) { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    label = { Text(text = type.localizedLabel()) },
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
                        Text(text = place.type.localizedLabel(), style = MaterialTheme.typography.labelSmall, color = place.type.tint, fontWeight = FontWeight.SemiBold)
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
                    text = place.description?.takeUnless { it.startsWith("http") } ?: LocalAppStrings.current.nearby.placeDescriptionFallback,
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

@Composable
private fun PlaceType.localizedLabel(): String = LocalAppStrings.current.nearby.placeTypeLabels[name].orEmpty()

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

@Composable
private fun durationLabel(minutes: Int): String {
    val strings = LocalAppStrings.current.tourism
    return if (minutes >= 60) {
        "${minutes / 60}${strings.walkingHourUnit} ${minutes % 60}${strings.walkingMinuteUnit}"
    } else {
        "${minutes}${strings.walkingMinuteUnit}"
    }
}
