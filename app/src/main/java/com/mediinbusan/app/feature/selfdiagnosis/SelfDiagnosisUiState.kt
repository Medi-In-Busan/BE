package com.mediinbusan.app.feature.selfdiagnosis

data class SelfDiagnosisUiState(
    // 홈 -> [인트로(시작 페이지)] -> 질문 1~5 -> 결과. hasStarted=false면 인트로 단계.
    val hasStarted: Boolean = false,
    val questions: List<DiagnosisQuestion> = DiagnosisQuestions,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<DiagnosisQuestionId, Set<DiagnosisAnswerOption>> = emptyMap(),
    val resultType: DiagnosisResultType? = null
) {
    val currentQuestion: DiagnosisQuestion
        get() = questions[currentQuestionIndex]

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questions.lastIndex

    val isResultVisible: Boolean
        get() = resultType != null

    val isIntroVisible: Boolean
        get() = !hasStarted && !isResultVisible

    val isQuestionVisible: Boolean
        get() = hasStarted && !isResultVisible

    val progress: Float
        get() = (currentQuestionIndex + 1) / questions.size.toFloat()

    val canGoNext: Boolean
        get() = selectedAnswers[currentQuestion.id]?.isNotEmpty() == true
}