package com.mediinbusan.app.feature.home

import androidx.annotation.DrawableRes
import com.mediinbusan.app.R
import com.mediinbusan.app.core.datastore.SupportedLanguages
import com.mediinbusan.app.data.hospital.Hospital

data class HomeUiState(
    val medicalPurposes: List<MedicalPurposeItem> = MedicalPurposeItem.DEFAULTS,
    val selectedPurpose: String? = null,
    val languageCode: String = SupportedLanguages.DEFAULT,
    val quickLinks: List<QuickLinkItem> = QuickLinkItem.DEFAULTS,
    val recommendedHospitals: List<Hospital> = emptyList(),
    val favoriteHospitalIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class MedicalPurposeItem(
    val label: String,
    @param:DrawableRes val iconRes: Int
) {
    companion object {
        val DEFAULTS = listOf(
            MedicalPurposeItem("피부·미용", R.drawable.select_skin),
            MedicalPurposeItem("건강검진", R.drawable.select_healthcheck),
            MedicalPurposeItem("치과", R.drawable.select_tooth),
            MedicalPurposeItem("한방", R.drawable.select_hanbang),
            MedicalPurposeItem("재활", R.drawable.select_recover),
            MedicalPurposeItem("웰니스", R.drawable.select_wellness)
        )
    }
}

enum class QuickLinkType { HOSPITAL_LIST, GUIDE, WELLNESS, MAP, SELF_DIAGNOSIS, FAVORITE }

data class QuickLinkItem(
    val type: QuickLinkType,
    val label: String,
    @param:DrawableRes val iconRes: Int
) {
    companion object {
        val DEFAULTS = listOf(
            QuickLinkItem(QuickLinkType.HOSPITAL_LIST, "의료기관 찾기", R.drawable.direct_findhospital),
            QuickLinkItem(QuickLinkType.GUIDE, "의료 이용 가이드", R.drawable.direct_guide),
            QuickLinkItem(QuickLinkType.WELLNESS, "추천 웰니스", R.drawable.direct_recommendwellness),
            QuickLinkItem(QuickLinkType.MAP, "지도에서 보기", R.drawable.direct_findmap),
            // TODO: SELF_DIAGNOSIS는 route 미구현(별도 이슈)
            QuickLinkItem(QuickLinkType.SELF_DIAGNOSIS, "진단하기", R.drawable.direct_testme),
            QuickLinkItem(QuickLinkType.FAVORITE, "즐겨찾기", R.drawable.direct_heart)
        )
    }
}
