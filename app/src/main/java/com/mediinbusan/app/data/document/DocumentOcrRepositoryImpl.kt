package com.mediinbusan.app.data.document

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.mediinbusan.app.core.common.Result
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

    override fun extractText(imageUri: Uri): Flow<Result<String>> = flow {
        emit(Result.Loading)
        try {
            val imageBytes = prepareImageBytes(imageUri)
            val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaType())
            val part = MultipartBody.Part.createFormData("image", "document.jpg", requestBody)
            emit(Result.Success(documentOcrApi.extractText(part).text))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Error(throwable = e))
        }
    }.flowOn(Dispatchers.IO)

    private fun prepareImageBytes(imageUri: Uri): ByteArray {
        val bitmap = decodeDownscaledBitmap(imageUri)
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
    }
}
