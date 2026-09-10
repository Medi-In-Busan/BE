package com.mediinbusan.app.feature.tourism

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material.icons.filled.Schedule
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
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.MediTipContent
import com.mediinbusan.app.core.ui.NearbyPlacesSection
import com.mediinbusan.app.core.ui.TravelerHelpContent
import com.mediinbusan.app.core.ui.VisitInfo
import com.mediinbusan.app.core.ui.VisitInfoContent
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.toPlaceType
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourismCatalogItemDetailScreen(
    onBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onSelectPlace: (String) -> Unit,
    recentItemId: String? = null,
    viewModel: TourismCatalogItemDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var mapFocusRequestId by remember { mutableIntStateOf(0) }

    LaunchedEffect(recentItemId) {
        if (recentItemId != null) viewModel.loadFromRecent(recentItemId)
    }

    LaunchedEffect(uiState.consumed, uiState.selectedTitle) {
        // 상태를 컴포지션에서 캡처한 값이 아니라 여기서 다시 읽는다.
        //
        // 바로 위 LaunchedEffect가 같은 프레임에 loadFromRecent()로 상태를 덮어쓰기 때문이다
        // (LaunchedEffect는 UNDISPATCHED로 시작해서 첫 중단점까지 동기 실행되고, 선언 순서대로
        // 돌아간다 — 그래서 이 블록이 돌 때는 이미 덮어써져 있다). 캡처값으로 판정하면 최근 본
        // 항목에서 들어왔을 때 그 덮어쓰기를 못 보고 상세가 열리자마자 닫힌다.
        val state = viewModel.uiState.value
        if (state.consumed && state.selectedTitle == null) onBack()
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
                // 관광공사 상세가 붙지 않은 채(목록에서 넘어온 원본만으로) 그리는 중이라는 표시.
                showMatchNotice = uiState.matchNotFound,
                nearbyPlaces = uiState.nearbySamePlaces,
                onSelectPlace = onSelectPlace,
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
        }
    }
}

@Composable
private fun TourismDetailLoaded(
    item: TourismCatalogItem,
    category: TourismCatalogCategory,
    showMatchNotice: Boolean,
    nearbyPlaces: List<Place>,
    onSelectPlace: (String) -> Unit,
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
    // 하단 "관련 링크 열기" 버튼용. 홈페이지는 여기서 제외한다 — 아래 방문 정보 카드가 이미 누를 수
    // 있는 링크 행으로 보여주고 있어서, 예전처럼 homepage를 먼저 집으면 같은 링크가 한 화면에 둘이
    // 된다. 이 버튼에 남는 건 GPX 경로(WALKING)·오디오 해설(AUDIO)처럼 방문 정보가 아닌 링크뿐이다.
    val externalLinkUrl = item.details.entries.firstOrNull { (key, value) ->
        key !in VisitInfo.DetailKeys && (value.startsWith("http://") || value.startsWith("https://"))
    }?.value
    // 방문 정보(운영시간·휴무일·대표메뉴·이용요금·주차·홈페이지)는 아래 일반 details 표가 아니라
    // 전용 카드로 뺀다 — 라벨+값 한 줄로 취급하기엔 값 길이가 제각각이고(한 줄 ~ 여러 문단),
    // 대표메뉴처럼 칩으로 흩어야 읽히는 값이 섞여 있다. 카드 내용은 웰니스 장소 상세와 공유한다.
    val visitInfo = remember(item.details) { VisitInfo.fromDetails(item.details) }
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

    // 이 화면에는 하단 바가 없고 루트 Scaffold도 인셋을 하나도 소비하지 않으므로(MediInBusanApp의
    // contentWindowInsets = WindowInsets(0.dp)), 마지막 카드가 기기 제스처/내비게이션 바에 그대로
    // 깔린다 — 그만큼을 콘텐츠 아래 여백에 더한다.
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp + navigationBarInset),
        verticalArrangement = Arrangement.spacedBy(SectionSpacing)
    ) {
        item { TourismHero(item = item) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(SectionSpacing)) {
                // 별도 item으로 빼지 않는다 — 아래 TOURISM_DETAIL_MAP_ITEM_INDEX가 가리키는 지도
                // 카드 위치가 이 배너의 유무에 따라 달라지면 안 된다.
                if (showMatchNotice) MatchNotFoundNotice()
                TourismSummaryCard(item = item, category = category)
            }
        }
        item {
            CurationSectionCard(title = strings.placeCuration.atAGlanceTitle) {
                AtAGlanceRow(profile = careProfile)
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
        // 운영시간·대표메뉴 같은 사실 정보를 소개문보다 먼저 둔다 — 웰니스 장소 상세도
        // 기본정보 → 방문정보 → 소개 순서라, 같은 장소를 어느 화면으로 들어가든 읽는 순서가 같다.
        if (!visitInfo.isEmpty) {
            item {
                CurationSectionCard(
                    title = strings.placeCuration.visitInfoTitle,
                    icon = Icons.Default.Schedule
                ) {
                    VisitInfoContent(
                        visitInfo = visitInfo,
                        onOpenHomepage = onOpenLink
                    )
                }
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
                    MediTipContent(copy = copy)
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
                    CautionList(cautions = careProfile.cautions)
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
        // 방문 정보 카드로 빠진 키는 여기서 뺀다 — 같은 값이 두 카드에 겹쳐 나오면 안 된다.
        val secondaryDetails = labeledDetails.filterNot {
            it.key in setOf("congestionRate", "baseYmd") || it.key in VisitInfo.DetailKeys
        }
        if (secondaryDetails.isNotEmpty()) {
            // 남는 건 지역·요일·난이도·거리처럼 성격이 제각각인 부수 항목이라 "방문 정보"가 아니라
            // 병원 상세와 같은 "기타정보"로 부른다(CLAUDE.md §5 — 같은 뜻의 문구를 새로 만들지 않는다).
            item { DetailInfoCard(title = strings.hospitalDetail.otherInfoSectionTitle, details = secondaryDetails) }
        }
        item {
            CurationSectionCard(
                title = strings.placeCuration.travelerHelpTitle,
                icon = Icons.Default.SupportAgent
            ) {
                TravelerHelpContent(onDial = { context.dialPhone(it) })
            }
        }
        // 좌표·주소·외부 링크가 하나도 없는 항목(관광공사 매칭에 실패한 핫플레이스 등)에서는
        // 버튼이 한 개도 안 그려져 제목만 남은 빈 카드가 됐다 — 그럴 땐 섹션째 뺀다.
        val canOpenMap = item.latitude != null && item.longitude != null || item.address != null
        if (canOpenMap || externalLinkUrl != null) {
            item {
                DetailSurface {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(sectionLabels.directions, style = SectionTitleStyle, color = TextPrimary)
                        ActionButtons(
                            canOpenMap = canOpenMap,
                            externalLinkUrl = externalLinkUrl,
                            category = category,
                            onOpenMap = onOpenMap,
                            onOpenLink = onOpenLink
                        )
                    }
                }
            }
        }
        // 병원 상세(S-05)·장소 상세와 같은 자리·같은 구성이다 — 이 항목을 다 읽은 뒤 "그럼 근처의
        // 같은 종류는?"으로 이어지도록 본문 제일 아래에 둔다(아래 출처 각주는 섹션이 아니라 푸터라
        // 그대로 맨 끝에 남는다). 좌표가 없는 항목(관광사진·혼잡도 등)에서는 ViewModel이 조회 자체를
        // 하지 않아 목록이 비고, 그러면 이 섹션도 통째로 빠진다.
        if (nearbyPlaces.isNotEmpty()) {
            item {
                NearbyPlacesSection(
                    anchorType = placeType,
                    places = nearbyPlaces,
                    onSelectPlace = onSelectPlace
                )
            }
        }
        // 이 화면도 앱이 직접 쓴 안내(가이드·팁·진료 전후 체크)와 TourAPI 원문이 섞여 있어,
        // 어디까지가 공식 데이터인지 밝힌다. 반드시 리스트 맨 끝이다 — 앞에 끼우면 아래
        // TOURISM_DETAIL_MAP_ITEM_INDEX가 가리키는 지도 카드 위치가 어긋난다.
        item {
            Text(
                text = strings.placeCuration.officialDataCreditLabel,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
        }
    }
}

/**
 * 관광공사(TourAPI) 관광지 DB에서 이 항목과 일치하는 곳을 못 찾았을 때, 화면 위쪽에 다는 안내.
 *
 * 화면을 막지 않고 알리기만 한다 — 아래로는 목록에서 이미 본 이름·구·군·혼잡도가 그대로 이어진다.
 * 문구는 예전 빈 화면이 쓰던 것(placeMatchNotFoundMessage)을 그대로 재사용한다(CLAUDE.md §5).
 */
@Composable
private fun MatchNotFoundNotice() {
    val strings = LocalAppStrings.current.tourism
    DetailSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, null, tint = CoralPrimary, modifier = Modifier.size(18.dp))
            Text(
                text = strings.placeMatchNotFoundMessage,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

/**
 * 섹션(카드) 사이 세로 여백. 병원 상세(feature/hospitaldetail)·장소 상세(feature/nearby)의 같은 이름
 * 상수와 값을 맞춰, 세 상세 화면의 리듬을 통일한다.
 */
private val SectionSpacing = 20.dp

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

// "방문 정보" 라벨은 이제 여기 없다 — 웰니스 장소 상세와 공유하는 카드가 쓰므로 공유 자리
// (PlaceCurationStrings.visitInfoTitle)로 옮겼다. 두 벌로 두면 같은 카드가 화면마다 다른 문구를 단다.
private data class DetailSectionLabels(
    val introduction: String,
    val directions: String
)

private fun SupportedLanguage.detailSectionLabels(): DetailSectionLabels = when (this) {
    SupportedLanguage.KO -> DetailSectionLabels("장소 소개", "위치 및 이동")
    SupportedLanguage.EN -> DetailSectionLabels("About this place", "Location and directions")
    SupportedLanguage.JA -> DetailSectionLabels("スポット紹介", "位置・アクセス")
    SupportedLanguage.ZH -> DetailSectionLabels("景点介绍", "位置与交通")
}

private data class DetailValue(val key: String, val label: String, val value: String) {
    val icon: ImageVector
        get() = when (key) {
            "tel" -> Icons.Default.Phone
            "distance", "crsDstnc" -> Icons.Default.Route
            "requiredTime", "leadTime", "crsTotlRqrmHour" -> Icons.Default.AccessTime
            "baseYmd", "baseYm", "daywkDivNm" -> Icons.Default.CalendarToday
            // detailIntro2 방문 정보 키는 여기 오지 않는다 — 전용 카드(VisitInfoContent)가 가져간다.
            else -> Icons.Default.Info
        }
}

private fun android.content.Context.dialPhone(phoneNumber: String) {
    if (phoneNumber.isBlank()) return
    launchIntentSafely(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
}
