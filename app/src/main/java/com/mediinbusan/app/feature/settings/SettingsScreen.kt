package com.mediinbusan.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "언어 변경", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.availableLanguages) { language ->
                FilterChip(
                    selected = uiState.selectedLanguage == language,
                    onClick = { viewModel.onLanguageSelected(language) },
                    label = { Text(text = language.uppercase()) }
                )
            }
        }

        // F-018: 데이터 출처 및 이용 유의사항
        Text(text = "데이터 출처", style = MaterialTheme.typography.titleMedium)
        Text(text = "본 앱의 의료기관·관광지 정보는 한국관광공사 공공데이터(OpenAPI)를 기반으로 제공됩니다.")
        Text(text = "실제 방문 전 병원·장소의 공식 채널을 통해 최신 정보를 확인하시기 바랍니다.")
    }
}
