package com.mediinbusan.app.feature.home

import androidx.annotation.DrawableRes
import com.mediinbusan.app.R
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.data.hospital.Hospital

data class HomeUiState(
    val medicalPurposes: List<MedicalCategory> = MedicalCategory.entries,
    val selectedPurpose: MedicalCategory? = null,
    val languageCode: String = SupportedLanguage.DEFAULT.code,
    val quickLinks: List<QuickLinkItem> = QuickLinkItem.DEFAULTS,
    val recommendedHospitals: List<Hospital> = emptyList(),
    val favoriteHospitalIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    // 로드 실패 여부와 서버 메시지를 분리한다. error가 null이어도 isError가 true면 화면에서
    // LocalAppStrings 기준으로 폴백 문구를 그려, 에러가 떠 있는 동안 언어를 바꿔도 즉시 반영되게 한다.
    val isError: Boolean = false,
    val error: String? = null
)

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
