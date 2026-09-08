package com.mediinbusan.app.feature.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceRepository
import com.mediinbusan.app.data.place.PlaceType
import com.mediinbusan.app.data.recent.RecentItemType
import com.mediinbusan.app.data.recent.RecentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
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
                            recordView(result.data, languageCode)
                            loadNearbySameType(result.data, languageCode)
                            state.copy(isLoading = false, place = result.data, errorMessage = null)
                        }
                        is Result.Error -> state.copy(
                            isLoading = false,
                            errorMessage = result.message ?: appStringsFor(languageCode).nearby.genericErrorMessage
                        )
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

    /**
     * 같은 종류(관광지/맛집 등)의 주변 장소를 이 장소 좌표 기준으로 불러온다.
     *
     * 기기 GPS는 쓰지 않는다 — 좌표는 방금 받은 장소 상세의 것이다(CLAUDE.md §1).
     *
     * 종류 필터는 앱에서 건다. 백엔드 /api/wellness/places는 좌표·반경만 받고 종류 파라미터가 없어서,
     * 반경 안을 받아온 뒤 같은 PlaceType만 남긴다. 반경이 좁아 응답 자체가 크지 않다.
     *
     * 실패하면 조용히 빈 목록으로 둔다 — 본문 정보가 아니라 곁들이는 추천이라 섹션만 사라지는 게 맞다.
     */
    private fun loadNearbySameType(place: Place, languageCode: String) {
        val latitude = place.latitude
        val longitude = place.longitude
        // 좌표가 없으면 "주변"을 정의할 수 없다. 지어내지 않고 섹션을 뺀다.
        if (latitude == null || longitude == null) return
        // "기타"는 종류가 아니라 분류 실패에 가깝다 — 같은 기타끼리 묶어봐야 서로 상관없는 곳들이다.
        if (place.type == PlaceType.OTHER) return

        viewModelScope.launch {
            placeRepository.getPlacesNear(
                latitude = latitude,
                longitude = longitude,
                radiusMeters = NEARBY_RADIUS_METERS,
                languageCode = languageCode
            ).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Success -> state.copy(
                            nearbySamePlaces = result.data
                                // 지금 보고 있는 장소가 결과에 그대로 섞여 들어온다 — 반드시 뺀다.
                                .filter { it.id != place.id && it.type == place.type }
                                .take(NEARBY_MAX_COUNT)
                        )
                        is Result.Error -> state.copy(nearbySamePlaces = emptyList())
                        is Result.Loading -> state
                    }
                }
            }
        }
    }

    private fun recordView(place: Place, languageCode: String) {
        viewModelScope.launch {
            recentRepository.recordView(
                itemId = place.id,
                itemName = place.name,
                itemType = RecentItemType.PLACE,
                imageUrl = place.imageUrl,
                subtitle = place.type.localizedLabel(languageCode),
                address = place.address,
                latitude = place.latitude,
                longitude = place.longitude
            )
        }
    }

    fun onToggleFavorite() {
        val place = _uiState.value.place ?: return
        viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            favoriteRepository.toggleFavorite(
                Favorite(
                    itemId = place.id,
                    itemType = FavoriteItemType.PLACE,
                    name = place.name,
                    imageUrl = place.imageUrl,
                    savedAt = System.currentTimeMillis(),
                    subtitle = place.type.localizedLabel(languageCode),
                    address = place.address,
                    latitude = place.latitude,
                    longitude = place.longitude
                )
            )
        }
    }

    companion object {
        /**
         * 주변 장소 검색 반경. 병원 추천(5km)보다 좁게 잡는다 — 관광지·맛집은 "가는 김에 들를 곳"이라
         * 도보·짧은 이동 거리 안에 있어야 의미가 있고, 넓히면 시내 전체가 걸려 추천이 무의미해진다.
         */
        private const val NEARBY_RADIUS_METERS = 2000.0

        /** 가로 스크롤 한 줄에 담기는 만큼만. 목록 화면이 아니라 곁들이는 추천이다. */
        private const val NEARBY_MAX_COUNT = 10
    }
}

// 즐겨찾기/최근 본 항목 카드의 태그 자리에 쓰는 장소 종류 한글 라벨. PlaceDetailScreen의 같은 이름
// private 확장과 동일한 매핑이다(ViewModel에는 Composable LocalAppStrings가 없어 별도로 둔다).
private fun PlaceType.localizedLabel(languageCode: String): String =
    appStringsFor(languageCode).nearby.placeTypeLabels[name].orEmpty()
