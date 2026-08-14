package com.mediinbusan.app.feature.documentscan

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediinbusan.app.BuildConfig
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.DividerColor
import com.mediinbusan.app.core.designsystem.MediInBusanTheme
import com.mediinbusan.app.core.designsystem.PageBackground
import com.mediinbusan.app.core.designsystem.SectionTitleStyle
import com.mediinbusan.app.core.designsystem.TextPrimary
import com.mediinbusan.app.core.designsystem.TextSecondary
import com.mediinbusan.app.core.i18n.DocumentScanStrings
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.AsyncImageBox
import com.mediinbusan.app.core.ui.BottomNavBarHeight
import com.mediinbusan.app.core.ui.BrandTopAppBar
import com.mediinbusan.app.core.ui.BrandSnackbarHost
import kotlinx.coroutines.launch
import java.io.File

/**
 * 진단서·처방전 OCR 번역(문서 스캔) 화면. 바텀바 5번째 탭.
 * 이미지 촬영/선택 + 미리보기 + backend/document(CLOVA OCR + Papago 번역 프록시) 호출·결과 표시를 담당한다.
 * 번역은 앱 언어가 한국어가 아닐 때만 요청하며(원문이 한국어라고 가정), 실패해도 원문(extractedText)은 항상 표시된다.
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
        onImageCleared = viewModel::onImageCleared,
        onAnalyzeClick = viewModel::onAnalyzeClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentScanContent(
    uiState: DocumentScanUiState,
    onMenuClick: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageCleared: () -> Unit,
    onAnalyzeClick: () -> Unit
) {
    val strings = LocalAppStrings.current.documentScan
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    fun onCopyClick(text: String) {
        clipboardManager.setText(AnnotatedString(text))
        coroutineScope.launch { snackbarHostState.showSnackbar(strings.copiedMessage) }
    }

    // 에뮬레이터·카메라가 없는 기기에서는 hasSystemFeature가 false거나, true여도 실제 촬영
    // 액티비티를 처리할 카메라 앱이 없을 수 있다 — 전자는 여기서, 후자는 launchCamera의
    // try/catch로 막는다. 둘 다 없으면 launch()가 ActivityNotFoundException으로 크래시한다.
    val hasCameraHardware = remember { context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) }

    // TakePicture()는 결과를 Bitmap이 아니라 우리가 미리 만들어 넘긴 Uri에 저장한다 — 그 Uri를
    // 콜백 시점까지 들고 있어야 해서 별도 상태로 보관한다. Uri는 Parcelable이라 프로세스가
    // 죽었다 복원돼도(카메라 앱 전환 중 메모리 회수 등) rememberSaveable이 그대로 복원해준다.
    var pendingCaptureUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            pendingCaptureUri?.let(onImageSelected)
        }
        pendingCaptureUri = null
    }

    fun launchCamera() {
        val uri = createCaptureImageUri(context)
        try {
            pendingCaptureUri = uri
            cameraLauncher.launch(uri)
        } catch (e: ActivityNotFoundException) {
            pendingCaptureUri = null
            coroutineScope.launch { snackbarHostState.showSnackbar(strings.cameraUnavailableMessage) }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            launchCamera()
        } else {
            coroutineScope.launch { snackbarHostState.showSnackbar(strings.cameraPermissionDeniedMessage) }
        }
    }

    // 갤러리 선택은 Android 13+ Photo Picker 기반이라 런타임 권한이 필요 없다.
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(onImageSelected)
    }

    fun onCaptureClick() {
        if (!hasCameraHardware) {
            coroutineScope.launch { snackbarHostState.showSnackbar(strings.cameraUnavailableMessage) }
            return
        }
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            launchCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = PageBackground,
        topBar = {
            BrandTopAppBar(
                onSettingsClick = onMenuClick,
                currentLanguageCode = uiState.languageCode,
                onLanguageSelected = onLanguageSelected
            )
        },
        snackbarHost = { BrandSnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        // Home/HospitalSearchListScreen/GuideScreen과 동일한 패턴: Modifier.padding(bottom = ...)로
        // Column 자체 레이아웃 영역을 줄이는 대신(그러면 스크롤해도 바텀바 뒤엔 절대 안 그려짐),
        // top padding만 유지하고 bottom은 맨 마지막 자식 Spacer로 확보한다.
        val bottomSafePadding = innerPadding.calculateBottomPadding() + BottomNavBarHeight
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    isAnalyzing = uiState.isAnalyzing,
                    extractedText = uiState.extractedText,
                    translatedText = uiState.translatedText,
                    isAnalysisError = uiState.isAnalysisError,
                    analysisError = uiState.analysisError,
                    onRetake = onImageCleared,
                    onAnalyze = onAnalyzeClick,
                    onCopyClick = ::onCopyClick
                )
            }
            Spacer(modifier = Modifier.height(bottomSafePadding))
        }
    }
}

@Composable
private fun DocumentScanIntro(
    strings: DocumentScanStrings,
    onCaptureClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(40.dp))
    Box(
        modifier = Modifier.size(88.dp).clip(CircleShape).background(CoralPrimaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = CoralPrimary,
            modifier = Modifier.size(40.dp)
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
    Text(text = strings.introTitle, style = SectionTitleStyle, color = TextPrimary, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = strings.introSubtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(32.dp))
    Button(
        onClick = onCaptureClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
    ) {
        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = strings.captureButton, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(
        onClick = onGalleryClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(text = strings.galleryButton)
    }
    Spacer(modifier = Modifier.height(20.dp))
    Text(text = strings.privacyNote, style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
}

@Composable
private fun DocumentScanPreview(
    strings: DocumentScanStrings,
    imageUri: Uri,
    isAnalyzing: Boolean,
    extractedText: String?,
    translatedText: String?,
    isAnalysisError: Boolean,
    analysisError: String?,
    onRetake: () -> Unit,
    onAnalyze: () -> Unit,
    onCopyClick: (String) -> Unit
) {
    // 분석을 시작한 뒤(로딩/에러/결과)에는 실물 사진이 결과 카드와 같은 비중으로 붙어있으면
    // "사진 모드"와 "텍스트 결과 모드"가 한 화면에서 부딪혀 보인다 — 원본 사진은 작은 썸네일로
    // 접어서 결과 카드가 화면의 주인공이 되게 한다.
    val hasStartedAnalysis = isAnalyzing || isAnalysisError || extractedText != null

    if (!hasStartedAnalysis) {
        // Crop으로 320dp 박스를 꽉 채우면 문서 가장자리가 잘려서 원본을 못 보게 되니, 여기서만
        // Fit으로 전체 사진이 다 보이게 하고 남는 여백은 옅은 배경으로 채운다.
        AsyncImageBox(
            model = imageUri,
            contentDescription = strings.previewImageContentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DividerColor)
                .border(width = 1.dp, color = DividerColor, shape = RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(text = strings.retakeButton)
            }
            Button(
                onClick = onAnalyze,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
            ) {
                Text(text = strings.analyzeButton, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    } else {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImageBox(
                model = imageUri,
                contentDescription = strings.previewImageContentDescription,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(width = 1.dp, color = DividerColor, shape = RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isAnalysisError) {
                TextButton(onClick = onAnalyze, enabled = !isAnalyzing) {
                    Text(text = strings.analyzeButton)
                }
            }
            IconButton(onClick = onRetake, enabled = !isAnalyzing) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = strings.retakeButton, tint = TextSecondary)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
    when {
        isAnalyzing -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(CoralPrimaryContainer)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = CoralPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = strings.analyzingMessage, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
        isAnalysisError -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = analysisError ?: strings.analysisErrorFallback,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        extractedText != null -> Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // 번역 결과가 있으면(=앱 언어가 한국어가 아니고 Papago 호출이 성공했으면) 번역문을
            // 먼저 보여주고 원문은 그 아래에 보조로 둔다. 번역이 없으면 원문 카드 하나만 보인다.
            if (translatedText != null) {
                DocumentScanTextResultCard(
                    title = strings.translatedResultTitle,
                    text = translatedText,
                    copyContentDescription = strings.copyButtonContentDescription,
                    onCopyClick = { onCopyClick(translatedText) }
                )
            }
            DocumentScanTextResultCard(
                title = strings.resultTitle,
                text = extractedText,
                copyContentDescription = strings.copyButtonContentDescription,
                onCopyClick = { onCopyClick(extractedText) }
            )
        }
    }
}

@Composable
private fun DocumentScanTextResultCard(
    title: String,
    text: String,
    copyContentDescription: String,
    onCopyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Color.White)
            .border(width = 1.dp, color = DividerColor, shape = MaterialTheme.shapes.large)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onCopyClick) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = copyContentDescription,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        HorizontalDivider(color = DividerColor)
        SelectionContainer {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.5f),
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
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
            onImageCleared = {},
            onAnalyzeClick = {}
        )
    }
}
