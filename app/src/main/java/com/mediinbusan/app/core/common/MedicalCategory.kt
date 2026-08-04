package com.mediinbusan.app.core.common

import androidx.annotation.DrawableRes
import com.mediinbusan.app.R

/** 홈 의료목적 선택 / 검색 필터 칩 등에서 공통으로 쓰는 의료 태그. 라벨·아이콘을 이 enum 하나로 관리한다. */
enum class MedicalCategory(val label: String, @param:DrawableRes val iconRes: Int) {
    SKIN_BEAUTY("피부·미용", R.drawable.select_skin),
    HEALTH_CHECKUP("건강검진", R.drawable.select_healthcheck),
    DENTAL("치과", R.drawable.select_tooth),
    ORIENTAL_MEDICINE("한방", R.drawable.select_hanbang),
    REHABILITATION("재활", R.drawable.select_recover),
    WELLNESS("웰니스", R.drawable.select_wellness),
    PLASTIC_SURGERY("성형외과", R.drawable.select_face),
    OBSTETRICS_GYNECOLOGY("산부인과", R.drawable.select_baby),
    OPHTHALMOLOGY("안과", R.drawable.select_eye),
    ETC("기타", R.drawable.select_etc)
}
