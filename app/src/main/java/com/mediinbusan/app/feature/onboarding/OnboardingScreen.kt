package com.mediinbusan.app.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "언어를 선택하세요", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.availableLanguages) { language ->
                FilterChip(
                    selected = uiState.selectedLanguage == language,
                    onClick = { viewModel.onLanguageSelected(language) },
                    label = { Text(text = language.uppercase()) }
                )
            }
        }

        Text(text = "의료 목적을 선택하세요", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.availablePurposes) { purpose ->
                FilterChip(
                    selected = uiState.selectedPurpose == purpose,
                    onClick = { viewModel.onPurposeSelected(purpose) },
                    label = { Text(text = purpose) }
                )
            }
        }

        Button(onClick = { viewModel.onComplete(onComplete) }) {
            Text(text = "시작하기")
        }
    }
}
