package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * F-019: 네트워크 오류 등에서 재시도 버튼과 함께 표시하는 공용 컴포넌트.
 *
 * 연출은 [StateFeedbackContent]가 맡는다. [EmptyState]와 같은 히어로를 쓰되 두 군데가 다르다 —
 * 줄 패턴이 짧게 끊겨 있고(내용을 못 받아왔다는 뜻), 배지가 CloudOff다(오류의 대부분이 네트워크다).
 * 재시도 버튼은 문서스캔 인트로의 주 버튼과 같은 톤(코랄 그림자를 깐 채움 버튼)으로 맞춘다.
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    icon: ImageVector = Icons.Default.CloudOff
) {
    StateFeedbackContent(
        badgeIcon = icon,
        message = message,
        linePattern = ErrorLinePattern,
        modifier = modifier
    ) {
        if (onRetry != null) {
            StateActionButton(
                label = LocalAppStrings.current.common.retryButtonLabel,
                onClick = onRetry
            )
        }
    }
}

/** 중간이 비고 끝이 짧다 — 받다 만 문서처럼 보이게 하는 패턴. */
private val ErrorLinePattern = listOf(0.9f, 0.45f, 0.75f, 0.3f)

@Preview(name = "ErrorState", showBackground = true, backgroundColor = 0xFFFFFAFA, heightDp = 420)
@Composable
private fun ErrorStatePreview() {
    MediInBusanTheme {
        Column(modifier = Modifier.background(HomeBackgroundPink)) {
            ErrorState(message = "정보를 불러오지 못했습니다.", onRetry = {})
        }
    }
}
