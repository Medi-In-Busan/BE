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
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.MedicalCategory
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
    onFilterToggled: (String) -> Unit,
    onSortSelected: (SearchSortOption) -> Unit,
    onLoadMore: () -> Unit,
    onResetSearchConditions: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSelectHospital: (String) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            BrandBackTopAppBar(
                onBack = onBack,
                currentLanguageCode = uiState.selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(innerPadding).padding(bottom = BottomNavBarHeight))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage,
                modifier = Modifier.padding(innerPadding).padding(bottom = BottomNavBarHeight),
                onRetry = onRetry
            )
            else -> Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(bottom = BottomNavBarHeight)) {
                Spacer(modifier = Modifier.height(16.dp))
                SearchInputBar(
                    query = uiState.query,
                    onQueryChanged = onQueryChanged,
                    onSearchSubmit = onSearchSubmit,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

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
                            onToggleFavorite = onToggleFavorite
                        )
                    }
                }
            }
        }
    }
}

// 검색어가 있으면 "'피부과' 검색결과"까지 굵은 검정으로, 건수는 브랜드 코랄 컬러로 강조한다.
@Composable
private fun SearchResultCountLabel(query: String, count: Int) {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = SettingsPrimaryText, fontWeight = FontWeight.Bold)) {
            if (query.isNotBlank()) {
                append("'$query' 검색결과 ")
            } else {
                append("검색결과 ")
            }
        }
        withStyle(SpanStyle(color = CoralPrimary, fontWeight = FontWeight.Bold)) {
            append("${count}건")
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
                    text = "병원 이름, 진료과목으로 검색",
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
                modifier = Modifier.fillMaxWidth()
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
            Icon(imageVector = Icons.Filled.Search, contentDescription = "검색", tint = Color.White)
        }
    }
}

// 전체 의료 태그 + 관광지를 한 줄에 모두 배치하고 가로로 슬라이드해서 볼 수 있게 한다.
@Composable
private fun FilterChipsRow(filters: List<SearchFilterChip>, onFilterToggled: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters, key = { it.label }) { chip ->
            FilterChipPill(label = chip.label, selected = chip.selected, onClick = { onFilterToggled(chip.label) })
        }
    }
}

// TODO: 정렬 기준 미확정. 선택은 되지만 목록 순서에는 영향 없는 스텁이다.
@Composable
private fun SortDropdownButton(selected: SearchSortOption, onSortSelected: (SearchSortOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier.clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "정렬", style = MaterialTheme.typography.labelMedium, color = SettingsSecondaryText)
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
                    label = option.label,
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
    onToggleFavorite: (String) -> Unit
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

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp)
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
        Text(text = "원하는 결과가 없으신가요?", style = SettingsItemTitleStyle, color = SettingsPrimaryText)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "검색어를 변경하거나 필터 조건을 확인해보세요",
            style = SettingsDescriptionStyle,
            color = SettingsSecondaryText
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "검색조건 초기화")
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
