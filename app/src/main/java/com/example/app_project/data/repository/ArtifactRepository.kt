package com.example.app_project.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import com.example.app_project.data.model.DashboardArtifact

class ArtifactRepository(
    private val context: Context,
    private val authRepository: AuthRepository // AuthRepository 사용
) {

    // ML Kit 한국어 텍스트 인식기
    private val textRecognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    /**
     * 이미지에서 OCR을 수행하여 텍스트를 추출 (실제 ML Kit 사용)
     */
    suspend fun performOCR(imageUri: Uri): Result<OcrResult> = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()

            // 이미지를 Bitmap으로 변환
            val bitmap = loadBitmapFromUri(imageUri)
                ?: return@withContext Result.failure(IOException("이미지를 로드할 수 없습니다"))

            // ML Kit을 사용한 실제 OCR 수행
            val recognizedText = performRealOCR(bitmap)

            val processingTime = System.currentTimeMillis() - startTime

            val ocrResult = OcrResult(
                id = UUID.randomUUID().toString(),
                text = recognizedText,
                confidence = 0.95f, // ML Kit에서 실제 confidence를 가져올 수 있습니다
                language = "ko",
                processingTime = processingTime,
                timestamp = Date(),
                imageUri = imageUri.toString(),
                isSuccess = recognizedText.isNotBlank(),
                errorMessage = if (recognizedText.isBlank()) "텍스트를 찾을 수 없습니다" else null
            )

            Result.success(ocrResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * OCR 텍스트를 사용하여 유물 검색 (AuthRepository의 기존 API 활용)
     */
    suspend fun searchByOcrText(token: String, ocrText: String): Result<List<DashboardArtifact>> {
        return try {
            // AuthRepository의 searchArtifacts 메서드 사용 (매개변수 2개만 전달)
            val searchResult = authRepository.searchArtifacts(token, ocrText)

            searchResult.fold(
                onSuccess = { searchResponse ->
                    // SearchApiResponse를 DashboardArtifact 리스트로 직접 변환
                    val artifacts = if (searchResponse.success) {
                        searchResponse.data.brief_info_list.map { briefInfo ->
                            DashboardArtifact(
                                id = briefInfo.id,
                                imgUri = briefInfo.imgThumUriM, // 중간 크기 썸네일 사용
                                nameKr = briefInfo.nameKr,
                                era = briefInfo.nationalityName // 시기 정보
                            )
                        }
                    } else {
                        emptyList()
                    }
                    Result.success(artifacts)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }

    //ML Kit > OCR 수행
    private suspend fun performRealOCR(bitmap: Bitmap): String = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)

        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val recognizedText = visionText.text
                continuation.resume(recognizedText)
            }
            .addOnFailureListener { e ->
                // 실패 시 빈 문자열 반환
                continuation.resume("")
            }
    }
}

// OcrResult 데이터 클래스 (누락된 부분)
data class OcrResult(
    val id: String,
    val text: String,
    val confidence: Float,
    val language: String,
    val processingTime: Long,
    val timestamp: Date,
    val imageUri: String,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)