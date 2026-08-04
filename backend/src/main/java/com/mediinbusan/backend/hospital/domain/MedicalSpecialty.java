package com.mediinbusan.backend.hospital.domain;

/**
 * Android 앱의 core/common/MedicalCategory.kt와 상수명을 동일하게 유지한다.
 * 한쪽만 바뀌면 프론트-백엔드 간 필터 값이 어긋나므로, 이 enum을 바꿀 때 앱 쪽도 같이 확인할 것.
 */
public enum MedicalSpecialty {
    SKIN_BEAUTY,
    HEALTH_CHECKUP,
    DENTAL,
    ORIENTAL_MEDICINE,
    REHABILITATION,
    WELLNESS,
    PLASTIC_SURGERY,
    OBSTETRICS_GYNECOLOGY,
    OPHTHALMOLOGY,
    ETC
}
