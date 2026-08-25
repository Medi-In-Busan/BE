package com.mediinbusan.app.feature.selfdiagnosis

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.HeroTitleStyle
import com.mediinbusan.app.core.designsystem.InfoBackground
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.designsystem.WarningBackground
import com.mediinbusan.app.core.i18n.LocalAppStrings

@Composable
fun DiagnosisResultContent(
    resultType: DiagnosisResultType,
    onCtaClick: (DiagnosisCtaTarget) -> Unit,
    onRestart: () -> Unit,
    onGoHome: () -> Unit,
    goHomeButtonLabel: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current.selfDiagnosis
    val display = resultType.toDisplay(strings.results)
    val metricLabels = listOf(
        strings.results.metricDirectInquiry,
        strings.results.metricDocumentComplexity,
        strings.results.metricSupportNeed,
        strings.results.metricTourismWellness
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = display.title,
                style = HeroTitleStyle,
                color = display.accentColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = display.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }

        Image(
            painter = painterResource(id = resultType.heroDrawableRes()),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        MetricBarsSection(levels = display.metricLevels, labels = metricLabels, accentColor = display.accentColor)

        HorizontalDivider(color = BorderColor)

        ChecklistSection(items = display.checklist, accentColor = display.accentColor)

        CtaGrid(ctas = display.ctas, onClick = onCtaClick)

        Button(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
        ) {
            Text(text = goHomeButtonLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = strings.restartButton)
        }

        Text(
            text = strings.commonSafetyNotice,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun DiagnosisResultType.heroDrawableRes(): Int = when (this) {
    DiagnosisResultType.TYPE_A -> R.drawable.self_diagnosis_type_a_direct_inquiry_main
    DiagnosisResultType.TYPE_B -> R.drawable.self_diagnosis_type_b_international_center_banner
    DiagnosisResultType.TYPE_C -> R.drawable.self_diagnosis_type_c_registered_agency_banner
    DiagnosisResultType.TYPE_D -> R.drawable.self_diagnosis_type_d_long_term_visa_banner
    DiagnosisResultType.TYPE_E -> R.drawable.self_diagnosis_type_e_wellness_experience_banner
}

@Composable
private fun MetricBarsSection(levels: List<Int>, labels: List<String>, accentColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        labels.forEachIndexed { index, label ->
            MetricBarRow(label = label, level = levels.getOrElse(index) { 0 }, accentColor = accentColor)
        }
    }
}

@Composable
private fun MetricBarRow(label: String, level: Int, accentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.width(86.dp)
        )
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (index < level) accentColor else BorderColor)
                )
            }
        }
    }
}

@Composable
private fun ChecklistSection(items: List<String>, accentColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item -> ChecklistRow(text = item, accentColor = accentColor) }
    }
}

@Composable
private fun ChecklistRow(text: String, accentColor: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}

@Composable
private fun CtaGrid(ctas: List<DiagnosisCta>, onClick: (DiagnosisCtaTarget) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ctas.forEach { cta ->
            Box(modifier = Modifier.weight(1f)) {
                CtaCard(cta = cta, onClick = { onClick(cta.target) })
            }
        }
    }
}

@Composable
private fun CtaCard(cta: DiagnosisCta, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "ctaCardPressScale"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "ctaIconFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ctaIconFloatOffset"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = cta.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer { translationY = floatOffset }
            )
            Text(
                text = cta.label,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/** SelfDiagnosisScreen의 인트로/에러 메시지에서도 재사용한다(같은 패키지). */
@Composable
fun NoticeBanner(text: String, isWarning: Boolean) {
    val backgroundColor = if (isWarning) WarningBackground else InfoBackground
    val iconTint = if (isWarning) Color(0xFFB45309) else SkyBlue
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isWarning) Icons.Default.WarningAmber else Icons.Default.Info,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.height(18.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun DiagnosisResultContentTypeAPreview() {
    MediInBusanTheme {
        DiagnosisResultContent(
            resultType = DiagnosisResultType.TYPE_A,
            onCtaClick = {},
            onRestart = {},
            onGoHome = {},
            goHomeButtonLabel = "홈으로 돌아가기"
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun DiagnosisResultContentTypeBPreview() {
    MediInBusanTheme {
        DiagnosisResultContent(
            resultType = DiagnosisResultType.TYPE_B,
            onCtaClick = {},
            onRestart = {},
            onGoHome = {},
            goHomeButtonLabel = "홈으로 돌아가기"
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun DiagnosisResultContentTypeCPreview() {
    MediInBusanTheme {
        DiagnosisResultContent(
            resultType = DiagnosisResultType.TYPE_C,
            onCtaClick = {},
            onRestart = {},
            onGoHome = {},
            goHomeButtonLabel = "홈으로 돌아가기"
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun DiagnosisResultContentTypeDPreview() {
    MediInBusanTheme {
        DiagnosisResultContent(
            resultType = DiagnosisResultType.TYPE_D,
            onCtaClick = {},
            onRestart = {},
            onGoHome = {},
            goHomeButtonLabel = "홈으로 돌아가기"
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun DiagnosisResultContentTypeEPreview() {
    MediInBusanTheme {
        DiagnosisResultContent(
            resultType = DiagnosisResultType.TYPE_E,
            onCtaClick = {},
            onRestart = {},
            onGoHome = {},
            goHomeButtonLabel = "홈으로 돌아가기"
        )
    }
}
