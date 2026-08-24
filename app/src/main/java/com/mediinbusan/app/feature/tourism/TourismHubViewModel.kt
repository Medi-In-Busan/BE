package com.mediinbusan.app.feature.tourism

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.isLanguageVariant
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 행동 기반 "맞춤 추천"(방문 기록·즐겨찾기·최근 본 항목 반영)은 feature/tourism-recommendation/84의
 * 몫이라 여기서는 다루지 않는다 — 현재 언어에 맞는 카테고리만 걸러 그룹별로 보여준다.
 */
@HiltViewModel
class TourismHubViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TourismHubUiState())
    val uiState: StateFlow<TourismHubUiState> = _uiState

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences
                .map { preferences ->
                    val language = SupportedLanguage.entries.find { it.code == preferences.languageCode }
                        ?: SupportedLanguage.DEFAULT
                    val languageCategory = tourismCategoryForLanguage(language.code)
                    val categories = TourismCatalogCategory.entries.filter { category ->
                        !category.isLanguageVariant || category == languageCategory
                    }
                    TourismHubUiState(language = language, categories = categories)
                }
                .collect { _uiState.value = it }
        }
    }
}
