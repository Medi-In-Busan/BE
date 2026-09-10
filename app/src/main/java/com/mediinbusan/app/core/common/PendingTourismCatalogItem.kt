package com.mediinbusan.app.core.common

import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismHotPlace
import com.mediinbusan.app.domain.tourism.busanDistrictOrNull
import com.mediinbusan.app.domain.tourism.isLanguageVariant
import com.mediinbusan.app.domain.tourism.congestionRateOrNull
import javax.inject.Inject
import javax.inject.Singleton

data class PendingTourismSelection(
    val category: TourismCatalogCategory,
    val item: TourismCatalogItem,
    // 최근 본 항목 재조회(findMatchingPlace)에 쓰는 구·군 컨텍스트. 구·군 비종속 카테고리(WALKING/
    // AUDIO/PHOTOS 등)는 null — TourismCatalogItemDetailViewModel이 이 경우 재조회를 시도하지 않는다.
    val district: BusanDistrict?
)

/**
 * 관광 데이터 목록(S-07 카탈로그)에서 상세 화면으로 넘어갈 때 선택한 항목을 1회성으로 실어 보내는
 * 싱글턴. 카탈로그 API에는 항목 단건 조회 엔드포인트가 없고(외부 API마다 식별자 체계가 달라 새로
 * 만들려면 API별로 별도 연동이 필요하다), `TourismCatalogItem`엔 `Map<String, String>` 필드가 있어
 * 타입세이프 Route 인자로 직렬화하기도 마땅치 않다. `core/common/PendingHospitalSearchEntry.kt`와 같은
 * 패턴으로 Nav 백스택과 무관한 순수 인메모리 홀더를 둔다.
 *
 * 목록 화면이 카드 탭 직후 set()을 부르고, 상세 화면이 진입 시 consume()으로 한 번 읽고 비운다 —
 * 프로세스가 죽었다 재생성되거나 상세 라우트로 직접 진입한 경우엔 비어 있을 수 있으니 호출 측은 null을
 * 그냥 뒤로가기로 처리한다.
 *
 * 혼잡도(CROWDING) 항목은 어느 진입점에서 넘어오든 여기서 같은 모양으로 정규화한다 — 아래
 * [setCrowding] 주석 참고.
 */
@Singleton
class PendingTourismCatalogItem @Inject constructor() {
    @Volatile
    private var entry: PendingTourismSelection? = null

    fun set(category: TourismCatalogCategory, item: TourismCatalogItem, district: BusanDistrict?) {
        if (category == TourismCatalogCategory.CROWDING) {
            // 혼잡도 목록(S-07 > 관광지 혼잡도)에서 고른 항목 — 핫플레이스 카드와 같은 데이터라
            // 같은 경로로 정규화한다. 목록의 구·군 필터가 걸려 있으면 그 값이 가장 정확하고
            // (백엔드가 signguCd로 거른다), 없으면 항목이 들고 있는 지역명으로 판정한다.
            setCrowding(
                item = item,
                district = district ?: item.busanDistrictOrNull() ?: DEFAULT_CROWDING_DISTRICT,
                congestionRate = item.congestionRateOrNull()
            )
            return
        }
        // 목록에 지역 필터가 걸려 있지 않아도(부산 관광지의 기본값 "전체") 항목 주소로 구·군을
        // 짚어둔다 — "최근 본 항목"에서 다시 들어왔을 때 이 값이 있어야 최신 정보로 재조회할 수
        // 있다(TourismCatalogItemDetailViewModel.loadFromRecent). 구·군과 무관한 카테고리
        // (걷기 코스·오디오·관광사진)는 그대로 null이다.
        val resolvedDistrict = district
            ?: item.busanDistrictOrNull()?.takeIf { category.supportsDistrict || category.isLanguageVariant }
        entry = PendingTourismSelection(category, item, resolvedDistrict)
    }

    fun setHotPlace(hotPlace: TourismHotPlace) {
        setCrowding(
            item = hotPlace.item,
            district = hotPlace.district,
            congestionRate = hotPlace.congestionRate
        )
    }

    /**
     * 혼잡도 항목을 상세 화면이 기대하는 모양으로 맞춘다.
     *
     * 혼잡도 응답에는 관광지 이름과 혼잡도 지수뿐이라(사진·주소·좌표·소개문이 전부 없다) 상세 화면이
     * 구·군 + 이름으로 관광공사 상세를 다시 찾아 붙인다(TourismCatalogItemDetailViewModel.retry).
     * 그 재조회에 필요한 구·군을 여기서 확정하고, 재조회 결과에 합쳐 남길 값(혼잡도 지수·지역명)도
     * 상세 화면이 읽는 키 이름(`congestionRate`/`signguNm`)으로 옮겨 담는다.
     *
     * 핫플레이스 카드(S-07 홈)와 혼잡도 리스트(S-07 > 전체보기)가 이 한 곳을 같이 쓰는 게 핵심이다 —
     * 예전엔 핫플레이스 경로만 이 정규화를 거쳐서, 같은 장소인데도 카드로 들어가면 사진·소개가 붙은
     * 관광공사 상세가, 리스트로 들어가면 이름과 지수만 있는 빈 상세가 나왔다.
     */
    private fun setCrowding(
        item: TourismCatalogItem,
        district: BusanDistrict,
        congestionRate: Double?
    ) {
        val details = buildMap {
            putAll(item.details)
            put("signguNm", item.details["signguNm"]?.takeIf { it.isNotBlank() } ?: district.label)
            congestionRate?.let { put("congestionRate", it.toString()) }
        }
        entry = PendingTourismSelection(
            TourismCatalogCategory.CROWDING,
            item.copy(details = details),
            district
        )
    }

    fun consume(): PendingTourismSelection? = entry.also { entry = null }

    private companion object {
        /**
         * 지역명이 없어 구·군을 판정하지 못했을 때. 재조회가 빗나가면 상세 화면이 목록에서 넘어온
         * 원본을 그대로 그리고 "관광공사 상세를 찾지 못했다"고 안내하므로(matchNotFound) 화면이
         * 비지는 않는다 — 핫플레이스 경로도 같은 기본값을 쓴다(NearbyViewModel.districtForItem).
         */
        val DEFAULT_CROWDING_DISTRICT = BusanDistrict.HAEUNDAE
    }
}
