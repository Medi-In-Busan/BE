package com.mediinbusan.app.data.place

import com.mediinbusan.app.core.common.Result
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getNearbyPlaces(hospitalId: String): Flow<Result<List<Place>>>
    fun getPlaceDetail(placeId: String): Flow<Result<Place>>
}
