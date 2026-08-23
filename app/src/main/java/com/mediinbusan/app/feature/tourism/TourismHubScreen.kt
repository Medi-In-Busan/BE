package com.mediinbusan.app.feature.tourism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
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
                title = { Text("부산 둘러보기") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            item {
                FeaturedExploreBanner(
                    languageName = uiState.language.displayName,
                    category = uiState.featuredCategory,
                    onClick = { onSelectCategory(uiState.featuredCategory) }
                )
            }
            item {
                JourneySection(
                    title = "회복 일정에 맞춰",
                    description = "이동 부담과 활동 강도를 고려해 골라보세요.",
                    categories = uiState.recoveryCategories,
                    onSelectCategory = onSelectCategory
                )
            }
            item {
                JourneySection(
                    title = "여행 전에 확인",
                    description = "함께 둘러볼 곳과 예상 혼잡도를 확인해요.",
                    categories = uiState.planningCategories,
                    onSelectCategory = onSelectCategory
                )
            }
            item { TourismSourceNotice() }
        }
    }
}

@Composable
private fun FeaturedExploreBanner(
    languageName: String,
    category: TourismCatalogCategory,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFE5DA), Color(0xFFFFF8F4), Color(0xFFE4F5FC))
                )
            )
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(148.dp)
                .align(Alignment.TopEnd)
                .padding(start = 34.dp, bottom = 34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.48f))
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(shape = CircleShape, color = SkyBlue.copy(alpha = 0.14f)) {
                Icon(
                    Icons.Default.Explore,
                    contentDescription = null,
                    tint = SkyBlue,
                    modifier = Modifier.padding(10.dp).size(24.dp)
                )
            }
            Text(
                text = "회복 사이,\n부산 한 걸음",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "지금 설정된 언어로 부산의 관광지와 쉬어가기 좋은 장소를 찾아보세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Text(
                text = "$languageName 관광정보",
                style = MaterialTheme.typography.labelLarge,
                color = CoralPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoralPrimary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(category.label, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun JourneySection(
    title: String,
    description: String,
    categories: List<TourismCatalogCategory>,
    onSelectCategory: (TourismCatalogCategory) -> Unit
) {
    if (categories.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = SectionTitleStyle, color = TextPrimary)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.dp, DividerColor)
        ) {
            Column {
                categories.forEachIndexed { index, category ->
                    TourismJourneyRow(
                        category = category,
                        onClick = { onSelectCategory(category) }
                    )
                    if (index < categories.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 18.dp),
                            color = DividerColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TourismJourneyRow(
    category: TourismCatalogCategory,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(shape = CircleShape, color = category.tint.copy(alpha = 0.12f)) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = category.tint,
                modifier = Modifier.padding(10.dp).size(21.dp)
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(category.label, style = CardTitleStyle, color = TextPrimary)
            Text(category.shortDescription, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun TourismSourceNotice() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "한국관광공사 공공데이터 활용",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "운영 정보와 혼잡도는 실제 방문 전에 공식 안내를 함께 확인해 주세요.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

private val TourismCatalogCategory.icon: ImageVector
    get() = when (this) {
        TourismCatalogCategory.ACCESSIBLE -> Icons.AutoMirrored.Filled.Accessible
        TourismCatalogCategory.WALKING -> Icons.Default.Route
        TourismCatalogCategory.RELATED -> Icons.Default.Explore
        TourismCatalogCategory.CROWDING -> Icons.Default.BarChart
        else -> Icons.Default.Explore
    }

private val TourismCatalogCategory.tint: Color
    get() = when (this) {
        TourismCatalogCategory.ACCESSIBLE -> SkyBlue
        TourismCatalogCategory.WALKING -> Color(0xFF3A7D7B)
        TourismCatalogCategory.RELATED -> CoralPrimary
        TourismCatalogCategory.CROWDING -> Color(0xFFCB6D3D)
        else -> SkyBlue
    }

internal val TourismCanvas = Color(0xFFFFFAF7)
