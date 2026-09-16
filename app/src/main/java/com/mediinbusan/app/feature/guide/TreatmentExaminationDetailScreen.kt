package com.mediinbusan.app.feature.guide

import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mediinbusan.app.R
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.TreatmentBriefingDefaultsStrings
import com.mediinbusan.app.core.i18n.TreatmentExaminationStrings
import com.mediinbusan.app.data.guide.TreatmentBriefing
import com.mediinbusan.app.data.guide.TreatmentBriefingField
import com.mediinbusan.app.data.guide.TreatmentBriefingTranslation
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class BriefingField(
    val label: String,
    val field: TreatmentBriefingField,
    val valueOf: (TreatmentBriefing) -> String,
    // 사용자가 아직 값을 입력하지 않았을 때(빈 문자열) 보여줄 언어별 예시 문구. 실제 입력값은 그대로 유지된다.
    val defaultValue: String
)

private fun briefingFields(s: TreatmentExaminationStrings, defaults: TreatmentBriefingDefaultsStrings): List<BriefingField> = listOf(
    BriefingField(s.briefingLabelVisitPurpose, TreatmentBriefingField.VISIT_PURPOSE, { it.visitPurpose }, defaults.visitPurpose),
    BriefingField(s.briefingLabelSymptoms, TreatmentBriefingField.SYMPTOMS, { it.symptoms }, defaults.symptoms),
    BriefingField(s.briefingLabelAllergy, TreatmentBriefingField.ALLERGY, { it.allergy }, defaults.allergy),
    BriefingField(s.briefingLabelMedication, TreatmentBriefingField.MEDICATION, { it.medication }, defaults.medication),
    BriefingField(s.briefingLabelReturnDate, TreatmentBriefingField.RETURN_DATE, { it.returnDate }, defaults.returnDate),
    BriefingField(s.briefingLabelMemo, TreatmentBriefingField.MEMO, { it.memo }, defaults.memo)
)

// returnDate(귀국·체류 일정)는 날짜 데이터라 번역 대상이 아니다 — null을 반환해 원문만 보이게 한다.
private fun translatedValueFor(field: TreatmentBriefingField, translation: TreatmentBriefingTranslation): String? =
    when (field) {
        TreatmentBriefingField.VISIT_PURPOSE -> translation.visitPurpose
        TreatmentBriefingField.SYMPTOMS -> translation.symptoms
        TreatmentBriefingField.ALLERGY -> translation.allergy
        TreatmentBriefingField.MEDICATION -> translation.medication
        TreatmentBriefingField.RETURN_DATE -> null
        TreatmentBriefingField.MEMO -> translation.memo
    }

// 다른 STEP의 메모지 카드 섹션과 동일하게 GuideMemoRow로 그리되, 항목별 삽화·배경을 명시적으로 지정한다.
private fun todayChecklistItems(s: TreatmentExaminationStrings): List<GuideDetailItem> = listOf(
    GuideDetailItem(
        id = "today_1",
        iconResId = R.drawable.guide_medical_document,
        title = s.todayItem1Title,
        description = s.todayItem1Description,
        memoIllustrationResId = R.drawable.guide_treatment_exam_name,
        memoBackgroundResId = R.drawable.guide_memo6
    ),
    GuideDetailItem(
        id = "today_2",
        iconResId = R.drawable.guide_medical_document,
        title = s.todayItem2Title,
        description = s.todayItem2Description,
        memoIllustrationResId = R.drawable.guide_exam_caution,
        memoBackgroundResId = R.drawable.guide_memo4
    ),
    GuideDetailItem(
        id = "today_3",
        iconResId = R.drawable.guide_medical_document,
        title = s.todayItem3Title,
        description = s.todayItem3Description,
        memoIllustrationResId = R.drawable.guide_result_receipt,
        memoBackgroundResId = R.drawable.guide_memo8
    )
)

// "방문 병원에 문의"는 별도 카드/텍스트 없이 하단 MEDIN TIP 배너 문구 자체로 대체됐다 —
// 실제 외부 링크가 있는 Medical Korea만 예약 및 문의(STEP02)의 "공식 사이트" 카드
// (GuideOfficialLinkRow/GuideOfficialLinkCard)와 동일한 디자인으로 카드 하나만 남긴다.
private fun inquiryItems(s: TreatmentExaminationStrings): List<GuideDetailItem> = listOf(
    GuideDetailItem(
        id = "inquiry_2",
        iconResId = R.drawable.guide_medical_korea_guide,
        title = s.inquiry2Title,
        description = s.inquiry2Description,
        url = "https://www.medicalkorea.or.kr/",
        // 카드가 하나뿐이라 기본 위치 기반 강조색(첫 카드=코랄)을 쓰면 다른 STEP의 "공식 사이트" 카드와
        // 색이 달라 보인다 — "공식 사이트" 배지·바로가기 버튼 색을 스카이블루로 고정한다.
        accentColor = SkyBlue
    )
)

// S-06 하위 STEP04 상세 (진료 및 검사). 브리핑 카드 섹션 형태가 STEP01~03과 달라 공용 GuideStepDetailScreen을 쓰지 않고 전용 화면으로 구현.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentExaminationDetailScreen(
    onBack: () -> Unit,
    viewModel: TreatmentExaminationViewModel = hiltViewModel()
) {
    val briefing by viewModel.briefing.collectAsStateWithLifecycle()
    val translationUiState by viewModel.translationUiState.collectAsStateWithLifecycle()
    var editingIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val appStrings = LocalAppStrings.current
    val guideStrings = appStrings.guide
    val s = guideStrings.treatmentExamination
    val fields = briefingFields(s, guideStrings.treatmentBriefingDefaults)
    val checklist = todayChecklistItems(s)
    val inquiries = inquiryItems(s)

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
                        Text(text = "04", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CoralPrimary)
                        Text(
                            text = " ${guideStrings.stepTreatmentExaminationTitle}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
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
            GuideStepHero(
                heroResId = R.drawable.guide_step04_treatment_examination_banner,
                stepTitle = guideStrings.stepTreatmentExaminationTitle,
                stepSubtitle = guideStrings.stepTreatmentExaminationHeroSubtitle,
                modifier = Modifier.padding(top = 16.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // GuideStepSectionHeader가 내부적으로 modifier.fillMaxWidth()를 호출하므로
                    // weight(1f)로 폭을 먼저 제한하지 않으면 이 Row를 통째로 차지해 옆 버튼이 밀려난다.
                    GuideStepSectionHeader(title = s.briefingSectionTitle, modifier = Modifier.weight(1f))
                    TranslateToKoreanButton(
                        uiState = translationUiState,
                        translateLabel = s.translateToKoreanButtonLabel,
                        hideLabel = s.hideTranslationButtonLabel,
                        onTranslate = { viewModel.translateToKorean() },
                        onDismiss = { viewModel.dismissTranslation() }
                    )
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CoralPrimary.copy(alpha = 0.28f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        fields.forEachIndexed { index, field ->
                            if (field.field == TreatmentBriefingField.RETURN_DATE) {
                                // 날짜는 DatePicker로 고르고 ISO(yyyy-MM-dd)로 저장한다 — 자유 텍스트 편집을
                                // 쓰는 BriefingInfoRow와는 입력 방식이 아예 달라 별도 컴포저블로 분리한다.
                                ReturnDateInfoRow(
                                    label = field.label,
                                    rawValue = field.valueOf(briefing),
                                    placeholder = field.defaultValue,
                                    language = appStrings.language,
                                    editContentDescription = s.editContentDescription,
                                    confirmLabel = s.datePickerConfirmLabel,
                                    cancelLabel = s.datePickerCancelLabel,
                                    onDateSelected = { isoDate -> viewModel.updateField(field.field, isoDate) }
                                )
                            } else {
                                BriefingInfoRow(
                                    field = field,
                                    value = field.valueOf(briefing),
                                    placeholder = field.defaultValue,
                                    translatedValue = translationUiState.translation?.let { translatedValueFor(field.field, it) },
                                    editContentDescription = s.editContentDescription,
                                    saveContentDescription = s.saveContentDescription,
                                    isEditing = editingIndex == index,
                                    onStartEdit = { editingIndex = index },
                                    onSave = { newValue -> viewModel.updateField(field.field, newValue) },
                                    onFinishEdit = { editingIndex = null }
                                )
                            }
                            if (index != fields.lastIndex) {
                                HorizontalDivider(color = CoralPrimary.copy(alpha = 0.28f))
                            }
                        }
                    }
                }
                if (translationUiState.isTranslationError) {
                    Text(
                        text = s.translationErrorMessage,
                        style = MaterialTheme.typography.labelMedium,
                        color = CoralPrimary,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideStepSectionHeader(title = s.todayChecklistTitle, modifier = Modifier.padding(bottom = 16.dp))
                GuideMemoRow(items = checklist, onNavigableClick = {})
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideStepSectionHeader(title = s.inquirySectionTitle, modifier = Modifier.padding(bottom = 16.dp))
                GuideOfficialLinkRow(
                    items = inquiries,
                    officialSiteLabel = guideStrings.officialSiteBadgeLabel,
                    visitSiteLabel = guideStrings.visitSiteButtonLabel,
                    onNavigableClick = {}
                )
            }

            GuideStepTipBanner(
                label = guideStrings.medinTipLabel,
                tipLead = guideStrings.stepTreatmentExaminationTipLead,
                tipHighlight = guideStrings.stepTreatmentExaminationTipHighlight,
                modifier = Modifier.padding(top = 28.dp, bottom = 28.dp)
            )
        }
    }
}

// 라벨은 위, 값은 아래로 세로 배치해서 값이 길어져도 줄바꿈되며 깨지지 않는다(반응형).
// 편집 중 입력값(draft)은 이 행 안에서만 remember한다 — 상위 화면 상태로 올리면 키 입력마다
// 화면 전체가 리컴포지션되면서 한글 조합(IME composing) 상태가 끊겨 완성된 글자가 반영되지 않는다.
// onSave(저장)와 onFinishEdit(편집 종료)를 분리한다 — 포커스 손실 시 저장만 하고 editingIndex는
// 건드리지 않아야, 다른 행을 선택해 편집을 이어가는 경우에도 방금 시작한 편집이 취소되지 않는다.
@Composable
private fun BriefingInfoRow(
    field: BriefingField,
    value: String,
    placeholder: String,
    translatedValue: String?,
    editContentDescription: String,
    saveContentDescription: String,
    isEditing: Boolean,
    onStartEdit: () -> Unit,
    onSave: (String) -> Unit,
    onFinishEdit: () -> Unit
) {
    val rowModifier = if (isEditing) Modifier else Modifier.clickable(onClick = onStartEdit)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(rowModifier)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(text = field.label, style = MaterialTheme.typography.labelMedium, color = CoralPrimary)
            if (isEditing) {
                // TextFieldValue로 관리해야 IME의 조합 중(composition) 범위가 리컴포지션 사이에도
                // 유지된다 — 한글처럼 자모를 조합해 완성하는 입력 방식에서 필수.
                // rememberSaveable로 구성 변경(화면 회전 등)에도 입력 중이던 값을 보존한다.
                var draft by rememberSaveable(field.field, stateSaver = TextFieldValue.Saver) {
                    mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
                }
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) { focusRequester.requestFocus() }

                fun finishEdit() {
                    onSave(draft.text)
                    onFinishEdit()
                }

                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .focusRequester(focusRequester)
                        // 다른 행을 선택하는 등 포커스를 잃는 모든 경우에 값을 저장한다.
                        .onFocusChanged { if (!it.isFocused) onSave(draft.text) },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { finishEdit() }),
                    placeholder = { Text(text = placeholder, style = MaterialTheme.typography.bodyMedium, color = TextSecondary) },
                    trailingIcon = {
                        IconButton(onClick = { finishEdit() }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = saveContentDescription, tint = CoralPrimary)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = CoralPrimary,
                        unfocusedIndicatorColor = BorderColor
                    )
                )
            } else {
                Text(
                    text = value.ifBlank { placeholder },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                // 원문은 절대 번역문으로 덮어쓰지 않는다 — 라벨 없이 원문 바로 아래에 병기만 한다.
                if (!translatedValue.isNullOrBlank()) {
                    Text(
                        text = translatedValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        if (!isEditing) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = editContentDescription,
                tint = CoralPrimary,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(16.dp)
            )
        }
    }
}

// Text+clickable로 직접 그린다 — Material3 TextButton은 48dp 최소 터치 영역을 강제해서
// 옆에 있는 GuideStepSectionHeader보다 Row 전체 높이가 훨씬 커지고, 그만큼 아래 카드와의
// 여백도 벌어져 보인다(BriefingInfoRow가 이미 Row 자체에 clickable을 다는 것과 같은 이유로 이 패턴을 쓴다).
@Composable
private fun TranslateToKoreanButton(
    uiState: TreatmentTranslationUiState,
    translateLabel: String,
    hideLabel: String,
    onTranslate: () -> Unit,
    onDismiss: () -> Unit
) {
    when {
        uiState.isTranslating -> CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = CoralPrimary,
            strokeWidth = 2.dp
        )
        uiState.translation != null -> Text(
            text = hideLabel,
            style = MaterialTheme.typography.labelMedium,
            color = CoralPrimary,
            modifier = Modifier.clickable(onClick = onDismiss)
        )
        else -> Text(
            text = translateLabel,
            style = MaterialTheme.typography.labelMedium,
            color = CoralPrimary,
            modifier = Modifier.clickable(onClick = onTranslate)
        )
    }
}

// BriefingInfoRow와 같은 라벨-위/값-아래 레이아웃을 쓰되, 편집 방식만 날짜 선택으로 바꾼다.
// Compose Material3의 DatePicker는 쓰지 않는다 — 이 프로젝트 의존성 그래프에서 androidx.compose.material3
// 버전이 뒤섞여 있어(§8.6 FlowRow와 동일한 원인) 컴파일은 통과해도 실기기에서
// NoSuchMethodError로 즉시 크래시했다(실제로 확인함). camera-compose 대신 camera-view를 쓰는 것과
// 같은 이유로, Compose 버전에 안 묶이는 플랫폼 기본 android.app.DatePickerDialog로 대체한다.
// rawValue는 ISO(yyyy-MM-dd) 문자열로 저장되고, 화면에는 현재 앱 언어에 맞는 자연스러운 표기로 보여준다.
@Composable
private fun ReturnDateInfoRow(
    label: String,
    rawValue: String,
    placeholder: String,
    language: SupportedLanguage,
    editContentDescription: String,
    confirmLabel: String,
    cancelLabel: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val displayValue = remember(rawValue, language) { formatReturnDateForDisplay(rawValue, language) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showReturnDatePicker(context, rawValue, language, confirmLabel, cancelLabel, onDateSelected) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = CoralPrimary)
            Text(
                text = displayValue ?: placeholder,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = editContentDescription,
            tint = CoralPrimary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
        )
    }
}

// 플랫폼 기본 다이얼로그는 기본적으로 Context의 로케일(=기기 시스템 언어)을 따른다 — 이 앱은 시스템
// 언어가 아니라 상단바 언어 드롭다운으로 언어를 관리하므로(core/i18n은 리소스 로케일이 아니라 수동 조회
// 방식), 그 상태를 반영하려면 로케일을 덮어쓴 Context를 직접 만들어 넘겨야 한다. 이렇게 하면 달력
// 헤더·요일·월 이름까지 앱이 선택한 언어를 따른다(확인/취소 버튼 문구는 원래도 앱 언어를 썼다).
//
// context.createConfigurationContext(...)는 절대 쓰지 않는다 — Activity와 완전히 분리된 새
// ContextImpl을 반환해서 윈도우 토큰이 없어지고, 그 Context로 Dialog.show()를 부르면
// WindowManager.BadTokenException("token null is not valid")로 즉시 크래시한다(실기기에서 확인함).
// LocaleContextWrapper는 getResources()만 오버라이드하고 나머지(getSystemService 등)는 전부
// 원래 Activity Context에 위임하는 ContextWrapper라 윈도우 토큰이 그대로 살아있다.
//
// 이것만으로는 부족한 기기가 있다(예: 삼성 One UI, 실기기에서 확인함) — 코랄색 헤더 위에 뜨는
// 큰 날짜 텍스트를 Context의 Resources 설정이 아니라 JVM 전역 Locale.getDefault()로 포맷하는
// 제조사 커스텀 DatePicker 구현이 있다. 다이얼로그가 떠 있는 동안만 전역 기본 로케일을 앱 선택
// 언어로 바꾸고 닫히면 원래대로 되돌린다 — 이 파일의 날짜 파싱/포맷은 전부 Locale.US를 명시해서
// 쓰므로 이 전역 변경에 영향받지 않는다.
private fun showReturnDatePicker(
    context: Context,
    rawValue: String,
    language: SupportedLanguage,
    confirmLabel: String,
    cancelLabel: String,
    onDateSelected: (String) -> Unit
) {
    val locale = localeFor(language)
    val previousDefaultLocale = Locale.getDefault()
    Locale.setDefault(locale)

    val localizedContext = LocaleContextWrapper(context, locale)
    val calendar = Calendar.getInstance()
    isoDateToDate(rawValue)?.let { calendar.time = it }

    val dialog = DatePickerDialog(
        localizedContext,
        R.style.ThemeOverlay_MediInBusan_DatePickerDialog,
        { _, year, month, dayOfMonth ->
            val selected = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onDateSelected(dateToIsoDate(selected.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // 귀국·체류 일정은 논리적으로 항상 오늘 이후여야 하므로 과거 날짜는 선택 자체를 막는다.
    dialog.datePicker.minDate = todayMidnight().timeInMillis
    dialog.setButton(DialogInterface.BUTTON_POSITIVE, confirmLabel, dialog)
    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelLabel) { d, _ -> d.dismiss() }
    // 확인/취소/뒤로가기/바깥 탭 등 어떤 경로로 닫히든 onDismiss는 항상 불린다 — 전역 기본 로케일을
    // 앱 전체에 계속 남겨두면 이 화면과 무관한 곳까지 영향을 주므로 반드시 여기서 되돌린다.
    dialog.setOnDismissListener { Locale.setDefault(previousDefaultLocale) }
    dialog.show()
}

private fun isoDateFormat(): SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

// DatePicker 도입 전 자유 텍스트로 저장된 값 등 ISO 형식이 아닌 값은 파싱 실패 시 미설정으로 취급한다.
private fun isoDateToDate(raw: String): Date? =
    if (raw.isBlank()) null else runCatching { isoDateFormat().parse(raw) }.getOrNull()

private fun dateToIsoDate(date: Date): String = isoDateFormat().format(date)

private fun todayMidnight(): Calendar = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

// java.time은 minSdk 24에서 core library desugaring 없이는 못 쓰므로(SelfDiagnosisScreen.localeFor와
// 동일한 이유) java.text.DateFormat으로 언어별 자연스러운 날짜 표기를 만든다.
private fun formatReturnDateForDisplay(raw: String, language: SupportedLanguage): String? {
    val date = isoDateToDate(raw) ?: return null
    return DateFormat.getDateInstance(DateFormat.LONG, localeFor(language)).format(date)
}

private fun localeFor(language: SupportedLanguage): Locale = when (language) {
    SupportedLanguage.KO -> Locale.KOREAN
    SupportedLanguage.EN -> Locale.ENGLISH
    SupportedLanguage.ZH -> Locale.CHINESE
    SupportedLanguage.JA -> Locale.JAPANESE
}

// getResources()만 로케일이 적용된 Resources로 바꿔치기하고 나머지는 base(원래 Activity Context)에
// 그대로 위임한다 — context.createConfigurationContext()와 달리 윈도우 토큰을 잃지 않아 Dialog에 안전하게 쓸 수 있다.
private class LocaleContextWrapper(base: Context, locale: Locale) : ContextWrapper(base) {
    private val localizedResources: Resources = run {
        val configuration = Configuration(base.resources.configuration).apply { setLocale(locale) }
        @Suppress("DEPRECATION")
        Resources(base.assets, base.resources.displayMetrics, configuration)
    }

    override fun getResources(): Resources = localizedResources
}
