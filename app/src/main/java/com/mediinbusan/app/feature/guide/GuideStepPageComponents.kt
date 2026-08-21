package com.mediinbusan.app.feature.guide

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MedinTipCardBackground
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.launchIntentSafely
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val MemoDrawableIds = listOf(
    R.drawable.guide_memo1,
    R.drawable.guide_memo2,
    R.drawable.guide_memo3,
    R.drawable.guide_memo4,
    R.drawable.guide_memo5,
    R.drawable.guide_memo6,
    R.drawable.guide_memo7,
    R.drawable.guide_memo8
)

private val MemoIconIds = listOf(
    R.drawable.guide_passport_document,
    R.drawable.guide_medical_document,
    R.drawable.guide_appointment_calendar,
    R.drawable.guide_medical_receipt,
    R.drawable.guide_visa_document,
    R.drawable.guide_global_guide,
    R.drawable.guide_passport_document,
    R.drawable.guide_medical_document
)

private val MemoRotations = listOf(-2f, 1.5f, -1f, 2f)

// 배너 이미지 자체에 이미 "STEP XX" 라벨이 박혀 있으므로(제목·부제목만 언어별로 다시 그려야 해서
// 없앤 것과 달리, STEP 배지는 이미지에 남아있다) 배지를 따로 그리지 않는다 — 겹쳐 보이는 문제 방지.
// STEP01~06 전부 이 하나의 컴포저블을 공유한다(GuideStepDetailScreen이 STEP01/02/03/05/06,
// TreatmentExaminationDetailScreen이 STEP04를 각각 호출) — 특정 STEP만 따로 그리지 않는다.
@Composable
fun GuideStepHero(
    @DrawableRes heroResId: Int,
    stepTitle: String,
    stepSubtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1672f / 941f)
            .clip(RoundedCornerShape(20.dp))
            .background(HomeBackgroundPink)
    ) {
        Image(
            painter = painterResource(id = heroResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterEnd)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.70f)
                .padding(start = 30.dp, end = 8.dp, top = 80.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stepTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = stepSubtitle,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GuideStepSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    if (title.isBlank()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.guide_camellia_flower_soft),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(start = 8.dp, end = 12.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(CoralPrimaryContainer)
        )
    }
}

@Composable
fun GuideStepTipBanner(
    label: String,
    tipLead: String,
    tipHighlight: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MedinTipCardBackground)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = CoralPrimary.copy(alpha = 0.28f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.guide_gwangandaegyo_bridge),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 10.dp)
                .width(150.dp)
                .height(74.dp)
                .graphicsLayer {
                    alpha = 0.52f
                }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.guid_camellia_flower),
                contentDescription = null,
                modifier = Modifier.size(52.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 72.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CoralPrimary.copy(alpha = 0.11f))
                        .padding(
                            horizontal = 11.dp,
                            vertical = 4.dp
                        )
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CoralPrimary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = buildAnnotatedString {
                        append(tipLead)

                        if (tipHighlight.isNotBlank()) {
                            append(" ")

                            withStyle(
                                SpanStyle(
                                    color = CoralPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(tipHighlight)
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}

@Composable
private fun guideItemClickAction(
    item: GuideDetailItem,
    onNavigableClick: (GuideDetailItem) -> Unit
): (() -> Unit)? {
    val context = LocalContext.current
    val url = item.url

    return when {
        url != null -> {
            {
                context.launchIntentSafely(
                    Intent(
                        Intent.ACTION_VIEW,
                        url.toUri()
                    )
                )
            }
        }

        item.navigable -> {
            {
                onNavigableClick(item)
            }
        }

        else -> null
    }
}

@Composable
fun GuideOfficialLinkRow(
    items: List<GuideDetailItem>,
    officialSiteLabel: String,
    visitSiteLabel: String,
    onNavigableClick: (GuideDetailItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = 5.dp,
            vertical = 5.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            val accent = item.accentColor ?: if (index == 0) CoralPrimary else SkyBlue

            GuideAppearingCard(
                index = index,
                modifier = Modifier.fillParentMaxWidth(0.60f)
            ) {
                GuideOfficialLinkCard(
                    item = item,
                    accent = accent,
                    officialSiteLabel = officialSiteLabel,
                    visitSiteLabel = visitSiteLabel,
                    onClick = guideItemClickAction(
                        item = item,
                        onNavigableClick = onNavigableClick
                    )
                )
            }
        }
    }
}

@Composable
private fun GuideOfficialLinkCard(
    item: GuideDetailItem,
    accent: Color,
    officialSiteLabel: String,
    visitSiteLabel: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    GuidePressableCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(accent.copy(alpha = 0.10f))
                    .padding(
                        horizontal = 12.dp,
                        vertical = 5.dp
                    )
            ) {
                Text(
                    text = officialSiteLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    maxLines = 1
                )
            }

            Image(
                painter = painterResource(id = item.iconResId),
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        top = 14.dp,
                        bottom = 10.dp
                    )
                    .size(54.dp)
            )

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 실제 외부 링크(url)가 있는 카드에만 "바로가기" 버튼을 붙인다 — 링크가 없는 순수 정보
            // 카드(예: STEP04 "방문 병원에 문의")도 같은 카드 디자인을 재사용할 수 있게 한다.
            if (item.url != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = accent
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 7.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = visitSiteLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun GuideMemoRow(
    items: List<GuideDetailItem>,
    onNavigableClick: (GuideDetailItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = 10.dp,
            vertical = 6.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            GuideAppearingCard(
                index = index,
                modifier = Modifier.fillParentMaxWidth(0.50f)
            ) {
                GuideMemoCard(
                    item = item,
                    memoResId = item.memoBackgroundResId ?: MemoDrawableIds[index % MemoDrawableIds.size],
                    iconResId = item.memoIllustrationResId ?: MemoIconIds[index % MemoIconIds.size],
                    rotation = MemoRotations[index % MemoRotations.size],
                    onClick = guideItemClickAction(
                        item = item,
                        onNavigableClick = onNavigableClick
                    )
                )
            }
        }
    }
}

@Composable
private fun GuideMemoCard(
    item: GuideDetailItem,
    @DrawableRes memoResId: Int,
    @DrawableRes iconResId: Int,
    rotation: Float,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    GuidePressableCard(
        onClick = onClick,
        baseRotation = rotation,
        wobble = true,
        useMemoBackground = true,
        modifier = modifier.aspectRatio(0.90f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = memoResId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp)
                )

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    // 영어 등 한국어보다 긴 번역 문구도 잘리지 않도록 여유 있게 4줄까지 허용한다
                    // (카드 세로 공간에 여유가 있어 4줄까지는 메모지 영역을 벗어나지 않는다).
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun GuideAppearingCard(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var appeared by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        delay((index * 50).milliseconds)
        appeared = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow
        ),
        label = "memoAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.96f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "memoScale"
    )

    val translateY by animateDpAsState(
        targetValue = if (appeared) 0.dp else 8.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow
        ),
        label = "memoTranslateY"
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            scaleX = scale
            scaleY = scale
            translationY = translateY.toPx()
        }
    ) {
        content()
    }
}

@Composable
private fun GuidePressableCard(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    baseRotation: Float = 0f,
    wobble: Boolean = false,
    useMemoBackground: Boolean = false,
    content: @Composable () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val pressed by interactionSource.collectIsPressedAsState()

    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessHigh
        ),
        label = "pressScale"
    )

    val wobbleOffset = if (wobble) {
        val infiniteTransition = rememberInfiniteTransition(
            label = "memoWobble"
        )

        val wobbleProgress by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1800,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "memoWobbleProgress"
        )

        wobbleProgress * 2.5f
    } else {
        0f
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }

    val cardModifier = modifier
        .graphicsLayer {
            rotationZ = baseRotation + wobbleOffset
            scaleX = pressScale
            scaleY = pressScale
        }
        .then(clickableModifier)

    if (useMemoBackground) {
        Box(
            modifier = cardModifier
        ) {
            content()
        }
    } else {
        Card(
            modifier = cardModifier,
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            content()
        }
    }
}