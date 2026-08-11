package com.mediinbusan.app.feature.documentscan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.BuildConfig
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.DocumentScanStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BrandBackTopAppBar
import com.mediinbusan.app.core.ui.BrandSnackbarHost
import kotlinx.coroutines.launch
import java.io.File

/**
 * 진단서·처방전 OCR 번역(문서 스캔) 화면. 바텀바 5번째 탭.
 * OCR/번역 백엔드가 아직 없어, 이 화면은 이미지 촬영/선택 + 미리보기까지만 담당한다.
 * "분석하기"는 지금은 안내 스낵바만 띄우는 스텁이고, 실제 호출은 후속 이슈에서 붙인다.
 */
@Composable
fun DocumentScanScreen(
    onMenuClick: () -> Unit = {},
    viewModel: DocumentScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DocumentScanContent(
        uiState = uiState,
        onMenuClick = onMenuClick,
        onLanguageSelected = viewModel::onLanguageSelected,
        onImageSelected = viewModel::onImageSelected,
        onImageCleared = viewModel::onImageCleared
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentScanContent(
    uiState: DocumentScanUiState,
    onMenuClick: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageCleared: () -> Unit
) {
    val strings = LocalAppStrings.current.documentScan
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // TakePicture()는 결과를 Bitmap이 아니라 우리가 미리 만들어 넘긴 Uri에 저장한다 — 그 Uri를
    // 콜백 시점까지 들고 있어야 해서 별도 상태로 보관한다.
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            pendingCaptureUri?.let(onImageSelected)
        }
        pendingCaptureUri = null
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val uri = createCaptureImageUri(context)
            pendingCaptureUri = uri
            cameraLauncher.launch(uri)
        } else {
            coroutineScope.launch { snackbarHostState.showSnackbar(strings.cameraPermissionDeniedMessage) }
        }
    }

    // 갤러리 선택은 Android 13+ Photo Picker 기반이라 런타임 권한이 필요 없다.
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(onImageSelected)
    }

    fun onCaptureClick() {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val uri = createCaptureImageUri(context)
            pendingCaptureUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = PageBackground,
        topBar = {
            BrandBackTopAppBar(
                onBack = onMenuClick,
                currentLanguageCode = uiState.languageCode,
                onLanguageSelected = onLanguageSelected,
                navigationIcon = Icons.Default.Menu
            )
        },
        snackbarHost = { BrandSnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(bottom = BottomNavBarHeight)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageUri = uiState.selectedImageUri
            if (imageUri == null) {
                DocumentScanIntro(
                    strings = strings,
                    onCaptureClick = ::onCaptureClick,
                    onGalleryClick = {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )
            } else {
                DocumentScanPreview(
                    strings = strings,
                    imageUri = imageUri,
                    onRetake = onImageCleared,
                    onAnalyze = {
                        coroutineScope.launch { snackbarHostState.showSnackbar(strings.comingSoonMessage) }
                    }
                )
            }
        }
    }
}

@Composable
private fun DocumentScanIntro(
    strings: DocumentScanStrings,
    onCaptureClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Text(text = strings.introTitle, style = SectionTitleStyle, color = TextPrimary, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = strings.introSubtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(28.dp))
    Button(
        onClick = onCaptureClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
    ) {
        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = strings.captureButton)
    }
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(onClick = onGalleryClick, modifier = Modifier.fillMaxWidth()) {
        Text(text = strings.galleryButton)
    }
}

@Composable
private fun DocumentScanPreview(
    strings: DocumentScanStrings,
    imageUri: Uri,
    onRetake: () -> Unit,
    onAnalyze: () -> Unit
) {
    AsyncImageBox(
        model = imageUri,
        contentDescription = strings.previewImageContentDescription,
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .clip(RoundedCornerShape(16.dp))
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(onClick = onRetake, modifier = Modifier.weight(1f)) {
            Text(text = strings.retakeButton)
        }
        Button(
            onClick = onAnalyze,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
        ) {
            Text(text = strings.analyzeButton)
        }
    }
}

// FileProvider로 앱 캐시 디렉토리 안의 임시 파일을 가리키는 content:// Uri를 만든다. 촬영한 원본
// 이미지를 TakePicture()가 이 Uri에 직접 써준다. res/xml/file_paths.xml의 cache-path와 짝을 이룬다.
private fun createCaptureImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File(imagesDir, "document_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
}

@Preview(name = "DocumentScan - 이미지 선택 전", showBackground = true)
@Composable
private fun DocumentScanContentEmptyPreview() {
    MediInBusanTheme {
        DocumentScanContent(
            uiState = DocumentScanUiState(),
            onMenuClick = {},
            onLanguageSelected = {},
            onImageSelected = {},
            onImageCleared = {}
        )
    }
}
