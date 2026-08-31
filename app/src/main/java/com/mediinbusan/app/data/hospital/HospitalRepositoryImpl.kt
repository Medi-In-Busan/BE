package com.mediinbusan.app.data.hospital

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.common.TtlCache
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MediInBusan 자체 백엔드(backend/)를 호출한다.
 * languageCode는 병원 상세(getHospitalDetail)의 소개글·영업시간에만 반영된다 — 목록 조회(getHospitals/
 * getAllHospitals) 응답에는 언어별로 달라지는 필드가 없어(이름·주소는 번역 대상 아님) 전달하지 않는다.
 */
@Singleton
class HospitalRepositoryImpl @Inject constructor(
    private val hospitalApi: HospitalApi
) : HospitalRepository {

    // 지도 "전체 브라우징"(하단 탭 '지도')과 의료기관 목록 화면 진입마다 같은 목록을 매번 네트워크로
    // 새로 받아오면 로딩이 느리게 느껴진다 — 이 리포지토리가 @Singleton이라 앱이 살아있는 동안
    // 유지되는 캐시를 둔다. 병원 목록은 큐레이션된 고정 시드 데이터(backend/CLAUDE.md §Architecture
    // 참고)라 몇 분 지연되어 반영돼도 문제없다. 키워드·진료과 조합별로 따로 캐시한다.
    private val hospitalListCache = TtlCache<String, List<Hospital>>(ttlMillis = TimeUnit.MINUTES.toMillis(10))

    override fun getHospitals(
        keyword: String?,
        specialties: List<MedicalCategory>,
        languageCode: String
    ): Flow<Result<List<Hospital>>> = flow {
        val cacheKey = "${keyword?.takeIf { it.isNotBlank() }.orEmpty()}|${specialties.joinToString(",") { it.name }}"
        hospitalListCache.get(cacheKey)?.let { cached ->
            emit(Result.Success(cached))
            return@flow
        }
        emit(Result.Loading)
        try {
            val page = hospitalApi.getHospitals(
                keyword = keyword?.takeIf { it.isNotBlank() },
                specialties = specialties.takeIf { it.isNotEmpty() }?.joinToString(separator = ",") { it.name }
            )
            val hospitals = page.content.map { it.toDomain() }
            hospitalListCache.put(cacheKey, hospitals)
            emit(Result.Success(hospitals))
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
            emit(Result.Success(hospitalApi.getHospitalDetail(hospitalId, languageCode).toDomain()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "병원 정보를 찾을 수 없습니다."))
        }
    }

    override fun getNearbyHospitals(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double?,
        specialties: List<MedicalCategory>
    ): Flow<Result<List<Hospital>>> = flow {
        emit(Result.Loading)
        try {
            val results = hospitalApi.getNearbyHospitals(
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                specialties = specialties.takeIf { it.isNotEmpty() }?.joinToString(separator = ",") { it.name }
            )
            emit(Result.Success(results.map { it.toDomain() }))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e, message = "이 위치 주변 병원을 불러오지 못했습니다."))
        }
    }
}
