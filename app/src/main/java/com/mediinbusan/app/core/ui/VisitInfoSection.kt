package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.designsystem.BadgeText
import com.mediinbusan.app.core.designsystem.CoralInk
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * 상세 화면 "방문 정보" 카드의 내용 — 웰니스 장소 상세(feature/nearby)와 부산 관광 카탈로그 상세
 * (feature/tourism)가 함께 쓴다.
 *
 * feature끼리는 서로 import할 수 없으므로(CLAUDE.md §4) 여기 core/ui에 두고, PlaceCurationSections와
 * 같은 규칙으로 **카드 껍데기는 각 화면이 씌우고 여기서는 안에 들어갈 내용만** 제공한다.
 *
 * ## 이 레이아웃을 결정한 데이터 사정
 *
 * 값은 TourAPI detailIntro2와 부산맛집정보에서 오는데, 세 가지가 형태를 정했다:
 *
 * 1. **여섯 칸이 다 차는 일이 거의 없다.** 유형마다 주는 칸이 다르고(관광지엔 대표메뉴가, 음식점엔
 *    이용요금이 없다), 상세 조회가 일일 API 한도 때문에 매 수집마다 일부 장소에만 돌아가서 같은
 *    목록 안에 6칸짜리와 0칸짜리가 섞인다. 그래서 고정된 표가 아니라 **있는 것만 쌓는 구조**다.
 * 2. **값이 자유 텍스트다.** "매일 09:00~21:00" 한 줄부터 줄바꿈 섞인 여러 문단까지 온다.
 * 3. **값이 한국어 원문이다.** 이름·주소·소개와 달리 언어별 API를 따로 부르지 않는다(호출량이 3배).
 *    라벨만 번역돼 있으면 "번역이 덜 됐나"로 읽히므로 KO가 아닐 때 각주를 붙인다.
 *
 * ## 하지 않기로 한 것: 영업 중 / 영업 종료 배지
 *
 * 운영시간이 자유 텍스트라("상시 개방", "09:00~18:00(동절기 17:00까지)", "평일만 운영") 여는 시각을
 * 신뢰성 있게 뽑을 수 없다. 잘못 파싱해 "영업 종료"라고 띄우면 가게 앞에 선 여행자를 돌려보내게
 * 되는데, 그건 정보를 아예 안 주느니만 못하다. 원문을 그대로 보여주고 판단은 사용자에게 맡긴다
 * (StatusOpenGreen/StatusClosedGray 토큰이 이 용도로 쓰이지 않는 이유다).
 */
@Immutable
data class VisitInfo(
    val businessHours: String? = null,
    val restDate: String? = null,
    val signatureMenu: String? = null,
    val usageFee: String? = null,
    val parkingInfo: String? = null,
    val homepageUrl: String? = null
) {
    val isEmpty: Boolean
        get() = listOf(businessHours, restDate, signatureMenu, usageFee, parkingInfo, homepageUrl)
            .all { it.isNullOrBlank() }

    companion object {
        /**
         * 관광 카탈로그 상세용 — 백엔드가 details 맵에 실어 보내는 키에서 만든다.
         * 키 이름은 웰니스 장소 응답(WellnessPlaceResponse)의 필드명과 일부러 똑같이 맞춰져 있다.
         */
        fun fromDetails(details: Map<String, String>): VisitInfo = VisitInfo(
            businessHours = details["businessHours"],
            restDate = details["restDate"],
            signatureMenu = details["signatureMenu"],
            usageFee = details["usageFee"],
            parkingInfo = details["parkingInfo"],
            homepageUrl = details["homepage"]
        )

        /** fromDetails가 소비하는 키 — 호출 화면이 "남은 항목"만 따로 그릴 때 쓴다. */
        val DetailKeys = setOf(
            "businessHours", "restDate", "signatureMenu", "usageFee", "parkingInfo", "homepage"
        )
    }
}

@Composable
fun VisitInfoContent(
    visitInfo: VisitInfo,
    onOpenHomepage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val curation = strings.placeCuration
    val businessHours = visitInfo.businessHours?.takeUnless { it.isBlank() }
    val restDate = visitInfo.restDate?.takeUnless { it.isBlank() }
    val signatureMenu = visitInfo.signatureMenu?.takeUnless { it.isBlank() }
    val usageFee = visitInfo.usageFee?.takeUnless { it.isBlank() }
    val parkingInfo = visitInfo.parkingInfo?.takeUnless { it.isBlank() }
    val homepageUrl = visitInfo.homepageUrl?.takeUnless { it.isBlank() }

    Column(modifier = modifier.fillMaxWidth()) {
        var rowsDrawn = 0

        // 운영시간과 휴무일은 둘 다 "언제 여는가"라서 한 덩어리로 묶는다 — 나란한 두 행으로 두면
        // 여섯 칸 중 둘을 차지하면서도 읽는 사람은 결국 둘을 합쳐서 이해해야 한다. 운영시간 없이
        // 휴무일만 오는 장소도 있어서, 그때는 휴무일이 제 행을 갖는다.
        if (businessHours != null) {
            VisitInfoRow(
                icon = Icons.Default.Schedule,
                label = strings.hospitalDetail.openingHoursLabel,
                showDivider = false
            ) {
                ExpandableValue(text = businessHours)
                if (restDate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = curation.restDateLabel + " · " + restDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            }
            rowsDrawn++
        } else if (restDate != null) {
            VisitInfoRow(icon = Icons.Default.EventBusy, label = curation.restDateLabel, showDivider = false) {
                ExpandableValue(text = restDate)
            }
            rowsDrawn++
        }

        if (signatureMenu != null) {
            VisitInfoRow(
                icon = Icons.Default.RestaurantMenu,
                label = curation.signatureMenuLabel,
                showDivider = rowsDrawn > 0
            ) {
                // 메뉴명 나열이면 칩으로 흩어놓는다 — 이 카드에서 가장 눈에 걸려야 하는 정보인데
                // "돼지국밥, 수육백반, 밀면"을 한 줄 문장으로 두면 다른 행과 구분이 안 된다.
                val chips = remember(signatureMenu) { signatureMenu.toMenuChips() }
                if (chips == null) {
                    ExpandableValue(text = signatureMenu)
                } else {
                    // FlowRow는 이 프로젝트에서 실기기 크래시를 낸다(CLAUDE.md §6-6).
                    WrapRow(modifier = Modifier.fillMaxWidth(), horizontalSpacing = 6.dp, verticalSpacing = 6.dp) {
                        chips.forEach { chip -> MenuChip(item = chip) }
                    }
                }
            }
            rowsDrawn++
        }

        if (usageFee != null) {
            VisitInfoRow(
                icon = Icons.Default.Payments,
                label = curation.usageFeeLabel,
                showDivider = rowsDrawn > 0
            ) {
                ExpandableValue(text = usageFee)
            }
            rowsDrawn++
        }

        if (parkingInfo != null) {
            VisitInfoRow(
                icon = Icons.Default.LocalParking,
                label = curation.parkingLabel,
                showDivider = rowsDrawn > 0
            ) {
                ExpandableValue(text = parkingInfo)
            }
            rowsDrawn++
        }

        if (homepageUrl != null) {
            // 유일하게 누를 수 있는 행이라, 값 글자색(CoralInk)과 꼬리 아이콘으로 그걸 드러낸다 —
            // 장소 상세의 전화 행(BasicInfoRow)이 쓰는 것과 같은 규칙이다.
            VisitInfoRow(
                icon = Icons.Default.Language,
                label = strings.hospitalDetail.homepageLabel,
                showDivider = rowsDrawn > 0,
                onClick = { onOpenHomepage(homepageUrl) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = homepageUrl.stripUrlScheme(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = CoralInk,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = CoralPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        // 라벨은 번역돼 있는데 값은 한국어라 "번역이 덜 됐다"로 읽히기 쉽다 — 왜 원문인지 밝힌다.
        // AtAGlanceRow가 각주를 직접 붙이는 것과 같은 이유로 이 컴포넌트가 직접 붙여서, 쓰는 화면
        // 어디서도 빠질 수 없게 한다.
        if (strings.language != SupportedLanguage.KO) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = curation.visitInfoOriginalLanguageNote,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

/**
 * 아이콘 원 + 라벨(위 작은 글씨) + 값(아래). 장소 상세의 BasicInfoRow와 같은 위계다 — 같은 화면에
 * 두 카드가 나란히 서므로 행 모양이 서로 달라 보이면 안 된다.
 *
 * 행 사이에만 구분선을 넣는다(첫 행 위에는 없다) — 값 높이가 한 줄부터 여러 문단까지 제각각이라
 * 간격만으로는 한 항목이 어디서 끝나는지 알기 어렵다.
 */
@Composable
private fun VisitInfoRow(
    icon: ImageVector,
    label: String,
    showDivider: Boolean,
    onClick: (() -> Unit)? = null,
    value: @Composable () -> Unit
) {
    if (showDivider) {
        HorizontalDivider(color = DividerColor)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(CoralPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(3.dp))
            value()
        }
    }
}

/**
 * 길면 접어두고 "더보기"로 펼치는 값 텍스트.
 *
 * 공공 API 값은 길이 편차가 커서(한 줄 ~ 여러 문단) 그대로 두면 주차 안내 하나가 카드 전체를
 * 차지하고 그 아래 홈페이지 링크가 화면 밖으로 밀린다. 반대로 무조건 자르면 짧은 값에도 쓸모없는
 * 토글이 붙으므로, 실제로 잘렸을 때만(hasVisualOverflow) 토글을 그린다.
 */
@Composable
private fun ExpandableValue(text: String) {
    var expanded by remember(text) { mutableStateOf(false) }
    var overflowed by remember(text) { mutableStateOf(false) }
    val common = LocalAppStrings.current.common

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = TextPrimary,
        maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_MAX_LINES,
        overflow = TextOverflow.Ellipsis,
        // 펼친 뒤에는 넘칠 일이 없어 false로 덮어써진다 — 그러면 토글이 사라져 되접을 수 없다.
        onTextLayout = { result -> if (!expanded) overflowed = result.hasVisualOverflow }
    )
    if (overflowed || expanded) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (expanded) common.collapseLabel else common.expandLabel,
            style = MaterialTheme.typography.labelMedium,
            color = CoralInk,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { expanded = !expanded }
                .padding(vertical = 2.dp)
        )
    }
}

/**
 * 메뉴 칩 하나 — 이름과 가격을 붙여서 한 덩어리로 두지 않는다.
 *
 * 원문이 "킹크랩 W12,000"처럼 이름과 가격이 한 문자열로 붙어 오는데, 그대로 한 줄로 두면
 * "킹크랩 W12 000"으로 읽혀 이름이 어디서 끝나고 가격이 어디서 시작하는지 알 수 없다. 이름은
 * 진하게, 가격은 중립색으로 떼어 놓으면 훑어볼 때 둘이 자동으로 갈린다.
 */
@Composable
private fun MenuChip(item: MenuItem) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CoralPrimaryContainer)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        if (item.price != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.price,
                style = MaterialTheme.typography.labelMedium,
                color = BadgeText
            )
        }
    }
}

private const val COLLAPSED_MAX_LINES = 4

/** 칩으로 쪼갤 메뉴 수 상한 — 이보다 많으면 나열이 아니라 설명문일 가능성이 높다. */
private const val MAX_MENU_CHIPS = 12

/** 칩 하나에 들어갈 만한 메뉴명 길이 상한. 이보다 길면 메뉴명이 아니라 문장이다. */
private const val MAX_MENU_NAME_LENGTH = 20

/** 메뉴 하나 — 이름과, 원문에 붙어 있으면 떼어낸 가격. (파싱 규칙을 단위 테스트가 고정한다) */
internal data class MenuItem(val name: String, val price: String?)

/**
 * 메뉴 사이 구분자.
 *
 * 쉼표는 **천 단위 구분자로도 쓰인다** — "킹크랩 12,000"을 무조건 쉼표로 쪼개면 "킹크랩 12"와
 * "000" 두 칩이 된다(실제로 그렇게 깨졌다). 그래서 앞뒤가 모두 숫자인 쉼표만 구분자에서 뺀다:
 * 첫 대안은 "앞이 숫자가 아닌 쉼표", 둘째는 "뒤가 숫자가 아닌 쉼표"라 둘의 합집합이 곧
 * "천 단위 구분자가 아닌 쉼표"다. "밀면 8000, 만두 6000"의 쉼표는 뒤가 공백이라 정상적으로 갈린다.
 */
private val MENU_SEPARATOR = Regex("""\s*(?:[·/|\n]|(?<![0-9]),|,(?![0-9]))\s*""")

/**
 * 문자열 끝에 붙은 가격.
 *
 * 원화 기호는 소스마다 `₩` / `\` / `W`로 제각각 온다(폰트에 따라 역슬래시가 원화로 보이는 탓에
 * ASCII 대체 표기가 섞인다). 숫자 사이 구분자도 쉼표일 때와 공백일 때가 있다("W12,000", "W12 000").
 * 숫자가 반드시 하나는 있어야 매치되므로 "2인분"처럼 단위가 뒤에 남는 값은 가격으로 오인되지 않는다.
 */
private val MENU_PRICE = Regex("""[0-9][0-9,. ]*\s*(?:원|won|WON)?\s*$""")

/**
 * 가격 앞에 붙는 통화 기호. 소스마다 제각각이라 셋 다 본다 — 원화 기호(₩), 역슬래시(폰트에 따라
 * 원화로 보여서 그대로 실려 온다), 그리고 ASCII 대체 표기 W.
 *
 * 정규식 문자 클래스에 넣지 않고 Char로 따로 두는 이유: 역슬래시는 정규식 안에서 이스케이프
 * 문자라 클래스에 넣으려면 이중 이스케이프가 필요한데, 그 표기는 읽는 사람도 도구도 자주 틀린다.
 */
private val WON_SIGNS = charArrayOf('₩', Char(0x5C), 'W')

/**
 * 대표메뉴 문자열을 칩 목록으로 쪼갠다. 메뉴 나열이 아니라고 판단되면 null을 돌려주고 호출부가
 * 원문을 그대로 보여준다 — 공공 API 값은 자유 텍스트라 파싱 실패를 전제로 둔다.
 *
 * TourAPI firstmenu와 부산맛집 대표메뉴는 "돼지국밥 9,000원, 밀면 8,000원"처럼 이름과 가격이
 * 붙어서 오기도 하고, 가게를 설명하는 문장이 통째로 들어오기도 한다. 조각 수와 **이름** 길이가
 * 메뉴명다울 때만 칩으로 만든다(가격은 길이 판정에서 뺀다 — 가격이 붙었다고 문장으로 볼 순 없다).
 *
 * 하나짜리도 칩으로 만든다 — 가장 흔한 형태가 "킹크랩 W12,000" 한 줄인데, 예전엔 구분자가 없어
 * 나열로 인정받지 못하고 이름과 가격이 붙은 채 그대로 나갔다.
 */
internal fun String.toMenuChips(): List<MenuItem>? {
    val items = split(MENU_SEPARATOR)
        .map { part -> part.trim().trim('-', ' ') }
        .filter { part -> part.isNotEmpty() }
        .map { part -> part.toMenuItem() }
    val looksLikeMenuList = items.size in 1..MAX_MENU_CHIPS &&
        items.all { menu -> menu.name.length <= MAX_MENU_NAME_LENGTH }
    return items.takeIf { looksLikeMenuList }
}

private fun String.toMenuItem(): MenuItem {
    val match = MENU_PRICE.find(this) ?: return MenuItem(this, null)
    var start = match.range.first
    if (start > 0 && this[start - 1] in WON_SIGNS) {
        start--
    }
    // 가격 앞에 공백이 없으면 이름 안에 있는 숫자를 잘못 떼어낸 것이다("커피1+1") — 통째로 이름으로 둔다.
    if (start == 0 || !this[start - 1].isWhitespace()) {
        return MenuItem(this, null)
    }
    val name = substring(0, start).trim()
    // 값 전체가 가격뿐이면(이름이 안 남으면) 쪼개지 않는다 — 이름 없는 칩은 아무 뜻도 없다.
    return if (name.isEmpty()) MenuItem(this, null) else MenuItem(name, normalizeWonSign(substring(start).trim()))
}

/** 통화 기호 표기를 하나로 모은다 — 같은 목록 안에서 "W12,000"과 "₩12,000"이 섞여 보이지 않게. */
private fun normalizeWonSign(price: String): String =
    if (price.isNotEmpty() && price[0] in WON_SIGNS) "₩" + price.drop(1).trimStart() else price

/** 링크는 스킴을 떼고 보여준다 — 한 줄에 들어가는 폭이 좁아 "https://"가 도메인을 밀어낸다. */
private fun String.stripUrlScheme(): String =
    removePrefix("https://").removePrefix("http://").removeSuffix("/")
