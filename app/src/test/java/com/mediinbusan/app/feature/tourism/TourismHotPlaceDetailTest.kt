package com.mediinbusan.app.feature.tourism

import com.mediinbusan.app.core.common.PendingTourismCatalogItem
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.data.tourism.TourismCatalogRepository
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalog
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem
import com.mediinbusan.app.domain.tourism.TourismHotPlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TourismHotPlaceDetailTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var pending: PendingTourismCatalogItem

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        pending = PendingTourismCatalogItem()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun everyTopFiveSelectionLoadsTheMatchingTourismDetail() = runTest {
        (1..5).forEach { rank ->
            val hotPlace = hotPlace("crowding-$rank", 100.0 - rank)
            pending.setHotPlace(hotPlace)
            val matched = tourismItem("tour-$rank", hotPlace.item.title)
            val repository = FakeRepository(Result.Success(matched))

            val viewModel = TourismCatalogItemDetailViewModel(pending, repository)
            assertTrue(viewModel.uiState.value.isLoading)
            advanceUntilIdle()
            val state = viewModel.uiState.value

            assertEquals(hotPlace.item.title, repository.requestedTitle)
            assertEquals(hotPlace.district, repository.requestedDistrict)
            assertEquals(TourismCatalogCategory.PLACES_KO, state.category)
            assertEquals(matched.id, state.item?.id)
            assertEquals(hotPlace.district.label, state.item?.details?.get("signguNm"))
            assertEquals(hotPlace.congestionRate.toString(), state.item?.details?.get("congestionRate"))
            assertFalse(state.isLoading)
            assertNull(pending.consume())
        }
    }

    @Test
    fun unmatchedPlaceShowsNotFoundInsteadOfCrowdingRowAsDetail() = runTest {
        pending.setHotPlace(hotPlace("crowding", 82.0))
        val viewModel = TourismCatalogItemDetailViewModel(pending, FakeRepository(Result.Success(null)))

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.matchNotFound)
        assertNull(viewModel.uiState.value.item)
        assertNull(viewModel.uiState.value.category)
    }

    @Test
    fun networkFailureCanBeRetried() = runTest {
        pending.setHotPlace(hotPlace("crowding", 82.0))
        val repository = FakeRepository(Result.Error(IllegalStateException("offline")))
        val viewModel = TourismCatalogItemDetailViewModel(pending, repository)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.loadFailed)

        repository.result = Result.Success(tourismItem("tour", "Tourist attraction crowding"))
        viewModel.retry()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.loadFailed)
        assertEquals("tour", viewModel.uiState.value.item?.id)
        assertEquals(2, repository.requestCount)
    }

    @Test
    fun ordinaryCatalogSelectionDoesNotCallMatcher() = runTest {
        pending.set(TourismCatalogCategory.ACCESSIBLE, tourismItem("accessible", "Accessible place"))
        val repository = FakeRepository(Result.Error())

        val state = TourismCatalogItemDetailViewModel(pending, repository).uiState.value
        advanceUntilIdle()

        assertEquals("accessible", state.item?.id)
        assertEquals(TourismCatalogCategory.ACCESSIBLE, state.category)
        assertFalse(state.isLoading)
        assertEquals(0, repository.requestCount)
    }

    @Test
    fun selectionPreservesSourceAndAddsInternalMatchDistrict() {
        val hotPlace = hotPlace("beach", 82.5)
        pending.setHotPlace(hotPlace)

        val item = pending.consume()!!.second

        assertEquals(BusanDistrict.HAEUNDAE.name, item.details["hotPlaceDistrict"])
        assertEquals(BusanDistrict.HAEUNDAE.label, item.details["signguNm"])
        assertEquals("82.5", item.details["congestionRate"])
        assertNull(hotPlace.item.details["hotPlaceDistrict"])
    }

    @Test
    fun detailWithoutSelectionStillSignalsEmptyState() {
        val state = TourismCatalogItemDetailViewModel(pending, FakeRepository(Result.Error())).uiState.value

        assertTrue(state.consumed)
        assertNull(state.selectedTitle)
        assertNull(state.category)
        assertNull(state.item)
    }

    private fun hotPlace(id: String, rate: Double) = TourismHotPlace(
        item = tourismItem(id, "Tourist attraction $id").copy(details = mapOf("tatsCnctrRate" to rate.toString())),
        district = BusanDistrict.HAEUNDAE,
        congestionRate = rate
    )

    private fun tourismItem(id: String, title: String) = TourismCatalogItem(
        id = id,
        title = title,
        subtitle = "Tourism description",
        address = "Busan address",
        imageUrl = "https://example.com/place.jpg",
        latitude = 35.158,
        longitude = 129.16,
        details = mapOf("homepage" to "https://example.com")
    )

    private class FakeRepository(var result: Result<TourismCatalogItem?>) : TourismCatalogRepository {
        var requestedTitle: String? = null
        var requestedDistrict: BusanDistrict? = null
        var requestCount = 0

        override suspend fun findMatchingPlace(
            title: String,
            district: BusanDistrict
        ): Result<TourismCatalogItem?> {
            requestCount++
            requestedTitle = title
            requestedDistrict = district
            return result
        }

        override fun getCatalog(
            category: TourismCatalogCategory,
            district: BusanDistrict?
        ): Flow<Result<TourismCatalog>> = flowOf(Result.Error())
    }
}
