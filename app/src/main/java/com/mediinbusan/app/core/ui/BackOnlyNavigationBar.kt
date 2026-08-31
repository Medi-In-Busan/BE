package com.mediinbusan.app.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.R

@Composable
fun BackOnlyNavigationBar(
    onBack: () -> Unit,
    background: Color,
    onHomeClick: (() -> Unit)? = null,
    onMapDetailsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = LocalAppStrings.current.common.backContentDescription,
                tint = CoralPrimary,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
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
                        contentDescription = "앱 지도에서 보기",
                        tint = CoralPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
