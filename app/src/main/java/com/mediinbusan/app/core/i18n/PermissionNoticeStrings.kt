package com.mediinbusan.app.core.i18n

/**
 * 앱 접근권한 사전 고지·동의(정보통신망법 제22조의2, 방송미디어통신위원회 "앱 접근권한 동의
 * 가이드라인") 문구. 원스토어 검증 의견(2026-09-02, OA01008717)으로 추가됐다 — 카메라 권한을
 * 요구하면서 사전 고지·동의 절차가 없다는 지적이었다.
 *
 * 세 곳에서 함께 쓴다: ① 최초 실행 시 고지 화면(feature/permission), ② 설정(S-10) > 정보 >
 * 앱 접근권한, ③ 문서 스캔에서 카메라 권한을 요청하기 직전 사전 고지 다이얼로그.
 * 설정 리스트 행의 제목/설명까지 여기 두는 이유는(다른 행은 SettingsStrings에 있다) 접근권한
 * 문구는 법정 고지 문구라 화면·다이얼로그와 한 파일에서 같이 관리되는 편이 안전해서다.
 */
data class PermissionNoticeStrings(
    val screenTitle: String,
    val intro: String,
    val requiredSectionTitle: String,
    val requiredBody: String,
    val optionalSectionTitle: String,
    val cameraTitle: String,
    val cameraBody: String,
    val optionalNotice: String,
    val withdrawSectionTitle: String,
    val withdrawBody: String,
    val openSystemSettingsButton: String,
    val confirmButton: String,
    val settingsRowDescription: String,
    val rationaleTitle: String,
    val rationaleBody: String,
    val rationaleAllowButton: String,
    val rationaleLaterButton: String,
    val deniedTitle: String,
    val deniedBody: String,
    val closeButton: String
) {
    companion object {
        val Ko = PermissionNoticeStrings(
            screenTitle = "앱 접근권한 안내",
            intro = "메디인부산은 서비스 제공에 꼭 필요한 범위에서만 기기 접근권한을 요청합니다. 아래 내용을 확인하신 후 이용해 주세요.",
            requiredSectionTitle = "필수 접근권한",
            requiredBody = "필수 접근권한이 없습니다. 회원가입 없이 의료기관 검색·이용 가이드·지도·주변 관광 정보 등 기본 기능을 모두 이용하실 수 있습니다.",
            optionalSectionTitle = "선택 접근권한",
            cameraTitle = "카메라",
            cameraBody = "문서 스캔(진단서·처방전) 기능에서 문서를 촬영할 때만 사용합니다. 촬영한 이미지는 문자 인식·번역 처리에만 이용되며 서버에 따로 저장되지 않습니다.",
            optionalNotice = "선택 접근권한은 허용하지 않아도 앱을 이용하실 수 있습니다. 카메라를 허용하지 않으시면 갤러리에서 이미지를 선택해 문서 스캔을 그대로 이용하실 수 있습니다.",
            withdrawSectionTitle = "접근권한 철회 방법",
            withdrawBody = "허용한 접근권한은 언제든지 철회할 수 있습니다.\n휴대전화 설정 > 애플리케이션(앱) > 메디인부산 > 권한에서 항목별로 변경하시거나, 아래 '권한 설정 열기' 버튼으로 바로 이동하실 수 있습니다.",
            openSystemSettingsButton = "권한 설정 열기",
            confirmButton = "확인했습니다",
            settingsRowDescription = "앱이 사용하는 접근권한과 철회 방법을 확인하세요",
            rationaleTitle = "카메라 접근권한이 필요해요",
            rationaleBody = "진단서·처방전을 촬영해 텍스트를 인식하려면 카메라 접근권한이 필요합니다. 선택 접근권한이라 허용하지 않아도 앱 이용에는 제한이 없고, 갤러리에서 이미지를 선택해 같은 기능을 이용하실 수 있어요.",
            rationaleAllowButton = "허용하기",
            rationaleLaterButton = "나중에",
            deniedTitle = "카메라 접근권한이 꺼져 있어요",
            deniedBody = "설정에서 카메라 접근권한을 허용하면 문서를 바로 촬영할 수 있어요. 지금도 갤러리에서 이미지를 선택해 문서 스캔을 이용하실 수 있습니다.",
            closeButton = "닫기"
        )

        val En = PermissionNoticeStrings(
            screenTitle = "App permissions",
            intro = "MediIn Busan only asks for device permissions that a specific feature actually needs. Please review the details below before you start.",
            requiredSectionTitle = "Required permissions",
            requiredBody = "There are no required permissions. You can use every core feature — hospital search, the medical guide, the map, and nearby tourism info — without signing up or granting anything.",
            optionalSectionTitle = "Optional permissions",
            cameraTitle = "Camera",
            cameraBody = "Used only when you photograph a document in Document Scan (medical certificates and prescriptions). The photo is used solely for text recognition and translation, and is not stored on our server.",
            optionalNotice = "You can use the app without granting optional permissions. If you don't allow camera access, you can still use Document Scan by picking an image from your gallery.",
            withdrawSectionTitle = "How to withdraw a permission",
            withdrawBody = "You can withdraw a permission at any time.\nGo to your phone's Settings > Apps > MediIn Busan > Permissions to change each item, or tap 'Open permission settings' below to go there directly.",
            openSystemSettingsButton = "Open permission settings",
            confirmButton = "I understand",
            settingsRowDescription = "See which permissions the app uses and how to withdraw them",
            rationaleTitle = "Camera access is needed",
            rationaleBody = "To photograph a medical certificate or prescription and recognize its text, the app needs camera access. It is an optional permission — declining does not limit the rest of the app, and you can use the same feature by picking an image from your gallery.",
            rationaleAllowButton = "Allow",
            rationaleLaterButton = "Not now",
            deniedTitle = "Camera access is turned off",
            deniedBody = "Allow camera access in Settings to photograph documents directly. You can still use Document Scan right now by picking an image from your gallery.",
            closeButton = "Close"
        )

        val Zh = PermissionNoticeStrings(
            screenTitle = "应用访问权限说明",
            intro = "MediIn Busan 仅在特定功能确实需要时才请求设备访问权限。请先确认以下内容后再使用。",
            requiredSectionTitle = "必需访问权限",
            requiredBody = "没有必需访问权限。无需注册，即可使用医疗机构搜索、就医指南、地图、周边旅游信息等全部基本功能。",
            optionalSectionTitle = "可选访问权限",
            cameraTitle = "相机",
            cameraBody = "仅在文档扫描（诊断书·处方笺）功能中拍摄文件时使用。拍摄的图片仅用于文字识别与翻译处理，不会另行保存在服务器上。",
            optionalNotice = "即使不同意可选访问权限，也可以正常使用应用。若不允许使用相机，您仍可从相册中选择图片来使用文档扫描功能。",
            withdrawSectionTitle = "撤回访问权限的方法",
            withdrawBody = "已允许的访问权限可以随时撤回。\n请在手机设置 > 应用程序 > MediIn Busan > 权限中逐项更改，或点击下方的“打开权限设置”按钮直接前往。",
            openSystemSettingsButton = "打开权限设置",
            confirmButton = "我知道了",
            settingsRowDescription = "查看应用使用的访问权限及撤回方法",
            rationaleTitle = "需要相机访问权限",
            rationaleBody = "拍摄诊断书·处方笺并识别文字需要相机访问权限。这是可选权限，不同意也不会影响应用的其他功能，您可以从相册中选择图片来使用相同功能。",
            rationaleAllowButton = "允许",
            rationaleLaterButton = "以后再说",
            deniedTitle = "相机访问权限已关闭",
            deniedBody = "在设置中允许相机访问权限后即可直接拍摄文件。现在您也可以从相册中选择图片来使用文档扫描。",
            closeButton = "关闭"
        )

        val Ja = PermissionNoticeStrings(
            screenTitle = "アプリのアクセス権限について",
            intro = "MediIn Busanは、機能に本当に必要な範囲でのみ端末のアクセス権限をリクエストします。以下の内容をご確認のうえご利用ください。",
            requiredSectionTitle = "必須のアクセス権限",
            requiredBody = "必須のアクセス権限はありません。会員登録なしで、医療機関検索・利用ガイド・地図・周辺観光情報などの基本機能をすべてご利用いただけます。",
            optionalSectionTitle = "任意のアクセス権限",
            cameraTitle = "カメラ",
            cameraBody = "文書スキャン（診断書・処方箋）機能で書類を撮影するときにのみ使用します。撮影した画像は文字認識・翻訳の処理にのみ利用され、サーバーに保存されることはありません。",
            optionalNotice = "任意のアクセス権限は、許可しなくてもアプリをご利用いただけます。カメラを許可しない場合も、ギャラリーから画像を選んで文書スキャンをそのままご利用いただけます。",
            withdrawSectionTitle = "アクセス権限の撤回方法",
            withdrawBody = "許可したアクセス権限はいつでも撤回できます。\n端末の設定 > アプリ > MediIn Busan > 権限から項目ごとに変更するか、下の「権限設定を開く」ボタンから直接移動できます。",
            openSystemSettingsButton = "権限設定を開く",
            confirmButton = "確認しました",
            settingsRowDescription = "アプリが使用するアクセス権限と撤回方法をご確認ください",
            rationaleTitle = "カメラへのアクセス権限が必要です",
            rationaleBody = "診断書・処方箋を撮影して文字を認識するには、カメラへのアクセス権限が必要です。任意の権限のため、許可しなくてもアプリの利用に制限はなく、ギャラリーから画像を選んで同じ機能をご利用いただけます。",
            rationaleAllowButton = "許可する",
            rationaleLaterButton = "あとで",
            deniedTitle = "カメラへのアクセス権限がオフになっています",
            deniedBody = "設定でカメラへのアクセス権限を許可すると、その場で書類を撮影できます。今もギャラリーから画像を選んで文書スキャンをご利用いただけます。",
            closeButton = "閉じる"
        )
    }
}
