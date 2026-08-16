package com.mediinbusan.app.feature.guide

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp as lerpColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.HeroTitleLargeStyle
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.InactiveIcon
import com.mediinbusan.app.core.designsystem.SettingsTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BrandTopAppBar
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.data.guide.GuideStep
import kotlin.math.absoluteValue

@Composable
fun GuideScreen(
    onMenuClick: () -> Unit = {},
    onStepClick: (GuideStep) -> Unit = {},
    viewModel: GuideViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GuideContent(
        uiState = uiState,
        onMenuClick = onMenuClick,
        onLanguageSelected = viewModel::onLanguageSelected,
        onStepClick = onStepClick
    )
}

@Composable
private fun GuideContent(
    uiState: GuideUiState,
    onMenuClick: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onStepClick: (GuideStep) -> Unit
) {
    Scaffold(
        containerColor = HomeBackgroundPink,
        topBar = {
            BrandTopAppBar(
                onSettingsClick = onMenuClick,
                currentLanguageCode = uiState.languageCode,
                onLanguageSelected = onLanguageSelected
            )
        }
    ) { innerPadding ->
        val errorMessage = uiState.errorMessage

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                uiState.isLoading -> LoadingState(modifier = Modifier.padding(bottom = BottomNavBarHeight))

                errorMessage != null -> ErrorState(
                    message = errorMessage,
                    modifier = Modifier.padding(bottom = BottomNavBarHeight)
                )

                else -> GuideStepCarousel(
                    steps = uiState.steps,
                    onStepClick = onStepClick
                )
            }
        }
    }
}

private const val CardWidthFraction = 0.60f
private const val IllustrationWidthFraction = 0.82f

private const val MascotWidthFraction = 0.58f
private const val MascotOffsetXFraction = -0.30f
private const val MascotOffsetYFraction = 0.76f

private const val BubbleMaxWidthFraction = 0.79f
private const val BubbleOffsetXFraction = 0.37f
private const val BubbleOffsetYFraction = 1.03f

private const val FooterSpacerFraction = 0.08f
private const val MascotAspectRatio = 1024f / 1536f
private const val CarouselItemHeightFraction = 2.60f

private const val GuideCardMinScale = 0.90f
private const val GuideCardFocusScale = 1.04f
private const val GuideCardMinSaturation = 0.62f
private const val GuideCardMinAlpha = 0.72f
private const val GuideCardMinContentScale = 0.86f

private val GuideSideCardYOffset = 22.dp
private val GuideSideCardPullIn = 40.dp
private val GuideCardShape = RoundedCornerShape(30.dp)

@Composable
private fun GuideStepCarousel(
    steps: List<GuideStep>,
    onStepClick: (GuideStep) -> Unit
) {
    val strings = LocalAppStrings.current.guide
    val density = LocalDensity.current
    val pagerState = rememberPagerState(pageCount = { steps.size })

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 2.dp)
        ) {
            Text(
                text = strings.screenTitle,
                style = SettingsTitleStyle,
                color = TextPrimary
            )

            Text(
                text = strings.screenSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        GuideCarouselProgress(
            modifier = Modifier.padding(horizontal = 20.dp),
            pageCount = steps.size,
            activePage = pagerState.currentPage
        )

        Spacer(modifier = Modifier.height(18.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val availableWidth = with(density) { constraints.maxWidth.toDp() }
            val availableHeight = with(density) { constraints.maxHeight.toDp() }

            val cardWidth = availableWidth * CardWidthFraction
            val sidePadding = (availableWidth - cardWidth) / 2f
            val illustrationSize = cardWidth * IllustrationWidthFraction
            val mascotWidth = cardWidth * MascotWidthFraction
            val bubbleMaxWidth = cardWidth * BubbleMaxWidthFraction
            val pagerHeight = (cardWidth * CarouselItemHeightFraction).coerceAtMost(availableHeight)

            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = sidePadding),
                pageSpacing = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(pagerHeight)
                    .align(Alignment.TopCenter)
            ) { page ->
                val step = steps[page]

                val pageOffset =
                    (pagerState.currentPage - page) +
                            pagerState.currentPageOffsetFraction

                GuideStepCarouselCard(
                    step = step,
                    pageOffset = pageOffset,
                    cardWidth = cardWidth,
                    illustrationSize = illustrationSize,
                    mascotWidth = mascotWidth,
                    bubbleMaxWidth = bubbleMaxWidth,
                    onClick = { onStepClick(step) }
                )
            }
        }

        Spacer(modifier = Modifier.height(BottomNavBarHeight))
    }
}

@Composable
private fun GuideCarouselProgress(
    modifier: Modifier = Modifier,
    pageCount: Int,
    activePage: Int
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(pageCount) { index ->
                val isActive = index == activePage

                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(if (isActive) 22.dp else 18.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(
                            if (isActive) {
                                CoralPrimary
                            } else {
                                InactiveIcon.copy(alpha = 0.55f)
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "${activePage + 1} / $pageCount",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
    }
}

@Composable
private fun GuideStepCarouselCard(
    step: GuideStep,
    pageOffset: Float,
    cardWidth: Dp,
    illustrationSize: Dp,
    mascotWidth: Dp,
    bubbleMaxWidth: Dp,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current.guide
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }

    var cardHeight by remember { mutableStateOf(0.dp) }

    val normalizedOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)
    val focus = 1f - normalizedOffset

    val scale = lerp(GuideCardMinScale, GuideCardFocusScale, focus)
    val contentScale = lerp(GuideCardMinContentScale, 1f, focus)
    val cardAlpha = lerp(GuideCardMinAlpha, 1f, focus)
    val saturation = lerp(GuideCardMinSaturation, 1f, focus)

    val badgeBackground = lerpColor(CoralPrimaryContainer, CoralPrimary, focus)
    val badgeTextColor = lerpColor(CoralPrimary, Color.White, focus)
    val titleColor = lerpColor(TextSecondary, TextPrimary, focus)
    val bodyColor = lerpColor(InactiveIcon, TextSecondary, focus)

    val illustrationColorFilter = remember(saturation) {
        ColorFilter.colorMatrix(
            ColorMatrix().apply {
                setToSaturation(saturation)
            }
        )
    }

    val isFocused = focus > 0.75f
    val sideCardYOffsetPx = with(density) { GuideSideCardYOffset.toPx() }
    val sideCardPullInPx = with(density) { GuideSideCardPullIn.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(focus)
            .graphicsLayer {
                transformOrigin = TransformOrigin(0.5f, 0f)

                scaleX = scale
                scaleY = scale
                alpha = cardAlpha

                translationY = sideCardYOffsetPx * (1f - focus)

                translationX =
                    when {
                        pageOffset > 0f -> sideCardPullInPx * normalizedOffset
                        pageOffset < 0f -> -sideCardPullInPx * normalizedOffset
                        else -> 0f
                    }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .shadow(
                    elevation = if (isFocused) 24.dp else 2.dp,
                    shape = GuideCardShape,
                    clip = false,
                    ambientColor = Color.Black.copy(
                        alpha = if (isFocused) 0.18f else 0.025f
                    ),
                    spotColor = Color.Black.copy(
                        alpha = if (isFocused) 0.24f else 0.025f
                    )
                )
                .clip(GuideCardShape)
                .background(Color.White)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 26.dp,
                    bottom = 24.dp
                )
                .onGloballyPositioned { coordinates ->
                    cardHeight = with(density) { coordinates.size.height.toDp() }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.graphicsLayer {
                    scaleX = contentScale
                    scaleY = contentScale
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(badgeBackground)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "STEP ${step.phase.toStepNumberLabel()}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = step.title,
                    style = if (step.title.length >= 8) {
                        HeroTitleLargeStyle.copy(
                            fontSize = 24.sp,
                            lineHeight = 30.sp
                        )
                    } else {
                        HeroTitleLargeStyle
                    },
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = step.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = bodyColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(illustrationSize * 0.94f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        CoralPrimary.copy(
                                            alpha = 0.12f * focus.coerceIn(0.2f, 1f)
                                        ),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    Image(
                        painter = painterResource(id = step.phase.toCardPhotoResId()),
                        contentDescription = step.title,
                        contentScale = ContentScale.Fit,
                        colorFilter = illustrationColorFilter,
                        modifier = Modifier.size(illustrationSize)
                    )
                }

                Spacer(modifier = Modifier.height(cardWidth * FooterSpacerFraction))
            }
        }

        if (isFocused && cardHeight > 0.dp) {
            Image(
                painter = painterResource(id = R.drawable.common_medin_busan_mascot),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .zIndex(4f)
                    .offset(
                        x = cardWidth * MascotOffsetXFraction,
                        y = cardHeight * MascotOffsetYFraction
                    )
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(32.dp),
                        clip = false,
                        ambientColor = Color.Black.copy(alpha = 0.10f),
                        spotColor = Color.Black.copy(alpha = 0.14f)
                    )
                    .width(mascotWidth)
                    .aspectRatio(MascotAspectRatio)
            )

            GuideMascotSpeechBubble(
                lead = step.phase.toTipLead(strings),
                highlight = step.phase.toTipHighlight(strings),
                maxWidth = bubbleMaxWidth,
                modifier = Modifier
                    .zIndex(5f)
                    .offset(
                        x = cardWidth * BubbleOffsetXFraction,
                        y = cardHeight * BubbleOffsetYFraction
                    )
            )
        }
    }
}

@Composable
private fun GuideMascotSpeechBubble(
    lead: String,
    highlight: String,
    maxWidth: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.width(maxWidth)) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-5).dp, y = (-10).dp)
                .size(18.dp)
                .graphicsLayer { rotationZ = 45f }
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(3.dp),
                    clip = false,
                    ambientColor = Color.Black.copy(alpha = 0.10f),
                    spotColor = Color.Black.copy(alpha = 0.14f)
                )
                .background(Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 22.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.22f),
                    spotColor = Color.Black.copy(alpha = 0.30f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = lead,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = highlight,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = CoralPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}