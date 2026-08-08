package com.mediinbusan.app.data.searchhistory

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun observeSearchHistory(): Flow<List<SearchHistoryItem>>
    suspend fun recordSearch(keyword: String)
    suspend fun deleteSearch(keyword: String)
}
