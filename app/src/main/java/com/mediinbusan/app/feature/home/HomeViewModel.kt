package com.mediinbusan.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.PendingHospitalSearchEntry
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.data.favorite.FavoriteRepository
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.hospital.HospitalRepository
import com.mediinbusan.app.data.recent.RecentRepository
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.data.tourism.TourismInteractionRepository
import com.mediinbusan.app.domain.recommendation.GetRecommendedHospitalsUseCase
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.BuildRecommendedTourismCourseUseCase
import com.mediinbusan.app.domain.tourism.RecommendTourismCatalogUseCase
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismInteractionProfile
import com.mediinbusan.app.domain.tourism.TourismRecommendationContext
import com.mediinbusan.app.domain.tourism.TourismReferenceLocation
import com.mediinbusan.app.domain.tourism.inferTourismRecoveryStage
import com.mediinbusan.app.domain.tourism.tourismCategoryForLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val hospitalRepository: HospitalRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentRepository: RecentRepository,
    private val tourismCatalogRepository: TourismCatalogRepository,
    private val tourismInteractionRepository: TourismInteractionRepository,
    private val pendingHospitalSearchEntry: PendingHospitalSearchEntry,
    private val getRecommendedHospitalsUseCase: GetRecommendedHospitalsUseCase,
    private val recommendTourismCatalog: RecommendTourismCatalogUseCase,
    private val buildRecommendedCourse: BuildRecommendedTourismCourseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    private var courseSources: List<HomeCourseSource> = emptyList()
    private var courseCursor = 0
    private var courseFeedContext: HomeCourseFeedContext? = null
    private var courseLoadJob: Job? = null

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                _uiState.update { it.copy(languageCode = preferences.languageCode) }
            }
        }
        loadRecommendedHospitals()
        resetRecommendedCourses()
    }

    // "이런 의료기관은 어떠세요?" 섹션 — GetRecommendedHospitalsUseCase 참고. 즐겨찾기·최근 본
    // 항목·전체 병원 목록 세 Flow를 combine해서, 즐겨찾기 토글이나 최근 본 항목 갱신이 생기면
    // 재조회 없이 바로 다시 점수를 매겨 갱신된다.
    private fun loadRecommendedHospitals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, error = null) }
            val languageCode = userPreferencesRepository.userPreferences.first().languageCode
            combine(
                favoriteRepository.observeFavorites(),
                hospitalRepository.getAllHospitals(languageCode),
                recentRepository.observeRecentlyViewed()
            ) { favorites, result, recentlyViewed -> Triple(favorites, result, recentlyViewed) }
                .collect { (favorites, result, recentlyViewed) ->
                    when (result) {
                        is Result.Success -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = false,
                                recommendedHospitals = getRecommendedHospitalsUseCase(
                                    allHospitals = result.data,
                                    favorites = favorites,
                                    recentlyViewed = recentlyViewed
                                ),
                                error = null
                            )
                        }
                        is Result.Error -> _uiState.update {
                            // 서버 메시지가 없을 때 보여줄 폴백 문구는 여기서 언어를 고정해 넣지 않고,
                            // 화면(HomeScreen)이 LocalAppStrings로 매 리컴포지션마다 새로 읽게 한다.
                            it.copy(isLoading = false, isError = true, error = result.message)
                        }
                        Result.Loading -> Unit
                    }
                }
        }
    }

    // 카테고리 칩 탭 직후(같은 클릭 핸들러에서) 검색 화면으로 이동하기 직전에 호출된다.
    // PendingHospitalSearchEntry 주석 참고 — DataStore가 아니라 인메모리 싱글턴에 심어서,
    // Home으로 돌아와도 아무 흔적이 남지 않고 검색 화면 진입 시 정확히 한 번만 적용된다.
    fun onCategorySelected(purpose: MedicalCategory) {
        pendingHospitalSearchEntry.setPurpose(purpose)
    }

    // 검색바 탭 직후(같은 클릭 핸들러에서) 검색 화면으로 이동하기 직전에 호출된다. 검색 화면이
    // 결과 목록 대신 검색 입력 모드(최근 검색어/자동완성 패널)로 바로 열리도록 요청만 남긴다.
    fun onSearchBarClicked() {
        pendingHospitalSearchEntry.requestFocus()
    }

    fun onRetryClicked() {
        loadRecommendedHospitals()
        resetRecommendedCourses()
    }

    fun onLanguageSelected(languageCode: String) {
        viewModelScope.launch {
            userPreferencesRepository.setLanguageCode(languageCode)
            resetRecommendedCourses(languageCode)
        }
    }

    fun onLoadMoreCourses() {
        if (_uiState.value.isCourseLoading || !_uiState.value.hasMoreCourses) return
        courseLoadJob = viewModelScope.launch { loadNextCoursePage() }
    }

    fun onRetryCourses() {
        if (_uiState.value.recommendedCourses.isEmpty()) resetRecommendedCourses() else onLoadMoreCourses()
    }

    private fun resetRecommendedCourses(languageOverride: String? = null) {
        courseLoadJob?.cancel()
        courseLoadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    recommendedCourses = emptyList(),
                    isCourseLoading = true,
                    hasMoreCourses = true,
                    courseError = null
                )
            }
            val preferences = userPreferencesRepository.userPreferences.first()
            val languageCode = languageOverride ?: preferences.languageCode
            val profile = tourismInteractionRepository.profile.first()
            val favorites = favoriteRepository.observeFavorites().first()
            val recent = recentRepository.observeRecentlyViewed().first()
            val recentHospital = recent.firstOrNull {
                it.itemType == FavoriteItemType.HOSPITAL && it.latitude != null && it.longitude != null
            }
            val reference = recentHospital?.let {
                TourismReferenceLocation(requireNotNull(it.latitude), requireNotNull(it.longitude))
            }
            val now = System.currentTimeMillis()
            courseFeedContext = HomeCourseFeedContext(
                profile = profile,
                favoritePlaceNames = favorites.filter { it.itemType == FavoriteItemType.PLACE }.map { it.name },
                recentPlaceNames = recent.filter { it.itemType == FavoriteItemType.PLACE }.map { it.itemName },
                recommendationContext = TourismRecommendationContext(
                    medicalPurpose = preferences.medicalPurpose,
                    referenceLocation = reference,
                    recoveryStage = inferTourismRecoveryStage(
                        preferences.medicalPurpose,
                        recentHospital?.viewedAt,
                        now
                    ),
                    nowEpochMillis = now
                )
            )
            courseSources = buildCourseSources(languageCode, profile, preferences.medicalPurpose)
            courseCursor = 0
            loadNextCoursePage()
        }
    }

    private suspend fun loadNextCoursePage() {
        val context = courseFeedContext ?: return
        _uiState.update { it.copy(isCourseLoading = true, courseError = null) }
        val additions = mutableListOf<HomeRecommendedCourse>()
        while (additions.size < COURSE_PAGE_SIZE && courseCursor < courseSources.size) {
            val source = courseSources[courseCursor++]
            val result = tourismCatalogRepository.getCatalog(source.category, source.district)
                .first { it !is Result.Loading }
            if (result !is Result.Success) continue
            val recommendation = recommendTourismCatalog(
                catalog = result.data,
                profile = context.profile,
                favoritePlaceNames = context.favoritePlaceNames,
                recentPlaceNames = context.recentPlaceNames,
                context = context.recommendationContext
            )
            val course = buildRecommendedCourse(
                rankedItems = recommendation.catalog.items,
                referenceLocation = context.recommendationContext.referenceLocation
            ) ?: continue
            additions += HomeRecommendedCourse(
                id = "${source.category.name}:${source.district?.name.orEmpty()}",
                category = source.category,
                district = source.district,
                course = course
            )
        }
        _uiState.update { state ->
            val courses = (state.recommendedCourses + additions).distinctBy { it.id }
            state.copy(
                recommendedCourses = courses,
                isCourseLoading = false,
                hasMoreCourses = courseCursor < courseSources.size,
                courseError = if (additions.isEmpty() && courses.isEmpty()) "course_load_failed" else null
            )
        }
    }

    private fun buildCourseSources(
        languageCode: String,
        profile: TourismInteractionProfile,
        medicalPurpose: MedicalCategory?
    ): List<HomeCourseSource> {
        val languageCategory = tourismCategoryForLanguage(languageCode)
        val districtOrder = BusanDistrict.entries.sortedBy { district ->
            if (district == profile.preferredDistrict) 0 else 1
        }
        val districtCategories = listOf(languageCategory, TourismCatalogCategory.ACCESSIBLE)
            .sortedByDescending { profile.categoryAffinityScores[it] ?: 0.0 }
        val districtSources = districtOrder.flatMap { district ->
            districtCategories.map { category -> HomeCourseSource(category, district) }
        }
        val walking = HomeCourseSource(TourismCatalogCategory.WALKING, null)
        return if (
            medicalPurpose == MedicalCategory.WELLNESS ||
            medicalPurpose == MedicalCategory.REHABILITATION ||
            (profile.categoryAffinityScores[TourismCatalogCategory.WALKING] ?: 0.0) > 0.0
        ) {
            listOf(walking) + districtSources
        } else {
            districtSources.take(4) + walking + districtSources.drop(4)
        }
    }

    private data class HomeCourseSource(
        val category: TourismCatalogCategory,
        val district: BusanDistrict?
    )

    private data class HomeCourseFeedContext(
        val profile: TourismInteractionProfile,
        val favoritePlaceNames: List<String>,
        val recentPlaceNames: List<String>,
        val recommendationContext: TourismRecommendationContext
    )

    private companion object {
        const val COURSE_PAGE_SIZE = 4
    }
}
