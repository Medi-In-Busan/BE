package com.mediinbusan.app.core.common

import java.util.concurrent.ConcurrentHashMap

/**
 * 프로세스 메모리에만 잠깐 들고 있는 아주 단순한 TTL 캐시. Room에 넣을 만큼 오래 보존할 데이터는
 * 아니다(Hospital/Place는 core/common/MedicalCategory.kt 주석에 문서화된 대로 Room 엔티티가 아니고
 * 매 세션 API에서 조회한다) — 다만 화면을 오갈 때마다 같은 전체 목록을 매번 네트워크로 다시 받아오는
 * 게 체감 로딩 지연의 원인일 때, @Singleton 리포지토리의 필드로 두면 앱이 살아있는 동안 유지되는
 * 캐시로 그 재조회를 줄여준다.
 */
class TtlCache<K, V>(private val ttlMillis: Long) {
    private val entries = ConcurrentHashMap<K, Entry<V>>()

    fun get(key: K): V? {
        val entry = entries[key] ?: return null
        if (System.currentTimeMillis() - entry.storedAt > ttlMillis) {
            entries.remove(key)
            return null
        }
        return entry.value
    }

    fun put(key: K, value: V) {
        entries[key] = Entry(value, System.currentTimeMillis())
    }

    private data class Entry<V>(val value: V, val storedAt: Long)
}
