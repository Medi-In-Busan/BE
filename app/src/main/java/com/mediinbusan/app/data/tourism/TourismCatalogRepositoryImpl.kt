package com.mediinbusan.app.data.tourism

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TourismCatalogRepositoryImpl @Inject constructor(
    private val api: TourismCatalogApi
) : TourismCatalogRepository {
    override fun getCatalog(
        category: TourismCatalogCategory,
        district: BusanDistrict?
    ): Flow<Result<TourismCatalog>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(api.getCatalog(category.name, district?.name).toDomain()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(e, "관광 데이터를 불러오지 못했습니다."))
        }
    }
}
