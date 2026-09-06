package com.mediinbusan.app.core.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

/**
 * 디버그 빌드의 BODY 로깅에서 민감한 엔드포인트만 빼고 로그를 남긴다.
 *
 * 문서 스캔(OCR)은 응답 본문이 진단서·처방전 원문 전체이고 주민등록번호까지 들어 있다. 화면에서는
 * [com.mediinbusan.app.feature.documentscan.maskSensitiveText]로 가리는 값이 BODY 로깅에서는
 * logcat에 평문으로 그대로 남는다 — 릴리즈는 NONE이라 안전하지만 디버그 APK는 팀에 그대로 돌아간다.
 * 자가진단 챗봇도 사용자가 입력한 증상·건강 상태가 본문에 실린다.
 *
 * 로깅을 아예 끄지는 않는다. 그 엔드포인트만 [HttpLoggingInterceptor.Level.BASIC]으로 낮춰
 * 메서드·URL·응답 코드·소요 시간은 남기므로, 디버깅에 필요한 "호출이 갔는지/몇 초 걸렸는지"는
 * 그대로 볼 수 있고 내용만 빠진다.
 */
internal class SensitivePathLoggingInterceptor(
    private val defaultLogger: HttpLoggingInterceptor,
    private val sensitiveLogger: HttpLoggingInterceptor,
    private val sensitivePaths: List<String>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath
        val logger = if (sensitivePaths.any { path.contains(it) }) sensitiveLogger else defaultLogger
        return logger.intercept(chain)
    }
}
