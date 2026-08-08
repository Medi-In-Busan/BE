package com.mediinbusan.app.data.searchhistory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun observeSearchHistory(): Flow<List<SearchHistoryItem>> =
        searchHistoryDao.observeRecentSearches().map { entities -> entities.map { it.toDomain() } }

    override suspend fun recordSearch(keyword: String) {
        val trimmed = keyword.trim()
        if (trimmed.isEmpty()) return
        searchHistoryDao.upsert(SearchHistoryEntity(keyword = trimmed, searchedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteSearch(keyword: String) {
        searchHistoryDao.deleteByKeyword(keyword)
    }
}
