package com.mediinbusan.app.feature.selfdiagnosis

data class SelfDiagnosisUiState(
    // 홈 -> [인트로(시작 페이지)] -> 질문 1~5 -> 결과. hasStarted=false면 인트로 단계.
    val hasStarted: Boolean = false,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<DiagnosisQuestionId, Set<DiagnosisAnswerOption>> = emptyMap(),
    val resultType: DiagnosisResultType? = null
) {
    // 질문 내용(title/description 등)은 언어에 따라 바뀌므로 여기 담지 않고 화면에서
    // diagnosisQuestions(strings)로 매번 조회한다. id 순서는 DiagnosisQuestionId 선언 순서(Q1~Q5)와 같다.
    private val questionIds: List<DiagnosisQuestionId>
        get() = DiagnosisQuestionId.entries

    val currentQuestionId: DiagnosisQuestionId
        get() = questionIds[currentQuestionIndex]

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questionIds.lastIndex

    val isResultVisible: Boolean
        get() = resultType != null

    val isIntroVisible: Boolean
        get() = !hasStarted && !isResultVisible

    val isQuestionVisible: Boolean
        get() = hasStarted && !isResultVisible

    val progress: Float
        get() = (currentQuestionIndex + 1) / questionIds.size.toFloat()

    val canGoNext: Boolean
        get() = selectedAnswers[currentQuestionId]?.isNotEmpty() == true
}