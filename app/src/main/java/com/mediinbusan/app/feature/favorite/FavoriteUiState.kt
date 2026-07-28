package com.mediinbusan.app.feature.favorite

import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType

data class FavoriteUiState(
    val favorites: List<Favorite> = emptyList(),
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code,
    val selectedFilter: FavoriteTypeFilter = FavoriteTypeFilter.ALL,
    val selectedSort: FavoriteSortOption = FavoriteSortOption.RECENT
) {
    val displayedFavorites: List<Favorite>
        get() {
            val filtered = when (selectedFilter) {
                FavoriteTypeFilter.ALL -> favorites
                FavoriteTypeFilter.HOSPITAL -> favorites.filter { it.itemType == FavoriteItemType.HOSPITAL }
                FavoriteTypeFilter.PLACE -> favorites.filter { it.itemType == FavoriteItemType.PLACE }
            }
            return when (selectedSort) {
                FavoriteSortOption.RECENT -> filtered.sortedByDescending { it.savedAt }
                FavoriteSortOption.NAME -> filtered.sortedBy { it.name }
            }
        }
}

enum class FavoriteTypeFilter(val label: String) {
    ALL("전체"),
    HOSPITAL("병원"),
    PLACE("장소")
}

enum class FavoriteSortOption(val label: String) {
    RECENT("최근 저장순"),
    NAME("이름순")
}
