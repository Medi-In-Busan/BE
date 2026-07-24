package com.mediinbusan.app.feature.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.data.recent.RecentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-012 관광·웰니스 장소 상세 정보. */
@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaceDetailUiState())
    val uiState: StateFlow<PlaceDetailUiState> = _uiState

    fun load(placeId: String) {
        viewModelScope.launch {
            placeRepository.getPlaceDetail(placeId).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isLoading = true, errorMessage = null)
                        is Result.Success -> {
                            recordView(result.data.id, result.data.name, result.data.imageUrl)
                            state.copy(isLoading = false, place = result.data, errorMessage = null)
                        }
                        is Result.Error -> state.copy(isLoading = false, errorMessage = result.message ?: "오류가 발생했습니다.")
                    }
                }
            }
        }
        viewModelScope.launch {
            favoriteRepository.observeIsFavorite(placeId).collect { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    private fun recordView(id: String, name: String, imageUrl: String?) {
        viewModelScope.launch { recentRepository.recordView(id, name, FavoriteItemType.PLACE, imageUrl) }
    }

    fun onToggleFavorite() {
        val place = _uiState.value.place ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(
                Favorite(
                    itemId = place.id,
                    itemType = FavoriteItemType.PLACE,
                    name = place.name,
                    imageUrl = place.imageUrl,
                    savedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
