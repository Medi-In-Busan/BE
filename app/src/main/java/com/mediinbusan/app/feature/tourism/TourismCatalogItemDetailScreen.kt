package com.mediinbusan.app.feature.tourism

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.common.careProfile
import com.mediinbusan.app.core.common.resolveBusanHighlight
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.designsystem.TourismAccentPink
import com.mediinbusan.app.core.designsystem.TourismAccentPinkContainer
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedCopy
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.AtAGlanceRow
import com.mediinbusan.app.core.ui.CautionList
import com.mediinbusan.app.core.ui.CenteredTopAppBar
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
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

@Composable
fun TourismCatalogItemDetailScreen(
    onBack: () -> Unit,
    onSelectPlace: (String) -> Unit,
    recentItemId: String? = null,
    viewModel: TourismCatalogItemDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = LocalAppStrings.current
    val context = LocalContext.current

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

    // 탑바는 로딩·오류·완료 세 상태 모두 core/ui의 공용 CenteredTopAppBar를 쓴다 — 흰 배경,
    // 검정 뒤로가기, 가운데 정렬된 굵은 제목뿐이고 오른쪽엔 아무 아이콘도 없다. 관광 카탈로그
    // 리스트업(TourismCatalogScreen)·웰니스 장소 상세(PlaceDetailScreen)도 같은 걸 쓴다.
    //
    // 혼잡도(CROWDING) 항목은 진입 직후 "관광지 혼잡도"였다가 관광공사 상세 매칭에 성공하면
    // "부산 관광지"로 바뀐다(TourismCatalogItemDetailViewModel.retry) — 실제로는 그 사이 카테고리가
    // 정말 바뀌는 게 맞지만, 화면에서는 이 전환이 탑바가 다른 화면인 것처럼 깜빡이는 걸로만
    // 보인다. 핫플레이스로 들어온 항목은 로딩 중에도 최종 상태와 같은 "부산 관광지"를 고정으로
    // 보여줘 탑바 자체는 한 번도 바뀌지 않게 한다.
    val topBarTitle = if (category == null || category == TourismCatalogCategory.CROWDING) {
        strings.tourism.busanPlacesListTitle
    } else {
        category.translatedLabel(strings.language)
    }
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenteredTopAppBar(title = topBarTitle, onBack = onBack)
        }
    ) { innerPadding ->
        if (item != null && category != null) {
            TourismDetailLoaded(
                item = item,
                category = category,
                // 관광공사 상세가 붙지 않은 채(목록에서 넘어온 원본만으로) 그리는 중이라는 표시.
                showMatchNotice = uiState.matchNotFound,
                nearbyPlaces = uiState.nearbySamePlaces,
                onSelectPlace = onSelectPlace,
                modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding()),
                bottomContentPadding = innerPadding.calculateBottomPadding(),
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
        } else {
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
}

@Composable
private fun TourismDetailLoaded(
    item: TourismCatalogItem,
    category: TourismCatalogCategory,
    showMatchNotice: Boolean,
    nearbyPlaces: List<Place>,
    onSelectPlace: (String) -> Unit,
    onOpenMap: () -> Unit,
    onOpenLink: (String) -> Unit,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
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
    val mapPin = remember(item.id, item.latitude, item.longitude) {
        val latitude = item.latitude
        val longitude = item.longitude
        if (latitude != null && longitude != null) {
            MapPin(item.id, latitude, longitude, MapPinType.TOURIST, selected = true)
        } else {
            null
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        // 화면을 감싼 Scaffold가 하단 인셋(기기 제스처/내비게이션 바)을 이미 계산해 넘겨준다
        // (bottomContentPadding) — 그 위에 마지막 카드와 인셋 사이 여백만 더한다.
        contentPadding = PaddingValues(bottom = 32.dp + bottomContentPadding),
        verticalArrangement = Arrangement.spacedBy(SectionSpacing)
    ) {
        item {
            TourismHero(item = item)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(SectionSpacing)) {
                if (showMatchNotice) MatchNotFoundNotice()
                TourismSummaryCard(item = item, category = category)
            }
        }
        item {
            // 참고 디자인(guide_tourism_place_detail.png)엔 이 pill 줄에 카드 배경도 "한눈에 보기"
            // 제목도 없다 — 흰 카드로 감싸던 걸 걷어내고 캔버스 위에 바로 얹는다. AtAGlanceRow
            // 자체(pill·출처 각주)는 웰니스 장소 상세와 공유하는 내용이라 그대로 둔다.
            AtAGlanceRow(profile = careProfile, modifier = Modifier.padding(horizontal = 20.dp))
        }
        // 지도부터 주변 관광지까지는 전부 한 item(Column) 안에서 같은 간격(TightSectionGap)을
        // 쓴다 — 예전엔 LazyColumn의 기본 간격(SectionSpacing=20dp) 위에 섹션마다 별도 Spacer를
        // 더해서 구간마다 여백이 조금씩 달랐다(사용자가 "다 동일하게 보인다"고 지적한 지점).
        // 한 Column으로 묶어 모든 구간이 정확히 같은 값을 쓰게 한다.
        item {
            Column(verticalArrangement = Arrangement.spacedBy(TightSectionGap)) {
                // 길찾기 버튼을 지도 카드 안(우하단)에 겹쳐 넣는다 — 예전엔 화면 맨 아래
                // "위치 및 이동" 카드에 따로 있어서, 지도를 보고 바로 길을 나서려면 다시 스크롤해
                // 내려가야 했다. 지도와 길찾기가 한 시선 안에 있는 게 자연스럽다.
                mapPin?.let { pin ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(260.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color.Black.copy(alpha = 0.10f),
                                spotColor = Color.Black.copy(alpha = 0.10f)
                            )
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        KakaoMapView(pins = listOf(pin), modifier = Modifier.fillMaxSize())
                        // 텍스트 라벨 없이 아이콘만 있는 동그란 FAB — 지도 위의 "길찾기"는 구글맵·
                        // 카카오맵·네이버맵 전부 이 모양(원형, 내비게이션 아이콘만)을 쓰는 국제적으로
                        // 굳어진 관례라 문구 없이도 바로 읽힌다. 기본 크기(56dp)는 살짝 크다는 피드백
                        // 이후 48dp로 소폭만 줄였다 — 40dp까지 줄였을 땐 눈에 안 띈다는 반대 피드백이
                        // 있었어서, 그 사이 지점이다. 그림자·흰 테두리 링은 그대로 유지해 대비는 유지.
                        // 아이콘은 병원 상세·웰니스 장소 상세의 길찾기 버튼과 같은 걸 쓴다
                        // (R.drawable.hospital_detail_findmap).
                        FloatingActionButton(
                            onClick = onOpenMap,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(48.dp)
                                .border(2.dp, Color.White, CircleShape),
                            containerColor = TourismAccentPink,
                            contentColor = Color.White,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 10.dp, pressedElevation = 6.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hospital_detail_findmap),
                                contentDescription = strings.hospitalDetail.directionsButton,
                                modifier = Modifier.size(19.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                }
                // 운영시간·대표메뉴 같은 사실 정보를 소개문보다 먼저 둔다 — 웰니스 장소 상세도
                // 기본정보 → 방문정보 → 소개 순서라, 같은 장소를 어느 화면으로 들어가든 읽는 순서가 같다.
                // 흰 카드+아이콘(CurationSectionCard)이던 걸 다른 섹션들과 같은 톤(PlainSection —
                // 카드 배경 없이 굵은 제목만)으로 맞췄다.
                if (!visitInfo.isEmpty) {
                    PlainSection(title = strings.placeCuration.visitInfoTitle) {
                        VisitInfoContent(
                            visitInfo = visitInfo,
                            onOpenHomepage = onOpenLink
                        )
                    }
                }
                // 소개는 이제 사라지지 않는다 — TourAPI subtitle이 비면 부산 명소 큐레이션 한 줄로,
                // 그것도 없으면 유형별 기본 소개문으로 내려간다(웰니스 장소 상세와 같은 폴백 체인).
                val introText = item.subtitle?.takeIf { it.isNotBlank() }
                    ?: highlightCopy?.tagline
                    ?: strings.placeCuration.typeIntroFallbacks[placeType.name]
                introText?.let { description ->
                    PlainSection(title = sectionLabels.introduction) {
                        Text(description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                }
                PlainSection(title = strings.nearby.recoveryCheckTitle) {
                    CautionList(cautions = careProfile.cautions)
                    // 면책 문구는 반드시 남긴다 — 위 안내는 의료 자문이 아니다.
                    Text(
                        strings.nearby.recoveryDisclaimer,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                item.details["congestionRate"]?.let { congestionRate ->
                    CongestionCard(
                        label = strings.tourism.detailFieldLabels["congestionRate"].orEmpty(),
                        value = congestionRate,
                        dateLabel = strings.tourism.detailFieldLabels["baseYmd"],
                        date = item.details["baseYmd"]
                    )
                }
                PlainSection(title = strings.placeCuration.travelerHelpTitle) {
                    TravelerHelpContent(onDial = { context.dialPhone(it) })
                }
                // 좌표가 있는 항목은 위 지도 카드에 이미 길찾기 버튼이 붙어 있다 — 여기서 또 넣으면
                // 같은 동작의 진입점이 화면에 둘이 된다. 좌표가 없어 지도 카드 자체가 안 그려진
                // 항목(주소만 있는 경우)에서만 이 버튼이 유일한 길찾기 진입점이 된다.
                val canOpenMapHere = mapPin == null && (item.latitude != null && item.longitude != null || item.address != null)
                if (canOpenMapHere || externalLinkUrl != null) {
                    DetailSurface {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(sectionLabels.directions, style = SectionTitleStyle, color = TextPrimary)
                            ActionButtons(
                                canOpenMap = canOpenMapHere,
                                externalLinkUrl = externalLinkUrl,
                                category = category,
                                onOpenMap = onOpenMap,
                                onOpenLink = onOpenLink
                            )
                        }
                    }
                }
                // 병원 상세(S-05)·장소 상세와 같은 자리·같은 구성이다 — 이 항목을 다 읽은 뒤 "그럼
                // 근처의 같은 종류는?"으로 이어지도록 본문 제일 아래에 둔다. 좌표가 없는 항목
                // (관광사진·혼잡도 등)에서는 ViewModel이 조회 자체를 하지 않아 목록이 비고, 그러면
                // 이 섹션도 통째로 빠진다.
                if (nearbyPlaces.isNotEmpty()) {
                    NearbyPlacesSection(
                        anchorType = placeType,
                        places = nearbyPlaces,
                        onSelectPlace = onSelectPlace
                    )
                }
            }
        }
        // 이 화면도 앱이 직접 쓴 안내(가이드·팁·진료 전후 체크)와 TourAPI 원문이 섞여 있어,
        // 어디까지가 공식 데이터인지 밝힌다. 반드시 리스트 맨 끝이다.
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
            Icon(Icons.Default.Info, null, tint = TourismAccentPink, modifier = Modifier.size(18.dp))
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

// 지도~주변 관광지 구간(TourismDetailLoaded의 merged Column) 전용 간격. 모든 구간(지도-소개,
// 소개-진료전후체크, 진료전후체크-여행자편의, 여행자편의-주변관광지)이 이 값 하나로 통일된다.
// 18dp로 맞췄더니 너무 붙어 보인다는 피드백으로 다시 조금 올렸다.
private val TightSectionGap = 22.dp

@Composable
private fun TourismHero(item: TourismCatalogItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(TourismHeroHeight)
            .background(Brush.linearGradient(listOf(TourismAccentPinkContainer, Color(0xFFEAF5FF))))
    ) {
        if (item.imageUrl != null) {
            AsyncImageBox(item.imageUrl, item.title, Modifier.fillMaxSize())
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(TourismAccentPinkContainer.copy(alpha = 0.6f), Color(0xFFEDEDF2))
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = TourismAccentPink, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

private val TourismHeroHeight = 260.dp

// guide_tourism_place_detail.png 기준 — 카드 배경 없이 캔버스 위에 바로 얹는다(히어로 사진
// 바로 아래, 지도 카드 사이 유일하게 흰 카드가 아닌 텍스트 블록).
@Composable
private fun TourismSummaryCard(item: TourismCatalogItem, category: TourismCatalogCategory) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text(
            category.translatedLabel(LocalAppStrings.current.language),
            style = MaterialTheme.typography.labelMedium,
            color = TourismAccentPink,
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

@Composable
private fun CongestionCard(label: String, value: String, dateLabel: String?, date: String?) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = TourismAccentPinkContainer,
        shadowElevation = 6.dp,
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
                Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = TourismAccentPink)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = TourismAccentPink)
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
                colors = ButtonDefaults.buttonColors(containerColor = TourismAccentPink, contentColor = Color.White)
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
                border = BorderStroke(1.dp, TourismAccentPink)
            ) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = TourismAccentPink)
                Spacer(Modifier.width(8.dp))
                Text(label, color = TourismAccentPink)
            }
        }
    }
}

// guide_tourism_place_detail.png 기준 — 흰 카드 없이 굵은 제목 한 줄 + 본문만 캔버스 위에 바로
// 얹는 섹션 껍데기. "방문 정보"/"장소 소개"/"진료 전후 체크"/"여행자 편의"가 쓴다(카드 배경이
// 필요한 나머지 섹션은 여전히 [DetailSurface] 기반 전용 카드를 쓴다).
@Composable
private fun PlainSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(title, style = SectionTitleStyle, color = TextPrimary, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
private fun DetailSurface(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(
                elevation = 6.dp,
                shape = MaterialTheme.shapes.large,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
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

private fun android.content.Context.dialPhone(phoneNumber: String) {
    if (phoneNumber.isBlank()) return
    launchIntentSafely(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
}
