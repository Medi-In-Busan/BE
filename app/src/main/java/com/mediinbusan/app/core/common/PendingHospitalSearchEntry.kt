package com.mediinbusan.app.core.common

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Home → 검색 리스트(S-04)로 진입할 때 함께 전달해야 하는 1회성 정보(의료목적 필터, 검색 입력창
 * 자동 포커스 요청)를 담는 싱글턴.
 *
 * Route.HospitalSearchList의 typesafe 인자로 이런 값을 실어 보내는 대신 이걸 쓰는 이유: 바텀바 "홈"
 * 탭이 정상 동작하려면 이 화면으로 가는 모든 경로가 다른 탭과 동일하게 navigateToTab(popUpTo+
 * saveState+restoreState)을 써야 하는데(core/navigation/Route.kt의 navigateToTab 함수 주석 참고),
 * 그 조합은 "예전에 다른 args로 이 목적지를 방문한 적이 있으면 restoreState가 그 예전 상태(그리고
 * args까지)를 그대로 되살려 이번에 새로 넘긴 args가 무시될 수 있다"는 문제가 있다. 반대로 일부 진입
 * 경로만 순수 navigate()로 우회하면(과거 검색바 전용 진입점이 그랬다) 이번엔 "홈" 탭이 못 빠져나오는
 * 문제가 생긴다 — 그래서 Route.HospitalSearchList는 항상 인자 없이 navigateToTab으로만 열고, 이런
 * 1회성 값은 Nav 백스택/저장 상태와 무관한 이 순수 인메모리 싱글턴으로 전달한다.
 *
 * Home이 칩 탭/검색바 탭 직후 set*()/request*()를 부르고, HospitalSearchListViewModel이 화면 진입
 * 시 consume*()으로 한 번 읽고 비운다 — 그래서 이후 같은 화면에 순수 재진입(바텀바 탭 재선택 등)
 * 했을 때는 다시 적용되지 않는다.
 */
@Singleton
class PendingHospitalSearchEntry @Inject constructor() {
    @Volatile
    private var purpose: MedicalCategory? = null

    @Volatile
    private var focusRequested: Boolean = false

    fun setPurpose(purpose: MedicalCategory) {
        this.purpose = purpose
    }

    fun requestFocus() {
        focusRequested = true
    }

    fun consumePurpose(): MedicalCategory? = purpose.also { purpose = null }

    fun consumeFocusRequest(): Boolean = focusRequested.also { focusRequested = false }
}
