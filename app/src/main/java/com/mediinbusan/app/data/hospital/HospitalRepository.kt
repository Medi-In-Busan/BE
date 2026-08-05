package com.mediinbusan.app.data.hospital

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface HospitalRepository {
    // keyword는 이름/주소 OR 부분일치, specialties는 IN(OR) 조건. 백엔드 GET /api/hospitals 그대로.
    fun getHospitals(
        keyword: String? = null,
        specialties: List<MedicalCategory> = emptyList(),
        languageCode: String
    ): Flow<Result<List<Hospital>>>
    fun getHospitalDetail(hospitalId: String, languageCode: String): Flow<Result<Hospital>>
}
