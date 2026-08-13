package com.mediinbusan.app.data.document

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.mediinbusan.app.core.common.Result
import com.mediinbusan.app.core.datastore.SupportedLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * MediInBusan 자체 백엔드(backend/, com.mediinbusan.backend.document)의 CLOVA OCR 프록시를 호출한다.
 * 백엔드 업로드 제한(10MB)과 무관하게, 카메라 원본은 보통 그보다 훨씬 커서 업로드 전 항상
 * 다운스케일·재압축한다.
 */
class DocumentOcrRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentOcrApi: DocumentOcrApi
) : DocumentOcrRepository {

    override fun extractText(imageUri: Uri, targetLanguage: String?): Flow<Result<DocumentOcrResult>> = flow {
        emit(Result.Loading)
        try {
            val imageBytes = prepareImageBytes(imageUri)
            val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaType())
            val part = MultipartBody.Part.createFormData("image", "document.jpg", requestBody)
            val response = documentOcrApi.extractText(part, toPapagoLanguageCode(targetLanguage))
            emit(Result.Success(DocumentOcrResult(response.text, response.translatedText, response.targetLanguage)))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e))
        }
    }.flowOn(Dispatchers.IO)

    // 원본 문서는 한국어라고 가정하므로(백엔드 SOURCE_LANGUAGE 고정) 대상 언어가 한국어면 번역이
    // 의미가 없어 요청 자체를 생략한다. 앱의 SupportedLanguage.ZH("zh")는 Papago 코드(zh-CN)와
    // 달라서 별도로 변환해야 한다.
    private fun toPapagoLanguageCode(languageCode: String?): String? = when (languageCode) {
        null, SupportedLanguage.KO.code -> null
        SupportedLanguage.ZH.code -> "zh-CN"
        else -> languageCode
    }

    // 다운스케일 → EXIF 방향 보정 → 용량 제한(MAX_UPLOAD_BYTES) 안에 들어올 때까지 품질을 낮추고,
    // 그래도 안 되면 비트맵을 추가로 축소해서 재시도한다. 그래도 실패하면 업로드 대신 에러를 낸다.
    private fun prepareImageBytes(imageUri: Uri): ByteArray {
        var bitmap = decodeDownscaledBitmap(imageUri)
        bitmap = applyExifRotation(bitmap, readExifRotationDegrees(imageUri))

        repeat(MAX_DOWNSCALE_ATTEMPTS) { attempt ->
            val bytes = compressToJpeg(bitmap)
            if (bytes.size <= MAX_UPLOAD_BYTES) return bytes
            if (attempt < MAX_DOWNSCALE_ATTEMPTS - 1) {
                val resized = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
                bitmap.recycle()
                bitmap = resized
            }
        }
        error("이미지 용량이 너무 커서 업로드할 수 없습니다.")
    }

    private fun compressToJpeg(bitmap: Bitmap): ByteArray {
        val output = ByteArrayOutputStream()
        var quality = INITIAL_JPEG_QUALITY
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
        while (output.size() > MAX_UPLOAD_BYTES && quality > MIN_JPEG_QUALITY) {
            output.reset()
            quality -= JPEG_QUALITY_STEP
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
        }
        return output.toByteArray()
    }

    // BitmapFactory는 EXIF Orientation 태그를 읽지 않고 픽셀만 디코드한다 — 세로로 찍은 사진이
    // 가로 픽셀 + 회전 태그로 저장된 경우, 이 보정 없이 재인코딩하면 텍스트가 옆으로 눕는다.
    private fun readExifRotationDegrees(imageUri: Uri): Int {
        val orientation = context.contentResolver.openInputStream(imageUri)?.use { input ->
            ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    private fun applyExifRotation(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated !== bitmap) bitmap.recycle()
        return rotated
    }

    private fun decodeDownscaledBitmap(imageUri: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(imageUri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        }
        val sampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, MAX_DIMENSION_PX)
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return context.contentResolver.openInputStream(imageUri)?.use { input ->
            BitmapFactory.decodeStream(input, null, decodeOptions)
        } ?: error("이미지를 읽을 수 없습니다: $imageUri")
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        var sampleSize = 1
        while (width / sampleSize > maxDimension || height / sampleSize > maxDimension) {
            sampleSize *= 2
        }
        return sampleSize
    }

    companion object {
        private const val MAX_DIMENSION_PX = 2000
        private const val MAX_UPLOAD_BYTES = 8 * 1024 * 1024 // 백엔드 10MB 제한에 여유를 둔다.
        private const val INITIAL_JPEG_QUALITY = 90
        private const val MIN_JPEG_QUALITY = 40
        private const val JPEG_QUALITY_STEP = 10
        private const val MAX_DOWNSCALE_ATTEMPTS = 3 // 2000px에서 3회면 250px까지 축소 — 그래도 넘으면 포기하고 에러.
    }
}
