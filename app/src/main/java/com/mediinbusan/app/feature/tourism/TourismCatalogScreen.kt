package com.mediinbusan.app.feature.tourism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.common.haversineDistanceMeters
import com.mediinbusan.app.core.common.toDistanceLabel
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.i18n.translatedTourismItemCategoryLabel
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BackOnlyNavigationBar
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BrandDropdownMenu
import com.mediinbusan.app.core.ui.BrandDropdownMenuItem
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.FilterChipPill
import com.mediinbusan.app.core.ui.InitialCardRevealCount
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.ShimmerSkeleton
import com.mediinbusan.app.core.ui.rememberCardRevealProgress
import com.mediinbusan.app.core.ui.rememberRevealedCount
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.isLanguageVariant
import com.mediinbusan.app.domain.tourism.placeCategoryCodes
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun TourismCatalogScreen(
    categoryName: String,
    onNavigateToCourse: (category: String, district: String?) -> Unit,
    onSelectItem: () -> Unit,
    onBack: () -> Unit,
    viewModel: TourismCatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val language = LocalAppStrings.current.language
    LaunchedEffect(categoryName, language) { viewModel.load(categoryName) }

    if (uiState.category == TourismCatalogCategory.ACCESSIBLE) {
        // 무장애 관광 리스트업 화면만 병원 목록(S-04)과 비슷한 톤의 전용 헤더·검색 UX를 쓴다 — 다른
        // 관광 카테고리(부산 관광지/걷기코스/함께 둘러보기/혼잡도 등)는 기존 TourismCatalogContent를 그대로 쓴다.
        AccessibleTourismCatalogContent(
            uiState = uiState,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onLoadMore = viewModel::loadNextPage,
            onToggleFavorite = viewModel::toggleFavorite,
            onItemSelected = { item ->
                viewModel.selectItem(item)
                onSelectItem()
            },
            onRetry = viewModel::retry,
            onBack = onBack
        )
        return
    }

    if (uiState.category?.isLanguageVariant == true) {
        // "부산 관광지"도 무장애 관광과 같은 헤더·카드·리빌 애니메이션을 쓰되, 카테고리·지역 필터는
        // 칩 대신 드롭다운 2개로, 목록은 개인화 추천 섹션 + 전체 목록 섹션 두 단으로 나눠 보여준다.
        RecommendedPlacesCatalogContent(
            uiState = uiState,
            onDistrictSelected = viewModel::selectDistrict,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onCategoryFilterSelected = viewModel::onCategoryFilterSelected,
            onResetFilters = viewModel::onResetFilters,
            onLoadMore = viewModel::loadNextPage,
            onToggleFavorite = viewModel::toggleFavorite,
            onItemSelected = { item ->
                viewModel.selectItem(item)
                onSelectItem()
            },
            onRetry = viewModel::retry,
            onBack = onBack
        )
        return
    }

    TourismCatalogContent(
        uiState = uiState,
        onDistrictSelected = viewModel::selectDistrict,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSortSelected = viewModel::onSortSelected,
        onCategoryFilterSelected = viewModel::onCategoryFilterSelected,
        onResetFilters = viewModel::onResetFilters,
        onItemSelected = { item ->
            viewModel.selectItem(item)
            onSelectItem()
        },
        onRetry = viewModel::retry,
        onNavigateToCourse = {
            uiState.category?.let { category ->
                onNavigateToCourse(category.name, uiState.selectedDistrict?.name)
            }
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TourismCatalogContent(
    uiState: TourismCatalogUiState,
    onDistrictSelected: (BusanDistrict) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSortSelected: (TourismSortOption) -> Unit,
    onCategoryFilterSelected: (String?) -> Unit,
    onResetFilters: () -> Unit,
    onItemSelected: (TourismCatalogItem) -> Unit,
    onRetry: () -> Unit,
    onNavigateToCourse: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    Scaffold(
        containerColor = HomeBackgroundPink,
        topBar = {
            if (uiState.category == TourismCatalogCategory.CROWDING) {
                BackOnlyNavigationBar(onBack = onBack, background = HomeBackgroundPink)
            } else {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.tourism.backContentDescription)
                        }
                    },
                    title = {
                        Text(
                            uiState.category?.translatedLabel(strings.language)
                                ?: strings.tourism.catalogDefaultTitle
                        )
                    },
                    actions = {
                        val canBuildCourse = uiState.catalog?.items?.count {
                            it.latitude != null && it.longitude != null
                        }?.let { it >= 3 } == true
                        IconButton(onClick = onNavigateToCourse, enabled = canBuildCourse) {
                            Icon(Icons.Default.Map, contentDescription = strings.tourism.openMapLabel)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(innerPadding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage,
                modifier = Modifier.padding(innerPadding),
                onRetry = onRetry
            )
            uiState.catalog == null || uiState.catalog.items.isEmpty() -> EmptyState(
                message = strings.tourism.emptyResultMessage,
                modifier = Modifier.padding(innerPadding)
            )
            else -> {
                val catalog = uiState.catalog
                // 카테고리 필터 칩은 실제로 2종 이상 섞여 있을 때만 의미가 있다 — 전부 같은
                // categoryCode면(예: RELATED류) 칩을 걸 이유가 없어 섹션 자체를 숨긴다.
                val categoryCodes = remember(catalog.items) {
                    catalog.items.mapNotNull { it.categoryCode }.distinct()
                }
                val revealedCount = rememberRevealedCount(
                    itemsKey = uiState.visibleItems,
                    itemCount = uiState.visibleItems.size
                )
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (uiState.category != TourismCatalogCategory.CROWDING) {
                        item {
                            CatalogSummaryCard(
                                title = catalog.title,
                                description = catalog.description,
                                source = strings.tourism.sourceLabels[catalog.source] ?: catalog.source,
                                itemCount = catalog.items.size
                            )
                        }
                    }
                    item {
                        CatalogSearchBar(
                            query = uiState.searchQuery,
                            onQueryChanged = onSearchQueryChanged
                        )
                    }
                    if (categoryCodes.size > 1) {
                        item {
                            CategoryFilterSection(
                                categoryCodes = categoryCodes,
                                selectedCategoryCode = uiState.selectedCategoryCode,
                                onCategoryFilterSelected = onCategoryFilterSelected
                            )
                        }
                    }
                    if (uiState.category?.supportsDistrict == true) {
                        item {
                            DistrictFilter(
                                selectedDistrict = uiState.selectedDistrict,
                                onDistrictSelected = onDistrictSelected
                            )
                        }
                    }
                    item {
                        ResultCountAndSortRow(
                            resultCount = uiState.visibleItems.size,
                            selectedSort = uiState.selectedSort,
                            onSortSelected = onSortSelected
                        )
                    }
                    if (uiState.visibleItems.isEmpty()) {
                        item {
                            EmptySearchFilterState(onReset = onResetFilters)
                        }
                    } else {
                        // 중복 id가 나올 수 있어(원본 API 응답 그대로 정규화) 인덱스를 함께 key에 섞어
                        // Compose 리스트 key 충돌로 인한 크래시를 막는다.
                        itemsIndexed(
                            items = uiState.visibleItems,
                            key = { index, item -> "${catalog.category.name}-${item.id}-$index" }
                        ) { index, item ->
                            if (uiState.category == TourismCatalogCategory.CROWDING) {
                                CrowdingRankCard(
                                    item = item,
                                    rank = index + 1,
                                    onClick = { onItemSelected(item) },
                                    isRevealAnimated = index < InitialCardRevealCount,
                                    isRevealed = index < revealedCount
                                )
                            } else {
                                TourismDataCard(
                                    item = item,
                                    onClick = { onItemSelected(item) },
                                    isRevealAnimated = index < InitialCardRevealCount,
                                    isRevealed = index < revealedCount
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 무장애 관광(ACCESSIBLE) 전용 리스트업 화면. "부산 관광지"(RecommendedPlacesCatalogContent)와
// 같은 3열→2열 포토 그리드 디자인으로 맞췄다 — 다만 이 카테고리는 관광지/숙박/맛집 같은 하위
// 구분이 없는 단일 카테고리라 카테고리 필터 행은 없고, 개인화 추천(FOR YOU) 섹션도 없다(항상
// visibleItems 하나로만 렌더링). 정렬은 항상 거리순으로 고정하고, 검색은 이미 로드된 카탈로그를
// 타이핑 즉시 클라이언트에서 필터링한다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccessibleTourismCatalogContent(
    uiState: TourismCatalogUiState,
    onSearchQueryChanged: (String) -> Unit,
    onLoadMore: () -> Unit,
    onToggleFavorite: (TourismCatalogItem) -> Unit,
    onItemSelected: (TourismCatalogItem) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    Scaffold(
        containerColor = Color.White,
        topBar = {
            // category.translatedLabel()을 그대로 쓰면 NearbyScreen 슬라이더·허브 화면 등 다른
            // 화면의 "무장애 관광" 표기까지 다 바뀌므로, 이 화면 전용 타이틀 문구를 따로 둔다.
            PlacesGridTopAppBar(title = strings.tourism.accessibleListTitle, onBack = onBack)
        }
    ) { innerPadding ->
        val contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + BottomNavBarHeight
        )
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(contentPadding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage,
                modifier = Modifier.padding(contentPadding),
                onRetry = onRetry
            )
            uiState.catalog == null || uiState.catalog.items.isEmpty() -> EmptyState(
                message = strings.tourism.emptyResultMessage,
                modifier = Modifier.padding(contentPadding)
            )
            else -> {
                // loadGeneration 기준 리빌 — RecommendedPlacesCatalogContent와 같은 이유
                // (append 때마다 catalog/visibleItems가 새 인스턴스가 돼도 스크롤 중엔 재생 안 함).
                val revealedCount = rememberRevealedCount(itemsKey = uiState.loadGeneration, itemCount = uiState.visibleItems.size)
                val gridState = rememberLazyGridState()
                LoadNextTourismGridPageEffect(
                    gridState = gridState,
                    hasNextPage = uiState.hasNextPage,
                    isLoadingMore = uiState.isLoadingMore,
                    onLoadMore = onLoadMore
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.padding(top = contentPadding.calculateTopPadding()).fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 0.dp,
                        end = 20.dp,
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    fullSpanItem {
                        PlacesGridSearchBar(
                            query = uiState.searchQuery,
                            onQueryChanged = onSearchQueryChanged,
                            placeholder = strings.tourism.accessibleCatalogSearchPlaceholder
                        )
                    }
                    fullSpanItem(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)) {
                        AccessibleHeroHeader(
                            label = strings.tourism.accessibleHeroLabel,
                            titleHighlight = strings.tourism.accessibleHeroTitleHighlight,
                            titleSuffix = strings.tourism.accessibleHeroTitleSuffix,
                            subtitle = strings.tourism.accessibleHeroSubtitle
                        )
                    }
                    if (uiState.visibleItems.isEmpty()) {
                        fullSpanItem { EmptySearchFilterState(onReset = { onSearchQueryChanged("") }) }
                    } else {
                        itemsIndexed(
                            items = uiState.visibleItems,
                            key = { _, item -> item.id }
                        ) { index, item ->
                            TourismGridPlaceCard(
                                item = item,
                                isFavorite = uiState.favoriteItemIds.contains(item.id),
                                onToggleFavorite = { onToggleFavorite(item) },
                                onClick = { onItemSelected(item) },
                                isRevealAnimated = index < InitialCardRevealCount,
                                isRevealed = index < revealedCount
                            )
                        }
                        if (uiState.isLoadingMore) {
                            fullSpanItem { TourismPageLoadingIndicator() }
                        }
                    }
                }
            }
        }
    }
}

// "부산 관광지"(언어별 PLACES_KO/EN/JA/ZH) 전용 리스트업 화면. wellness_tourism_recommendation_list.png
// 디자인 기준으로 3열 포토 그리드 + 카테고리 3종(관광지/숙박/맛집) 고정 필터 + 지역 드롭다운으로
// 개편했다. 카테고리 필터는 클라이언트 필터, 지역은 서버 재조회이며, 목록은 "추천" 섹션(개인화
// 점수 상위, FOR YOU 헤더)과 "전체" 섹션(TourAPI 원본 목록에서 추천에 뽑히지 않은 나머지) 두
// 단으로 나뉜다. 정렬 선택지는 없다 — 추천 섹션은 개인화 점수순, 전체 섹션은 서버 원본 순서.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendedPlacesCatalogContent(
    uiState: TourismCatalogUiState,
    onDistrictSelected: (BusanDistrict?) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onCategoryFilterSelected: (String?) -> Unit,
    onResetFilters: () -> Unit,
    onLoadMore: () -> Unit,
    onToggleFavorite: (TourismCatalogItem) -> Unit,
    onItemSelected: (TourismCatalogItem) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    Scaffold(
        containerColor = Color.White,
        topBar = {
            // category.translatedLabel()은 언어별 소스를 가리키는 내부 구분용 라벨이라(EN이면
            // "Busan in English") 화면 제목으로 쓰면 "부산 관광지"의 번역이 아니라 이상하게
            // 보인다 — 이 화면 전용 문구를 따로 둔다(무장애 관광의 accessibleListTitle과 동일 패턴).
            PlacesGridTopAppBar(title = strings.tourism.busanPlacesListTitle, onBack = onBack)
        }
    ) { innerPadding ->
        val contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + BottomNavBarHeight
        )
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(contentPadding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage,
                modifier = Modifier.padding(contentPadding),
                onRetry = onRetry
            )
            uiState.catalog == null || uiState.catalog.items.isEmpty() -> EmptyState(
                message = strings.tourism.emptyResultMessage,
                modifier = Modifier.padding(contentPadding)
            )
            else -> {
                val combinedCount = uiState.recommendedItems.size + uiState.visibleItems.size
                // itemsKey를 loadGeneration으로 고정한다 — catalog는 append(무한 스크롤 다음
                // 페이지)에서도 매번 새 인스턴스(items 병합)라, 그걸 키로 쓰면 스크롤할 때마다
                // 이미 보이던 카드까지 스켈레톤부터 다시 리빌돼 버벅였다. loadGeneration은 진짜
                // 새 조회(초기 진입·재시도·지역 변경)에서만 올라가므로 스크롤 중엔 그대로다.
                val revealedCount = rememberRevealedCount(
                    itemsKey = uiState.loadGeneration,
                    itemCount = combinedCount
                )
                val gridState = rememberLazyGridState()
                LoadNextTourismGridPageEffect(
                    gridState = gridState,
                    hasNextPage = uiState.hasNextPage,
                    isLoadingMore = uiState.isLoadingMore,
                    onLoadMore = onLoadMore
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    modifier = Modifier.padding(top = contentPadding.calculateTopPadding()).fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 0.dp,
                        end = 20.dp,
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    fullSpanItem {
                        PlacesGridSearchBar(
                            query = uiState.searchQuery,
                            onQueryChanged = onSearchQueryChanged,
                            placeholder = strings.tourism.nearHospitalSearchPlaceholder
                        )
                    }
                    // 그리드 자체의 verticalArrangement(10dp)가 아이템 사이마다 이미 들어가므로,
                    // 여기 추가 padding은 그 위에 더 얹히는 값이다 — png보다 여백이 넓어 보여서 줄였다.
                    fullSpanItem(modifier = Modifier.padding(top = 2.dp)) {
                        PlacesCategoryAndDistrictFilterRow(
                            category = uiState.category,
                            selectedCategoryCode = uiState.selectedCategoryCode,
                            onCategoryFilterSelected = onCategoryFilterSelected,
                            selectedDistrict = uiState.selectedDistrict,
                            onDistrictSelected = onDistrictSelected
                        )
                    }
                    // FOR YOU 헤더는 어떤 카테고리 칩을 누르고 있든 항상 보인다 — 특정 카테고리에서
                    // recommendedItems가 비어도(개인화 신호가 없을 때) 자리는 그대로 유지한다.
                    fullSpanItem(modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)) {
                        ForYouRecommendationHeader(
                            forYouLabel = strings.tourism.forYouLabel,
                            titlePrefix = strings.tourism.nearHospitalRecommendationTitlePrefix,
                            titleHighlight = strings.tourism.nearHospitalRecommendationTitleHighlight,
                            subtitle = strings.tourism.nearHospitalRecommendationSubtitle
                        )
                    }
                    if (combinedCount == 0) {
                        fullSpanItem { EmptySearchFilterState(onReset = onResetFilters) }
                    } else {
                        // "부산 관광지 전체" 같은 구분 라벨 없이 추천 다음 전체 목록을 그대로 이어
                        // 붙인다 — 인스타그램 피드처럼 끊김 없는 한 장의 그리드로 보이게 한다.
                        if (uiState.recommendedItems.isNotEmpty()) {
                            itemsIndexed(
                                items = uiState.recommendedItems,
                                key = { _, item -> "reco-${item.id}" }
                            ) { index, item ->
                                TourismGridPlaceCard(
                                    item = item,
                                    isFavorite = uiState.favoriteItemIds.contains(item.id),
                                    onToggleFavorite = { onToggleFavorite(item) },
                                    onClick = { onItemSelected(item) },
                                    isRevealAnimated = index < InitialCardRevealCount,
                                    isRevealed = index < revealedCount
                                )
                            }
                        }
                        if (uiState.visibleItems.isNotEmpty()) {
                            itemsIndexed(
                                items = uiState.visibleItems,
                                key = { _, item -> "all-${item.id}" }
                            ) { index, item ->
                                val globalIndex = uiState.recommendedItems.size + index
                                TourismGridPlaceCard(
                                    item = item,
                                    isFavorite = uiState.favoriteItemIds.contains(item.id),
                                    onToggleFavorite = { onToggleFavorite(item) },
                                    onClick = { onItemSelected(item) },
                                    isRevealAnimated = globalIndex < InitialCardRevealCount,
                                    isRevealed = globalIndex < revealedCount
                                )
                            }
                        }
                        if (uiState.isLoadingMore) {
                            fullSpanItem { TourismPageLoadingIndicator() }
                        }
                    }
                }
            }
        }
    }
}

// LazyVerticalGrid에서 검색바·필터·섹션 헤더처럼 3칸을 다 차지해야 하는 항목을 매번
// span = { GridItemSpan(maxLineSpan) }로 반복 쓰지 않기 위한 축약 확장 함수.
private fun LazyGridScope.fullSpanItem(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Box(modifier = modifier) { content() }
    }
}

// LoadNextTourismPageEffect의 그리드(LazyVerticalGrid) 버전 — RecommendedPlacesCatalogContent
// 3열 카드 그리드 전용.
@Composable
private fun LoadNextTourismGridPageEffect(
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState,
    hasNextPage: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(gridState, hasNextPage, isLoadingMore) {
        if (!hasNextPage || isLoadingMore) return@LaunchedEffect
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            // 백엔드가 이 카테고리를 캐싱 없이 실시간 프록시해서 페이지당 왕복이 느리다 — 사용자가
            // 마지막 줄에 닿기 전에 미리 다음 페이지 요청을 걸어 대기 체감을 줄인다(2열 기준 약 5줄분).
            layoutInfo.totalItemsCount > 0 && lastVisibleIndex >= layoutInfo.totalItemsCount - 10
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }
}

@Composable
private fun TourismPageLoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            color = CoralPrimary,
            strokeWidth = 3.dp
        )
    }
}

// wellness_tourism_recommendation_list.png 필터 행 — 카테고리 3종(관광지/숙박/맛집, TourAPI
// contenttypeid 12/32/39 고정) 알약 버튼 + 지역 드롭다운 1개, 총 4개를 한 줄에 배치한다.
// 카테고리는 재선택 시 해제(전체 보기)되는 단일 선택, 지역은 "전체"를 포함한 전체 목록을 보여준다.
@Composable
private fun PlacesCategoryAndDistrictFilterRow(
    category: TourismCatalogCategory?,
    selectedCategoryCode: String?,
    onCategoryFilterSelected: (String?) -> Unit,
    selectedDistrict: BusanDistrict?,
    onDistrictSelected: (BusanDistrict?) -> Unit
) {
    val strings = LocalAppStrings.current
    // TourAPI는 국문 서비스(PLACES_KO)와 외국어 서비스(PLACES_EN/JA/ZH)가 contenttypeid 체계
    // 자체가 달라서(placeCategoryCodes 참고), "관광지/숙박/맛집" 3개 알약이 실제로 걸어야 하는
    // 코드도 현재 카테고리에 따라 달라진다 — 코드를 하드코딩하면 영어 등에서 필터가 전부
    // 빈 결과로 나온다(실제 라이브 API로 확인한 버그).
    val codes = category?.placeCategoryCodes()
    // 언어별로 라벨 길이가 크게 다르다(한국어는 2~3자, 영어는 "Attractions"처럼 훨씬 길다) —
    // 고정 Row라 한국어 기준으로만 맞춰두면 영어 등에서 알약 4개 폭 합이 화면을 넘어 뒤쪽(지역)
    // 알약이 통째로 잘려 안 보이고, 옆으로 넘기는 인터랙션도 없어서 확인할 방법이 없었다 —
    // 가로 스크롤을 안전망으로 남겨두되, 라벨을 작게 줄여 대부분은 스크롤 없이 다 보이게 한다.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (codes != null) {
            PlacesCategoryPill(
                label = codes.spot.translatedTourismItemCategoryLabel(strings.language).orEmpty(),
                selected = selectedCategoryCode == codes.spot,
                onClick = {
                    onCategoryFilterSelected(if (selectedCategoryCode == codes.spot) null else codes.spot)
                },
                icon = { tint ->
                    // png의 야자수 아이콘을 그대로 크롭해 만든 실루엣 에셋(ic_tourist_spot_palm) —
                    // Material 아이콘으로 대체하지 않고 컬러필터로 선택 상태 색만 입힌다.
                    Image(
                        painter = painterResource(id = R.drawable.ic_tourist_spot_palm),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(tint),
                        modifier = Modifier.size(17.dp)
                    )
                }
            )
            PlacesCategoryPill(
                label = codes.lodging.translatedTourismItemCategoryLabel(strings.language).orEmpty(),
                selected = selectedCategoryCode == codes.lodging,
                onClick = {
                    onCategoryFilterSelected(if (selectedCategoryCode == codes.lodging) null else codes.lodging)
                },
                icon = { tint -> Icon(Icons.Filled.Hotel, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp)) }
            )
            PlacesCategoryPill(
                label = codes.food.translatedTourismItemCategoryLabel(strings.language).orEmpty(),
                selected = selectedCategoryCode == codes.food,
                icon = { tint -> Icon(Icons.Filled.Restaurant, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp)) },
                onClick = {
                    onCategoryFilterSelected(if (selectedCategoryCode == codes.food) null else codes.food)
                }
            )
        }
        PlacesDistrictDropdownPill(selectedDistrict = selectedDistrict, onDistrictSelected = onDistrictSelected)
    }
}

// 선택 시 코랄 배경 채움, 미선택 시 흰 배경 + 옅은 회색 보더. 아이콘+라벨 순서, 코너 14dp로
// 지역 드롭다운 알약과 톤을 맞춘다.
@Composable
private fun PlacesCategoryPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (tint: Color) -> Unit
) {
    val contentColor = if (selected) PlacesAccentPink else TextPrimary
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) PlacesAccentPinkContainer else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) Color.Transparent else DividerColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon(contentColor)
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            maxLines = 1
        )
    }
}

// 나머지 3개 카테고리 알약과 같은 코너·패딩. 보더는 평소엔 다른 알약과 같은 옅은 회색이고,
// 펼쳐졌을 때만(png의 "전체" 팝업이 뜬 스크린샷과 같은 상태) 코랄로 강조한다. 드롭다운 목록
// 너비를 이 알약 실측 너비에 맞춰(anchorWidth) DropdownMenu 기본 콘텐츠-핏 동작을 덮어쓴다 —
// 그렇지 않으면 목록 폭이 알약보다 좁거나 넓게 떠서 어긋나 보인다.
@Composable
private fun PlacesDistrictDropdownPill(
    selectedDistrict: BusanDistrict?,
    onDistrictSelected: (BusanDistrict?) -> Unit
) {
    val strings = LocalAppStrings.current
    val density = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var anchorWidthPx by remember { mutableStateOf(0) }
    Box {
        Row(
            modifier = Modifier
                .onSizeChanged { anchorWidthPx = it.width }
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(
                    width = if (expanded) 1.5.dp else 1.dp,
                    color = if (expanded) PlacesAccentPink else DividerColor,
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable { expanded = true }
                .padding(horizontal = 9.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(17.dp))
            Text(
                text = selectedDistrict?.translatedLabel(strings.language) ?: strings.tourism.focusAll,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        BrandDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            maxHeight = 280.dp,
            modifier = Modifier.width(with(density) { anchorWidthPx.toDp() }),
            // 위쪽 모서리를 각지게 깎아서 바로 위 알약과 하나로 이어지는 느낌을 낸다.
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 14.dp, bottomEnd = 14.dp)
        ) {
            BrandDropdownMenuItem(
                label = strings.tourism.focusAll,
                selected = selectedDistrict == null,
                onClick = { onDistrictSelected(null); expanded = false },
                accentColor = PlacesAccentPink
            )
            BusanDistrict.entries.forEach { district ->
                BrandDropdownMenuItem(
                    label = district.translatedLabel(strings.language),
                    selected = selectedDistrict == district,
                    onClick = { onDistrictSelected(district); expanded = false },
                    accentColor = PlacesAccentPink
                )
            }
        }
    }
}

// 상단 검색바 — wellness_tourism_recommendation_list.png 기준으로 테두리 없는 옅은 회색 배경 +
// 왼쪽 돋보기 아이콘(회색)으로 다른 관광 화면들의 흰 배경+코랄 보더 검색바와 톤을 다르게 맞췄다.
@Composable
private fun PlacesGridSearchBar(query: String, onQueryChanged: (String) -> Unit, placeholder: String) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SearchBarFill)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                // 언어(특히 영어)에 따라 문구 길이가 검색바 폭보다 길어질 수 있어 — 두 줄로
                // 늘어나 바 모양이 깨지는 대신 한 줄로 유지하고 넘치면 말줄임으로 자른다.
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = strings.search.deleteSearchTermContentDescription,
                tint = TextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onQueryChanged("") }
            )
        }
    }
}

private val SearchBarFill = Color(0xFFF2F1F3)

// wellness_tourism_recommendation_list.png의 메인 포인트 핑크 — 관광지 선택 상태, "추천 장소"
// 강조 텍스트, 지역 드롭다운 체크 아이콘 등 이 화면(부산 관광지)의 주요 포인트 전용 색이다.
// 앱 전체에서 쓰는 core/designsystem의 CoralPrimary(#FD6677)와는 다른 값이라 여기서만 따로 둔다.
private val PlacesAccentPink = Color(0xFFFD3569)
private val PlacesAccentPinkContainer = Color(0xFFFFE3EA)

// 아이콘 라벨(FOR YOU/EASY TRIP) + 큰 제목(강조 부분만 코랄, 순서는 호출부가 AnnotatedString으로
// 직접 조립) + 부제. "부산 관광지"·무장애 관광 리스트업 화면이 검색바 밑에서 공용으로 쓴다.
@Composable
private fun PlacesHeroHeader(eyebrowLabel: String, title: AnnotatedString, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = eyebrowLabel, style = MaterialTheme.typography.labelMedium, color = PlacesAccentPink, fontWeight = FontWeight.Bold)
        Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

// "부산 관광지"(FOR YOU) — 검정 접두사 + 코랄 강조 접미사 순서.
@Composable
private fun ForYouRecommendationHeader(forYouLabel: String, titlePrefix: String, titleHighlight: String, subtitle: String) {
    PlacesHeroHeader(
        eyebrowLabel = forYouLabel,
        title = buildAnnotatedString {
            withStyle(SpanStyle(color = TextPrimary)) { append(titlePrefix) }
            withStyle(SpanStyle(color = PlacesAccentPink)) { append(titleHighlight) }
        },
        subtitle = subtitle
    )
}

// 무장애 관광(EASY TRIP) — 코랄 강조 접두사 + 검정 접미사 순서(부산 관광지와 반대).
@Composable
private fun AccessibleHeroHeader(label: String, titleHighlight: String, titleSuffix: String, subtitle: String) {
    PlacesHeroHeader(
        eyebrowLabel = label,
        title = buildAnnotatedString {
            withStyle(SpanStyle(color = PlacesAccentPink)) { append(titleHighlight) }
            withStyle(SpanStyle(color = TextPrimary)) { append(titleSuffix) }
        },
        subtitle = subtitle
    )
}

// wellness_tourism_recommendation_list.png 기준 — 뒤로가기 + 완전히 가운데 정렬된 굵은 검정
// 제목만 있는 가벼운 헤더(동백꽃 장식 없음). RecommendedPlacesCatalogContent 전용.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlacesGridTopAppBar(title: String, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = LocalAppStrings.current.common.backContentDescription,
                    tint = TextPrimary
                )
            }
        },
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    )
}

@Composable
private fun CatalogSummaryCard(title: String, description: String, source: String, itemCount: Int) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(listOf(Color(0xFFFFE7E9), Color(0xFFFFF8F8), Color(0xFFEAF5FF)))
            )
        ) {
            Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(title, style = SectionTitleStyle, color = TextPrimary)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoBadge(source)
                    InfoBadge(String.format(strings.tourism.sourceCountFormat, itemCount))
                }
            }
        }
    }
}

// 최상단 검색바 — feature/hospitalsearchlist의 SearchInputBar와 같은 알약형 룩을 관광 톤(코랄
// 보더)으로 맞췄다. 자동완성/최근검색 패널은 없다 — catalog.items가 이미 전부 로드된 작은
// 목록이라 즉시 클라이언트 필터링만으로 충분하고, 별도 어시스트 패널은 이번 스코프 밖이다.
@Composable
private fun CatalogSearchBar(query: String, onQueryChanged: (String) -> Unit) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShapePercent50)
            .background(Color.White)
            .border(width = 1.dp, color = CoralPrimary.copy(alpha = 0.35f), shape = RoundedCornerShapePercent50)
            .padding(start = 18.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = strings.tourism.catalogSearchPlaceholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = strings.search.deleteSearchTermContentDescription,
                tint = TextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onQueryChanged("") }
            )
        }
    }
}

@Composable
private fun CategoryFilterSection(
    categoryCodes: List<String>,
    selectedCategoryCode: String?,
    onCategoryFilterSelected: (String?) -> Unit
) {
    val strings = LocalAppStrings.current
    val labeledCodes = categoryCodes.mapNotNull { code ->
        code.translatedTourismItemCategoryLabel(strings.language)?.let { code to it }
    }
    if (labeledCodes.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text(strings.tourism.categorySectionTitle, style = SectionTitleStyle, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChipPill(
                    label = strings.tourism.focusAll,
                    selected = selectedCategoryCode == null,
                    onClick = { onCategoryFilterSelected(null) }
                )
            }
            items(labeledCodes, key = { it.first }) { (code, label) ->
                FilterChipPill(
                    label = label,
                    selected = selectedCategoryCode == code,
                    onClick = { onCategoryFilterSelected(code) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DistrictFilter(selectedDistrict: BusanDistrict?, onDistrictSelected: (BusanDistrict) -> Unit) {
    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text(strings.tourism.districtSectionTitle, style = SectionTitleStyle, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(BusanDistrict.entries, key = { it.name }) { district ->
                FilterChip(
                    selected = selectedDistrict == district,
                    onClick = { onDistrictSelected(district) },
                    label = { Text(district.translatedLabel(strings.language)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CoralPrimaryContainer,
                        selectedLabelColor = CoralPrimary,
                        containerColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun ResultCountAndSortRow(
    resultCount: Int,
    selectedSort: TourismSortOption,
    onSortSelected: (TourismSortOption) -> Unit
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(String.format(strings.tourism.resultCountFormat, resultCount), style = SectionTitleStyle, color = TextPrimary)
        TourismSortDropdown(selected = selectedSort, onSortSelected = onSortSelected)
    }
}

@Composable
private fun TourismSortDropdown(selected: TourismSortOption, onSortSelected: (TourismSortOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current.search

    Box {
        Row(
            modifier = Modifier.clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = strings.sortLabel, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        BrandDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BrandDropdownMenuItem(
                label = strings.sortDistance,
                selected = selected == TourismSortOption.DISTANCE,
                onClick = { onSortSelected(TourismSortOption.DISTANCE); expanded = false }
            )
            BrandDropdownMenuItem(
                label = strings.sortName,
                selected = selected == TourismSortOption.NAME,
                onClick = { onSortSelected(TourismSortOption.NAME); expanded = false }
            )
        }
    }
}

@Composable
private fun EmptySearchFilterState(onReset: () -> Unit) {
    val strings = LocalAppStrings.current.search
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(strings.emptyResultsTitle, style = CardTitleStyle, color = TextPrimary)
        Text(strings.emptyResultsSubtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        FilterChipPill(label = strings.resetFiltersButton, selected = false, onClick = onReset)
    }
}

@Composable
private fun TourismDataCard(
    item: TourismCatalogItem,
    onClick: () -> Unit,
    isRevealAnimated: Boolean,
    isRevealed: Boolean
) {
    val distanceLabel = rememberTourismItemDistanceLabel(item)
    val revealProgress = rememberCardRevealProgress(isRevealAnimated, isRevealed)
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = revealProgress
                    translationY = (1f - revealProgress) * 10.dp.toPx()
                }
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TourismCardBody(item = item, distanceLabel = distanceLabel)
            }
        }
        if (isRevealAnimated && revealProgress < 1f) {
            ShimmerSkeleton(alpha = 1f - revealProgress, modifier = Modifier.matchParentSize())
        }
    }
}

@Composable
private fun rememberTourismItemDistanceLabel(item: TourismCatalogItem): String? =
    remember(item.latitude, item.longitude) {
        val lat = item.latitude
        val lng = item.longitude
        if (lat == null || lng == null) {
            null
        } else {
            haversineDistanceMeters(DefaultSearchOrigin.LATITUDE, DefaultSearchOrigin.LONGITUDE, lat, lng).toDistanceLabel()
        }
    }

// TourismDataCard(기존 카테고리)와 AccessibleTourismDataCard(무장애 관광)가 공유하는 카드 내용 —
// 바깥 컨테이너(테두리 있는 Card vs 그림자만 있는 Box)만 다르고 안쪽 레이아웃은 동일하다.
@Composable
private fun TourismCardBody(item: TourismCatalogItem, distanceLabel: String?) {
    val strings = LocalAppStrings.current
    item.imageUrl?.let { imageUrl ->
        AsyncImageBox(
            model = imageUrl,
            contentDescription = item.title,
            modifier = Modifier.fillMaxWidth().height(176.dp)
        )
    }
    Column(
        modifier = Modifier.padding(
            start = 16.dp,
            top = if (item.imageUrl == null) 16.dp else 0.dp,
            end = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Text(item.title, style = CardTitleStyle, color = TextPrimary, modifier = Modifier.weight(1f))
            distanceLabel?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Text(it, style = MaterialTheme.typography.labelMedium, color = CoralPrimary)
            }
        }
        item.subtitle?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
        item.address?.let { address ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.height(16.dp))
                Spacer(Modifier.width(5.dp))
                Text(address, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        // 카테고리 코드·원본 타임스탬프 같은 원본 API 필드를 그대로 보여주지 않도록,
        // TourismStrings.detailFieldLabels에 사람이 읽을 라벨이 있는 필드만 고른다
        // (없는 필드는 raw key로 대체 표시하지 않고 그냥 숨긴다).
        item.details.entries
            .mapNotNull { (key, value) -> strings.tourism.detailFieldLabels[key]?.let { it to value } }
            .take(4)
            .forEach { (label, value) -> DetailRow(label = label, value = value) }
    }
}

// wellness_tourism_recommendation_list.png의 3열 포토 그리드 카드 — 사진이 카드 전체를 채우고
// 하단 그라데이션 위에 흰 글씨로 제목/주소/거리를 얹는다. 우상단 하트는 즐겨찾기 토글.
@Composable
private fun TourismGridPlaceCard(
    item: TourismCatalogItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    isRevealAnimated: Boolean,
    isRevealed: Boolean
) {
    val strings = LocalAppStrings.current
    val distanceLabel = rememberTourismItemDistanceLabel(item)
    val revealProgress = rememberCardRevealProgress(isRevealAnimated, isRevealed)
    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .graphicsLayer {
                alpha = revealProgress
                translationY = (1f - revealProgress) * 10.dp.toPx()
            }
            .clip(cardShape)
            .background(CoralPrimaryContainer)
            .clickable(onClick = onClick)
    ) {
        if (item.imageUrl != null) {
            AsyncImageBox(model = item.imageUrl, contentDescription = item.title, modifier = Modifier.fillMaxSize())
        } else {
            // TourAPI가 사진을 안 내려준 항목 — CoralPrimaryContainer(옅은 핑크) 배경만 남으면
            // 흰 화면 위 그리드에서 "빈 카드처럼" 보여서(가운데만 흰 화면으로 보인다는 문의의
            // 실제 원인 중 하나), 사진이 없다는 걸 분명히 보여주는 아이콘을 얹는다.
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = CoralPrimary.copy(alpha = 0.35f),
                modifier = Modifier.align(Alignment.Center).size(40.dp)
            )
        }
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.72f)))
            )
        )
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = strings.tourism.seeAllLabel,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(20.dp)
                .clickable(onClick = onToggleFavorite)
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = item.title,
                style = CardTitleStyle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            item.address?.let { address ->
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.78f),
                        modifier = Modifier.padding(top = 2.dp).size(12.dp)
                    )
                    Text(
                        text = address,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.78f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            distanceLabel?.let {
                Text(text = it, style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        if (isRevealAnimated && revealProgress < 1f) {
            ShimmerSkeleton(alpha = 1f - revealProgress, modifier = Modifier.matchParentSize())
        }
    }
}

@Composable
private fun CrowdingRankCard(
    item: TourismCatalogItem,
    rank: Int,
    onClick: () -> Unit,
    isRevealAnimated: Boolean,
    isRevealed: Boolean
) {
    val strings = LocalAppStrings.current
    val congestion = item.details["congestionRate"] ?: item.subtitle.orEmpty()
    val revealProgress = rememberCardRevealProgress(isRevealAnimated, isRevealed)
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = revealProgress
                    translationY = (1f - revealProgress) * 10.dp.toPx()
                }
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
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 64.dp, height = 76.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color(0xFFEAF5FF)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.TrendingUp, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(25.dp))
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(7.dp),
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Text(
                            "#$rank",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = CoralPrimary,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(item.title, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        item.details["signguNm"] ?: item.address ?: strings.tourism.catalogDefaultTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    item.details["baseYmd"]?.let {
                        Text(
                            "${strings.tourism.detailFieldLabels["baseYmd"].orEmpty()} $it",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(congestion, style = MaterialTheme.typography.titleMedium, color = CoralPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(
                        strings.tourism.detailFieldLabels["congestionRate"].orEmpty(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
        if (isRevealAnimated && revealProgress < 1f) {
            ShimmerSkeleton(alpha = 1f - revealProgress, modifier = Modifier.matchParentSize())
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = SkyBlue, modifier = Modifier.width(72.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun InfoBadge(text: String) {
    Surface(shape = MaterialTheme.shapes.small, color = Color.White.copy(alpha = 0.86f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = CoralPrimary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    }
}

private val RoundedCornerShapePercent50 = RoundedCornerShape(percent = 50)
