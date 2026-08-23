package com.mediinbusan.app.data.tourism

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import kotlinx.coroutines.flow.Flow

interface TourismCatalogRepository {
    fun getCatalog(
        category: TourismCatalogCategory,
        district: BusanDistrict? = null
    ): Flow<Result<TourismCatalog>>
}
