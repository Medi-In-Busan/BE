package com.mediinbusan.app.feature.selfdiagnosis

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.i18n.ChatStrings
import com.mediinbusan.app.core.i18n.DiagnosisAnswerOptionStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * S-XX 준비 유형 진단 챗봇. 자유 텍스트 + 추천 답변 칩으로 5개 슬롯(방문목적/체류기간/예약상태/
 * 통역필요/입국체류조건)을 채우면 서버가 판정한 준비 유형(TYPE A~E) 결과를 보여준다.
 */
@Composable
fun SelfDiagnosisScreen(
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

    SelfDiagnosisContent(uiState = uiState, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelfDiagnosisContent(
    uiState: SelfDiagnosisUiState,
    onIntent: (SelfDiagnosisIntent) -> Unit
) {
    val strings = LocalAppStrings.current.selfDiagnosis
    val chatStrings = LocalAppStrings.current.chat

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
            if (resultType != null) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        DiagnosisResultContent(
                            resultType = resultType,
                            onCtaClick = { target -> onIntent(SelfDiagnosisIntent.ClickCta(target)) },
                            onRestart = { onIntent(SelfDiagnosisIntent.Restart) },
                            onGoHome = { onIntent(SelfDiagnosisIntent.FinishSetup) },
                            goHomeButtonLabel = strings.backToHomeButton
                        )
                    }
                }
            } else {
                ChatContent(
                    uiState = uiState,
                    chatStrings = chatStrings,
                    optionStrings = strings.options,
                    onIntent = onIntent
                )
            }
        }
    }
}

@Composable
private fun ChatContent(
    uiState: SelfDiagnosisUiState,
    chatStrings: ChatStrings,
    optionStrings: DiagnosisAnswerOptionStrings,
    onIntent: (SelfDiagnosisIntent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        // 고정 인사말(1) + 대화 메시지 + 로딩 버블(있으면) + 에러 배너(있으면). 새 항목이 생길
        // 때마다 맨 아래로 스크롤한다.
        val totalItemCount = 1 + uiState.messages.size + (if (uiState.isLoading) 1 else 0) + (if (uiState.hasError) 1 else 0)
        LaunchedEffect(totalItemCount) {
            if (totalItemCount > 0) listState.animateScrollToItem(totalItemCount - 1)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { ChatBubble(role = ChatMessageRole.ASSISTANT, text = chatStrings.greetingMessage) }
            items(uiState.messages) { message -> ChatBubble(role = message.role, text = message.text) }
            if (uiState.isLoading) {
                item { TypingIndicatorBubble() }
            }
            if (uiState.hasError) {
                item { NoticeBanner(text = chatStrings.errorMessage, isWarning = true) }
            }
        }

        val suggestedOptions = uiState.suggestedOptions
        if (!uiState.isLoading && suggestedOptions != null) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedOptions) { option ->
                    val label = option.label(optionStrings)
                    SuggestedReplyChip(label = label, onClick = { onIntent(SelfDiagnosisIntent.TapSuggestedReply(label)) })
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        ChatInputBar(
            value = uiState.inputText,
            enabled = !uiState.isLoading,
            placeholder = chatStrings.inputPlaceholder,
            sendContentDescription = chatStrings.sendButtonContentDescription,
            onValueChange = { onIntent(SelfDiagnosisIntent.UpdateInputText(it)) },
            onSend = { onIntent(SelfDiagnosisIntent.SendMessage(uiState.inputText)) }
        )
    }
}

@Composable
private fun ChatBubble(role: ChatMessageRole, text: String) {
    val isUser = role == ChatMessageRole.USER
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) CoralPrimary else Color.White,
            border = if (isUser) null else BorderStroke(1.dp, BorderColor)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                color = if (isUser) Color.White else TextPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TypingIndicatorBubble() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = CoralPrimary)
            }
        }
    }
}

@Composable
private fun SuggestedReplyChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(percent = 50),
        color = Color.White,
        border = BorderStroke(1.dp, CoralPrimary)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = CoralPrimary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    enabled: Boolean,
    placeholder: String,
    sendContentDescription: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val canSend = enabled && value.isNotBlank()
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(text = placeholder) },
            enabled = enabled,
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { if (value.isNotBlank()) onSend() })
        )
        IconButton(
            onClick = onSend,
            enabled = canSend,
            modifier = Modifier
                .size(48.dp)
                .background(if (canSend) CoralPrimary else BorderColor, CircleShape)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = sendContentDescription, tint = Color.White)
        }
    }
}
