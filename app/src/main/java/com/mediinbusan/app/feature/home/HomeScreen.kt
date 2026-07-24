package com.mediinbusan.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.designsystem.BadgeOutline
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HeroSubtitleStyle
import com.mediinbusan.app.core.designsystem.HeroTitleStyle
import com.mediinbusan.app.core.designsystem.InactiveIcon
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.FavoriteHeartButton
import com.mediinbusan.app.core.ui.LanguageBadge
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.toLanguageBadgeLabel
import com.mediinbusan.app.data.hospital.Hospital
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onNavigateToHospitalList: (String?) -> Unit,
    onNavigateToHospitalDetail: (String) -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToSettings: () -> Unit,
    // NavHost에서 실제 배선됨. WELLNESS는 전용 route가 없어 HospitalList(medicalPurpose="웰니스")로
    // 임시 연결되어 있고, SELF_DIAGNOSIS는 준비 중 스텁 화면으로 연결된다 (MediInBusanNavHost.kt 참고).
    onNavigateToWellness: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToSelfDiagnosis: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onNavigateToHospitalList = onNavigateToHospitalList,
        onNavigateToHospitalDetail = onNavigateToHospitalDetail,
        onNavigateToGuide = onNavigateToGuide,
        onNavigateToWellness = onNavigateToWellness,
        onNavigateToMap = onNavigateToMap,
        onNavigateToSelfDiagnosis = onNavigateToSelfDiagnosis,
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToFavorite = onNavigateToFavorite,
        onNavigateToSettings = onNavigateToSettings,
        onPurposeSelected = viewModel::onMedicalPurposeSelected,
        onFavoriteClick = viewModel::onFavoriteToggleClicked,
        onRetry = viewModel::onRetryClicked,
        onLanguageSelected = viewModel::onLanguageSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onNavigateToHospitalList: (String?) -> Unit,
    onNavigateToHospitalDetail: (String) -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToWellness: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToSelfDiagnosis: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onPurposeSelected: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onRetry: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val isLoading = uiState.isLoading
    val error = uiState.error

    Scaffold(
        topBar = {
            // Home 진입 즉시 바가 나타나면 Splash(풀스크린) → Home(상단바 있음) 전환이 한
            // 프레임에 훅 줄어드는 느낌을 준다. 짧게 지연 후 페이드인해서 완화한다.
            var topBarVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { topBarVisible = true }
            AnimatedVisibility(
                visible = topBarVisible,
                enter = fadeIn(tween(durationMillis = 300, delayMillis = 150))
            ) {
                HomeTopAppBar(
                    onMenuClick = onNavigateToSettings,
                    currentLanguageCode = uiState.languageCode,
                    onLanguageSelected = onLanguageSelected
                )
            }
        }
    ) { innerPadding ->
        // Home은 공용 하단 탭바가 항상 보이는 화면이라, 상위 Scaffold의 innerPadding에 기대지
        //않고 BottomNavBarHeight를 직접 더한다(MediInBusanApp.kt 주석 참고).
        val contentPadding = PaddingValues(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + BottomNavBarHeight
        )
        // 로딩(화면 중앙 정렬)에서 콘텐츠(맨 위부터 시작)로 Crossfade하면 눈에는 "위로 밀리는"
        // 것처럼 보여서 순간 전환으로 되돌린다. 샘플 데이터라 로딩 자체가 사실상 즉시 끝난다.
        when {
            isLoading -> LoadingState(modifier = Modifier.padding(contentPadding))
            error != null -> ErrorState(
                message = error,
                modifier = Modifier.padding(contentPadding),
                onRetry = onRetry
            )
            else -> {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    HeroBannerSection(onSearchClick = onNavigateToSearch)

                    Spacer(modifier = Modifier.height(24.dp))
                    MedicalPurposeSection(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        purposes = uiState.medicalPurposes,
                        selectedPurpose = uiState.selectedPurpose,
                        // 카테고리는 "탐색 진입점"이라 로컬 상태 갱신과 HospitalList 이동이 함께 일어난다.
                        onPurposeClick = { purpose ->
                            onPurposeSelected(purpose)
                            onNavigateToHospitalList(purpose)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    QuickLinksSection(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        quickLinks = uiState.quickLinks,
                        onQuickLinkClick = { type ->
                            when (type) {
                                QuickLinkType.HOSPITAL_LIST -> onNavigateToHospitalList(null)
                                QuickLinkType.GUIDE -> onNavigateToGuide()
                                QuickLinkType.WELLNESS -> onNavigateToWellness()
                                QuickLinkType.MAP -> onNavigateToMap()
                                QuickLinkType.SELF_DIAGNOSIS -> onNavigateToSelfDiagnosis()
                                QuickLinkType.FAVORITE -> onNavigateToFavorite()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    RecommendedHospitalSection(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        hospitals = uiState.recommendedHospitals,
                        favoriteHospitalIds = uiState.favoriteHospitalIds,
                        onSeeAllClick = { onNavigateToHospitalList(null) },
                        onHospitalClick = onNavigateToHospitalDetail,
                        onFavoriteClick = onFavoriteClick
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    onMenuClick: () -> Unit,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "설정 메뉴")
            }
        },
        title = { HomeWordmark() },
        actions = {
            LanguageDropdown(
                currentLanguageCode = currentLanguageCode,
                onLanguageSelected = onLanguageSelected
            )
        }
    )
}

@Composable
private fun HomeWordmark() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "메디인부산 로고",
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = CoralPrimary, fontWeight = FontWeight.Bold)) { append("MEDIN") }
                append(" ")
                withStyle(SpanStyle(color = SkyBlue, fontWeight = FontWeight.Bold)) { append("BUSAN") }
            },
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun LanguageDropdown(currentLanguageCode: String, onLanguageSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(end = 12.dp)) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .border(width = 1.dp, color = BadgeOutline, shape = MaterialTheme.shapes.medium)
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${currentLanguageCode.toLanguageBadgeLabel()} ▾",
                style = MaterialTheme.typography.labelSmall,
                color = BadgeText
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SupportedLanguage.CODES.forEach { code ->
                DropdownMenuItem(
                    text = { Text(text = code.toLanguageBadgeLabel()) },
                    onClick = {
                        onLanguageSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

private val BannerImages = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
private const val BANNER_AUTO_SCROLL_DELAY_MS = 3500L

@Composable
private fun HeroBannerSection(onSearchClick: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { BannerImages.size })
    // 배너 박스 비율을 배너2 원본 이미지 비율에 맞춘다. 배너1/3은 그 비율에 맞게 Crop된다.
    val banner2IntrinsicSize = painterResource(id = R.drawable.banner2).intrinsicSize
    val bannerAspectRatio = if (banner2IntrinsicSize.isSpecified && banner2IntrinsicSize.height > 0f) {
        banner2IntrinsicSize.width / banner2IntrinsicSize.height
    } else {
        16f / 9f
    }

    LaunchedEffect(pagerState) {
        while (true) {
            delay(BANNER_AUTO_SCROLL_DELAY_MS)
            val nextPage = (pagerState.currentPage + 1) % BannerImages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column {
        // 배너 칸 자체에 상하좌우 여백을 줘서 화면에 꽉 차지 않게 한다.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .aspectRatio(bannerAspectRatio)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            ) { page ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = BannerImages[page]),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        // 배너 박스 비율 자체가 배너2 원본 비율과 같아서 Crop해도 잘리지 않는다.
                        contentScale = ContentScale.Crop
                    )
                    // 배너1은 텍스트 없는 사진이라 우리가 타이틀을 얹는다.
                    // 배너2/3은 사진 안에 이미 텍스트가 들어있어 그대로 쓴다.
                    if (page == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 20.dp)
                                // 아래쪽 캐러셀 점과 겹치지 않도록 여백을 더 둔다.
                                .padding(bottom = 50.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "부산에서 만나는\n맞춤 의료 여행", style = HeroTitleStyle, color = Color.White)
                            Text(
                                text = "의료 목적에 맞는 부산 의료기관을 찾아보세요",
                                style = HeroSubtitleStyle,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // 캐러셀 점은 사진 안, 중앙 하단에 배치한다.
            CarouselIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 35.dp),
                pageCount = BannerImages.size,
                activePage = pagerState.currentPage
            )

            // 검색바는 세로 중앙이 사진 아랫변에 걸치도록 절반은 사진 안, 절반은 여백으로 뺀다.
            SearchBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp)
                    .offset(y = 25.dp),
                onClick = onSearchClick
            )
        }
        // 검색바가 배너 박스 아래로 절반(25dp) 튀어나오므로 다음 섹션과 겹치지 않게 확보한다.
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
private fun CarouselIndicator(modifier: Modifier = Modifier, pageCount: Int, activePage: Int) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (index == activePage) CoralPrimary else InactiveIcon)
            )
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = RoundedCornerShape(percent = 50))
            // TODO: 실제 키워드 검색(쿼리 파라미터, 결과 필터링)은 범위 밖.
            // 탭하면 검색어 없이 HospitalList로만 이동한다.
            .clickable(onClick = onClick)
            .padding(start = 20.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "병원 이름, 진료과목으로 검색",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CoralPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "검색", tint = Color.White)
        }
    }
}

@Composable
private fun MedicalPurposeSection(
    modifier: Modifier = Modifier,
    purposes: List<MedicalPurposeItem>,
    selectedPurpose: String?,
    onPurposeClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = "의료 목적 선택", style = SectionTitleStyle, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(purposes, key = { it.label }) { item ->
                MedicalPurposeChip(
                    item = item,
                    selected = item.label == selectedPurpose,
                    onClick = { onPurposeClick(item.label) }
                )
            }
        }
    }
}

@Composable
private fun MedicalPurposeChip(item: MedicalPurposeItem, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) CoralPrimary else DividerColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) CoralPrimary else TextPrimary,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuickLinksSection(
    modifier: Modifier = Modifier,
    quickLinks: List<QuickLinkItem>,
    onQuickLinkClick: (QuickLinkType) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = "바로가기", style = SectionTitleStyle, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        quickLinks.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { item ->
                    QuickLinkCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onQuickLinkClick(item.type) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun QuickLinkCard(item: QuickLinkItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun RecommendedHospitalSection(
    modifier: Modifier = Modifier,
    hospitals: List<Hospital>,
    favoriteHospitalIds: Set<String>,
    onSeeAllClick: () -> Unit,
    onHospitalClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "추천 의료기관", style = SectionTitleStyle, color = TextPrimary)
            Text(
                text = "전체보기",
                style = MaterialTheme.typography.bodyMedium,
                color = CoralPrimary,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (hospitals.isEmpty()) {
            // F-019 전체화면 EmptyState 대신, 결정사항에 따라 섹션 내부에 텍스트만 인라인 표시
            Text(
                text = "추천 의료기관이 없습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(hospitals, key = { it.id }) { hospital ->
                    RecommendedHospitalCard(
                        hospital = hospital,
                        isFavorite = hospital.id in favoriteHospitalIds,
                        onClick = { onHospitalClick(hospital.id) },
                        onFavoriteClick = { onFavoriteClick(hospital.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendedHospitalCard(
    hospital: Hospital,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(170.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = MaterialTheme.shapes.large)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            AsyncImageBox(
                model = hospital.imageUrl,
                contentDescription = hospital.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            FavoriteHeartButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            )
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = hospital.name,
                style = CardTitleStyle,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hospital.address,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                hospital.supportedLanguages.take(3).forEach { lang ->
                    LanguageBadge(text = lang.toLanguageBadgeLabel())
                }
            }
        }
    }
}

private val PreviewHospitals = listOf(
    Hospital(
        id = "preview-1",
        name = "부산 예시 피부과의원",
        specialties = listOf("피부·미용"),
        address = "부산광역시 해운대구 센텀중앙로 55",
        latitude = 35.1691,
        longitude = 129.1315,
        phoneNumber = "051-000-0001",
        homepageUrl = null,
        supportedLanguages = listOf("en", "ja", "zh"),
        description = null,
        imageUrl = null,
        lastModified = null
    ),
    Hospital(
        id = "preview-2",
        name = "부산 예시 종합건강검진센터",
        specialties = listOf("건강검진"),
        address = "부산광역시 부산진구 중앙대로 668",
        latitude = 35.1579,
        longitude = 129.0597,
        phoneNumber = "051-000-0002",
        homepageUrl = null,
        supportedLanguages = listOf("en", "zh"),
        description = null,
        imageUrl = null,
        lastModified = null
    )
)

@Composable
private fun PreviewHomeContent(uiState: HomeUiState) {
    MediInBusanTheme {
        HomeContent(
            uiState = uiState,
            onNavigateToHospitalList = {},
            onNavigateToHospitalDetail = {},
            onNavigateToGuide = {},
            onNavigateToWellness = {},
            onNavigateToMap = {},
            onNavigateToSelfDiagnosis = {},
            onNavigateToSearch = {},
            onNavigateToFavorite = {},
            onNavigateToSettings = {},
            onPurposeSelected = {},
            onFavoriteClick = {},
            onRetry = {},
            onLanguageSelected = {}
        )
    }
}

@Preview(name = "Home - 데이터 있음", showBackground = true)
@Composable
private fun HomeContentDataPreview() {
    PreviewHomeContent(
        uiState = HomeUiState(
            selectedPurpose = "피부·미용",
            recommendedHospitals = PreviewHospitals,
            favoriteHospitalIds = setOf("preview-1"),
            isLoading = false,
            error = null
        )
    )
}

@Preview(name = "Home - 로딩", showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    PreviewHomeContent(uiState = HomeUiState(isLoading = true))
}

@Preview(name = "Home - 추천 의료기관 없음", showBackground = true)
@Composable
private fun HomeContentEmptyPreview() {
    PreviewHomeContent(
        uiState = HomeUiState(
            recommendedHospitals = emptyList(),
            isLoading = false,
            error = null
        )
    )
}
