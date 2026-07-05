package com.mediinbusan.app.feature.hospitaldetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState

@Composable
fun HospitalDetailScreen(
    hospitalId: String,
    onNavigateToGuide: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToMap: () -> Unit,
    onBack: () -> Unit,
    viewModel: HospitalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hospital = uiState.hospital
    val errorMessage = uiState.errorMessage

    LaunchedEffect(hospitalId) {
        viewModel.load(hospitalId)
    }

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(hospitalId) })
        hospital != null -> Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = hospital.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = hospital.specialties.joinToString())
            Text(text = hospital.address)
            hospital.phoneNumber?.let { Text(text = it) }
            Text(text = "지원 언어: " + hospital.supportedLanguages.joinToString())
            hospital.description?.let { Text(text = it) }

            Button(onClick = { viewModel.onToggleFavorite() }) {
                Text(text = if (uiState.isFavorite) "즐겨찾기 해제" else "즐겨찾기 추가")
            }
            OutlinedButton(onClick = onNavigateToGuide) { Text(text = "의료 이용 가이드") }
            OutlinedButton(onClick = onNavigateToNearby) { Text(text = "주변 관광·웰니스") }
            OutlinedButton(onClick = onNavigateToMap) { Text(text = "지도에서 보기") }
        }
    }
}
