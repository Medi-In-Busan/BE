package com.mediinbusan.app.core.common

import com.mediinbusan.app.data.place.PlaceType

/**
 * 부산 대표 명소 큐레이션 레지스트리 — 메디인부산 고유 정적 데이터의 두 번째 축.
 *
 * [PlaceCareProfile]이 "어떤 장소가 와도 빈칸이 없게" 만드는 축이라면, 이쪽은 "유명한 장소는
 * 그 장소다운 이야기를 준다"는 축이다. 웰니스/관광 API가 내려주는 소개문은 비어 있거나
 * 이미지 URL·`EX0000` 같은 코드값이 그대로 실려 오는 경우가 많아서, 사람이 직접 쓴 한 줄
 * 소개와 진료 전후 관점의 팁을 앱이 들고 있다가 이름으로 찾아 붙인다.
 *
 * 여기엔 식별자와 매칭 키워드만 둔다(순수 도메인). 언어별 문구는
 * core/i18n/BusanHighlightStrings.kt의 [translatedCopy]가 갖는다 —
 * MedicalCategory(core/common) ↔ MedicalCategoryStrings(core/i18n)와 같은 분리다.
 */
enum class BusanHighlightId {
    // 해운대·센텀
    SPALAND_CENTUM,
    SHINSEGAE_CENTUM,
    BEXCO,
    BUSAN_CINEMA_CENTER,
    BUSAN_AQUARIUM,
    HAEUNDAE_BLUELINE_PARK,
    HAEUNDAE_BEACH,
    DONGBAEKSEOM,
    SONGJEONG_BEACH,

    // 광안·남구
    GWANGALLI_BEACH,
    IGIDAE,
    OERYUKDO_SKYWALK,
    HWANGNYEONGSAN,

    // 원도심·서구
    GAMCHEON_CULTURE_VILLAGE,
    JAGALCHI_MARKET,
    GUKJE_MARKET,
    GWANGBOK_ROAD,
    YONGDUSAN_BUSAN_TOWER,
    BOSUDONG_BOOK_STREET,
    CHORYANG_IBAGU_GIL,

    // 영도·사하
    TAEJONGDAE,
    HINYEOUL_CULTURE_VILLAGE,
    SONGDO_BEACH,
    DADAEPO_BEACH,
    EULSUKDO,

    // 동래·금정·북구
    HEOSIMCHEONG,
    DONGNAE_EUPSEONG,
    ONCHEONCHEON_CAFE_STREET,
    BEOMEOSA,
    SAMNAK_ECO_PARK,

    // 기장·기타
    HAEDONG_YONGGUNGSA,
    BUSAN_CITIZENS_PARK,
    MOCA_BUSAN
}

/**
 * 명소별 매칭 키워드. **반드시 다국어로 채운다.**
 *
 * 백엔드가 요청 언어로 장소 이름을 번역해 내려주고(backend WellnessDtoMapper), 관광 카탈로그도
 * PLACES_EN/JA/ZH 카테고리는 영·일·중 제목으로 온다 — 한국어 키워드만 두면 한국어로 볼 때만
 * 팁이 뜨고 정작 이 기능이 필요한 외국어 사용자에겐 안 뜬다.
 *
 * 키워드는 [normalizeForMatch]를 거친 형태로 비교하므로 여기서도 공백·구분자 없이 쓸 필요는
 * 없다(비교 직전에 양쪽 다 정규화한다). 중국어는 간체·번체를 함께 넣는다.
 *
 * ⚠️ 짧고 흔한 조각("해운대", "송도")은 넣지 않는다 — "해운대암소갈비집" 같은 식당이 해수욕장
 * 큐레이션을 물려받는다. 지역명 + 시설명이 붙은 형태까지만 키워드로 인정한다.
 */
private val highlightKeywords: Map<BusanHighlightId, List<String>> = mapOf(
    BusanHighlightId.SPALAND_CENTUM to listOf("스파랜드", "spaland", "スパランド", "水疗乐园", "水療樂園"),
    BusanHighlightId.SHINSEGAE_CENTUM to listOf(
        "신세계센텀시티", "센텀시티몰", "shinsegaecentum", "centumcity",
        "新世界百货", "新世界百貨", "シンセゲセンタムシティ", "センタムシティ"
    ),
    BusanHighlightId.BEXCO to listOf("벡스코", "bexco", "ベクスコ", "釜山会展中心", "釜山會展中心"),
    BusanHighlightId.BUSAN_CINEMA_CENTER to listOf(
        "영화의전당", "두레라움", "busancinemacenter", "cinemacenter",
        "映画の殿堂", "电影殿堂", "電影殿堂"
    ),
    BusanHighlightId.BUSAN_AQUARIUM to listOf(
        "아쿠아리움", "씨라이프", "sealife", "aquarium", "アクアリウム", "水族馆", "水族館"
    ),
    BusanHighlightId.HAEUNDAE_BLUELINE_PARK to listOf(
        "블루라인파크", "스카이캡슐", "해변열차", "bluelinepark", "skycapsule",
        "ブルーラインパーク", "스카이캡슐", "蓝线公园", "藍線公園"
    ),
    BusanHighlightId.HAEUNDAE_BEACH to listOf(
        "해운대해수욕장", "해운대해변", "해운대비치", "haeundaebeach",
        "海雲台海水浴場", "海云台海水浴场", "ヘウンデビーチ", "海雲台ビーチ"
    ),
    BusanHighlightId.DONGBAEKSEOM to listOf(
        "동백섬", "누리마루", "dongbaekisland", "dongbaekseom", "nurimaru",
        "冬柏岛", "冬柏島", "トンベクソム", "APEC世峰楼"
    ),
    BusanHighlightId.SONGJEONG_BEACH to listOf(
        "송정해수욕장", "송정해변", "songjeongbeach", "松亭海水浴場", "松亭海水浴场", "ソンジョンビーチ"
    ),
    BusanHighlightId.GWANGALLI_BEACH to listOf(
        "광안리해수욕장", "광안리해변", "광안대교", "gwangallibeach", "gwanganbridge", "diamondbridge",
        "広安里海水浴場", "広安大橋", "广安里海水浴场", "廣安里海水浴場", "广安大桥", "クァンアンリビーチ"
    ),
    BusanHighlightId.IGIDAE to listOf("이기대", "igidae", "二妓台", "二妓臺", "イギデ"),
    BusanHighlightId.OERYUKDO_SKYWALK to listOf(
        "오륙도", "스카이워크", "oryukdo", "skywalk", "五六岛", "五六島", "オリュクド"
    ),
    BusanHighlightId.HWANGNYEONGSAN to listOf(
        "황령산", "hwangnyeongsan", "荒岭山", "荒嶺山", "ファンリョンサン"
    ),
    BusanHighlightId.GAMCHEON_CULTURE_VILLAGE to listOf(
        "감천문화마을", "감천마을", "gamcheonculturevillage", "gamcheon",
        "甘川文化村", "カムチョン文化村"
    ),
    BusanHighlightId.JAGALCHI_MARKET to listOf(
        "자갈치", "jagalchi", "札嘎其", "チャガルチ"
    ),
    BusanHighlightId.GUKJE_MARKET to listOf(
        "국제시장", "biff광장", "biffsquare", "gukjemarket", "国际市场", "國際市場", "国際市場"
    ),
    BusanHighlightId.GWANGBOK_ROAD to listOf(
        "광복로", "남포동", "gwangbokro", "nampodong", "光复路", "光復路", "南浦洞", "ナンポドン"
    ),
    BusanHighlightId.YONGDUSAN_BUSAN_TOWER to listOf(
        "용두산", "부산타워", "yongdusan", "busantower", "龙头山", "龍頭山", "釜山タワー", "釜山塔"
    ),
    BusanHighlightId.BOSUDONG_BOOK_STREET to listOf(
        "보수동책방골목", "보수동", "bosudongbook", "宝水洞", "寶水洞", "ポスドン"
    ),
    BusanHighlightId.CHORYANG_IBAGU_GIL to listOf(
        "이바구길", "초량이바구", "168계단", "ibagugil", "草梁", "チョリャン"
    ),
    BusanHighlightId.TAEJONGDAE to listOf("태종대", "taejongdae", "太宗台", "太宗臺", "テジョンデ"),
    BusanHighlightId.HINYEOUL_CULTURE_VILLAGE to listOf(
        "흰여울문화마을", "흰여울", "huinnyeoul", "hinyeoul", "白险滩文化村", "白險灘文化村", "フィニョウル"
    ),
    BusanHighlightId.SONGDO_BEACH to listOf(
        "송도해수욕장", "송도해변", "송도구름산책로", "송도해상케이블", "songdobeach", "songdoskywalk",
        "松島海水浴場", "松岛海水浴场", "ソンドビーチ"
    ),
    BusanHighlightId.DADAEPO_BEACH to listOf(
        "다대포", "몰운대", "dadaepo", "molundae", "多大浦", "沒雲台", "没云台", "タデポ"
    ),
    BusanHighlightId.EULSUKDO to listOf("을숙도", "eulsukdo", "乙淑岛", "乙淑島", "ウルスクド"),
    BusanHighlightId.HEOSIMCHEONG to listOf(
        "허심청", "동래온천", "heosimcheong", "hurshimchung", "虚心厅", "虛心廳", "ホシムチョン"
    ),
    BusanHighlightId.DONGNAE_EUPSEONG to listOf(
        "동래읍성", "금강공원", "dongnaeeupseong", "geumgangpark", "东莱邑城", "東萊邑城", "トンネ邑城"
    ),
    BusanHighlightId.ONCHEONCHEON_CAFE_STREET to listOf(
        "온천천", "oncheoncheon", "温泉川", "溫泉川", "オンチョンチョン"
    ),
    BusanHighlightId.BEOMEOSA to listOf("범어사", "beomeosa", "梵鱼寺", "梵魚寺", "ポモサ"),
    BusanHighlightId.SAMNAK_ECO_PARK to listOf(
        "삼락생태공원", "삼락공원", "samnakecopark", "三乐生态公园", "三樂生態公園", "サムラク"
    ),
    BusanHighlightId.HAEDONG_YONGGUNGSA to listOf(
        "해동용궁사", "용궁사", "yonggungsa", "海东龙宫寺", "海東龍宮寺", "龍宮寺"
    ),
    BusanHighlightId.BUSAN_CITIZENS_PARK to listOf(
        "부산시민공원", "citizenspark", "釜山市民公园", "釜山市民公園", "釜山市民公園"
    ),
    BusanHighlightId.MOCA_BUSAN to listOf(
        "부산현대미술관", "부산시립미술관", "museumofcontemporaryart", "busanmuseumofart",
        "釜山现代美术馆", "釜山現代美術館", "釜山市立美術館"
    )
)

/**
 * 장소 이름으로 큐레이션 항목을 찾는다. 못 찾으면 null — 그 경우 화면은 [PlaceCareProfile] 기반
 * 섹션만으로도 충분히 채워지므로 팁 카드만 빠진다.
 *
 * 선언 순서대로 검사하므로, 한 이름에 여러 키워드가 걸리면 **먼저 선언된 항목이 이긴다**
 * (예: "신세계 스파랜드"는 SPALAND_CENTUM이 SHINSEGAE_CENTUM보다 앞이라 스파랜드로 잡힌다).
 * core/common/MedicalCategory.kt의 resolveHospitalThumbnailRes와 같은 규칙이다.
 *
 * 주소는 일부러 보지 않는다 — "부산 해운대구 해운대해변로 264"에 있는 평범한 카페까지
 * 해운대해수욕장 큐레이션을 물려받아 엉뚱한 팁이 붙는다.
 *
 * [type]도 같은 이유로 받는다. 부산에는 명소 이름을 그대로 쓰는 음식점·숙소가 아주 많아서
 * ("해운대암소갈비집", "자갈치곰장어", "송도호텔") 이름만 보면 그 명소로 잡히고, 소개 폴백까지
 * 명소 설명으로 덮여 "이 갈비집은 부산을 대표하는 백사장입니다"가 된다. 여기 등록된 곳 중
 * 음식점·숙소는 하나도 없으므로, 그 두 유형은 아예 매칭 대상에서 뺀다.
 */
fun resolveBusanHighlight(name: String, type: PlaceType): BusanHighlightId? {
    if (name.isBlank()) return null
    if (type == PlaceType.RESTAURANT || type == PlaceType.LODGING) return null
    val normalizedName = normalizeForMatch(name)
    if (normalizedName.isEmpty()) return null
    return BusanHighlightId.entries.firstOrNull { id ->
        highlightKeywords[id]?.any { keyword -> normalizedName.contains(normalizeForMatch(keyword)) } == true
    }
}

/**
 * 표기 흔들림을 흡수한다 — "해운대 해수욕장", "해운대해수욕장", "Haeundae Beach",
 * "Haeundae-Beach"가 모두 같은 문자열이 되도록 공백·구분자를 지우고 소문자로 맞춘다.
 */
private fun normalizeForMatch(value: String): String = buildString(value.length) {
    value.forEach { char ->
        if (!char.isWhitespace() && char !in MatchSeparators) append(char.lowercaseChar())
    }
}

private const val MatchSeparators = "·・‧-–—_/,.()[]<>「」『』&"
