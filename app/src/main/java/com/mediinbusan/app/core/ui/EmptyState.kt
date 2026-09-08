package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.mediinbusan.app.core.designsystem.HomeBackgroundPink
import com.mediinbusan.app.core.designsystem.MediInBusanTheme

/**
 * F-019: 검색 결과 없음, 즐겨찾기 없음 등 빈 상태를 표시하는 공용 컴포넌트.
 *
 * 연출은 [StateFeedbackContent]가 맡는다(코랄 후광 위 흰 카드 + 페이드·상승 진입 + 느린 부유).
 * 줄 패턴은 길이가 고른 정상 문단 — "망가진 게 아니라 아직 담긴 게 없다"는 뜻이다.
 *
 * [actionLabel]과 [onAction]을 함께 넘기면 문구 아래에 주 버튼이 하나 붙는다. 빈 화면에서 나갈
 * 길을 남기기 위한 것으로, 낯선 나라에서 앱을 쓰는 사용자에게 막다른 화면은 특히 불안하다.
 * 화면에 이미 뚜렷한 출구가 있으면(상단바 뒤로가기로 충분한 상세 화면 등) 넘기지 않는 게 낫다 —
 * 같은 일을 하는 버튼이 둘이 되면 오히려 무엇을 눌러야 할지 흐려진다.
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.SearchOff,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    StateFeedbackContent(
        badgeIcon = icon,
        message = message,
        linePattern = EmptyLinePattern,
        modifier = modifier
    ) {
        if (actionLabel != null && onAction != null) {
            StateActionButton(label = actionLabel, onClick = onAction)
        }
    }
}

private val EmptyLinePattern = listOf(1f, 0.85f, 0.95f, 0.7f)

@Preview(name = "EmptyState", showBackground = true, backgroundColor = 0xFFFFFAFA, heightDp = 360)
@Composable
private fun EmptyStatePreview() {
    MediInBusanTheme {
        Column(modifier = Modifier.background(HomeBackgroundPink)) {
            EmptyState(
                message = "저장한 병원이 없습니다.",
                actionLabel = "병원 둘러보기",
                onAction = {}
            )
        }
    }
}
