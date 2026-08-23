package com.mediinbusan.app.feature.tourism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.core.designsystem.CardTitleStyle
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.SkyBlue
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourismHubScreen(
    onSelectCategory: (TourismCatalogCategory) -> Unit,
    onBack: () -> Unit,
    viewModel: TourismHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = TourismCanvas,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = { Text("부산 관광 데이터") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { TourismHubHero(languageName = uiState.language.displayName) }
            if (uiState.recommendedCategories.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("맞춤 추천", style = SectionTitleStyle, color = TextPrimary)
                        Text("앱에서 둘러본 기록을 바탕으로 추천해요.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                items(
                    items = uiState.recommendedCategories,
                    key = { "recommended-${it.name}" }
                ) { category ->
                    TourismCategoryCard(category = category, onClick = { onSelectCategory(category) }, recommended = true)
                }
            }
            TourismCatalogGroup.entries.forEach { group ->
                val groupCategories = uiState.categories.filter { it.group == group }
                if (groupCategories.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(group.label, style = SectionTitleStyle, color = TextPrimary)
                            Text(group.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    items(
                        items = groupCategories,
                        key = { "group-${it.name}" }
                    ) { category ->
                        TourismCategoryCard(category = category, onClick = { onSelectCategory(category) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TourismHubHero(languageName: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = CoralPrimaryContainer,
        border = BorderStroke(1.dp, Color.White)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(CoralPrimaryContainer, Color.White, Color(0xFFEAF7FF))))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(shape = CircleShape, color = SkyBlue.copy(alpha = 0.12f)) {
                    Icon(
                        Icons.Default.Explore,
                        contentDescription = null,
                        tint = SkyBlue,
                        modifier = Modifier.padding(10.dp).size(24.dp)
                    )
                }
                Text("부산을 더 편하게 둘러보세요", style = SectionTitleStyle, color = TextPrimary)
                Text(
                    "한국관광공사 공공데이터를 장소, 언어, 동선, 여행 데이터로 나눠 한곳에 정리했습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text("$languageName 관광정보 적용 중", style = MaterialTheme.typography.labelLarge, color = CoralPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TourismCategoryCard(
    category: TourismCatalogCategory,
    onClick: () -> Unit,
    recommended: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(shape = CircleShape, color = category.group.tint.copy(alpha = 0.12f)) {
                Icon(
                    category.group.icon,
                    contentDescription = null,
                    tint = category.group.tint,
                    modifier = Modifier.padding(11.dp).size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(category.label, style = CardTitleStyle, color = TextPrimary)
                    if (recommended) {
                        Text("추천", style = MaterialTheme.typography.labelSmall, color = CoralPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(category.shortDescription, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextSecondary)
        }
    }
}

private val TourismCatalogGroup.icon: ImageVector
    get() = when (this) {
        TourismCatalogGroup.PLACES -> Icons.AutoMirrored.Filled.Accessible
        TourismCatalogGroup.ROUTES -> Icons.Default.Route
        TourismCatalogGroup.INSIGHTS -> Icons.Default.BarChart
    }

private val TourismCatalogGroup.tint: Color
    get() = when (this) {
        TourismCatalogGroup.PLACES -> SkyBlue
        TourismCatalogGroup.ROUTES -> Color(0xFF3A7D7B)
        TourismCatalogGroup.INSIGHTS -> CoralPrimary
    }

internal val TourismCanvas = Color(0xFFFFFAFF)
