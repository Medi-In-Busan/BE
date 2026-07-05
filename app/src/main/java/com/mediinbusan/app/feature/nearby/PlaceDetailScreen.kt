package com.mediinbusan.app.feature.nearby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
fun PlaceDetailScreen(
    placeId: String,
    onBack: () -> Unit,
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val place = uiState.place
    val errorMessage = uiState.errorMessage

    LaunchedEffect(placeId) {
        viewModel.load(placeId)
    }

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(placeId) })
        place != null -> Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = place.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = place.address)
            place.description?.let { Text(text = it) }
            place.phoneNumber?.let { Text(text = it) }
            Button(onClick = { viewModel.onToggleFavorite() }) {
                Text(text = if (uiState.isFavorite) "즐겨찾기 해제" else "즐겨찾기 추가")
            }
        }
    }
}
