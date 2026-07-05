package com.mediinbusan.app.feature.nearby

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState

@Composable
fun NearbyScreen(
    hospitalId: String,
    onSelectPlace: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    onBack: () -> Unit,
    viewModel: NearbyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    LaunchedEffect(hospitalId) {
        viewModel.load(hospitalId)
    }

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(hospitalId) })
        uiState.places.isEmpty() -> EmptyState(message = "주변 장소 정보가 없습니다.")
        else -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Button(onClick = onNavigateToMap) { Text(text = "지도로 보기") }
            LazyColumn {
                items(uiState.places) { place ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelectPlace(place.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = place.name)
                            Text(text = place.address)
                        }
                    }
                }
            }
        }
    }
}
