package com.mediinbusan.app.feature.selfdiagnosis

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.data.guide.GuidePhase
import com.mediinbusan.app.feature.guide.GuideStepDetailScreen

private sealed interface RoutingPreviewScreen {
    data object Result : RoutingPreviewScreen
    data class Guide(val phase: GuidePhase) : RoutingPreviewScreen
    data class Placeholder(val message: String) : RoutingPreviewScreen
}

@Composable
private fun SelfDiagnosisRoutingPreviewHarness(resultType: DiagnosisResultType) {
    var screen by remember { mutableStateOf<RoutingPreviewScreen>(RoutingPreviewScreen.Result) }

    when (val current = screen) {
        RoutingPreviewScreen.Result -> DiagnosisResultContent(
            resultType = resultType,
            onCtaClick = { target ->
                screen = when (target) {
                    DiagnosisCtaTarget.HOSPITAL_BROWSE -> RoutingPreviewScreen.Placeholder("✅ 리다이렉트 성공: 의료기관 목록(HospitalSearchListScreen) 탭으로 이동")
                    DiagnosisCtaTarget.WELLNESS_PLACES -> RoutingPreviewScreen.Placeholder("✅ 리다이렉트 성공: 주변 관광·웰니스(NearbyScreen)로 이동")
                    DiagnosisCtaTarget.GUIDE_STEP01_ENTRY_PREPARATION -> RoutingPreviewScreen.Guide(GuidePhase.ENTRY_PREPARATION)
                    DiagnosisCtaTarget.GUIDE_STEP02_RESERVATION_INQUIRY -> RoutingPreviewScreen.Guide(GuidePhase.RESERVATION_INQUIRY)
                    DiagnosisCtaTarget.GUIDE_STEP03_HOSPITAL_CHECKIN -> RoutingPreviewScreen.Guide(GuidePhase.HOSPITAL_CHECKIN)
                    DiagnosisCtaTarget.GUIDE_STEP06_AFTERCARE_RETURN_CHECK -> RoutingPreviewScreen.Guide(GuidePhase.AFTERCARE_RETURN_CHECK)
                }
            },
            onRestart = { screen = RoutingPreviewScreen.Placeholder("✅ 리다이렉트 성공: 다시 진단하기 → 챗봇 화면(같은 화면 안에서 상태만 초기화됨)") },
            onGoHome = { screen = RoutingPreviewScreen.Placeholder("✅ 리다이렉트 성공: 메인 홈(Route.Home)으로 이동") },
            goHomeButtonLabel = "홈으로 돌아가기"
        )
        is RoutingPreviewScreen.Guide -> GuideStepDetailScreen(
            phase = current.phase,
            onBack = { screen = RoutingPreviewScreen.Result }
        )
        is RoutingPreviewScreen.Placeholder -> Placeholder(message = current.message, onBack = { screen = RoutingPreviewScreen.Result })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Placeholder(message: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = { Text("리다이렉트 확인") }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "$message\n\n(위 뒤로가기 버튼을 누르면 결과 화면으로 돌아갑니다 — 이 텍스트 자리는 실제 앱에선 진짜 화면입니다)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1600, name = "라우팅 확인 · TYPE A")
@Composable
private fun SelfDiagnosisRoutingPreviewTypeA() {
    MediInBusanTheme { SelfDiagnosisRoutingPreviewHarness(DiagnosisResultType.TYPE_A) }
}

@Preview(showBackground = true, heightDp = 1600, name = "라우팅 확인 · TYPE B")
@Composable
private fun SelfDiagnosisRoutingPreviewTypeB() {
    MediInBusanTheme { SelfDiagnosisRoutingPreviewHarness(DiagnosisResultType.TYPE_B) }
}

@Preview(showBackground = true, heightDp = 1600, name = "라우팅 확인 · TYPE C")
@Composable
private fun SelfDiagnosisRoutingPreviewTypeC() {
    MediInBusanTheme { SelfDiagnosisRoutingPreviewHarness(DiagnosisResultType.TYPE_C) }
}

@Preview(showBackground = true, heightDp = 1600, name = "라우팅 확인 · TYPE D")
@Composable
private fun SelfDiagnosisRoutingPreviewTypeD() {
    MediInBusanTheme { SelfDiagnosisRoutingPreviewHarness(DiagnosisResultType.TYPE_D) }
}

@Preview(showBackground = true, heightDp = 1600, name = "라우팅 확인 · TYPE E")
@Composable
private fun SelfDiagnosisRoutingPreviewTypeE() {
    MediInBusanTheme { SelfDiagnosisRoutingPreviewHarness(DiagnosisResultType.TYPE_E) }
}
