package com.mediinbusan.app.core.i18n

/**
 * 장소 상세(S-07 웰니스 장소 / 부산 관광 카탈로그) 두 화면이 공유하는 큐레이션 섹션 문구.
 *
 * core/common/PlaceCareProfile.kt와 짝을 이룬다 — 그쪽은 문자열이 없는 순수 도메인이고,
 * 표시 문구는 전부 여기에 enum 이름을 키로 하는 맵으로 들어온다(NearbyStrings.placeTypeLabels가
 * 이미 쓰고 있는 관례). 모든 enum 값을 채우므로 조회가 실패할 일은 없지만 호출부는 `?: ""`로 방어한다.
 *
 * 부산 명소별 개별 문구는 분량이 커서 BusanHighlightStrings.kt로 따로 뺐다.
 */
data class PlaceCurationStrings(
    val atAGlanceTitle: String,
    val recoveryFitLabel: String,
    val activityLevelLabel: String,
    val settingLabel: String,
    val stayTimeLabel: String,
    /** "%d~%d분" 형태. 권장 체류 시간 범위. */
    val stayTimeRangeFormat: String,
    /** 숙소처럼 체류 시간을 못 박는 게 의미 없는 유형에 쓴다(PlaceCareProfile의 0~0). */
    val stayTimeFlexible: String,
    /** 키는 RecoveryFit.name. */
    val recoveryFitLabels: Map<String, String>,
    /** 키는 ActivityLevel.name. */
    val activityLevelLabels: Map<String, String>,
    /** 키는 PlaceSetting.name. */
    val settingLabels: Map<String, String>,
    /** 키는 PlaceCautionKey.name. */
    val cautionLabels: Map<String, String>,
    val mediTipTitle: String,
    val bestTimeLabel: String,
    /**
     * 소개 문구 폴백. 키는 PlaceType.name.
     * 웰니스 API의 description이 비어 있거나 URL·`EX0000` 코드로 오는 장소가 많아, 예전엔
     * 소개 섹션이 통째로 사라졌다 — 이제 이 문구로라도 항상 채운다.
     */
    val typeIntroFallbacks: Map<String, String>,
    val travelerHelpTitle: String,
    val travelerHelpTourLineLabel: String,
    val travelerHelpTourLineNumber: String,
    val travelerHelpTourLineDescription: String,
    val travelerHelpEmergencyLabel: String,
    val travelerHelpEmergencyNumber: String,
    val travelerHelpEmergencyDescription: String,
    val travelerHelpPaymentLabel: String,
    val travelerHelpPaymentDescription: String
) {
    companion object {
        val Ko = PlaceCurationStrings(
            atAGlanceTitle = "한눈에 보기",
            recoveryFitLabel = "방문 시기",
            activityLevelLabel = "활동 강도",
            settingLabel = "환경",
            stayTimeLabel = "권장 체류",
            stayTimeRangeFormat = "%d~%d분",
            stayTimeFlexible = "일정에 맞춰",
            recoveryFitLabels = mapOf(
                "IMMEDIATE" to "진료 당일도 무난",
                "AFTER_FEW_DAYS" to "회복 2~3일 후",
                "AFTER_RECOVERY" to "회복 후 권장"
            ),
            activityLevelLabels = mapOf(
                "LIGHT" to "가볍게",
                "MODERATE" to "보통",
                "ACTIVE" to "활동적"
            ),
            settingLabels = mapOf(
                "INDOOR" to "실내",
                "OUTDOOR" to "실외",
                "MIXED" to "실내·실외"
            ),
            cautionLabels = mapOf(
                "HEAT_EXPOSURE" to "사우나·온열 이용은 시술 종류에 따라 제한될 수 있습니다.",
                "UV_EXPOSURE" to "야외 자외선 노출이 길어질 수 있으니 모자·자외선 차단을 챙기세요.",
                "LONG_WALKING" to "걷는 구간이 길어 중간에 앉아 쉴 지점을 미리 정해두면 좋습니다.",
                "STAIRS_SLOPE" to "계단과 경사가 있어 이동이 불편할 수 있습니다.",
                "WATER_CONTACT" to "물에 닿는 활동은 상처·시술 부위 상태를 먼저 확인하세요.",
                "FASTING" to "진료 전 금식 안내가 있었는지 먼저 확인하세요.",
                "SPICY_FOOD" to "맵고 자극적인 음식은 회복 중 부담이 될 수 있습니다.",
                "CROWDED_HOURS" to "혼잡한 시간대를 피하면 훨씬 편하게 둘러볼 수 있습니다.",
                "LONG_STAY" to "체류가 길어지기 쉬우니 휴식 시간을 함께 잡아두세요.",
                "CASH_ONLY_STALLS" to "현금만 받는 노점이 섞여 있어 소액 현금을 챙기면 편합니다."
            ),
            mediTipTitle = "메디인부산 팁",
            bestTimeLabel = "추천 시간대",
            typeIntroFallbacks = mapOf(
                "TOURIST_ATTRACTION" to "부산을 대표하는 관광 권역에 있는 장소입니다. 진료 일정 사이 여유 시간에 짧게 들르기 좋습니다.",
                "RESTAURANT" to "가볍게 끼니를 해결하거나 대기 시간에 쉬어가기 좋은 카페·음식점입니다.",
                "SHOPPING" to "실내 이동 중심이라 날씨 영향을 덜 받는 쇼핑 권역입니다.",
                "LODGING" to "병원 이동 동선을 짧게 유지하는 데 도움이 되는 숙박 시설입니다.",
                "SPA" to "휴식 중심 일정에 넣기 좋은 스파·온열 시설입니다.",
                "WALK" to "무리하지 않는 회복형 일정에 맞는 산책 코스입니다.",
                "OTHER" to "병원 주변에서 짧게 들러 쉬어가기 좋은 장소입니다."
            ),
            travelerHelpTitle = "여행자 편의",
            travelerHelpTourLineLabel = "관광통역안내 1330",
            travelerHelpTourLineNumber = "1330",
            travelerHelpTourLineDescription = "한국관광공사가 운영하는 24시간 안내 전화로, 영어·중국어·일본어 통역 안내를 받을 수 있습니다.",
            travelerHelpEmergencyLabel = "응급 상황 119",
            travelerHelpEmergencyNumber = "119",
            travelerHelpEmergencyDescription = "응급 의료·구급차 요청은 119입니다. 외국어 통역 연결도 지원합니다.",
            travelerHelpPaymentLabel = "결제·이동",
            travelerHelpPaymentDescription = "대부분의 시설에서 신용카드 결제가 가능하고, 지하철·버스는 교통카드 한 장으로 환승할 수 있습니다."
        )
        val En = PlaceCurationStrings(
            atAGlanceTitle = "At a glance",
            recoveryFitLabel = "When to visit",
            activityLevelLabel = "Effort",
            settingLabel = "Setting",
            stayTimeLabel = "Suggested stay",
            stayTimeRangeFormat = "%d–%d min",
            stayTimeFlexible = "As your schedule allows",
            recoveryFitLabels = mapOf(
                "IMMEDIATE" to "Fine on treatment day",
                "AFTER_FEW_DAYS" to "2–3 days into recovery",
                "AFTER_RECOVERY" to "Best after recovery"
            ),
            activityLevelLabels = mapOf(
                "LIGHT" to "Light",
                "MODERATE" to "Moderate",
                "ACTIVE" to "Active"
            ),
            settingLabels = mapOf(
                "INDOOR" to "Indoor",
                "OUTDOOR" to "Outdoor",
                "MIXED" to "Indoor & outdoor"
            ),
            cautionLabels = mapOf(
                "HEAT_EXPOSURE" to "Saunas and heat treatments may be restricted depending on your procedure.",
                "UV_EXPOSURE" to "Sun exposure can run long outdoors — bring a hat and sun protection.",
                "LONG_WALKING" to "There is a fair amount of walking; decide in advance where you can sit and rest.",
                "STAIRS_SLOPE" to "Stairs and slopes can make getting around harder.",
                "WATER_CONTACT" to "Check the state of any wound or treated area before activities involving water.",
                "FASTING" to "Check first whether you were told to fast before your appointment.",
                "SPICY_FOOD" to "Spicy, heavily seasoned food can be hard on you while recovering.",
                "CROWDED_HOURS" to "Avoiding peak hours makes the visit far more comfortable.",
                "LONG_STAY" to "Visits here tend to run long — plan rest breaks as well.",
                "CASH_ONLY_STALLS" to "Some stalls take cash only, so a little cash on hand helps."
            ),
            mediTipTitle = "MediIn Busan tip",
            bestTimeLabel = "Best time",
            typeIntroFallbacks = mapOf(
                "TOURIST_ATTRACTION" to "A spot in one of Busan's signature sightseeing areas — easy to fit into a gap between appointments.",
                "RESTAURANT" to "A cafe or restaurant for a light meal, or a break while you wait.",
                "SHOPPING" to "A mostly indoor shopping area, so the weather matters less.",
                "LODGING" to "Accommodation that helps keep your trips to and from the hospital short.",
                "SPA" to "A spa and heat facility that fits a rest-focused day.",
                "WALK" to "A walking route suited to a gentle, recovery-paced schedule.",
                "OTHER" to "An easy place near the hospital to stop by and take a break."
            ),
            travelerHelpTitle = "Traveler essentials",
            travelerHelpTourLineLabel = "Tourist help line 1330",
            travelerHelpTourLineNumber = "1330",
            travelerHelpTourLineDescription = "The Korea Tourism Organization's 24-hour line, with interpretation in English, Chinese, and Japanese.",
            travelerHelpEmergencyLabel = "Emergency 119",
            travelerHelpEmergencyNumber = "119",
            travelerHelpEmergencyDescription = "Call 119 for emergency medical care or an ambulance. Interpretation support is available.",
            travelerHelpPaymentLabel = "Payment & transit",
            travelerHelpPaymentDescription = "Credit cards work almost everywhere, and one transit card covers subway and bus transfers."
        )
        val Zh = PlaceCurationStrings(
            atAGlanceTitle = "一览",
            recoveryFitLabel = "建议时机",
            activityLevelLabel = "活动强度",
            settingLabel = "环境",
            stayTimeLabel = "建议停留",
            stayTimeRangeFormat = "%d~%d分钟",
            stayTimeFlexible = "按行程安排",
            recoveryFitLabels = mapOf(
                "IMMEDIATE" to "就诊当天也可以",
                "AFTER_FEW_DAYS" to "恢复2~3天后",
                "AFTER_RECOVERY" to "建议恢复后"
            ),
            activityLevelLabels = mapOf(
                "LIGHT" to "轻松",
                "MODERATE" to "适中",
                "ACTIVE" to "较活跃"
            ),
            settingLabels = mapOf(
                "INDOOR" to "室内",
                "OUTDOOR" to "户外",
                "MIXED" to "室内·户外"
            ),
            cautionLabels = mapOf(
                "HEAT_EXPOSURE" to "桑拿和热疗可能因治疗种类而受限。",
                "UV_EXPOSURE" to "户外紫外线照射时间可能较长，请备好帽子和防晒用品。",
                "LONG_WALKING" to "步行路段较长，建议提前想好中途休息的地点。",
                "STAIRS_SLOPE" to "有台阶和坡道，移动可能不太方便。",
                "WATER_CONTACT" to "涉水活动前请先确认伤口或治疗部位的状态。",
                "FASTING" to "请先确认就诊前是否有禁食要求。",
                "SPICY_FOOD" to "辛辣刺激的食物在恢复期间可能造成负担。",
                "CROWDED_HOURS" to "避开拥挤时段可以逛得更舒适。",
                "LONG_STAY" to "停留时间容易变长，建议同时安排休息时间。",
                "CASH_ONLY_STALLS" to "部分摊位只收现金，随身带些零钱会更方便。"
            ),
            mediTipTitle = "MediIn Busan 小贴士",
            bestTimeLabel = "推荐时段",
            typeIntroFallbacks = mapOf(
                "TOURIST_ATTRACTION" to "位于釜山代表性观光区域的景点，适合在诊疗行程之间短暂游览。",
                "RESTAURANT" to "适合简单用餐或在等待时间休息的咖啡厅·餐厅。",
                "SHOPPING" to "以室内移动为主的购物区域，受天气影响较小。",
                "LODGING" to "有助于缩短往返医院路程的住宿设施。",
                "SPA" to "适合安排在休息日程中的水疗·热疗设施。",
                "WALK" to "适合不勉强的康复型行程的散步路线。",
                "OTHER" to "医院周边适合短暂停留休息的场所。"
            ),
            travelerHelpTitle = "旅行便利信息",
            travelerHelpTourLineLabel = "旅游咨询热线 1330",
            travelerHelpTourLineNumber = "1330",
            travelerHelpTourLineDescription = "韩国观光公社运营的24小时咨询电话，提供英语、汉语、日语翻译服务。",
            travelerHelpEmergencyLabel = "紧急情况 119",
            travelerHelpEmergencyNumber = "119",
            travelerHelpEmergencyDescription = "急救医疗和救护车请拨打119，同时支持外语翻译连线。",
            travelerHelpPaymentLabel = "支付·交通",
            travelerHelpPaymentDescription = "大部分设施都可以刷信用卡，地铁和公交使用一张交通卡即可换乘。"
        )
        val Ja = PlaceCurationStrings(
            atAGlanceTitle = "ひと目でわかる",
            recoveryFitLabel = "訪問の目安",
            activityLevelLabel = "活動量",
            settingLabel = "環境",
            stayTimeLabel = "目安の滞在",
            stayTimeRangeFormat = "%d~%d分",
            stayTimeFlexible = "予定に合わせて",
            recoveryFitLabels = mapOf(
                "IMMEDIATE" to "診療当日でも無理なく",
                "AFTER_FEW_DAYS" to "回復2~3日後",
                "AFTER_RECOVERY" to "回復後がおすすめ"
            ),
            activityLevelLabels = mapOf(
                "LIGHT" to "軽め",
                "MODERATE" to "ふつう",
                "ACTIVE" to "アクティブ"
            ),
            settingLabels = mapOf(
                "INDOOR" to "屋内",
                "OUTDOOR" to "屋外",
                "MIXED" to "屋内・屋外"
            ),
            cautionLabels = mapOf(
                "HEAT_EXPOSURE" to "サウナ・温熱の利用は施術の種類によって制限される場合があります。",
                "UV_EXPOSURE" to "屋外で紫外線を浴びる時間が長くなりがちです。帽子や日焼け対策をご用意ください。",
                "LONG_WALKING" to "歩く区間が長いため、途中で座って休める場所を決めておくと安心です。",
                "STAIRS_SLOPE" to "階段や坂があり、移動しにくい場合があります。",
                "WATER_CONTACT" to "水に触れる活動の前に、傷や施術部位の状態を確認してください。",
                "FASTING" to "診療前の絶食の案内がなかったか、先に確認してください。",
                "SPICY_FOOD" to "辛くて刺激の強い食事は回復中の負担になることがあります。",
                "CROWDED_HOURS" to "混雑する時間帯を避けると、ぐっと快適に回れます。",
                "LONG_STAY" to "滞在が長くなりやすいので、休憩時間も一緒に確保しましょう。",
                "CASH_ONLY_STALLS" to "現金のみの露店もあるため、少額の現金があると便利です。"
            ),
            mediTipTitle = "メディイン釜山のヒント",
            bestTimeLabel = "おすすめの時間帯",
            typeIntroFallbacks = mapOf(
                "TOURIST_ATTRACTION" to "釜山を代表する観光エリアにあるスポットです。診療の合間の空き時間に立ち寄りやすい場所です。",
                "RESTAURANT" to "軽く食事をしたり、待ち時間に休んだりできるカフェ・飲食店です。",
                "SHOPPING" to "屋内移動が中心で、天候の影響を受けにくいショッピングエリアです。",
                "LODGING" to "病院への移動を短く保つのに役立つ宿泊施設です。",
                "SPA" to "休息中心の予定に組み込みやすいスパ・温熱施設です。",
                "WALK" to "無理のない回復ペースの予定に合う散歩コースです。",
                "OTHER" to "病院周辺で短く立ち寄って休むのにちょうどよい場所です。"
            ),
            travelerHelpTitle = "旅行者向け案内",
            travelerHelpTourLineLabel = "観光通訳案内 1330",
            travelerHelpTourLineNumber = "1330",
            travelerHelpTourLineDescription = "韓国観光公社が運営する24時間の案内電話で、英語・中国語・日本語の通訳案内を受けられます。",
            travelerHelpEmergencyLabel = "緊急時 119",
            travelerHelpEmergencyNumber = "119",
            travelerHelpEmergencyDescription = "救急医療・救急車の要請は119です。外国語の通訳対応もあります。",
            travelerHelpPaymentLabel = "支払い・移動",
            travelerHelpPaymentDescription = "ほとんどの施設でクレジットカードが使え、地下鉄とバスは交通カード1枚で乗り換えできます。"
        )
    }
}
