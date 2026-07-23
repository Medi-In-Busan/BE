package com.mediinbusan.app.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.BodyRegularStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.DisplayTitleStyle
import com.mediinbusan.app.core.designsystem.HeroSubtitleStyle
import com.mediinbusan.app.core.designsystem.InfoBackgroundBlue
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary

// 언어 카드 선택 원(28dp)의 미선택 테두리 색. 디자인 스펙에서 이 화면 전용으로만 지정된
// 값이라 전역 팔레트(core/designsystem/Color.kt)로 승격하지 않고 여기에 둔다.
private val UnselectedCircleBorder = Color(0xFFD7DEE7)

@Composable
fun LanguageSelectScreen(
    onNext: () -> Unit,
    viewModel: LanguageSelectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LanguageSelectContent(
        uiState = uiState,
        onLanguageSelected = viewModel::onLanguageSelected,
        onNextClick = { viewModel.onNextClicked(onNext) }
    )
}

@Composable
private fun LanguageSelectContent(
    uiState: LanguageSelectUiState,
    onLanguageSelected: (String) -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            LogoLockup()
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "언어를 선택해주세요", style = DisplayTitleStyle, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "앱에서 사용할 언어를 선택할 수 있습니다.", style = BodyRegularStyle, color = TextSecondary)

        Spacer(modifier = Modifier.height(24.dp))
        uiState.options.forEachIndexed { index, option ->
            LanguageCard(
                option = option,
                selected = option.code == uiState.selectedCode,
                onClick = { onLanguageSelected(option.code) }
            )
            if (index != uiState.options.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        InfoCard()

        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(text = "다음", onClick = onNextClick)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LogoLockup() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "메디인부산 로고",
            modifier = Modifier.size(32.dp)
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
private fun LanguageCard(option: LanguageOption, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "language_card_scale"
    )
    val glowColor = if (selected) CoralPrimary else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .scale(scale)
            .shadow(
                elevation = if (selected) 12.dp else 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = glowColor.copy(alpha = 0.08f),
                spotColor = glowColor.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) CoralPrimary else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = option.flagRes),
                contentDescription = option.label,
                modifier = Modifier.size(56.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = option.label,
                style = SectionTitleStyle.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            SelectionIndicator(selected = selected)
        }
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) CoralPrimary else Color.White,
        animationSpec = spring(),
        label = "selection_circle_background"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) CoralPrimary else UnselectedCircleBorder,
        animationSpec = spring(),
        label = "selection_circle_border"
    )

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(width = 1.5.dp, color = borderColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = scaleIn(animationSpec = spring()) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun InfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InfoBackgroundBlue)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SkyBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "언어는 설정 화면에서 언제든 변경 가능합니다.",
            style = HeroSubtitleStyle,
            color = TextSecondary
        )
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = CoralPrimary.copy(alpha = 0.25f),
                spotColor = CoralPrimary.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(CoralPrimary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = SectionTitleStyle, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
private fun LanguageSelectContentPreview() {
    MediInBusanTheme {
        LanguageSelectContent(
            uiState = LanguageSelectUiState(selectedCode = "ko"),
            onLanguageSelected = {},
            onNextClick = {}
        )
    }
}
