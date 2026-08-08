package com.mediinbusan.app.data.searchhistory

data class SearchHistoryItem(
    val keyword: String,
    val searchedAt: Long
)

fun SearchHistoryEntity.toDomain(): SearchHistoryItem = SearchHistoryItem(
    keyword = keyword,
    searchedAt = searchedAt
)
