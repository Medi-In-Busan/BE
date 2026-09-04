package com.mediinbusan.app.feature.nearby

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.InactiveIcon
import com.mediinbusan.app.core.designsystem.MediBlue40
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.i18n.translatedRecoveryHint
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.DetailPullDismissBox
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.placeKindVisual
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import java.util.Locale

@Composable
fun PlaceDetailScreen(
    placeId: String,
    onBack: () -> Unit,
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val language = LocalAppStrings.current.language

    LaunchedEffect(placeId, language) {
        viewModel.load(placeId)
    }

    // NearbyScreen(웰니스·관광 목록)의 배경(WellnessCanvas, 0xFFFFFAFF)과 값을 맞춰, 그 화면에서
    // 넘어왔을 때 배경색이 끊겨 보이지 않게 한다(HospitalDetailScreen이 HomeBackgroundPink를
    // 재사용하는 것과 같은 이유 — core/designsystem 공용 토큰이 아니라 화면마다 자기 캔버스색을
    // 갖는 기존 관례를 그대로 따른다).
    // HospitalDetailScreen과 같은 이유 — 지도에서 카드를 끌어올려 들어온 화면을 아래로 끌어
    // 되돌린다(core/ui/DetailPullTransition.kt).
    DetailPullDismissBox(
        onDismiss = onBack,
        modifier = Modifier.fillMaxSize().background(PlaceDetailCanvas)
    ) {
        val place = uiState.place
        val errorMessage = uiState.errorMessage
        when {
            uiState.isLoading -> LoadingState()
            errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(placeId) })
            place != null -> PlaceDetailContent(
                place = place,
                isFavorite = uiState.isFavorite,
                onToggleFavorite = viewModel::onToggleFavorite,
                onBack = onBack
            )
            else -> EmptyState(message = LocalAppStrings.current.nearby.placeNotFoundMessage)
        }
    }
}

@Composable
private fun PlaceDetailContent(
    place: Place,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val strings = LocalAppStrings.current
    // 이 화면 전체가 쓰는 액센트 — 지도 마커/클러스터와 같은 색이라, 지도에서 파란 관광 핀을
    // 눌러 들어오면 상세 화면도 파란 톤으로 이어진다(PlaceType.tint 참고).
    val accent = place.type.tint
    // 하단 고정 액션바 실측 높이를 그대로 스크롤 콘텐츠 하단 여백으로 써서, 콘텐츠가 액션바에
    // 가려지거나 반대로 그 사이에 빈 여백이 남지 않고 정확히 맞닿게 한다(HospitalDetailScreen과 동일).
    var bottomBarHeight by remember { mutableStateOf(0.dp) }
    val scrollState = rememberScrollState()
    // 히어로 사진을 지나 스크롤하면 상단바가 서서히 나타난다 — 예전엔 흰 배경 위에 코랄 화살표만
    // 덩그러니 떠서 본문(주소 줄)과 겹쳐 보였다. 사진 높이의 절반쯤 지나면 완전히 불투명해진다.
    // 사진이 없는 장소도 이제 마스코트 일러스트가 그 자리를 채우므로(PlaceHeroSection) 사진과
    // 같은 높이를 쓴다 — 아이콘 배지 하나뿐이라 화면 위쪽이 휑하던 시절엔 한 단계 낮춰뒀었다.
    val heroHeight = HeroHeight
    val topBarAlpha by remember(heroHeight) {
        derivedStateOf {
            val fadeDistancePx = with(density) { (heroHeight / 2).toPx() }
            (scrollState.value / fadeDistancePx).coerceIn(0f, 1f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = bottomBarHeight)
            ) {
                PlaceHeroSection(place = place, height = heroHeight)

                // 사진 위로 콘텐츠 시트를 끌어올려 겹친다 — 사진이 카드처럼 따로 떠 있던 예전
                // 레이아웃보다 화면이 한 장으로 이어져 보인다(장소/숙소 앱들의 표준 상세 패턴).
                Surface(
                    modifier = Modifier.fillMaxWidth().offset(y = -SheetOverlap),
                    color = PlaceDetailCanvas,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(26.dp))
                PlaceTitleSection(place = place)

                Spacer(modifier = Modifier.height(18.dp))
                // 예전엔 즐겨찾기·공유가 제목 옆 작은 아이콘 두 개였고 전화는 정보 표 안에 묻혀
                // 있었다 — 세 가지를 같은 크기의 액션 줄로 끌어올린다(길찾기는 하단 CTA 하나로
                // 유지: 화면에 길찾기 진입점을 둘로 늘리지 않는다).
                QuickActionRow(
                    accent = accent,
                    isFavorite = isFavorite,
                    phoneNumber = place.phoneNumber?.takeUnless { it.isBlank() },
                    onToggleFavorite = onToggleFavorite,
                    onShare = { context.sharePlace(place) },
                    onCall = { place.phoneNumber?.let { context.dialPhone(it) } }
                )

                Spacer(modifier = Modifier.height(26.dp))
                // HospitalDetailScreen의 기본정보(BasicInfoRow: 운영시간/전화/홈페이지/언어) 카드와
                // 같은 아이콘 원형+라벨+값 구성 — 전화·거리에 더해, 지금까지 화면 어디에도 없던
                // "정보 갱신일"(place.lastModified, 웰니스 API 원본에 실려 오지만 그동안 안 쓰였다)을
                // 세 번째 행으로 추가한다. 전화 행은 그 자체가 다이얼 액션도 겸해서, 예전에 따로
                // 있던 "전화" 아이콘 버튼(ActionButtonsRow)이 여기 하나로 합쳐진다.
                // 값이 없는 행은 아예 그리지 않는다 — 웰니스 API는 전화·거리가 비어 오는 장소가
                // 많아서, 예전엔 "정보 없음"만 두 줄 채운 카드가 화면 한가운데를 차지했다.
                // (거리는 병원을 기준으로 조회했을 때만 의미가 있어, 지도에서 바로 들어온 경우엔
                // 기준점이 없다 — 없는 걸 임의의 좌표로 지어내지 않고 행 자체를 뺀다.)
                val phoneNumber = place.phoneNumber?.takeUnless { it.isBlank() }
                val distanceText = place.distanceFromHospitalMeters
                    ?.let { strings.nearby.distanceFromHospitalFormat.format(it.toDistanceLabel()) }
                val lastUpdated = place.lastModified?.takeUnless { it.isBlank() }?.toDisplayDate()
                if (phoneNumber != null || distanceText != null || lastUpdated != null) {
                    InfoSection(title = strings.hospitalDetail.basicInfoSectionTitle, accent = accent) {
                        phoneNumber?.let {
                            BasicInfoRow(
                                icon = Icons.Default.Call,
                                label = strings.hospitalDetail.phoneLabel,
                                value = it,
                                accent = accent,
                                onClick = { context.dialPhone(it) }
                            )
                        }
                        distanceText?.let {
                            BasicInfoRow(
                                icon = Icons.Default.NearMe,
                                label = strings.nearby.distanceLabel,
                                value = it,
                                accent = accent
                            )
                        }
                        lastUpdated?.let {
                            BasicInfoRow(
                                icon = Icons.Default.Update,
                                label = strings.nearby.lastUpdatedLabel,
                                value = it,
                                accent = accent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                place.displayDescription?.takeUnless { it.isBlank() }?.let { description ->
                    InfoSection(title = strings.nearby.introSectionTitle, icon = Icons.Default.Info, accent = accent) {
                        Text(text = description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                RecoveryNoticeSection(place = place, accent = accent)

                Spacer(modifier = Modifier.height(14.dp))
                // 이 미니맵은 이제 순수 미리보기다 — 예전엔 지도 자체도 눌리고(외부 길찾기), 그 바로
                // 아래 "길찾기" 버튼도 있고, 화면 맨 아래 고정바에도 같은 "길찾기" 버튼이 있어 한
                // 화면에 길찾기 진입점이 4개(액션 pill/지도/버튼/하단바)였다 — 하단 고정 CTA
                // 하나로 합치고 나머지는 없앤다.
                InfoSection(title = strings.hospitalDetail.locationSectionTitle, icon = Icons.Default.Place, accent = accent) {
                    LocationMiniMap(place = place)
                }
                        // 시트를 위로 끌어올린 만큼(SheetOverlap) 아래에서 다시 채워, 마지막 카드와
                        // 하단 액션바 사이 간격이 예전과 같게 유지한다.
                        Spacer(modifier = Modifier.height(14.dp + SheetOverlap))
                    }
                }
            }

            // 길찾기 CTA는 화면 전체에서 이 버튼 하나뿐이다(위 주석 참고) — 즐겨찾기도 타이틀 줄
            // (PlaceTitleSection)에 이미 있어 하단바에서는 빼고, 폭 전체를 길찾기 버튼에 준다.
            BottomActionBar(
                onDirectionsClick = { context.launchDirections(place) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onGloballyPositioned { coordinates ->
                        bottomBarHeight = with(density) { coordinates.size.height.toDp() }
                    }
            )
        }
        // 스크롤에 따라 나타나는 상단바 — 사진 위에서는 투명하고(사진을 가리지 않는다), 본문
        // 구간에서는 흰 배경 + 장소 이름이 떠서 지금 뭘 보고 있는지가 계속 보인다.
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .graphicsLayer { alpha = topBarAlpha },
            color = Color.White,
            shadowElevation = if (topBarAlpha > 0.95f) 3.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 56.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        // 뒤로가기는 사진 위(어두운 배경)와 흰 본문 위 양쪽에 얹히므로, 아이콘만 두면 한쪽에서
        // 반드시 묻힌다 — 반투명 흰 원을 깔아 어디서든 같은 대비를 유지한다.
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
                .size(40.dp)
                .shadow(elevation = 2.dp, shape = CircleShape, ambientColor = Color.Black.copy(alpha = 0.2f), spotColor = Color.Black.copy(alpha = 0.2f))
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.92f))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = LocalAppStrings.current.common.backContentDescription,
                tint = CoralPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PlaceHeroSection(place: Place, height: Dp) {
    // 사진은 사진 역할만 한다 — 예전엔 이 위에 카테고리 라벨과 장소 이름을 얹었는데, 바로 아래
    // PlaceTitleSection이 같은 배지와 같은 제목을 한 번 더 보여줘서 화면 상단에 같은 문구가 두 번
    // 나왔다. 텍스트를 전부 아래 타이틀 블록으로 몰아, 사진이 없는 장소에서도 레이아웃이 같아진다.
    // 좌우 여백 없이 화면 폭을 꽉 채우고 상태바 아래까지 올라간다 — 여백을 두고 둥글게 잘린
    // "사진 카드"보다 몰입감이 크고, 바로 아래 콘텐츠 시트가 이 위로 겹쳐 올라오면서 화면이 한 장으로 읽힌다.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (place.imageUrl != null) {
            AsyncImageBox(
                model = place.imageUrl,
                contentDescription = place.name,
                modifier = Modifier.fillMaxSize()
            )
            // 위쪽은 상태바 아이콘과 뒤로가기 버튼이 얹히는 자리라 살짝 눌러 대비를 확보하고,
            // 아래쪽은 시트가 겹치는 자리라 그대로 둔다.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent),
                            endY = 320f
                        )
                    )
            )
        } else {
            // 실제 사진이 없는 장소가 많아(웰니스 API 원문에 이미지가 비어있는 경우) 이 자리표시자가
            // 사실상 기본 히어로가 된다 — 예전엔 작은 아이콘 배지 하나뿐이라 화면 위쪽이 휑했다.
            // 대신 종류에 맞는 마스코트 일러스트를 세우고, 배경은 그대로 장소 종류(place.type.tint)에
            // 맞춘 옅은 그라데이션을 깔아 투명한 캐릭터가 얹힐 바닥을 만든다.
            //
            // 상세화면 전용이다 — 리스트/카드 썸네일은 지금처럼 fallbackBannerImageFor()를 쓴다.
            // 같은 캐릭터를 목록 행마다 반복하면 행이 전부 똑같아 보여서 구분이 되지 않는다.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(place.type.tint.copy(alpha = 0.22f), Color(0xFFEDEDF2)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 이름/카테고리는 아래 타이틀 블록이 책임진다. 그림 자체에 "관광 SIGHTSEEING" /
                // "식사 FOOD" 표지판이 그려져 있어 별도 배지를 겹치지 않아도 종류가 읽힌다.
                Image(
                    painter = painterResource(id = place.type.fallbackCharacterImage()),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        // 위는 상태바와 떠 있는 뒤로가기 버튼이, 아래는 겹쳐 올라오는 콘텐츠
                        // 시트(SheetOverlap)가 덮는 자리다 — 캐릭터의 모자와 발이 그 밑으로
                        // 잘리지 않게 그만큼 비워두고 그 안에 맞춰 넣는다.
                        .statusBarsPadding()
                        .padding(bottom = SheetOverlap)
                )
            }
        }
    }
}

// HospitalDetailScreen의 타이틀 줄(이름+즐겨찾기+공유 → 서브타이틀 주소)과 동일한 배치. 카드 배경
// 없이 화면 캔버스 위에 바로 얹혀서, 아래 카드형 정보 섹션들과 시각적으로 구분되는 "헤더" 블록이 된다.
@Composable
private fun PlaceTitleSection(place: Place) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        CategoryBadge(place = place)
        Spacer(modifier = Modifier.height(12.dp))
        // 액션 아이콘을 아래 QuickActionRow로 내리면서 제목이 한 줄 폭을 다 쓴다 — 긴 장소 이름이
        // 아이콘에 밀려 두 줄로 꺾이던 게 줄고, 화면에서 가장 큰 글씨가 확실한 시작점이 된다.
        Text(
            text = place.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = MaterialTheme.typography.headlineSmall.fontSize * 1.25f
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp).padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = place.address, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

/**
 * 저장·공유·전화를 같은 크기의 원형 액션으로 나란히 둔 줄. 각 항목은 아이콘 원(44dp, 터치 타깃
 * 기준 충족) + 아래 작은 라벨로, 아이콘만 있을 때보다 뜻이 분명하다.
 * 길찾기는 여기 넣지 않는다 — 화면의 유일한 길찾기 진입점은 하단 고정 CTA다.
 */
@Composable
private fun QuickActionRow(
    accent: Color,
    isFavorite: Boolean,
    phoneNumber: String?,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onCall: () -> Unit
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickAction(
            icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            label = strings.nearby.favoriteActionLabel,
            contentDescription = if (isFavorite) {
                strings.nearby.favoriteRemoveContentDescription
            } else {
                strings.nearby.favoriteAddContentDescription
            },
            // 즐겨찾기만 앱 브랜드색(코랄)을 유지한다 — 다른 화면의 하트와 같은 색이어야 한다.
            accent = CoralPrimary,
            filled = isFavorite,
            onClick = onToggleFavorite,
            modifier = Modifier.weight(1f)
        )
        QuickAction(
            icon = Icons.Default.Share,
            label = strings.hospitalDetail.actionShare,
            contentDescription = strings.hospitalDetail.actionShare,
            accent = accent,
            onClick = onShare,
            modifier = Modifier.weight(1f)
        )
        // 전화번호가 없는 장소가 많아, 없으면 흐리게 두고 누르지 못하게 한다(누르면 아무 일도
        // 일어나지 않는 버튼을 남겨두지 않는다).
        QuickAction(
            icon = Icons.Default.Call,
            label = strings.hospitalDetail.phoneLabel,
            contentDescription = strings.hospitalDetail.phoneLabel,
            accent = accent,
            enabled = phoneNumber != null,
            onClick = onCall,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    enabled: Boolean = true
) {
    val tint = if (enabled) accent else InactiveIcon
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (filled) tint else tint.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (filled) Color.White else tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (enabled) TextSecondary else InactiveIcon,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// HospitalDetailScreen의 BasicInfoRow(운영시간/전화/홈페이지/언어)와 같은 구성 — 흰 테두리 원
// 안의 아이콘 + 고정폭 라벨 + 값. 두 상세 화면의 "기본정보" 카드가 같은 표 형태로 보이게 한다.
// [onClick]이 있으면(전화번호가 있는 "전화" 행) 행 전체가 탭 가능해진다 — 예전엔 이 값을 보여주는
// 행과, 탭하면 전화를 거는 별도 아이콘 버튼(ActionButtonsRow)이 따로 있었는데 하나로 합친 것이다.
@Composable
private fun BasicInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 흰 원+회색 테두리에 담긴 래스터 아이콘이었는데, 카테고리 색을 못 받아 화면 톤과 겉돌았다
        // — 종류 색을 옅게 깐 원 안의 벡터 아이콘으로 바꿔 배지·섹션 아이콘과 같은 계열로 맞춘다.
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        // 라벨을 값 옆 고정폭 칸이 아니라 값 위 작은 글씨로 올린다 — 값이 길어도 줄이 안 밀리고,
        // 읽는 순서(무엇 → 얼마)가 자연스럽다.
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = if (onClick != null) accent else TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun RecoveryNoticeSection(place: Place, accent: Color = CoralPrimary) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = MaterialTheme.shapes.large,
        // 배경도 장소 종류 색의 옅은 톤으로 — 코랄 고정이면 파란 관광 배지 바로 아래에서 색이 튀었다.
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
    ) {
        // 왼쪽에 카테고리 색 레일을 세워 "본문 카드"가 아니라 인용/안내 블록으로 읽히게 한다.
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .padding(vertical = 14.dp)
                    .padding(start = 14.dp)
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(percent = 50))
                    .background(accent)
            )
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // 제목 앞에 작은 원형 아이콘 배지를 달아, 아래 InfoSection들의 "타이틀+아이콘" 톤과
            // 일관되게 맞춘다 — 텍스트만 있던 이전보다 이 카드가 "경고문 한 줄"이 아니라 안내
            // 섹션으로 자연스럽게 읽힌다.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = strings.nearby.recoveryCheckTitle, style = CardTitleStyle, color = TextPrimary)
            }
            // 본문이 회색(TextSecondary) + 작은 글씨라 흐렸다 — 정작 읽어야 할 안내라서 본문 색과
            // 크기를 올리고, 면책 문구만 작고 옅게 남겨 둘의 위계를 분명히 한다(예전엔 파란 면책
            // 문구가 회색 본문보다 더 눈에 띄었다).
            Text(
                text = place.type.translatedRecoveryHint(strings.language),
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Text(
                text = strings.nearby.recoveryDisclaimer,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            }
        }
    }
}

// 카드 상단에 카테고리를 색 텍스트 한 줄로만 보여주던 것을, 아이콘+옅은 배경의 배지(pill)로
// 바꾼다 — 화면 전체의 액센트(place.type.tint)를 그대로 쓰는 자리라, 바로 위 히어로의
// 배경 그라데이션과 톤이 이어진다.
@Composable
private fun CategoryBadge(place: Place, modifier: Modifier = Modifier) {
    val language = LocalAppStrings.current.language
    // 아이콘·색을 지도 목록의 종류 칩과 같은 표에서 받는다 — 같은 장소가 목록에선 청록 쇼핑백,
    // 상세에선 파란 카메라로 보이던 문제를 없앤다(core/ui/PlaceKindVisuals.kt).
    val visual = placeKindVisual(place.type, place.category)
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(visual.color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            // 지도 목록(MapScreen)의 종류 칩과 같은 규칙 — 세부 분류를 알면 그걸(백화점/전통시장/
            // 면세점), 모르면 장소 종류로 되돌아간다. 같은 장소가 목록과 상세에서 다른 이름으로
            // 불리지 않게 두 곳이 같은 순서를 쓴다.
            text = place.category.translatedLabel(language)
                .ifBlank { place.type.translatedLabel(language) },
            style = MaterialTheme.typography.labelMedium,
            color = visual.color,
            fontWeight = FontWeight.Bold
        )
    }
}

// 화면 안에 길찾기 진입점을 하나(BottomActionBar)로 정리하면서, 이 미니맵은 지도를 눌러도 아무
// 일도 일어나지 않는 순수 미리보기가 됐다 — 탭 가능한 것처럼 보이는 배지/클릭 리스너를 더 이상
// 달지 않는다(잘못된 기대를 주지 않기 위해서다).
@Composable
private fun LocationMiniMap(place: Place) {
    val lat = place.latitude
    val lng = place.longitude
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(MaterialTheme.shapes.large)
    ) {
        if (lat != null && lng != null) {
            KakaoMapView(
                pins = listOf(
                    MapPin(id = place.id, latitude = lat, longitude = lng, type = place.type.toMapPinType(), selected = true)
                ),
                modifier = Modifier.fillMaxSize(),
                // 탭해도 아무 동작이 없는(길찾기는 하단 고정 CTA 하나로만 연결) 순수 미리보기라,
                // 팬/핀치 등 카메라 제스처까지 살아있으면 실수로 지도를 옮길 수 있다 — 꺼둔다(코드리뷰 지적).
                interactive = false
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFE9E9EE)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = LocalAppStrings.current.hospitalDetail.noLocationInfo, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

// 즐겨찾기는 PlaceTitleSection에 이미 있어(HospitalDetailScreen과 같은 자리) 여기서는 중복으로
// 넣지 않는다 — 화면 전체에서 유일한 길찾기 진입점인 이 버튼 하나에 폭 전체를 준다.
@Composable
private fun BottomActionBar(
    onDirectionsClick: () -> Unit,
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
                // 흰 바 위에 흰 카드가 스크롤돼 지나가면 경계가 안 보여 콘텐츠가 잘린 것처럼 보였다
                // — 얇은 구분선 한 줄로 "여기서부터 고정 영역"임을 분명히 한다.
                .drawBehind {
                    drawLine(
                        color = DividerColor,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = onDirectionsClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
            ) {
                // 코랄 배경 위라 원본(코랄) 그대로 두면 안 보인다 — 흰색으로 tint해서 올린다.
                Image(
                    painter = painterResource(id = R.drawable.hospital_detail_findmap),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = LocalAppStrings.current.hospitalDetail.directionsButton,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 웰니스 프로그램다운 톤을 위해 각 정보 구획을 옅은 캔버스 배경 위에 뜬 흰 카드로 감싼다(Soft UI:
// 은은한 그림자 + 넉넉한 라운드 코너) — HospitalDetailScreen의 SectionCard/InfoSection과 같은 패턴.
// feature 패키지는 서로 직접 import하지 않는다는 규칙(CLAUDE.md §4)에 따라 각 화면이 자기 파일 안에
// private로 따로 둔다.
// title 앞에 작은 아이콘을 얹을 수 있게 열어둔다 — "소개"/"위치" 섹션이 텍스트만 있을 때보다
// 눈에 더 잘 띄고, RecoveryNoticeSection의 아이콘+타이틀 톤과도 화면 전체에서 통일된다.
@Composable
private fun InfoSection(
    title: String,
    icon: ImageVector? = null,
    // 섹션 아이콘 색. 기본값(코랄) 대신 장소 종류 색을 넘겨 지도 마커 색과 맞춘다.
    accent: Color = CoralPrimary,
    content: @Composable ColumnScope.() -> Unit
) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
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
            .padding(20.dp),
        content = content
    )
}

private fun Context.launchDirections(place: Place) {
    launchExternalDirections(
        latitude = place.latitude,
        longitude = place.longitude,
        label = place.name,
        fallbackAddress = place.address
    )
}

private fun Context.dialPhone(phoneNumber: String?) {
    if (phoneNumber.isNullOrBlank()) return
    launchIntentSafely(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
}

private fun Context.sharePlace(place: Place) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "${place.name}\n${place.address}")
    }
    launchIntentSafely(Intent.createChooser(intent, place.name))
}

// PlaceType.label/recoveryHint의 언어별 문구는 core/i18n/PlaceTypeStrings.kt(translatedLabel/
// translatedRecoveryHint)로 옮겼다 — 여기 있던 한글 하드코딩 버전은 삭제한다.

/**
 * 이 화면의 액센트 색. 지도 마커(core/ui/KakaoMapView.kt의 map_*maker.webp)와 같은 값을 써서,
 * 지도에서 파란 핀을 눌러 들어온 화면이 갑자기 코랄 일색으로 바뀌지 않게 한다 — 클러스터 배지도
 * 같은 색 체계다. 지도는 장소를 딱 두 종류로만 나누므로(RESTAURANT=음식, 나머지=관광,
 * [toMapPinType] 참고) 여기서도 같은 기준으로 두 색만 쓴다.
 */
private val PlaceType.tint: Color
    get() = if (this == PlaceType.RESTAURANT) FoodPinColor else TouristPinColor

private val TouristPinColor = Color(0xFF326BF6)
private val FoodPinColor = Color(0xFFFAA85C)

// 히어로 사진 높이. 상단바가 서서히 나타나는 구간(이 높이의 절반)을 계산하는 데도 쓴다.
// 화면 폭을 꽉 채우게 되면서 예전(240dp, 좌우 여백 있는 카드)보다 키워 몰입감을 준다.
private val HeroHeight = 300.dp


// 콘텐츠 시트가 히어로 사진 위로 겹쳐 올라오는 양.
private val SheetOverlap = 28.dp

/**
 * 사진이 없을 때 히어로에 세우는 마스코트(상세화면 전용 — PlaceHeroSection 참고).
 *
 * 앱 전체가 쓰는 분류 기준을 그대로 따른다: RESTAURANT만 "식사", 나머지(SHOPPING/LODGING/SPA/
 * WALK/OTHER 포함)는 전부 "관광"이다(Place.toMapPin, MapUiState.visiblePlaces와 동일한 기준).
 * 지도 "관광" 탭에 묶여 있던 장소가 상세에 들어가서 갑자기 다른 종류로 보이지 않게 하려는 것이다.
 */
private fun PlaceType.fallbackCharacterImage(): Int =
    if (this == PlaceType.RESTAURANT) R.drawable.travel_character_food else R.drawable.travel_character

private fun PlaceType.icon(): ImageVector = when (this) {
    PlaceType.TOURIST_ATTRACTION -> Icons.Default.PhotoCamera
    PlaceType.RESTAURANT -> Icons.Default.Restaurant
    PlaceType.SHOPPING -> Icons.Default.ShoppingBag
    PlaceType.LODGING -> Icons.Default.Hotel
    PlaceType.SPA -> Icons.Default.Spa
    PlaceType.WALK -> Icons.AutoMirrored.Filled.DirectionsWalk
    PlaceType.OTHER -> Icons.Default.Place
}

private fun PlaceType.toMapPinType(): MapPinType = if (this == PlaceType.RESTAURANT) MapPinType.FOOD else MapPinType.TOURIST

private fun Double.toDistanceLabel(): String =
    if (this < 1000.0) "${toInt()}m" else String.format(Locale.US, "%.1fkm", this / 1000.0)

// place.lastModified는 백엔드(WellnessDtoMapper)가 LocalDate.toString()으로 내려주는 ISO 형식
// ("2023-06-15") 그대로다 — 화면 표기용으로 점(.) 구분자로만 바꾼다. 형식이 예상과 다른 값이
// 오더라도(방어적으로) 원문을 그대로 보여준다.
private fun String.toDisplayDate(): String =
    takeIf { it.length == 10 && it[4] == '-' && it[7] == '-' }
        ?.replace('-', '.')
        ?: this

private val Place.displayDescription: String?
    get() = description?.takeUnless { it.startsWith("http") || it.matches(Regex("EX\\d+")) }

// NearbyScreen(웰니스·관광 목록)의 WellnessCanvas(0xFFFFFAFF)와 같은 값 — 위 주석 참고.
private val PlaceDetailCanvas = Color(0xFFFFFAFF)
