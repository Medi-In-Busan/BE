package com.mediinbusan.app.data.hospital

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface HospitalRepository {
    fun getHospitals(medicalPurpose: MedicalCategory?, languageCode: String): Flow<Result<List<Hospital>>>
    fun getHospitalDetail(hospitalId: String, languageCode: String): Flow<Result<Hospital>>
}
