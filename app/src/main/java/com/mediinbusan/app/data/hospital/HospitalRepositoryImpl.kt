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
                id = "hospital-1",
                name = "부산성모병원",
                specialties = listOf("피부·미용", "건강검진"),
                address = "부산 해운대구 센텀중앙로 90",
                latitude = 35.1691,
                longitude = 129.1315,
                phoneNumber = "051-123-4567",
                homepageUrl = "www.bsmc.kr",
                supportedLanguages = listOf("en", "ja", "zh"),
                description = "부산성모병원은 피부미용 및 건강검진 전문 의료진이 상주하는 종합 의료기관입니다. " +
                    "외국인 환자를 위한 다국어 통역 및 안내 서비스를 제공합니다.",
                imageUrl = null,
                lastModified = null,
                imageUrls = emptyList(),
                openingHours = "평일 09:00 - 18:00",
                isOpen = true
            ),
            Hospital(
                id = "hospital-2",
                name = "해운대백병원",
                specialties = listOf("건강검진"),
                address = "부산 해운대구 좌동순환로 435",
                latitude = 35.1657,
                longitude = 129.1756,
                phoneNumber = "051-234-5678",
                homepageUrl = "www.hdpaik.kr",
                supportedLanguages = listOf("en"),
                description = "해운대백병원은 정밀 건강검진 프로그램을 전문으로 하는 종합병원입니다.",
                imageUrl = null,
                lastModified = null,
                imageUrls = emptyList(),
                openingHours = "평일 08:30 - 17:30",
                isOpen = true
            ),
            Hospital(
                id = "hospital-3",
                name = "부산대치과병원",
                specialties = listOf("치과"),
                address = "부산 서구 구덕로 179",
                latitude = 35.1004,
                longitude = 129.0165,
                phoneNumber = "051-345-6789",
                homepageUrl = null,
                supportedLanguages = listOf("en", "zh"),
                description = "부산대치과병원은 임플란트·교정 등 전 진료과목을 갖춘 대학병원급 치과 전문기관입니다.",
                imageUrl = null,
                lastModified = null,
                imageUrls = emptyList(),
                openingHours = "평일 09:00 - 17:00, 토 09:00 - 13:00",
                isOpen = false
            ),
            Hospital(
                id = "hospital-4",
                name = "부산 예시 한방클리닉",
                specialties = listOf("한방"),
                address = "부산 부산진구 중앙대로 668",
                latitude = 35.1579,
                longitude = 129.0597,
                phoneNumber = "051-456-7890",
                homepageUrl = null,
                supportedLanguages = listOf("en", "ja"),
                description = "실제 데이터 연동 전 표시되는 샘플 병원입니다.",
                imageUrl = null,
                lastModified = null,
                imageUrls = emptyList(),
                openingHours = "평일 09:00 - 18:00",
                isOpen = true
            )
        )
    }
}
