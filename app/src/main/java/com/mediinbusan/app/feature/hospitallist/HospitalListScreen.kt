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
import com.mediinbusan.app.core.ui.BottomNavBarHeight
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

    // 하단 탭바가 항상 보이는 화면이라 BottomNavBarHeight만큼 직접 여백을 둔다
    // (MediInBusanApp.kt 주석 참고 — 상위 Scaffold의 innerPadding에 기대지 않는 이유).
    when {
        uiState.isLoading -> LoadingState(modifier = Modifier.padding(bottom = BottomNavBarHeight))
        errorMessage != null -> ErrorState(
            message = errorMessage,
            modifier = Modifier.padding(bottom = BottomNavBarHeight),
            onRetry = { viewModel.load(medicalPurpose) }
        )
        else -> Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(bottom = BottomNavBarHeight)) {
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
