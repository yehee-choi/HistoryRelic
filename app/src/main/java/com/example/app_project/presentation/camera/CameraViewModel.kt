package com.example.app_project.presentation.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_project.data.repository.ArtifactRepository
import com.example.app_project.data.repository.AuthRepository
import com.example.app_project.data.model.DashboardArtifact
import com.example.app_project.data.model.toDashboardArtifactList
import com.example.app_project.utils.PreferenceManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



class CameraViewModel  constructor(
    private val context: Context,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    companion object {
        private const val TAG = "CameraViewModel"
    }

    // Repository 초기화
    private val authRepository = AuthRepository(context)
    private val artifactRepository = ArtifactRepository(context, authRepository)

    // Google ML Kit Text Recognizer
    private val textRecognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    private val _ocrText = MutableStateFlow("")
    val ocrText: StateFlow<String> = _ocrText.asStateFlow()

    private val _isOcrLoading = MutableStateFlow(false)
    val isOcrLoading: StateFlow<Boolean> = _isOcrLoading.asStateFlow()

    private val _ocrError = MutableStateFlow<String?>(null)
    val ocrError: StateFlow<String?> = _ocrError.asStateFlow()

    // 촬영된 이미지 상태
    private val _capturedImageUri = MutableStateFlow<Uri?>(null)
    val capturedImageUri: StateFlow<Uri?> = _capturedImageUri.asStateFlow()

    // 카메라 상태
    private val _isCameraReady = MutableStateFlow(false)
    val isCameraReady: StateFlow<Boolean> = _isCameraReady.asStateFlow()

    // 검색 결과 상태
    private val _searchResults = MutableStateFlow<List<DashboardArtifact>>(emptyList())
    val searchResults: StateFlow<List<DashboardArtifact>> = _searchResults.asStateFlow()

    private val _isSearchLoading = MutableStateFlow(false)
    val isSearchLoading: StateFlow<Boolean> = _isSearchLoading.asStateFlow()

    // 네비게이션 상태 추가
    private val _shouldNavigateToHistory = MutableStateFlow(false)
    val shouldNavigateToHistory: StateFlow<Boolean> = _shouldNavigateToHistory.asStateFlow()

    init {
        Log.d(TAG, "CameraViewModel 초기화")
        authRepository.setupGoogleSignIn()
    }

    /**
     * 이미지 URI를 저장하고 로컬 OCR 처리만 수행
     */
    fun onPhotoTaken(imageUri: Uri) {
        Log.d(TAG, "사진 촬영됨: $imageUri")
        _capturedImageUri.value = imageUri
        performLocalOCR(imageUri)
    }

    /**
     * 로컬 OCR 처리 수행 (Google ML Kit 사용)
     */
    fun performOCR(imageUri: Uri) {
        Log.d(TAG, "OCR 수행 요청: $imageUri")
        performLocalOCR(imageUri)
    }

    /**
     * 로컬 OCR 처리 수행 (Google ML Kit 직접 사용)
     */
    private fun performLocalOCR(imageUri: Uri) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "OCR 처리 시작")
                _isOcrLoading.value = true
                _ocrError.value = null

                // InputImage 생성
                Log.d(TAG, "InputImage 생성 중...")
                val inputImage = InputImage.fromFilePath(context, imageUri)
                Log.d(TAG, "InputImage 생성 완료: ${inputImage.width}x${inputImage.height}")

                // Google ML Kit으로 텍스트 인식
                Log.d(TAG, "텍스트 인식 시작...")
                val visionText = textRecognizer.process(inputImage).await()
                val recognizedText = visionText.text

                Log.d(TAG, "텍스트 인식 완료")
                Log.d(TAG, "인식된 텍스트 길이: ${recognizedText.length}")
                Log.d(TAG, "인식된 텍스트: '$recognizedText'")

                _ocrText.value = recognizedText

                if (recognizedText.isBlank()) {
                    Log.w(TAG, "텍스트가 인식되지 않음")
                    _ocrError.value = "텍스트를 찾을 수 없습니다"
                } else {
                    Log.d(TAG, "텍스트 인식 성공!")
                }

            } catch (e: Exception) {
                Log.e(TAG, "OCR 처리 실패", e)
                _ocrError.value = when (e) {
                    is java.io.FileNotFoundException -> {
                        Log.e(TAG, "이미지 파일을 찾을 수 없음: ${e.message}")
                        "이미지 파일을 찾을 수 없습니다"
                    }
                    else -> {
                        Log.e(TAG, "OCR 오류: ${e.message}")
                        "텍스트 인식에 실패했습니다: ${e.message}"
                    }
                }
            } finally {
                _isOcrLoading.value = false
                Log.d(TAG, "OCR 처리 종료")
            }
        }
    }

    /**
     * 유물 검색하기 버튼 클릭 시 호출 - 서버로 텍스트 전송
     */
//    fun searchArtifactsByOcrText(ocrText: String) {
//        Log.d(TAG, "유물 검색 시작: '$ocrText'")
//
//        val token = preferenceManager.getJWTToken()
//        Log.d(TAG, "JWT 토큰 확인: ${if (token.isNullOrEmpty()) "없음" else "있음 (${token.take(20)}...)"}")
//
//        if (token.isNullOrEmpty()) {
//            Log.w(TAG, "JWT 토큰이 없어서 검색 불가")
//            _ocrError.value = "로그인이 필요합니다"
//            return
//        }
//
//        viewModelScope.launch {
//            try {
//                _isSearchLoading.value = true
//                Log.d(TAG, "서버로 검색 요청 전송...")
//
//                // 서버로 OCR 텍스트 전송하여 유물 검색
//                val searchResult = authRepository.searchArtifacts(token, ocrText)
//
//                Log.d(TAG, "서버 응답 받음")
//
//                if (searchResult.isSuccess) {
//                    val searchResponse = searchResult.getOrNull()
//                    val artifacts = searchResponse?.toDashboardArtifactList() ?: emptyList()
//                    _searchResults.value = artifacts
//
//                    Log.d(TAG, "검색 성공! 결과 개수: ${artifacts.size}")
//                    artifacts.forEachIndexed { index, artifact ->
//                        Log.d(TAG, "결과 $index: ${artifact.nameKr}")
//                    }
//
//                    if (artifacts.isEmpty()) {
//                        Log.w(TAG, "검색 결과가 없음")
//                        _ocrError.value = "검색 결과가 없습니다"
//                    } else {
//                        // 검색 결과가 있으면 HistoryScreen으로 이동 트리거
//                        _shouldNavigateToHistory.value = true
//                        Log.d(TAG, "HistoryScreen으로 네비게이션 트리거")
//                    }
//                } else {
//                    val error = searchResult.exceptionOrNull()
//                    Log.e(TAG, "검색 실패", error)
//                    _ocrError.value = "유물 검색에 실패했습니다: ${error?.message}"
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "검색 중 예외 발생", e)
//                _ocrError.value = "검색 중 오류가 발생했습니다: ${e.message}"
//            } finally {
//                _isSearchLoading.value = false
//                Log.d(TAG, "유물 검색 종료")
//            }
//        }
//    }
    /**
     * 유물 검색하기 버튼 클릭 시 호출 - 서버로 텍스트 전송
     */
    /**
     * 유물 검색하기 버튼 클릭 시 호출 - 서버로 텍스트 전송
     */
    fun searchArtifactsByOcrText(ocrText: String) {
        Log.d(TAG, "유물 검색 시작: '$ocrText'")

        val token = preferenceManager.getJWTToken()
        Log.d(TAG, "JWT 토큰 확인: ${if (token.isNullOrEmpty()) "없음" else "있음 (${token.take(20)}...)"}")

        if (token.isNullOrEmpty()) {
            Log.w(TAG, "JWT 토큰이 없어서 검색 불가")
            _ocrError.value = "로그인이 필요합니다"
            return
        }

        viewModelScope.launch {
            try {
                _isSearchLoading.value = true
                Log.d(TAG, "서버로 검색 요청 전송...")

                // 서버로 OCR 텍스트 전송하여 유물 검색
                val searchResult = authRepository.searchArtifacts(token, ocrText)

                Log.d(TAG, "서버 응답 받음")

                if (searchResult.isSuccess) {
                    val searchResponse = searchResult.getOrNull()

                    // 서버 응답 전체 로깅 (null이어도 확인)
                    Log.d(TAG, "=== ViewModel에서 서버 응답 상세 정보 ===")
                    Log.d(TAG, "응답 객체: $searchResponse")
                    Log.d(TAG, "응답이 null인가?: ${searchResponse == null}")

                    if (searchResponse != null) {
                        Log.d(TAG, "응답 객체 타입: ${searchResponse::class.java.simpleName}")
                        Log.d(TAG, "success: ${searchResponse.success}")
                        Log.d(TAG, "message: ${searchResponse.message}")
                        Log.d(TAG, "userId: ${searchResponse.userId}")
                        Log.d(TAG, "totalCount: ${searchResponse.data.totalCount}")
                        Log.d(TAG, "brief_info_list 크기: ${searchResponse.data.brief_info_list.size}")

                        // 각 brief_info 확인
                        searchResponse.data.brief_info_list.forEachIndexed { index, briefInfo ->
                            Log.d(TAG, "ViewModel - brief_info[$index]: ${briefInfo.nameKr}")
                        }
                    }

                    val artifacts = searchResponse?.toDashboardArtifactList() ?: emptyList()

                    // toDashboardArtifactList() 변환 결과도 상세 로깅
                    Log.d(TAG, "변환된 artifacts 리스트:")
                    Log.d(TAG, "artifacts 개수: ${artifacts.size}")
                    Log.d(TAG, "artifacts가 비어있는가?: ${artifacts.isEmpty()}")

                    artifacts.forEachIndexed { index, artifact ->
                        Log.d(TAG, "=== ViewModel 변환 결과 $index ===")
                        Log.d(TAG, "nameKr: ${artifact.nameKr}")
                        Log.d(TAG, "id: ${artifact.id}")
                        Log.d(TAG, "imgUri: ${artifact.imgUri}")
                        Log.d(TAG, "era: ${artifact.era}")
                        Log.d(TAG, "전체 artifact: $artifact")
                    }

                    _searchResults.value = artifacts

                    if (artifacts.isEmpty()) {
                        Log.w(TAG, "검색 결과가 없음")
                        if (searchResponse != null && searchResponse.success) {
                            Log.w(TAG, "서버 응답은 성공이지만 brief_info_list가 비어있음")
                            Log.w(TAG, "서버 totalCount: ${searchResponse.data.totalCount}")
                        }
                        _ocrError.value = "검색 결과가 없습니다"
                    } else {
                        // 검색 결과가 있으면 HistoryScreen으로 이동 트리거
                        _shouldNavigateToHistory.value = true
                        Log.d(TAG, "HistoryScreen으로 네비게이션 트리거")
                    }
                } else {
                    val error = searchResult.exceptionOrNull()
                    Log.e(TAG, "=== 검색 실패 상세 정보 ===")
                    Log.e(TAG, "검색 실패 - 서버 응답 실패")
                    Log.e(TAG, "에러 타입: ${error?.javaClass?.simpleName}")
                    Log.e(TAG, "에러 메시지: ${error?.message}")
                    Log.e(TAG, "전체 에러: $error")
                    _ocrError.value = "유물 검색에 실패했습니다: ${error?.message}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "=== 검색 중 예외 발생 ===")
                Log.e(TAG, "예외 타입: ${e.javaClass.simpleName}")
                Log.e(TAG, "예외 메시지: ${e.message}")
                Log.e(TAG, "스택 트레이스:", e)
                _ocrError.value = "검색 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isSearchLoading.value = false
                Log.d(TAG, "유물 검색 종료")
            }
        }
    }
    /**
     * 네비게이션 완료 후 호출 (상태 초기화)
     */
    fun onNavigatedToHistory() {
        Log.d(TAG, "네비게이션 완료, 상태 초기화")
        _shouldNavigateToHistory.value = false
    }

    /**
     * OCR 재시도 (로컬 OCR만)
     */
    fun retryOCR() {
        Log.d(TAG, "OCR 재시도 요청")
        _capturedImageUri.value?.let { uri ->
            performLocalOCR(uri)
        }
    }

    /**
     * 상태 초기화 (새로운 촬영 시)
     */
    fun resetCameraState() {
        Log.d(TAG, "카메라 상태 초기화")
        _ocrText.value = ""
        _ocrError.value = null
        _isOcrLoading.value = false
        _isSearchLoading.value = false
        _capturedImageUri.value = null
        _searchResults.value = emptyList()
        _shouldNavigateToHistory.value = false
    }

    /**
     * 카메라 준비 상태 설정
     */
    fun setCameraReady(isReady: Boolean) {
        Log.d(TAG, "카메라 준비 상태: $isReady")
        _isCameraReady.value = isReady
    }

    /**
     * 수동 유물 검색 (옵션)
     */
    fun searchArtifacts(query: String) {
        searchArtifactsByOcrText(query)
    }

    /**
     * 에러 상태 클리어
     */
    fun clearError() {
        Log.d(TAG, "에러 상태 클리어")
        _ocrError.value = null
    }

    /**
     * 텍스트 수동 설정 (편집 기능용)
     */
    fun updateOcrText(newText: String) {
        Log.d(TAG, "OCR 텍스트 수동 업데이트: '$newText'")
        _ocrText.value = newText
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel 정리 중...")
        textRecognizer.close()
    }
}