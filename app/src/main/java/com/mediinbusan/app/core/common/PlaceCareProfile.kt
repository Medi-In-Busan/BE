package com.mediinbusan.app.core.common

import com.mediinbusan.app.data.place.PlaceType

/**
 * 장소 유형별 "진료 전후 케어 프로필" — 메디인부산 고유의 정적 큐레이션 데이터 중 첫 번째 축.
 *
 * 웰니스/관광 API(TourAPI 기반)가 내려주는 장소 데이터는 이름·주소·좌표·사진 정도가 전부라,
 * 병원 상세(운영시간·진료과·지원언어·홈페이지)와 달리 상세 화면에 채울 게 거의 없다. 게다가
 * 전화번호·소개문이 비어 오는 장소가 흔해서, 값이 없으면 섹션을 통째로 감추는 기존 규칙과
 * 맞물려 화면이 "사진 + 이름 + 지도"만 남는 경우가 많았다.
 *
 * 그래서 서버 데이터에 의존하지 않고 **장소 유형만으로 항상 결정되는** 정보를 따로 만든다.
 * 일반 관광앱이 주는 평점·영업시간이 아니라, 의료관광객이 진료 전후 일정을 짤 때 실제로
 * 필요한 축(언제쯤 가도 되는지 / 얼마나 몸을 쓰는지 / 실내인지 / 얼마나 머무는지 /
 * 무엇을 조심할지)으로 구성했다 — 이게 이 앱만 줄 수 있는 정보다.
 *
 * 여기엔 표시 문구를 두지 않는다(순수 도메인). 언어별 문구는 core/i18n/PlaceCurationStrings.kt가
 * enum 이름을 키로 들고 있다 — MedicalCategory(core/common) ↔ MedicalCategoryStrings(core/i18n)와 같은 분리다.
 *
 * ⚠️ 의료 자문이 아니다. 어떤 값도 "안전하다"고 단정하지 않으며, 화면에는 항상
 * NearbyStrings.recoveryDisclaimer("개인 상태에 따라 …병원 안내를 우선하세요")가 함께 붙는다.
 */
data class PlaceCareProfile(
    val recoveryFit: RecoveryFit,
    val activityLevel: ActivityLevel,
    val setting: PlaceSetting,
    val stayMinutesMin: Int,
    val stayMinutesMax: Int,
    val cautions: List<PlaceCautionKey>
)

/** 진료·시술 후 언제쯤 방문하기 무난한지. 값이 클수록 뒤로 미루는 게 좋다. */
enum class RecoveryFit {
    /** 진료 당일에도 부담이 적다(짧은 실내 체류·앉아서 쉬는 활동). */
    IMMEDIATE,

    /** 회복 초기(2~3일)가 지난 뒤가 편하다(걷기·야외 노출이 섞인다). */
    AFTER_FEW_DAYS,

    /** 회복이 어느 정도 끝난 뒤를 권한다(온열·장시간·체력 소모가 크다). */
    AFTER_RECOVERY
}

/** 방문에 드는 몸의 부담. */
enum class ActivityLevel { LIGHT, MODERATE, ACTIVE }

/** 실내/실외 — 날씨와 자외선 노출, 이동 편의에 직결된다. */
enum class PlaceSetting { INDOOR, OUTDOOR, MIXED }

/** 진료 전후 관점의 주의 항목. 표시 문구는 PlaceCurationStrings.cautionLabels가 갖는다. */
enum class PlaceCautionKey {
    /** 사우나·온천 등 온열 노출. */
    HEAT_EXPOSURE,

    /** 장시간 자외선 노출(시술 부위 색소침착 등). */
    UV_EXPOSURE,

    /** 오래 걷기. */
    LONG_WALKING,

    /** 계단·경사로가 많아 이동이 불편. */
    STAIRS_SLOPE,

    /** 물에 닿는 활동(상처·시술 부위). */
    WATER_CONTACT,

    /** 진료 전 금식 여부 확인. */
    FASTING,

    /** 맵고 자극적인 음식. */
    SPICY_FOOD,

    /** 혼잡 시간대. */
    CROWDED_HOURS,

    /** 체류가 길어지기 쉬움. */
    LONG_STAY,

    /** 현금만 받는 노점이 섞여 있음. */
    CASH_ONLY_STALLS
}

/**
 * 유형 하나당 프로필 하나. 모든 [PlaceType]을 빠짐없이 덮으므로 이 값은 절대 null이 될 수 없고,
 * 따라서 상세 화면의 "한눈에 보기" 줄은 어떤 장소가 와도 항상 채워진다 — 이 데이터를 만든 이유다.
 */
val PlaceType.careProfile: PlaceCareProfile
    get() = when (this) {
        PlaceType.TOURIST_ATTRACTION -> PlaceCareProfile(
            recoveryFit = RecoveryFit.AFTER_FEW_DAYS,
            activityLevel = ActivityLevel.MODERATE,
            setting = PlaceSetting.MIXED,
            stayMinutesMin = 60,
            stayMinutesMax = 120,
            cautions = listOf(PlaceCautionKey.UV_EXPOSURE, PlaceCautionKey.CROWDED_HOURS, PlaceCautionKey.LONG_WALKING)
        )
        PlaceType.RESTAURANT -> PlaceCareProfile(
            recoveryFit = RecoveryFit.IMMEDIATE,
            activityLevel = ActivityLevel.LIGHT,
            setting = PlaceSetting.INDOOR,
            stayMinutesMin = 40,
            stayMinutesMax = 70,
            cautions = listOf(PlaceCautionKey.FASTING, PlaceCautionKey.SPICY_FOOD, PlaceCautionKey.CROWDED_HOURS)
        )
        PlaceType.SHOPPING -> PlaceCareProfile(
            recoveryFit = RecoveryFit.IMMEDIATE,
            activityLevel = ActivityLevel.MODERATE,
            setting = PlaceSetting.INDOOR,
            stayMinutesMin = 60,
            stayMinutesMax = 150,
            cautions = listOf(PlaceCautionKey.LONG_STAY, PlaceCautionKey.LONG_WALKING)
        )
        PlaceType.LODGING -> PlaceCareProfile(
            recoveryFit = RecoveryFit.IMMEDIATE,
            activityLevel = ActivityLevel.LIGHT,
            setting = PlaceSetting.INDOOR,
            stayMinutesMin = 0,
            stayMinutesMax = 0,
            cautions = emptyList()
        )
        PlaceType.SPA -> PlaceCareProfile(
            recoveryFit = RecoveryFit.AFTER_RECOVERY,
            activityLevel = ActivityLevel.LIGHT,
            setting = PlaceSetting.INDOOR,
            stayMinutesMin = 90,
            stayMinutesMax = 150,
            cautions = listOf(PlaceCautionKey.HEAT_EXPOSURE, PlaceCautionKey.WATER_CONTACT, PlaceCautionKey.LONG_STAY)
        )
        PlaceType.WALK -> PlaceCareProfile(
            recoveryFit = RecoveryFit.AFTER_FEW_DAYS,
            activityLevel = ActivityLevel.MODERATE,
            setting = PlaceSetting.OUTDOOR,
            stayMinutesMin = 40,
            stayMinutesMax = 90,
            cautions = listOf(PlaceCautionKey.LONG_WALKING, PlaceCautionKey.UV_EXPOSURE, PlaceCautionKey.STAIRS_SLOPE)
        )
        PlaceType.OTHER -> PlaceCareProfile(
            recoveryFit = RecoveryFit.AFTER_FEW_DAYS,
            activityLevel = ActivityLevel.LIGHT,
            setting = PlaceSetting.MIXED,
            stayMinutesMin = 40,
            stayMinutesMax = 90,
            cautions = listOf(PlaceCautionKey.CROWDED_HOURS)
        )
    }
