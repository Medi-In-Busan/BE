package com.mediinbusan.app.core.common

/** 모든 리포지토리/뷰모델이 로딩·성공·에러(F-019 오류 처리)를 표현하는 공용 래퍼. */
sealed interface Result<out T> {
    data object Loading : Result<Nothing>
    data class Success<T>(val data: T) : Result<T>
    data class Error(val throwable: Throwable? = null, val message: String? = null) : Result<Nothing>
}
