package com.mediinbusan.app.data.tourism

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismInteractionProfile
import com.mediinbusan.app.domain.tourism.TourismItemInteraction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.ln

interface TourismInteractionRepository {
    val profile: Flow<TourismInteractionProfile>
    suspend fun recordCategoryView(category: TourismCatalogCategory)
    suspend fun recordItemSelection(category: TourismCatalogCategory, item: TourismCatalogItem)
    suspend fun setPreferredDistrict(district: BusanDistrict)
}

class TourismInteractionRepositoryImpl internal constructor(
    private val dataStore: DataStore<Preferences>,
    private val clock: () -> Long
) : TourismInteractionRepository {
    @Inject
    constructor(dataStore: DataStore<Preferences>) : this(dataStore, System::currentTimeMillis)

    override val profile: Flow<TourismInteractionProfile> = dataStore.data.map { preferences ->
        val categoryViews = TourismCatalogCategory.entries.associateWith { category ->
            preferences[categoryViewKey(category)] ?: 0
        }.filterValues { it > 0 }
        val categoryLastViewedAt = TourismCatalogCategory.entries.mapNotNull { category ->
            preferences[categoryLastViewedAtKey(category)]?.let { category to it }
        }.toMap()
        val categoryAffinityScores = TourismCatalogCategory.entries.mapNotNull { category ->
            preferences[categoryAffinityKey(category)]?.let { category to it }
        }.toMap()
        TourismInteractionProfile(
            categoryViews = categoryViews,
            categoryAffinityScores = categoryAffinityScores,
            categoryLastViewedAt = categoryLastViewedAt,
            selectedItemIds = preferences[SELECTED_ITEM_IDS].orEmpty(),
            interestKeywords = preferences[INTEREST_KEYWORDS].orEmpty(),
            preferredDistrict = preferences[PREFERRED_DISTRICT]?.let { stored ->
                runCatching { BusanDistrict.valueOf(stored) }.getOrNull()
            },
            itemInteractions = preferences[ITEM_INTERACTIONS].orEmpty()
                .mapNotNull(TourismInteractionCodec::decode)
                .sortedByDescending { it.occurredAtEpochMillis }
        )
    }

    override suspend fun recordCategoryView(category: TourismCatalogCategory) {
        dataStore.edit { preferences ->
            updateCategoryAffinity(preferences, category, clock())
        }
    }

    override suspend fun recordItemSelection(category: TourismCatalogCategory, item: TourismCatalogItem) {
        dataStore.edit { preferences ->
            val now = clock()
            updateCategoryAffinity(preferences, category, now)
            preferences[SELECTED_ITEM_IDS] = (preferences[SELECTED_ITEM_IDS].orEmpty() + item.id).toList().takeLast(50).toSet()
            val interaction = TourismItemInteraction(
                itemId = item.id,
                category = category,
                keywords = tokenize(item.title) + tokenize(item.subtitle.orEmpty()),
                occurredAtEpochMillis = now
            )
            preferences[ITEM_INTERACTIONS] = (
                preferences[ITEM_INTERACTIONS].orEmpty()
                    .mapNotNull(TourismInteractionCodec::decode) + interaction
            ).sortedByDescending { it.occurredAtEpochMillis }
                .take(MAX_INTERACTIONS)
                .map(TourismInteractionCodec::encode)
                .toSet()
        }
    }

    override suspend fun setPreferredDistrict(district: BusanDistrict) {
        dataStore.edit { it[PREFERRED_DISTRICT] = district.name }
    }

    private fun updateCategoryAffinity(
        preferences: androidx.datastore.preferences.core.MutablePreferences,
        category: TourismCatalogCategory,
        now: Long
    ) {
        val countKey = categoryViewKey(category)
        val affinityKey = categoryAffinityKey(category)
        val lastViewedAtKey = categoryLastViewedAtKey(category)
        val previousAffinity = preferences[affinityKey] ?: (preferences[countKey] ?: 0).toDouble()
        val previousAt = preferences[lastViewedAtKey]
        val decayedAffinity = previousAt?.let {
            val age = (now - it).coerceAtLeast(0L).toDouble()
            previousAffinity * exp(-ln(2.0) * age / CATEGORY_HALF_LIFE_MILLIS)
        } ?: previousAffinity * LEGACY_AFFINITY_WEIGHT

        preferences[countKey] = (preferences[countKey] ?: 0) + 1
        preferences[affinityKey] = decayedAffinity + 1.0
        preferences[lastViewedAtKey] = now
    }

    private companion object {
        const val CATEGORY_VIEW_PREFIX = "tourism_category_view_"
        val SELECTED_ITEM_IDS = stringSetPreferencesKey("tourism_selected_item_ids")
        val INTEREST_KEYWORDS = stringSetPreferencesKey("tourism_interest_keywords")
        val ITEM_INTERACTIONS = stringSetPreferencesKey("tourism_item_interactions_v2")
        val PREFERRED_DISTRICT = stringPreferencesKey("tourism_preferred_district")
        const val MAX_INTERACTIONS = 100
        val CATEGORY_HALF_LIFE_MILLIS = TimeUnit.DAYS.toMillis(30).toDouble()
        const val LEGACY_AFFINITY_WEIGHT = 0.5

        fun categoryViewKey(category: TourismCatalogCategory) =
            intPreferencesKey(CATEGORY_VIEW_PREFIX + category.name.lowercase())

        fun categoryLastViewedAtKey(category: TourismCatalogCategory) =
            longPreferencesKey(CATEGORY_VIEW_PREFIX + category.name.lowercase() + "_last_at")

        fun categoryAffinityKey(category: TourismCatalogCategory) =
            doublePreferencesKey(CATEGORY_VIEW_PREFIX + category.name.lowercase() + "_affinity")

        fun tokenize(value: String): Set<String> = value
            .lowercase()
            .split(Regex("[^가-힣a-z0-9]+"))
            .filter { it.length >= 2 }
            .toSet()
    }
}

internal object TourismInteractionCodec {
    private const val SEPARATOR = "|"

    fun encode(interaction: TourismItemInteraction): String = listOf(
        interaction.occurredAtEpochMillis.toString(),
        interaction.category.name,
        escape(interaction.itemId),
        interaction.keywords.joinToString(",") { escape(it) }
    ).joinToString(SEPARATOR)

    fun decode(value: String): TourismItemInteraction? {
        val parts = value.split(SEPARATOR, limit = 4)
        if (parts.size != 4) return null
        val occurredAt = parts[0].toLongOrNull() ?: return null
        val category = runCatching { TourismCatalogCategory.valueOf(parts[1]) }.getOrNull() ?: return null
        return TourismItemInteraction(
            itemId = unescape(parts[2]),
            category = category,
            keywords = parts[3].split(',')
                .filter(String::isNotBlank)
                .map(::unescape)
                .toSet(),
            occurredAtEpochMillis = occurredAt
        )
    }

    private fun escape(value: String): String = value
        .replace("%", "%25")
        .replace(SEPARATOR, "%7C")
        .replace(",", "%2C")

    private fun unescape(value: String): String = value
        .replace("%2C", ",")
        .replace("%7C", SEPARATOR)
        .replace("%25", "%")
}
