package com.mediinbusan.app.data.tourism

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import kotlinx.coroutines.flow.Flow

interface TourismCatalogRepository {
    suspend fun findMatchingPlace(title: String, district: BusanDistrict): Result<TourismCatalogItem?>

    fun getCatalog(
        category: TourismCatalogCategory,
        district: BusanDistrict? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Result<TourismCatalog>>
}
