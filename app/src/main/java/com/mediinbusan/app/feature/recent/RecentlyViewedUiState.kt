package com.mediinbusan.app.feature.recent

import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.data.recent.RecentlyViewed

data class RecentlyViewedUiState(
    val items: List<RecentlyViewed> = emptyList(),
    val selectedLanguage: String = SupportedLanguage.DEFAULT.code
)
