package com.mediinbusan.app.feature.tourism

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
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
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory

@Composable
fun TourismCatalogScreen(
    categoryName: String,
    onNavigateToCourse: (category: String, district: String?) -> Unit,
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
        onNavigateToCourse = {
            uiState.category?.let { category ->
                onNavigateToCourse(category.name, uiState.selectedDistrict?.name)
            }
        },
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
    onNavigateToCourse: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    Scaffold(
        containerColor = HomeBackgroundPink,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.tourism.backContentDescription)
                    }
                },
                title = { Text(uiState.category?.label ?: "관광 데이터") },
                actions = {
                    val canBuildCourse = uiState.catalog?.items?.count {
                        it.latitude != null && it.longitude != null
                    }?.let { it >= 3 } == true
                    IconButton(onClick = onNavigateToCourse, enabled = canBuildCourse) {
                        Icon(Icons.Default.Map, contentDescription = "추천 장소 동선 보기")
                    }
                }
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
                    ) { index, item ->
                        TourismDataCard(
                            item = item,
                            rank = index + 1,
                            isCrowding = catalog.category == TourismCatalogCategory.CROWDING,
                            onClick = { onItemSelected(item) }
                        )
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
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White)
    ) {
        Box(
            modifier = Modifier.background(
                Brush.linearGradient(listOf(Color(0xFFFFE7E9), Color(0xFFFFF8F8), Color(0xFFEAF5FF)))
            )
        ) {
            Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(title, style = SectionTitleStyle, color = TextPrimary)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoBadge(source)
                    InfoBadge(String.format(strings.tourism.sourceCountFormat, itemCount))
                }
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
private fun TourismDataCard(item: TourismCatalogItem, rank: Int, isCrowding: Boolean, onClick: () -> Unit) {
    if (isCrowding) {
        CrowdingRankCard(item = item, rank = rank, onClick = onClick)
        return
    }
    val strings = LocalAppStrings.current
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.18f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
private fun CrowdingRankCard(item: TourismCatalogItem, rank: Int, onClick: () -> Unit) {
    val strings = LocalAppStrings.current
    val congestion = item.details["congestionRate"] ?: item.subtitle.orEmpty()
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.18f),
                spotColor = Color.Black.copy(alpha = 0.18f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color(0xFFEAF5FF)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(25.dp))
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(7.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Text(
                        "#$rank",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = CoralPrimary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(item.title, style = CardTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    item.details["signguNm"] ?: item.address ?: "부산 관광지",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                item.details["baseYmd"]?.let {
                    Text(
                        "${strings.tourism.detailFieldLabels["baseYmd"].orEmpty()} $it",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(congestion, style = MaterialTheme.typography.titleMedium, color = CoralPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(
                    strings.tourism.detailFieldLabels["congestionRate"].orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1
                )
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

private fun String.toSourceLabel(): String = when (this) {
    "tourism-ko" -> "국문 TourAPI"
    "tourism-en" -> "영문 TourAPI"
    "tourism-ja" -> "일문 TourAPI"
    "tourism-zh" -> "중문 TourAPI"
    "accessible-tourism" -> "무장애 TourAPI"
    "related-tourism" -> "연관 관광지 API"
    "hub-tourism" -> "지역 관광 허브 API"
    "crowding-forecast" -> "관광 혼잡도 API"
    "photo-gallery" -> "관광사진 갤러리"
    "durunubi" -> "두루누비"
    "odii" -> "오디"
    else -> this
}

private fun String.toDetailLabel(): String = when (this) {
    "tel" -> "전화"
    "cat1", "cat2", "cat3" -> "분류"
    "modifiedtime", "modifiedTime" -> "수정일"
    "distance" -> "거리"
    "requiredTime", "leadTime" -> "소요시간"
    "tatsCnctrRate", "cnctrRate" -> "혼잡도"
    "baseYmd", "baseYm" -> "기준일"
    "signguNm" -> "지역"
    else -> this
}
