package com.mediinbusan.app.data.searchhistory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT 10")
    fun observeRecentSearches(): Flow<List<SearchHistoryEntity>>

    // keyword가 PK라 REPLACE로 upsert하면 기존 검색어가 searchedAt만 갱신되며 자연히 최신순 맨 위로 올라온다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE keyword = :keyword")
    suspend fun deleteByKeyword(keyword: String)
}
