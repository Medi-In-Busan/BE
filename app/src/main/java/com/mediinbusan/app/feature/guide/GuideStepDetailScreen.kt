package com.mediinbusan.app.feature.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.data.guide.GuidePhase

// S-06 하위 STEP 상세
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideStepDetailScreen(
    phase: GuidePhase,
    title: String,
    onBack: () -> Unit,
    onItemClick: (GuideDetailItem) -> Unit = {}
) {
    val content = phase.toDetailContent()

    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = {
                    Text(
                        text = "${phase.toStepNumberLabel()} $title",
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
            if (content.bannerResId != null) {
                GuideDetailBanner(
                    bannerResId = content.bannerResId,
                    aspectRatio = 1480f / 1063f,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }

            if (content.checklistItems.isNotEmpty()) {
                GuideDetailItemSection(
                    title = content.checklistTitle,
                    items = content.checklistItems,
                    showArrow = true,
                    onItemClick = onItemClick
                )
            }
            if (content.situationalItems.isNotEmpty()) {
                GuideDetailItemSection(
                    title = content.situationalTitle,
                    items = content.situationalItems,
                    showArrow = false,
                    onItemClick = onItemClick
                )
            }

            GuideDetailNoticeBanner(
                iconResId = content.noticeIconResId,
                text = content.noticeText,
                modifier = Modifier.padding(top = 28.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GuideDetailItemSection(
    title: String,
    items: List<GuideDetailItem>,
    showArrow: Boolean,
    onItemClick: (GuideDetailItem) -> Unit
) {
    Column(modifier = Modifier.padding(top = 28.dp)) {
        GuideDetailSectionTitle(title = title)
        Column(
            modifier = Modifier.padding(top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                GuideDetailItemCard(
                    iconResId = item.iconResId,
                    title = item.title,
                    description = item.description,
                    trailingIcon = if (showArrow) Icons.AutoMirrored.Filled.KeyboardArrowRight else null,
                    onClick = if (showArrow) ({ onItemClick(item) }) else null
                )
            }
        }
    }
}