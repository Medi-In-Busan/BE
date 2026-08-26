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

/**
 * 챗봇을 실행하지 않고 결과 화면의 CTA 리다이렉트가 맞는지 확인하기 위한 미리보기 전용 조합.
 * feature 패키지는 서로를 직접 import하지 않는다(core/navigation/MediInBusanNavHost.kt를 통해서만
 * 연결) — 그래서 GUIDE_STEP* 대상도 실제 feature/guide의 GuideStepDetailScreen을 그리지 않고,
 * 다른 대상(HOSPITAL_BROWSE 등)과 동일하게 로컬 placeholder로만 "어디로 갈지"를 확인한다. 실제
 * 화면 전환까지 보고 싶으면 NavHost를 통해 앱을 실행해서 확인해야 한다.
 * Android Studio에서 이 파일을 열고 Interactive Mode(▶ 아이콘, 프리뷰 썸네일에 마우스를 올리면
 * 나타남)로 들어가면 카드를 실제로 눌러볼 수 있다.
 */
@Composable
private fun SelfDiagnosisRoutingPreviewHarness(resultType: DiagnosisResultType) {
    var redirectMessage by remember { mutableStateOf<String?>(null) }

    val message = redirectMessage
    if (message == null) {
        DiagnosisResultContent(
            resultType = resultType,
            onCtaClick = { target ->
                redirectMessage = when (target) {
                    DiagnosisCtaTarget.HOSPITAL_BROWSE -> "✅ 리다이렉트 성공: 의료기관 목록(HospitalSearchListScreen) 탭으로 이동"
                    DiagnosisCtaTarget.WELLNESS_PLACES -> "✅ 리다이렉트 성공: 주변 관광·웰니스(NearbyScreen)로 이동"
                    DiagnosisCtaTarget.GUIDE_STEP01_ENTRY_PREPARATION -> "✅ 리다이렉트 성공: 이용 가이드 STEP01 입국 전 준비 상세로 이동"
                    DiagnosisCtaTarget.GUIDE_STEP02_RESERVATION_INQUIRY -> "✅ 리다이렉트 성공: 이용 가이드 STEP02 예약 및 문의 상세로 이동"
                    DiagnosisCtaTarget.GUIDE_STEP03_HOSPITAL_CHECKIN -> "✅ 리다이렉트 성공: 이용 가이드 STEP03 병원 방문 및 접수 상세로 이동"
                    DiagnosisCtaTarget.GUIDE_STEP06_AFTERCARE_RETURN_CHECK -> "✅ 리다이렉트 성공: 이용 가이드 STEP06 회복 귀국 준비 상세로 이동"
                }
            },
            onRestart = { redirectMessage = "✅ 리다이렉트 성공: 다시 진단하기 → 챗봇 화면(같은 화면 안에서 상태만 초기화됨)" },
            onGoHome = { redirectMessage = "✅ 리다이렉트 성공: 메인 홈(Route.Home)으로 이동" },
            goHomeButtonLabel = "홈으로 돌아가기"
        )
    } else {
        Placeholder(message = message, onBack = { redirectMessage = null })
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
                text = "$message\n\n(위 뒤로가기 버튼을 누르면 결과 화면으로 돌아갑니다 — 실제 화면 렌더링은 앱을 실행해서 확인하세요)",
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
