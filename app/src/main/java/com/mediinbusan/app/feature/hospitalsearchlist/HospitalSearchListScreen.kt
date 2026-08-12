package com.mediinbusan.app.feature.hospitalsearchlist

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.SearchStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BrandBackTopAppBar
import com.mediinbusan.app.core.ui.BrandDropdownMenu
import com.mediinbusan.app.core.ui.BrandDropdownMenuItem
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.FavoriteHeartButton
import com.mediinbusan.app.core.ui.FilterChipPill
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.toLanguageBadgeLabel
import com.mediinbusan.app.core.ui.LanguageBadge
import com.mediinbusan.app.data.hospital.Hospital

@Composable
fun HospitalSearchListScreen(
    medicalPurpose: MedicalCategory?,
    onSelectHospital: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: HospitalSearchListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(medicalPurpose)
    }

    HospitalSearchListContent(
        uiState = uiState,
        onBack = onBack,
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
        onToggleFavorite = viewModel::onToggleFavorite,
        onSelectHospital = onSelectHospital,
        onRetry = viewModel::onRetry
    )
}

@Composable
private fun HospitalSearchListContent(
    uiState: HospitalSearchListUiState,
    onBack: () -> Unit,
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
    onToggleFavorite: (String) -> Unit,
    onSelectHospital: (String) -> Unit,
    onRetry: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isSearchFocused by remember { mutableStateOf(false) }
    // 콘텐츠 유무가 아니라 포커스 여부만으로 패널을 띄운다. 자동완성 후보 개수는 한글 조합
    // 중간 단계(예: "부산" 입력 중 "부"+미완성 글자)에서 순간적으로 0건이 될 수 있는데,
    // 콘텐츠 유무로 판단하면 그 찰나에 패널이 사라지고 뒤의 결과 리스트가 노출돼버린다.
    val showAssistPanel = isSearchFocused
    Scaffold(
        topBar = {
            BrandBackTopAppBar(
                onBack = onBack,
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
                    onFocusChanged = { isSearchFocused = it },
                    modifier = Modifier.padding(horizontal = 20.dp)
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

                    Spacer(modifier = Modifier.height(14.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.results.isEmpty()) {
                            EmptySearchBanner(
                                onReset = onResetSearchConditions,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        } else {
                            SearchResultList(
                                results = uiState.results,
                                favoriteHospitalIds = uiState.favoriteHospitalIds,
                                hasReachedEnd = uiState.hasReachedEnd,
                                onLoadMore = onLoadMore,
                                onSelectHospital = onSelectHospital,
                                onToggleFavorite = onToggleFavorite,
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
@Composable
private fun SearchResultCountLabel(query: String, count: Int) {
    val strings = LocalAppStrings.current.search
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = SettingsPrimaryText, fontWeight = FontWeight.Bold)) {
            if (query.isNotBlank()) {
                append(strings.resultCountWithQueryFormat.format(query))
            } else {
                append(strings.resultCountGenericLabel)
            }
        }
        withStyle(SpanStyle(color = CoralPrimary, fontWeight = FontWeight.Bold)) {
            append(strings.resultCountSuffixFormat.format(count))
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = RoundedCornerShape(percent = 50))
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
                    .onFocusChanged { onFocusChanged(it.isFocused) }
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CoralPrimary)
                .clickable(onClick = onSearchSubmit),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.Search, contentDescription = LocalAppStrings.current.common.searchContentDescription, tint = Color.White)
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
    SearchSortOption.RELEVANCE -> strings.sortRelevance
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

// TODO: 정렬 기준 미확정. 선택은 되지만 목록 순서에는 영향 없는 스텁이다.
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
            SearchSortOption.entries.forEach { option ->
                BrandDropdownMenuItem(
                    label = displayLabelForSortOption(option, strings),
                    selected = option == selected,
                    onClick = {
                        onSortSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchResultList(
    results: List<Hospital>,
    favoriteHospitalIds: Set<String>,
    hasReachedEnd: Boolean,
    onLoadMore: () -> Unit,
    onSelectHospital: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    bottomContentPadding: Dp
) {
    val listState = rememberLazyListState()

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
        items(results, key = { it.id }) { hospital ->
            SearchResultCard(
                hospital = hospital,
                isFavorite = hospital.id in favoriteHospitalIds,
                onClick = { onSelectHospital(hospital.id) },
                onFavoriteClick = { onToggleFavorite(hospital.id) }
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
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        AsyncImageBox(
            model = hospital.imageUrl,
            contentDescription = hospital.name,
            modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = hospital.name,
                style = SettingsItemTitleStyle,
                color = SettingsPrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hospital.specialties.joinToString(", ").ifEmpty { hospital.address },
                style = SettingsDescriptionStyle,
                color = SettingsSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                hospital.supportedLanguages.take(3).forEach { lang ->
                    LanguageBadge(text = lang.toLanguageBadgeLabel())
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        FavoriteHeartButton(isFavorite = isFavorite, onClick = onFavoriteClick)
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
            onBack = {},
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
            onToggleFavorite = {},
            onSelectHospital = {},
            onRetry = {}
        )
    }
}
