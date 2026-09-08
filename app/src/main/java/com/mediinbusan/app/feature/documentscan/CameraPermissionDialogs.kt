package com.mediinbusan.app.feature.documentscan

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.mediinbusan.app.core.designsystem.CoralPrimary
import com.mediinbusan.app.core.designsystem.CoralPrimaryContainer
import com.mediinbusan.app.core.designsystem.SettingsBorder
import com.mediinbusan.app.core.designsystem.SettingsDescriptionStyle
import com.mediinbusan.app.core.designsystem.SettingsItemTitleStyle
import com.mediinbusan.app.core.designsystem.SettingsPrimaryText
import com.mediinbusan.app.core.designsystem.SettingsSecondaryText
import com.mediinbusan.app.core.i18n.LocalAppStrings

/**
 * 시스템 권한 팝업을 띄우기 **직전**에 목적을 먼저 알리는 사전 고지 다이얼로그
 * (방송미디어통신위원회 "앱 접근권한 동의 가이드라인" — 권한 요청 시점의 고지).
 * 최초 실행 때 본 전체 고지(feature/permission)와 별개로, 실제 요청 순간에도 한 번 더 알린다.
 */
@Composable
fun CameraPermissionRationaleDialog(onAllow: () -> Unit, onDismiss: () -> Unit) {
    val strings = LocalAppStrings.current.permissionNotice
    PermissionDialogScaffold(
        title = strings.rationaleTitle,
        body = strings.rationaleBody,
        onDismiss = onDismiss,
        confirmLabel = strings.rationaleAllowButton,
        onConfirm = onAllow,
        dismissLabel = strings.rationaleLaterButton
    )
}

/**
 * 권한을 거부한 뒤(특히 "다시 묻지 않음" 상태) 다시 촬영을 시도했을 때 — 시스템 팝업이 더는 뜨지
 * 않으므로 철회/재허용 경로(설정)를 직접 안내한다. 여기서도 갤러리로 계속 이용할 수 있다는 점을
 * 같이 알린다(선택 접근권한이라는 사실의 재확인).
 */
@Composable
fun CameraPermissionDeniedDialog(onOpenSettings: () -> Unit, onDismiss: () -> Unit) {
    val strings = LocalAppStrings.current.permissionNotice
    PermissionDialogScaffold(
        title = strings.deniedTitle,
        body = strings.deniedBody,
        onDismiss = onDismiss,
        confirmLabel = strings.openSystemSettingsButton,
        onConfirm = onOpenSettings,
        dismissLabel = strings.closeButton
    )
}

/** 설정(S-10)의 캐시 삭제 다이얼로그와 같은 양식 — 원형 코랄 아이콘 + 가운데 정렬 본문 + 2버튼. */
@Composable
private fun PermissionDialogScaffold(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        icon = {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(CoralPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = CoralPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                style = SettingsItemTitleStyle.copy(fontSize = 17.sp),
                color = SettingsPrimaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = body,
                style = SettingsDescriptionStyle,
                color = SettingsSecondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = confirmLabel)
                }
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, SettingsBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SettingsSecondaryText),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = dismissLabel)
                }
            }
        }
    )
}

/**
 * 카메라 권한을 한 번 거부당한 뒤에도 시스템 팝업을 다시 띄울 수 있는 상태인지.
 *
 * 주의: 이 값은 "아직 물어본 적 없음"과 "다시 묻지 않음(영구 거부)" 두 경우 모두 false다 —
 * 두 상태를 가르는 건 DataStore에 남기는 요청 이력(UserPreferencesKeys.CAMERA_PERMISSION_REQUESTED)
 * 쪽이고, 이 함수는 "요청한 적은 있다"가 확인된 뒤에만 의미가 있다.
 */
fun Context.shouldShowCameraPermissionRationale(): Boolean {
    val activity = findActivity() ?: return false
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.CAMERA)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
