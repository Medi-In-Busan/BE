package com.mediinbusan.app.feature.guide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MedinTipCardBackground
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.data.guide.GuidePhase

// S-06 하위 STEP 상세(합본 페이지). 예전에는 STEP 개요 + 항목별 leaf 화면(최대 3개)으로 나뉘어 있던
// 것을 hero + 섹션들 + MEDIN TIP 배너 하나의 스크롤 페이지로 합쳤다(GuideStepPageContentMapper 참고).
// leaf 화면 자체와 그 route는 SelfDiagnosis 등 다른 진입점이 계속 쓰므로 그대로 남겨둔다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideStepDetailScreen(
    phase: GuidePhase,
    onBack: () -> Unit,
    onItemClick: (GuideDetailItem) -> Unit = {}
) {
    val appStrings = LocalAppStrings.current
    val strings = appStrings.guide
    val content = phase.toStepPageContent(strings)

    Scaffold(
        containerColor = HomeBackgroundPink,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HomeBackgroundPink),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = appStrings.common.backContentDescription)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = content.stepNumberLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CoralPrimary)
                        Text(
                            text = " ${content.stepTitle}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                GuideStepHero(
                    heroResId = content.heroResId,
                    stepTitle = content.stepTitle,
                    stepSubtitle = content.stepSubtitle,
                    modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
                )
            }
            itemsIndexed(content.sections) { index, section ->
                GuideStepSectionBlock(
                    section = section,
                    officialSiteLabel = strings.officialSiteBadgeLabel,
                    visitSiteLabel = strings.visitSiteButtonLabel,
                    onItemClick = onItemClick,
                    modifier = Modifier.padding(top = if (index == 0) 8.dp else 32.dp)
                )
            }
            item {
                GuideStepTipBanner(
                    label = strings.medinTipLabel,
                    tipLead = content.tipLead,
                    tipHighlight = content.tipHighlight,
                    modifier = Modifier.padding(top = 32.dp, bottom = 28.dp)
                )
            }
        }
    }
}

@Composable
private fun GuideStepSectionBlock(
    section: GuideStepPageSection,
    officialSiteLabel: String,
    visitSiteLabel: String,
    onItemClick: (GuideDetailItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        GuideStepSectionHeader(title = section.title, modifier = Modifier.padding(bottom = 16.dp))
        when (section.cardStyle) {
            GuideStepCardStyle.LINK -> GuideOfficialLinkRow(
                items = section.rowItems,
                officialSiteLabel = officialSiteLabel,
                visitSiteLabel = visitSiteLabel,
                onNavigableClick = onItemClick
            )
            GuideStepCardStyle.MEMO -> GuideMemoRow(items = section.rowItems, onNavigableClick = onItemClick)
            GuideStepCardStyle.INFO -> section.rowItems.firstOrNull()?.let { item ->
                GuideDetailItemCard(
                    iconResId = item.iconResId,
                    title = item.title,
                    description = item.description,
                    onClick = { onItemClick(item) },
                    containerColor = MedinTipCardBackground,
                    borderColor = CoralPrimary.copy(alpha = 0.28f),
                    titleColor = TextPrimary,
                    descriptionColor = TextPrimary,
                    descriptionHighlightPrefix = item.descriptionHighlightPrefix,
                    descriptionHighlightColor = CoralPrimary
                )
            }
        }
        if (section.orderSteps.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 18.dp)) {
                GuideStepSectionHeader(title = section.orderStepsTitle, modifier = Modifier.padding(bottom = 14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    section.orderSteps.forEachIndexed { index, step ->
                        GuideOrderStepCard(number = index + 1, title = step.title, description = step.description)
                    }
                }
            }
        }
        if (section.questions.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 18.dp)) {
                GuideStepSectionHeader(title = section.questionsTitle, modifier = Modifier.padding(bottom = 14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    section.questions.forEachIndexed { questionIndex, question ->
                        GuideQuestionCard(number = questionIndex + 1, question = question)
                    }
                }
            }
        }
    }
}
