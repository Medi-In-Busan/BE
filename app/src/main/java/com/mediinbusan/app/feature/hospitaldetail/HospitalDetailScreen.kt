package com.mediinbusan.app.feature.hospitaldetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.common.MedicalCategory
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.HospitalDetailStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.designsystem.BadgeOutline
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.StatusClosedGray
import com.mediinbusan.app.core.designsystem.StatusOpenGreen
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.FavoriteHeartButton
import com.mediinbusan.app.core.ui.LanguageBadge
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.RoundIconButton
import com.mediinbusan.app.core.ui.WrapRow
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.core.ui.toLanguageBadgeLabel
import com.mediinbusan.app.core.ui.toLanguageDisplayName
import com.mediinbusan.app.data.hospital.Hospital

@Composable
fun HospitalDetailScreen(
    hospitalId: String,
    onNavigateToGuide: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToMap: () -> Unit,
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
    Box(modifier = Modifier.fillMaxSize().background(HomeBackgroundPink)) {
        when {
            uiState.isLoading -> LoadingState()
            uiState.isError -> ErrorState(
                message = uiState.errorMessage ?: LocalAppStrings.current.hospitalDetail.genericErrorFallback,
                onRetry = { viewModel.load(hospitalId) }
            )
            hospital != null -> HospitalDetailContent(
                hospital = hospital,
                isFavorite = uiState.isFavorite,
                onToggleFavorite = viewModel::onToggleFavorite,
                onNavigateToGuide = onNavigateToGuide,
                onNavigateToNearby = onNavigateToNearby,
                onNavigateToMap = onNavigateToMap,
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
    onToggleFavorite: () -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToMap: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val strings = LocalAppStrings.current.hospitalDetail
    // 하드코딩한 값 대신 실측한 풋터 높이를 그대로 스크롤 콘텐츠 하단 여백으로 써서, 콘텐츠와
    // 풋터 사이에 뜬 여백(또는 반대로 풋터에 가려지는 현상) 없이 정확히 맞닿게 한다.
    var bottomBarHeight by remember { mutableStateOf(0.dp) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = bottomBarHeight)
        ) {
            ImageCarouselSection(
                imageUrls = hospital.imageUrls,
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite,
                onBack = onBack,
                onShare = { context.shareHospital(hospital) },
                strings = strings
            )

            // 웰니스 프로그램다운 톤: 굵은 회색 구분선으로 정보를 뚝뚝 끊어내던 "행정 서류" 같은
            // 느낌 대신, 옅은 분홍 배경(HomeBackgroundPink) 위에 카드가 하나씩 둥둥 떠 있는 스파
            // 앱 스타일 레이아웃으로 바꿨다. 각 InfoSection이 스스로 흰 카드+그림자를 두르므로
            // (아래 InfoSection 정의 참고) 여기서는 카드 사이 여백만 둔다.
            Spacer(modifier = Modifier.height(4.dp))
            SectionCard {
                CategoryAndStatusRow(hospital = hospital, strings = strings)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = hospital.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (hospital.supportedLanguages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        hospital.supportedLanguages.forEach { lang -> LanguageBadge(text = lang.toLanguageBadgeLabel()) }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
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

                Spacer(modifier = Modifier.height(16.dp))
                ActionButtonsRow(hospital = hospital, strings = strings)
            }

            Spacer(modifier = Modifier.height(14.dp))
            InfoSection(title = strings.introSectionTitle) {
                Text(
                    text = hospital.description ?: strings.introEmpty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            InfoSection(title = strings.basicInfoSectionTitle) {
                BasicInfoRow(icon = Icons.Default.AccessTime, label = strings.openingHoursLabel, value = hospital.openingHours ?: strings.infoNotAvailable)
                BasicInfoRow(icon = Icons.Default.Phone, label = strings.phoneLabel, value = hospital.phoneNumber ?: strings.infoNotAvailable)
                BasicInfoRow(icon = Icons.Default.Public, label = strings.homepageLabel, value = hospital.homepageUrl ?: strings.infoNotAvailable)
                BasicInfoRow(
                    icon = Icons.Default.Language,
                    label = strings.supportedLanguagesLabel,
                    value = hospital.supportedLanguages.takeIf { it.isNotEmpty() }
                        ?.joinToString(" · ") { it.toLanguageDisplayName() }
                        ?: strings.infoNotAvailable
                )
            }

            if (hospital.specialties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
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
            InfoSection(title = strings.locationSectionTitle) {
                LocationMiniMap(hospital = hospital, onExpandClick = onNavigateToMap, strings = strings)
                Spacer(modifier = Modifier.height(12.dp))
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
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { context.launchDirections(hospital) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = strings.directionsButton)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            SectionCard(innerPadding = 0.dp) {
                QuickLinkRow(label = strings.guideQuickLink, onClick = onNavigateToGuide)
                HorizontalDivider(thickness = 1.dp, color = DividerColor)
                QuickLinkRow(label = strings.nearbyQuickLink, onClick = onNavigateToNearby)
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        BottomActionBar(
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite,
            onCallClick = { context.dialPhone(hospital.phoneNumber) },
            callEnabled = !hospital.phoneNumber.isNullOrBlank(),
            strings = strings,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onGloballyPositioned { coordinates ->
                    bottomBarHeight = with(density) { coordinates.size.height.toDp() }
                }
        )
    }
}

@Composable
private fun ImageCarouselSection(
    imageUrls: List<String>,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit,
    strings: HospitalDetailStrings
) {
    val pageCount = imageUrls.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    // 하단 모서리를 둥글게 깎아 아래 카드 스택으로 이어지는 전환을 부드럽게 한다(각진 사각형 →
    // 스파 앱다운 곡선). 뒤로가기·공유 같은 오버레이 컨트롤은 위쪽에 있어 영향받지 않는다.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
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
        }

        RoundIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = LocalAppStrings.current.common.backContentDescription,
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            size = 36.dp
        )
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            // 두 버튼 다 시각적으로는 36dp인데 RoundIconButton/FavoriteHeartButton이 각자
            // minimumInteractiveComponentSize()로 터치 영역을 48dp까지 보이지 않게 넓힌다 —
            // 8dp 간격이면 그 넓어진 터치 영역끼리 겹쳐서 옆 버튼을 눌러버릴 수 있어 12dp로 늘린다.
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RoundIconButton(icon = Icons.Default.Share, contentDescription = strings.shareContentDescription, onClick = onShare, size = 36.dp)
            FavoriteHeartButton(isFavorite = isFavorite, onClick = onToggleFavorite)
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
                text = "${pagerState.currentPage + 1}/$pageCount",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

// hospital.specialties는 API 응답 원문(현재는 샘플 데이터) 그대로라 건드리지 않고, 화면에
// 그릴 때만 MedicalCategory 라벨과 일치하면 언어별 문구로 바꿔 보여준다. 매칭되는 카테고리가
// 없는 자유 문구(예: 실제 API의 세부 진료과목명)는 원문을 그대로 표시한다.
private fun translatedSpecialtyLabel(specialty: String, language: SupportedLanguage): String =
    MedicalCategory.entries.find { it.label == specialty }?.translatedLabel(language) ?: specialty

@Composable
private fun CategoryAndStatusRow(hospital: Hospital, strings: HospitalDetailStrings) {
    val language = LocalAppStrings.current.language
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 영어 등 긴 번역 문구가 2개 붙으면 오른쪽 영업 상태 배지와 부딪힐 수 있어, weight(fill=false)로
        // 남는 공간만 쓰게 하고 WrapRow로 넘치면 아래 줄로 흘러가게 한다(위 진료과목 섹션과 같은 이유 —
        // 실험적 FlowRow는 이 프로젝트에서 NoSuchMethodError로 크래시났다).
        WrapRow(
            modifier = Modifier.weight(1f, fill = false),
            horizontalSpacing = 6.dp,
            verticalSpacing = 4.dp
        ) {
            hospital.specialties.take(2).forEach { specialty ->
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(CoralPrimaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    // SpecialtyChip과 같은 이유로 줄바꿈을 막는다(FlowRow가 좁은 잔여 공간에 텍스트를
                    // 세로로 욱여넣는 것 방지).
                    // 글자색은 CoralPrimary가 아니라 TextPrimary를 쓴다 — CoralPrimaryContainer
                    // 배경 위 CoralPrimary 텍스트는 명암비를 재보면 약 2.5:1로, WCAG AA 본문
                    // 기준(4.5:1)에 크게 못 미친다(UI/UX Pro Max 스킬 검증). 배경의 코랄 톤만으로도
                    // "강조 배지"라는 건 충분히 전달되니 실제 읽어야 하는 글자는 명암비가 넉넉한
                    // 짙은 색으로 둔다.
                    Text(
                        text = translatedSpecialtyLabel(specialty, language),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
        hospital.isOpen?.let { isOpen ->
            val (color, label) = if (isOpen) StatusOpenGreen to strings.statusOpen else StatusClosedGray to strings.statusClosed
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(hospital: Hospital, strings: HospitalDetailStrings) {
    val context = LocalContext.current
    val hasPhoneNumber = !hospital.phoneNumber.isNullOrBlank()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Phone,
            label = strings.actionCall,
            backgroundColor = CoralPrimaryContainer,
            iconTint = CoralPrimary,
            enabled = hasPhoneNumber,
            onClick = { context.dialPhone(hospital.phoneNumber) }
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Navigation,
            label = strings.actionDirections,
            backgroundColor = Color(0xFFEAF4FB),
            iconTint = SkyBlue,
            onClick = { context.launchDirections(hospital) }
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Filled.Chat,
            label = strings.actionInquiry,
            backgroundColor = Color(0xFFF2F2F2),
            iconTint = TextSecondary,
            enabled = hasPhoneNumber,
            onClick = { context.smsInquiry(hospital, strings) }
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Share,
            label = strings.actionShare,
            backgroundColor = Color(0xFFF2F2F2),
            iconTint = TextSecondary,
            onClick = { context.shareHospital(hospital) }
        )
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    backgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val contentAlpha = if (enabled) 1f else 0.4f
    Column(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(backgroundColor.copy(alpha = contentAlpha))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // clickable()이 이 Column 전체를 하나의 접근성 노드로 묶기 때문에, 아이콘에도
            // label과 같은 contentDescription을 달면 바로 아래 Text(label)와 겹쳐 "통화, 통화"처럼
            // 두 번 읽힌다 — 장식용으로 뺀다(MapScreen의 FilterPillButton과 같은 이유).
            Icon(imageVector = icon, contentDescription = null, tint = iconTint.copy(alpha = contentAlpha), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextPrimary.copy(alpha = contentAlpha))
    }
}

// 웰니스 프로그램의 스파 앱다운 톤을 위해 각 정보 구획을 옅은 분홍 배경 위에 뜬 흰 카드로
// 감싼다(Soft UI: 은은한 그림자 + 넉넉한 라운드 코너). InfoSection은 제목+본문을 카드 안에
// 배치하는 표준 형태이고, SectionCard는 제목 없이 카드만 필요한 곳(상단 정보 블록, 바로가기
// 묶음)에 쓰는 더 낮은 레벨의 래퍼다.
@Composable
private fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    SectionCard {
        Text(text = title, style = SectionTitleStyle, color = TextPrimary)
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
            .shadow(
                elevation = 2.dp,
                shape = MaterialTheme.shapes.large,
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .clip(MaterialTheme.shapes.large)
            .background(Color.White)
            .padding(innerPadding),
        content = content
    )
}

@Composable
private fun BasicInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).background(CoralPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(15.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.width(72.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
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

@Composable
private fun QuickLinkRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}

@Composable
private fun BottomActionBar(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCallClick: () -> Unit,
    callEnabled: Boolean,
    strings: HospitalDetailStrings,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(width = 1.dp, color = DividerColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                FavoriteHeartButton(isFavorite = isFavorite, onClick = onToggleFavorite, size = 32.dp)
            }
            Button(
                onClick = onCallClick,
                enabled = callEnabled,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
            ) {
                Text(
                    text = if (callEnabled) strings.callButton else strings.callButtonDisabled,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
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
