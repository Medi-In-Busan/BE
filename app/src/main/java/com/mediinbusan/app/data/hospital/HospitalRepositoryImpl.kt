package com.mediinbusan.app.data.hospital

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * MediInBusan 자체 백엔드(backend/)를 호출한다.
 * languageCode는 백엔드가 아직 다국어를 지원하지 않아(TODO) 지금은 쓰이지 않는다.
 */
class HospitalRepositoryImpl @Inject constructor(
    private val hospitalApi: HospitalApi
) : HospitalRepository {

    override fun getHospitals(
        keyword: String?,
        specialties: List<MedicalCategory>,
        languageCode: String
    ): Flow<Result<List<Hospital>>> = flow {
        emit(Result.Loading)
        try {
            val page = hospitalApi.getHospitals(
                keyword = keyword?.takeIf { it.isNotBlank() },
                specialties = specialties.takeIf { it.isNotEmpty() }?.joinToString(separator = ",") { it.name }
            )
            emit(Result.Success(page.content.map { it.toDomain() }))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "병원 목록을 불러오지 못했습니다."))
        }
    }

    override fun getAllHospitals(languageCode: String): Flow<Result<List<Hospital>>> = flow {
        emit(Result.Loading)
        try {
            val allHospitals = mutableListOf<Hospital>()
            var page = 0
            do {
                val pageDto = hospitalApi.getHospitals(page = page)
                allHospitals += pageDto.content.map { it.toDomain() }
                page++
            } while (page < pageDto.totalPages)
            emit(Result.Success(allHospitals))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "병원 목록을 불러오지 못했습니다."))
        }
    }

    override fun getHospitalDetail(hospitalId: String, languageCode: String): Flow<Result<Hospital>> = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(hospitalApi.getHospitalDetail(hospitalId).toDomain()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "병원 정보를 찾을 수 없습니다."))
        }
    }
}
