package com.mediinbusan.app.core.i18n

data class DocumentScanStrings(
    val introTitle: String,
    val introSubtitle: String,
    val captureButton: String,
    val galleryButton: String,
    val retakeButton: String,
    val analyzeButton: String,
    val previewImageContentDescription: String,
    val cameraPermissionDeniedMessage: String,
    val cameraUnavailableMessage: String,
    val analyzingMessage: String,
    val resultTitle: String,
    val analysisErrorFallback: String
) {
    companion object {
        val Ko = DocumentScanStrings(
            introTitle = "진단서·처방전을 스캔해보세요",
            introSubtitle = "사진을 촬영하거나 갤러리에서 선택하면 텍스트를 인식해서 보여드려요.",
            captureButton = "촬영하기",
            galleryButton = "갤러리에서 선택",
            retakeButton = "다시 선택",
            analyzeButton = "분석하기",
            previewImageContentDescription = "선택한 문서 이미지",
            cameraPermissionDeniedMessage = "카메라를 사용하려면 권한이 필요합니다.",
            cameraUnavailableMessage = "이 기기에서는 카메라를 사용할 수 없습니다.",
            analyzingMessage = "이미지를 분석하고 있어요...",
            resultTitle = "인식된 텍스트",
            analysisErrorFallback = "분석에 실패했어요. 다시 시도해주세요."
        )
        val En = DocumentScanStrings(
            introTitle = "Scan a diagnosis certificate or prescription",
            introSubtitle = "Take a photo or pick one from your gallery and we'll recognize the text for you.",
            captureButton = "Take photo",
            galleryButton = "Choose from gallery",
            retakeButton = "Choose again",
            analyzeButton = "Analyze",
            previewImageContentDescription = "Selected document image",
            cameraPermissionDeniedMessage = "Camera permission is required to use this feature.",
            cameraUnavailableMessage = "Camera is not available on this device.",
            analyzingMessage = "Analyzing your image...",
            resultTitle = "Recognized text",
            analysisErrorFallback = "Analysis failed. Please try again."
        )
        val Zh = DocumentScanStrings(
            introTitle = "扫描诊断书·处方笺",
            introSubtitle = "拍照或从相册中选择，我们将为您识别文字。",
            captureButton = "拍照",
            galleryButton = "从相册选择",
            retakeButton = "重新选择",
            analyzeButton = "开始分析",
            previewImageContentDescription = "已选择的文档图片",
            cameraPermissionDeniedMessage = "使用相机需要获得权限。",
            cameraUnavailableMessage = "此设备无法使用相机。",
            analyzingMessage = "正在分析图片...",
            resultTitle = "识别出的文字",
            analysisErrorFallback = "分析失败，请重试。"
        )
        val Ja = DocumentScanStrings(
            introTitle = "診断書・処方箋をスキャンしてみましょう",
            introSubtitle = "写真を撮影するかギャラリーから選択すると、テキストを認識してお見せします。",
            captureButton = "撮影する",
            galleryButton = "ギャラリーから選択",
            retakeButton = "選び直す",
            analyzeButton = "分析する",
            previewImageContentDescription = "選択した文書画像",
            cameraPermissionDeniedMessage = "カメラを使用するには権限が必要です。",
            cameraUnavailableMessage = "この端末ではカメラを使用できません。",
            analyzingMessage = "画像を分析しています...",
            resultTitle = "認識されたテキスト",
            analysisErrorFallback = "分析に失敗しました。もう一度お試しください。"
        )
    }
}
