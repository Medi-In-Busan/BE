package com.mediinbusan.app.feature.tourism

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourismCatalogItemDetailScreen(
    onBack: () -> Unit,
    viewModel: TourismCatalogItemDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    LaunchedEffect(uiState.consumed, uiState.selectedTitle) {
        if (uiState.consumed && uiState.selectedTitle == null) onBack()
    }
    if (uiState.selectedTitle == null) return

    Scaffold(
        containerColor = HomeBackgroundPink,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.tourism.backContentDescription
                        )
                    }
                },
                title = {
                    Text(
                        text = uiState.category?.translatedLabel(strings.language)
                            ?: strings.tourism.catalogDefaultTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(Modifier.padding(innerPadding))
            uiState.loadFailed -> ErrorState(
                strings.tourism.placeMatchErrorMessage,
                Modifier.padding(innerPadding),
                viewModel::retry
            )
            uiState.matchNotFound -> EmptyState(
                strings.tourism.placeMatchNotFoundMessage,
                Modifier.padding(innerPadding)
            )
            uiState.item != null && uiState.category != null -> TourismDetailLoaded(
                item = uiState.item!!,
                category = uiState.category!!,
                modifier = Modifier.padding(innerPadding),
                onOpenMap = {
                    context.launchExternalDirections(
                        latitude = uiState.item!!.latitude,
                        longitude = uiState.item!!.longitude,
                        label = uiState.item!!.title,
                        fallbackAddress = uiState.item!!.address.orEmpty()
                    )
                },
                onOpenLink = { url ->
                    context.launchIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            )
        }
    }
}

@Composable
private fun TourismDetailLoaded(
    item: TourismCatalogItem,
    category: TourismCatalogCategory,
    onOpenMap: () -> Unit,
    onOpenLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val externalLinkUrl = item.details["homepage"]
        ?: item.details.values.firstOrNull { it.startsWith("http://") || it.startsWith("https://") }
    val labeledDetails = item.details.entries.mapNotNull { (key, value) ->
        strings.tourism.detailFieldLabels[key]?.let { label -> DetailValue(key, label, value) }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { TourismHero(item = item, category = category) }
        item.address?.let { address -> item { AddressCard(address) } }
        item.details["congestionRate"]?.let { congestionRate ->
            item {
                CongestionCard(
                    label = strings.tourism.detailFieldLabels["congestionRate"].orEmpty(),
                    value = congestionRate,
                    dateLabel = strings.tourism.detailFieldLabels["baseYmd"],
                    date = item.details["baseYmd"]
                )
            }
        }
        item {
            ActionButtons(
                canOpenMap = item.latitude != null && item.longitude != null || item.address != null,
                externalLinkUrl = externalLinkUrl,
                category = category,
                onOpenMap = onOpenMap,
                onOpenLink = onOpenLink
            )
        }
        item.subtitle?.takeIf { it.isNotBlank() }?.let { description ->
            item { DescriptionCard(description) }
        }
        val secondaryDetails = labeledDetails.filterNot { it.key in setOf("congestionRate", "baseYmd") }
        if (secondaryDetails.isNotEmpty()) item { DetailInfoCard(secondaryDetails) }
    }
}

@Composable
private fun TourismHero(item: TourismCatalogItem, category: TourismCatalogCategory) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(258.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color(0xFFEAF5FF))))
    ) {
        if (item.imageUrl != null) {
            AsyncImageBox(item.imageUrl, item.title, Modifier.fillMaxSize())
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.04f), Color.Black.copy(alpha = 0.76f))
                    )
                )
            )
        } else {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = CoralPrimary.copy(alpha = 0.24f),
                modifier = Modifier.align(Alignment.Center).size(86.dp)
            )
        }
        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(18.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.94f)
        ) {
            Text(
                category.translatedLabel(LocalAppStrings.current.language),
                style = MaterialTheme.typography.labelMedium,
                color = CoralPrimary,
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
            )
        }
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            val onImage = item.imageUrl != null
            Text(
                item.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (onImage) Color.White else TextPrimary
            )
        }
    }
}

@Composable
private fun AddressCard(address: String) {
    DetailSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.LocationOn, null, tint = CoralPrimary, modifier = Modifier.size(22.dp))
            Text(address, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CongestionCard(label: String, value: String, dateLabel: String?, date: String?) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = CoralPrimaryContainer,
        border = BorderStroke(1.dp, Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = CoralPrimary)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = CoralPrimary)
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (dateLabel != null && date != null) {
                    Text("$dateLabel $date", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    canOpenMap: Boolean,
    externalLinkUrl: String?,
    category: TourismCatalogCategory,
    onOpenMap: () -> Unit,
    onOpenLink: (String) -> Unit
) {
    val strings = LocalAppStrings.current.tourism
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (canOpenMap) {
            Button(
                onClick = onOpenMap,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Map, null, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(8.dp))
                Text(strings.openMapLabel)
            }
        }
        if (externalLinkUrl != null) {
            val label = when (category) {
                TourismCatalogCategory.AUDIO -> strings.listenAudioLabel
                TourismCatalogCategory.WALKING -> strings.openGpxLabel
                else -> strings.openExternalLinkLabel
            }
            val icon = if (category == TourismCatalogCategory.AUDIO) {
                Icons.Default.Headphones
            } else {
                Icons.AutoMirrored.Filled.OpenInNew
            }
            OutlinedButton(
                onClick = { onOpenLink(externalLinkUrl) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, CoralPrimary)
            ) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = CoralPrimary)
                Spacer(Modifier.width(8.dp))
                Text(label, color = CoralPrimary)
            }
        }
    }
}

@Composable
private fun DescriptionCard(description: String) {
    DetailSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, null, tint = CoralPrimary, modifier = Modifier.size(22.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun DetailInfoCard(details: List<DetailValue>) {
    DetailSurface {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            details.forEachIndexed { index, detail ->
                if (index > 0) HorizontalDivider(color = DividerColor)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(detail.icon, null, tint = CoralPrimary, modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(detail.label, style = MaterialTheme.typography.labelSmall, color = BadgeText)
                        Text(detail.value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSurface(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = Color.White,
        border = BorderStroke(1.dp, DividerColor),
        shadowElevation = 1.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) { content() }
    }
}

private data class DetailValue(val key: String, val label: String, val value: String) {
    val icon: ImageVector
        get() = when (key) {
            "tel" -> Icons.Default.Phone
            "distance", "crsDstnc" -> Icons.Default.Route
            "requiredTime", "leadTime", "crsTotlRqrmHour" -> Icons.Default.AccessTime
            "baseYmd", "baseYm", "daywkDivNm" -> Icons.Default.CalendarToday
            else -> Icons.Default.Info
        }
}
