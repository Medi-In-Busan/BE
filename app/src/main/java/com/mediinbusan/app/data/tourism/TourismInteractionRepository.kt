package com.mediinbusan.app.data.tourism

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismInteractionProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface TourismInteractionRepository {
    val profile: Flow<TourismInteractionProfile>
    suspend fun recordCategoryView(category: TourismCatalogCategory)
    suspend fun recordItemSelection(category: TourismCatalogCategory, item: TourismCatalogItem)
    suspend fun setPreferredDistrict(district: BusanDistrict)
}

class TourismInteractionRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TourismInteractionRepository {

    override val profile: Flow<TourismInteractionProfile> = dataStore.data.map { preferences ->
        val categoryViews = TourismCatalogCategory.entries.associateWith { category ->
            preferences[categoryViewKey(category)] ?: 0
        }.filterValues { it > 0 }
        TourismInteractionProfile(
            categoryViews = categoryViews,
            selectedItemIds = preferences[SELECTED_ITEM_IDS].orEmpty(),
            interestKeywords = preferences[INTEREST_KEYWORDS].orEmpty(),
            preferredDistrict = preferences[PREFERRED_DISTRICT]?.let { stored ->
                runCatching { BusanDistrict.valueOf(stored) }.getOrNull()
            }
        )
    }

    override suspend fun recordCategoryView(category: TourismCatalogCategory) {
        dataStore.edit { preferences ->
            val key = categoryViewKey(category)
            preferences[key] = (preferences[key] ?: 0) + 1
        }
    }

    override suspend fun recordItemSelection(category: TourismCatalogCategory, item: TourismCatalogItem) {
        dataStore.edit { preferences ->
            val categoryKey = categoryViewKey(category)
            preferences[categoryKey] = (preferences[categoryKey] ?: 0) + 1
            preferences[SELECTED_ITEM_IDS] = (preferences[SELECTED_ITEM_IDS].orEmpty() + item.id).toList().takeLast(50).toSet()
            preferences[INTEREST_KEYWORDS] = (
                preferences[INTEREST_KEYWORDS].orEmpty() + tokenize(item.title) + tokenize(item.subtitle.orEmpty())
            ).toList().takeLast(40).toSet()
        }
    }

    override suspend fun setPreferredDistrict(district: BusanDistrict) {
        dataStore.edit { it[PREFERRED_DISTRICT] = district.name }
    }

    private companion object {
        const val CATEGORY_VIEW_PREFIX = "tourism_category_view_"
        val SELECTED_ITEM_IDS = stringSetPreferencesKey("tourism_selected_item_ids")
        val INTEREST_KEYWORDS = stringSetPreferencesKey("tourism_interest_keywords")
        val PREFERRED_DISTRICT = stringPreferencesKey("tourism_preferred_district")

        fun categoryViewKey(category: TourismCatalogCategory) =
            intPreferencesKey(CATEGORY_VIEW_PREFIX + category.name.lowercase())

        fun tokenize(value: String): Set<String> = value
            .lowercase()
            .split(Regex("[^가-힣a-z0-9]+"))
            .filter { it.length >= 2 }
            .toSet()
    }
}
