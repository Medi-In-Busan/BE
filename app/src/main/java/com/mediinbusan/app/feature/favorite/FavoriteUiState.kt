package com.mediinbusan.app.feature.favorite

import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.data.favorite.Favorite

data class FavoriteUiState(
    val favorites: List<Favorite> = emptyList(),
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code
) {
    // 필터/정렬 UI는 없앴고, 항상 최근 저장순으로 보여준다.
    val displayedFavorites: List<Favorite>
        get() = favorites.sortedByDescending { it.savedAt }
}
