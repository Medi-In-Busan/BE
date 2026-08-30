package com.mediinbusan.app.data.tourism

import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.UserPreferencesRepository
import com.mediinbusan.app.core.i18n.appStringsFor
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TourismCatalogRepositoryImpl @Inject constructor(
    private val api: TourismCatalogApi,
    private val userPreferencesRepository: UserPreferencesRepository
) : TourismCatalogRepository {
    override suspend fun findMatchingPlace(title: String, district: BusanDistrict): Result<TourismCatalogItem?> = try {
        val response = api.findMatchingPlace(title, district.name)
        Result.Success(if (response.matched) requireNotNull(response.item).toDomain() else null)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun getCatalog(
        category: TourismCatalogCategory,
        district: BusanDistrict?,
        page: Int,
        pageSize: Int
    ): Flow<Result<TourismCatalog>> = flow {
        emit(Result.Loading)
        try {
            val language = userPreferencesRepository.userPreferences.first().languageCode
            emit(Result.Success(api.getCatalog(category.name, district?.name, page, pageSize, language).toDomain()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val language = userPreferencesRepository.userPreferences.first().languageCode
            emit(Result.Error(e, appStringsFor(language).tourism.catalogLoadError))
        }
    }
}
