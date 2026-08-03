package com.mediinbusan.app.feature.guide

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
import androidx.compose.ui.res.painterResource
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
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.ui.launchIntentSafely
import com.mediinbusan.app.data.guide.TreatmentBriefing
import com.mediinbusan.app.data.guide.TreatmentBriefingField

private data class BriefingField(
    @param:DrawableRes val iconResId: Int,
    val label: String,
    val field: TreatmentBriefingField,
    val valueOf: (TreatmentBriefing) -> String
)

private val BRIEFING_FIELDS = listOf(
    BriefingField(R.drawable.ic_visit_purpose_target, "방문 목적", TreatmentBriefingField.VISIT_PURPOSE) { it.visitPurpose },
    BriefingField(R.drawable.ic_symptoms_face, "현재 증상", TreatmentBriefingField.SYMPTOMS) { it.symptoms },
    BriefingField(
        R.drawable.ic_allergy_pill,
        "알레르기·복용약",
        TreatmentBriefingField.ALLERGY_MEDICATION
    ) { it.allergyMedication },
    BriefingField(R.drawable.ic_return_date_calendar, "귀국 예정일", TreatmentBriefingField.RETURN_DATE) { it.returnDate },
    BriefingField(R.drawable.ic_memo_note, "전달 메모", TreatmentBriefingField.MEMO) { it.memo }
)

private data class TodayChecklistItem(
    @param:DrawableRes val iconResId: Int,
    val title: String,
    val description: String
)

private val TODAY_CHECKLIST = listOf(
    TodayChecklistItem(
        iconResId = R.drawable.ic_examination_name_clipboard,
        title = "정확한 진료·검사 이름",
        description = "오늘 실제로 받는 진료·검사·시술 이름을 확인하세요."
    ),
    TodayChecklistItem(
        iconResId = R.drawable.ic_caution_warning,
        title = "개인별 주의사항",
        description = "금식, 복용약, 일상 복귀, 피해야 할 행동을 확인하세요."
    ),
    TodayChecklistItem(
        iconResId = R.drawable.ic_results_document,
        title = "결과·서류·재문의 방법",
        description = "결과 수령 시점, 영문 서류, 귀국 후 문의 채널을 확인하세요."
    )
)

// S-06 하위 STEP04 상세 (진료 및 검사). 브리핑 카드 섹션 형태가 STEP01~03과 달라 공용 GuideStepDetailScreen을 쓰지 않고 전용 화면으로 구현.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentExaminationDetailScreen(
    onBack: () -> Unit,
    viewModel: TreatmentExaminationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val briefing by viewModel.briefing.collectAsStateWithLifecycle()
    var editingIndex by rememberSaveable { mutableStateOf<Int?>(null) }

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
                        text = "04 진료 및 검사",
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
                backgroundResId = R.drawable.img_examination_test_banner,
                aspectRatio = 1448f / 1086f,
                title = "진료 및 검사",
                subtitle = "진료 전후에 꼭 필요한 정보를 빠르게 확인할 수 있도록 도와드려요.",
                stepLabel = "STEP 04",
                modifier = Modifier.padding(top = 20.dp)
            )

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "내 진료 브리핑 카드", iconResId = R.drawable.ic_briefing_card_header)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        BRIEFING_FIELDS.forEachIndexed { index, field ->
                            BriefingInfoRow(
                                field = field,
                                value = field.valueOf(briefing),
                                isEditing = editingIndex == index,
                                onStartEdit = { editingIndex = index },
                                onSave = { newValue -> viewModel.updateField(field.field, newValue) },
                                onFinishEdit = { editingIndex = null }
                            )
                            if (index != BRIEFING_FIELDS.lastIndex) {
                                HorizontalDivider(color = DividerColor)
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "오늘 꼭 확인할 3가지", iconResId = R.drawable.ic_check_section_header)
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TODAY_CHECKLIST.forEach { item ->
                        GuideDetailItemCard(iconResId = item.iconResId, title = item.title, description = item.description)
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 28.dp)) {
                GuideDetailSectionTitle(title = "귀국 후 문의 채널", iconResId = R.drawable.ic_inquiry_channel_headset)
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GuideDetailItemCard(
                        iconResId = R.drawable.ic_hospital_inquiry_building,
                        title = "방문한 병원에 먼저 문의",
                        description = "진료 결과, 처방, 이상 증상은 방문한 병원 국제진료센터에 먼저 문의하세요."
                    )
                    GuideDetailItemCard(
                        iconResId = R.drawable.ic_medical_korea_logo_badge,
                        title = "Medical Korea 공식 상담",
                        description = "의료 이용 상담이나 불편 사항은 Medical Korea 공식 지원센터에서 확인할 수 있어요.",
                        trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                        trailingIconTint = SkyBlue,
                        badgeLabel = "공식",
                        onClick = { context.launchIntentSafely(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.medicalkorea.or.kr/"))) }
                    )
                }
            }

            GuideDetailNoticeBanner(
                iconResId = R.drawable.ic_guide_information,
                text = "본 안내는 일반 정보이며, 실제 진료 내용과 주의사항은 의료진 안내를 우선해 주세요.",
                modifier = Modifier.padding(top = 28.dp, bottom = 24.dp)
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
        Image(
            painter = painterResource(id = field.iconResId),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(28.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, end = 8.dp)
        ) {
            Text(text = field.label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
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
                    trailingIcon = {
                        IconButton(onClick = { finishEdit() }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "저장", tint = CoralPrimary)
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
                    text = value,
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
                contentDescription = "수정",
                tint = TextSecondary,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(16.dp)
            )
        }
    }
}
