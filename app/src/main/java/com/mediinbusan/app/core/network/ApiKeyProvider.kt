package com.mediinbusan.app.core.network

import com.mediinbusan.app.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/** local.properties -> BuildConfig로 주입된 한국관광공사 서비스 키를 리포지토리에 노출한다. */
@Singleton
class ApiKeyProvider @Inject constructor() {
    val tourismApiServiceKey: String
        get() = BuildConfig.TOURISM_API_SERVICE_KEY
}
