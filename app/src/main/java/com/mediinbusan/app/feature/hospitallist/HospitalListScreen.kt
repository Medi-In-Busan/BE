package com.mediinbusan.app.feature.hospitallist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
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
fun HospitalListScreen(
    medicalPurpose: String?,
    onSelectHospital: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: HospitalListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    LaunchedEffect(medicalPurpose) {
        viewModel.load(medicalPurpose)
    }

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(medicalPurpose) })
        else -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text("병원명 검색") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (uiState.filteredHospitals.isEmpty()) {
                EmptyState(message = "조건에 맞는 병원이 없습니다.")
            } else {
                LazyColumn {
                    items(uiState.filteredHospitals) { hospital ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelectHospital(hospital.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = hospital.name)
                                Text(text = hospital.address)
                                Text(text = hospital.specialties.joinToString())
                            }
                        }
                    }
                }
            }
        }
    }
}
