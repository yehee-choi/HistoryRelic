package com.example.app_project.presentation.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_project.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.app_project.data.model.ArtifactDetail
import com.example.app_project.data.model.toArtifactDetail

class DetailViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _artifactDetailState = MutableStateFlow<ArtifactDetail?>(null)
    val artifactDetailState: StateFlow<ArtifactDetail?> = _artifactDetailState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    companion object {
        private const val TAG = "DetailViewModel"
    }

    fun fetchArtifactDetail(artifactId: String, token: String) {
        if (artifactId.isBlank() || token.isBlank()) {
            Log.w(TAG, "Artifact ID or Token is blank. Cannot fetch detail.")
            _errorState.value = "유물 정보 또는 인증 토큰이 유효하지 않습니다."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            Log.d(TAG, "Fetching detail for ID: $artifactId with token: $token")

            val result = authRepository.getArtifactDetail(token, artifactId)

            result.fold(
                onSuccess = { detailApiResponse ->
                    if (detailApiResponse.success) {
                        val artifactDetail = detailApiResponse.toArtifactDetail()
                        _artifactDetailState.value = artifactDetail
                        Log.i(TAG, "Artifact detail fetched successfully: ${artifactDetail?.nameKr}")
                        if (artifactDetail == null) {
                            Log.w(TAG, "Successfully fetched API but no detail info in list or conversion failed.")
                            _errorState.value = "유물 상세 정보를 변환하는데 실패했습니다."
                        }
                    } else {
                        Log.w(TAG, "API call successful but server indicated failure: ${detailApiResponse.message}")
                        _artifactDetailState.value = null
                        _errorState.value = "서버에서 유물 정보를 가져오지 못했습니다: ${detailApiResponse.message}"
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to fetch artifact detail", error)
                    _artifactDetailState.value = null
                    _errorState.value = "유물 정보 로딩 중 오류 발생: ${error.localizedMessage}"
                }
            )
            _isLoading.value = false
        }
    }

    fun clearDetailState() {
        _artifactDetailState.value = null
        _isLoading.value = false
        _errorState.value = null
    }

    fun clearError() {
        _errorState.value = null
    }
}