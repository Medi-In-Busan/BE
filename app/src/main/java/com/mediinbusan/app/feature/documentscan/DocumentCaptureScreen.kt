package com.mediinbusan.app.feature.documentscan

import android.content.Context
import android.net.Uri
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
// awaitInstance는 Companion의 확장 함수(ProcessCameraProviderExtKt)라 따로 import해야 한다.
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mediinbusan.app.BuildConfig
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.i18n.LocalAppStrings
import com.mediinbusan.app.core.ui.BrandSnackbarHost
import com.mediinbusan.app.core.ui.rememberHaptics
import kotlinx.coroutines.launch
import java.io.File

/**
 * 진단서·처방전 촬영 화면. 예전에는 `ActivityResultContracts.TakePicture()`로 시스템 카메라 앱에
 * 넘겼는데, 그러면 촬영 UI를 전혀 제어할 수 없어 "문서를 프레임에 맞춰 찍어달라"는 안내를 줄 수가
 * 없었다 — 프레임을 벗어나거나 기울어진 사진이 그대로 OCR로 올라가 인식률이 떨어졌다.
 *
 * 그래서 CameraX(PreviewView + AndroidView, KakaoMapView와 같은 패턴)로 앱 안에 촬영 화면을 직접
 * 둔다. camera-compose 아티팩트는 아직 alpha라 쓰지 않는다(CLAUDE.md §6-6: 실험적 API 금지).
 *
 * 자동 모서리 감지·자동 크롭은 없다. A4 비율 가이드 프레임으로 사용자가 직접 맞추게 하는 데까지가
 * 이 화면의 범위이고, 잘라내기·원근 보정이 필요해지면 별도 작업으로 붙인다.
 */

/** 가이드 프레임이 차지하는 화면 가로 비율. */
private const val GuideWidthFraction = 0.86f

/** 가이드 프레임 세로/가로 비율. A4(210×297)를 세로로 세운 값. */
private const val GuideHeightRatio = 1.414f

/**
 * 가이드 프레임을 화면 전체가 아니라 위아래 컨트롤을 뺀 영역의 한가운데 놓는다 — 화면 전체 기준으로
 * 중앙 정렬하면 세로로 긴 기기에서 프레임 아래쪽 선이 안내 문구와 겹친다.
 */
private const val GuideTopBandFraction = 0.10f // 닫기·플래시 줄
private const val GuideBottomBandFraction = 0.28f // 안내 문구 + 셔터 줄

@Composable
fun DocumentCaptureScreen(
    onImageCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val strings = LocalAppStrings.current.documentScan
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptics = rememberHaptics()

    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }
    val imageCapture = remember {
        // 문서는 글자 획이 뭉개지면 OCR이 통째로 틀리므로 지연보다 화질을 택한다.
        ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build()
    }

    var camera by remember { mutableStateOf<Camera?>(null) }
    var isCameraUnavailable by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
    var isTorchOn by rememberSaveable { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(onImageCaptured)
    }

    LaunchedEffect(Unit) {
        // 바인딩은 실패할 수 있다(다른 앱이 카메라 점유, 카메라 없는 기기, 프로세스 복원 직후 등).
        // 실패해도 화면이 검게만 남지 않도록 안내 + 갤러리 경로를 남긴다.
        try {
            val cameraProvider = ProcessCameraProvider.awaitInstance(context)
            val preview = Preview.Builder().build().apply { surfaceProvider = previewView.surfaceProvider }
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            isCameraUnavailable = true
            snackbarHostState.showSnackbar(strings.cameraStartFailedMessage)
        }
    }

    // 화면이 돌면 EXIF에 기록될 방향도 같이 돌아야 한다 — 앱에 방향 고정이 없어서
    // (AndroidManifest에 screenOrientation 없음) 회전할 때마다 다시 맞춘다. 업로드 직전
    // DocumentOcrRepositoryImpl이 이 EXIF를 읽어 회전을 보정한다.
    val configuration = LocalConfiguration.current
    LaunchedEffect(configuration.orientation, camera) {
        imageCapture.targetRotation = previewView.display?.rotation ?: Surface.ROTATION_0
    }

    val hasTorch = camera?.cameraInfo?.hasFlashUnit() == true
    LaunchedEffect(isTorchOn, camera) {
        if (hasTorch) {
            camera?.cameraControl?.enableTorch(isTorchOn)
        }
    }

    fun capture() {
        if (isCapturing || camera == null) {
            return
        }
        // 저장 완료(onImageSaved)가 아니라 누른 순간에 울린다 — 고화질 모드라 저장까지 수백 ms가
        // 걸리는데, 그때 울리면 셔터를 누른 손과 피드백이 어긋나 두 번 누르게 된다.
        haptics.confirm()
        isCapturing = true
        val file = createCaptureImageFile(context)
        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    isCapturing = false
                    onImageCaptured(captureImageUri(context, file))
                }

                override fun onError(exception: ImageCaptureException) {
                    isCapturing = false
                    coroutineScope.launch { snackbarHostState.showSnackbar(strings.captureFailedMessage) }
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        CaptureGuideOverlay(
            isScanning = !isCameraUnavailable && !isCapturing,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.captureCloseContentDescription,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (hasTorch) {
                    IconButton(onClick = { isTorchOn = !isTorchOn }) {
                        Icon(
                            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = if (isTorchOn) {
                                strings.captureTorchOffContentDescription
                            } else {
                                strings.captureTorchOnContentDescription
                            },
                            tint = if (isTorchOn) CoralPrimary else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (isCameraUnavailable) strings.cameraStartFailedMessage else strings.captureGuideMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = strings.galleryButton,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                ShutterButton(
                    enabled = camera != null && !isCapturing,
                    isCapturing = isCapturing,
                    contentDescription = strings.captureShutterContentDescription,
                    onClick = ::capture
                )
                Spacer(modifier = Modifier.weight(1f))
                // 셔터가 화면 정중앙에 오도록 갤러리 버튼과 같은 폭을 반대편에 비워 둔다.
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        BrandSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).windowInsetsPadding(WindowInsets.safeDrawing)
        )
    }
}

/**
 * 스캔라인이 가이드 프레임을 한 번 훑는 시간.
 *
 * 인트로 히어로(2600ms)·분석 중 오버레이(2200ms)보다 느리다. 앞의 둘은 "지금 읽고 있다"를
 * 보여주지만 여기는 사용자가 문서를 맞추기를 기다리는 화면이라, 빠르면 이미 처리 중인 줄 알게 된다.
 */
private const val CaptureSweepCycleMs = 3000

/**
 * 가이드 프레임 밖을 어둡게 덮고, 프레임 모서리에 코랄 브래킷을 그린 뒤 그 안을 스캔라인이 훑는다.
 *
 * 스캔 결과 화면의 `ScanFrameCorners`와 같은 톤이지만 그걸 재사용하지 않는다 — 어두운 스크림에
 * 구멍을 뚫는 것과 브래킷을 그리는 것이 **같은 사각형**을 알아야 해서, 둘을 한 번의 그리기 패스에서
 * 같은 좌표로 계산해야 어긋나지 않는다. 스캔라인도 같은 이유로 여기서 같이 그린다.
 *
 * 스캔라인을 넣은 이유: 인트로([DocumentScanIntro])와 분석 중([DocumentScanningOverlay])은 라인이
 * 도는데 그 사이에 끼는 이 화면만 정지 프레임이라, 세 화면을 잇는 시각 언어가 가운데서 끊겨 있었다.
 *
 * @param isScanning 카메라가 살아 있고 촬영 중이 아닐 때만 true. 셔터를 누른 순간 라인이 멈춰
 *   "잡혔다"는 신호가 되고, 카메라를 못 켠 상태에서 훑고 있는 척하지도 않는다.
 */
@Composable
private fun CaptureGuideOverlay(isScanning: Boolean, modifier: Modifier = Modifier) {
    val guideDescription = LocalAppStrings.current.documentScan.captureGuideMessage
    val transition = rememberInfiniteTransition(label = "captureGuide")
    // 인트로 히어로와 같은 Reverse. 아직 잡아낸 게 없는 대기 상태라 왕복이 잔잔하다
    // (분석 중 오버레이만 박스를 하나씩 잡는 사이클이라 Restart를 쓴다).
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = CaptureSweepCycleMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "captureGuideSweep"
    )

    Canvas(
        // 스크림에 구멍을 뚫으려면(BlendMode.Clear) 이 레이어가 별도 버퍼에 그려져야 한다.
        // Offscreen이 없으면 화면 전체가 지워진다.
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .semantics { contentDescription = guideDescription }
    ) {
        val bandTop = size.height * GuideTopBandFraction
        val available = size.height - bandTop - size.height * GuideBottomBandFraction
        val guideHeight = minOf(size.width * GuideWidthFraction * GuideHeightRatio, available)
        val guideWidth = guideHeight / GuideHeightRatio
        val left = (size.width - guideWidth) / 2f
        val top = bandTop + (available - guideHeight) / 2f
        val cornerRadius = CornerRadius(20.dp.toPx())

        drawRect(color = Color.Black.copy(alpha = 0.55f))
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(guideWidth, guideHeight),
            cornerRadius = cornerRadius,
            blendMode = BlendMode.Clear
        )
        drawGuideCorners(left = left, top = top, width = guideWidth, height = guideHeight)
        if (isScanning) {
            // 구멍(BlendMode.Clear)을 뚫은 뒤에 그려야 라인이 지워지지 않고 남는다.
            drawGuideSweep(left = left, top = top, width = guideWidth, height = guideHeight, sweep = sweep)
        }
    }
}

/**
 * 가이드 프레임 안쪽만 훑는 스캔라인 + 뒤따르는 글로우.
 *
 * 분석 중 오버레이(`drawScanBeam`)와 같은 구성이되 두 가지가 다르다 — 프레임 밖으로 새지 않도록
 * 가로 범위를 가이드 사각형으로 자르고, 카메라 프리뷰 위라 밝기를 낮춘다(흰 종이 위에서 라인이
 * 너무 세면 문서를 맞추는 데 방해가 된다).
 */
private fun DrawScope.drawGuideSweep(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    sweep: Float
) {
    val lineY = top + sweep * height
    val glowHeight = 64.dp.toPx()
    // Reverse라 라인이 위로 올라갈 때도 글로우가 아래에 깔린다. 진행 방향을 따라 뒤집지 않는 건
    // 여기 글로우가 "지나간 자리"가 아니라 라인을 도드라지게 하는 그림자 역할이라서다.
    val glowTop = (lineY - glowHeight).coerceAtLeast(top)

    if (lineY > glowTop) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, CoralPrimary.copy(alpha = 0.20f)),
                startY = glowTop,
                endY = lineY
            ),
            topLeft = Offset(left, glowTop),
            size = Size(width, lineY - glowTop)
        )
    }

    drawLine(
        color = CoralPrimary.copy(alpha = 0.75f),
        start = Offset(left, lineY),
        end = Offset(left + width, lineY),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawGuideCorners(left: Float, top: Float, width: Float, height: Float) {
    val cornerLength = 28.dp.toPx()
    val strokeWidth = 4.dp.toPx()
    val right = left + width
    val bottom = top + height
    val segments = listOf(
        Offset(left, top + cornerLength) to Offset(left, top),
        Offset(left, top) to Offset(left + cornerLength, top),
        Offset(right - cornerLength, top) to Offset(right, top),
        Offset(right, top) to Offset(right, top + cornerLength),
        Offset(left, bottom - cornerLength) to Offset(left, bottom),
        Offset(left, bottom) to Offset(left + cornerLength, bottom),
        Offset(right - cornerLength, bottom) to Offset(right, bottom),
        Offset(right, bottom) to Offset(right, bottom - cornerLength)
    )
    segments.forEach { (start, end) ->
        drawLine(
            color = CoralPrimary,
            start = start,
            end = end,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

/** 카메라 앱 관례를 따른 흰 링 + 코랄 코어 셔터. 촬영 중에는 코어 자리에 진행 표시가 들어간다. */
@Composable
private fun ShutterButton(
    enabled: Boolean,
    isCapturing: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .background(color = Color.White.copy(alpha = 0.25f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(64.dp)
        ) {
            if (isCapturing) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp, color = Color.White)
            } else {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(
                            color = if (enabled) CoralPrimary else CoralPrimary.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .semantics { this.contentDescription = contentDescription }
                )
            }
        }
    }
}

/**
 * 촬영본을 담을 앱 캐시 파일. res/xml/file_paths.xml의 cache-path와 짝을 이룬다.
 * 시스템 카메라 앱에 넘기던 시절부터 쓰던 자리를 그대로 쓴다 — CameraX가 직접 이 파일에 쓴다.
 */
internal fun createCaptureImageFile(context: Context): File {
    val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
    return File(imagesDir, "document_${System.currentTimeMillis()}.jpg")
}

/**
 * 캐시 파일을 가리키는 content:// Uri. 앱 안에서만 쓰지만 file:// 대신 FileProvider를 계속 쓴다 —
 * 이 Uri는 SavedStateHandle에 저장돼 프로세스 복원 뒤에도 되살아나고, Coil·ContentResolver가
 * 양쪽 스킴을 다르게 다루는 걸 신경 쓸 필요가 없어진다.
 */
internal fun captureImageUri(context: Context, file: File): Uri =
    FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)

/**
 * 캐시에 남은 촬영본 중 [keep]이 가리키는 것만 남기고 전부 지운다.
 *
 * 촬영본은 앱 캐시에 실물 JPEG로 떨어지는데, 그게 진단서·처방전 사진이라 쌓아둘 이유가 없다.
 * 설정의 "캐시 지우기"는 Coil 이미지 캐시만 비우므로(SettingsViewModel.onClearCacheConfirmed)
 * 이 파일들을 정리하는 건 이 함수뿐이다. 안드로이드가 저장공간이 부족할 때 알아서 비워주긴 하지만,
 * 그때까지 남아 있는다는 뜻이라 우리가 필요 없어지는 시점에 직접 지운다. 부르는 곳은 두 군데다 —
 * 선택이 바뀔 때마다 도는 DocumentScanScreen(지금 쓰는 것만 남긴다)과, 화면이 아주 빠질 때
 * 남은 것까지 지우는 DocumentScanViewModel.onCleared(keep = null).
 *
 * [keep]이 갤러리에서 고른 이미지(MediaStore Uri)면 이름이 우리 파일과 겹치지 않아 촬영본이
 * 전부 지워진다 — 의도한 동작이다.
 */
internal fun clearCapturedImages(context: Context, keep: Uri?) {
    val keepName = keep?.lastPathSegment?.substringAfterLast('/')
    File(context.cacheDir, "images").listFiles()?.forEach { file ->
        if (file.name != keepName) {
            file.delete()
        }
    }
}
