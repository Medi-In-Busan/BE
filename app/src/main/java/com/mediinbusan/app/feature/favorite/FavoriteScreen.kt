package com.mediinbusan.app.feature.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.data.favorite.FavoriteItemType

@Composable
fun FavoriteScreen(
    onSelectHospital: (String) -> Unit,
    onSelectPlace: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.favorites.isEmpty()) {
        EmptyState(message = "저장한 병원·장소가 없습니다.")
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(uiState.favorites) { favorite ->
                ListItem(
                    headlineContent = { Text(text = favorite.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (favorite.itemType) {
                                FavoriteItemType.HOSPITAL -> onSelectHospital(favorite.itemId)
                                FavoriteItemType.PLACE -> onSelectPlace(favorite.itemId)
                            }
                        }
                )
            }
        }
    }
}
