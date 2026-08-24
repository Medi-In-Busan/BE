package com.mediinbusan.app.feature.hospitalsearchlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.haversineDistanceMeters
import com.mediinbusan.app.core.common.toDistanceLabel
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.SearchStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BrandTopAppBar
import com.mediinbusan.app.core.ui.BrandDropdownMenu
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.common.resolveHospitalThumbnailRes
import com.mediinbusan.app.core.ui.FilterChipPill
import com.mediinbusan.app.core.ui.InitialCardRevealCount
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.ShimmerSkeleton
import com.mediinbusan.app.core.ui.rememberCardRevealProgress
import com.mediinbusan.app.core.ui.rememberCountUpValue
import com.mediinbusan.app.core.ui.rememberRevealedCount
import com.mediinbusan.app.core.ui.toLanguageBadgeLabel
import com.mediinbusan.app.core.ui.LanguageBadge
import com.mediinbusan.app.data.hospital.Hospital

@Composable
fun HospitalSearchListScreen(
    onSelectHospital: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HospitalSearchListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    HospitalSearchListContent(
        uiState = uiState,
        onNavigateToSettings = onNavigateToSettings,
        onLanguageSelected = viewModel::onLanguageSelected,
        onQueryChanged = viewModel::onQueryChanged,
        onSearchSubmit = viewModel::onSearchSubmit,
        onSuggestionSelected = viewModel::onSuggestionSelected,
        onRecentSearchSelected = viewModel::onRecentSearchSelected,
        onRecentSearchDeleted = viewModel::onRecentSearchDeleted,
        onFilterToggled = viewModel::onFilterToggled,
        onSortSelected = viewModel::onSortSelected,
        onLoadMore = viewModel::onLoadMore,
        onResetSearchConditions = viewModel::onResetSearchConditions,
        onSelectHospital = onSelectHospital,
        onRetry = viewModel::onRetry,
        onAutoFocusApplied = viewModel::onAutoFocusApplied
    )
}

@Composable
private fun HospitalSearchListContent(
    uiState: HospitalSearchListUiState,
    onNavigateToSettings: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onSuggestionSelected: (String) -> Unit,
    onRecentSearchSelected: (String) -> Unit,
    onRecentSearchDeleted: (String) -> Unit,
    onFilterToggled: (String) -> Unit,
    onSortSelected: (SearchSortOption) -> Unit,
    onLoadMore: () -> Unit,
    onResetSearchConditions: () -> Unit,
    onSelectHospital: (String) -> Unit,
    onRetry: () -> Unit,
    onAutoFocusApplied: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    // uiState.shouldAutoFocusSearch는 ViewModel.initialize()가 채워주기 전(최초 컴포지션 시점)엔
    // 항상 false다 — PendingHospitalSearchEntry.consumeFocusRequest()가 LaunchedEffect(Unit) 안에서
    // 비동기로 실행되기 때문. 그래서 remember 초기값으로 한 번만 캡처하면(과거엔 Route 인자라
    // 최초 컴포지션 시점에 이미 정확한 값이었지만, 이제는 아니다) 항상 false로 굳어버린다 —
    // 아래 LaunchedEffect로 값이 true로 바뀌는 시점을 반응형으로 잡아야 한다.
    var isSearchFocused by remember { mutableStateOf(false) }
    // 콘텐츠 유무가 아니라 포커스 여부만으로 패널을 띄운다. 자동완성 후보 개수는 한글 조합
    // 중간 단계(예: "부산" 입력 중 "부"+미완성 글자)에서 순간적으로 0건이 될 수 있는데,
    // 콘텐츠 유무로 판단하면 그 찰나에 패널이 사라지고 뒤의 결과 리스트가 노출돼버린다.
    val showAssistPanel = isSearchFocused
    val searchFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    // Home 검색바를 탭해 들어온 경우, 진짜로 검색창을 탭한 것처럼 키보드까지 바로 띄운다.
    // 진입 직후엔 uiState.isLoading이 true라 SearchInputBar 자체가 아직 화면에 없는 상태다 —
    // 그때 requestFocus()를 부르면 타깃이 없어 허공에 날아가고, 잠시 뒤 로딩이 끝나 텍스트필드가
    // 처음 마운트되면서 "포커스 없음" 초기 상태를 onFocusChanged(false)로 보고해 위에서 세팅해둔
    // isSearchFocused=true를 덮어써버린다(그래서 패널이 잠깐 보였다가 바로 결과 목록으로 바뀌었다).
    // isLoading이 false로 바뀐 시점(=SearchInputBar가 실제로 존재하는 시점)에 걸어야 하고, 이후
    // 재검색 등으로 isLoading이 다시 토글돼도 매번 포커스를 뺏지 않도록 최초 1회만 실행한다.
    //
    // 그런데 이 요청을 아무리 빨리 걸어도, 텍스트필드가 "처음 화면에 붙는" 바로 그 프레임에
    // 안드로이드가 "아직 포커스 없음"이라는 초기 상태를 onFocusChanged(false)로 합성해서 먼저
    // 보고한다 — 이게 위에서 세팅해둔 isSearchFocused=true를 한 프레임 덮어써서 결과 목록이
    // 찰나에 보였다가 그다음 프레임(실제 requestFocus 성공 시점)에 다시 패널로 돌아오는
    // "깜빡임"의 원인이었다. shouldAutoFocusSearch가 대기 중(hasAppliedAutoFocus==false)인 동안 오는
    // onFocusChanged 이벤트는 전부 이 합성 이벤트이므로 무시하고, 우리가 실제로 포커스를 건
    // 이후(hasAppliedAutoFocus==true)부터만 사용자의 진짜 포커스 변화를 반영한다.
    //
    // 포커스를 적용한 직후 onAutoFocusApplied()로 ViewModel의 uiState.shouldAutoFocusSearch도 바로
    // false로 꺼야 한다 — 이 hasAppliedAutoFocus는 로컬 remember라 병원 상세로 넘어갔다 뒤로가기로
    // 이 화면이 다시 조립되면 초기화되는데, ViewModel 쪽 shouldAutoFocusSearch를 안 꺼두면 그때마다
    // "포커스 요청이 있다"고 다시 착각해서 검색 입력 패널(최근 검색어)이 또 열리고 키보드도 다시 뜬다.
    var hasAppliedAutoFocus by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.shouldAutoFocusSearch, uiState.isLoading) {
        if (uiState.shouldAutoFocusSearch && !uiState.isLoading && !hasAppliedAutoFocus) {
            hasAppliedAutoFocus = true
            isSearchFocused = true
            withFrameNanos {}
            searchFocusRequester.requestFocus()
            keyboardController?.show()
            onAutoFocusApplied()
        }
    }

    // 결과 리스트를 아래로 스크롤하면 필터칩+정렬 줄을 접어 리스트가 보이는 영역을 넓혀주고,
    // 위로 스크롤하거나(또는 맨 위 근처면) 다시 펼친다. listState는 SearchResultList의
    // LazyColumn과 공유해야 스크롤 방향을 감지할 수 있어 여기서 끌어올린다.
    val resultListState = rememberLazyListState()
    var isFilterBarCollapsed by remember { mutableStateOf(false) }
    LaunchedEffect(resultListState) {
        var previousIndex = resultListState.firstVisibleItemIndex
        var previousOffset = resultListState.firstVisibleItemScrollOffset
        snapshotFlow { resultListState.firstVisibleItemIndex to resultListState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val scrollDelta = (index - previousIndex) * 1000 + (offset - previousOffset)
                when {
                    index == 0 && offset <= 4 -> isFilterBarCollapsed = false
                    scrollDelta > 6 -> isFilterBarCollapsed = true
                    scrollDelta < -6 -> isFilterBarCollapsed = false
                }
                previousIndex = index
                previousOffset = offset
            }
    }

    Scaffold(
        // Home과 같은 맨 뒤 배경(연분홍) — 기본값(테마 background)이 Home과 달라 화면 전환 시
        // 배경색이 순간 바뀌어 보이던 것을 통일한다.
        containerColor = HomeBackgroundPink,
        topBar = {
            BrandTopAppBar(
                onSettingsClick = onNavigateToSettings,
                currentLanguageCode = uiState.selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }
    ) { innerPadding ->
        // Home에서 검증한 패턴과 동일: 바깥 Column에 bottom padding을 걸어 레이아웃 영역 자체를
        // 줄이는 대신(그러면 그 안의 LazyColumn이 스크롤해도 바텀바 뒤엔 절대 그려질 수 없다),
        // top padding만 유지하고 실제 바텀바 회피는 아래 SearchResultList의 LazyColumn이 자기
        // contentPadding/트레일링 아이템으로 직접 처리한다.
        val contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + BottomNavBarHeight
        )
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(contentPadding))
            uiState.isError -> ErrorState(
                message = uiState.errorMessage ?: LocalAppStrings.current.search.loadErrorFallback,
                modifier = Modifier.padding(contentPadding),
                onRetry = onRetry
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = contentPadding.calculateTopPadding())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SearchInputBar(
                    query = uiState.query,
                    onQueryChanged = onQueryChanged,
                    onSearchSubmit = { focusManager.clearFocus(); onSearchSubmit() },
                    onFocusChanged = { focused ->
                        // shouldAutoFocusSearch 대기 중에 오는 이벤트는 텍스트필드가 처음 붙을 때
                        // 항상 한 번 오는 합성 초기값(false)이라 무시한다 — 위 LaunchedEffect 주석 참고.
                        if (hasAppliedAutoFocus || !uiState.shouldAutoFocusSearch) {
                            isSearchFocused = focused
                        }
                    },
                    modifier = Modifier.padding(horizontal = 20.dp),
                    focusRequester = searchFocusRequester
                )

                if (showAssistPanel) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SearchAssistPanel(
                        query = uiState.query,
                        recentSearches = uiState.recentSearches,
                        autocompleteSuggestions = uiState.autocompleteSuggestions,
                        onSuggestionSelected = { focusManager.clearFocus(); onSuggestionSelected(it) },
                        onRecentSearchSelected = { focusManager.clearFocus(); onRecentSearchSelected(it) },
                        onRecentSearchDeleted = onRecentSearchDeleted,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                    // 리스트를 아래로 스크롤하면 접히고, 위로 스크롤하거나 맨 위 근처면 다시 펼쳐진다.
                    AnimatedVisibility(
                        visible = !isFilterBarCollapsed,
                        enter = expandVertically(animationSpec = tween(durationMillis = 420)) +
                            fadeIn(animationSpec = tween(durationMillis = 340, delayMillis = 50)),
                        exit = shrinkVertically(animationSpec = tween(durationMillis = 380)) +
                            fadeOut(animationSpec = tween(durationMillis = 280))
                    ) {
                        Column {
                            FilterChipsRow(filters = uiState.filters, onFilterToggled = onFilterToggled)

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SearchResultCountLabel(query = uiState.query, count = uiState.results.size)
                                SortDropdownButton(selected = uiState.selectedSort, onSortSelected = onSortSelected)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    // 맨 뒤 배경(Home과 같은 HomeBackgroundPink)은 그대로 비쳐 보이게 두고, 흰색+그림자는
                    // 개별 결과 카드(SearchResultCard)에만 준다 — Home의 SectionCardContainer 같은
                    // 큰 흰 카드로 이 영역 전체를 감싸지 않는다.
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.results.isEmpty()) {
                            EmptySearchBanner(
                                onReset = onResetSearchConditions,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        } else {
                            SearchResultList(
                                listState = resultListState,
                                results = uiState.results,
                                hasReachedEnd = uiState.hasReachedEnd,
                                selectedSort = uiState.selectedSort,
                                onLoadMore = onLoadMore,
                                onSelectHospital = onSelectHospital,
                                bottomContentPadding = contentPadding.calculateBottomPadding()
                            )
                        }
                    }
                }
            }
        }
    }
}

// 검색어가 있으면 "'피부과' 검색결과"까지 굵은 검정으로, 건수는 브랜드 코랄 컬러로 강조한다.
// 건수는 0에서 실제 값까지 카운트업 애니메이션으로 올라간다 — count 자체가 바뀔 때만(새 검색/필터
// 결과가 도착했을 때만) 재생되고, 정렬만 바꿔 순서만 바뀌는 경우(count 불변)는 재생되지 않는다.
@Composable
private fun SearchResultCountLabel(query: String, count: Int) {
    val strings = LocalAppStrings.current.search
    val animatedCount = rememberCountUpValue(count)
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = SettingsPrimaryText, fontWeight = FontWeight.Bold)) {
            if (query.isNotBlank()) {
                append(strings.resultCountWithQueryFormat.format(query))
            } else {
                append(strings.resultCountGenericLabel)
            }
        }
        withStyle(SpanStyle(color = CoralPrimary, fontWeight = FontWeight.Bold)) {
            append(strings.resultCountSuffixFormat.format(animatedCount))
        }
    }
    Text(text = text, style = SettingsDescriptionStyle)
}

// Home의 알약형 검색바(SearchBar)와 같은 룩을 쓰되, 여기서는 실제 입력 가능한 텍스트필드다.
// 타이핑 중에는 재조회하지 않고, 돋보기 아이콘 클릭 또는 키보드 검색 액션에서만 onSearchSubmit이 불린다.
@Composable
private fun SearchInputBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(width = 1.dp, color = CoralPrimary.copy(alpha = 0.35f), shape = RoundedCornerShape(percent = 50))
            .padding(start = 20.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = LocalAppStrings.current.common.searchPlaceholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { onFocusChanged(it.isFocused) }
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clickable(onClick = onSearchSubmit),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = LocalAppStrings.current.common.searchContentDescription, tint = CoralPrimary)
        }
    }
}

// 검색창 포커스 중에만 결과 리스트 자리를 대신 채운다. 입력이 비어있으면 최근 검색어,
// 입력 중이면 자동완성 후보 — 둘 중 하나만 보여준다(showAssistPanel이 이미 상위에서 분기).
@Composable
private fun SearchAssistPanel(
    query: String,
    recentSearches: List<String>,
    autocompleteSuggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    onRecentSearchSelected: (String) -> Unit,
    onRecentSearchDeleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.search
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp)
    ) {
        if (query.isBlank()) {
            if (recentSearches.isEmpty()) {
                item { SearchAssistEmptyLabel(text = strings.recentSearchesEmpty) }
            } else {
                item {
                    Text(
                        text = strings.recentSearchesTitle,
                        style = SettingsDescriptionStyle,
                        color = SettingsSecondaryText,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(recentSearches, key = { it }) { keyword ->
                    RecentSearchRow(
                        keyword = keyword,
                        onClick = { onRecentSearchSelected(keyword) },
                        onDelete = { onRecentSearchDeleted(keyword) }
                    )
                }
            }
        } else {
            if (autocompleteSuggestions.isEmpty()) {
                item { SearchAssistEmptyLabel(text = strings.noMatchingHospitals) }
            } else {
                items(autocompleteSuggestions, key = { it }) { suggestion ->
                    AutocompleteSuggestionRow(name = suggestion, onClick = { onSuggestionSelected(suggestion) })
                }
            }
        }
    }
}

// 조합 중인 한글 입력 등으로 후보가 잠깐 0건이 되는 순간에도 패널 자체는 계속 떠 있고
// 이 빈 상태만 보이게 해서, 뒤에 깔린 결과 리스트가 새어나오지 않게 한다.
@Composable
private fun SearchAssistEmptyLabel(text: String) {
    Text(
        text = text,
        style = SettingsDescriptionStyle,
        color = SettingsSecondaryText,
        modifier = Modifier.padding(vertical = 20.dp)
    )
}

@Composable
private fun RecentSearchRow(keyword: String, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Outlined.History, contentDescription = null, tint = SettingsSecondaryText, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = keyword,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = LocalAppStrings.current.search.deleteSearchTermContentDescription,
                tint = SettingsSecondaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AutocompleteSuggestionRow(name: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = SettingsSecondaryText, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// 전체 의료 태그 + 관광지를 한 줄에 모두 배치하고 가로로 슬라이드해서 볼 수 있게 한다.
// 칩의 selected 상태 토글/서버 specialties 파라미터는 전부 chip.label(한국어) 값을 키로 쓰고
// 있어 그대로 두고(HospitalSearchListViewModel.kt 참고), 화면에 보여줄 때만 언어별 문구로 바꾼다.
private fun displayLabelForFilterChip(chipLabel: String, language: SupportedLanguage, strings: SearchStrings): String =
    MedicalCategory.entries.find { it.label == chipLabel }?.translatedLabel(language)
        ?: strings.tourismFilterLabel

private fun displayLabelForSortOption(option: SearchSortOption, strings: SearchStrings): String = when (option) {
    SearchSortOption.NAME -> strings.sortName
    SearchSortOption.DISTANCE -> strings.sortDistance
}

@Composable
private fun FilterChipsRow(filters: List<SearchFilterChip>, onFilterToggled: (String) -> Unit) {
    val appStrings = LocalAppStrings.current
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters, key = { it.label }) { chip ->
            FilterChipPill(
                label = displayLabelForFilterChip(chip.label, appStrings.language, appStrings.search),
                selected = chip.selected,
                onClick = { onFilterToggled(chip.label) }
            )
        }
    }
}

// 트리거는 원래대로 회색 "정렬 ▾" 텍스트 그대로 두고, 펼쳤을 때 옵션 목록만 탑바 언어
// 드롭다운(BrandTopAppBar의 BrandLanguageDropdown)과 같은 톤으로 바꾼다 — 항목 전체가 아니라
// 텍스트 크기만큼만 알약(pill) 모양으로 잡히고, 선택된 옵션은 코랄 배경+굵은 코랄 텍스트+체크
// 아이콘으로 활성화 표시한다.
@Composable
private fun SortDropdownButton(selected: SearchSortOption, onSortSelected: (SearchSortOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current.search

    Box {
        Row(
            modifier = Modifier.clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = strings.sortLabel, style = MaterialTheme.typography.labelMedium, color = SettingsSecondaryText)
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = SettingsSecondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
        BrandDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Column(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SearchSortOption.entries.forEach { option ->
                    val isSelected = option == selected
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable {
                                onSortSelected(option)
                                expanded = false
                            }
                            .background(if (isSelected) CoralPrimaryContainer else Color.Transparent)
                            .padding(horizontal = 15.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = displayLabelForSortOption(option, strings),
                            style = SettingsItemTitleStyle,
                            color = if (isSelected) CoralPrimary else SettingsPrimaryText,
                            fontWeight = if (isSelected) FontWeight.Bold else null
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = CoralPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultList(
    listState: LazyListState,
    results: List<Hospital>,
    hasReachedEnd: Boolean,
    selectedSort: SearchSortOption,
    onLoadMore: () -> Unit,
    onSelectHospital: (String) -> Unit,
    bottomContentPadding: Dp
) {
    // 정렬을 바꿨을 때 결과는 바로 재배열되지만, 이미 아래로 스크롤한 상태면 화면엔 안 보여서
    // 위로 직접 스크롤해야 하는 불편함이 있었다 — 애니메이션 없이 즉시 맨 위로 리프레시한다.
    LaunchedEffect(selectedSort) {
        listState.scrollToItem(0)
    }

    // results가 새 리스트로 바뀔 때마다(새 검색/필터 결과 도착) 0부터 다시 순차 공개한다.
    val revealedCount = rememberRevealedCount(itemsKey = results, itemCount = results.size)

    // 무한스크롤 UI 훅: 실제 페이지네이션은 백엔드 연동 후 onLoadMore 내부에서 채운다.
    LaunchedEffect(listState, hasReachedEnd) {
        if (hasReachedEnd) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= results.lastIndex - 2) {
                    onLoadMore()
                }
            }
    }

    // LazyColumn은 Column+verticalScroll과 달리 자체 contentPadding 파라미터가 있다. bottom을
    // 0dp로 두고(레이아웃 영역 자체는 안 줄임) 대신 맨 마지막 item으로 바텀바 회피용 Spacer를
    // 넣는다 — 스크롤 중엔 카드가 바텀바 뒤로 실제로 지나가고, 끝까지 내리면 이 Spacer가 자리를
    // 벌려줘서 마지막 카드는 여전히 바텀바에 가려 클릭 안 되는 일이 없다.
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 0.dp)
    ) {
        itemsIndexed(results, key = { _, hospital -> hospital.id }) { index, hospital ->
            SearchResultCard(
                hospital = hospital,
                onClick = { onSelectHospital(hospital.id) },
                isRevealAnimated = index < InitialCardRevealCount,
                isRevealed = index < revealedCount
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        item {
            Spacer(modifier = Modifier.height(bottomContentPadding))
        }
    }
}

@Composable
private fun SearchResultCard(
    hospital: Hospital,
    onClick: () -> Unit,
    isRevealAnimated: Boolean,
    isRevealed: Boolean
) {
    val revealProgress = rememberCardRevealProgress(isRevealAnimated, isRevealed)

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // 등장 전엔 살짝 아래에서 흐릿하게(투명도 0) 시작해 위로 올라오며 선명해진다.
                .graphicsLayer {
                    alpha = revealProgress
                    translationY = (1f - revealProgress) * 10.dp.toPx()
                }
                // 옅은 분홍(HomeBackgroundPink) 배경 위라 기존 0.04 알파 그림자(다른 화면의 흰 배경
                // 기준)로는 거의 안 보였다 — 카드가 배경과 확실히 분리돼 보이도록 더 진하게 준다.
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onClick)
                // 좌우 여백(14dp)만 카드 자체에 준다 — 상하 여백은 사진/텍스트가 각자 따로 갖는다(아래 참고).
                .padding(horizontal = 14.dp)
        ) {
        // 즐겨찾기 버튼을 빼서(상세 화면에 이미 있어 중복) 생긴 여유를 사진 쪽으로 좀 더 준다.
        // 사진 위아래 여백을 좌우 여백(14dp)과 같은 값으로 맞춰서, 기존 20dp 여백일 때보다
        // 위아래로 6dp씩(총 12dp) 더 늘어나 보이게 한다 — 카드 전체 크기·텍스트 위치는 그대로.
        // 실제 사진(imageUrl)이 없으면 의료태그/이름 기반으로 고른 진료과 사진으로 채운다
        // (ETC 태그 최우선 → 이름 키워드 → 태그, resolveHospitalThumbnailRes 참고).
        if (hospital.imageUrl != null) {
            AsyncImageBox(
                model = hospital.imageUrl,
                contentDescription = hospital.name,
                modifier = Modifier
                    .padding(vertical = 14.dp)
                    .width(96.dp)
                    .height(112.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            Image(
                painter = painterResource(
                    id = resolveHospitalThumbnailRes(hospital.name, hospital.specialties)
                ),
                contentDescription = hospital.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(vertical = 14.dp)
                    .width(96.dp)
                    .height(112.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f).padding(vertical = 20.dp)) {
            // 의료태그를 타이틀 자리로, 병원명을 태그 자리로 — 서로 위치를 바꿨다.
            Text(
                text = hospital.specialties.joinToString(", "),
                style = SettingsDescriptionStyle,
                color = SettingsSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = hospital.name,
                style = SettingsItemTitleStyle,
                color = SettingsPrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // 병원 상세(HospitalDetailScreen)와 같은 위치 아이콘 — 텍스트 크기는 타이틀(14sp)과
            // 태그(12sp) 중간인 13sp. 주소가 길면 말줄임(...) 처리.
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = SettingsSecondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = hospital.address,
                    style = SettingsDescriptionStyle.copy(fontSize = 13.sp),
                    color = SettingsSecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // 사용자 GPS가 아닌 Map 화면 기본 위치(DefaultSearchOrigin=서면) 기준 거리. 정렬 선택과 무관하게 항상 보여준다.
            val hospitalLatitude = hospital.latitude
            val hospitalLongitude = hospital.longitude
            if (hospitalLatitude != null && hospitalLongitude != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = haversineDistanceMeters(
                        DefaultSearchOrigin.LATITUDE,
                        DefaultSearchOrigin.LONGITUDE,
                        hospitalLatitude,
                        hospitalLongitude
                    ).toDistanceLabel(),
                    style = SettingsDescriptionStyle,
                    color = CoralPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                hospital.supportedLanguages.take(3).forEach { lang ->
                    LanguageBadge(text = lang.toLanguageBadgeLabel())
                }
            }
        }
        }
        // 등장 전(revealProgress<1)에만 카드 위에 반짝이는 스켈레톤을 덮어, 콘텐츠가 서서히
        // 드러나는 동안 로딩 중인 듯한 느낌을 준다. 진행도가 반비례로 사라지게(1-progress) 해서
        // 카드 본문이 선명해지는 타이밍과 자연스럽게 맞물린다.
        if (isRevealAnimated && revealProgress < 1f) {
            ShimmerSkeleton(
                alpha = 1f - revealProgress,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

@Composable
private fun EmptySearchBanner(onReset: () -> Unit, modifier: Modifier = Modifier) {
    val strings = LocalAppStrings.current.search
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(CoralPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Outlined.SearchOff, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = strings.emptyResultsTitle, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = strings.emptyResultsSubtitle,
            style = SettingsDescriptionStyle,
            color = SettingsSecondaryText
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = strings.resetFiltersButton)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HospitalSearchListContentPreview() {
    MediInBusanTheme {
        HospitalSearchListContent(
            uiState = HospitalSearchListUiState(isLoading = false),
            onNavigateToSettings = {},
            onLanguageSelected = {},
            onQueryChanged = {},
            onSearchSubmit = {},
            onSuggestionSelected = {},
            onRecentSearchSelected = {},
            onRecentSearchDeleted = {},
            onFilterToggled = {},
            onSortSelected = {},
            onLoadMore = {},
            onResetSearchConditions = {},
            onSelectHospital = {},
            onRetry = {},
            onAutoFocusApplied = {}
        )
    }
}
