package com.mediinbusan.app.core.common

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Home 카테고리 칩 → 검색 리스트(S-04)로 넘기는 1회성 필터 전달용 싱글턴.
 *
 * medicalPurpose를 Route.HospitalSearchList의 typesafe 인자로 실어 보내는 대신 이걸 쓰는 이유:
 * 바텀바 "홈" 탭이 정상 동작하려면 이 화면 진입은 다른 탭과 동일하게 navigateToTab(popUpTo+saveState+
 * restoreState)을 써야 하는데(core/navigation/Route.kt의 navigateToTab 함수 주석 참고), 그 조합은
 * "예전에 다른 args로 이 목적지를 방문한 적이 있으면 restoreState가 그 예전 상태(그리고 args까지)를
 * 그대로 되살려 이번에 새로 넘긴 args가 무시될 수 있다"는 문제가 있다. Route 인자는 이 문제에 그대로
 * 노출되지만, 이 싱글턴은 Nav 백스택/저장 상태와 무관한 순수 인메모리 값이라 항상 최신 값을 정확히
 * 전달한다.
 *
 * Home이 칩 탭 직후 set()하고, HospitalSearchListViewModel이 화면 진입 시 consume()으로 한 번 읽고
 * 비운다 — 그래서 이후 같은 화면에 순수 재진입(바텀바 탭 재선택 등)했을 때는 다시 적용되지 않는다.
 */
@Singleton
class PendingHospitalSearchFilter @Inject constructor() {
    @Volatile
    private var purpose: MedicalCategory? = null

    fun set(purpose: MedicalCategory) {
        this.purpose = purpose
    }

    fun consume(): MedicalCategory? = purpose.also { purpose = null }
}
