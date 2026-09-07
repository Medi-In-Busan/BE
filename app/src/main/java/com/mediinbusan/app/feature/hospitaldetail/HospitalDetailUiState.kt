package com.mediinbusan.app.feature.hospitaldetail

import com.mediinbusan.app.data.hospital.Hospital

data class HospitalDetailUiState(
    val isLoading: Boolean = true,
    val hospital: Hospital? = null,
    val isFavorite: Boolean = false,
    // 로드 실패 여부와 서버 메시지를 분리한다. errorMessage가 null이어도 isError가 true면
    // 화면에서 LocalAppStrings 기준 폴백 문구를 그려, 에러 표시 중 언어를 바꿔도 즉시 반영된다.
    val isError: Boolean = false,
    val errorMessage: String? = null,
    // 이 병원 좌표를 기준으로 조회한, 진료과목이 겹치는 주변 병원들(자기 자신 제외).
    // 부가 정보라 실패해도 화면 전체를 오류로 만들지 않는다 — 그냥 섹션이 안 그려진다.
    val nearbyHospitals: List<Hospital> = emptyList(),
    val isNearbyLoading: Boolean = false
)
