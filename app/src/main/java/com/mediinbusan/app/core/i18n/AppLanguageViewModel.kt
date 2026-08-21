package com.mediinbusan.app.core.i18n

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** 앱 루트(MediInBusanApp)에서 LocalAppStrings를 채우기 위해 현재 언어만 구독하는 최소 ViewModel. */
@HiltViewModel
class AppLanguageViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val language: StateFlow<SupportedLanguage> = userPreferencesRepository.userPreferences
        .map { preferences -> SupportedLanguage.entries.find { it.code == preferences.languageCode } ?: SupportedLanguage.DEFAULT }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SupportedLanguage.DEFAULT)
}
