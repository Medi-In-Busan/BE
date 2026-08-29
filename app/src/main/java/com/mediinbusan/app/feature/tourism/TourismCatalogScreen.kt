package com.mediinbusan.app.feature.tourism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
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
import com.mediinbusan.app.core.ui.rememberCountUpValue
import com.mediinbusan.app.core.ui.rememberRevealedCount
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.isLanguageVariant
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.foundation.shape.CircleShape

@Composable
fun TourismCatalogScreen(
    categoryName: String,
    onNavigateToCourse: (category: String, district: String?) -> Unit,
    onSelectItem: () -> Unit,
    onBack: () -> Unit,
    viewModel: TourismCatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(categoryName) { viewModel.load(categoryName) }

    if (uiState.category == TourismCatalogCategory.ACCESSIBLE) {
        // 무장애 관광 리스트업 화면만 병원 목록(S-04)과 비슷한 톤의 전용 헤더·검색 UX를 쓴다 — 다른
        // 관광 카테고리(부산 관광지/걷기코스/함께 둘러보기/혼잡도 등)는 기존 TourismCatalogContent를 그대로 쓴다.
        AccessibleTourismCatalogContent(
            uiState = uiState,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
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
                    title = { Text(uiState.category?.label ?: "관광 데이터") },
                    actions = {
                        val canBuildCourse = uiState.catalog?.items?.count {
                            it.latitude != null && it.longitude != null
                        }?.let { it >= 3 } == true
                        IconButton(onClick = onNavigateToCourse, enabled = canBuildCourse) {
                            Icon(Icons.Default.Map, contentDescription = "추천 장소 동선 보기")
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

// 무장애 관광(ACCESSIBLE) 전용 리스트업 화면. 다른 관광 카테고리와 달리 배너(CatalogSummaryCard),
// 카테고리·지역 필터, 정렬 선택, "추천 동선 보기"가 전부 없다 — 정렬은 항상 거리순
// (TourismCatalogUiState.selectedSort 기본값)으로 고정하고, 검색은 (병원 목록의 디바운스+자동완성+
// 수동 제출과 달리) 이미 로드된 카탈로그를 타이핑 즉시 클라이언트에서 필터링한다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccessibleTourismCatalogContent(
    uiState: TourismCatalogUiState,
    onSearchQueryChanged: (String) -> Unit,
    onItemSelected: (TourismCatalogItem) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    Scaffold(
        containerColor = TourismCanvas,
        topBar = {
            TourismListTopAppBar(
                title = uiState.category?.translatedLabel(strings.language) ?: strings.tourism.catalogDefaultTitle,
                onBack = onBack
            )
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
                // 병원 목록(SearchResultList)과 같은 "시그널 리빌" — 새 목록이 도착할 때마다 앞
                // 6개까지 순차로 스켈레톤→카드 페이드인. core/ui/CardRevealAnimation.kt 참고.
                val revealedCount = rememberRevealedCount(itemsKey = uiState.visibleItems, itemCount = uiState.visibleItems.size)
                LazyColumn(
                    modifier = Modifier.padding(top = contentPadding.calculateTopPadding()).fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 16.dp,
                        end = 20.dp,
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        TourismListSearchBar(
                            query = uiState.searchQuery,
                            onQueryChanged = onSearchQueryChanged,
                            placeholder = strings.tourism.accessibleCatalogSearchPlaceholder
                        )
                    }
                    item { TourismListResultCountLabel(resultCount = uiState.visibleItems.size) }
                    if (uiState.visibleItems.isEmpty()) {
                        item { EmptySearchFilterState(onReset = { onSearchQueryChanged("") }) }
                    } else {
                        itemsIndexed(
                            items = uiState.visibleItems,
                            key = { index, item -> "${uiState.category?.name}-${item.id}-$index" }
                        ) { index, item ->
                            TourismListDataCard(
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

// "부산 관광지"(언어별 PLACES_KO/EN/JA/ZH) 전용 리스트업 화면. 무장애 관광과 같은 헤더·검색바·
// 카드 디자인·리빌 애니메이션을 쓰지만, 카테고리·지역 필터가 칩 대신 드롭다운 2개이고(카테고리는
// 클라이언트 필터, 지역은 서버 재조회), 목록이 "추천" 섹션(개인화 점수 상위)과 "전체" 섹션(TourAPI
// 원본 목록에서 추천에 뽑히지 않은 나머지) 두 단으로 나뉜다. 정렬 선택지는 없다 — 추천 섹션은
// 개인화 점수순, 전체 섹션은 서버가 내려준 원본 순서를 그대로 따른다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendedPlacesCatalogContent(
    uiState: TourismCatalogUiState,
    onDistrictSelected: (BusanDistrict) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onCategoryFilterSelected: (String?) -> Unit,
    onResetFilters: () -> Unit,
    onItemSelected: (TourismCatalogItem) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    Scaffold(
        containerColor = TourismCanvas,
        topBar = {
            TourismListTopAppBar(
                title = uiState.category?.translatedLabel(strings.language) ?: strings.tourism.catalogDefaultTitle,
                onBack = onBack
            )
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
                val catalog = uiState.catalog
                val categoryCodes = remember(catalog.items) {
                    catalog.items.mapNotNull { it.categoryCode }.distinct()
                }
                val combinedCount = uiState.recommendedItems.size + uiState.visibleItems.size
                // 두 섹션을 합친 순서로 리빌 인덱스를 매겨야 "전체" 섹션 카드도 리스트 앞쪽에
                // 있으면 애니메이션 대상(InitialCardRevealCount 이내)이 된다.
                val revealedCount = rememberRevealedCount(
                    itemsKey = uiState.recommendedItems to uiState.visibleItems,
                    itemCount = combinedCount
                )
                LazyColumn(
                    modifier = Modifier.padding(top = contentPadding.calculateTopPadding()).fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 16.dp,
                        end = 20.dp,
                        bottom = contentPadding.calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        TourismListSearchBar(
                            query = uiState.searchQuery,
                            onQueryChanged = onSearchQueryChanged,
                            placeholder = strings.tourism.catalogSearchPlaceholder
                        )
                    }
                    item {
                        PlacesFilterDropdownRow(
                            categoryCodes = categoryCodes,
                            selectedCategoryCode = uiState.selectedCategoryCode,
                            onCategoryFilterSelected = onCategoryFilterSelected,
                            selectedDistrict = uiState.selectedDistrict,
                            onDistrictSelected = onDistrictSelected
                        )
                    }
                    item { TourismListResultCountLabel(resultCount = combinedCount) }
                    if (combinedCount == 0) {
                        item { EmptySearchFilterState(onReset = onResetFilters) }
                    } else {
                        if (uiState.recommendedItems.isNotEmpty()) {
                            item { TourismListSectionHeader(strings.tourism.recommendedPlacesSectionTitle) }
                            itemsIndexed(
                                items = uiState.recommendedItems,
                                key = { index, item -> "reco-${item.id}-$index" }
                            ) { index, item ->
                                TourismListDataCard(
                                    item = item,
                                    onClick = { onItemSelected(item) },
                                    isRevealAnimated = index < InitialCardRevealCount,
                                    isRevealed = index < revealedCount
                                )
                            }
                        }
                        if (uiState.visibleItems.isNotEmpty()) {
                            item { TourismListSectionHeader(strings.tourism.allPlacesSectionTitle) }
                            itemsIndexed(
                                items = uiState.visibleItems,
                                key = { index, item -> "all-${item.id}-$index" }
                            ) { index, item ->
                                val globalIndex = uiState.recommendedItems.size + index
                                TourismListDataCard(
                                    item = item,
                                    onClick = { onItemSelected(item) },
                                    isRevealAnimated = globalIndex < InitialCardRevealCount,
                                    isRevealed = globalIndex < revealedCount
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TourismListSectionHeader(title: String) {
    Text(text = title, style = SectionTitleStyle, color = TextPrimary, fontWeight = FontWeight.Bold)
}

// 기존 LazyRow 칩 2줄(카테고리·지역) 대신 나란히 배치한 드롭다운 2개. 지역은 항상 하나가 선택돼
// 있어야 하고(서버 재조회 트리거) "전체" 옵션이 없다 — 카테고리만 "전체"를 지원한다.
@Composable
private fun PlacesFilterDropdownRow(
    categoryCodes: List<String>,
    selectedCategoryCode: String?,
    onCategoryFilterSelected: (String?) -> Unit,
    selectedDistrict: BusanDistrict?,
    onDistrictSelected: (BusanDistrict) -> Unit
) {
    val strings = LocalAppStrings.current
    // fillMaxWidth를 안 줘서 한 줄을 다 채우지 않고 내용 크기만큼만 왼쪽부터 나란히 놓인다.
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TourismFilterDropdown(
            label = strings.tourism.categorySectionTitle,
            selectedLabel = selectedCategoryCode?.translatedTourismItemCategoryLabel(strings.language) ?: strings.tourism.focusAll
        ) { collapse ->
            BrandDropdownMenuItem(
                label = strings.tourism.focusAll,
                selected = selectedCategoryCode == null,
                onClick = { onCategoryFilterSelected(null); collapse() }
            )
            categoryCodes.mapNotNull { code -> code.translatedTourismItemCategoryLabel(strings.language)?.let { code to it } }
                .forEach { (code, label) ->
                    BrandDropdownMenuItem(
                        label = label,
                        selected = selectedCategoryCode == code,
                        onClick = { onCategoryFilterSelected(code); collapse() }
                    )
                }
        }
        TourismFilterDropdown(
            label = strings.tourism.districtSectionTitle,
            selectedLabel = selectedDistrict?.translatedLabel(strings.language) ?: strings.tourism.districtSectionTitle
        ) { collapse ->
            BusanDistrict.entries.forEach { district ->
                BrandDropdownMenuItem(
                    label = district.translatedLabel(strings.language),
                    selected = selectedDistrict == district,
                    onClick = { onDistrictSelected(district); collapse() }
                )
            }
        }
    }
}

// 알약형 검색바와 같은 흰 배경+코랄 보더 톤으로 맞춘 셀렉트 박스. 라벨을 위에 작게, 현재 선택값과
// 드롭다운 화살표를 아래 박스에 둔다. fillMaxWidth를 안 줘서 내용 크기만큼만 차지한다(한 줄을
// 다 채우는 큰 박스가 아니라 작은 칩형 선택 버튼). content는 BrandDropdownMenu 안에 그릴 항목들 —
// 항목 클릭 시 collapse()를 불러 메뉴를 닫는다(BrandDropdownMenuItem의 onClick 안에서 호출).
@Composable
private fun TourismFilterDropdown(
    label: String,
    selectedLabel: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(collapse: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            Row(
                modifier = Modifier
                    .height(38.dp)
                    .shadow(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = CoralPrimary.copy(alpha = 0.14f),
                        spotColor = CoralPrimary.copy(alpha = 0.14f)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(width = 1.dp, color = CoralPrimary.copy(alpha = 0.35f), shape = RoundedCornerShape(12.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 110.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = CoralPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            BrandDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                content { expanded = false }
            }
        }
    }
}

// 설정/언어 아이콘이 있는 병원 목록용 BrandTopAppBar 대신, 뒤로가기 버튼 + 카테고리명만 있는
// 가벼운 전용 헤더. 로고·설정·언어 전환은 이 화면들의 목적(장소 탐색)과 무관해서 뺐다.
// CenterAlignedTopAppBar 대신 일반 TopAppBar를 써서 뒤로가기 버튼 옆에 왼쪽 정렬 —
// 가이드 STEP 상세 헤더(GuideStepDetailScreen)와 같은 패턴이라 앱 전체 톤과도 맞는다.
// 무장애 관광·부산 관광지 리스트업 화면이 함께 쓴다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TourismListTopAppBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        // 본문 배경(TourismCanvas)과 같은 색으로 맞춰서 헤더-본문 경계가 안 보이게 한다.
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TourismCanvas),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = LocalAppStrings.current.common.backContentDescription,
                    tint = CoralPrimary
                )
            }
        },
        title = {
            // 글자 뒤(오른쪽)에 동백꽃 — 글자 크기는 titleMedium 유지.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Image(
                    painter = painterResource(id = R.drawable.guide_camellia_flower_soft),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    )
}

// 병원 목록의 SearchInputBar와 같은 알약형 룩·치수를 쓰되, 돋보기 아이콘을 오른쪽으로 옮긴다.
// 여기서는 이미 로드된 목록을 타이핑 즉시 클라이언트에서 필터링하므로(TourismCatalogViewModel.
// applyClientFilters) 오른쪽 돋보기는 병원 목록처럼 "제출" 액션이 아니라 순수 표시 아이콘이다.
// 무장애 관광·부산 관광지 리스트업 화면이 함께 쓴다(플레이스홀더 문구만 화면마다 다르게 넘긴다).
@Composable
private fun TourismListSearchBar(query: String, onQueryChanged: (String) -> Unit, placeholder: String) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShapePercent50,
                ambientColor = CoralPrimary.copy(alpha = 0.14f),
                spotColor = CoralPrimary.copy(alpha = 0.14f)
            )
            .clip(RoundedCornerShapePercent50)
            .background(Color.White)
            .border(width = 1.dp, color = CoralPrimary.copy(alpha = 0.35f), shape = RoundedCornerShapePercent50)
            .padding(start = 20.dp, end = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
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
            Spacer(modifier = Modifier.width(10.dp))
        }
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = strings.common.searchContentDescription,
            tint = CoralPrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

// 병원 목록의 SearchResultCountLabel과 같은 톤 — 라벨은 굵은 검정, 건수만 브랜드 코랄로 강조하고
// 카운트업 애니메이션을 준다. 무장애 관광·부산 관광지 리스트업 화면이 함께 쓴다(둘 다 정렬 선택지 없음).
@Composable
private fun TourismListResultCountLabel(resultCount: Int) {
    val strings = LocalAppStrings.current.tourism
    val animatedCount = rememberCountUpValue(resultCount)
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = CoralPrimary, fontWeight = FontWeight.Bold)) {
            append(animatedCount.toString())
        }
        withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold)) {
            append(strings.resultCountUnitLabel)
        }
    }
    Text(text = text, style = SectionTitleStyle)
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
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = SkyBlue, modifier = Modifier.height(16.dp))
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

// 무장애 관광·부산 관광지 리스트업 화면 전용 카드 — 테두리 대신 코랄 톤 그림자로 배경과 분리하고,
// 병원 목록과 같은 시그널 리빌(등장 페이드+슬라이드업, 등장 전 ShimmerSkeleton)을 적용한다.
@Composable
private fun TourismListDataCard(
    item: TourismCatalogItem,
    onClick: () -> Unit,
    isRevealAnimated: Boolean,
    isRevealed: Boolean
) {
    val distanceLabel = rememberTourismItemDistanceLabel(item)
    val revealProgress = rememberCardRevealProgress(isRevealAnimated, isRevealed)
    val cardShape = MaterialTheme.shapes.large

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = revealProgress
                    translationY = (1f - revealProgress) * 10.dp.toPx()
                }
                .shadow(
                    elevation = 6.dp,
                    shape = cardShape,
                    ambientColor = CoralPrimary.copy(alpha = 0.22f),
                    spotColor = CoralPrimary.copy(alpha = 0.22f)
                )
                .clip(cardShape)
                .background(Color.White)
                .clickable(onClick = onClick),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TourismCardBody(item = item, distanceLabel = distanceLabel)
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
                        item.details["signguNm"] ?: item.address ?: "부산 관광지",
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
