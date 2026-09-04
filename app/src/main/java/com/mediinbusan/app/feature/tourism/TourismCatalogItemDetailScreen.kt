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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TipsAndUpdates
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.careProfile
import com.mediinbusan.app.core.common.resolveBusanHighlight
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedCopy
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.i18n.translatedRecoveryHint
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.AtAGlanceRow
import com.mediinbusan.app.core.ui.BackOnlyNavigationBar
import com.mediinbusan.app.core.ui.CautionList
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.MediTipContent
import com.mediinbusan.app.core.ui.TravelerHelpContent
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.data.place.toPlaceType
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourismCatalogItemDetailScreen(
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: TourismCatalogItemDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var mapFocusRequestId by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.consumed, uiState.selectedTitle) {
        if (uiState.consumed && uiState.selectedTitle == null) onBack()
    }
    if (uiState.selectedTitle == null) return

    val item = uiState.item
    val category = uiState.category
    if (item != null && category != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeBackgroundPink)
        ) {
            BackOnlyNavigationBar(
                onBack = onBack,
                background = HomeBackgroundPink,
                onHomeClick = onNavigateHome,
                onMapDetailsClick = if (
                    category != TourismCatalogCategory.CROWDING &&
                    item.latitude != null &&
                    item.longitude != null
                ) {
                    { mapFocusRequestId++ }
                } else {
                    null
                }
            )
            TourismDetailLoaded(
                item = item,
                category = category,
                mapFocusRequestId = mapFocusRequestId,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                onOpenMap = {
                    context.launchExternalDirections(
                        latitude = item.latitude,
                        longitude = item.longitude,
                        label = item.title,
                        fallbackAddress = item.address.orEmpty()
                    )
                },
                onOpenLink = { url ->
                    context.launchIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            )
        }
        return
    }

    Scaffold(
        containerColor = HomeBackgroundPink,
        topBar = {
            BackOnlyNavigationBar(
                onBack = onBack,
                background = HomeBackgroundPink,
                onHomeClick = onNavigateHome
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
        }
    }
}

@Composable
private fun TourismDetailLoaded(
    item: TourismCatalogItem,
    category: TourismCatalogCategory,
    mapFocusRequestId: Int,
    onOpenMap: () -> Unit,
    onOpenLink: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val sectionLabels = strings.language.detailSectionLabels()
    // TourAPI contenttypeid를 웰니스 장소와 똑같은 규칙으로 PlaceType에 옮겨(data/place/PlaceMapper.kt),
    // 장소 상세와 같은 케어 프로필·명소 큐레이션을 그대로 재사용한다 — 같은 장소가 어느 화면으로
    // 들어오든 같은 안내를 보게 하려는 것이다.
    val placeType = remember(item.categoryCode) { item.categoryCode.toPlaceType() }
    val careProfile = remember(placeType) { placeType.careProfile }
    val highlightCopy = remember(item.title, placeType, strings.language) {
        resolveBusanHighlight(item.title, placeType)?.translatedCopy(strings.language)
    }
    val externalLinkUrl = item.details["homepage"]
        ?: item.details.values.firstOrNull { it.startsWith("http://") || it.startsWith("https://") }
    val labeledDetails = item.details.entries.mapNotNull { (key, value) ->
        strings.tourism.detailFieldLabels[key]?.let { label -> DetailValue(key, label, value) }
    }
    val mapPin = remember(item.id, item.latitude, item.longitude) {
        val latitude = item.latitude
        val longitude = item.longitude
        if (latitude != null && longitude != null) {
            MapPin(item.id, latitude, longitude, MapPinType.TOURIST, selected = true)
        } else {
            null
        }
    }

    LaunchedEffect(mapFocusRequestId) {
        if (mapFocusRequestId > 0 && mapPin != null) {
            listState.animateScrollToItem(TOURISM_DETAIL_MAP_ITEM_INDEX)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { TourismHero(item = item) }
        item { TourismSummaryCard(item = item, category = category) }
        item {
            CurationSectionCard(title = strings.placeCuration.atAGlanceTitle) {
                AtAGlanceRow(profile = careProfile, accent = CoralPrimary)
            }
        }
        mapPin?.let { pin ->
            item {
                KakaoMapView(
                    pins = listOf(pin),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
            }
        }
        // 소개는 이제 사라지지 않는다 — TourAPI subtitle이 비면 부산 명소 큐레이션 한 줄로,
        // 그것도 없으면 유형별 기본 소개문으로 내려간다(웰니스 장소 상세와 같은 폴백 체인).
        val introText = item.subtitle?.takeIf { it.isNotBlank() }
            ?: highlightCopy?.tagline
            ?: strings.placeCuration.typeIntroFallbacks[placeType.name]
        introText?.let { description ->
            item { DescriptionCard(title = sectionLabels.introduction, description = description) }
        }
        highlightCopy?.let { copy ->
            item {
                CurationSectionCard(
                    title = strings.placeCuration.mediTipTitle,
                    icon = Icons.Default.TipsAndUpdates
                ) {
                    MediTipContent(copy = copy, accent = CoralPrimary)
                }
            }
        }
        item {
            CurationSectionCard(
                title = strings.nearby.recoveryCheckTitle,
                icon = Icons.Default.HealthAndSafety
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        placeType.translatedRecoveryHint(strings.language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    CautionList(cautions = careProfile.cautions, accent = CoralPrimary)
                    // 면책 문구는 반드시 남긴다 — 위 안내는 의료 자문이 아니다.
                    Text(
                        strings.nearby.recoveryDisclaimer,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
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
        val secondaryDetails = labeledDetails.filterNot { it.key in setOf("congestionRate", "baseYmd") }
        if (secondaryDetails.isNotEmpty()) {
            item { DetailInfoCard(title = sectionLabels.visitInformation, details = secondaryDetails) }
        }
        item {
            CurationSectionCard(
                title = strings.placeCuration.travelerHelpTitle,
                icon = Icons.Default.SupportAgent
            ) {
                TravelerHelpContent(accent = CoralPrimary, onDial = { context.dialPhone(it) })
            }
        }
        item {
            DetailSurface {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(sectionLabels.directions, style = SectionTitleStyle, color = TextPrimary)
                    ActionButtons(
                        canOpenMap = item.latitude != null && item.longitude != null || item.address != null,
                        externalLinkUrl = externalLinkUrl,
                        category = category,
                        onOpenMap = onOpenMap,
                        onOpenLink = onOpenLink
                    )
                }
            }
        }
    }
}

// 히어로 / 요약카드 / 한눈에 보기 다음이 지도다 — 앞에 item을 끼우거나 빼면 이 값도 같이 고쳐야
// 지도 보기 버튼(mapFocusRequestId)이 엉뚱한 카드로 스크롤되지 않는다.
private const val TOURISM_DETAIL_MAP_ITEM_INDEX = 3

@Composable
private fun TourismHero(item: TourismCatalogItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 10.dp, end = 20.dp)
            .height(260.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color(0xFFEAF5FF))))
    ) {
        if (item.imageUrl != null) {
            AsyncImageBox(item.imageUrl, item.title, Modifier.fillMaxSize())
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(CoralPrimaryContainer.copy(alpha = 0.6f), Color(0xFFEDEDF2))
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = CoralPrimary, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TourismSummaryCard(item: TourismCatalogItem, category: TourismCatalogCategory) {
    DetailSurface {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text(
                category.translatedLabel(LocalAppStrings.current.language),
                style = MaterialTheme.typography.labelMedium,
                color = CoralPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(item.title, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            item.address?.let { address ->
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.LocationOn, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Text(address, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CongestionCard(label: String, value: String, dateLabel: String?, date: String?) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = CoralPrimaryContainer,
        border = BorderStroke(1.dp, Color.White),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
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

/**
 * 큐레이션 섹션(한눈에 보기 / 메디인부산 팁 / 진료 전후 체크 / 여행자 편의)의 카드 껍데기.
 * 내용 자체는 core/ui/PlaceCurationSections.kt가 웰니스 장소 상세와 공유하고, 껍데기만 이 화면의
 * [DetailSurface] 톤으로 감싼다(CLAUDE.md §4 — feature끼리 직접 import하지 않는다).
 */
@Composable
private fun CurationSectionCard(
    title: String,
    icon: ImageVector? = null,
    content: @Composable () -> Unit
) {
    DetailSurface {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, null, tint = CoralPrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text(title, style = SectionTitleStyle, color = TextPrimary)
            }
            content()
        }
    }
}

@Composable
private fun DescriptionCard(title: String, description: String) {
    DetailSurface {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = SectionTitleStyle, color = TextPrimary)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
    }
}

@Composable
private fun DetailInfoCard(title: String, details: List<DetailValue>) {
    DetailSurface {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(title, style = SectionTitleStyle, color = TextPrimary)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 2.dp,
                shape = MaterialTheme.shapes.large,
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .clip(MaterialTheme.shapes.large)
            .background(Color.White)
            .padding(20.dp)
    ) {
        content()
    }
}

private data class DetailSectionLabels(
    val introduction: String,
    val visitInformation: String,
    val directions: String
)

private fun SupportedLanguage.detailSectionLabels(): DetailSectionLabels = when (this) {
    SupportedLanguage.KO -> DetailSectionLabels("장소 소개", "방문 정보", "위치 및 이동")
    SupportedLanguage.EN -> DetailSectionLabels("About this place", "Visitor information", "Location and directions")
    SupportedLanguage.JA -> DetailSectionLabels("スポット紹介", "訪問情報", "位置・アクセス")
    SupportedLanguage.ZH -> DetailSectionLabels("景点介绍", "访问信息", "位置与交通")
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

private fun android.content.Context.dialPhone(phoneNumber: String) {
    if (phoneNumber.isBlank()) return
    launchIntentSafely(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
}
