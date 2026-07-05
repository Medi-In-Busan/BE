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
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState

@Composable
fun GuideScreen(
    onBack: () -> Unit,
    viewModel: GuideViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage)
        else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(uiState.steps) { step ->
                ListItem(
                    headlineContent = { Text(text = step.title) },
                    supportingContent = { Text(text = step.content) }
                )
            }
        }
    }
}
