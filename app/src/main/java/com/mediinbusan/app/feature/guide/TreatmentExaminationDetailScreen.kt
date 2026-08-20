package com.mediinbusan.app.feature.guide

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
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.i18n.TreatmentBriefingDefaultsStrings
import com.mediinbusan.app.core.i18n.TreatmentExaminationStrings
import com.mediinbusan.app.data.guide.TreatmentBriefing
import com.mediinbusan.app.data.guide.TreatmentBriefingField

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
                GuideStepSectionHeader(title = s.briefingSectionTitle, modifier = Modifier.padding(bottom = 14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, CoralPrimary.copy(alpha = 0.28f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        fields.forEachIndexed { index, field ->
                            BriefingInfoRow(
                                field = field,
                                value = field.valueOf(briefing),
                                placeholder = field.defaultValue,
                                editContentDescription = s.editContentDescription,
                                saveContentDescription = s.saveContentDescription,
                                isEditing = editingIndex == index,
                                onStartEdit = { editingIndex = index },
                                onSave = { newValue -> viewModel.updateField(field.field, newValue) },
                                onFinishEdit = { editingIndex = null }
                            )
                            if (index != fields.lastIndex) {
                                HorizontalDivider(color = CoralPrimary.copy(alpha = 0.28f))
                            }
                        }
                    }
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
