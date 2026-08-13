package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.InfoBackgroundBlue
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.HospitalLocationCheckinStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings

private data class ReceptionStep(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private fun receptionSteps(s: HospitalLocationCheckinStrings): List<ReceptionStep> = listOf(
    ReceptionStep(iconResId = R.drawable.ic_reception_direction_sign, title = s.step1Title, description = s.step1Description),
    ReceptionStep(iconResId = R.drawable.ic_passport_document, title = s.step2Title, description = s.step2Description),
    ReceptionStep(iconResId = R.drawable.ic_waiting_chair, title = s.step3Title, description = s.step3Description),
    ReceptionStep(iconResId = R.drawable.ic_examination_room_door, title = s.step4Title, description = s.step4Description)
)

// S-06 하위 STEP03 상세의 "병원 위치와 접수 절차 확인" 카드 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalLocationCheckinDetailScreen(onBack: () -> Unit, onNavigateToMap: () -> Unit = {}) {
    val strings = LocalAppStrings.current
    val s = strings.guide.hospitalLocationCheckin
    val steps = receptionSteps(s)
    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.common.backContentDescription)
                    }
                },
                title = {
                    Text(
                        text = "03-03 ${s.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            GuideDetailBanner(
                backgroundResId = R.drawable.img_hospital_reception_banner,
                aspectRatio = 1448f / 1086f,
                title = s.bannerTitle,
                subtitle = s.bannerSubtitle,
                stepLabel = "STEP 03",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 20.dp)) {
                GuideDetailItemCard(
                    iconResId = R.drawable.ic_hospital_location_map,
                    title = s.itemCardTitle,
                    description = s.itemCardDescription,
                    trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    onClick = onNavigateToMap
                )
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = s.sectionTitle)
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    steps.forEachIndexed { index, step ->
                        ReceptionStepRow(
                            number = index + 1,
                            step = step,
                            showConnector = index != steps.lastIndex
                        )
                    }
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = s.noticeText,
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun ReceptionStepRow(number: Int, step: ReceptionStep, showConnector: Boolean) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.width(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SkyBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            if (showConnector) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .padding(vertical = 4.dp)
                ) {
                    drawLine(
                        color = SkyBlue,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                }
            }
        }

        Box(modifier = Modifier.padding(start = 12.dp, bottom = if (showConnector) 12.dp else 0.dp)) {
            GuideDetailItemCard(
                iconResId = step.iconResId,
                title = step.title,
                description = step.description
            )
        }
    }
}
