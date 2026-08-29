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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.mediinbusan.app.core.designsystem.MediBlue40
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.i18n.translatedRecoveryHint
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
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

    LaunchedEffect(placeId) {
        viewModel.load(placeId)
    }

    // NearbyScreen(웰니스·관광 목록)의 배경(WellnessCanvas, 0xFFFFFAFF)과 값을 맞춰, 그 화면에서
    // 넘어왔을 때 배경색이 끊겨 보이지 않게 한다(HospitalDetailScreen이 HomeBackgroundPink를
    // 재사용하는 것과 같은 이유 — core/designsystem 공용 토큰이 아니라 화면마다 자기 캔버스색을
    // 갖는 기존 관례를 그대로 따른다).
    Box(modifier = Modifier.fillMaxSize().background(PlaceDetailCanvas)) {
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
    // 하단 고정 액션바 실측 높이를 그대로 스크롤 콘텐츠 하단 여백으로 써서, 콘텐츠가 액션바에
    // 가려지거나 반대로 그 사이에 빈 여백이 남지 않고 정확히 맞닿게 한다(HospitalDetailScreen과 동일).
    var bottomBarHeight by remember { mutableStateOf(0.dp) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = bottomBarHeight)
            ) {
                PlaceHeroSection(place = place)

                Spacer(modifier = Modifier.height(14.dp))
                PlaceTitleSection(
                    place = place,
                    isFavorite = isFavorite,
                    onToggleFavorite = onToggleFavorite,
                    onShare = { context.sharePlace(place) }
                )

                // HospitalDetailScreen의 위치 서브타이틀-소개 카드 사이 여백(42dp)과 같은 비율로,
                // "타이틀 블록"과 "카드형 정보 섹션들"을 시각적으로 크게 구분한다.
                Spacer(modifier = Modifier.height(32.dp))
                // HospitalDetailScreen의 기본정보(BasicInfoRow: 운영시간/전화/홈페이지/언어) 카드와
                // 같은 아이콘 원형+라벨+값 구성 — 전화·거리에 더해, 지금까지 화면 어디에도 없던
                // "정보 갱신일"(place.lastModified, 웰니스 API 원본에 실려 오지만 그동안 안 쓰였다)을
                // 세 번째 행으로 추가한다. 전화 행은 그 자체가 다이얼 액션도 겸해서, 예전에 따로
                // 있던 "전화" 아이콘 버튼(ActionButtonsRow)이 여기 하나로 합쳐진다.
                InfoSection(title = strings.hospitalDetail.basicInfoSectionTitle) {
                    BasicInfoRow(
                        iconRes = R.drawable.hospital_detail_phone,
                        label = strings.hospitalDetail.phoneLabel,
                        value = place.phoneNumber ?: strings.hospitalDetail.infoNotAvailable,
                        onClick = place.phoneNumber?.takeUnless { it.isBlank() }?.let { { context.dialPhone(it) } }
                    )
                    BasicInfoRow(
                        iconRes = R.drawable.hospital_detail_findmap,
                        label = strings.nearby.distanceLabel,
                        value = place.distanceFromHospitalMeters
                            ?.let { strings.nearby.distanceFromHospitalFormat.format(it.toDistanceLabel()) }
                            ?: strings.hospitalDetail.infoNotAvailable
                    )
                    place.lastModified?.let { lastModified ->
                        BasicInfoRow(
                            iconRes = R.drawable.hospital_detail_runtime,
                            label = strings.nearby.lastUpdatedLabel,
                            value = lastModified.toDisplayDate()
                        )
                    }
                }

                place.displayDescription?.let { description ->
                    Spacer(modifier = Modifier.height(14.dp))
                    InfoSection(title = strings.nearby.introSectionTitle, icon = Icons.Default.Info) {
                        Text(text = description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                RecoveryNoticeSection(place = place)

                Spacer(modifier = Modifier.height(14.dp))
                // 이 미니맵은 이제 순수 미리보기다 — 예전엔 지도 자체도 눌리고(외부 길찾기), 그 바로
                // 아래 "길찾기" 버튼도 있고, 화면 맨 아래 고정바에도 같은 "길찾기" 버튼이 있어 한
                // 화면에 길찾기 진입점이 4개(액션 pill/지도/버튼/하단바)였다 — 하단 고정 CTA
                // 하나로 합치고 나머지는 없앤다.
                InfoSection(title = strings.hospitalDetail.locationSectionTitle, icon = Icons.Default.Place) {
                    LocationMiniMap(place = place)
                }
                Spacer(modifier = Modifier.height(14.dp))
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
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = LocalAppStrings.current.common.backContentDescription,
                tint = CoralPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun PlaceHeroSection(place: Place) {
    // HospitalDetailScreen의 ImageCarouselSection과 같은 톤: 위쪽 여백 + 네 모서리를 전부 둥글려
    // 한 장의 사진처럼 떠 보이게 한다. 뒤로가기/공유/즐겨찾기는 더 이상 이 사진 위에 얹지 않는다 —
    // PlaceDetailTopBar(뒤로가기)와 PlaceTitleSection(공유/즐겨찾기)으로 옮겨, HospitalDetailScreen처럼
    // 사진은 순수하게 사진 역할만 하게 한다.
    val language = LocalAppStrings.current.language
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .height(240.dp)
            .clip(RoundedCornerShape(28.dp))
    ) {
        if (place.imageUrl != null) {
            AsyncImageBox(
                model = place.imageUrl,
                contentDescription = place.name,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.72f))
                        )
                    )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = place.type.translatedLabel(language), style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = place.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
            }
        } else {
            // 실제 사진이 없는 장소가 많아(웰니스 API 원문에 이미지가 비어있는 경우) 이 자리표시자가
            // 사실상 기본 히어로가 된다 — 무채색 박스 대신 장소 종류(place.type.tint)에 맞춘 옅은
            // 그라데이션 + 아이콘 배지로, HospitalDetailScreen의 기본 갤러리 폴백과 같은 톤을 쓴다.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(place.type.tint.copy(alpha = 0.22f), Color(0xFFEDEDF2)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                brush = Brush.radialGradient(listOf(Color.White, place.type.tint.copy(alpha = 0.18f))),
                                shape = CircleShape
                            )
                            .border(width = 1.dp, color = place.type.tint.copy(alpha = 0.25f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = place.type.icon(), contentDescription = null, tint = place.type.tint, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = place.type.translatedLabel(language), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}

// HospitalDetailScreen의 타이틀 줄(이름+즐겨찾기+공유 → 서브타이틀 주소)과 동일한 배치. 카드 배경
// 없이 화면 캔버스 위에 바로 얹혀서, 아래 카드형 정보 섹션들과 시각적으로 구분되는 "헤더" 블록이 된다.
@Composable
private fun PlaceTitleSection(
    place: Place,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    val strings = LocalAppStrings.current
    Column {
        CategoryBadge(place = place, modifier = Modifier.padding(start = 20.dp))
        Spacer(modifier = Modifier.height(10.dp))
        // 즐겨찾기/공유 아이콘 줄은 20dp 안쪽 패딩 없이 fillMaxWidth로 화면 진짜 오른쪽 끝까지
        // 채운다 — HospitalDetailScreen의 타이틀 줄과 동일한 폭 기준이다.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 20.dp).weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(12.dp))
            // 사진 위 흰 원형 배경 버튼(RoundIconButton/FavoriteHeartButton) 대신, HospitalDetailScreen의
            // 타이틀 줄과 똑같이 배경 없는 일반 IconButton으로 즐겨찾기/공유를 얹는다 — 이제 캔버스
            // 배경 위라 원형 배경이 없어도 아이콘이 묻히지 않는다.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.offset(x = 12.dp)) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) {
                                strings.nearby.favoriteRemoveContentDescription
                            } else {
                                strings.nearby.favoriteAddContentDescription
                            },
                            tint = CoralPrimary
                        )
                    }
                }
                IconButton(onClick = onShare) {
                    Image(
                        painter = painterResource(id = R.drawable.hospital_detail_share),
                        contentDescription = strings.hospitalDetail.actionShare,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = place.address, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

// HospitalDetailScreen의 BasicInfoRow(운영시간/전화/홈페이지/언어)와 같은 구성 — 흰 테두리 원
// 안의 아이콘 + 고정폭 라벨 + 값. 두 상세 화면의 "기본정보" 카드가 같은 표 형태로 보이게 한다.
// [onClick]이 있으면(전화번호가 있는 "전화" 행) 행 전체가 탭 가능해진다 — 예전엔 이 값을 보여주는
// 행과, 탭하면 전화를 거는 별도 아이콘 버튼(ActionButtonsRow)이 따로 있었는데 하나로 합친 것이다.
@Composable
private fun BasicInfoRow(iconRes: Int, label: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.width(56.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (onClick != null) CoralPrimary else TextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RecoveryNoticeSection(place: Place) {
    val strings = LocalAppStrings.current
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = MaterialTheme.shapes.large,
        color = CoralPrimaryContainer,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // 제목 앞에 작은 원형 아이콘 배지를 달아, 아래 InfoSection들의 "타이틀+아이콘" 톤과
            // 일관되게 맞춘다 — 텍스트만 있던 이전보다 이 카드가 "경고문 한 줄"이 아니라 안내
            // 섹션으로 자연스럽게 읽힌다.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = CoralPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = strings.nearby.recoveryCheckTitle, style = CardTitleStyle, color = TextPrimary)
            }
            Text(
                text = place.type.translatedRecoveryHint(strings.language),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                text = strings.nearby.recoveryDisclaimer,
                style = MaterialTheme.typography.labelSmall,
                color = MediBlue40
            )
        }
    }
}

// 카드 상단에 카테고리를 색 텍스트 한 줄로만 보여주던 것을, 아이콘+옅은 배경의 배지(pill)로
// 바꾼다 — PlaceHeroSection의 이미지 없는 폴백이 이미 이 색(place.type.tint)의 아이콘 배지를
// 쓰고 있어 톤을 그대로 이어받는다.
@Composable
private fun CategoryBadge(place: Place, modifier: Modifier = Modifier) {
    val language = LocalAppStrings.current.language
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(place.type.tint.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = place.type.icon(),
            contentDescription = null,
            tint = place.type.tint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = place.type.translatedLabel(language),
            style = MaterialTheme.typography.labelMedium,
            color = place.type.tint,
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
private fun InfoSection(title: String, icon: ImageVector? = null, content: @Composable ColumnScope.() -> Unit) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(20.dp))
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

private val PlaceType.tint: Color
    get() = when (this) {
        PlaceType.TOURIST_ATTRACTION -> Color(0xFF2F6690)
        PlaceType.RESTAURANT -> Color(0xFF3A7D7B)
        PlaceType.SHOPPING -> Color(0xFF9A5C7F)
        PlaceType.LODGING -> Color(0xFF6A6F4C)
        PlaceType.SPA -> Color(0xFF7C6A9B)
        PlaceType.WALK -> Color(0xFF4F8A5B)
        PlaceType.OTHER -> Color(0xFF667085)
    }

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
