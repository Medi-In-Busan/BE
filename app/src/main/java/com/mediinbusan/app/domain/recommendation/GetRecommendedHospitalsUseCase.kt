package com.mediinbusan.app.domain.recommendation

import com.mediinbusan.app.core.common.DefaultSearchOrigin
import com.mediinbusan.app.core.common.haversineDistanceMeters
import com.mediinbusan.app.data.favorite.Favorite
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.recent.RecentItemType
import com.mediinbusan.app.data.recent.RecentlyViewed
import javax.inject.Inject

/**
 * Home "이런 의료기관은 어떠세요?" 카드 10개를 고른다. 서버 추천 API 없이 로컬에 이미 있는 데이터
 * (즐겨찾기·최근 본 항목·거리)만으로 점수화하는 MVP 버전.
 *
 * 점수 = TAG_WEIGHT * 태그매치 + DISTANCE_WEIGHT * 거리(서면 DefaultSearchOrigin 기준, GPS 아님 —
 * CLAUDE.md §1). 관심 태그 신호(즐겨찾기+최근 본 병원의 specialties)가 하나도 없는 신규 유저는
 * 태그매치가 무의미하므로 거리 점수만으로 폴백한다.
 *
 * 이미 즐겨찾기한 병원은 후보에서 제외한다 — 이미 저장한 걸 다시 "이런 병원 어떠세요"로 보여주는
 * 건 의미가 없다는 판단(확정 사항). 후보가 10개 미만이면(병원 총수가 적을 때) 있는 만큼만 반환한다 —
 * take(10)이 자연히 처리한다.
 */
class GetRecommendedHospitalsUseCase @Inject constructor() {

    operator fun invoke(
        allHospitals: List<Hospital>,
        favorites: List<Favorite>,
        recentlyViewed: List<RecentlyViewed>
    ): List<Hospital> {
        val favoriteHospitalIds = favorites
            .filter { it.itemType == FavoriteItemType.HOSPITAL }
            .map { it.itemId }
            .toSet()
        val candidates = allHospitals.filterNot { it.id in favoriteHospitalIds }

        val interestTagCounts = buildInterestTagCounts(allHospitals, favoriteHospitalIds, recentlyViewed)

        val scored = if (interestTagCounts.isEmpty()) {
            candidates.sortedByDescending { distanceScore(it) }
        } else {
            candidates.sortedByDescending { hospital ->
                TAG_WEIGHT * tagMatchScore(hospital, interestTagCounts) + DISTANCE_WEIGHT * distanceScore(hospital)
            }
        }

        return scored.take(RECOMMENDATION_COUNT)
    }

    // 즐겨찾기 + 최근 본 병원(최신순 상위 N개)의 specialties를 모아 태그별 빈도를 센다 —
    // "이 유저가 관심 있는 진료과는 무엇인가"를 행동 기반으로 근사한다.
    private fun buildInterestTagCounts(
        allHospitals: List<Hospital>,
        favoriteHospitalIds: Set<String>,
        recentlyViewed: List<RecentlyViewed>
    ): Map<String, Int> {
        val hospitalsById = allHospitals.associateBy { it.id }
        val recentHospitalIds = recentlyViewed
            .filter { it.itemType == RecentItemType.HOSPITAL }
            .take(RECENT_SIGNAL_LIMIT)
            .map { it.itemId }

        return (favoriteHospitalIds.toList() + recentHospitalIds)
            .mapNotNull { hospitalsById[it] }
            .flatMap { it.specialties }
            .groupingBy { it }
            .eachCount()
    }

    // 관심 태그 전체 가중치 중 이 병원이 차지하는 비중(0~1).
    private fun tagMatchScore(hospital: Hospital, interestTagCounts: Map<String, Int>): Double {
        val totalWeight = interestTagCounts.values.sum().toDouble()
        if (totalWeight == 0.0) return 0.0
        val matchedWeight = hospital.specialties.sumOf { interestTagCounts[it] ?: 0 }
        return (matchedWeight / totalWeight).coerceIn(0.0, 1.0)
    }

    // 서면(DefaultSearchOrigin) 기준 거리가 가까울수록 1에 가깝다. DISTANCE_CAP_KM 이상은 전부 0점.
    private fun distanceScore(hospital: Hospital): Double {
        val latitude = hospital.latitude ?: return 0.0
        val longitude = hospital.longitude ?: return 0.0
        val distanceMeters = haversineDistanceMeters(
            DefaultSearchOrigin.LATITUDE,
            DefaultSearchOrigin.LONGITUDE,
            latitude,
            longitude
        )
        val capMeters = DISTANCE_CAP_KM * 1000.0
        return (1.0 - (distanceMeters.coerceAtMost(capMeters) / capMeters)).coerceIn(0.0, 1.0)
    }

    companion object {
        private const val RECOMMENDATION_COUNT = 10
        private const val RECENT_SIGNAL_LIMIT = 10
        private const val TAG_WEIGHT = 0.6
        private const val DISTANCE_WEIGHT = 0.4
        private const val DISTANCE_CAP_KM = 15.0
    }
}
