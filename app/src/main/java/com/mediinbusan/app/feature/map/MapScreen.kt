package com.mediinbusan.app.feature.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.FavoriteHeartButton
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LanguageBadge
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.toLanguageBadgeLabel
import com.mediinbusan.app.data.hospital.Hospital
import com.mediinbusan.app.data.place.Place

/**
 * S-08. hospitalId가 있으면 상세페이지 '지도에서 보기'로 진입한 "특정 병원 지도" 모드,
 * 없으면 하단 탭 '지도'로 진입한 "전체 병원 브라우징" 모드다(Route.MapView 문서 참고).
 */
@Composable
fun MapScreen(
    hospitalId: String?,
    onSelectHospital: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.errorMessage

    LaunchedEffect(hospitalId) {
        viewModel.load(hospitalId)
    }

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(hospitalId) })
        hospitalId != null -> {
            val hospital = uiState.focusedHospital
            if (hospital != null) {
                FocusedHospitalMap(hospital = hospital, nearbyPlaces = uiState.nearbyPlaces, onBack = onBack)
            }
        }
        else -> BrowseMap(
            uiState = uiState,
            onCategorySelected = viewModel::onCategorySelected,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onMarkerSelected = viewModel::onMarkerSelected,
            onToggleFavorite = viewModel::onToggleFavorite,
            onSelectHospital = onSelectHospital
        )
    }
}

@Composable
private fun FocusedHospitalMap(hospital: Hospital, nearbyPlaces: List<Place>, onBack: () -> Unit) {
    val pins = remember(hospital, nearbyPlaces) {
        buildList {
            hospital.toMapPin(selectedId = hospital.id)?.let(::add)
            nearbyPlaces.forEach { place -> place.toMapPin(selectedId = null)?.let(::add) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KakaoMapView(pins = pins, modifier = Modifier.fillMaxSize())

        RoundIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.medium).background(Color(0xFFE9E9EE))
                ) {
                    AsyncImageBox(model = hospital.imageUrl, contentDescription = hospital.name, modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = hospital.name, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = hospital.districtLabel(), style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                }
                val context = LocalContext.current
                RoundIconButton(
                    icon = Icons.Default.Navigation,
                    contentDescription = "길찾기",
                    onClick = {
                        context.launchExternalDirections(
                            latitude = hospital.latitude,
                            longitude = hospital.longitude,
                            label = hospital.name,
                            fallbackAddress = hospital.address
                        )
                    },
                    background = CoralPrimary,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun BrowseMap(
    uiState: MapUiState,
    onCategorySelected: (MapCategory) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onMarkerSelected: (String?) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onSelectHospital: (String) -> Unit
) {
    val hospitalPins = remember(uiState.visibleHospitals, uiState.selectedMarkerId) {
        uiState.visibleHospitals.mapNotNull { it.toMapPin(uiState.selectedMarkerId) }
    }
    val placePins = remember(uiState.visiblePlaces, uiState.selectedMarkerId) {
        uiState.visiblePlaces.mapNotNull { it.toMapPin(uiState.selectedMarkerId) }
    }
    val pins = if (uiState.selectedCategory == MapCategory.HOSPITAL) hospitalPins else placePins

    Box(modifier = Modifier.fillMaxSize().padding(bottom = BottomNavBarHeight)) {
        KakaoMapView(
            pins = pins,
            modifier = Modifier.fillMaxSize(),
            onPinClick = onMarkerSelected
        )

        Column(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MapSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterPillButton()
            }
            Spacer(modifier = Modifier.height(12.dp))
            CategoryTabsRow(selected = uiState.selectedCategory, onSelected = onCategorySelected)
        }

        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 100.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoundIconButton(icon = Icons.Default.Layers, contentDescription = "레이어", onClick = {}, shape = MaterialTheme.shapes.medium)
            // 실제 GPS 위치가 아닌 고정된 부산 기본 좌표로만 이동한다 — 위치 권한을 쓰지 않는다.
            RoundIconButton(
                icon = Icons.Default.MyLocation,
                contentDescription = "기본 위치로 이동",
                onClick = { onMarkerSelected(null) },
                background = CoralPrimary,
                tint = Color.White,
                shape = MaterialTheme.shapes.medium
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            when (uiState.selectedCategory) {
                MapCategory.HOSPITAL -> {
                    val selected = uiState.visibleHospitals.firstOrNull { it.id == uiState.selectedMarkerId }
                    when {
                        selected != null -> SelectedHospitalCard(
                            hospital = selected,
                            isFavorite = selected.id in uiState.favoriteHospitalIds,
                            onFavoriteClick = { onToggleFavorite(selected.id) },
                            onDetailClick = { onSelectHospital(selected.id) }
                        )
                        uiState.visibleHospitals.isNotEmpty() -> HospitalCardRow(
                            hospitals = uiState.visibleHospitals,
                            onCardClick = { onMarkerSelected(it) }
                        )
                        else -> EmptyResultCard(message = "표시할 병원이 없습니다.")
                    }
                }
                else -> {
                    val selected = uiState.visiblePlaces.firstOrNull { it.id == uiState.selectedMarkerId }
                    when {
                        selected != null -> SelectedPlaceCard(place = selected)
                        uiState.visiblePlaces.isNotEmpty() -> PlaceCardRow(
                            places = uiState.visiblePlaces,
                            onCardClick = { onMarkerSelected(it) }
                        )
                        else -> EmptyResultCard(message = "표시할 장소가 없습니다.")
                    }
                }
            }
        }
    }
}

@Composable
private fun MapSearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(text = "지도에서 검색...", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                cursorBrush = SolidColor(CoralPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FilterPillButton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(CoralPrimary)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.FilterList, contentDescription = "필터", tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "필터", style = MaterialTheme.typography.labelMedium, color = Color.White)
    }
}

@Composable
private fun CategoryTabsRow(selected: MapCategory, onSelected: (MapCategory) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CategoryTab(label = "병원", icon = Icons.Default.LocalHospital, selected = selected == MapCategory.HOSPITAL, onClick = { onSelected(MapCategory.HOSPITAL) })
        CategoryTab(label = "관광", icon = Icons.Default.Park, selected = selected == MapCategory.TOURIST, onClick = { onSelected(MapCategory.TOURIST) })
        CategoryTab(label = "음식", icon = Icons.Default.Restaurant, selected = selected == MapCategory.FOOD, onClick = { onSelected(MapCategory.FOOD) })
    }
}

@Composable
private fun CategoryTab(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) CoralPrimary else Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = if (selected) Color.White else TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = if (selected) Color.White else TextPrimary)
    }
}

@Composable
private fun RoundIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.White,
    tint: Color = TextPrimary,
    shape: androidx.compose.ui.graphics.Shape = CircleShape
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(shape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
    }
}

@Composable
private fun HospitalCardRow(hospitals: List<Hospital>, onCardClick: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(hospitals, key = { it.id }) { hospital ->
            InfoMiniCard(
                title = hospital.name,
                subtitle = hospital.districtLabel(),
                languages = hospital.supportedLanguages,
                onClick = { onCardClick(hospital.id) }
            )
        }
    }
}

@Composable
private fun PlaceCardRow(places: List<Place>, onCardClick: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(places, key = { it.id }) { place ->
            InfoMiniCard(
                title = place.name,
                subtitle = place.address,
                languages = emptyList(),
                onClick = { onCardClick(place.id) }
            )
        }
    }
}

@Composable
private fun InfoMiniCard(title: String, subtitle: String, languages: List<String>, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .clip(MaterialTheme.shapes.large)
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(text = title, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (languages.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                languages.take(3).forEach { lang -> LanguageBadge(text = lang.toLanguageBadgeLabel()) }
            }
        }
    }
}

@Composable
private fun SelectedHospitalCard(
    hospital: Hospital,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = hospital.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = hospital.districtLabel(), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            if (hospital.supportedLanguages.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    hospital.supportedLanguages.take(3).forEach { lang -> LanguageBadge(text = lang.toLanguageBadgeLabel()) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
                ) {
                    Text(text = "상세보기", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).border(width = 1.dp, color = DividerColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    FavoriteHeartButton(isFavorite = isFavorite, onClick = onFavoriteClick, size = 32.dp)
                }
            }
        }
    }
}

// Place는 아직 전용 상세 진입 동선이 지도에 배선되어 있지 않아, 병원 카드와 달리 정보 표시만 한다.
@Composable
private fun SelectedPlaceCard(place: Place) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = place.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            place.description?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun EmptyResultCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

private fun Hospital.districtLabel(): String {
    val district = address.split(" ").firstOrNull { it.endsWith("구") || it.endsWith("군") || it.endsWith("시") }
    val category = specialties.firstOrNull()
    return listOfNotNull(category, district).joinToString(" · ").ifBlank { address }
}

private fun Hospital.toMapPin(selectedId: String?): MapPin? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null
    return MapPin(id = id, latitude = lat, longitude = lng, type = MapPinType.HOSPITAL, selected = id == selectedId)
}

private fun Place.toMapPin(selectedId: String?): MapPin? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null
    return MapPin(id = id, latitude = lat, longitude = lng, type = MapPinType.PLACE, selected = id == selectedId)
}
