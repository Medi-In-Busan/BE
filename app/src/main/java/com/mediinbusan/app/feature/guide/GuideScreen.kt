package com.mediinbusan.app.feature.guide

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.SettingsTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BrandBackTopAppBar
import com.mediinbusan.app.core.ui.ErrorState
import com.mediinbusan.app.core.ui.LoadingState
import com.mediinbusan.app.data.guide.GuideStep

/** S-06 의료 이용 절차 가이드(F-008). STEP 01~06 카드 목록 — 단계별 상세 화면 연결은 후속 이슈. */
@Composable
fun GuideScreen(
    onMenuClick: () -> Unit = {},
    // 하위 STEP 상세 화면 미구현, 기본값 no-op, NavHost 연결은 후속 작업
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
    // 하단 탭바: MediInBusanApp.kt 공용 컴포넌트, 이 화면은 BottomNavBarHeight만큼 여백만 추가
    Scaffold(
        containerColor = PageBackground,
        topBar = {
            BrandBackTopAppBar(
                onBack = onMenuClick,
                currentLanguageCode = uiState.languageCode,
                onLanguageSelected = onLanguageSelected,
                navigationIcon = Icons.Default.Menu
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
                else -> GuideStepList(steps = uiState.steps, onStepClick = onStepClick)
            }
        }
    }
}

@Composable
private fun GuideStepList(steps: List<GuideStep>, onStepClick: (GuideStep) -> Unit) {
    val strings = LocalAppStrings.current.guide
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = BottomNavBarHeight),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)) {
                Text(text = strings.screenTitle, style = SettingsTitleStyle, color = TextPrimary)
                Text(
                    text = strings.screenSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
        items(items = steps, key = { it.id }) { step ->
            GuideStepCard(step = step, onClick = { onStepClick(step) })
        }
    }
}

@Composable
private fun GuideStepCard(step: GuideStep, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = step.phase.toIconResId()),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = step.phase.toStepNumberLabel(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = step.phase.toAccentColor()
                    )
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                Text(
                    text = step.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}