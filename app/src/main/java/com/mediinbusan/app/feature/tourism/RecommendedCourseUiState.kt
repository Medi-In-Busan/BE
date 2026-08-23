package com.mediinbusan.app.feature.tourism

import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.RecommendedTourismCourse
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory

data class RecommendedCourseUiState(
    val language: SupportedLanguage = SupportedLanguage.DEFAULT,
    val category: TourismCatalogCategory? = null,
    val district: BusanDistrict? = null,
    val course: RecommendedTourismCourse? = null,
    val selectedStopId: String? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
