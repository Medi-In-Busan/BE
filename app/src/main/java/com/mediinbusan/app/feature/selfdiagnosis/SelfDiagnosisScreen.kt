package com.mediinbusan.app.feature.selfdiagnosis

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.SelfDiagnosisStrings

/**
 * S-XX 준비 유형 진단(인트로 → 5문항 → 준비 유형).
 * [fromOnboarding]이 true면 최초 실행 흐름(Splash -> 언어선택 -> 진단 -> Home) 중이라는 뜻으로,
 * 인트로에 "건너뛰기"가, 결과 화면에 "홈으로 시작하기"가 추가로 노출된다.
 */
@Composable
fun SelfDiagnosisScreen(
    fromOnboarding: Boolean = false,
    onBack: () -> Unit = {},
    onNavigateToCtaTarget: (DiagnosisCtaTarget) -> Unit = {},
    onFinishSetup: () -> Unit = {},
    viewModel: SelfDiagnosisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SelfDiagnosisEvent.NavigateBack -> onBack()
                is SelfDiagnosisEvent.NavigateToCtaTarget -> onNavigateToCtaTarget(event.target)
                SelfDiagnosisEvent.NavigateToHome -> onFinishSetup()
            }
        }
    }

    SelfDiagnosisContent(
        uiState = uiState,
        fromOnboarding = fromOnboarding,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelfDiagnosisContent(
    uiState: SelfDiagnosisUiState,
    fromOnboarding: Boolean,
    onIntent: (SelfDiagnosisIntent) -> Unit
) {
    val strings = LocalAppStrings.current.selfDiagnosis
    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                title = { Text(text = strings.topBarTitle) },
                navigationIcon = {
                    IconButton(onClick = { onIntent(SelfDiagnosisIntent.ClickBack) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.backContentDescription)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBackground)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val resultType = uiState.resultType
            when {
                resultType != null -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            DiagnosisResultContent(
                                resultType = resultType,
                                onCtaClick = { target -> onIntent(SelfDiagnosisIntent.ClickCta(target)) },
                                onRestart = { onIntent(SelfDiagnosisIntent.Restart) },
                                onFinishSetup = if (fromOnboarding) {
                                    { onIntent(SelfDiagnosisIntent.FinishSetup) }
                                } else {
                                    null
                                }
                            )
                        }
                    }
                }
                uiState.isIntroVisible -> {
                    IntroContent(
                        strings = strings,
                        onStart = { onIntent(SelfDiagnosisIntent.StartDiagnosis) },
                        onSkip = if (fromOnboarding) {
                            { onIntent(SelfDiagnosisIntent.FinishSetup) }
                        } else {
                            null
                        }
                    )
                }
                else -> {
                    QuestionContent(uiState = uiState, strings = strings, onIntent = onIntent)
                }
            }
        }
    }
}

@Composable
private fun IntroContent(strings: SelfDiagnosisStrings, onStart: () -> Unit, onSkip: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.self_diagnosis_start),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1536f / 1024f)
                .clip(RoundedCornerShape(20.dp))
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = strings.introTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = strings.introDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = strings.introDuration,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        NoticeBanner(text = strings.commonSafetyNotice, isWarning = false)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
        ) {
            Text(text = strings.introStartButton, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        if (onSkip != null) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onSkip) {
                Text(text = strings.introSkipButton, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun QuestionContent(
    uiState: SelfDiagnosisUiState,
    strings: SelfDiagnosisStrings,
    onIntent: (SelfDiagnosisIntent) -> Unit
) {
    val questions = diagnosisQuestions(strings.questions)
    val question = questions[uiState.currentQuestionIndex]
    val selectedOptions = uiState.selectedAnswers[question.id].orEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier.fillMaxWidth(),
            color = CoralPrimary,
            trackColor = BorderColor
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = strings.questionProgressFormat.format(uiState.currentQuestionIndex + 1, questions.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
            item {
                Text(
                    text = question.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            item {
                Text(
                    text = question.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            items(question.options) { option ->
                AnswerOptionCard(
                    label = option.label(strings.options),
                    isMultiSelect = question.isMultiSelect,
                    isSelected = option in selectedOptions,
                    onClick = { onIntent(SelfDiagnosisIntent.SelectAnswer(question.id, option)) }
                )
            }
            if (question.noticeText != null) {
                item { NoticeBanner(text = question.noticeText, isWarning = false) }
            }
            item {
                Button(
                    onClick = { onIntent(SelfDiagnosisIntent.GoNext) },
                    enabled = uiState.canGoNext,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = Color.White)
                ) {
                    Text(
                        text = if (uiState.isLastQuestion) strings.viewResultButton else strings.nextButton,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerOptionCard(
    label: String,
    isMultiSelect: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) CoralPrimary else BorderColor
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(width = if (isSelected) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMultiSelect) {
                CheckboxIndicator(isSelected = isSelected)
            } else {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = CoralPrimary)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun CheckboxIndicator(isSelected: Boolean) {
    val backgroundColor = if (isSelected) CoralPrimary else Color.Transparent
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(width = 1.dp, color = if (isSelected) CoralPrimary else BorderColor, shape = RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
