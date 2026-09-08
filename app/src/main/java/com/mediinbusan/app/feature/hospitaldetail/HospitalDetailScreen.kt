package com.mediinbusan.app.feature.hospitaldetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mediinbusan.app.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.resolveHospitalThumbnailRes
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.HospitalDetailStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.designsystem.BadgeOutline
import com.mediinbusan.app.core.designsystem.CoralInk
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.DetailPullDismissBox
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.WrapRow
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.core.ui.rememberFavoriteTogglePop
import com.mediinbusan.app.core.ui.toLanguageDisplayName
import com.mediinbusan.app.data.hospital.Hospital
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HospitalDetailScreen(
    hospitalId: String,
    onSelectHospital: (String) -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    viewModel: HospitalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hospital = uiState.hospital

    LaunchedEffect(hospitalId) {
        viewModel.load(hospitalId)
    }

    // 홈(S-03)/의료기관 목록(S-04)이 Scaffold containerColor로 쓰는 HomeBackgroundPink를 그대로
    // 깔아, 그 화면들에서 넘어왔을 때 배경색이 끊겨 보이지 않게 한다(root Surface의 M3 기본
    // 배경색과 미세하게 달라 이음매가 보이던 문제).
    // 지도(S-08)에서 선택 카드를 위로 끌어올려 들어오는 화면이라, 반대로 본문을 맨 위에서 더
    // 아래로 끌면 그대로 되돌아간다 — 들어온 동작을 그 자리에서 되감는 짝이다. 다른 화면
    // (홈/목록/즐겨찾기)에서 들어왔을 때도 같은 제스처가 뒤로가기로 동작한다.
    DetailPullDismissBox(
        onDismiss = onBack,
        modifier = Modifier.fillMaxSize().background(HomeBackgroundPink)
    ) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.isError -> ErrorState(
                message = uiState.errorMessage ?: LocalAppStrings.current.hospitalDetail.genericErrorFallback,
                onRetry = { viewModel.load(hospitalId) }
            )
            hospital != null -> HospitalDetailContent(
                hospital = hospital,
                isFavorite = uiState.isFavorite,
                nearbyHospitals = uiState.nearbyHospitals,
                onSelectHospital = onSelectHospital,
                onToggleFavorite = viewModel::onToggleFavorite,
                onNavigateToGuide = onNavigateToGuide,
                onNavigateToNearby = onNavigateToNearby,
                onNavigateToMap = onNavigateToMap,
                onNavigateToHome = onNavigateToHome,
                onBack = onBack
            )
            else -> EmptyState(message = LocalAppStrings.current.hospitalDetail.notFoundMessage)
        }
    }
}

@Composable
private fun HospitalDetailContent(
    hospital: Hospital,
    isFavorite: Boolean,
    nearbyHospitals: List<Hospital>,
    onSelectHospital: (String) -> Unit,
    onToggleFavorite: () -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current.hospitalDetail
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // 값이 없는 정보는 "정보 없음" 행으로 채우지 않고 아예 그리지 않는다. 장소 상세(S-07)가 먼저
    // 이렇게 바꿨는데(PlaceDetailScreen의 기본정보 카드 주석 참고) 병원 상세만 예전 방식으로 남아,
    // 값이 비면 "정보 없음"이 네 줄 찍힌 카드가 화면 한가운데를 차지했다.
    val openingHours = hospital.openingHours?.takeUnless { it.isBlank() }
    val phoneNumber = hospital.phoneNumber?.takeUnless { it.isBlank() }
    val homepageUrl = hospital.homepageUrl?.takeUnless { it.isBlank() }
    val supportedLanguages = hospital.supportedLanguages
        .takeIf { it.isNotEmpty() }
        ?.joinToString(" · ") { it.toLanguageDisplayName() }
    val description = hospital.description?.takeUnless { it.isBlank() }

    // 실제로 그려지는 섹션만 축약 탭바에 올린다. 예전에는 라벨 5개를 항상 그려서, 진료과목이 없는
    // 병원에서 "진료과목" 탭을 누르면 위치를 못 찾고(top이 0으로 남아) 화면 맨 위로 튀었다.
    val visibleSections = buildList {
        if (description != null) add(HospitalDetailSection.INTRO)
        if (openingHours != null || phoneNumber != null || homepageUrl != null || supportedLanguages != null) {
            add(HospitalDetailSection.BASIC_INFO)
        }
        if (hospital.specialties.isNotEmpty()) add(HospitalDetailSection.SPECIALTIES)
        add(HospitalDetailSection.LOCATION)
        // 주변 병원은 조회 결과가 있을 때만. 좌표가 없거나 겹치는 과목이 없으면 ViewModel이 아예
        // 요청하지 않아 빈 목록으로 남고, 그러면 이 섹션도 탭도 함께 사라진다.
        if (nearbyHospitals.isNotEmpty()) add(HospitalDetailSection.NEARBY)
        add(HospitalDetailSection.OTHER_INFO)
    }

    // 각 섹션의 스크롤 콘텐츠 내 세로 위치(px). onGloballyPositioned로 매 배치마다 갱신되므로
    // 콘텐츠 높이가 언어/데이터에 따라 달라져도 항상 최신 위치를 가리킨다.
    val sectionTops = remember { mutableStateMapOf<HospitalDetailSection, Int>() }

    // 의료기관 목록(HospitalSearchListScreen)의 필터바 접힘/펼침과 같은 델타 기반 스크롤 방향
    // 감지 로직을 그대로 가져오되, 방향은 반대다 — 거기서는 아래로 스크롤하면 필터바가 접히고
    // (숨고) 위로 올리면 펼쳐지는데, 여기서는 아래로 스크롤하면 축약 헤더(탑바 타이틀+섹션
    // 탭바)가 나타나고, 위로 스크롤하거나 맨 위 근처로 돌아오면 다시 기본 구성으로 사라진다.
    var showCollapsedHeader by remember { mutableStateOf(false) }
    // 탭을 눌러 animateScrollTo로 위쪽 섹션까지 프로그램적으로 스크롤할 때도 scrollState.value가
    // 위로 흐르면서 아래 델타 감지에 "사용자가 위로 스크롤함"으로 잡혀 축약 헤더가 꺼져버렸다.
    // 탭 이동 중에는 이 플래그로 방향 감지를 잠깐 꺼서, 실제 손가락 스크롤일 때만 사라지게 한다.
    var isNavigatingToSection by remember { mutableStateOf(false) }
    LaunchedEffect(scrollState) {
        var previousValue = scrollState.value
        snapshotFlow { scrollState.value }.collect { value ->
            if (!isNavigatingToSection) {
                val delta = value - previousValue
                when {
                    value <= 4 -> showCollapsedHeader = false
                    delta > 6 -> showCollapsedHeader = true
                    delta < -6 -> showCollapsedHeader = false
                }
            }
            previousValue = value
        }
    }

    // 밑줄은 손으로 스크롤할 때도 따라와야 한다 — 예전에는 탭을 눌렀을 때만 바뀌어서, 3번 섹션을
    // 보고 있어도 밑줄은 1번에 남아 있었다. 화면 위에서 조금 아래를 기준선 삼아, 그 선을 지난
    // 마지막 섹션을 활성으로 본다.
    val sectionActivateOffset = with(LocalDensity.current) { 140.dp.roundToPx() }
    val activeSectionIndex by remember(visibleSections, sectionActivateOffset) {
        derivedStateOf {
            val scroll = scrollState.value
            // 맨 아래에서는 마지막 섹션이 기준선까지 못 올라오는 게 정상이라 따로 잡아준다.
            if (scrollState.maxValue != Int.MAX_VALUE && scroll >= scrollState.maxValue - 8) {
                visibleSections.lastIndex
            } else {
                visibleSections
                    .indexOfLast { (sectionTops[it] ?: Int.MAX_VALUE) <= scroll + sectionActivateOffset }
                    .coerceAtLeast(0)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 사진 위에 떠 있던 뒤로가기/공유/즐겨찾기 오버레이 대신, Home 탑바와 같은 영역·배경의
        // 고정 탑바. 공유는 타이틀 영역에, 즐겨찾기는 타이틀 영역에 이미 동일 기능이 있어
        // 오버레이에서 빠져도 기능이 사라지지 않는다. 아래로 스크롤하면 뒤로가기 옆에 병원
        // 타이틀이 나타난다(본문 타이틀과 동일한 스타일).
        HospitalDetailTopBar(
            onBack = onBack,
            onNavigateToHome = onNavigateToHome,
            onNavigateToInquiry = { context.smsInquiry(hospital, strings) },
            onNavigateToDirections = { context.launchDirections(hospital) },
            hospitalName = hospital.name,
            showTitle = showCollapsedHeader
        )
        // 알약(pill) 칩이 아니라 단순 텍스트+밑줄 탭 5개짜리 필터바. HospitalSearchListScreen의
        // 필터바 접힘/펼침과 동일한 expandVertically/shrinkVertically+fade 애니메이션을 그대로 쓴다.
        AnimatedVisibility(
            visible = showCollapsedHeader,
            enter = expandVertically(animationSpec = tween(durationMillis = 420)) +
                fadeIn(animationSpec = tween(durationMillis = 340, delayMillis = 50)),
            exit = shrinkVertically(animationSpec = tween(durationMillis = 380)) +
                fadeOut(animationSpec = tween(durationMillis = 280))
        ) {
            HospitalDetailSectionTabsBar(
                labels = visibleSections.map { it.label(strings) },
                selectedIndex = activeSectionIndex,
                onTabSelected = { index ->
                    val targetTop = sectionTops[visibleSections[index]] ?: 0
                    coroutineScope.launch {
                        isNavigatingToSection = true
                        scrollState.animateScrollTo(targetTop)
                        isNavigatingToSection = false
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            ImageCarouselSection(
                imageUrls = hospital.imageUrls.ifEmpty { HospitalDetailFallbackImages },
                strings = strings
            )

            // 웰니스 프로그램다운 톤: 굵은 회색 구분선으로 정보를 뚝뚝 끊어내던 "행정 서류" 같은
            // 느낌 대신, 옅은 분홍 배경(HomeBackgroundPink) 위에 카드가 하나씩 둥둥 떠 있는 스파
            // 앱 스타일 레이아웃으로 바꿨다. 각 InfoSection이 스스로 흰 카드+그림자를 두르므로
            // (아래 InfoSection 정의 참고) 여기서는 카드 사이 여백만 둔다. 사진-타이틀 간격도
            // 다른 섹션 사이 여백(14dp)과 동일하게 맞춘다.
            Spacer(modifier = Modifier.height(14.dp))
            // 카드 배경/그림자 없이 옅은 분홍 배경(HomeBackgroundPink)이 그대로 비치는 영역.
            // 타이틀 텍스트는 다른 배경 요소들과 같은 20dp 왼쪽 여백을 쓰지만, 즐겨찾기/공유
            // 아이콘 줄은 20dp 안쪽 패딩을 아예 안 주고 화면 진짜 오른쪽 끝까지 fillMaxWidth로
            // 채운다 — HospitalDetailTopBar의 actions Row와 동일한 폭 기준(전체 화면 너비)이라야
            // 공유 아이콘의 오른쪽 끝이 탑바 findmap 아이콘의 오른쪽 끝과 정확히 같은 위치에 온다.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hospital.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 20.dp).weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(12.dp))
                // 즐겨찾기(흰 원 배경 없이 아이콘만)와 공유(기존 shareHospital 로직). 크기는
                // 그대로 유지하고, 둘 사이 간격만 탑바 홈/길찾기보다 살짝 더 좁힌다(offset 8dp → 12dp).
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val favoritePop = rememberFavoriteTogglePop(
                        isFavorite = isFavorite,
                        onToggle = onToggleFavorite
                    )
                    Box(modifier = Modifier.offset(x = 12.dp)) {
                        IconButton(onClick = favoritePop.onClick) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) {
                                    LocalAppStrings.current.common.favoriteRemoveContentDescription
                                } else {
                                    LocalAppStrings.current.common.favoriteAddContentDescription
                                },
                                tint = CoralPrimary,
                                modifier = favoritePop.scaleModifier
                            )
                        }
                    }
                    IconButton(onClick = { context.shareHospital(hospital) }) {
                        Image(
                            painter = painterResource(id = R.drawable.hospital_detail_share),
                            contentDescription = strings.actionShare,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                // 위치 서브타이틀이 타이틀 바로 아래 "밑줄"처럼 붙어 보이도록 간격을 최소로 줄인다.
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = hospital.address, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            // 위치 서브타이틀-병원소개 사이는 다른 섹션 간 여백(14dp)의 약 3배를 둬서 "타이틀
            // 블록"과 "카드형 정보 섹션들" 사이를 시각적으로 크게 구분한다.
            Spacer(modifier = Modifier.height(42.dp))

            // 소개가 없으면 "등록된 소개 정보가 없습니다" 카드를 남기지 않고 섹션째 뺀다 — 위
            // 기본정보와 같은 규칙이다. 위 visibleSections에서 탭도 같이 빠지므로 어긋나지 않는다.
            description?.let { introText ->
                Box(
                    modifier = Modifier.onGloballyPositioned {
                        sectionTops[HospitalDetailSection.INTRO] = it.positionInParent().y.roundToInt()
                    }
                ) {
                    InfoSection(title = strings.introSectionTitle, icon = Icons.Default.Info) {
                        Text(
                            text = introText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (visibleSections.contains(HospitalDetailSection.BASIC_INFO)) {
                Box(
                    modifier = Modifier.onGloballyPositioned {
                        sectionTops[HospitalDetailSection.BASIC_INFO] = it.positionInParent().y.roundToInt()
                    }
                ) {
                    InfoSection(title = strings.basicInfoSectionTitle) {
                        openingHours?.let {
                            BasicInfoRow(
                                iconRes = R.drawable.hospital_detail_runtime,
                                label = strings.openingHoursLabel,
                                value = it
                            )
                        }
                        // 전화·홈페이지는 그 자체가 액션을 겸한다. 예전에는 값을 눈으로 읽기만 할 수
                        // 있어서, 전화를 걸려면 화면 맨 아래 "기타정보" 카드까지 스크롤해야 했고
                        // 홈페이지는 아예 열 방법이 없었다(값이 여기 텍스트로만 있었다).
                        phoneNumber?.let {
                            BasicInfoRow(
                                iconRes = R.drawable.hospital_detail_phone,
                                label = strings.phoneLabel,
                                value = it,
                                onClick = { context.dialPhone(it) }
                            )
                        }
                        homepageUrl?.let {
                            BasicInfoRow(
                                iconRes = R.drawable.hospital_detail_homepage,
                                label = strings.homepageLabel,
                                value = it,
                                onClick = { context.launchHomepage(it) }
                            )
                        }
                        supportedLanguages?.let {
                            BasicInfoRow(
                                iconRes = R.drawable.home_languege,
                                label = strings.supportedLanguagesLabel,
                                value = it
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (hospital.specialties.isNotEmpty()) {
                Box(
                    modifier = Modifier.onGloballyPositioned {
                        sectionTops[HospitalDetailSection.SPECIALTIES] = it.positionInParent().y.roundToInt()
                    }
                ) {
                    InfoSection(title = strings.specialtiesSectionTitle) {
                        val language = LocalAppStrings.current.language
                        // 한글 라벨("피부·미용" 4자)은 한 줄에 다 들어가지만 영어 번역("Obstetrics &
                        // Gynecology" 23자 등)은 훨씬 길어서, 줄바꿈 없는 Row에선 화면 밖으로 넘치거나
                        // 칩이 잘려 보였다(영어로 바꿨을 때 "깨지는" 원인). androidx.compose.foundation의
                        // 실험적 FlowRow를 처음 썼다가 이 프로젝트 의존성 그래프의 버전 스큐 때문에
                        // 실기기에서 NoSuchMethodError로 즉시 죽는 걸 확인해서(core/ui/WrapRow.kt 문서
                        // 참고), 안정 API만으로 짠 WrapRow로 교체했다.
                        WrapRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalSpacing = 8.dp,
                            verticalSpacing = 8.dp
                        ) {
                            hospital.specialties.forEach { specialty ->
                                SpecialtyChip(text = translatedSpecialtyLabel(specialty, language))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Box(
                modifier = Modifier.onGloballyPositioned {
                    sectionTops[HospitalDetailSection.LOCATION] = it.positionInParent().y.roundToInt()
                }
            ) {
                // 주소는 타이틀 바로 아래 서브타이틀로 이미 한 번 보여준다 — 같은 문자열을 이 카드에
                // 또 적으면 한 화면에 두 번 나오는 셈이라, 여기는 지도만 남긴다.
                InfoSection(title = strings.locationSectionTitle, icon = Icons.Default.LocationOn) {
                    LocationMiniMap(hospital = hospital, onExpandClick = onNavigateToMap, strings = strings)
                }
            }

            // 위치(지도) 바로 다음에 둔다 — "여기가 어디인지"를 본 직후라 "이 근처에 또 뭐가 있는지"가
            // 자연스럽게 이어진다. 카드가 화면 끝까지 스크롤돼 나가야 가로로 더 있다는 게 보이므로,
            // 흰 카드(InfoSection)로 감싸지 않고 배경 위에 직접 얹는다.
            if (nearbyHospitals.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier.onGloballyPositioned {
                        sectionTops[HospitalDetailSection.NEARBY] = it.positionInParent().y.roundToInt()
                    }
                ) {
                    NearbyHospitalsSection(
                        hospitals = nearbyHospitals,
                        strings = strings,
                        onSelectHospital = onSelectHospital
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier.onGloballyPositioned {
                    sectionTops[HospitalDetailSection.OTHER_INFO] = it.positionInParent().y.roundToInt()
                }
            ) {
                SectionCard(innerPadding = 0.dp) {
                    Text(
                        text = strings.otherInfoSectionTitle,
                        style = SectionTitleStyle,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    QuickLinkRow(label = strings.guideQuickLink, onClick = onNavigateToGuide)
                    HorizontalDivider(thickness = 1.dp, color = DividerColor)
                    QuickLinkRow(label = strings.nearbyQuickLink, onClick = onNavigateToNearby)
                    HorizontalDivider(thickness = 1.dp, color = DividerColor)
                    // 라벨은 기존 통화 문구(callButton = "전화 문의하기")를 그대로 재사용한다 — 새
                    // 문구를 또 만들지 않는다. 문의(qa) 아이콘은 탑바로 옮겨서 여기는 다른 두 줄과
                    // 동일한 기본 ">" 트레일링 아이콘을 그대로 쓴다(트레일링 파라미터 생략).
                    QuickLinkRow(
                        label = strings.callButton,
                        onClick = { context.dialPhone(hospital.phoneNumber) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

// Home 탑바(HomeScreen.kt의 HomeTopAppBar)와 동일한 영역·배경·아이콘 크기의 병원 상세 전용
// 탑바. 왼쪽은 표준 뒤로가기 화살표, 오른쪽은 홈 이동/문의(qa)/길찾기 아이콘 3개를 순서대로
// 둔다 — 문의 아이콘이 새로 끼어들면서 홈 아이콘은 자연히 더 왼쪽으로 밀리고, 길찾기는 그대로
// 가장 오른쪽 끝(다른 화면 요소들의 오른쪽 정렬 기준)에 남는다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HospitalDetailTopBar(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToInquiry: () -> Unit,
    onNavigateToDirections: () -> Unit,
    hospitalName: String,
    showTitle: Boolean
) {
    val strings = LocalAppStrings.current
    TopAppBar(
        title = {
            // 본문 타이틀(HospitalDetailContent)과 완전히 같은 스타일 — 아래로 스크롤해서 본문
            // 타이틀이 화면 밖으로 지나가면, 뒤로가기 아이콘 옆에 같은 크기로 나타난다.
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 80)),
                exit = fadeOut(animationSpec = tween(durationMillis = 140))
            ) {
                Text(
                    text = hospitalName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            // 화살표(줄+화살촉) 모양의 ArrowBack 대신, 단순 꺾쇠 "<" 모양인 ChevronLeft를 쓴다.
            // 지정된 코랄 톤(0xFFE36A6D, 디자인 토큰 CoralPrimary와는 미세하게 다른 값)으로 tint.
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = strings.common.backContentDescription,
                    tint = Color(0xFFE36A6D),
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        actions = {
            // 세 아이콘(홈/문의/길찾기) 사이 간격은 그대로 두고, 그룹 전체를 타이틀 영역의 공유
            // 아이콘(화면 진짜 오른쪽 끝까지 fillMaxWidth)과 같은 위치로 밀어준다 — TopAppBar의
            // actions 슬롯 자체가 안쪽으로 약간 패딩이 있어 그만큼 오른쪽으로 당긴다.
            Row(modifier = Modifier.offset(x = 12.dp)) {
                // Home 탑바의 언어선택 아이콘과 같은 자리 — 같은 오프셋으로 간격까지 맞춘다.
                Box(modifier = Modifier.offset(x = 8.dp)) {
                    IconButton(onClick = onNavigateToHome) {
                        Image(
                            painter = painterResource(id = R.drawable.hospital_detail_home),
                            contentDescription = strings.hospitalDetail.homeContentDescription,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                // 병원 문의(문자 발송) 아이콘 — 홈과 길찾기 사이에 새로 끼워 넣는다. 이 아이콘 자체는
                // 옮기지 않는다(옮기면 홈-문의 간격까지 같이 벌어진다) — 대신 오른쪽 길찾기를 왼쪽으로
                // 당겨서 문의-길찾기 간격만 홈-문의 간격과 같게 좁힌다.
                IconButton(onClick = onNavigateToInquiry) {
                    Image(
                        painter = painterResource(id = R.drawable.hospital_detail_qa),
                        contentDescription = strings.hospitalDetail.actionInquiry,
                        modifier = Modifier.size(26.dp)
                    )
                }
                // Home 탑바의 설정 아이콘과 같은 자리. 기존 "길찾기"(launchExternalDirections) 로직을
                // 그대로 매핑한다. 문의 아이콘과의 간격이 홈-문의 간격보다 넓어 보여서 좀 더 좁힌다.
                Box(modifier = Modifier.offset(x = (-14).dp)) {
                    IconButton(onClick = onNavigateToDirections) {
                        Image(
                            painter = painterResource(id = R.drawable.hospital_detail_findmap),
                            contentDescription = strings.hospitalDetail.directionsButton,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        },
        windowInsets = WindowInsets.statusBars.exclude(WindowInsets(top = 14.dp)),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = HomeBackgroundPink)
    )
}

// 알약(pill) 모양의 FilterChipPill(HospitalSearchListScreen 참고) 대신, 단순 텍스트+밑줄
// 탭 5개를 슬라이드(가로 스크롤) 없이 균등폭으로 배치한다. 기본은 회색 비활성 밑줄이고,
// 탭을 고르면 뒤로가기 아이콘과 같은 코랄 톤으로 텍스트/밑줄이 활성화되면서 해당 섹션으로
// 스크롤된다(호출부의 onTabSelected 참고).
@Composable
private fun HospitalDetailSectionTabsBar(
    labels: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HomeBackgroundPink)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val active = index == selectedIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(index) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (active) Color(0xFFE36A6D) else TextSecondary,
                    fontWeight = if (active) FontWeight.Bold else null,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                // 탭마다 끊어진 짧은 밑줄 대신, 각 탭이 자기 폭을 전부 채워서 옆 탭의 밑줄과
                // 이어붙어 탭바 전체 폭에 걸친 하나의 연결된 선처럼 보이게 한다.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (active) Color(0xFFE36A6D) else DividerColor)
                )
            }
        }
    }
}

// 병원마다 다른 실제 사진이 없어서, 모든 병원 상세에 공통으로 쓰는 4장짜리 기본 갤러리.
// hospital.imageUrls가 실제 값(String URL)으로 채워지면 그쪽이 우선(ifEmpty 폴백이라)한다.
private val HospitalDetailFallbackImages: List<Any> = listOf(
    R.drawable.hospital_detail1,
    R.drawable.hospital_detail2,
    R.drawable.hospital_detail3,
    R.drawable.hospital_detail4
)

@Composable
private fun ImageCarouselSection(
    imageUrls: List<Any>,
    strings: HospitalDetailStrings
) {
    val pageCount = imageUrls.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    // Home 배너(HeroBannerSection)와 동일한 피킹(peek) 캐러셀 톤 — contentPadding으로 좌우에
    // 다음/이전 페이지가 살짝 보이게 한다. 다만 Home처럼 양 끝을 순환시키는 가상 페이지 트릭은
    // 쓰지 않는다: 실제 페이지 개수 그대로 bounded 페이저를 쓰면 첫 장 왼쪽/마지막 장 오른쪽엔
    // 넘길 페이지 자체가 없어 자연히 아무것도 peek 되지 않고 슬라이드도 안 먹힌다 — 요청한
    // "1번에서 왼쪽으로 4번 슬라이드 금지, 4번에서 오른쪽으로 1번 슬라이드 금지, 2·3번은 가능"
    // 동작 그대로다. 화면 맨 위에 딱 붙지 않게 위쪽에 작은 여백만 둔다(둥근 모서리는 각 페이지에
    // 개별로 준다 — 그래야 옆으로 peek되는 이웃 페이지도 카드 형태로 보인다).
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .height(260.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 10.dp,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
            ) {
                val imageUrl = imageUrls.getOrNull(page)
                if (imageUrl != null) {
                    AsyncImageBox(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().background(Color(0xFFE9E9EE))
                    )
                } else {
                    // 실제 병원 사진이 아직 없는 경우가 대부분이라(샘플 데이터에 imageUrls가 비어있음)
                    // 이 자리표시자가 사실상 기본 히어로가 된다 — 무채색 박스 대신 앱 브랜드 톤(코랄)의
                    // 옅은 그라데이션 + 아이콘 배지로, DocumentScanScreen 인트로와 같은 톤을 재사용한다.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(CoralPrimaryContainer.copy(alpha = 0.6f), Color(0xFFEDEDF2))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(
                                        brush = Brush.radialGradient(listOf(Color.White, CoralPrimaryContainer)),
                                        shape = CircleShape
                                    )
                                    .border(width = 1.dp, color = CoralPrimary.copy(alpha = 0.18f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = CoralPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = strings.imagePlaceholderLabel, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${page + 1}/$pageCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// hospital.specialties는 API 응답 원문(현재는 샘플 데이터) 그대로라 건드리지 않고, 화면에
// 그릴 때만 MedicalCategory 라벨과 일치하면 언어별 문구로 바꿔 보여준다. 매칭되는 카테고리가
// 없는 자유 문구(예: 실제 API의 세부 진료과목명)는 원문을 그대로 표시한다.
private fun translatedSpecialtyLabel(specialty: String, language: SupportedLanguage): String =
    MedicalCategory.entries.find { it.label == specialty }?.translatedLabel(language) ?: specialty

// 웰니스 프로그램의 스파 앱다운 톤을 위해 각 정보 구획을 옅은 분홍 배경 위에 뜬 흰 카드로
// 감싼다(Soft UI: 은은한 그림자 + 넉넉한 라운드 코너). InfoSection은 제목+본문을 카드 안에
// 배치하는 표준 형태이고, SectionCard는 제목 없이 카드만 필요한 곳(상단 정보 블록, 바로가기
// 묶음)에 쓰는 더 낮은 레벨의 래퍼다.
@Composable
private fun InfoSection(
    title: String,
    // 제목 앞 작은 코랄 아이콘. 텍스트만 있는 섹션(소개/위치)이 눈에 더 잘 띄게 하려고 열어뒀다 —
    // 장소 상세(PlaceDetailScreen)의 InfoSection과 같은 패턴이고, 거기처럼 모든 섹션에 다 붙이지는
    // 않는다(다 붙이면 아이콘이 배경 무늬가 되어 아무것도 강조하지 못한다).
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(imageVector = it, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = title, style = SectionTitleStyle, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    innerPadding: Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            // Home의 SectionCardContainer(HomeScreen.kt)와 동일한 그림자 톤 — 사진 캐러셀을
            // 제외한 타이틀/소개/기본정보 등 흰 카드 영역 전부가 이 SectionCard를 공유해서 쓴다.
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.large,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(MaterialTheme.shapes.large)
            .background(Color.White)
            .padding(innerPadding),
        content = content
    )
}

@Composable
private fun BasicInfoRow(
    iconRes: Int,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(width = 1.dp, color = DividerColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.width(72.dp)
        )
        // 누를 수 있는 값(전화·홈페이지)만 코랄 글자색으로 구분한다 — 장소 상세의 BasicInfoRow와
        // 같은 규칙이다. 면이 아니라 글자라서 CoralPrimary가 아니라 CoralInk를 쓴다.
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (onClick != null) CoralInk else TextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SpecialtyChip(text: String) {
    Box(
        modifier = Modifier
            .border(width = 1.dp, color = BadgeOutline, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // maxLines/softWrap 없이 두면, 한 줄에 남은 폭이 좁을 때(예: 세 번째 칩이 첫 줄 끝에 살짝
        // 걸칠 때) FlowRow가 "안 들어가니 다음 줄로" 판단하지 못하고 그 좁은 자리에 그대로 밀어
        // 넣어버려 텍스트가 한 글자씩 세로로 줄바꿈되는 기형적인 칩이 생겼다("Rehabilitation"이
        // R/e/h/a/b/i/l/i/t/a/t/i/o/n처럼 세로로 쌓이던 현상). 줄바꿈을 원천 차단해 칩이 항상
        // 한 줄의 실제 폭을 그대로 보고하게 하면, FlowRow가 그 폭 기준으로 제대로 다음 줄로 넘긴다.
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = TextPrimary, maxLines = 1, softWrap = false)
    }
}

@Composable
private fun LocationMiniMap(hospital: Hospital, onExpandClick: () -> Unit, strings: HospitalDetailStrings) {
    val lat = hospital.latitude
    val lng = hospital.longitude
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onExpandClick)
    ) {
        if (lat != null && lng != null) {
            KakaoMapView(
                pins = listOf(MapPin(id = hospital.id, latitude = lat, longitude = lng, type = MapPinType.HOSPITAL, selected = true)),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFE9E9EE)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = strings.noLocationInfo, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

// 트레일링 슬롯을 교체 가능하게 열어둬서, 기본 ">" 화살표 대신 다른 트리거(전화 아이콘 등)를
// 써야 하는 줄(예: "전화 문의하기")도 같은 높이(20/16dp 패딩)를 그대로 재사용한다 — 별도
// 컴포저블을 두면 IconButton의 48dp 최소 터치 영역 때문에 줄 높이가 달라지는 문제가 있었다.
// 왼쪽 아이콘 없이 텍스트만 왼쪽 정렬한다.
@Composable
private fun QuickLinkRow(
    label: String,
    onClick: () -> Unit,
    trailingIcon: @Composable () -> Unit = {
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        trailingIcon()
    }
}

private fun Context.launchDirections(hospital: Hospital) {
    launchExternalDirections(
        latitude = hospital.latitude,
        longitude = hospital.longitude,
        label = hospital.name,
        fallbackAddress = hospital.address
    )
}

/**
 * "주변 같은 진료과목 병원" 가로 스크롤 섹션.
 *
 * 목록 화면이 아니라 곁들이는 추천이라 세로로 쌓지 않고 한 줄로 흘린다 — 카드가 화면 오른쪽 끝을
 * 넘어가 잘려 보여야 "더 있다"가 전달되므로, contentPadding으로 여백을 주고 카드 자체는 화면 끝까지
 * 스크롤되게 둔다(흰 카드로 감싸면 그 효과가 사라진다).
 */
@Composable
private fun NearbyHospitalsSection(
    hospitals: List<Hospital>,
    strings: HospitalDetailStrings,
    onSelectHospital: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = CoralPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = strings.nearbySameSpecialtyTitle, style = SectionTitleStyle, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            // 이 목록이 무엇을 기준으로 뽑힌 건지 한 줄로 밝힌다 — 근거 없는 추천처럼 보이지 않게.
            Text(
                text = strings.nearbySameSpecialtySubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = hospitals, key = { it.id }) { nearby ->
                NearbyHospitalCard(hospital = nearby, onClick = { onSelectHospital(nearby.id) })
            }
        }
    }
}

/** 썸네일 + 거리 배지 + 이름 + 대표 진료과목 한 줄짜리 카드. */
@Composable
private fun NearbyHospitalCard(hospital: Hospital, onClick: () -> Unit) {
    val language = LocalAppStrings.current.language
    Column(
        modifier = Modifier
            .width(164.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(DividerColor)
        ) {
            // 병원마다 실제 사진이 없는 경우가 많아, 목록 화면과 같은 규칙(이름 키워드 → 태그 순)으로
            // 진료과목별 대표 사진을 고른다(core/common/MedicalCategory.kt의 resolveHospitalThumbnailRes).
            if (hospital.imageUrl != null) {
                AsyncImageBox(
                    model = hospital.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(
                        id = resolveHospitalThumbnailRes(hospital.name, hospital.specialties)
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // 거리는 서버가 기준 좌표로부터 계산해 내려준 값이라, 없으면 배지를 아예 안 단다.
            hospital.distanceMeters?.let { meters ->
                Text(
                    text = meters.toDistanceLabel(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = CoralInk,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = hospital.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                // 이름이 한 줄인 카드와 두 줄인 카드가 섞이면 아래 과목 줄의 높이가 어긋난다 —
                // 두 줄 자리를 항상 잡아 카드들의 바닥선을 맞춘다.
                minLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hospital.specialties.firstOrNull()
                    ?.let { translatedSpecialtyLabel(it, language) }
                    .orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** 1km 미만은 10m 단위 미터로, 그 이상은 소수 한 자리 km로. */
private fun Double.toDistanceLabel(): String =
    if (this < 1000) "${(this / 10).roundToInt() * 10}m" else String.format(Locale.US, "%.1fkm", this / 1000)

/** 축약 탭바가 가리키는 본문 섹션. 실제로 그려지는 것만 탭에 올라간다. */
private enum class HospitalDetailSection { INTRO, BASIC_INFO, SPECIALTIES, LOCATION, NEARBY, OTHER_INFO }

private fun HospitalDetailSection.label(strings: HospitalDetailStrings): String = when (this) {
    HospitalDetailSection.INTRO -> strings.introSectionTitle
    HospitalDetailSection.BASIC_INFO -> strings.basicInfoSectionTitle
    HospitalDetailSection.SPECIALTIES -> strings.specialtiesSectionTitle
    HospitalDetailSection.LOCATION -> strings.locationSectionTitle
    HospitalDetailSection.NEARBY -> strings.nearbySameSpecialtyTitle
    HospitalDetailSection.OTHER_INFO -> strings.otherInfoSectionTitle
}

/**
 * 병원 공식 홈페이지를 기기 브라우저로 연다.
 *
 * 백엔드가 주는 website 값에 스킴이 빠져 있는 경우가 있어(`www.example.com`) 그대로 넘기면
 * ACTION_VIEW가 받아줄 앱이 없다 — 없으면 https를 붙인다.
 */
private fun Context.launchHomepage(url: String) {
    val normalized = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
    launchIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse(normalized)))
}

private fun Context.dialPhone(phoneNumber: String?) {
    if (phoneNumber.isNullOrBlank()) return
    launchIntentSafely(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
}

// CLAUDE.md의 "실시간 상담/통역사 매칭 기능 없음" 제약을 지키기 위해 인앱 채팅 대신
// 사용자의 기본 메시지 앱으로 넘기는 SMS 인텐트만 연다.
private fun Context.smsInquiry(hospital: Hospital, strings: HospitalDetailStrings) {
    val phoneNumber = hospital.phoneNumber
    if (phoneNumber.isNullOrBlank()) return
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber")).apply {
        putExtra("sms_body", strings.smsInquiryTemplateFormat.format(hospital.name))
    }
    launchIntentSafely(intent)
}

private fun Context.shareHospital(hospital: Hospital) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "${hospital.name}\n${hospital.address}")
    }
    launchIntentSafely(Intent.createChooser(intent, hospital.name))
}
