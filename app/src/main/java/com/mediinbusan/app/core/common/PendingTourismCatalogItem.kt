package com.mediinbusan.app.core.common

import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismHotPlace
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
 */
@Singleton
class PendingTourismCatalogItem @Inject constructor() {
    @Volatile
    private var entry: PendingTourismSelection? = null

    fun set(category: TourismCatalogCategory, item: TourismCatalogItem, district: BusanDistrict?) {
        entry = PendingTourismSelection(category, item, district)
    }

    fun setHotPlace(hotPlace: TourismHotPlace) {
        val item = hotPlace.item
        set(
            TourismCatalogCategory.CROWDING,
            item.copy(
                details = item.details + mapOf(
                    "hotPlaceDistrict" to hotPlace.district.name,
                    "signguNm" to (item.details["signguNm"]?.takeIf { it.isNotBlank() }
                        ?: hotPlace.district.label),
                    "congestionRate" to hotPlace.congestionRate.toString()
                )
            ),
            district = hotPlace.district
        )
    }

    fun consume(): PendingTourismSelection? = entry.also { entry = null }
}
