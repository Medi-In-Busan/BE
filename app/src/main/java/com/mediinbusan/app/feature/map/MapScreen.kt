package com.mediinbusan.app.feature.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
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
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place

/**
 * TODO: 실제 Kakao Map(com.kakao.vectormap) 렌더링으로 교체한다.
 * 지금은 병원·주변 장소의 공개 좌표/주소를 텍스트 목록으로 표시해 화면 배선만 검증한다.
 * F-010 예외 처리: 좌표가 없으면 주소 텍스트만 표시한다.
 */
@Composable
fun MapScreen(
    hospitalId: String,
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    LaunchedEffect(hospitalId) {
        viewModel.load(hospitalId)
    }

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(hospitalId) })
        else -> Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "지도 (Kakao Map 연동 예정)", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                uiState.hospital?.let { hospital ->
                    item {
                        ListItem(
                            headlineContent = { Text(text = hospital.name) },
                            supportingContent = {
                                Text(text = hospital.coordinateLabel())
                            }
                        )
                    }
                }
                items(uiState.nearbyPlaces) { place ->
                    ListItem(
                        headlineContent = { Text(text = place.name) },
                        supportingContent = {
                            Text(text = place.coordinateLabel())
                        }
                    )
                }
            }
        }
    }
}

private fun Hospital.coordinateLabel(): String =
    if (latitude != null && longitude != null) "$address ($latitude, $longitude)" else address

private fun Place.coordinateLabel(): String =
    if (latitude != null && longitude != null) "$address ($latitude, $longitude)" else address
