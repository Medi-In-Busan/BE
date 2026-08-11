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
    val comingSoonMessage: String
) {
    companion object {
        val Ko = DocumentScanStrings(
            introTitle = "진단서·처방전을 스캔해보세요",
            introSubtitle = "사진을 촬영하거나 갤러리에서 선택하면 번역해서 보여드려요.",
            captureButton = "촬영하기",
            galleryButton = "갤러리에서 선택",
            retakeButton = "다시 선택",
            analyzeButton = "분석하기",
            previewImageContentDescription = "선택한 문서 이미지",
            cameraPermissionDeniedMessage = "카메라를 사용하려면 권한이 필요합니다.",
            comingSoonMessage = "분석 기능은 준비 중이에요."
        )
        val En = DocumentScanStrings(
            introTitle = "Scan a diagnosis certificate or prescription",
            introSubtitle = "Take a photo or pick one from your gallery and we'll translate it for you.",
            captureButton = "Take photo",
            galleryButton = "Choose from gallery",
            retakeButton = "Choose again",
            analyzeButton = "Analyze",
            previewImageContentDescription = "Selected document image",
            cameraPermissionDeniedMessage = "Camera permission is required to use this feature.",
            comingSoonMessage = "Analysis is coming soon."
        )
        val Zh = DocumentScanStrings(
            introTitle = "扫描诊断书·处方笺",
            introSubtitle = "拍照或从相册中选择，我们将为您翻译。",
            captureButton = "拍照",
            galleryButton = "从相册选择",
            retakeButton = "重新选择",
            analyzeButton = "开始分析",
            previewImageContentDescription = "已选择的文档图片",
            cameraPermissionDeniedMessage = "使用相机需要获得权限。",
            comingSoonMessage = "分析功能正在开发中。"
        )
        val Ja = DocumentScanStrings(
            introTitle = "診断書・処方箋をスキャンしてみましょう",
            introSubtitle = "写真を撮影するかギャラリーから選択すると、翻訳してお見せします。",
            captureButton = "撮影する",
            galleryButton = "ギャラリーから選択",
            retakeButton = "選び直す",
            analyzeButton = "分析する",
            previewImageContentDescription = "選択した文書画像",
            cameraPermissionDeniedMessage = "カメラを使用するには権限が必要です。",
            comingSoonMessage = "分析機能は準備中です。"
        )
    }
}
