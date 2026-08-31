package com.mediinbusan.app.feature.selfdiagnosis

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mediinbusan.app.R
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.StatusOpenGreen
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.ChatStrings
import com.mediinbusan.app.core.i18n.DiagnosisAnswerOptionStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        containerColor = HomeBackgroundPink,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 카드 느낌의 원형 프로필 — 홈 FAB(AiChatFab)과 같은 흰 배경 + 테두리
                            // 패턴을 재사용하되, 탑바 자리에 맞게 더 크게(44dp) 키웠다.
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(width = 1.dp, color = BorderColor, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.home_chatbot),
                                    contentDescription = null,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = strings.topBarTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    // 스탠다드한 "활동 중" 점 — 단색 대신 민트→그린 그라데이션 +
                                    // 얇은 흰 테두리를 줘서 좀 더 트렌디한 배지 느낌을 낸다.
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF6EE7B7), StatusOpenGreen)
                                                )
                                            )
                                            .border(width = 1.dp, color = Color.White, shape = CircleShape)
                                    )
                                }
                                Text(
                                    text = strings.topBarSubtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { onIntent(SelfDiagnosisIntent.ClickBack) }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.backContentDescription)
                        }
                    },
                    // 아이콘/텍스트 크기는 그대로 두고, 상태바 인셋만큼 생기는 탑바 위쪽 여백만
                    // 줄인다(core/ui/BrandTopAppBar.kt·HomeScreen.kt의 HomeTopAppBar와 동일한 값).
                    windowInsets = WindowInsets.statusBars.exclude(WindowInsets(top = 14.dp)),
                    // 본문(HomeBackgroundPink)과 달리 탑바는 흰색 카드처럼 계속 도드라지게 둔다.
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                // 슬롯 4개(방문목적/체류기간/예약상태/통역필요) 중 채워진 개수만큼 자라나는
                // 진행 바 — HospitalDetailScreen 탭 밑줄과 같은 코랄/회색 톤을 그대로 쓴다.
                // 결과가 나오면(모든 슬롯이 찼다는 뜻) 자연히 꽉 찬 상태로 보인다.
                val completedSteps = listOfNotNull(
                    uiState.slots.visitPurpose,
                    uiState.slots.stayDuration,
                    uiState.slots.reservationStatus,
                    uiState.slots.interpretationNeed
                ).size
                val totalSteps = 4
                // 흰 탑바 카드 안쪽에 여전히 속해 있되, 좌우/아래 여백을 둬서 탑바-본문 경계선
                // 처럼 보이지 않게 한다. 위치 자체(탑바 바로 밑)는 그대로 고정.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChatStepProgressBar(
                        currentStep = completedSteps,
                        totalSteps = totalSteps,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = String.format(chatStrings.stepProgressFormat, completedSteps, totalSteps),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    ) { innerPadding ->
        // 채팅 입력창이 키보드 위로 밀려 올라오도록 imePadding을 준다 — 결과 화면(스크롤
        // 리스트만 있음)에는 영향 없고, 대화 화면의 ChatInputBar가 Column 맨 아래에 있어
        // 키보드가 올라오면 그만큼 이 Box 콘텐츠 영역이 줄어들며 입력창도 함께 올라온다.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
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
        // 시간 라벨(1) + 고정 인사말(1) + 대화 메시지(응답 대기 중인 로딩 말풍선도 이 안에 포함) +
        // 에러 배너(있으면). 새 항목이 생길 때마다 맨 아래로 스크롤한다.
        val totalItemCount = 2 + uiState.messages.size + (if (uiState.hasError) 1 else 0)
        LaunchedEffect(totalItemCount) {
            if (totalItemCount > 0) listState.animateScrollToItem(totalItemCount - 1)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 챗봇을 처음 열었을 때 한 번만, 화면 중앙에 "오늘 오후 3:12" 같은 시각 안내를
            // 띄운다 — 메시지마다 매번 시간을 찍지 않는다.
            item(key = "opened-at") {
                ChatOpenedTimeLabel(language = LocalAppStrings.current.language, todayLabel = chatStrings.todayLabel)
            }
            item(key = "greeting") { ChatBubble(role = ChatMessageRole.ASSISTANT, text = chatStrings.greetingMessage) }
            items(uiState.messages, key = { it.id }) { message ->
                ChatBubble(role = message.role, text = message.text, isPending = message.isPending)
            }
            if (uiState.hasError) {
                item(key = "error-banner") { NoticeBanner(text = chatStrings.errorMessage, isWarning = true) }
            }
        }

        val suggestedOptions = uiState.suggestedOptions
        if (!uiState.isLoading && suggestedOptions != null) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedOptions) { option ->
                    val label = option.label(optionStrings)
                    SuggestedReplyChip(label = label, onClick = { onIntent(SelfDiagnosisIntent.TapSuggestedReply(option, label)) })
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
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

// 탑바 밑에 붙는 진행 바. HospitalDetailScreen 섹션 탭의 밑줄(코랄 활성/회색 비활성, 2dp)과
// 같은 톤이다 — 처음엔 색 없는 회색 일자 선이었다가, 슬롯이 하나씩 채워질 때마다 코랄색으로
// 그만큼 길어진다.
@Composable
private fun ChatStepProgressBar(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    val targetFraction = if (totalSteps == 0) 0f else (currentStep.toFloat() / totalSteps).coerceIn(0f, 1f)
    val fraction by animateFloatAsState(targetValue = targetFraction, animationSpec = tween(400), label = "chatStepProgress")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(DividerColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction)
                .background(CoralPrimary)
        )
    }
}

// 언어별로 자연스러운 오전/오후 표기까지 시스템이 알아서 골라주도록 java.text 포맷을 쓴다
// (minSdk 24라 java.time은 desugaring 없이 못 씀). "오늘 오후 3:12"처럼 챗봇을 처음 열 때
// 한 번만, 화면 중앙에 보여준다.
@Composable
private fun ChatOpenedTimeLabel(language: SupportedLanguage, todayLabel: String) {
    val timeText = remember(language) {
        val pattern = if (language == SupportedLanguage.EN) "h:mm a" else "a h:mm"
        SimpleDateFormat(pattern, localeFor(language)).format(Date())
    }
    Text(
        text = "$todayLabel $timeText",
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondary,
        textAlign = TextAlign.Center
    )
}

private fun localeFor(language: SupportedLanguage): Locale = when (language) {
    SupportedLanguage.KO -> Locale.KOREAN
    SupportedLanguage.EN -> Locale.ENGLISH
    SupportedLanguage.ZH -> Locale.CHINESE
    SupportedLanguage.JA -> Locale.JAPANESE
}

// 챗봇 쪽 말풍선 옆(왼쪽 하단)에 붙는 프로필 이미지. 유저 말풍선은 아바타 없이 기존 그대로 둔다.
@Composable
private fun ChatbotAvatar() {
    Image(
        painter = painterResource(id = R.drawable.home_chatbot),
        contentDescription = null,
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(width = 1.dp, color = BorderColor, shape = CircleShape)
    )
}

// 말풍선이 리스트에 새로 들어올 때만 한 번 재생되는 팝인 커브. CSS의
// cubic-bezier(0.34, 1.56, 0.64, 1)(살짝 오버슈트하며 튀는 느낌)을 그대로 옮겼다.
private val BubblePopEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
private const val BubblePopDurationMs = 420
private const val BubblePopStartScale = 0.82f

@Composable
private fun ChatBubble(role: ChatMessageRole, text: String, isPending: Boolean = false) {
    val isUser = role == ChatMessageRole.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            ChatbotAvatar()
            Spacer(modifier = Modifier.width(6.dp))
        }
        // remember는 이 말풍선이 LazyColumn에 같은 key로 남아있는 한 유지된다 — 로딩 말풍선의
        // 내용만 답변으로 채워지는 경우(같은 id)는 이 Animatable이 새로 시작되지 않고 이미
        // 1f에 도달해 있으므로 팝 애니메이션이 다시 재생되지 않는다. 완전히 새 메시지가 들어올
        // 때만(새 key로 컴포지션에 처음 들어올 때) 0에서 시작해 재생된다.
        val popProgress = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            popProgress.animateTo(1f, animationSpec = tween(BubblePopDurationMs, easing = BubblePopEasing))
        }
        Surface(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .graphicsLayer {
                    alpha = popProgress.value
                    val scale = BubblePopStartScale + (1f - BubblePopStartScale) * popProgress.value
                    scaleX = scale
                    scaleY = scale
                    // 유저 말풍선은 오른쪽 아래, 챗봇 말풍선은 왼쪽 아래를 기준점 삼아 튀어나온다
                    // (CSS의 transform-origin: 0 100% / 100% 100%와 동일).
                    transformOrigin = TransformOrigin(if (isUser) 1f else 0f, 1f)
                },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) CoralPrimary else Color.White,
            border = if (isUser) null else BorderStroke(1.dp, BorderColor)
        ) {
            // 로딩 점 3개 -> 실제 답변 텍스트로의 전환(같은 말풍선, 같은 key 안에서 내용만
            // 크로스페이드) — 챗봇 응답을 기다리는 동안에만 쓰이는 "인디케이터 → 본문" 패턴.
            AnimatedContent(
                targetState = isPending,
                label = "chatBubbleContent",
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(180)) }
            ) { pending ->
                if (pending) {
                    TypingDots()
                } else {
                    Text(
                        text = text,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color = if (isUser) Color.White else TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// 각 점이 0.18초씩 시차를 두고 커졌다 작아지기를 반복한다(1.1초 주기, 35% 지점에서 최대 크기).
private const val TypingDotCycleMs = 1100
private const val TypingDotStaggerMs = 180

@Composable
private fun TypingDots() {
    val transition = rememberInfiniteTransition(label = "typingDots")
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by transition.animateFloat(
                initialValue = 0.7f,
                targetValue = 0.7f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = TypingDotCycleMs
                        0.7f at 0
                        1.25f at (TypingDotCycleMs * 0.35f).toInt()
                        0.7f at (TypingDotCycleMs * 0.7f).toInt()
                        0.7f at TypingDotCycleMs
                    },
                    initialStartOffset = StartOffset(index * TypingDotStaggerMs)
                ),
                label = "typingDotScale$index"
            )
            val dotAlpha by transition.animateFloat(
                initialValue = 0.4f,
                targetValue = 0.4f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = TypingDotCycleMs
                        0.4f at 0
                        1f at (TypingDotCycleMs * 0.35f).toInt()
                        0.4f at (TypingDotCycleMs * 0.7f).toInt()
                        0.4f at TypingDotCycleMs
                    },
                    initialStartOffset = StartOffset(index * TypingDotStaggerMs)
                ),
                label = "typingDotAlpha$index"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = dotAlpha
                    }
                    .background(TextSecondary, CircleShape)
            )
        }
    }
}

@Composable
private fun SuggestedReplyChip(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // 핑크(CoralPrimary)는 '내가 보낸 답변' 말풍선에만 남기고, 아직 선택 전인 제안 칩은
    // 중립 hairline 테두리 + 진한 텍스트로 가장 조용하게 보여준다.
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = TextPrimary,
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
    var isFocused by remember { mutableStateOf(false) }
    // 입력칸과 전송 화살표를 하나의 알약형 영역으로 합친다. 화살표는 배경 없이 아이콘만
    // 두고 항상 같은 자리에 있으며, 포커스 여부에 따라 각도만 회전한다(아래 rotation 참고).
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(width = 1.dp, color = BorderColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(text = placeholder, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                enabled = enabled,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                cursorBrush = SolidColor(CoralPrimary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (value.isNotBlank()) onSend() })
            )
        }
        // chatbot_answer 이미지를 원본 색 그대로 전송 버튼으로 쓴다. 평소(포커스 전)엔
        // 완전히 숨어 있다가(alpha 0, 살짝 작은 크기), 입력칸에 포커스가 잡히면 통통 튀듯
        // 커지면서 페이드인한다 — 단순 알파 변화보다 눈에 띄게 스프링으로 팝인시킨다.
        val focusTransition by animateFloatAsState(
            targetValue = if (isFocused) 1f else 0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "sendButtonFocusTransition"
        )
        IconButton(
            onClick = onSend,
            enabled = canSend,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(48.dp)
                .graphicsLayer {
                    alpha = focusTransition
                    scaleX = focusTransition
                    scaleY = focusTransition
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.chatbot_answer),
                contentDescription = sendContentDescription,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

// ViewModel(hiltViewModel) 없이 결과 화면 상태를 흉내 낸 미리보기 — 에뮬레이터 없이 확인할 때 사용.
// 5개 유형 전부: Android Studio에서 이 파일을 Split/Design 뷰로 열면 아래 5개가 세로로 나란히
// 렌더링된다 — 프리뷰 패널을 스크롤해서 보면 된다(코드 스크롤과 별개).
@Preview(showBackground = true, heightDp = 1600, name = "TYPE A 직접 문의형")
@Composable
private fun SelfDiagnosisContentResultTypeAPreview() {
    MediInBusanTheme {
        SelfDiagnosisContent(
            uiState = SelfDiagnosisUiState(resultType = DiagnosisResultType.TYPE_A),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1600, name = "TYPE B 국제진료센터 문의형")
@Composable
private fun SelfDiagnosisContentResultTypeBPreview() {
    MediInBusanTheme {
        SelfDiagnosisContent(
            uiState = SelfDiagnosisUiState(resultType = DiagnosisResultType.TYPE_B),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1600, name = "TYPE C 등록 유치기관 확인형")
@Composable
private fun SelfDiagnosisContentResultTypeCPreview() {
    MediInBusanTheme {
        SelfDiagnosisContent(
            uiState = SelfDiagnosisUiState(resultType = DiagnosisResultType.TYPE_C),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1600, name = "TYPE D 장기치료·비자확인형")
@Composable
private fun SelfDiagnosisContentResultTypeDPreview() {
    MediInBusanTheme {
        SelfDiagnosisContent(
            uiState = SelfDiagnosisUiState(resultType = DiagnosisResultType.TYPE_D),
            onIntent = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1600, name = "TYPE E 관광 중심 웰니스 체험형")
@Composable
private fun SelfDiagnosisContentResultTypeEPreview() {
    MediInBusanTheme {
        SelfDiagnosisContent(
            uiState = SelfDiagnosisUiState(resultType = DiagnosisResultType.TYPE_E),
            onIntent = {}
        )
    }
}

/** 대화 중(결과 나오기 전) 상태 미리보기. */
@Preview(showBackground = true, heightDp = 900)
@Composable
private fun SelfDiagnosisContentChatPreview() {
    MediInBusanTheme {
        SelfDiagnosisContent(
            uiState = SelfDiagnosisUiState(
                messages = listOf(
                    ChatMessage(role = ChatMessageRole.ASSISTANT, text = "안녕하세요! 방문 목적을 알려주세요.")
                )
            ),
            onIntent = {}
        )
    }
}
