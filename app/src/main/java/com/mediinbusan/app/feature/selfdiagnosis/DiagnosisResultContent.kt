package com.mediinbusan.app.feature.selfdiagnosis

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.InfoBackground
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.WarningBackground

@Composable
fun DiagnosisResultContent(
    resultType: DiagnosisResultType,
    onCtaClick: (DiagnosisCtaTarget) -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = resultType.bannerDrawableRes()),
            contentDescription = "${resultType.title}. ${resultType.description}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1536f / 1024f)
                .clip(RoundedCornerShape(20.dp))
        )

        ResultCard(resultType = resultType)

        if (resultType.noticeText != null) {
            NoticeBanner(text = resultType.noticeText, isWarning = true)
        }
        NoticeBanner(text = DiagnosisResultType.COMMON_SAFETY_NOTICE, isWarning = false)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            resultType.ctas.forEach { cta ->
                CtaButton(cta = cta, onClick = { onCtaClick(cta.target) })
            }
        }

        OutlinedButton(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "다시 진단하기")
        }
    }
}

/** 배너 이미지에 이미 타입별 그래픽이 있어 A~E 매핑은 UI 레이어에만 둔다(도메인 enum은 리소스에 의존하지 않음). */
private fun DiagnosisResultType.bannerDrawableRes(): Int = when (this) {
    DiagnosisResultType.TYPE_A -> R.drawable.self_diagnosis_type_a
    DiagnosisResultType.TYPE_B -> R.drawable.self_diagnosis_type_b
    DiagnosisResultType.TYPE_C -> R.drawable.self_diagnosis_type_c
    DiagnosisResultType.TYPE_D -> R.drawable.self_diagnosis_type_d
    DiagnosisResultType.TYPE_E -> R.drawable.self_diagnosis_type_e
}

@Composable
private fun ResultCard(resultType: DiagnosisResultType) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "다음 확인 항목",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                resultType.checklist.forEach { item -> ChecklistRow(text = item) }
            }
        }
    }
}

@Composable
private fun ChecklistRow(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = CoralPrimary,
            modifier = Modifier.padding(top = 2.dp).height(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}

/** SelfDiagnosisScreen의 인트로 화면에서도 재사용한다(같은 패키지). */
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
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (isWarning) Icons.Default.WarningAmber else Icons.Default.Info,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.height(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
    }
}

@Composable
private fun CtaButton(cta: DiagnosisCta, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
    ) {
        Text(text = cta.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}
