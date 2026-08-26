package com.mediinbusan.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.common.resolveHospitalThumbnailRes
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.HomeStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralMuted
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HeroBodyGray
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.HeroCtaTextStyle
import com.mediinbusan.app.core.designsystem.HeroSubtitleStyle
import com.mediinbusan.app.core.designsystem.HeroTitleLargeStyle
import com.mediinbusan.app.core.designsystem.HeroTitleStyle
import com.mediinbusan.app.core.designsystem.InactiveIcon
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LanguageBadge
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.toLanguageBadgeLabel
import com.mediinbusan.app.data.hospital.Hospital
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onNavigateToHospitalDetail: (String) -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToMap: () -> Unit = {},
    onNavigateToWellness: () -> Unit = {},
    onNavigateToRecommendedCourse: (String, String?) -> Unit = { _, _ -> },
    // SELF_DIAGNOSIS는 준비 중 스텁 화면으로 연결된다 (MediInBusanNavHost.kt 참고).
    onNavigateToSelfDiagnosis: () -> Unit = {},
    // 의료목적 선택 칩이 여기로 모인다. 실제 필터 값은 이 콜백이 아니라 viewModel::onCategorySelected
    // (PendingHospitalSearchEntry)가 전달하고, 이 콜백은 순수하게 "검색 화면으로 이동"만 담당한다.
    onNavigateToSearch: () -> Unit = {},
    // 배너의 검색바 전용. 마찬가지로 실제 "포커스 요청" 값은 viewModel::onSearchBarClicked
    // (PendingHospitalSearchEntry)가 전달하고, 이 콜백은 순수하게 "검색 화면으로 이동"만 담당한다.
    onNavigateToSearchFocused: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onNavigateToHospitalDetail = onNavigateToHospitalDetail,
        onNavigateToGuide = onNavigateToGuide,
        onNavigateToMap = onNavigateToMap,
        onNavigateToWellness = onNavigateToWellness,
        onNavigateToRecommendedCourse = onNavigateToRecommendedCourse,
        onNavigateToSelfDiagnosis = onNavigateToSelfDiagnosis,
        onNavigateToSearch = onNavigateToSearch,
        onNavigateToSearchFocused = onNavigateToSearchFocused,
        onNavigateToFavorite = onNavigateToFavorite,
        onNavigateToSettings = onNavigateToSettings,
        onPurposeSelected = viewModel::onCategorySelected,
        onSearchBarClicked = viewModel::onSearchBarClicked,
        onRetry = viewModel::onRetryClicked,
        onLoadMoreCourses = viewModel::onLoadMoreCourses,
        onRetryCourses = viewModel::onRetryCourses,
        onLanguageSelected = viewModel::onLanguageSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onNavigateToHospitalDetail: (String) -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToWellness: () -> Unit,
    onNavigateToRecommendedCourse: (String, String?) -> Unit,
    onNavigateToSelfDiagnosis: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSearchFocused: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onPurposeSelected: (MedicalCategory) -> Unit,
    onSearchBarClicked: () -> Unit,
    onRetry: () -> Unit,
    onLoadMoreCourses: () -> Unit,
    onRetryCourses: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val isLoading = uiState.isLoading
    val isError = uiState.isError

    Scaffold(
        // 기본값(colorScheme.background, 거의 흰색)보다 살짝 더 연한 코랄핑크로 — Home 페이지
        // 맨 뒤 배경 전용 톤. 아래 HomeTopAppBar에도 같은 색을 줘서 탑바-본문 경계가 안 보이게 한다.
        containerColor = HomeBackgroundPink,
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
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            // 공용 하단 탭바는 이 Scaffold 밖(MediInBusanApp.kt)에서 떠 있는 오버레이라, FAB 기본
            // 위치(화면 맨 아래)에 그대로 두면 바텀바에 가려진다. BottomNavBarHeight만큼 띄운다.
            AiChatFab(
                onClick = onNavigateToSelfDiagnosis,
                modifier = Modifier.padding(bottom = BottomNavBarHeight + 8.dp)
            )
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
            isError -> ErrorState(
                message = uiState.error ?: LocalAppStrings.current.home.loadErrorFallback,
                modifier = Modifier.padding(contentPadding),
                onRetry = onRetry
            )
            else -> {
                // contentPadding을 Column 전체에 걸면(이전 방식) 바텀바 높이만큼 Column 자신의
                // 레이아웃 영역 자체가 줄어들어서, 그 안의 어떤 자식도 스크롤을 아무리 해도 바텀바
                // 뒤 영역엔 절대 그려질 수 없다(hazeEffect가 블러링할 콘텐츠가 거기 없는 원인이었음).
                // 대신 Column은 fillMaxSize로 화면 전체(바텀바 뒤 포함)를 그대로 쓰고, 상단
                // 패딩만 유지한 채, 맨 마지막 자식으로 bottom padding만큼의 Spacer 하나만 둔다 —
                // 스크롤 중엔 카드/배너가 바텀바 뒤로 실제로 지나가고, 끝까지 내리면 이 Spacer가
                // 자리를 벌려줘서 마지막 카드는 여전히 바텀바에 안 가린다.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = contentPadding.calculateTopPadding())
                ) {
                    HeroBannerSection(
                        // 카테고리 칩과 같은 패턴: onSearchBarClicked가 PendingHospitalSearchEntry에
                        // 포커스 요청을 심고, onNavigateToSearchFocused는 순수하게 화면 이동만 한다.
                        onSearchClick = { onSearchBarClicked(); onNavigateToSearchFocused() },
                        onWellnessClick = onNavigateToWellness
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    // 의료 목적 선택 + 바로가기의 "추천 웰니스"를 하나의 원형 아이콘 그리드로 합쳤다.
                    // 나머지 바로가기(의료기관/가이드/지도)는 바텀바와 중복이라 뺐다.
                    // 카테고리는 필터 토글이 아니라 순수 "탐색 진입점"이다 — 탭하면 바로 그 필터로
                    // 검색 화면으로 이동하고 끝이라 Home으로 돌아왔을 때 선택 상태를 남기지 않는다
                    // (예전엔 DataStore에 마지막 선택을 영구 저장해서 Home에 계속 남아있었음).
                    // onPurposeSelected가 실제 필터 값을 PendingHospitalSearchEntry에 심고,
                    // onNavigateToSearch는 순수하게 화면 이동만 한다(HomeViewModel.onCategorySelected 참고).
                    CategoryGridSection(
                        onPurposeClick = { purpose ->
                            onPurposeSelected(purpose)
                            onNavigateToSearch()
                        },
                        onWellnessClick = onNavigateToWellness
                    )

                    Spacer(modifier = Modifier.height(72.dp))
                    RecommendedHospitalSection(
                        hospitals = uiState.recommendedHospitals,
                        onHospitalClick = onNavigateToHospitalDetail
                    )

                    Spacer(modifier = Modifier.height(72.dp))
                    RecommendedCourseSection(
                        courses = uiState.recommendedCourses,
                        isLoading = uiState.isCourseLoading,
                        hasMore = uiState.hasMoreCourses,
                        hasError = uiState.courseError != null,
                        onCourseClick = { course ->
                            onNavigateToRecommendedCourse(course.category.name, course.district?.name)
                        },
                        onLoadMore = onLoadMoreCourses,
                        onRetry = onRetryCourses
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    // 바텀바 뒤로 밀려 들어간 만큼의 여백 — 이게 있어야 마지막 카드가 끝까지
                    // 스크롤됐을 때 바텀바에 가려지지 않는다(Column 자체는 더 이상 이 영역을
                    // 제외하지 않으므로, 콘텐츠 쪽에서 직접 확보해야 한다).
                    Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
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
    val strings = LocalAppStrings.current
    // 리디자인: 파비콘+텍스트 워드마크(HomeWordmark) 대신 home_logo 이미지 한 장을 왼쪽에 둔다.
    // CenterAlignedTopAppBar는 title을 항상 가운데로 미는데(양옆 폭이 달라도 강제로 중앙 정렬),
    // "왼쪽에 위치"시키려면 title이 왼쪽부터 시작하는 일반 TopAppBar를 써야 한다. 햄버거 메뉴는
    // 톱니(설정) 아이콘으로 바뀌어 오른쪽 언어선택 바로 왼쪽으로 옮겨간다 — 눌렀을 때 동작(설정
    // 화면 이동)은 그대로다.
    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.home_logo),
                contentDescription = LocalAppStrings.current.common.logoContentDescription,
                // 기존 워드마크(파비콘 42dp + 텍스트)와 세로 크기를 동일하게 맞춘다. home_logo는
                // 가로로 긴 이미지라 높이만 고정하면 원본 비율대로 폭이 정해진다.
                modifier = Modifier.height(42.dp)
            )
        },
        actions = {
            // 기존 "KO ▾" 알약 트리거 대신 지구본 아이콘이 트리거다. 드롭다운 메뉴 자체
            // (LanguageDropdown)는 재사용하고 expanded 상태만 여기로 끌어올렸다. 탑바 아이콘은
            // 원형 배경/경계선 없이 이미지만 보여준다(Settings 안쪽 RowIconImage와는 다른 톤).
            var languageMenuExpanded by remember { mutableStateOf(false) }
            // 설정 톱니 쪽으로 살짝 밀어서 둘 사이 여백을 좁힌다 — 아이콘과 드롭다운을 한 Box로
            // 같이 옮겨서 드롭다운도 이동한 아이콘 바로 아래에 뜨게 한다.
            Box(modifier = Modifier.offset(x = 8.dp)) {
                IconButton(onClick = { languageMenuExpanded = true }) {
                    Image(
                        painter = painterResource(id = R.drawable.home_languege),
                        contentDescription = strings.common.languageSelectorContentDescription,
                        modifier = Modifier.size(26.dp)
                    )
                }
                LanguageDropdown(
                    expanded = languageMenuExpanded,
                    onDismissRequest = { languageMenuExpanded = false },
                    currentLanguageCode = currentLanguageCode,
                    onLanguageSelected = onLanguageSelected
                )
            }
            IconButton(onClick = onMenuClick) {
                Image(
                    painter = painterResource(id = R.drawable.home_setting),
                    contentDescription = strings.home.settingsMenuContentDescription,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        // 아이콘/텍스트 크기는 그대로 두고, 상태바 인셋만큼 생기는 탑바 위쪽 여백만 줄인다
        // (core/ui/BrandTopAppBar.kt의 BrandBackTopAppBar와 동일한 값으로 맞춤).
        windowInsets = WindowInsets.statusBars.exclude(WindowInsets(top = 14.dp)),
        // Scaffold의 containerColor(HomeBackgroundPink)와 맞춰 탑바-본문 경계가 안 보이게 한다.
        colors = TopAppBarDefaults.topAppBarColors(containerColor = HomeBackgroundPink)
    )
}

// core/ui/BrandTopAppBar.kt의 BrandLanguageDropdown과 같은 톤. 드롭다운은 DropdownMenuItem 대신
// 직접 Row를 그린다 — DropdownMenuItem은 내부적으로 최소 112dp 폭을 강제해서 모디파이어로는 줄일
// 수 없었다. 직접 그리면 텍스트 크기만큼만 차지한다. Home은 팀원의 미머지 PR과 충돌을 피하려고
// 로컬 사본을 따로 두지만(파일 상단 주석 참고), 시각적으로는 항상 같은 디자인을 유지한다.
//
// 트리거는 지구본 아이콘(HomeTopAppBar)이라 expanded를 내부 remember로 안 갖고 호출부에서 끌어올려 받는다.
@Composable
private fun LanguageDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    // 펼쳐졌을 때 목록은 1.5배 크기 + 알약(pill) 모양 선택 표시로 꾸민다.
    val expandedItemTextStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = MaterialTheme.typography.labelSmall.fontSize * 1.2f,
        lineHeight = MaterialTheme.typography.labelSmall.lineHeight * 1.2f
    )
    val pillShape = RoundedCornerShape(percent = 50)

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = MaterialTheme.shapes.large,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SupportedLanguage.CODES.forEachIndexed { index, code ->
                val selected = code == currentLanguageCode
                // 항목이 위에서부터 순서대로 살짝 옆으로 미끄러지며 나타나는 진입 애니메이션.
                // 펼쳐질 때만 인덱스만큼 지연시켜 순차 등장시키고, 닫힐 때는 바로 리셋한다.
                val itemReveal = remember(code) { Animatable(0f) }
                LaunchedEffect(expanded, code) {
                    if (expanded) {
                        delay(index * 55L)
                        itemReveal.animateTo(1f, animationSpec = tween(durationMillis = 160))
                    } else {
                        itemReveal.snapTo(0f)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = itemReveal.value
                            translationX = (1f - itemReveal.value) * -10.dp.toPx()
                        }
                        .clip(pillShape)
                        .clickable {
                            onLanguageSelected(code)
                            onDismissRequest()
                        }
                        .background(if (selected) CoralPrimaryContainer else Color.Transparent)
                        .padding(horizontal = 10.dp * 1.5f, vertical = 6.dp * 1.5f)
                ) {
                    Text(
                        text = code.toLanguageBadgeLabel(),
                        style = expandedItemTextStyle,
                        color = if (selected) CoralPrimary else BadgeText,
                        fontWeight = if (selected) FontWeight.Bold else null
                    )
                    if (selected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = CoralPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private val BannerImages = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
private const val BANNER_AUTO_SCROLL_DELAY_MS = 3500L
// 가로 폭은 그대로 두고 배너 박스 세로만 5% 키운다 (가로 대비 세로 비율을 그만큼 낮춤).
private const val BannerHeightBoostFactor = 1.05f
// 실제 배너는 3장뿐이지만, 양쪽 끝(1번↔3번)에서도 반대쪽 배너가 자연스럽게 미리보기(peek)되도록
// 아주 큰 가상 페이지 개수를 두고 실제 이미지 인덱스는 나머지 연산(% 3)으로 순환시킨다.
private val BannerVirtualPageCount = BannerImages.size * 1000
// 배너2/3 텍스트 블록 공용 고정 높이. 서브텍스트 줄바꿈 여부와 무관하게 두 배너의 텍스트
// 시작 위치를 정확히 맞추기 위한 값 — 실제 콘텐츠(2줄 서브텍스트 포함)보다 여유 있게 잡는다.
// 번역 언어에서 서브텍스트가 2줄로 늘어나면 기존 130dp로는 배너 카드의 clip 경계에 걸려
// 둘째 줄이 잘렸어서 여유를 더 뒀다.
private val HeroTextBlockHeight = 160.dp

@Composable
private fun HeroBannerSection(onSearchClick: () -> Unit, onWellnessClick: () -> Unit) {
    val initialPage = remember { (BannerVirtualPageCount / 2 / BannerImages.size) * BannerImages.size }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { BannerVirtualPageCount })
    // 배너 박스 비율을 배너2 원본 이미지 비율에 맞춘다. 배너1/3은 그 비율에 맞게 Crop된다.
    val banner2IntrinsicSize = painterResource(id = R.drawable.banner2).intrinsicSize
    val bannerAspectRatio = if (banner2IntrinsicSize.isSpecified && banner2IntrinsicSize.height > 0f) {
        banner2IntrinsicSize.width / banner2IntrinsicSize.height
    } else {
        16f / 9f
    } / BannerHeightBoostFactor

    LaunchedEffect(pagerState) {
        // 가상 페이지가 순환되므로 끝에서 방향을 꺾을 필요 없이 계속 다음 페이지로만 넘어간다.
        while (true) {
            delay(BANNER_AUTO_SCROLL_DELAY_MS)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    Column {
        // 배너 칸 자체에 상하 여백을 줘서 화면에 꽉 차지 않게 한다. 좌우 여백은 아래
        // HorizontalPager의 contentPadding으로 옮겨서, 그 여백 자리에 양옆 배너가 살짝
        // 미리보기(peek)되며 슬라이드되게 한다 — 공식 문서가 권장하는 피킹 캐러셀 패턴.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .aspectRatio(bannerAspectRatio)
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 20.dp),
                pageSpacing = 10.dp,
                modifier = Modifier.fillMaxSize()
            ) { virtualPage ->
                // 가상 페이지 인덱스를 실제 배너 인덱스(0~2)로 순환시킨다.
                val page = virtualPage % BannerImages.size
                // 페이저 전체가 아니라 배너 한 장 한 장에 각각 둥근 모서리를 줘야
                // 옆으로 미리보이는 이웃 배너도 카드 형태로 보인다.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = BannerImages[page]),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        // 배너 박스 비율 자체가 배너2 원본 비율과 같아서 Crop해도 잘리지 않는다.
                        contentScale = ContentScale.Crop
                    )
                    // 배너1(사진에 텍스트 없음, 흰 글씨 사용)만 가독성을 위해 텍스트가 놓이는
                    // 상단 쪽에 옅은 그라데이션을 깐다. 사진 자체가 밝아 흰 글씨가 묻히던 문제를
                    // 완화하는 정도로만 — 사진 톤을 크게 죽이지 않도록 이전(0.45)보다 옅게 둔다.
                    if (page == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent)))
                        )
                    }
                    val strings = LocalAppStrings.current.home
                    when (page) {
                        0 -> {
                            // 배너2/3과 정확히 같은 텍스트 시작 지점을 맞추려면 BiasAlignment 계산
                            // 기준이 되는 요소 자체의 높이도 같아야 한다 — Column만 그대로 정렬하면
                            // 실제 콘텐츠 높이(제목 2줄보다 짧음)가 달라 위치가 어긋난다. 배너2/3과
                            // 동일한 고정 높이 Box로 감싸서 기준 높이를 맞춘다.
                            Box(
                                modifier = Modifier
                                    .align(BiasAlignment(-1f, -0.3f))
                                    .padding(horizontal = 20.dp)
                                    .height(HeroTextBlockHeight),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Column {
                                    Text(text = strings.heroTitle, style = HeroTitleStyle, color = Color.White)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = strings.heroSubtitle,
                                        style = HeroSubtitleStyle,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                        1 -> {
                            // 텍스트는 세로 중앙에, 버튼은 기존 하단 위치를 그대로 유지한다.
                            // 텍스트 블록에 배너2/3 공통 고정 높이를 줘서, 서브텍스트 줄 수가
                            // 달라 Column 실제 높이가 달라져도(1줄 vs 2줄) BiasAlignment 계산
                            // 기준이 같아지므로 두 배너의 텍스트 시작 위치가 정확히 일치한다.
                            Box(
                                modifier = Modifier
                                    .align(BiasAlignment(-1f, -0.3f))
                                    .padding(horizontal = 20.dp)
                                    .height(HeroTextBlockHeight),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Column {
                                    Text(text = strings.heroBanner2TitleMain, style = HeroTitleLargeStyle, color = TextPrimary)
                                    Text(text = strings.heroBanner2TitleAccent, style = HeroTitleLargeStyle, color = CoralMuted)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = strings.heroBanner2Subtitle,
                                        style = HeroSubtitleStyle,
                                        color = HeroBodyGray
                                    )
                                }
                            }
                            BannerCtaButton(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 20.dp)
                                    // 번역 언어에서 서브텍스트가 2줄로 늘어나도 겹치지 않도록
                                    // 텍스트 블록과의 간격을 좀 더 확보한다.
                                    .padding(bottom = 50.dp),
                                text = strings.heroBanner2Cta,
                                onClick = onWellnessClick
                            )
                        }
                        else -> {
                            // 배너2와 동일한 세로 중앙 위치 + 동일한 고정 높이 블록.
                            Box(
                                modifier = Modifier
                                    .align(BiasAlignment(-1f, -0.3f))
                                    .padding(horizontal = 20.dp)
                                    .height(HeroTextBlockHeight),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Column {
                                    Text(text = strings.heroBanner3TitleMain, style = HeroTitleLargeStyle, color = TextPrimary)
                                    Text(text = strings.heroBanner3TitleAccent, style = HeroTitleLargeStyle, color = CoralPrimary)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text(
                                        text = strings.heroBanner3Subtitle,
                                        style = HeroSubtitleStyle,
                                        color = HeroBodyGray
                                    )
                                }
                            }
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
                activePage = pagerState.currentPage % BannerImages.size
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
private fun BannerCtaButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(CoralPrimary)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, style = HeroCtaTextStyle, color = Color.White)
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
            .border(width = 1.dp, color = CoralPrimary.copy(alpha = 0.35f), shape = RoundedCornerShape(percent = 50))
            // 탭하면 결과 목록이 아니라, HospitalSearchListScreen의 검색 입력 모드(최근 검색어/
            // 자동완성 패널)로 바로 진입한다 — onNavigateToSearchFocused 참고.
            .clickable(onClick = onClick)
            .padding(start = 20.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val strings = LocalAppStrings.current
        Text(
            text = strings.common.searchPlaceholder,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = strings.common.searchContentDescription, tint = CoralPrimary)
        }
    }
}

// 섹션 제목(주 텍스트)은 밖에 그대로 두고, 카드/칩 콘텐츠(+그에 딸린 보조 텍스트)만 크게
// 둥근 흰색 카드 영역으로 묶는다. 맨 뒤 페이지 배경(연분홍 그라데이션)은 그대로 비쳐 보인다.
private val SectionCardShape = RoundedCornerShape(40.dp)

@Composable
private fun SectionCardContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    // 페이지 좌우 여백(20dp)까지 흰색으로 덮도록 카드 자체는 화면 폭 끝까지 채우고, 대신 안쪽
    // 콘텐츠에 같은 20dp를 다시 줘서 다른 요소들과 좌우 정렬은 그대로 맞춘다.
    Box(
        modifier = modifier
            .fillMaxWidth()
            // 큰 흰 영역이라 카드류(0.04 알파)보다 강하게 — BottomNavBar와 같은 톤의 진한 그림자.
            .shadow(
                elevation = 8.dp,
                shape = SectionCardShape,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(SectionCardShape)
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        content()
    }
}

// "AI 진단하기" 챗봇 진입점. 온보딩 강제 흐름에서 빠진 준비 유형 진단(챗봇)은 이제 여기서만
// 접근한다(MediInBusanNavHost.kt의 Route.SelfDiagnosis 배선 참고). Scaffold의 floatingActionButton
// 슬롯에 얹혀있어 스크롤과 무관하게 항상 같은 자리(바텀바 바로 위)에 떠 있다.
@Composable
private fun AiChatFab(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val strings = LocalAppStrings.current.chat
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        containerColor = CoralPrimary,
        contentColor = Color.White
    ) {
        Text(
            text = strings.chatBubbleLabel,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

// 의료 목적 선택(9개) + 바로가기의 "추천 웰니스"(나머지 바로가기는 바텀바와 중복이라 제외)를
// 하나의 원형 아이콘 그리드로 합친다. 웰니스는 검색 필터가 아니라 원래 바로가기와 같은 목적지
// (onWellnessClick)로 보내야 해서 목록 맨 끝에 두고 클릭 핸들러도 분기한다.
private val CategoryGridOrder = listOf(
    MedicalCategory.SKIN_BEAUTY,
    MedicalCategory.HEALTH_CHECKUP,
    MedicalCategory.DENTAL,
    MedicalCategory.ORIENTAL_MEDICINE,
    MedicalCategory.REHABILITATION,
    MedicalCategory.PLASTIC_SURGERY,
    MedicalCategory.OBSTETRICS_GYNECOLOGY,
    MedicalCategory.OPHTHALMOLOGY,
    MedicalCategory.ETC,
    MedicalCategory.WELLNESS
)
private const val CategoryGridColumns = 5

@Composable
private fun CategoryGridSection(
    modifier: Modifier = Modifier,
    onPurposeClick: (MedicalCategory) -> Unit,
    onWellnessClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    SectionCardContainer(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CategoryGridOrder.chunked(CategoryGridColumns).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { item ->
                        val isWellness = item == MedicalCategory.WELLNESS
                        CategoryCircleItem(
                            iconRes = item.iconRes,
                            label = if (isWellness) {
                                quickLinkLabel(QuickLinkType.WELLNESS, strings.home)
                            } else {
                                item.translatedLabel(strings.language)
                            },
                            modifier = Modifier.weight(1f),
                            onClick = { if (isWellness) onWellnessClick() else onPurposeClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCircleItem(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            // 기존 RoundIconButton과 동일한 순서(size → clip → background → clickable) —
            // clip이 clickable보다 먼저라 리플이 원 밖으로 안 번지고 원 안에서만 퍼진다.
            // clickable을 라벨 텍스트까지 포함한 바깥 Column이 아니라 이 원에만 걸어서
            // 클릭 영역도 원 안으로 제한한다.
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(width = 1.dp, color = DividerColor, shape = CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

private fun quickLinkLabel(type: QuickLinkType, strings: HomeStrings): String =
    when (type) {
        QuickLinkType.HOSPITAL_LIST -> strings.quickLinkHospitalList
        QuickLinkType.GUIDE -> strings.quickLinkGuide
        QuickLinkType.WELLNESS -> strings.quickLinkWellness
        QuickLinkType.MAP -> strings.quickLinkMap
        QuickLinkType.SELF_DIAGNOSIS -> strings.quickLinkSelfDiagnosis
        QuickLinkType.FAVORITE -> strings.quickLinkFavorite
    }

@Composable
private fun RecommendedHospitalSection(
    modifier: Modifier = Modifier,
    hospitals: List<Hospital>,
    onHospitalClick: (String) -> Unit
) {
    val strings = LocalAppStrings.current.home
    Column(modifier = modifier) {
        Text(
            text = strings.recommendedHospitalsSectionTitle,
            style = SectionTitleStyle,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        // 카드 안 보조 텍스트(병원 주소)와 같은 스타일로 통일.
        Text(
            text = strings.recommendedHospitalsSubtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        SectionCardContainer {
            if (hospitals.isEmpty()) {
                // F-019 전체화면 EmptyState 대신, 결정사항에 따라 섹션 내부에 텍스트만 인라인 표시
                Text(
                    text = strings.recommendedHospitalsEmpty,
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
                            onClick = { onHospitalClick(hospital.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendedHospitalCard(
    hospital: Hospital,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(170.dp)
            .shadow(
                elevation = 3.dp,
                shape = MaterialTheme.shapes.large,
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
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
            if (hospital.imageUrl != null) {
                AsyncImageBox(
                    model = hospital.imageUrl,
                    contentDescription = hospital.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            } else {
                Image(
                    painter = painterResource(
                        id = resolveHospitalThumbnailRes(hospital.name, hospital.specialties)
                    ),
                    contentDescription = hospital.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }
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

@Composable
private fun RecommendedCourseSection(
    courses: List<HomeRecommendedCourse>,
    isLoading: Boolean,
    hasMore: Boolean,
    hasError: Boolean,
    onCourseClick: (HomeRecommendedCourse) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.home
    val listState = rememberLazyListState()
    LaunchedEffect(listState, courses.size, hasMore, isLoading) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            info.totalItemsCount > 0 && lastVisible >= info.totalItemsCount - 2
        }
            .distinctUntilChanged()
            .filter { it && hasMore && !isLoading }
            .collect { onLoadMore() }
    }
    Column(modifier = modifier) {
        Text(
            text = strings.recommendedCourseSectionTitle,
            style = SectionTitleStyle,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = strings.recommendedCourseSubtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        SectionCardContainer {
            when {
                courses.isEmpty() && isLoading -> Box(
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CoralPrimary, strokeWidth = 2.dp)
                }
                courses.isEmpty() -> Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(strings.recommendedCourseEmpty, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    if (hasError) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = strings.recommendedCourseRetry,
                            modifier = Modifier.clickable(onClick = onRetry).padding(8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = CoralPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else -> LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(courses, key = { it.id }) { course ->
                        RecommendedCourseCard(course = course, onClick = { onCourseClick(course) })
                    }
                    if (isLoading) {
                        item(key = "course-loading") {
                            Box(modifier = Modifier.width(64.dp).height(210.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = CoralPrimary, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendedCourseCard(course: HomeRecommendedCourse, onClick: () -> Unit) {
    val strings = LocalAppStrings.current.home
    val representative = course.course.stops.first()
    val title = course.course.stops.take(2).joinToString(" · ") { it.item.title }
    val location = course.district?.label ?: representative.item.address.orEmpty()
    Column(
        modifier = Modifier
            .width(210.dp)
            .shadow(
                elevation = 3.dp,
                shape = MaterialTheme.shapes.large,
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = MaterialTheme.shapes.large)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(138.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {
            AsyncImageBox(
                model = representative.item.imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "${course.course.stops.size}${strings.courseStopsSuffix}",
                modifier = Modifier.align(Alignment.TopStart).padding(9.dp).background(
                    color = Color.White.copy(alpha = 0.92f),
                    shape = CircleShape
                ).padding(horizontal = 9.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelSmall,
                color = CoralPrimary
            )
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = CardTitleStyle,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = location,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
            onNavigateToHospitalDetail = {},
            onNavigateToGuide = {},
            onNavigateToMap = {},
            onNavigateToWellness = {},
            onNavigateToRecommendedCourse = { _, _ -> },
            onNavigateToSelfDiagnosis = {},
            onNavigateToSearch = {},
            onNavigateToSearchFocused = {},
            onNavigateToFavorite = {},
            onNavigateToSettings = {},
            onPurposeSelected = {},
            onSearchBarClicked = {},
            onRetry = {},
            onLoadMoreCourses = {},
            onRetryCourses = {},
            onLanguageSelected = {}
        )
    }
}

@Preview(name = "Home - 데이터 있음", showBackground = true)
@Composable
private fun HomeContentDataPreview() {
    PreviewHomeContent(
        uiState = HomeUiState(
            recommendedHospitals = PreviewHospitals,
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
