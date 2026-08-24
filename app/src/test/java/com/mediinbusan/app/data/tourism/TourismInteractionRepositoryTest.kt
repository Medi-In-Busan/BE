package com.mediinbusan.app.data.tourism

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.util.concurrent.TimeUnit

class TourismInteractionRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `카테고리 조회와 항목 선택을 시각 및 키워드와 함께 저장한다`() = runBlocking {
        val dataStore = PreferenceDataStoreFactory.create(scope = scope) {
            temporaryFolder.root.resolve("tourism.preferences_pb")
        }
        val repository = TourismInteractionRepositoryImpl(dataStore)
        val before = System.currentTimeMillis()

        repository.recordCategoryView(TourismCatalogCategory.WALKING)
        repository.recordItemSelection(
            TourismCatalogCategory.WALKING,
            TourismCatalogItem(
                id = "course-42",
                title = "해운대 바다 산책",
                subtitle = "가벼운 걷기",
                address = null,
                imageUrl = null,
                latitude = null,
                longitude = null,
                details = emptyMap()
            )
        )

        val profile = repository.profile.first()
        assertEquals(2, profile.categoryViews[TourismCatalogCategory.WALKING])
        assertTrue(requireNotNull(profile.categoryAffinityScores[TourismCatalogCategory.WALKING]) in 1.99..2.0)
        assertTrue(requireNotNull(profile.categoryLastViewedAt[TourismCatalogCategory.WALKING]) >= before)
        assertEquals("course-42", profile.itemInteractions.single().itemId)
        assertTrue("해운대" in profile.itemInteractions.single().keywords)
        assertTrue("가벼운" in profile.itemInteractions.single().keywords)
    }

    @Test
    fun `오래된 카테고리 조회 점수는 새 조회 전에 감쇠되어 과거 횟수가 되살아나지 않는다`() = runBlocking {
        val dataStore = PreferenceDataStoreFactory.create(scope = scope) {
            temporaryFolder.root.resolve("decay.preferences_pb")
        }
        var now = 1_000_000_000_000L
        val repository = TourismInteractionRepositoryImpl(dataStore, { now })

        repository.recordCategoryView(TourismCatalogCategory.WALKING)
        now += TimeUnit.DAYS.toMillis(60)
        repository.recordCategoryView(TourismCatalogCategory.WALKING)

        val affinity = requireNotNull(
            repository.profile.first().categoryAffinityScores[TourismCatalogCategory.WALKING]
        )
        assertTrue(affinity in 1.24..1.26)
    }
}
