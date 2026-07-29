package com.mediinbusan.app.feature.guide

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mediinbusan.app.core.designsystem.BorderColor
import com.mediinbusan.app.core.designsystem.InfoBackgroundBlue
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary

// S-06 하위 상세 화면 공용 컴포넌트 (STEP 상세, 체크리스트 항목 상세 등에서 재사용)

// 텍스트가 이미지 픽셀에 합성된 기존 배너용 (STEP01·STEP02 등). 언어 전환 대응 불가.
@Composable
fun GuideDetailBanner(@DrawableRes bannerResId: Int, aspectRatio: Float, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = bannerResId),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(20.dp))
    )
}

// 텍스트 없는 배경 이미지 위에 STEP 배지·제목·부제목을 Compose Text로 얹는 배너.
// title/subtitle/stepLabel이 코드에 있는 문자열이라 이후 언어별 string resource로 옮기기 쉽다.
@Composable
fun GuideDetailBanner(
    @DrawableRes backgroundResId: Int,
    aspectRatio: Float,
    title: String,
    subtitle: String,
    stepLabel: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 24.dp)
        ) {
            if (stepLabel != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(InfoBackgroundBlue)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stepLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun GuideDetailSectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(text = title, style = SectionTitleStyle, color = TextPrimary, modifier = modifier)
}

@Composable
fun GuideDetailItemCard(
    @DrawableRes iconResId: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    trailingIconTint: Color = TextSecondary,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    badgeLabel: String? = null,
    badgeBackgroundColor: Color = InfoBackgroundBlue,
    badgeTextColor: Color = SkyBlue
) {
    val shape = RoundedCornerShape(20.dp)
    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val border = BorderStroke(1.dp, BorderColor)
    val elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(id = iconResId), contentDescription = null, modifier = Modifier.size(52.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (badgeLabel != null) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(badgeBackgroundColor)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = badgeLabel, style = MaterialTheme.typography.labelSmall, color = badgeTextColor)
                        }
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (trailingIcon != null) {
                Icon(imageVector = trailingIcon, contentDescription = null, tint = trailingIconTint, modifier = Modifier.size(22.dp))
            }
        }
    }

    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = shape, colors = colors, border = border, elevation = elevation) {
            content()
        }
    } else {
        Card(modifier = modifier.fillMaxWidth(), shape = shape, colors = colors, border = border, elevation = elevation) {
            content()
        }
    }
}

@Composable
fun GuideDetailNoticeBanner(@DrawableRes iconResId: Int, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InfoBackgroundBlue)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = iconResId), contentDescription = null, modifier = Modifier.size(28.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}