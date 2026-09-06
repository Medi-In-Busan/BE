package com.mediinbusan.app.core.i18n

import com.mediinbusan.app.core.common.BusanHighlightId
import com.mediinbusan.app.core.datastore.SupportedLanguage

/**
 * 부산 대표 명소별 큐레이션 문구.
 *
 * PlaceTypeStrings.kt / MedicalCategoryStrings.kt와 같은 확장 함수 패턴을 쓴다 —
 * [BusanHighlightId]는 core/common에 있는 순수 도메인 enum이라, 언어별 표시 문구를 여기서
 * 따로 매핑하고 enum 자체는 문자열을 모른 채로 둔다.
 *
 * 문구를 쓸 때 지킨 규칙:
 * - [tagline]은 "이 장소가 무엇인지" 한 줄. 웰니스 API의 description이 비어 있을 때 소개 섹션의
 *   폴백으로도 그대로 쓰이므로, 팁이 아니라 사실 서술로만 쓴다.
 * - [tip]은 **진료 전후 관점**에서만 쓴다(경사·계단·온열·자외선·혼잡·이동 부담). 일반 관광
 *   가이드에도 있는 정보를 옮겨 적는 자리가 아니다 — 이 앱이 줄 수 있는 정보만 남긴다.
 * - 예약·결제·통역사 매칭을 암시하는 표현은 넣지 않는다(CLAUDE.md §1 MVP 하드 제약).
 * - 어떤 문구도 의료 자문이 아니다. 제한이 걸릴 수 있는 활동은 "병원 안내를 먼저 확인"으로 넘긴다.
 */
data class BusanHighlightCopy(
    val tagline: String,
    val tip: String,
    val bestTime: String
)

fun BusanHighlightId.translatedCopy(language: SupportedLanguage): BusanHighlightCopy = when (language) {
    SupportedLanguage.KO -> koreanCopy()
    SupportedLanguage.EN -> englishCopy()
    SupportedLanguage.ZH -> chineseCopy()
    SupportedLanguage.JA -> japaneseCopy()
}

private fun BusanHighlightId.koreanCopy(): BusanHighlightCopy = when (this) {
    BusanHighlightId.SPALAND_CENTUM -> BusanHighlightCopy(
        tagline = "신세계 센텀시티 안에 있는 도심형 스파로, 여러 온천탕과 휴게 공간이 실내에 모여 있습니다.",
        tip = "온열·사우나는 시술 종류에 따라 한동안 제한되는 경우가 많습니다. 회복 후 일정에 넣고, 이용 가능한 시점은 진료받은 병원 안내를 먼저 확인하세요.",
        bestTime = "평일 오전 (주말 오후는 대기가 깁니다)"
    )
    BusanHighlightId.SHINSEGAE_CENTUM -> BusanHighlightCopy(
        tagline = "백화점·서점·영화관·식당이 한 건물에 모여 있어 밖으로 나가지 않고 반나절을 보낼 수 있는 실내 권역입니다.",
        tip = "지하철역과 바로 연결돼 이동 부담이 적고, 자외선을 피해야 하는 회복 기간이나 비 오는 날 대안으로 좋습니다.",
        bestTime = "평일 오후"
    )
    BusanHighlightId.BEXCO -> BusanHighlightCopy(
        tagline = "부산의 대형 전시·컨벤션 센터로, 시기에 따라 박람회와 공연이 열립니다.",
        tip = "행사가 없는 날은 볼거리가 거의 없으니 방문 전 일정을 확인하세요. 실내 전시장이라 걷는 거리는 길어도 날씨 영향은 없습니다.",
        bestTime = "행사 일정에 맞춰"
    )
    BusanHighlightId.BUSAN_CINEMA_CENTER -> BusanHighlightCopy(
        tagline = "거대한 지붕 구조로 알려진 부산국제영화제의 중심 공간으로, 상영관과 야외 광장이 함께 있습니다.",
        tip = "야외 광장은 앉아서 쉬기 좋고 저녁 조명은 오래 걷지 않아도 볼 수 있어, 회복 중 일정에 부담이 적습니다.",
        bestTime = "해 질 무렵 저녁"
    )
    BusanHighlightId.BUSAN_AQUARIUM -> BusanHighlightCopy(
        tagline = "해운대 해수욕장 바로 앞 지하에 있는 대형 실내 수족관입니다.",
        tip = "전 구간이 실내 평지라 오래 걷기 어려운 회복 초기에도 비교적 무리가 적습니다.",
        bestTime = "평일 오전 개장 직후"
    )
    BusanHighlightId.HAEUNDAE_BLUELINE_PARK -> BusanHighlightCopy(
        tagline = "옛 동해남부선 철길을 따라 바다를 보며 이동하는 해변열차와 스카이캡슐 구간입니다.",
        tip = "앉아서 바다를 볼 수 있어 걷기 부담이 거의 없습니다. 스카이캡슐은 좌석이 한정돼 당일 대기가 길어질 수 있습니다.",
        bestTime = "오전 또는 일몰 전"
    )
    BusanHighlightId.HAEUNDAE_BEACH -> BusanHighlightCopy(
        tagline = "부산을 대표하는 백사장으로, 해변 산책로를 따라 카페와 호텔이 이어집니다.",
        tip = "모래사장 대신 뒤편 포장 산책로를 걸으면 발목 부담이 적습니다. 한여름 한낮은 자외선이 강해, 회복 중이라면 이른 아침이나 저녁이 낫습니다.",
        bestTime = "이른 아침 또는 일몰 무렵"
    )
    BusanHighlightId.DONGBAEKSEOM -> BusanHighlightCopy(
        tagline = "해운대 끝자락의 완만한 해안 산책로로, 전망 포인트와 누리마루 APEC하우스가 있습니다.",
        tip = "경사가 완만하고 한 바퀴가 짧으며 중간중간 벤치가 있어, 회복형 산책에 가장 무난한 코스 중 하나입니다.",
        bestTime = "오전 또는 늦은 오후"
    )
    BusanHighlightId.SONGJEONG_BEACH -> BusanHighlightCopy(
        tagline = "해운대보다 한적한 해변으로, 서핑 스팟과 조용한 카페가 모여 있습니다.",
        tip = "사람이 적어 천천히 쉬기 좋습니다. 도심에서 다소 떨어져 있으니 이동 시간을 넉넉히 잡으세요.",
        bestTime = "평일 오전"
    )
    BusanHighlightId.GWANGALLI_BEACH -> BusanHighlightCopy(
        tagline = "광안대교 야경으로 알려진 해변으로, 백사장을 따라 카페와 음식점이 늘어서 있습니다.",
        tip = "앉아서 야경만 보고 돌아와도 충분해, 체력 소모가 적은 저녁 일정으로 만들기 쉽습니다. 주말 저녁은 매우 혼잡합니다.",
        bestTime = "저녁"
    )
    BusanHighlightId.IGIDAE -> BusanHighlightCopy(
        tagline = "바다를 끼고 이어지는 해안 절벽 산책로로, 광안대교가 정면으로 보입니다.",
        tip = "계단과 데크의 오르내림이 많습니다. 회복 초기에는 전체 코스를 완주하기보다 입구 쪽 전망 구간만 다녀오는 편이 좋습니다.",
        bestTime = "선선한 오전"
    )
    BusanHighlightId.OERYUKDO_SKYWALK -> BusanHighlightCopy(
        tagline = "유리 바닥 전망대에서 오륙도와 바다가 맞닿는 지점을 내려다보는 곳입니다.",
        tip = "주차장에서 전망대까지 짧은 오르막이 있고 바람이 강한 날이 많습니다. 겉옷을 챙기면 좋습니다.",
        bestTime = "맑은 날 오전"
    )
    BusanHighlightId.HWANGNYEONGSAN -> BusanHighlightCopy(
        tagline = "부산 도심과 광안대교를 한눈에 내려다보는 야경 명소입니다.",
        tip = "전망대 가까이까지 차로 올라갈 수 있어, 거의 걷지 않고도 야경을 볼 수 있습니다.",
        bestTime = "일몰 직후"
    )
    BusanHighlightId.GAMCHEON_CULTURE_VILLAGE -> BusanHighlightCopy(
        tagline = "산비탈을 따라 색색의 집이 층층이 놓인 마을로, 골목마다 벽화와 작은 공방이 있습니다.",
        tip = "경사와 계단이 많아 체력 부담이 큰 편입니다. 회복 중이라면 마을버스로 위쪽까지 올라간 뒤 내려오는 방향으로 걸으세요.",
        bestTime = "오전 (한낮 오르막은 피할 것)"
    )
    BusanHighlightId.JAGALCHI_MARKET -> BusanHighlightCopy(
        tagline = "한국 최대 규모의 수산물 시장으로, 1층 좌판과 2층 식당가가 이어집니다.",
        tip = "회·해산물은 진료 전후 식이 제한에 걸릴 수 있으니 먼저 확인하세요. 바닥이 젖은 구간이 있어 미끄럽지 않은 신발이 안전합니다.",
        bestTime = "오전"
    )
    BusanHighlightId.GUKJE_MARKET -> BusanHighlightCopy(
        tagline = "먹거리 골목과 잡화 상점이 촘촘히 이어지는 부산 원도심의 전통시장입니다.",
        tip = "골목이 좁고 사람이 많아 혼잡 시간대는 피하는 편이 좋습니다. 카드를 받지 않는 노점이 섞여 있습니다.",
        bestTime = "평일 오전~이른 오후"
    )
    BusanHighlightId.GWANGBOK_ROAD -> BusanHighlightCopy(
        tagline = "국제시장과 자갈치 사이를 잇는 보행자 중심 거리로, 상점과 카페가 이어집니다.",
        tip = "평지 보행 구간이라 원도심에서 가장 걷기 편한 축입니다. 지하철역에서 바로 이어져 이동 부담도 적습니다.",
        bestTime = "오후"
    )
    BusanHighlightId.YONGDUSAN_BUSAN_TOWER -> BusanHighlightCopy(
        tagline = "원도심 한가운데 언덕 위 공원과 전망 타워로, 부산항이 내려다보입니다.",
        tip = "광복로 쪽 에스컬레이터를 이용하면 언덕을 걸어 오르지 않아도 됩니다.",
        bestTime = "늦은 오후~저녁"
    )
    BusanHighlightId.BOSUDONG_BOOK_STREET -> BusanHighlightCopy(
        tagline = "헌책방이 좁은 골목을 따라 늘어선 부산 원도심의 오래된 거리입니다.",
        tip = "실내를 오가며 천천히 둘러보는 곳이라 체력 부담이 적습니다. 문을 닫는 가게가 많은 요일이 있어 미리 확인하면 좋습니다.",
        bestTime = "평일 오후"
    )
    BusanHighlightId.CHORYANG_IBAGU_GIL -> BusanHighlightCopy(
        tagline = "부산역 뒤편 산복도로를 따라 옛 골목과 전망대를 잇는 길입니다.",
        tip = "168계단 구간은 경사가 매우 급합니다. 옆에 모노레일이 운행하니 걸어 오르지 말고 이용하세요.",
        bestTime = "오전"
    )
    BusanHighlightId.TAEJONGDAE -> BusanHighlightCopy(
        tagline = "영도 남단의 해안 절벽 공원으로, 등대와 전망대, 바다 절경이 이어집니다.",
        tip = "순환 코스가 길어 도보 완주는 부담이 큽니다. 순환 열차를 이용하면 주요 지점만 편하게 볼 수 있습니다.",
        bestTime = "맑은 날 오전"
    )
    BusanHighlightId.HINYEOUL_CULTURE_VILLAGE -> BusanHighlightCopy(
        tagline = "영도 해안 절벽 위에 자리한 마을로, 좁은 골목과 바다 전망 카페가 이어집니다.",
        tip = "골목이 좁고 계단이 많습니다. 아래 해안 산책로까지 내려가면 돌아 올라오는 길이 가파르니 체력을 남겨두세요.",
        bestTime = "오전~이른 오후"
    )
    BusanHighlightId.SONGDO_BEACH -> BusanHighlightCopy(
        tagline = "한국 최초의 공설 해수욕장으로, 바다 위 구름산책로와 해상 케이블카가 이어집니다.",
        tip = "구름산책로는 평지 데크라 걷기 편합니다. 케이블카를 이용하면 언덕을 오르지 않고 전망을 볼 수 있습니다.",
        bestTime = "오후~일몰"
    )
    BusanHighlightId.DADAEPO_BEACH -> BusanHighlightCopy(
        tagline = "넓은 백사장과 낙조분수, 몰운대 숲길이 있는 부산 서쪽 끝 해변입니다.",
        tip = "시내에서 이동 시간이 긴 편이라 반나절 일정으로 잡는 편이 좋습니다. 일몰 시간대가 특히 좋습니다.",
        bestTime = "일몰 무렵"
    )
    BusanHighlightId.EULSUKDO -> BusanHighlightCopy(
        tagline = "낙동강 하구의 철새 도래지로, 생태공원과 탐방로가 조성되어 있습니다.",
        tip = "평지 산책로 위주라 걷기 부담이 적습니다. 그늘이 적은 구간이 있어 한낮에는 모자를 챙기세요.",
        bestTime = "가을·겨울 오전"
    )
    BusanHighlightId.HEOSIMCHEONG -> BusanHighlightCopy(
        tagline = "동래온천 지구의 대형 온천 시설로, 다양한 온천탕이 실내에 모여 있습니다.",
        tip = "온천은 시술 부위와 상처에 영향을 줄 수 있어 회복 초기에는 권하지 않습니다. 이용 가능한 시점은 반드시 병원 안내를 따르세요.",
        bestTime = "평일 오전"
    )
    BusanHighlightId.DONGNAE_EUPSEONG -> BusanHighlightCopy(
        tagline = "동래 지역의 옛 성곽길과 이어지는 금강공원으로, 케이블카와 산책로가 있습니다.",
        tip = "성곽길에는 오르막이 있지만 금강공원 쪽은 평지 구간이 많습니다. 온천 지구와 가까워 함께 묶기 좋습니다.",
        bestTime = "오전"
    )
    BusanHighlightId.ONCHEONCHEON_CAFE_STREET -> BusanHighlightCopy(
        tagline = "도심을 가로지르는 하천을 따라 산책로와 카페가 이어지는 구간입니다.",
        tip = "하천변 평지 산책로라 부담이 가장 적은 걷기 코스 중 하나입니다. 중간에 언제든 돌아 나올 수 있는 점도 좋습니다.",
        bestTime = "이른 저녁"
    )
    BusanHighlightId.BEOMEOSA -> BusanHighlightCopy(
        tagline = "금정산 자락의 천년 고찰로, 숲길과 전각이 이어지는 조용한 공간입니다.",
        tip = "일주문에서 대웅전까지 완만한 오르막이 있습니다. 사람이 적은 평일 오전이 가장 조용합니다.",
        bestTime = "평일 이른 오전"
    )
    BusanHighlightId.SAMNAK_ECO_PARK -> BusanHighlightCopy(
        tagline = "낙동강변에 넓게 펼쳐진 생태공원으로, 자전거길과 갈대밭이 이어집니다.",
        tip = "완전한 평지에 길이 넓어 천천히 걷기 좋습니다. 다만 공원이 매우 넓으니 목표 구간을 미리 정해두세요.",
        bestTime = "가을 오후"
    )
    BusanHighlightId.HAEDONG_YONGGUNGSA -> BusanHighlightCopy(
        tagline = "바닷가 바위 위에 세워진, 국내에서 보기 드문 해안 사찰입니다.",
        tip = "입구에서 사찰까지 108계단을 내려갔다 다시 올라와야 합니다. 회복 중이라면 계단 부담을 미리 감안하세요.",
        bestTime = "평일 오전"
    )
    BusanHighlightId.BUSAN_CITIZENS_PARK -> BusanHighlightCopy(
        tagline = "도심 한가운데 넓게 조성된 공원으로, 잔디밭과 물길, 그늘 산책로가 있습니다.",
        tip = "평지에 그늘과 벤치가 많아 회복 중 짧은 산책에 가장 무난합니다. 서면 권역이라 접근성도 좋습니다.",
        bestTime = "오전 또는 늦은 오후"
    )
    BusanHighlightId.MOCA_BUSAN -> BusanHighlightCopy(
        tagline = "실내 전시 위주의 미술관으로, 기획전에 따라 전시 구성이 바뀝니다.",
        tip = "냉난방이 되는 실내에서 앉아 쉬어가며 볼 수 있어, 날씨나 체력이 부담될 때 대안으로 좋습니다. 휴관일을 미리 확인하세요.",
        bestTime = "평일 오후"
    )
}

private fun BusanHighlightId.englishCopy(): BusanHighlightCopy = when (this) {
    BusanHighlightId.SPALAND_CENTUM -> BusanHighlightCopy(
        tagline = "An urban spa inside Shinsegae Centum City, with many hot-spring baths and lounge areas all indoors.",
        tip = "Heat and sauna use is often restricted for a while depending on your procedure. Save it for after recovery, and check with your hospital first about when it is fine.",
        bestTime = "Weekday mornings (weekend afternoons mean long waits)"
    )
    BusanHighlightId.SHINSEGAE_CENTUM -> BusanHighlightCopy(
        tagline = "A department store, bookshop, cinema, and restaurants under one roof — you can spend half a day without stepping outside.",
        tip = "It connects straight to the subway, so getting there is easy, and it is a good fallback on rainy days or while you need to avoid the sun.",
        bestTime = "Weekday afternoons"
    )
    BusanHighlightId.BEXCO -> BusanHighlightCopy(
        tagline = "Busan's large exhibition and convention center, hosting fairs and performances depending on the season.",
        tip = "There is little to see on days without an event, so check the schedule first. It is all indoors, so the weather never matters.",
        bestTime = "Whenever an event is on"
    )
    BusanHighlightId.BUSAN_CINEMA_CENTER -> BusanHighlightCopy(
        tagline = "The home of the Busan International Film Festival, known for its vast cantilevered roof, with theaters and an open plaza.",
        tip = "The plaza is easy to sit in and the evening lighting can be enjoyed without much walking, so it is light on a recovery schedule.",
        bestTime = "Around sunset"
    )
    BusanHighlightId.BUSAN_AQUARIUM -> BusanHighlightCopy(
        tagline = "A large indoor aquarium underground, right in front of Haeundae Beach.",
        tip = "The whole route is indoors and flat, so it is manageable even early in recovery when long walks are hard.",
        bestTime = "Right after opening on a weekday"
    )
    BusanHighlightId.HAEUNDAE_BLUELINE_PARK -> BusanHighlightCopy(
        tagline = "A beach train and sky capsule running along a former railway line with the sea beside you the whole way.",
        tip = "You stay seated with a sea view, so there is almost no walking involved. Sky capsule seats are limited and same-day waits can be long.",
        bestTime = "Morning, or just before sunset"
    )
    BusanHighlightId.HAEUNDAE_BEACH -> BusanHighlightCopy(
        tagline = "Busan's signature stretch of sand, lined with cafes and hotels along the seaside promenade.",
        tip = "Walking the paved promenade behind the beach is easier on your ankles than the sand. Midsummer afternoons bring strong sun — early morning or evening is better while recovering.",
        bestTime = "Early morning or around sunset"
    )
    BusanHighlightId.DONGBAEKSEOM -> BusanHighlightCopy(
        tagline = "A gentle coastal loop at the end of Haeundae, with viewpoints and the Nurimaru APEC House.",
        tip = "The slope is gentle, the loop is short, and there are benches along the way — one of the easiest walks here for recovery.",
        bestTime = "Morning or late afternoon"
    )
    BusanHighlightId.SONGJEONG_BEACH -> BusanHighlightCopy(
        tagline = "A quieter beach than Haeundae, with surf spots and calm cafes.",
        tip = "Fewer people means it is easy to rest at your own pace. It sits a little outside the city center, so allow extra travel time.",
        bestTime = "Weekday mornings"
    )
    BusanHighlightId.GWANGALLI_BEACH -> BusanHighlightCopy(
        tagline = "The beach known for the Gwangan Bridge night view, with cafes and restaurants lining the sand.",
        tip = "Sitting and taking in the night view is enough on its own, which makes for a low-effort evening. Weekend evenings get very crowded.",
        bestTime = "Evening"
    )
    BusanHighlightId.IGIDAE -> BusanHighlightCopy(
        tagline = "A cliffside coastal trail facing Gwangan Bridge head-on.",
        tip = "There is a lot of up and down over stairs and decking. Early in recovery, walk only the viewpoint section near the entrance rather than the full trail.",
        bestTime = "Cool mornings"
    )
    BusanHighlightId.OERYUKDO_SKYWALK -> BusanHighlightCopy(
        tagline = "A glass-floored deck looking down where the Oryukdo islets meet the open sea.",
        tip = "There is a short uphill from the parking area, and windy days are common — bring a layer.",
        bestTime = "Clear mornings"
    )
    BusanHighlightId.HWANGNYEONGSAN -> BusanHighlightCopy(
        tagline = "A night-view lookout over downtown Busan and Gwangan Bridge.",
        tip = "You can drive most of the way up to the lookout, so the view costs almost no walking.",
        bestTime = "Just after sunset"
    )
    BusanHighlightId.GAMCHEON_CULTURE_VILLAGE -> BusanHighlightCopy(
        tagline = "A hillside village of brightly painted houses stacked in tiers, with murals and small studios down every alley.",
        tip = "Slopes and stairs make this one of the more demanding stops. While recovering, take the village bus to the top and walk downhill instead.",
        bestTime = "Morning (avoid climbing in midday heat)"
    )
    BusanHighlightId.JAGALCHI_MARKET -> BusanHighlightCopy(
        tagline = "Korea's largest seafood market, with stalls on the ground floor and restaurants above.",
        tip = "Raw fish and shellfish may fall under dietary restrictions before or after treatment, so check first. Some floors stay wet — non-slip shoes are safer.",
        bestTime = "Morning"
    )
    BusanHighlightId.GUKJE_MARKET -> BusanHighlightCopy(
        tagline = "A traditional market in Busan's old downtown, packed with food alleys and general goods shops.",
        tip = "The alleys are narrow and busy, so it is worth avoiding peak hours. Some stalls do not take cards.",
        bestTime = "Weekday morning to early afternoon"
    )
    BusanHighlightId.GWANGBOK_ROAD -> BusanHighlightCopy(
        tagline = "A pedestrian street of shops and cafes linking Gukje Market and Jagalchi.",
        tip = "It is flat the whole way, making it the easiest walking route in the old downtown, and it runs straight from the subway station.",
        bestTime = "Afternoon"
    )
    BusanHighlightId.YONGDUSAN_BUSAN_TOWER -> BusanHighlightCopy(
        tagline = "A hilltop park and observation tower in the middle of the old downtown, looking out over Busan Port.",
        tip = "Take the escalator from the Gwangbok-ro side and you never have to walk up the hill.",
        bestTime = "Late afternoon to evening"
    )
    BusanHighlightId.BOSUDONG_BOOK_STREET -> BusanHighlightCopy(
        tagline = "An old lane in Busan's original downtown lined end to end with second-hand bookshops.",
        tip = "You browse slowly, mostly indoors, so it asks very little of you physically. Many shops close on certain days — worth checking ahead.",
        bestTime = "Weekday afternoons"
    )
    BusanHighlightId.CHORYANG_IBAGU_GIL -> BusanHighlightCopy(
        tagline = "A route behind Busan Station linking old hillside alleys and lookout points along the ridge road.",
        tip = "The 168-step stairway is very steep. A monorail runs alongside it — use that rather than climbing.",
        bestTime = "Morning"
    )
    BusanHighlightId.TAEJONGDAE -> BusanHighlightCopy(
        tagline = "A cliff park at the southern tip of Yeongdo, with a lighthouse, lookouts, and sweeping sea views.",
        tip = "The full loop is long and hard on foot. The shuttle train lets you see the main points comfortably instead.",
        bestTime = "Clear mornings"
    )
    BusanHighlightId.HINYEOUL_CULTURE_VILLAGE -> BusanHighlightCopy(
        tagline = "A village perched on the cliffs of Yeongdo, with narrow alleys and cafes looking out to sea.",
        tip = "The alleys are narrow and full of stairs. If you go down to the shoreline path, the climb back up is steep — keep something in reserve.",
        bestTime = "Morning to early afternoon"
    )
    BusanHighlightId.SONGDO_BEACH -> BusanHighlightCopy(
        tagline = "Korea's first public beach, with a skywalk over the water and a cable car across the bay.",
        tip = "The skywalk is flat decking and easy to walk. The cable car gives you the view without climbing the hill.",
        bestTime = "Afternoon to sunset"
    )
    BusanHighlightId.DADAEPO_BEACH -> BusanHighlightCopy(
        tagline = "A wide beach at Busan's western edge, with a sunset fountain and the wooded Molundae headland.",
        tip = "It takes a while to reach from the city center, so plan it as a half-day. Sunset is the highlight.",
        bestTime = "Around sunset"
    )
    BusanHighlightId.EULSUKDO -> BusanHighlightCopy(
        tagline = "A migratory bird sanctuary at the Nakdong River estuary, with an ecological park and walking trails.",
        tip = "The trails are flat and easy on the body. Some stretches have little shade, so bring a hat in the middle of the day.",
        bestTime = "Autumn and winter mornings"
    )
    BusanHighlightId.HEOSIMCHEONG -> BusanHighlightCopy(
        tagline = "A large hot-spring complex in the Dongnae spa district, with a range of baths all indoors.",
        tip = "Hot springs can affect wounds and treated areas, so they are not advised early in recovery. Follow your hospital's guidance on when it is fine.",
        bestTime = "Weekday mornings"
    )
    BusanHighlightId.DONGNAE_EUPSEONG -> BusanHighlightCopy(
        tagline = "Geumgang Park, connected to the old Dongnae fortress wall, with a cable car and walking paths.",
        tip = "The fortress path climbs, but the park side is largely flat. It sits close to the spa district, so the two pair well.",
        bestTime = "Morning"
    )
    BusanHighlightId.ONCHEONCHEON_CAFE_STREET -> BusanHighlightCopy(
        tagline = "A stretch of walking path and cafes following a stream through the middle of the city.",
        tip = "A flat streamside path makes this one of the gentlest walks here, and you can turn back at any point.",
        bestTime = "Early evening"
    )
    BusanHighlightId.BEOMEOSA -> BusanHighlightCopy(
        tagline = "A thousand-year-old temple on the slopes of Geumjeongsan, quiet among forest paths and halls.",
        tip = "There is a gentle climb from the front gate to the main hall. Weekday mornings are the quietest.",
        bestTime = "Early weekday mornings"
    )
    BusanHighlightId.SAMNAK_ECO_PARK -> BusanHighlightCopy(
        tagline = "A broad ecological park along the Nakdong River, with cycling paths and reed fields.",
        tip = "Completely flat and wide open, so it is good for a slow walk. It is very large, though — pick a stretch before you go.",
        bestTime = "Autumn afternoons"
    )
    BusanHighlightId.HAEDONG_YONGGUNGSA -> BusanHighlightCopy(
        tagline = "A rare seaside temple built onto the rocks right above the water.",
        tip = "You descend 108 steps from the entrance and climb back up the same way. Factor that in if you are still recovering.",
        bestTime = "Weekday mornings"
    )
    BusanHighlightId.BUSAN_CITIZENS_PARK -> BusanHighlightCopy(
        tagline = "A large park in the middle of the city, with lawns, water channels, and shaded paths.",
        tip = "Flat, with plenty of shade and benches — the easiest option here for a short walk while recovering, and close to Seomyeon.",
        bestTime = "Morning or late afternoon"
    )
    BusanHighlightId.MOCA_BUSAN -> BusanHighlightCopy(
        tagline = "An art museum built around indoor exhibitions, with the lineup changing by season.",
        tip = "Climate-controlled indoors with places to sit, so it works well when the weather or your energy is the limiting factor. Check the closing day first.",
        bestTime = "Weekday afternoons"
    )
}

private fun BusanHighlightId.chineseCopy(): BusanHighlightCopy = when (this) {
    BusanHighlightId.SPALAND_CENTUM -> BusanHighlightCopy(
        tagline = "位于新世界Centum City内的都市型水疗设施，多种温泉浴池和休息空间都在室内。",
        tip = "桑拿和热疗常会因治疗种类而在一段时间内受限。建议安排在恢复之后，具体可用时间请先确认就诊医院的指引。",
        bestTime = "工作日上午（周末下午等候时间较长）"
    )
    BusanHighlightId.SHINSEGAE_CENTUM -> BusanHighlightCopy(
        tagline = "百货、书店、影院和餐厅集中在同一栋楼内，不用外出也能待上半天的室内区域。",
        tip = "与地铁站直接相连，移动负担小；在需要避开紫外线的恢复期或下雨天是很好的选择。",
        bestTime = "工作日下午"
    )
    BusanHighlightId.BEXCO -> BusanHighlightCopy(
        tagline = "釜山的大型展览会议中心，会根据时期举办博览会和演出。",
        tip = "没有活动的日子几乎没有可看的内容，请先确认日程。全程室内，不受天气影响。",
        bestTime = "配合活动日程"
    )
    BusanHighlightId.BUSAN_CINEMA_CENTER -> BusanHighlightCopy(
        tagline = "以巨大屋顶结构闻名的釜山国际电影节主场地，设有放映厅和户外广场。",
        tip = "户外广场适合坐下休息，夜间灯光不用走太多路也能欣赏，对恢复期的行程负担较小。",
        bestTime = "日落时分"
    )
    BusanHighlightId.BUSAN_AQUARIUM -> BusanHighlightCopy(
        tagline = "位于海云台海水浴场正前方地下的大型室内水族馆。",
        tip = "全程室内平地，即使在难以久走的恢复初期也相对轻松。",
        bestTime = "工作日开馆后不久"
    )
    BusanHighlightId.HAEUNDAE_BLUELINE_PARK -> BusanHighlightCopy(
        tagline = "沿旧铁路线行驶、一路可眺望大海的海滨列车与天空胶囊列车路段。",
        tip = "可以坐着看海，几乎不需要走路。天空胶囊座位有限，当天等候时间可能较长。",
        bestTime = "上午或日落前"
    )
    BusanHighlightId.HAEUNDAE_BEACH -> BusanHighlightCopy(
        tagline = "釜山最具代表性的沙滩，海滨步道沿线分布着咖啡厅和酒店。",
        tip = "走沙滩后方的铺装步道比走沙地更省力。盛夏正午紫外线强烈，恢复期建议选择清晨或傍晚。",
        bestTime = "清晨或日落时分"
    )
    BusanHighlightId.DONGBAEKSEOM -> BusanHighlightCopy(
        tagline = "海云台尽头坡度平缓的海岸步道，设有观景点和APEC世峰楼。",
        tip = "坡度平缓、环线较短，沿途还有长椅，是恢复期散步最合适的路线之一。",
        bestTime = "上午或傍晚前"
    )
    BusanHighlightId.SONGJEONG_BEACH -> BusanHighlightCopy(
        tagline = "比海云台更清静的海滩，聚集着冲浪点和安静的咖啡厅。",
        tip = "人少，适合慢慢休息。距离市中心稍远，请预留充足的移动时间。",
        bestTime = "工作日上午"
    )
    BusanHighlightId.GWANGALLI_BEACH -> BusanHighlightCopy(
        tagline = "以广安大桥夜景闻名的海滩，沙滩沿线遍布咖啡厅和餐厅。",
        tip = "只是坐着看夜景就已足够，很容易安排成体力消耗小的傍晚行程。周末晚上非常拥挤。",
        bestTime = "傍晚"
    )
    BusanHighlightId.IGIDAE -> BusanHighlightCopy(
        tagline = "沿海崖延伸的海岸步道，正面可以看到广安大桥。",
        tip = "台阶和木栈道的上下起伏较多。恢复初期建议只走入口附近的观景路段，不要走完全程。",
        bestTime = "凉爽的上午"
    )
    BusanHighlightId.OERYUKDO_SKYWALK -> BusanHighlightCopy(
        tagline = "在玻璃地板观景台上俯瞰五六岛与大海交汇之处。",
        tip = "从停车场到观景台有一小段上坡，且风大的日子较多，建议带上外套。",
        bestTime = "晴天上午"
    )
    BusanHighlightId.HWANGNYEONGSAN -> BusanHighlightCopy(
        tagline = "可一览釜山市区和广安大桥的夜景名所。",
        tip = "可以开车上到接近观景台的位置，几乎不用走路就能看到夜景。",
        bestTime = "日落后不久"
    )
    BusanHighlightId.GAMCHEON_CULTURE_VILLAGE -> BusanHighlightCopy(
        tagline = "沿山坡层层叠叠排列着彩色房屋的村庄，每条巷子里都有壁画和小工坊。",
        tip = "坡道和台阶多，体力负担较大。恢复期间建议先乘社区巴士上到高处，再沿下坡方向步行。",
        bestTime = "上午（避开正午上坡）"
    )
    BusanHighlightId.JAGALCHI_MARKET -> BusanHighlightCopy(
        tagline = "韩国最大的水产市场，一层是摊位，二层是餐饮区。",
        tip = "生鱼片和海鲜可能涉及就诊前后的饮食限制，请先确认。部分地面湿滑，建议穿防滑的鞋子。",
        bestTime = "上午"
    )
    BusanHighlightId.GUKJE_MARKET -> BusanHighlightCopy(
        tagline = "釜山老城区的传统市场，小吃街和杂货店密集相连。",
        tip = "巷子窄、人也多，建议避开拥挤时段。部分摊位不支持刷卡。",
        bestTime = "工作日上午至午后"
    )
    BusanHighlightId.GWANGBOK_ROAD -> BusanHighlightCopy(
        tagline = "连接国际市场与札嘎其的步行街，沿路是商店和咖啡厅。",
        tip = "全程平地，是老城区最好走的一条路线，并且与地铁站直接相连。",
        bestTime = "下午"
    )
    BusanHighlightId.YONGDUSAN_BUSAN_TOWER -> BusanHighlightCopy(
        tagline = "位于老城区中心山丘上的公园和观景塔，可以俯瞰釜山港。",
        tip = "从光复路一侧乘坐扶梯，就不必徒步爬坡。",
        bestTime = "傍晚前后"
    )
    BusanHighlightId.BOSUDONG_BOOK_STREET -> BusanHighlightCopy(
        tagline = "釜山老城区的旧街，狭窄巷子两侧排满了旧书店。",
        tip = "以室内慢慢浏览为主，体力负担很小。有些日子不少书店会休息，建议提前确认。",
        bestTime = "工作日下午"
    )
    BusanHighlightId.CHORYANG_IBAGU_GIL -> BusanHighlightCopy(
        tagline = "釜山站后方沿山腰路连接老巷子与观景台的步道。",
        tip = "168级台阶路段坡度非常陡。旁边有单轨电车运行，请乘坐而不要步行攀爬。",
        bestTime = "上午"
    )
    BusanHighlightId.TAEJONGDAE -> BusanHighlightCopy(
        tagline = "影岛南端的海岸悬崖公园，有灯塔、观景台和壮阔海景。",
        tip = "环线较长，全程步行负担大。乘坐环线小火车可以轻松游览主要景点。",
        bestTime = "晴天上午"
    )
    BusanHighlightId.HINYEOUL_CULTURE_VILLAGE -> BusanHighlightCopy(
        tagline = "坐落在影岛海岸悬崖上的村庄，狭窄巷子与海景咖啡厅相连。",
        tip = "巷子窄、台阶多。若下到下方的海岸步道，返回时的上坡较陡，请保留体力。",
        bestTime = "上午至午后"
    )
    BusanHighlightId.SONGDO_BEACH -> BusanHighlightCopy(
        tagline = "韩国第一个公立海水浴场，设有海上云端步道和跨海缆车。",
        tip = "云端步道是平坦的木栈道，走起来轻松。乘坐缆车则不必爬坡也能欣赏风景。",
        bestTime = "下午至日落"
    )
    BusanHighlightId.DADAEPO_BEACH -> BusanHighlightCopy(
        tagline = "位于釜山西端的宽阔海滩，有落日喷泉和没云台林间小路。",
        tip = "从市区过来需要较长时间，建议安排为半天行程。日落时分尤其值得一看。",
        bestTime = "日落时分"
    )
    BusanHighlightId.EULSUKDO -> BusanHighlightCopy(
        tagline = "洛东江入海口的候鸟栖息地，建有生态公园和观察步道。",
        tip = "以平地步道为主，行走负担小。部分路段缺少遮阴，正午时请备好帽子。",
        bestTime = "秋冬上午"
    )
    BusanHighlightId.HEOSIMCHEONG -> BusanHighlightCopy(
        tagline = "东莱温泉区的大型温泉设施，各种温泉浴池都集中在室内。",
        tip = "温泉可能影响伤口和治疗部位，恢复初期不建议前往。可用时间请务必遵循医院的指引。",
        bestTime = "工作日上午"
    )
    BusanHighlightId.DONGNAE_EUPSEONG -> BusanHighlightCopy(
        tagline = "与东莱旧城墙相连的金刚公园，设有缆车和散步道。",
        tip = "城墙路段有上坡，但公园一侧平地较多。距离温泉区很近，适合安排在一起。",
        bestTime = "上午"
    )
    BusanHighlightId.ONCHEONCHEON_CAFE_STREET -> BusanHighlightCopy(
        tagline = "沿着穿越市区的溪流延伸的散步道与咖啡厅路段。",
        tip = "河边平地步道，是负担最小的步行路线之一，而且随时可以中途折返。",
        bestTime = "傍晚早些时候"
    )
    BusanHighlightId.BEOMEOSA -> BusanHighlightCopy(
        tagline = "金井山脚下的千年古刹，林间小路与殿宇相连，十分静谧。",
        tip = "从一柱门到大雄殿有一段缓坡。人少的工作日上午最为安静。",
        bestTime = "工作日清晨"
    )
    BusanHighlightId.SAMNAK_ECO_PARK -> BusanHighlightCopy(
        tagline = "洛东江边开阔的生态公园，有自行车道和芦苇丛。",
        tip = "完全平地且道路宽阔，适合慢慢散步。但公园面积很大，建议先定好要走的路段。",
        bestTime = "秋季下午"
    )
    BusanHighlightId.HAEDONG_YONGGUNGSA -> BusanHighlightCopy(
        tagline = "建在海边岩石上的寺庙，在韩国十分少见的海岸古刹。",
        tip = "从入口到寺庙要走下108级台阶，回程还要原路爬回。恢复期间请提前考虑台阶的负担。",
        bestTime = "工作日上午"
    )
    BusanHighlightId.BUSAN_CITIZENS_PARK -> BusanHighlightCopy(
        tagline = "位于市中心的大型公园，有草坪、水道和林荫步道。",
        tip = "地势平坦，遮阴和长椅都很多，是恢复期短途散步最合适的选择，而且靠近西面。",
        bestTime = "上午或傍晚前"
    )
    BusanHighlightId.MOCA_BUSAN -> BusanHighlightCopy(
        tagline = "以室内展览为主的美术馆，展览内容会随企划展更换。",
        tip = "在有冷暖气的室内可以坐着休息慢慢观看，天气或体力吃紧时是不错的替代方案。请提前确认闭馆日。",
        bestTime = "工作日下午"
    )
}

private fun BusanHighlightId.japaneseCopy(): BusanHighlightCopy = when (this) {
    BusanHighlightId.SPALAND_CENTUM -> BusanHighlightCopy(
        tagline = "新世界センタムシティ内にある都市型スパで、多彩な温泉浴槽と休憩スペースが屋内にそろっています。",
        tip = "温熱・サウナは施術の種類によってしばらく制限されることが多い設備です。回復後の予定に入れ、利用できる時期はまず受診した病院の案内を確認してください。",
        bestTime = "平日の午前（週末の午後は待ち時間が長めです）"
    )
    BusanHighlightId.SHINSEGAE_CENTUM -> BusanHighlightCopy(
        tagline = "百貨店・書店・映画館・レストランが一つの建物にそろい、外に出ずに半日過ごせる屋内エリアです。",
        tip = "地下鉄駅と直結していて移動の負担が少なく、紫外線を避けたい回復期や雨の日の代案として便利です。",
        bestTime = "平日の午後"
    )
    BusanHighlightId.BEXCO -> BusanHighlightCopy(
        tagline = "釜山の大型展示・コンベンションセンターで、時期によって博覧会や公演が開かれます。",
        tip = "イベントのない日は見どころがほとんどないため、訪問前に日程の確認を。屋内なので天候の影響は受けません。",
        bestTime = "イベントの日程に合わせて"
    )
    BusanHighlightId.BUSAN_CINEMA_CENTER -> BusanHighlightCopy(
        tagline = "巨大な屋根構造で知られる釜山国際映画祭の中心施設で、上映館と屋外広場があります。",
        tip = "屋外広場は座って休みやすく、夜の照明演出も長く歩かずに楽しめるため、回復中の予定でも負担が軽めです。",
        bestTime = "日暮れどき"
    )
    BusanHighlightId.BUSAN_AQUARIUM -> BusanHighlightCopy(
        tagline = "海雲台ビーチのすぐ目の前、地下にある大型の屋内水族館です。",
        tip = "全区間が屋内の平坦な順路なので、長く歩きにくい回復初期でも比較的無理がありません。",
        bestTime = "平日の開館直後"
    )
    BusanHighlightId.HAEUNDAE_BLUELINE_PARK -> BusanHighlightCopy(
        tagline = "かつての鉄道跡に沿って海を眺めながら進むビーチトレインとスカイカプセルの区間です。",
        tip = "座ったまま海を眺められるので、歩く負担はほとんどありません。スカイカプセルは席数が限られ、当日待ちが長くなることがあります。",
        bestTime = "午前、または日没前"
    )
    BusanHighlightId.HAEUNDAE_BEACH -> BusanHighlightCopy(
        tagline = "釜山を代表する砂浜で、遊歩道沿いにカフェやホテルが続きます。",
        tip = "砂浜より後ろの舗装遊歩道を歩くほうが足首への負担が少なめです。真夏の日中は紫外線が強いため、回復中なら早朝か夕方がおすすめです。",
        bestTime = "早朝または日没ごろ"
    )
    BusanHighlightId.DONGBAEKSEOM -> BusanHighlightCopy(
        tagline = "海雲台の端にある緩やかな海岸遊歩道で、展望ポイントとヌリマルAPECハウスがあります。",
        tip = "傾斜が緩やかで一周が短く、ベンチも点在するため、回復期の散歩に最も無理のないコースの一つです。",
        bestTime = "午前または夕方近く"
    )
    BusanHighlightId.SONGJEONG_BEACH -> BusanHighlightCopy(
        tagline = "海雲台より落ち着いたビーチで、サーフスポットと静かなカフェが集まっています。",
        tip = "人が少なく、自分のペースで休みやすい場所です。中心部からやや離れているので移動時間は余裕をもって。",
        bestTime = "平日の午前"
    )
    BusanHighlightId.GWANGALLI_BEACH -> BusanHighlightCopy(
        tagline = "広安大橋の夜景で知られるビーチで、砂浜沿いにカフェや飲食店が並びます。",
        tip = "座って夜景を眺めるだけでも十分なので、体力の消耗が少ない夜の予定を組みやすい場所です。週末の夜はかなり混雑します。",
        bestTime = "夜"
    )
    BusanHighlightId.IGIDAE -> BusanHighlightCopy(
        tagline = "海沿いの断崖に続く遊歩道で、広安大橋を正面に望みます。",
        tip = "階段やデッキの上り下りが多い道です。回復初期は全区間を歩き切らず、入口側の展望区間だけにしておくのが安心です。",
        bestTime = "涼しい午前"
    )
    BusanHighlightId.OERYUKDO_SKYWALK -> BusanHighlightCopy(
        tagline = "ガラス床の展望デッキから、五六島と海が接する地点を見下ろせる場所です。",
        tip = "駐車場から展望台まで短い上り坂があり、風の強い日も多めです。羽織るものがあると安心です。",
        bestTime = "晴れた日の午前"
    )
    BusanHighlightId.HWANGNYEONGSAN -> BusanHighlightCopy(
        tagline = "釜山の市街地と広安大橋を一望できる夜景スポットです。",
        tip = "展望台の近くまで車で上がれるため、ほとんど歩かずに夜景を楽しめます。",
        bestTime = "日没直後"
    )
    BusanHighlightId.GAMCHEON_CULTURE_VILLAGE -> BusanHighlightCopy(
        tagline = "斜面に沿って色とりどりの家が段々に並ぶ集落で、路地ごとに壁画や小さな工房があります。",
        tip = "坂と階段が多く、体力的な負担は大きめです。回復中なら村内を走る循環バスで上まで上がり、下る方向に歩くのがおすすめです。",
        bestTime = "午前（日中の上り坂は避けて）"
    )
    BusanHighlightId.JAGALCHI_MARKET -> BusanHighlightCopy(
        tagline = "韓国最大級の水産市場で、1階の店先と2階の食堂街が続きます。",
        tip = "刺身や魚介類は診療前後の食事制限にかかる場合があるので先に確認を。床が濡れている区間があるため、滑りにくい靴が安心です。",
        bestTime = "午前"
    )
    BusanHighlightId.GUKJE_MARKET -> BusanHighlightCopy(
        tagline = "屋台の食べ物通りと雑貨店がびっしり続く、釜山旧市街の伝統市場です。",
        tip = "路地が狭く人も多いため、混雑する時間帯は避けるほうが快適です。カードが使えない露店も混じっています。",
        bestTime = "平日の午前～昼過ぎ"
    )
    BusanHighlightId.GWANGBOK_ROAD -> BusanHighlightCopy(
        tagline = "国際市場とチャガルチを結ぶ歩行者中心の通りで、店とカフェが続きます。",
        tip = "全区間が平坦で、旧市街では最も歩きやすい軸です。地下鉄駅からそのままつながっています。",
        bestTime = "午後"
    )
    BusanHighlightId.YONGDUSAN_BUSAN_TOWER -> BusanHighlightCopy(
        tagline = "旧市街の中心、丘の上の公園と展望タワーで、釜山港を見下ろせます。",
        tip = "光復路側のエスカレーターを使えば、丘を歩いて上る必要がありません。",
        bestTime = "夕方から夜"
    )
    BusanHighlightId.BOSUDONG_BOOK_STREET -> BusanHighlightCopy(
        tagline = "古書店が狭い路地に軒を連ねる、釜山旧市街の古い通りです。",
        tip = "屋内をのぞきながらゆっくり見て回る場所なので、体力的な負担はごくわずかです。休みの店が多い曜日があるため、事前確認を。",
        bestTime = "平日の午後"
    )
    BusanHighlightId.CHORYANG_IBAGU_GIL -> BusanHighlightCopy(
        tagline = "釜山駅の裏手、山腹の道沿いに古い路地と展望台をつなぐ道です。",
        tip = "168階段の区間はかなりの急勾配です。隣にモノレールが運行しているので、歩いて上らずそちらを使ってください。",
        bestTime = "午前"
    )
    BusanHighlightId.TAEJONGDAE -> BusanHighlightCopy(
        tagline = "影島南端の海岸断崖公園で、灯台や展望台、海の絶景が続きます。",
        tip = "周遊コースが長く、徒歩で回りきるのは負担が大きめです。循環列車を使えば主要ポイントだけを楽に見られます。",
        bestTime = "晴れた日の午前"
    )
    BusanHighlightId.HINYEOUL_CULTURE_VILLAGE -> BusanHighlightCopy(
        tagline = "影島の海岸断崖の上に広がる集落で、狭い路地と海が見えるカフェが続きます。",
        tip = "路地が狭く階段も多めです。下の海岸遊歩道まで降りると戻りの上りが急なので、体力を残しておいてください。",
        bestTime = "午前～昼過ぎ"
    )
    BusanHighlightId.SONGDO_BEACH -> BusanHighlightCopy(
        tagline = "韓国初の公設海水浴場で、海上のスカイウォークと海上ケーブルカーがつながっています。",
        tip = "スカイウォークは平坦なデッキで歩きやすい道です。ケーブルカーを使えば坂を上らずに眺めを楽しめます。",
        bestTime = "午後～日没"
    )
    BusanHighlightId.DADAEPO_BEACH -> BusanHighlightCopy(
        tagline = "広い砂浜と夕日の噴水、モルンデの林の道がある釜山西端のビーチです。",
        tip = "市内からの移動時間が長めなので、半日の予定として組むのがおすすめです。夕日の時間帯が特に見応えがあります。",
        bestTime = "日没ごろ"
    )
    BusanHighlightId.EULSUKDO -> BusanHighlightCopy(
        tagline = "洛東江河口の渡り鳥飛来地で、生態公園と探訪路が整備されています。",
        tip = "平坦な遊歩道が中心で歩く負担が少なめです。日陰の少ない区間があるため、日中は帽子があると安心です。",
        bestTime = "秋・冬の午前"
    )
    BusanHighlightId.HEOSIMCHEONG -> BusanHighlightCopy(
        tagline = "東萊温泉エリアの大型温泉施設で、多様な浴槽が屋内にそろっています。",
        tip = "温泉は傷や施術部位に影響することがあるため、回復初期にはおすすめできません。利用できる時期は必ず病院の案内に従ってください。",
        bestTime = "平日の午前"
    )
    BusanHighlightId.DONGNAE_EUPSEONG -> BusanHighlightCopy(
        tagline = "東萊の古い城郭道とつながる金剛公園で、ケーブルカーと散策路があります。",
        tip = "城郭道には上りがありますが、公園側は平坦な区間が多めです。温泉エリアが近いので組み合わせやすい場所です。",
        bestTime = "午前"
    )
    BusanHighlightId.ONCHEONCHEON_CAFE_STREET -> BusanHighlightCopy(
        tagline = "市街地を横切る川沿いに遊歩道とカフェが続く区間です。",
        tip = "川沿いの平坦な遊歩道で、負担が最も少ない散歩コースの一つです。途中でいつでも引き返せるのも利点です。",
        bestTime = "夕方の早い時間"
    )
    BusanHighlightId.BEOMEOSA -> BusanHighlightCopy(
        tagline = "金井山のふもとにある千年の古刹で、森の道と伽藍が続く静かな場所です。",
        tip = "一柱門から大雄殿まで緩やかな上りがあります。人が少ない平日の午前が最も静かです。",
        bestTime = "平日の早い午前"
    )
    BusanHighlightId.SAMNAK_ECO_PARK -> BusanHighlightCopy(
        tagline = "洛東江沿いに広がる生態公園で、サイクリングロードとアシ原が続きます。",
        tip = "完全な平地で道幅も広く、ゆっくり歩くのに向いています。ただし非常に広いので、歩く区間を決めてから行くと安心です。",
        bestTime = "秋の午後"
    )
    BusanHighlightId.HAEDONG_YONGGUNGSA -> BusanHighlightCopy(
        tagline = "海辺の岩の上に建てられた、韓国では珍しい海岸の寺院です。",
        tip = "入口から寺まで108段の階段を下り、帰りは同じ道を上ります。回復中なら階段の負担をあらかじめ見込んでおいてください。",
        bestTime = "平日の午前"
    )
    BusanHighlightId.BUSAN_CITIZENS_PARK -> BusanHighlightCopy(
        tagline = "市街地の真ん中に広がる公園で、芝生や水路、木陰の遊歩道があります。",
        tip = "平坦で日陰とベンチが多く、回復中の短い散歩には最も無理がありません。ソミョン圏でアクセスも良好です。",
        bestTime = "午前または夕方近く"
    )
    BusanHighlightId.MOCA_BUSAN -> BusanHighlightCopy(
        tagline = "屋内展示が中心の美術館で、企画展によって展示内容が変わります。",
        tip = "冷暖房の効いた屋内で座って休みながら見られるので、天候や体力が気になるときの代案になります。休館日は事前に確認を。",
        bestTime = "平日の午後"
    )
}
