package com.mediinbusan.app.feature.nearby

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.common.careProfile
import com.mediinbusan.app.core.common.resolveBusanHighlight
import com.mediinbusan.app.core.designsystem.InactiveIcon
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.designsystem.TourismAccentPink
import com.mediinbusan.app.core.designsystem.TourismAccentPinkContainer
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.translatedCopy
import com.mediinbusan.app.core.i18n.translatedLabel
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.AtAGlanceRow
import com.mediinbusan.app.core.ui.CautionList
import com.mediinbusan.app.core.ui.CenteredTopAppBar
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.DetailPullDismissBox
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.KakaoMapView
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.core.ui.MapPin
import com.mediinbusan.app.core.ui.MapPinType
import com.mediinbusan.app.core.ui.PlaceKindVisual
import com.mediinbusan.app.core.ui.placeKindVisual
import com.mediinbusan.app.core.ui.MediTipContent
import com.mediinbusan.app.core.ui.NearbyPlacesSection
import com.mediinbusan.app.core.ui.TravelerHelpContent
import com.mediinbusan.app.core.ui.VisitInfo
import com.mediinbusan.app.core.ui.VisitInfoContent
import com.mediinbusan.app.core.ui.launchExternalDirections
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.core.ui.rememberFavoriteTogglePop
import com.mediinbusan.app.data.place.Place
import com.mediinbusan.app.data.place.PlaceType
import java.util.Locale

@Composable
fun PlaceDetailScreen(
    placeId: String,
    onSelectPlace: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val language = LocalAppStrings.current.language

    LaunchedEffect(placeId, language) {
        viewModel.load(placeId)
    }

    // HospitalDetailScreen과 같은 이유 — 지도에서 카드를 끌어올려 들어온 화면을 아래로 끌어
    // 되돌린다(core/ui/DetailPullTransition.kt). 탑바가 고정 영역으로 새로 생겼어도, 스크롤 가능한
    // 본문이 여전히 이 Box의 자손이라 nestedScroll 제스처는 그대로 동작한다.
    DetailPullDismissBox(
        onDismiss = onBack,
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        val place = uiState.place
        // 관광 카탈로그 상세(TourismCatalogItemDetailScreen)와 같은 탑바를 쓴다 — 제목은 그 화면의
        // 카테고리 라벨 자리에 맞춰 이 장소의 종류 라벨을 넣는다(CategoryBadge와 같은 문구 규칙).
        val topBarTitle = place?.let { it.category.translatedLabel(language).ifBlank { it.type.translatedLabel(language) } }.orEmpty()
        Column(modifier = Modifier.fillMaxSize()) {
            CenteredTopAppBar(title = topBarTitle, onBack = onBack)
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val errorMessage = uiState.errorMessage
                when {
                    uiState.isLoading -> LoadingState()
                    errorMessage != null -> ErrorState(message = errorMessage, onRetry = { viewModel.load(placeId) })
                    place != null -> PlaceDetailContent(
                        place = place,
                        isFavorite = uiState.isFavorite,
                        nearbyPlaces = uiState.nearbySamePlaces,
                        onSelectPlace = onSelectPlace,
                        onToggleFavorite = viewModel::onToggleFavorite
                    )
                    else -> EmptyState(message = LocalAppStrings.current.nearby.placeNotFoundMessage)
                }
            }
        }
    }
}

@Composable
private fun PlaceDetailContent(
    place: Place,
    isFavorite: Boolean,
    nearbyPlaces: List<Place>,
    onSelectPlace: (String) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    // 장소 종류 색. 예전엔 이 색이 화면 본문 전체(전화 글자·액션 아이콘·안내 카드 배경까지)를
    // 물들였는데, 배지는 7색 표(쇼핑=청록 등)를 쓰고 본문은 별도의 2색 표(관광=파랑/음식=주황)를
    // 써서 한 화면에 계열이 두 개 겹쳤고, 거기에 코랄 CTA까지 더해 색이 셋이었다. 이제 종류 색은
    // **"이게 무슨 장소인가"를 말하는 자리에만** 남는다 — 카테고리 배지, 사진이 없을 때의 히어로
    // 배경, 지도 핀. 그 셋을 뺀 나머지(아이콘·본문·누를 수 있는 것)는 관광 상세와 같은 포인트
    // 컬러(TourismAccentPink)와 중립색이다.
    val visual = remember(place.type, place.category) { placeKindVisual(place.type, place.category) }
    // 어떤 장소가 와도 항상 채워지는 유형별 케어 프로필과, 부산 대표 명소일 때만 붙는 큐레이션 문구.
    // 웰니스 API가 전화·소개를 비워 내려주면 이 화면이 "사진+이름+지도"만 남던 문제를 이 둘로 메운다.
    val careProfile = remember(place.type) { place.type.careProfile }
    val highlightCopy = remember(place.name, place.type, strings.language) {
        resolveBusanHighlight(place.name, place.type)?.translatedCopy(strings.language)
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        PlaceHero(place = place, kindColor = visual.color)

        Spacer(modifier = Modifier.height(SectionSpacing))
        PlaceSummarySection(
            place = place,
            visual = visual,
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite,
            onShare = { context.sharePlace(place) },
            onCall = { place.phoneNumber?.let { context.dialPhone(it) } }
        )

        Spacer(modifier = Modifier.height(SectionSpacing))
        // 방문 시기·활동 강도·환경·권장 체류. 유형만으로 결정되므로 값이 비는 일이 없다 — 아래
        // 기본정보/소개 카드가 통째로 사라지는 장소에서도 이 줄은 항상 남는다. 카드 배경·제목
        // 없이 캔버스 위에 바로 얹는다(관광 상세와 같은 톤 — AtAGlanceRow 자체가 pill·출처 각주를
        // 이미 갖고 있다).
        AtAGlanceRow(profile = careProfile, modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(modifier = Modifier.height(SectionSpacing))
        // 지도부터 주변 장소까지는 관광 상세(TourismCatalogItemDetailScreen)와 똑같이 한 Column
        // 안에서 같은 간격(TightSectionGap)을 쓴다 — 구간마다 다른 임시 여백을 더하면 조금씩
        // 다른 값이 되어 "화면마다 리듬이 다르다"는 인상을 준다.
        Column(verticalArrangement = Arrangement.spacedBy(TightSectionGap)) {
            val lat = place.latitude
            val lng = place.longitude
            if (lat != null && lng != null) {
                // 길찾기 버튼을 지도 카드 안(우하단)에 겹쳐 넣는다 — 관광 상세와 같은 자리·같은
                // 모양(원형 FAB)이다. 예전엔 이 미니맵 아래에 또 "길찾기" 버튼이 있고, 화면 맨
                // 아래 고정바에도 같은 버튼이 있어 진입점이 여러 개였다 — 이 FAB 하나로 합친다.
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
                    KakaoMapView(
                        pins = listOf(
                            MapPin(id = place.id, latitude = lat, longitude = lng, type = place.type.toMapPinType(), selected = true)
                        ),
                        modifier = Modifier.fillMaxSize(),
                        // 탭해도 아무 동작이 없는(길찾기는 FAB 하나로만 연결) 순수 미리보기라,
                        // 팬/핀치 등 카메라 제스처까지 살아있으면 실수로 지도를 옮길 수 있다.
                        interactive = false
                    )
                    FloatingActionButton(
                        onClick = { context.launchDirections(place) },
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
            } else {
                // 좌표가 없는 장소(드묾) — 지도 카드 자체를 그리지 않고, 주소 기준 길찾기 버튼
                // 하나만 남긴다. 이게 유일한 진입점이라 항상 있어야 한다.
                PlainSection(title = strings.hospitalDetail.locationSectionTitle) {
                    DirectionsButton(onClick = { context.launchDirections(place) })
                }
            }

            // HospitalDetailScreen의 기본정보(BasicInfoRow: 운영시간/전화/홈페이지/언어) 카드와
            // 같은 아이콘 원형+라벨+값 구성 — 전화·거리에 더해 "정보 갱신일"을 세 번째 행으로
            // 추가한다. 값이 없는 행은 아예 그리지 않는다.
            val phoneNumber = place.phoneNumber?.takeUnless { it.isBlank() }
            val distanceText = place.distanceFromHospitalMeters
                ?.let { strings.nearby.distanceFromHospitalFormat.format(it.toDistanceLabel()) }
            val lastUpdated = place.lastModified?.takeUnless { it.isBlank() }?.toDisplayDate()
            if (phoneNumber != null || distanceText != null || lastUpdated != null) {
                PlainSection(title = strings.hospitalDetail.basicInfoSectionTitle) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        phoneNumber?.let {
                            BasicInfoRow(
                                icon = Icons.Default.Call,
                                label = strings.hospitalDetail.phoneLabel,
                                value = it,
                                onClick = { context.dialPhone(it) }
                            )
                        }
                        distanceText?.let {
                            BasicInfoRow(
                                icon = Icons.Default.NearMe,
                                label = strings.nearby.distanceLabel,
                                value = it
                            )
                        }
                        lastUpdated?.let {
                            BasicInfoRow(
                                icon = Icons.Default.Update,
                                label = strings.nearby.lastUpdatedLabel,
                                value = it
                            )
                        }
                    }
                }
            }

            // 영업시간·휴무일·대표메뉴·이용요금·주차·홈페이지.
            val visitInfo = remember(place) {
                VisitInfo(
                    businessHours = place.businessHours,
                    restDate = place.restDate,
                    signatureMenu = place.signatureMenu,
                    usageFee = place.usageFee,
                    parkingInfo = place.parkingInfo,
                    homepageUrl = place.homepageUrl
                )
            }
            if (!visitInfo.isEmpty) {
                PlainSection(title = strings.placeCuration.visitInfoTitle) {
                    VisitInfoContent(visitInfo = visitInfo, onOpenHomepage = { url -> context.openWebPage(url) })
                }
            }

            // 소개는 이제 사라지지 않는다 — 원문(displayDescription)이 없으면 부산 명소 큐레이션
            // 한 줄로, 그것도 없으면 유형별 기본 소개문으로 내려간다.
            val introText = place.displayDescription?.takeUnless { it.isBlank() }
                ?: highlightCopy?.tagline
                ?: strings.placeCuration.typeIntroFallbacks[place.type.name]
            introText?.let { description ->
                PlainSection(title = strings.nearby.introSectionTitle) {
                    Text(text = description, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
            }

            // 부산 대표 명소로 매칭됐을 때만 붙는 카드.
            highlightCopy?.let { copy ->
                PlainSection(title = strings.placeCuration.mediTipTitle) {
                    MediTipContent(copy = copy)
                }
            }

            PlainSection(title = strings.nearby.recoveryCheckTitle) {
                CautionList(cautions = place.type.careProfile.cautions)
                // 면책 문구는 반드시 남긴다 — 위 안내는 의료 자문이 아니다.
                Text(
                    text = strings.nearby.recoveryDisclaimer,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // 장소와 무관하게 항상 같은 공공 안내(1330·119·결제/교통). 앱이 상담이나 통역사를
            // 연결하는 게 아니라 공개된 번호를 안내만 한다(CLAUDE.md §1 MVP 하드 제약).
            PlainSection(title = strings.placeCuration.travelerHelpTitle) {
                TravelerHelpContent(onDial = { context.dialPhone(it) })
            }

            // 병원 상세(S-05)의 "주변 같은 진료과목 병원"과 같은 자리·같은 구성이다.
            if (nearbyPlaces.isNotEmpty()) {
                NearbyPlacesSection(
                    anchorType = place.type,
                    places = nearbyPlaces,
                    onSelectPlace = onSelectPlace
                )
            }

            // 위 카드들 중 실제로 한국관광공사 TourAPI에서 온 항목이 어디까지인지 밝힌다.
            Text(
                text = strings.placeCuration.officialDataCreditLabel,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// 뒤로가기는 이제 화면 상단 탑바(CenteredTopAppBar)에 있다 — 이 히어로는 순수 사진이다. 좌우
// 여백·둥근 모서리 없이 화면 폭을 꽉 채우는 풀블리드로, 관광 카탈로그 상세(TourismHero)와
// 같은 톤이다.
@Composable
private fun PlaceHero(place: Place, kindColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeroHeight)
    ) {
        if (place.imageUrl != null) {
            AsyncImageBox(
                model = place.imageUrl,
                contentDescription = place.name,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // 실제 사진이 없는 장소가 많아(웰니스 API 원문에 이미지가 비어있는 경우) 이
            // 자리표시자가 사실상 기본 히어로가 된다 — 종류에 맞는 마스코트 일러스트를 세우고,
            // 배경은 장소 종류 색(kindColor)에 맞춘 옅은 그라데이션을 깔아 캐릭터가 얹힐 바닥을
            // 만든다.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(kindColor.copy(alpha = 0.22f), Color(0xFFEDEDF2)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = place.type.fallbackCharacterImage()),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private val HeroHeight = 260.dp

// guide_tourism_place_detail.png 기준 — 카드 배경 없이 캔버스 위에 바로 얹는다. 카테고리 배지 +
// 이름 + 주소 + 즐겨찾기/공유/전화 액션 줄을 한데 묶는다(관광 상세엔 없는, 이 화면만의 액션들).
@Composable
private fun PlaceSummarySection(
    place: Place,
    visual: PlaceKindVisual,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onCall: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            CategoryBadge(place = place, visual = visual)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
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
        Spacer(modifier = Modifier.height(2.dp))
        QuickActionRow(
            isFavorite = isFavorite,
            phoneNumber = place.phoneNumber?.takeUnless { it.isBlank() },
            onToggleFavorite = onToggleFavorite,
            onShare = onShare,
            onCall = onCall
        )
    }
}

/**
 * 저장·공유·전화를 같은 크기의 원형 액션으로 나란히 둔 줄. 세 항목 모두 관광 상세와 같은
 * 포인트 컬러(TourismAccentPink)다 — 누를 수 있는 것은 화면 어디서나 같은 색이다.
 * 길찾기는 여기 넣지 않는다 — 화면의 유일한 길찾기 진입점은 지도 카드 위 FAB다.
 */
@Composable
private fun QuickActionRow(
    isFavorite: Boolean,
    phoneNumber: String?,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onCall: () -> Unit
) {
    val strings = LocalAppStrings.current
    // 하트만 누른 순간 팝이 들어간다 — 공유·전화는 화면이 바뀌거나 다이얼러가 떠서 피드백이
    // 저절로 생기지만, 즐겨찾기는 아이콘이 바뀌는 것 말고 아무 일도 일어나지 않는다.
    val favoritePop = rememberFavoriteTogglePop(isFavorite = isFavorite, onToggle = onToggleFavorite)
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
            filled = isFavorite,
            onClick = favoritePop.onClick,
            modifier = Modifier.weight(1f),
            iconModifier = favoritePop.scaleModifier
        )
        QuickAction(
            icon = Icons.Default.Share,
            label = strings.hospitalDetail.actionShare,
            contentDescription = strings.hospitalDetail.actionShare,
            onClick = onShare,
            modifier = Modifier.weight(1f)
        )
        // 전화번호가 없는 장소가 많아, 없으면 흐리게 두고 누르지 못하게 한다.
        QuickAction(
            icon = Icons.Default.Call,
            label = strings.hospitalDetail.phoneLabel,
            contentDescription = strings.hospitalDetail.phoneLabel,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    enabled: Boolean = true,
    // 아이콘에만 거는 추가 Modifier(즐겨찾기 팝 스케일). 원 배경까지 같이 커지면 옆 버튼을
    // 밀어내는 것처럼 보여서 아이콘 층에만 건다.
    iconModifier: Modifier = Modifier
) {
    val tint = if (enabled) TourismAccentPink else InactiveIcon
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
                modifier = Modifier.size(20.dp).then(iconModifier)
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

// HospitalDetailScreen의 BasicInfoRow(운영시간/전화/홈페이지/언어)와 같은 구성 — 원 안의 아이콘 +
// 고정폭 라벨 + 값. [onClick]이 있으면(전화번호가 있는 "전화" 행) 행 전체가 탭 가능해진다.
@Composable
private fun BasicInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
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
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(TourismAccentPinkContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = TourismAccentPink, modifier = Modifier.size(18.dp))
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
                color = if (onClick != null) TourismAccentPink else TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// 카드 상단에 카테고리를 아이콘+옅은 배경의 배지(pill)로 보여준다. 아이콘·색은 지도 목록의 종류
// 칩과 같은 표에서 받는다(core/ui/PlaceKindVisuals.kt) — 이 자리는 장소 종류(관광/음식/쇼핑 등)를
// 밝히는 자리라 관광 상세의 포인트 컬러가 아니라 종류별 색을 그대로 쓴다.
@Composable
private fun CategoryBadge(place: Place, visual: PlaceKindVisual, modifier: Modifier = Modifier) {
    val language = LocalAppStrings.current.language
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
            tint = visual.ink,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = place.category.translatedLabel(language)
                .ifBlank { place.type.translatedLabel(language) },
            style = MaterialTheme.typography.labelMedium,
            color = visual.ink,
            fontWeight = FontWeight.Bold
        )
    }
}

/** 좌표가 없는 장소를 위한 유일한 길찾기 진입점(주소 기준). */
@Composable
private fun DirectionsButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(containerColor = TourismAccentPink, contentColor = Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.hospital_detail_findmap),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(LocalAppStrings.current.hospitalDetail.directionsButton)
    }
}

// guide_tourism_place_detail.png 기준 — 흰 카드 없이 굵은 제목 한 줄 + 본문만 캔버스 위에 바로
// 얹는 섹션 껍데기. 관광 카탈로그 상세(TourismCatalogItemDetailScreen.PlainSection)와 같은 톤이다.
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

private fun Context.launchDirections(place: Place) {
    launchExternalDirections(
        latitude = place.latitude,
        longitude = place.longitude,
        label = place.name,
        fallbackAddress = place.address
    )
}

// 홈페이지 값은 백엔드가 앵커 태그(<a href="...">)에서 URL만 뽑아 내려준다 — 앱은 그대로 연다.
// 열 수 있는 앱이 없을 때 크래시하지 않는 건 launchIntentSafely가 처리한다(전화·공유와 같은 규칙).
private fun Context.openWebPage(url: String) {
    if (url.isBlank()) return
    launchIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
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

// 섹션 사이 세로 여백. 병원 상세(feature/hospitaldetail)·관광 카탈로그 상세
// (feature/tourism)의 같은 이름 상수와 값을 맞춰, 세 상세 화면의 리듬을 통일한다.
private val SectionSpacing = 20.dp

// 지도~주변 장소 구간 전용 간격 — 관광 카탈로그 상세(TourismCatalogItemDetailScreen.TightSectionGap)
// 와 같은 값이다.
private val TightSectionGap = 22.dp

/**
 * 사진이 없을 때 히어로에 세우는 마스코트.
 *
 * 앱 전체가 쓰는 분류 기준을 그대로 따른다: RESTAURANT만 "식사", 나머지(SHOPPING/LODGING/SPA/
 * WALK/OTHER 포함)는 전부 "관광"이다(Place.toMapPin, MapUiState.visiblePlaces와 동일한 기준).
 */
private fun PlaceType.fallbackCharacterImage(): Int =
    if (this == PlaceType.RESTAURANT) R.drawable.travel_character_food else R.drawable.travel_character

private fun PlaceType.toMapPinType(): MapPinType = if (this == PlaceType.RESTAURANT) MapPinType.FOOD else MapPinType.TOURIST

private fun Double.toDistanceLabel(): String =
    if (this < 1000.0) "${toInt()}m" else String.format(Locale.US, "%.1fkm", this / 1000.0)

// place.lastModified는 백엔드(WellnessDtoMapper)가 LocalDate.toString()으로 내려주는 ISO 형식
// ("2023-06-15") 그대로다 — 화면 표기용으로 점(.) 구분자로만 바꾼다.
private fun String.toDisplayDate(): String =
    takeIf { it.length == 10 && it[4] == '-' && it[7] == '-' }
        ?.replace('-', '.')
        ?: this

private val Place.displayDescription: String?
    get() = description?.takeUnless { it.startsWith("http") || it.matches(Regex("EX\\d+")) }
