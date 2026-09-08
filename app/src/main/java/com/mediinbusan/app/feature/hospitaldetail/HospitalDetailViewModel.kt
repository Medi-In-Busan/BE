package com.mediinbusan.app.feature.hospitaldetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.recent.RecentItemType
import com.mediinbusan.app.data.recent.RecentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** F-006 상세 정보, F-007 다국어, F-009 지원 언어 표시, F-015 즐겨찾기, F-016 최근 본 항목. */
@HiltViewModel
class HospitalDetailViewModel @Inject constructor(
    private val hospitalRepository: HospitalRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HospitalDetailUiState())
    val uiState: StateFlow<HospitalDetailUiState> = _uiState

    fun load(hospitalId: String) {
        viewModelScope.launch {
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            hospitalRepository.getHospitalDetail(hospitalId, languageCode).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isLoading = true, isError = false, errorMessage = null)
                        is Result.Success -> {
                            recordView(result.data)
                            loadNearbySameSpecialty(result.data)
                            state.copy(isLoading = false, isError = false, hospital = result.data, errorMessage = null)
                        }
                        // 폴백 문구는 여기서 언어를 고정하지 않고 화면이 LocalAppStrings로 매번 새로 읽는다.
                        is Result.Error -> state.copy(isLoading = false, isError = true, errorMessage = result.message)
                    }
                }
            }
        }
        viewModelScope.launch {
            favoriteRepository.observeIsFavorite(hospitalId).collect { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    /**
     * 진료과목이 겹치는 주변 병원을 이 병원 좌표 기준으로 불러온다.
     *
     * 기기 GPS는 쓰지 않는다 — 좌표는 방금 받은 병원 상세의 것이다(CLAUDE.md §1의 "사용자 GPS를
     * 서버로 보내는 위치기반 추천 없음" 제약. HospitalRepository.getNearbyHospitals도 좌표를 항상
     * 호출부가 넘기도록 되어 있다).
     *
     * 실패해도 화면 전체를 오류로 만들지 않는다. 이 섹션은 본문 정보가 아니라 곁들이는 추천이라,
     * 못 불러오면 조용히 빈 목록으로 두고 섹션만 사라지는 게 맞다.
     */
    private fun loadNearbySameSpecialty(hospital: Hospital) {
        val latitude = hospital.latitude
        val longitude = hospital.longitude
        // 좌표가 없으면 "주변"을 정의할 수 없다. 지어내지 않고 섹션을 뺀다.
        if (latitude == null || longitude == null) return

        // 라벨 문자열을 그대로 서버에 넘기지 않고 enum으로 되돌린다 — 매칭되지 않는 라벨은 버린다.
        val specialties = hospital.specialties
            .mapNotNull { label -> MedicalCategory.entries.find { it.label == label } }
            .distinct()
        // 겹칠 과목이 하나도 없으면 "같은 진료과목"이라는 말 자체가 성립하지 않는다.
        if (specialties.isEmpty()) return

        viewModelScope.launch {
            hospitalRepository.getNearbyHospitals(
                latitude = latitude,
                longitude = longitude,
                radiusMeters = NEARBY_RADIUS_METERS,
                specialties = specialties
            ).collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Loading -> state.copy(isNearbyLoading = true)
                        is Result.Success -> state.copy(
                            isNearbyLoading = false,
                            // 지금 보고 있는 병원이 결과에 그대로 섞여 들어온다 — 반드시 뺀다.
                            nearbyHospitals = result.data
                                .filter { it.id != hospital.id }
                                .take(NEARBY_MAX_COUNT)
                        )
                        is Result.Error -> state.copy(isNearbyLoading = false, nearbyHospitals = emptyList())
                    }
                }
            }
        }
    }

    private fun recordView(hospital: Hospital) {
        viewModelScope.launch {
            recentRepository.recordView(
                itemId = hospital.id,
                itemName = hospital.name,
                itemType = RecentItemType.HOSPITAL,
                imageUrl = hospital.imageUrl,
                subtitle = hospital.specialties.joinToString(", "),
                address = hospital.address,
                latitude = hospital.latitude,
                longitude = hospital.longitude
            )
        }
    }

    fun onToggleFavorite() {
        val hospital = _uiState.value.hospital ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(
                Favorite(
                    itemId = hospital.id,
                    itemType = FavoriteItemType.HOSPITAL,
                    name = hospital.name,
                    imageUrl = hospital.imageUrl,
                    savedAt = System.currentTimeMillis(),
                    subtitle = hospital.specialties.joinToString(", "),
                    address = hospital.address,
                    latitude = hospital.latitude,
                    longitude = hospital.longitude
                )
            )
        }
    }

    companion object {
        /**
         * 주변 병원 검색 반경. 부산 시내에서 "다른 선택지"로 실제 고려할 만한 거리로 잡았다 —
         * 더 넓히면 반대편 구까지 올라와 "주변"이라는 말이 무색해지고, 더 좁히면 결과가 거의 없다.
         */
        private const val NEARBY_RADIUS_METERS = 5000.0

        /** 가로 스크롤 한 줄에 담기는 만큼만. 이 섹션은 목록이 아니라 곁들이는 추천이다. */
        private const val NEARBY_MAX_COUNT = 10
    }
}
