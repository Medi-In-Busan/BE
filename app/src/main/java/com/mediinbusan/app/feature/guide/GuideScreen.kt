package com.mediinbusan.app.feature.guide

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState

@Composable
fun GuideScreen(
    onBack: () -> Unit,
    viewModel: GuideViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    // 하단 탭바가 항상 보이는 화면이라 BottomNavBarHeight만큼 직접 여백을 둔다
    // (MediInBusanApp.kt 주석 참고 — 상위 Scaffold의 innerPadding에 기대지 않는 이유).
    when {
        uiState.isLoading -> LoadingState(modifier = Modifier.padding(bottom = BottomNavBarHeight))
        errorMessage != null -> ErrorState(
            message = errorMessage,
            modifier = Modifier.padding(bottom = BottomNavBarHeight)
        )
        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = BottomNavBarHeight)
        ) {
            items(uiState.steps) { step ->
                ListItem(
                    headlineContent = { Text(text = step.title) },
                    supportingContent = { Text(text = step.content) }
                )
            }
        }
    }
}
