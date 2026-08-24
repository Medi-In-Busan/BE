package com.mediinbusan.app.feature.tourism

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogItem

@Composable
fun TourismCatalogScreen(
    categoryName: String,
    onSelectItem: () -> Unit,
    onBack: () -> Unit,
    viewModel: TourismCatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(categoryName) { viewModel.load(categoryName) }

    TourismCatalogContent(
        uiState = uiState,
        onDistrictSelected = viewModel::selectDistrict,
        onItemSelected = { item ->
            viewModel.selectItem(item)
            onSelectItem()
        },
        onRetry = viewModel::retry,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TourismCatalogContent(
    uiState: TourismCatalogUiState,
    onDistrictSelected: (BusanDistrict) -> Unit,
    onItemSelected: (TourismCatalogItem) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
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
                title = { Text(uiState.category?.translatedLabel(strings.language) ?: strings.tourism.catalogDefaultTitle) }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(innerPadding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage,
                modifier = Modifier.padding(innerPadding),
                onRetry = onRetry
            )
            uiState.catalog == null || uiState.catalog.items.isEmpty() -> EmptyState(
                message = strings.tourism.emptyResultMessage,
                modifier = Modifier.padding(innerPadding)
            )
            else -> {
                val catalog = uiState.catalog
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        CatalogSummaryCard(
                            title = catalog.title,
                            description = catalog.description,
                            source = strings.tourism.sourceLabels[catalog.source] ?: catalog.source,
                            itemCount = catalog.items.size
                        )
                    }
                    if (uiState.category?.supportsDistrict == true) {
                        item {
                            DistrictFilter(
                                selectedDistrict = uiState.selectedDistrict,
                                onDistrictSelected = onDistrictSelected
                            )
                        }
                    }
                    item {
                        Text(String.format(strings.tourism.resultCountFormat, catalog.items.size), style = SectionTitleStyle, color = TextPrimary)
                    }
                    // 중복 id가 나올 수 있어(원본 API 응답 그대로 정규화) 인덱스를 함께 key에 섞어
                    // Compose 리스트 key 충돌로 인한 크래시를 막는다.
                    itemsIndexed(
                        items = catalog.items,
                        key = { index, item -> "${catalog.category.name}-${item.id}-$index" }
                    ) { _, item ->
                        TourismDataCard(item = item, onClick = { onItemSelected(item) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogSummaryCard(title: String, description: String, source: String, itemCount: Int) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = CoralPrimaryContainer,
        border = BorderStroke(1.dp, Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text(title, style = SectionTitleStyle, color = TextPrimary)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoBadge(source)
                InfoBadge(String.format(strings.tourism.sourceCountFormat, itemCount))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DistrictFilter(selectedDistrict: BusanDistrict?, onDistrictSelected: (BusanDistrict) -> Unit) {
    val strings = LocalAppStrings.current
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text(strings.tourism.districtSectionTitle, style = SectionTitleStyle, color = TextPrimary)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(BusanDistrict.entries, key = { it.name }) { district ->
                FilterChip(
                    selected = selectedDistrict == district,
                    onClick = { onDistrictSelected(district) },
                    label = { Text(district.translatedLabel(strings.language)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CoralPrimaryContainer,
                        selectedLabelColor = CoralPrimary,
                        containerColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun TourismDataCard(item: TourismCatalogItem, onClick: () -> Unit) {
    val strings = LocalAppStrings.current
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item.imageUrl?.let { imageUrl ->
                AsyncImageBox(
                    model = imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxWidth().height(176.dp)
                )
            }
            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = if (item.imageUrl == null) 16.dp else 0.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(item.title, style = CardTitleStyle, color = TextPrimary)
                item.subtitle?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
                item.address?.let { address ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = SkyBlue, modifier = Modifier.height(16.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(address, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                // 카테고리 코드·원본 타임스탬프 같은 원본 API 필드를 그대로 보여주지 않도록,
                // TourismStrings.detailFieldLabels에 사람이 읽을 라벨이 있는 필드만 고른다
                // (없는 필드는 raw key로 대체 표시하지 않고 그냥 숨긴다).
                item.details.entries
                    .mapNotNull { (key, value) -> strings.tourism.detailFieldLabels[key]?.let { it to value } }
                    .take(4)
                    .forEach { (label, value) -> DetailRow(label = label, value = value) }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = SkyBlue, modifier = Modifier.width(72.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun InfoBadge(text: String) {
    Surface(shape = MaterialTheme.shapes.small, color = Color.White.copy(alpha = 0.86f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = CoralPrimary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    }
}
