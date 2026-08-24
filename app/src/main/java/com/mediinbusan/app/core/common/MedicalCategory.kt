package com.mediinbusan.app.core.common

import androidx.annotation.DrawableRes
import com.mediinbusan.app.R

/** 홈 의료목적 선택 / 검색 필터 칩 등에서 공통으로 쓰는 의료 태그. 라벨·아이콘을 이 enum 하나로 관리한다. */
enum class MedicalCategory(
    val label: String,
    @param:DrawableRes val iconRes: Int,
    // 병원 목록 카드 썸네일 전용 사진. hospital_wellness 원본이 없어 WELLNESS는 ETC 사진으로 폴백한다.
    @param:DrawableRes val hospitalPhotoRes: Int
) {
    SKIN_BEAUTY("피부·미용", R.drawable.select_skin, R.drawable.hospital_skin),
    HEALTH_CHECKUP("건강검진", R.drawable.select_healthcheck, R.drawable.hospital_healthcheck),
    DENTAL("치과", R.drawable.select_tooth, R.drawable.hospital_tooth),
    ORIENTAL_MEDICINE("한방", R.drawable.select_hanbang, R.drawable.hospital_hanbang),
    REHABILITATION("재활", R.drawable.select_recover, R.drawable.hospital_recover),
    WELLNESS("웰니스", R.drawable.select_wellness, R.drawable.hospital_etc),
    PLASTIC_SURGERY("성형외과", R.drawable.select_face, R.drawable.hospital_plastic),
    OBSTETRICS_GYNECOLOGY("산부인과", R.drawable.select_baby, R.drawable.hospital_baby),
    OPHTHALMOLOGY("안과", R.drawable.select_eye, R.drawable.hospital_eye),
    ETC("기타", R.drawable.select_etc, R.drawable.hospital_etc)
}

// 병원 이름에 특정 진료과 키워드가 포함되어 있으면 태그보다 우선해서 사진을 고른다(2순위).
// enum 선언 순서대로 검사하므로, 여러 키워드가 동시에 걸리는 이름이 있으면 먼저 선언된 카테고리가 이긴다.
private val hospitalTitleKeywords: Map<MedicalCategory, List<String>> = mapOf(
    MedicalCategory.SKIN_BEAUTY to listOf("피부과"),
    MedicalCategory.HEALTH_CHECKUP to listOf("건강검진"),
    MedicalCategory.DENTAL to listOf("치과"),
    MedicalCategory.ORIENTAL_MEDICINE to listOf("한의원", "한방병원"),
    MedicalCategory.REHABILITATION to listOf("재활"),
    MedicalCategory.PLASTIC_SURGERY to listOf("성형외과"),
    MedicalCategory.OBSTETRICS_GYNECOLOGY to listOf("산부인과"),
    MedicalCategory.OPHTHALMOLOGY to listOf("안과")
)

/**
 * 병원 목록 카드 썸네일에 쓸 사진을 고른다.
 * 1순위: 태그에 ETC(기타)가 포함되면 무조건 ETC 사진.
 * 2순위: 병원 이름에 특정 진료과 키워드가 있으면 그 사진.
 * 3순위: 보유 태그 중 enum 선언 순서상 가장 먼저 나오는 카테고리의 사진(태그가 없거나 매칭 안 되면 ETC).
 */
fun resolveHospitalThumbnailRes(name: String, specialtyLabels: List<String>): Int {
    if (specialtyLabels.contains(MedicalCategory.ETC.label)) return MedicalCategory.ETC.hospitalPhotoRes

    val byTitle = MedicalCategory.entries.firstOrNull { category ->
        hospitalTitleKeywords[category]?.any { keyword -> name.contains(keyword) } == true
    }
    if (byTitle != null) return byTitle.hospitalPhotoRes

    val byTag = specialtyLabels
        .mapNotNull { label -> MedicalCategory.entries.find { it.label == label } }
        .minByOrNull { it.ordinal }
    return (byTag ?: MedicalCategory.ETC).hospitalPhotoRes
}
