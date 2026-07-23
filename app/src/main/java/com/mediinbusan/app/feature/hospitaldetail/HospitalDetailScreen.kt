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
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.BadgeOutline
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
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
    val errorMessage = uiState.errorMessage

    LaunchedEffect(hospitalId) {
        viewModel.load(hospitalId)
    }

    when {
        uiState.isLoading -> LoadingState()
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(hospitalId) })
        hospital != null -> HospitalDetailContent(
            hospital = hospital,
            isFavorite = uiState.isFavorite,
            onToggleFavorite = viewModel::onToggleFavorite,
            onNavigateToGuide = onNavigateToGuide,
            onNavigateToNearby = onNavigateToNearby,
            onNavigateToMap = onNavigateToMap,
            onBack = onBack
        )
        else -> EmptyState(message = "병원 정보를 찾을 수 없습니다.")
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
                onShare = { context.shareHospital(hospital) }
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                CategoryAndStatusRow(hospital = hospital)
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
                ActionButtonsRow(hospital = hospital)
            }

            HorizontalDivider(thickness = 8.dp, color = DividerColor)
            InfoSection(title = "병원 소개") {
                Text(
                    text = hospital.description ?: "등록된 소개 정보가 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }

            HorizontalDivider(thickness = 8.dp, color = DividerColor)
            InfoSection(title = "기본 정보") {
                BasicInfoRow(icon = Icons.Default.AccessTime, label = "운영시간", value = hospital.openingHours ?: "정보 없음")
                BasicInfoRow(icon = Icons.Default.Phone, label = "전화", value = hospital.phoneNumber ?: "정보 없음")
                BasicInfoRow(icon = Icons.Default.Public, label = "홈페이지", value = hospital.homepageUrl ?: "정보 없음")
                BasicInfoRow(
                    icon = Icons.Default.Language,
                    label = "지원 언어",
                    value = hospital.supportedLanguages.takeIf { it.isNotEmpty() }
                        ?.joinToString(" · ") { it.toLanguageDisplayName() }
                        ?: "정보 없음"
                )
            }

            if (hospital.specialties.isNotEmpty()) {
                HorizontalDivider(thickness = 8.dp, color = DividerColor)
                InfoSection(title = "진료과목") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        hospital.specialties.forEach { specialty -> SpecialtyChip(text = specialty) }
                    }
                }
            }

            HorizontalDivider(thickness = 8.dp, color = DividerColor)
            InfoSection(title = "위치") {
                LocationMiniMap(hospital = hospital, onExpandClick = onNavigateToMap)
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
                    Text(text = "길찾기")
                }
            }

            HorizontalDivider(thickness = 8.dp, color = DividerColor)
            QuickLinkRow(label = "의료 이용 가이드 보기", onClick = onNavigateToGuide)
            HorizontalDivider(thickness = 1.dp, color = DividerColor)
            QuickLinkRow(label = "주변 관광·웰니스 보기", onClick = onNavigateToNearby)
        }

        BottomActionBar(
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite,
            onCallClick = { context.dialPhone(hospital.phoneNumber) },
            callEnabled = !hospital.phoneNumber.isNullOrBlank(),
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
    onShare: () -> Unit
) {
    val pageCount = imageUrls.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val imageUrl = imageUrls.getOrNull(page)
            if (imageUrl != null) {
                AsyncImageBox(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().background(Color(0xFFE9E9EE))
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFE9E9EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "병원 대표 이미지", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }
        }

        RoundIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "뒤로가기",
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            size = 36.dp
        )
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoundIconButton(icon = Icons.Default.Share, contentDescription = "공유", onClick = onShare, size = 36.dp)
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

@Composable
private fun CategoryAndStatusRow(hospital: Hospital) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            hospital.specialties.take(2).forEach { specialty ->
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(CoralPrimaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = specialty, style = MaterialTheme.typography.labelSmall, color = CoralPrimary)
                }
            }
        }
        hospital.isOpen?.let { isOpen ->
            val (color, label) = if (isOpen) StatusOpenGreen to "영업 중" else StatusClosedGray to "영업 종료"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(hospital: Hospital) {
    val context = LocalContext.current
    val hasPhoneNumber = !hospital.phoneNumber.isNullOrBlank()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Phone,
            label = "전화",
            backgroundColor = CoralPrimaryContainer,
            iconTint = CoralPrimary,
            enabled = hasPhoneNumber,
            onClick = { context.dialPhone(hospital.phoneNumber) }
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Navigation,
            label = "길찾기",
            backgroundColor = Color(0xFFEAF4FB),
            iconTint = SkyBlue,
            onClick = { context.launchDirections(hospital) }
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Filled.Chat,
            label = "문의",
            backgroundColor = Color(0xFFF2F2F2),
            iconTint = TextSecondary,
            enabled = hasPhoneNumber,
            onClick = { context.smsInquiry(hospital) }
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Share,
            label = "공유",
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
            Icon(imageVector = icon, contentDescription = label, tint = iconTint.copy(alpha = contentAlpha), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextPrimary.copy(alpha = contentAlpha))
    }
}

@Composable
private fun InfoSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(text = title, style = SectionTitleStyle, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun BasicInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.width(72.dp)
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SpecialtyChip(text: String) {
    Box(
        modifier = Modifier
            .border(width = 1.dp, color = BadgeOutline, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
    }
}

@Composable
private fun LocationMiniMap(hospital: Hospital, onExpandClick: () -> Unit) {
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
                Text(text = "위치 정보가 없습니다", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
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
                    text = if (callEnabled) "전화 문의하기" else "전화번호 정보 없음",
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
private fun Context.smsInquiry(hospital: Hospital) {
    val phoneNumber = hospital.phoneNumber
    if (phoneNumber.isNullOrBlank()) return
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber")).apply {
        putExtra("sms_body", "[메디인부산] ${hospital.name} 문의드립니다.")
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
