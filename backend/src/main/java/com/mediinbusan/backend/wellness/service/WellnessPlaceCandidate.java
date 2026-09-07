package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceType;

import java.time.LocalDate;

record WellnessPlaceCandidate(
    String contentId,
    String name,
    WellnessPlaceType placeType,
    // TourAPI cat3 원본 코드. TourAPI가 아닌 소스(부산맛집정보 등)에서 온 후보는 null이다.
    String categoryCode,
    String address,
    Coordinates coordinates,
    String imageUrl,
    String description,
    String phoneNumber,
    LocalDate modifiedDate,
    // 영업시간·휴무일·대표메뉴 등 상세 화면용 방문 정보. 소스가 안 주면 WellnessVisitInfo.EMPTY다.
    WellnessVisitInfo visitInfo
) {
    boolean isValid() {
        return hasText(contentId)
            && hasText(name)
            && placeType != null
            && hasText(address);
    }

    /**
     * areaBasedList2엔 전화번호·상세설명·방문 정보가 없어, detailCommon2/detailIntro2로 따로 받아온
     * 값을 채워 넣을 때 쓴다. 값이 null이면(그 항목만 상세 조회를 못 했거나 실제로 비어있는 경우)
     * 기존 값을 유지한다 — 방문 정보도 칸 단위로 같은 규칙이다(WellnessVisitInfo.merge).
     */
    WellnessPlaceCandidate withDetail(String phoneNumber, String description, WellnessVisitInfo visitInfo) {
        return new WellnessPlaceCandidate(
            contentId,
            name,
            placeType,
            categoryCode,
            address,
            coordinates,
            imageUrl,
            description != null ? description : this.description,
            phoneNumber != null ? phoneNumber : this.phoneNumber,
            modifiedDate,
            this.visitInfo.merge(visitInfo)
        );
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
