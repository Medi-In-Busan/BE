package com.mediinbusan.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    onNavigateToHospitalList: (String?) -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "메디인부산", style = MaterialTheme.typography.headlineSmall)

        Text(text = "의료 목적으로 찾기", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(uiState.medicalPurposes) { purpose ->
                OutlinedButton(onClick = { onNavigateToHospitalList(purpose) }) {
                    Text(text = purpose)
                }
            }
        }

        Button(onClick = { onNavigateToHospitalList(null) }) {
            Text(text = "전체 병원 보기")
        }
        Button(onClick = onNavigateToGuide) {
            Text(text = "의료 이용 가이드")
        }
        Button(onClick = onNavigateToFavorite) {
            Text(text = "즐겨찾기")
        }
        Button(onClick = onNavigateToSettings) {
            Text(text = "설정")
        }
    }
}
