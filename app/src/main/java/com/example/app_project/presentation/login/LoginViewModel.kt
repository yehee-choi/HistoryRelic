package com.example.app_project.presentation.login

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_project.data.repository.AuthRepository
import com.example.app_project.utils.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        authRepository.setupGoogleSignIn()
    }

    fun getGoogleSignInIntent() = authRepository.getGoogleSignInIntent()

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            authRepository.handleSignInResult(data)
                .onSuccess { idToken ->
                    Log.d("LoginViewModel", "구글 로그인 성공, ID 토큰 받음")

                    // 백엔드에 토큰 전송
                    authRepository.sendTokenToBackend(idToken)
                        .onSuccess { jwtToken ->
                            Log.d("LoginViewModel", "백엔드 토큰 전송 성공")
                            preferenceManager.saveJWTToken(jwtToken)

                            // JWT 토큰 테스트 추가!
                            authRepository.testJwtToken(jwtToken)
                                .onSuccess { message ->
                                    Log.d("LoginViewModel", "JWT 토큰 테스트 성공: $message")
                                    _loginState.value = LoginState.Success
                                }
                                .onFailure { error ->
                                    Log.e("LoginViewModel", "JWT 토큰 테스트 실패", error)
                                    // 토큰 테스트 실패해도 로그인은 성공으로 처리
                                    _loginState.value = LoginState.Success
                                }
                        }
                        .onFailure { error ->
                            _loginState.value = LoginState.Error(error.message ?: "백엔드 통신 실패")
                        }
                }
                .onFailure { error ->
                    _loginState.value = LoginState.Error(error.message ?: "구글 로그인 실패")
                }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
