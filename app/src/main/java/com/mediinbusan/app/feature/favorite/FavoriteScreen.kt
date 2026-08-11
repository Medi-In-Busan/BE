package com.mediinbusan.app.feature.favorite

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.i18n.FavoriteStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.SettingsTitleStyle
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BrandBackTopAppBar
import com.mediinbusan.app.core.ui.BrandDropdownMenu
import com.mediinbusan.app.core.ui.BrandDropdownMenuItem
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.FavoriteHeartButton
import com.mediinbusan.app.core.ui.FilterChipPill
import com.mediinbusan.app.core.ui.ItemTypeBadge
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType

@Composable
fun FavoriteScreen(
    onSelectHospital: (String) -> Unit,
    onSelectPlace: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    FavoriteContent(
        uiState = uiState,
        onBack = onBack,
        onLanguageSelected = viewModel::onLanguageSelected,
        onFilterSelected = viewModel::onFilterSelected,
        onSortSelected = viewModel::onSortSelected,
        onSelectFavorite = { favorite ->
            when (favorite.itemType) {
                FavoriteItemType.HOSPITAL -> onSelectHospital(favorite.itemId)
                FavoriteItemType.PLACE -> onSelectPlace(favorite.itemId)
            }
        },
        onRemove = viewModel::onRemove
    )
}

@Composable
private fun FavoriteContent(
    uiState: FavoriteUiState,
    onBack: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onFilterSelected: (FavoriteTypeFilter) -> Unit,
    onSortSelected: (FavoriteSortOption) -> Unit,
    onSelectFavorite: (Favorite) -> Unit,
    onRemove: (Favorite) -> Unit
) {
    val favorites = uiState.displayedFavorites
    val strings = LocalAppStrings.current.favorite

    Scaffold(
        topBar = {
            BrandBackTopAppBar(
                onBack = onBack,
                currentLanguageCode = uiState.selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = strings.screenTitle, style = SettingsTitleStyle, color = SettingsPrimaryText)
                Spacer(modifier = Modifier.height(16.dp))
            }

            FavoriteFilterChipsRow(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = onFilterSelected,
                strings = strings,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.totalCountFormat.format(favorites.size),
                    style = SettingsDescriptionStyle,
                    color = SettingsSecondaryText
                )
                FavoriteSortDropdownButton(selected = uiState.selectedSort, onSortSelected = onSortSelected, strings = strings)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (favorites.isEmpty()) {
                    EmptyState(message = strings.emptyMessage)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        items(favorites, key = { it.itemId }) { favorite ->
                            FavoriteRow(
                                favorite = favorite,
                                onClick = { onSelectFavorite(favorite) },
                                onRemove = { onRemove(favorite) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

// HospitalSearchListScreen의 필터 칩과 같은 룩이지만, 즐겨찾기 항목 타입(전체/병원/장소)은
// 상호 배타적이라 다중 토글이 아닌 단일 선택으로 동작한다.
private fun displayLabelForTypeFilter(filter: FavoriteTypeFilter, strings: FavoriteStrings): String = when (filter) {
    FavoriteTypeFilter.ALL -> strings.filterAll
    FavoriteTypeFilter.HOSPITAL -> strings.filterHospital
    FavoriteTypeFilter.PLACE -> strings.filterPlace
}

private fun displayLabelForSortOption(option: FavoriteSortOption, strings: FavoriteStrings): String = when (option) {
    FavoriteSortOption.RECENT -> strings.sortRecent
    FavoriteSortOption.NAME -> strings.sortName
}

@Composable
private fun FavoriteFilterChipsRow(
    selectedFilter: FavoriteTypeFilter,
    onFilterSelected: (FavoriteTypeFilter) -> Unit,
    strings: FavoriteStrings,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FavoriteTypeFilter.entries.forEach { filter ->
            FilterChipPill(
                label = displayLabelForTypeFilter(filter, strings),
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

// HospitalSearchListScreen의 정렬 드롭다운과 동일한 디자인. 저장순/이름순은 실제 데이터(savedAt,
// name)로 바로 정렬 가능해서 스텁이 아니라 실제 동작한다.
@Composable
private fun FavoriteSortDropdownButton(selected: FavoriteSortOption, onSortSelected: (FavoriteSortOption) -> Unit, strings: FavoriteStrings) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier.clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = displayLabelForSortOption(selected, strings), style = MaterialTheme.typography.labelMedium, color = SettingsSecondaryText)
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = SettingsSecondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
        BrandDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            FavoriteSortOption.entries.forEach { option ->
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
private fun FavoriteRow(favorite: Favorite, onClick: () -> Unit, onRemove: () -> Unit) {
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
            model = favorite.imageUrl,
            contentDescription = favorite.name,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = favorite.name, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(6.dp))
            ItemTypeBadge(itemType = favorite.itemType)
        }
        FavoriteHeartButton(isFavorite = true, onClick = onRemove)
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteContentPreview() {
    MediInBusanTheme {
        FavoriteContent(
            uiState = FavoriteUiState(
                favorites = listOf(
                    Favorite(
                        itemId = "1",
                        itemType = FavoriteItemType.HOSPITAL,
                        name = "부산대학교병원",
                        imageUrl = null,
                        savedAt = System.currentTimeMillis()
                    )
                )
            ),
            onBack = {},
            onLanguageSelected = {},
            onFilterSelected = {},
            onSortSelected = {},
            onSelectFavorite = {},
            onRemove = {}
        )
    }
}
