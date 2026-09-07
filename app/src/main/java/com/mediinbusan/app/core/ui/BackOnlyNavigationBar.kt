package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.HeroSubtitleStyle
import com.mediinbusan.app.core.designsystem.HeroTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.R

@Composable
fun BackOnlyNavigationBar(
    onBack: () -> Unit,
    background: Color,
    onHomeClick: (() -> Unit)? = null,
    onMapDetailsClick: (() -> Unit)? = null,
    title: String? = null,
    subtitle: String? = null,
    trailingImageRes: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 42.dp) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = LocalAppStrings.current.common.backContentDescription,
                    tint = CoralPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        if (title != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.padding(top = 12.dp, bottom = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = title, style = HeroTitleStyle, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (subtitle != null) {
                    Text(text = subtitle, style = HeroSubtitleStyle, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        trailingImageRes?.let { imageRes ->
            // 타이틀+서브타이틀 블록과 같은 높이로 키워서, 오른쪽 정렬 상태로 왼쪽으로도 더 넓게
            // 확장되도록 한다(Row 폭은 고정이라 높이를 키우면 원본 비율대로 폭도 같이 늘어난다).
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.height(56.dp)
            )
        }
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 42.dp) {
            onHomeClick?.let { onClick ->
                IconButton(onClick = onClick) {
                    Image(
                        painter = painterResource(id = R.drawable.hospital_detail_home),
                        contentDescription = LocalAppStrings.current.common.bottomNavHomeLabel,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            onMapDetailsClick?.let { onClick ->
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = LocalAppStrings.current.common.mapDetailsContentDescription,
                        tint = CoralPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
