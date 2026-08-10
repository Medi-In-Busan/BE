package com.mediinbusan.app.core.i18n

import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.datastore.SupportedLanguage

/**
 * MedicalCategory.label(한국어)은 검색 필터 선택 상태/서버 specialties 파라미터의 식별자로도
 * 쓰이고 있어 그대로 두고(core/datastore/UserPreferencesRepository.kt, HospitalSearchListViewModel.kt
 * 참고), 화면에 실제로 그릴 때만 이 함수로 현재 언어에 맞는 표시 문구를 가져온다.
 */
fun MedicalCategory.translatedLabel(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> label
    SupportedLanguage.EN -> when (this) {
        MedicalCategory.SKIN_BEAUTY -> "Skin & Beauty"
        MedicalCategory.HEALTH_CHECKUP -> "Health Checkup"
        MedicalCategory.DENTAL -> "Dental"
        MedicalCategory.ORIENTAL_MEDICINE -> "Oriental Medicine"
        MedicalCategory.REHABILITATION -> "Rehabilitation"
        MedicalCategory.WELLNESS -> "Wellness"
        MedicalCategory.PLASTIC_SURGERY -> "Plastic Surgery"
        MedicalCategory.OBSTETRICS_GYNECOLOGY -> "Obstetrics & Gynecology"
        MedicalCategory.OPHTHALMOLOGY -> "Ophthalmology"
        MedicalCategory.ETC -> "Other"
    }
    SupportedLanguage.ZH -> when (this) {
        MedicalCategory.SKIN_BEAUTY -> "皮肤美容"
        MedicalCategory.HEALTH_CHECKUP -> "健康体检"
        MedicalCategory.DENTAL -> "牙科"
        MedicalCategory.ORIENTAL_MEDICINE -> "韩医(中医)"
        MedicalCategory.REHABILITATION -> "康复"
        MedicalCategory.WELLNESS -> "养生"
        MedicalCategory.PLASTIC_SURGERY -> "整形外科"
        MedicalCategory.OBSTETRICS_GYNECOLOGY -> "妇产科"
        MedicalCategory.OPHTHALMOLOGY -> "眼科"
        MedicalCategory.ETC -> "其他"
    }
    SupportedLanguage.JA -> when (this) {
        MedicalCategory.SKIN_BEAUTY -> "皮膚・美容"
        MedicalCategory.HEALTH_CHECKUP -> "健康診断"
        MedicalCategory.DENTAL -> "歯科"
        MedicalCategory.ORIENTAL_MEDICINE -> "韓方(漢方)"
        MedicalCategory.REHABILITATION -> "リハビリ"
        MedicalCategory.WELLNESS -> "ウェルネス"
        MedicalCategory.PLASTIC_SURGERY -> "美容外科"
        MedicalCategory.OBSTETRICS_GYNECOLOGY -> "産婦人科"
        MedicalCategory.OPHTHALMOLOGY -> "眼科"
        MedicalCategory.ETC -> "その他"
    }
}
