package com.mediinbusan.app.feature.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.designsystem.SettingsTitleStyle
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BrandBackTopAppBar
import com.mediinbusan.app.core.ui.EmptyState
import com.mediinbusan.app.core.ui.ItemTypeBadge
import com.mediinbusan.app.data.favorite.FavoriteItemType
import com.mediinbusan.app.data.recent.RecentlyViewed
import java.util.concurrent.TimeUnit

@Composable
fun RecentlyViewedScreen(
    onSelectHospital: (String) -> Unit,
    onSelectPlace: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RecentlyViewedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RecentlyViewedContent(
        items = uiState.items,
        selectedLanguage = uiState.selectedLanguage,
        onBack = onBack,
        onLanguageSelected = viewModel::onLanguageSelected,
        onSelectItem = { item ->
            when (item.itemType) {
                FavoriteItemType.HOSPITAL -> onSelectHospital(item.itemId)
                FavoriteItemType.PLACE -> onSelectPlace(item.itemId)
            }
        }
    )
}

@Composable
private fun RecentlyViewedContent(
    items: List<RecentlyViewed>,
    selectedLanguage: String,
    onBack: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onSelectItem: (RecentlyViewed) -> Unit
) {
    Scaffold(
        topBar = {
            BrandBackTopAppBar(
                onBack = onBack,
                currentLanguageCode = selectedLanguage,
                onLanguageSelected = onLanguageSelected
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "최근 본 항목", style = SettingsTitleStyle, color = SettingsPrimaryText)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (items.isEmpty()) {
                    EmptyState(message = "최근 확인한 병원·장소가 없습니다.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
                    ) {
                        items(items, key = { it.itemId }) { item ->
                            RecentlyViewedRow(item = item, onClick = { onSelectItem(item) })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentlyViewedRow(item: RecentlyViewed, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        AsyncImageBox(
            model = item.imageUrl,
            contentDescription = item.itemName,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(text = item.itemName, style = SettingsItemTitleStyle, color = SettingsPrimaryText)
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                ItemTypeBadge(itemType = item.itemType)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.viewedAt.toRelativeTimeLabel(),
                    style = SettingsDescriptionStyle.copy(fontSize = MaterialTheme.typography.labelSmall.fontSize),
                    color = SettingsSecondaryText,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

private fun Long.toRelativeTimeLabel(): String {
    val elapsedMillis = System.currentTimeMillis() - this
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
    val days = TimeUnit.MILLISECONDS.toDays(elapsedMillis)
    return when {
        minutes < 1 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        hours < 24 -> "${hours}시간 전"
        else -> "${days}일 전"
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentlyViewedContentPreview() {
    MediInBusanTheme {
        RecentlyViewedContent(
            items = listOf(
                RecentlyViewed(
                    itemId = "1",
                    itemName = "부산대학교병원",
                    itemType = FavoriteItemType.HOSPITAL,
                    imageUrl = null,
                    viewedAt = System.currentTimeMillis()
                )
            ),
            selectedLanguage = "ko",
            onBack = {},
            onLanguageSelected = {},
            onSelectItem = {}
        )
    }
}
