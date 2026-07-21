package com.mediinbusan.app.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.InactiveTabColor

/**
 * BottomNavBar의 실측 높이(NavigationBar 기본 80dp + 구분선 1dp).
 * 이 바가 보이는 화면들은 이 값만큼 자기 콘텐츠에 직접 하단 여백을 둬야 한다 — 상위 Scaffold의
 * innerPadding에 기대면, back stack이 바뀌는 시점과 실제 화면이 바뀌는 시점 사이의 타이밍
 * 차이 때문에 아직 화면에 남아있는 이전 화면(Splash 등)이 잠깐 눌리는 현상이 생긴다.
 */
val BottomNavBarHeight = 81.dp

/** 4개 탭을 가진 공용 하단 내비게이션 바. Route를 알지 못하는 순수 프레젠테이션 컴포넌트다. */
data class BottomNavTabUiModel(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean,
    val onClick: () -> Unit
)

@Composable
fun BottomNavBar(tabs: List<BottomNavTabUiModel>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        HorizontalDivider(thickness = 1.dp, color = DividerColor)
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            tabs.forEach { tab ->
                NavigationBarItem(
                    selected = tab.selected,
                    onClick = tab.onClick,
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(text = tab.label, style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CoralPrimary,
                        selectedTextColor = CoralPrimary,
                        unselectedIconColor = InactiveTabColor,
                        unselectedTextColor = InactiveTabColor,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
