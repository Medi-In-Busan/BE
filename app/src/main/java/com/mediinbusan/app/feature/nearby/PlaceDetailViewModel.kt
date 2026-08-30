package com.mediinbusan.app.feature.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.data.recent.RecentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-012 관광·웰니스 장소 상세 정보. */
@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaceDetailUiState())
    val uiState: StateFlow<PlaceDetailUiState> = _uiState

    fun load(placeId: String) {
        viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            placeRepository.getPlaceDetail(placeId, languageCode).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isLoading = true, errorMessage = null)
                        is Result.Success -> {
                            recordView(result.data)
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

    private fun recordView(place: Place) {
        viewModelScope.launch {
            recentRepository.recordView(
                itemId = place.id,
                itemName = place.name,
                itemType = FavoriteItemType.PLACE,
                imageUrl = place.imageUrl,
                subtitle = place.type.label,
                address = place.address,
                latitude = place.latitude,
                longitude = place.longitude
            )
        }
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
                    savedAt = System.currentTimeMillis(),
                    subtitle = place.type.label,
                    address = place.address,
                    latitude = place.latitude,
                    longitude = place.longitude
                )
            )
        }
    }
}

// 즐겨찾기/최근 본 항목 카드의 태그 자리에 쓰는 장소 종류 한글 라벨. PlaceDetailScreen의 같은 이름
// private 확장과 동일한 매핑이다(ViewModel에는 Composable LocalAppStrings가 없어 별도로 둔다).
private val PlaceType.label: String
    get() = when (this) {
        PlaceType.TOURIST_ATTRACTION -> "관광지"
        PlaceType.RESTAURANT -> "카페·맛집"
        PlaceType.SHOPPING -> "쇼핑"
        PlaceType.LODGING -> "숙소"
        PlaceType.SPA -> "스파"
        PlaceType.WALK -> "산책"
        PlaceType.OTHER -> "기타"
    }
