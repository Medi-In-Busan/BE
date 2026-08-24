package com.mediinbusan.app.feature.tourism

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.ui.AsyncImageBox
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
    val item = uiState.item
    val category = uiState.category

    LaunchedEffect(uiState.consumed, item) {
        if (uiState.consumed && item == null) {
            onBack()
        }
    }

    if (item == null || category == null) return

    // 오디오(Odii)·GPX(두루누비 걷기 코스)는 details 맵의 원본 필드 이름이 API마다 달라 별도
    // 구조화된 필드가 없다 — http(s) URL로 보이는 값을 찾아 그대로 외부에 위임한다.
    val externalLinkUrl = item.details.values.firstOrNull { it.startsWith("http://") || it.startsWith("https://") }

    Scaffold(
        containerColor = TourismCanvas,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.tourism.backContentDescription)
                    }
                },
                title = { Text(item.title, maxLines = 1) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item.imageUrl?.let { imageUrl ->
                item {
                    AsyncImageBox(
                        model = imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(MaterialTheme.shapes.large)
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.title, style = SectionTitleStyle, color = TextPrimary)
                    item.subtitle?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = TextSecondary) }
                    item.address?.let { address ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SkyBlue, modifier = Modifier.height(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(address, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (item.latitude != null && item.longitude != null || item.address != null) {
                        Button(
                            onClick = {
                                context.launchExternalDirections(
                                    latitude = item.latitude,
                                    longitude = item.longitude,
                                    label = item.title,
                                    fallbackAddress = item.address.orEmpty()
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(strings.tourism.openMapLabel)
                        }
                    }
                    if (externalLinkUrl != null) {
                        val label = when (category) {
                            TourismCatalogCategory.AUDIO -> strings.tourism.listenAudioLabel
                            TourismCatalogCategory.WALKING -> strings.tourism.openGpxLabel
                            else -> strings.tourism.openExternalLinkLabel
                        }
                        val icon = if (category == TourismCatalogCategory.AUDIO) Icons.Default.Headphones else Icons.AutoMirrored.Filled.OpenInNew
                        OutlinedButton(
                            onClick = { context.launchIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse(externalLinkUrl))) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            }
            // 카테고리 코드·원본 타임스탬프 같은 원본 API 필드를 그대로 보여주지 않도록,
            // TourismStrings.detailFieldLabels에 사람이 읽을 라벨이 있는 필드만 고른다
            // (없는 필드는 raw key로 대체 표시하지 않고 그냥 숨긴다).
            val labeledDetails = item.details.entries.mapNotNull { (key, value) ->
                strings.tourism.detailFieldLabels[key]?.let { it to value }
            }
            if (labeledDetails.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = CoralPrimaryContainer,
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            labeledDetails.forEach { (label, value) ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = CoralPrimary,
                                        modifier = Modifier.width(88.dp)
                                    )
                                    Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
