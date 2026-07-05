package com.mediinbusan.app.data.hospital

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * TODO(F-004,F-006,F-007,F-009): [HospitalApi]를 통한 실제 한국관광공사 의료관광정보 연동으로 교체한다.
 * 지금은 화면·내비게이션 스캐폴드 검증을 위한 샘플 데이터를 반환한다.
 */
class HospitalRepositoryImpl @Inject constructor(
    private val hospitalApi: HospitalApi
) : HospitalRepository {

    override fun getHospitals(medicalPurpose: String?, languageCode: String): Flow<Result<List<Hospital>>> = flow {
        emit(Result.Loading)
        emit(Result.Success(sampleHospitals))
    }

    override fun getHospitalDetail(hospitalId: String, languageCode: String): Flow<Result<Hospital>> = flow {
        emit(Result.Loading)
        val hospital = sampleHospitals.firstOrNull { it.id == hospitalId }
        if (hospital != null) {
            emit(Result.Success(hospital))
        } else {
            emit(Result.Error(message = "병원 정보를 찾을 수 없습니다."))
        }
    }

    companion object {
        private val sampleHospitals = listOf(
            Hospital(
                id = "sample-1",
                name = "부산 샘플 병원",
                specialties = listOf("피부·미용"),
                address = "부산광역시 해운대구",
                latitude = 35.1587,
                longitude = 129.1604,
                phoneNumber = "051-000-0000",
                homepageUrl = null,
                supportedLanguages = listOf("en", "ja", "zh"),
                description = "실제 데이터 연동 전 표시되는 샘플 병원입니다.",
                imageUrl = null,
                lastModified = null
            )
        )
    }
}
