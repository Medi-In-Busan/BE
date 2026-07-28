package com.mediinbusan.app.feature.nearby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalPhone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.MediBlue40
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType

@Composable
fun PlaceDetailScreen(
    placeId: String,
    onBack: () -> Unit,
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(placeId) {
        viewModel.load(placeId)
    }

    PlaceDetailContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.load(placeId) },
        onToggleFavorite = viewModel::onToggleFavorite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceDetailContent(
    uiState: PlaceDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = { Text(text = "장소 상세") },
                actions = {
                    IconButton(onClick = onToggleFavorite, enabled = uiState.place != null) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "즐겨찾기",
                            tint = CoralPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val place = uiState.place
        val errorMessage = uiState.errorMessage
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(innerPadding))
            errorMessage != null -> ErrorState(message = errorMessage, modifier = Modifier.padding(innerPadding), onRetry = onRetry)
            place != null -> PlaceDetailLoaded(
                place = place,
                isFavorite = uiState.isFavorite,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun PlaceDetailLoaded(
    place: Place,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 28.dp)),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PlaceHeroCard(place = place)
        PlaceInfoCard(place = place)
        RecoveryNoticeCard(place = place)
        FavoriteActionButton(isFavorite = isFavorite, onToggleFavorite = onToggleFavorite)
    }
}

@Composable
private fun PlaceHeroCard(place: Place) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = place.type.tint.copy(alpha = 0.14f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = place.type.tint)
            }
            Text(text = place.type.label, style = MaterialTheme.typography.labelMedium, color = place.type.tint)
            Text(text = place.name, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            place.description?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun PlaceInfoCard(place: Place) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DividerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(text = "방문 정보", style = SectionTitleStyle, color = TextPrimary)
            DetailRow(icon = Icons.Default.Place, label = "주소", value = place.address)
            place.distanceFromHospitalMeters?.let {
                DetailRow(icon = Icons.Default.Schedule, label = "병원 기준", value = it.toDistanceLabel())
            }
            place.phoneNumber?.let {
                DetailRow(icon = Icons.Default.LocalPhone, label = "연락처", value = it)
            }
        }
    }
}

@Composable
private fun RecoveryNoticeCard(place: Place) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = CoralPrimaryContainer,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "진료 전후 체크", style = CardTitleStyle, color = TextPrimary)
            Text(text = place.type.recoveryHint, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(
                text = "개인 상태에 따라 적합한 활동이 달라질 수 있으니, 진료 직후 일정은 병원 안내를 우선하세요.",
                style = MaterialTheme.typography.labelSmall,
                color = MediBlue40
            )
        }
    }
}

@Composable
private fun FavoriteActionButton(isFavorite: Boolean, onToggleFavorite: () -> Unit) {
    if (isFavorite) {
        OutlinedButton(onClick = onToggleFavorite, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "즐겨찾기 해제")
        }
    } else {
        Button(onClick = onToggleFavorite, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "즐겨찾기 추가")
        }
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Icon(imageVector = icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = BadgeText)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
    }
}

private val PlaceType.label: String
    get() = when (this) {
        PlaceType.TOURIST_ATTRACTION -> "관광지"
        PlaceType.RESTAURANT -> "카페·맛집"
        PlaceType.SHOPPING -> "쇼핑"
        PlaceType.LODGING -> "숙소"
        PlaceType.SPA -> "스파"
        PlaceType.WALK -> "산책"
        PlaceType.OTHER -> "기타"
    }

private val PlaceType.recoveryHint: String
    get() = when (this) {
        PlaceType.SPA -> "스파·온열 시설은 시술 종류에 따라 제한될 수 있어 이용 전 확인이 필요합니다."
        PlaceType.WALK -> "짧은 산책 중심으로 계획하고, 오래 걷는 일정은 컨디션을 보며 조절하세요."
        PlaceType.RESTAURANT -> "진료 전후 금식이나 자극적인 음식 제한이 있는지 먼저 확인하세요."
        PlaceType.SHOPPING -> "실내 이동은 편하지만 체류 시간이 길어질 수 있어 휴식 시간을 함께 잡는 편이 좋습니다."
        PlaceType.TOURIST_ATTRACTION -> "혼잡한 시간대와 장시간 야외 활동을 피하면 더 편안하게 방문할 수 있습니다."
        PlaceType.LODGING -> "병원과 가까운 숙소는 이동 부담을 줄여 회복 일정에 도움이 됩니다."
        PlaceType.OTHER -> "방문 전 이동 시간과 체류 시간을 짧게 잡아 컨디션을 우선하세요."
    }

private val PlaceType.tint: Color
    get() = when (this) {
        PlaceType.TOURIST_ATTRACTION -> Color(0xFF2F6690)
        PlaceType.RESTAURANT -> Color(0xFF3A7D7B)
        PlaceType.SHOPPING -> Color(0xFF9A5C7F)
        PlaceType.LODGING -> Color(0xFF6A6F4C)
        PlaceType.SPA -> Color(0xFF7C6A9B)
        PlaceType.WALK -> Color(0xFF4F8A5B)
        PlaceType.OTHER -> Color(0xFF667085)
    }

private fun Double.toDistanceLabel(): String =
    if (this < 1000.0) "${toInt()}m" else String.format("%.1fkm", this / 1000.0)
