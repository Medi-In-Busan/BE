package com.mediinbusan.app.core.i18n

import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.data.place.PlaceCategory
import com.mediinbusan.app.data.place.PlaceType

/**
 * MedicalCategoryStrings.kt(MedicalCategory.translatedLabel)와 같은 패턴 — PlaceType 자체는
 * core/i18n이 아니라 data/place에 있는 순수 도메인 enum이라 언어별 표시 문구를 여기서 별도로 매핑한다.
 * PlaceDetailScreen(S-07 상세)과 NearbyScreen(S-07 목록)이 각자 파일 안에 하드코딩 한글로 들고 있던
 * PlaceType.label/recoveryHint 중 상세 화면 쪽을 이 공용 함수로 옮긴다 — 목록 화면 쪽 소급 정비는
 * NearbyStrings.kt 문서 주석과 같은 이유로 별도 스코프로 남겨둔다.
 */
fun PlaceType.translatedLabel(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> when (this) {
        PlaceType.TOURIST_ATTRACTION -> "관광지"
        PlaceType.RESTAURANT -> "카페·맛집"
        PlaceType.SHOPPING -> "쇼핑"
        PlaceType.LODGING -> "숙소"
        PlaceType.SPA -> "스파"
        PlaceType.WALK -> "산책"
        PlaceType.OTHER -> "기타"
    }
    SupportedLanguage.EN -> when (this) {
        PlaceType.TOURIST_ATTRACTION -> "Tourist Attraction"
        PlaceType.RESTAURANT -> "Cafe & Restaurant"
        PlaceType.SHOPPING -> "Shopping"
        PlaceType.LODGING -> "Lodging"
        PlaceType.SPA -> "Spa"
        PlaceType.WALK -> "Walk"
        PlaceType.OTHER -> "Other"
    }
    SupportedLanguage.ZH -> when (this) {
        PlaceType.TOURIST_ATTRACTION -> "旅游景点"
        PlaceType.RESTAURANT -> "咖啡·美食"
        PlaceType.SHOPPING -> "购物"
        PlaceType.LODGING -> "住宿"
        PlaceType.SPA -> "水疗"
        PlaceType.WALK -> "散步"
        PlaceType.OTHER -> "其他"
    }
    SupportedLanguage.JA -> when (this) {
        PlaceType.TOURIST_ATTRACTION -> "観光地"
        PlaceType.RESTAURANT -> "カフェ・グルメ"
        PlaceType.SHOPPING -> "ショッピング"
        PlaceType.LODGING -> "宿泊施設"
        PlaceType.SPA -> "スパ"
        PlaceType.WALK -> "散歩"
        PlaceType.OTHER -> "その他"
    }
}

/**
 * [PlaceCategory]의 언어별 표시 문구 — [translatedLabel]의 세부 분류판이다.
 *
 * [PlaceCategory.OTHER]는 "기타"가 아니라 **빈 문자열**을 돌려준다. OTHER는 실제로 "그 밖의 무언가"가
 * 아니라 "세부 분류를 모른다"는 뜻이고(재수집 전 장소, 아직 세분화 대상이 아닌 관광지·숙박·음식점이
 * 전부 여기 온다), 그런 장소가 대다수다 — 목록 절반에 "기타" 칩이 붙으면 정보가 아니라 잡음이 된다.
 * 호출부는 빈 문자열이면 [PlaceType.translatedLabel]로 되돌아간다.
 */
fun PlaceCategory.translatedLabel(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> when (this) {
        PlaceCategory.DEPARTMENT_STORE -> "백화점"
        PlaceCategory.DUTY_FREE -> "면세점"
        PlaceCategory.TRADITIONAL_MARKET -> "전통시장"
        PlaceCategory.LARGE_MART -> "대형마트"
        PlaceCategory.SPECIALTY_STORE -> "전문매장"
        PlaceCategory.LOCAL_PRODUCTS -> "특산물"
        PlaceCategory.CRAFT_WORKSHOP -> "공예·공방"
        PlaceCategory.OTHER -> ""
    }
    SupportedLanguage.EN -> when (this) {
        PlaceCategory.DEPARTMENT_STORE -> "Department Store"
        PlaceCategory.DUTY_FREE -> "Duty Free"
        PlaceCategory.TRADITIONAL_MARKET -> "Traditional Market"
        PlaceCategory.LARGE_MART -> "Supermarket"
        PlaceCategory.SPECIALTY_STORE -> "Specialty Store"
        PlaceCategory.LOCAL_PRODUCTS -> "Local Products"
        PlaceCategory.CRAFT_WORKSHOP -> "Craft Workshop"
        PlaceCategory.OTHER -> ""
    }
    SupportedLanguage.ZH -> when (this) {
        PlaceCategory.DEPARTMENT_STORE -> "百货商店"
        PlaceCategory.DUTY_FREE -> "免税店"
        PlaceCategory.TRADITIONAL_MARKET -> "传统市场"
        PlaceCategory.LARGE_MART -> "大型超市"
        PlaceCategory.SPECIALTY_STORE -> "专卖店"
        PlaceCategory.LOCAL_PRODUCTS -> "特产店"
        PlaceCategory.CRAFT_WORKSHOP -> "工艺工坊"
        PlaceCategory.OTHER -> ""
    }
    SupportedLanguage.JA -> when (this) {
        PlaceCategory.DEPARTMENT_STORE -> "百貨店"
        PlaceCategory.DUTY_FREE -> "免税店"
        PlaceCategory.TRADITIONAL_MARKET -> "伝統市場"
        PlaceCategory.LARGE_MART -> "大型スーパー"
        PlaceCategory.SPECIALTY_STORE -> "専門店"
        PlaceCategory.LOCAL_PRODUCTS -> "特産品店"
        PlaceCategory.CRAFT_WORKSHOP -> "工芸工房"
        PlaceCategory.OTHER -> ""
    }
}

/** RecoveryNoticeSection(PlaceDetailScreen.kt)의 "진료 전후 체크" 카드에 쓰는 장소 유형별 안내 문구. */
fun PlaceType.translatedRecoveryHint(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> when (this) {
        PlaceType.SPA -> "스파·온열 시설은 시술 종류에 따라 제한될 수 있어 이용 전 확인이 필요합니다."
        PlaceType.WALK -> "짧은 산책 중심으로 계획하고, 오래 걷는 일정은 컨디션을 보며 조절하세요."
        PlaceType.RESTAURANT -> "진료 전후 금식이나 자극적인 음식 제한이 있는지 먼저 확인하세요."
        PlaceType.SHOPPING -> "실내 이동은 편하지만 체류 시간이 길어질 수 있어 휴식 시간을 함께 잡는 편이 좋습니다."
        PlaceType.TOURIST_ATTRACTION -> "혼잡한 시간대와 장시간 야외 활동을 피하면 더 편안하게 방문할 수 있습니다."
        PlaceType.LODGING -> "병원과 가까운 숙소는 이동 부담을 줄여 회복 일정에 도움이 됩니다."
        PlaceType.OTHER -> "방문 전 이동 시간과 체류 시간을 짧게 잡아 컨디션을 우선하세요."
    }
    SupportedLanguage.EN -> when (this) {
        PlaceType.SPA -> "Spa and heat treatments may be restricted depending on your procedure — check before you go."
        PlaceType.WALK -> "Plan short walks and pace longer ones based on how you're feeling."
        PlaceType.RESTAURANT -> "Check first whether you need to fast or avoid spicy food before or after treatment."
        PlaceType.SHOPPING -> "Indoor shopping is easy on the body, but visits can run long — plan rest breaks too."
        PlaceType.TOURIST_ATTRACTION -> "Avoid crowded hours and long outdoor activity for a more comfortable visit."
        PlaceType.LODGING -> "Staying close to the hospital reduces travel strain and helps your recovery schedule."
        PlaceType.OTHER -> "Keep travel and stay times short before your visit and prioritize your condition."
    }
    SupportedLanguage.ZH -> when (this) {
        PlaceType.SPA -> "水疗·热疗设施可能因治疗种类而受限，使用前请先确认。"
        PlaceType.WALK -> "建议以短距离散步为主，较长的行程请根据身体状况调整。"
        PlaceType.RESTAURANT -> "请先确认就诊前后是否需要禁食或避免刺激性食物。"
        PlaceType.SHOPPING -> "室内购物较为轻松，但停留时间可能较长，建议安排休息时间。"
        PlaceType.TOURIST_ATTRACTION -> "避开拥挤时段和长时间户外活动，可以让您的行程更舒适。"
        PlaceType.LODGING -> "选择靠近医院的住宿可以减轻移动负担，有助于恢复行程。"
        PlaceType.OTHER -> "出行前请尽量缩短移动和停留时间，优先考虑身体状况。"
    }
    SupportedLanguage.JA -> when (this) {
        PlaceType.SPA -> "スパ・温熱施設は施術の種類によって制限される場合があるため、利用前にご確認ください。"
        PlaceType.WALK -> "短い散歩を中心に計画し、長い行程は体調を見ながら調整してください。"
        PlaceType.RESTAURANT -> "診療前後に絶食や刺激物の制限がないか、事前に確認してください。"
        PlaceType.SHOPPING -> "屋内移動は楽ですが滞在時間が長くなりがちなので、休憩時間も一緒に計画しましょう。"
        PlaceType.TOURIST_ATTRACTION -> "混雑する時間帯や長時間の屋外活動を避けると、より快適に訪問できます。"
        PlaceType.LODGING -> "病院に近い宿泊施設は移動の負担を減らし、回復スケジュールに役立ちます。"
        PlaceType.OTHER -> "訪問前は移動時間と滞在時間を短めに設定し、体調を優先してください。"
    }
}
