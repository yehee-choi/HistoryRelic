package com.example.app_project.presentation.navigation

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app_project.data.repository.AuthRepository
import com.example.app_project.data.model.DashboardArtifact
import com.example.app_project.presentation.camera.CameraScreen
import com.example.app_project.presentation.camera.ResultScreen
import com.example.app_project.presentation.detail.ArtifactDetailScreen
import com.example.app_project.presentation.history.HistoryScreen
import com.example.app_project.presentation.history.DetailViewModel
import com.example.app_project.presentation.home.HomeScreen
import com.example.app_project.presentation.login.LoginViewModel
import com.example.app_project.presentation.login.LoginScreen
import com.example.app_project.presentation.onboarding.OnBoardingScreen1
import com.example.app_project.presentation.onboarding.OnBoardingScreen2
import com.example.app_project.presentation.onboarding.OnBoardingScreen3
import com.example.app_project.utils.PreferenceManager

// 방어 코드 , 내부 DB, 특정 기간에 한 번 api호출, 없는 경우 > 유저 요청 리스트& 관리자 페이지 (pm)
// 지표세우기(AB testing), 클릭 횟수 > 지표,
// Onboarding 의 형태에 대한 기준, 검색 횟수 등, 기준에 대한 정의는 명확해야함.

@Composable
fun AppNavigation(
    onGoogleLogin: (LoginViewModel) -> Unit = {},
    preferenceManager: PreferenceManager
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    var searchResults by remember { mutableStateOf<List<DashboardArtifact>>(emptyList()) }

    NavHost(navController, startDestination = "onboarding1") {

        // 온보딩 화면들
        composable("onboarding1") {
            OnBoardingScreen1(
                onNextClick = { navController.navigate("onboarding2") }
            )
        }

        composable("onboarding2") {
            OnBoardingScreen2(
                onSkipClick = { navController.navigate("login") },
                onNextClick = { navController.navigate("onboarding3") }
            )
        }

        composable("onboarding3") {
            OnBoardingScreen3(
                onStartClick = { navController.navigate("login") }
            )
        }
        // 로그인 화면
        composable("login") {
            val authRepository = remember { AuthRepository(context) }
            val loginViewModel: LoginViewModel = viewModel {
                LoginViewModel(authRepository, preferenceManager)
            }

            val loginState by loginViewModel.loginState.collectAsState()
            // Handler
            // BackHandler 추가
            BackHandler(enabled = true) {
                // 아무것도 하지 않음 - 뒤로가기 차단
            }

            LoginScreen(
                loginState = loginState,
                onGoogleLogin = { onGoogleLogin(loginViewModel) },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackClick = { } // 뒤로가기 버튼도 동작 안하게
            )
        }
        // 홈 화면
        composable("home") {
            HomeScreen(
                onStartAnalysisClick = { navController.navigate("camera") }
            )
        }

        // 카메라 화면
        composable("camera") {
            CameraScreen(
                onBackClick = { navController.popBackStack() },
                onPhotoTaken = { uri ->
                    navController.navigate("result/${Uri.encode(uri.toString())}")
                }
            )
        }
        // 결과 화면
        composable("result/{imageUri}") { backStackEntry ->
            val imageUriString = backStackEntry.arguments?.getString("imageUri")
            val imageUri = Uri.parse(Uri.decode(imageUriString))

            ResultScreen(
                imageUri = imageUri,
                preferenceManager = preferenceManager,
                onBackClick = { navController.popBackStack() },
                onRetakeClick = { navController.popBackStack() },
                onNavigateToHistory = { artifacts ->
                    searchResults = artifacts
                    navController.navigate("history")
                }
            )
        }

        // 히스토리 화면
        composable("history") {
            Log.d("AppNavigation", "Composing HistoryScreen with ${searchResults.size} items")
            HistoryScreen(
                artifacts = searchResults,
                onBackClick = {
                    searchResults = emptyList()
                    navController.popBackStack()
                },
                onArtifactClick = { artifact ->
                    Log.d("AppNavigation", "History item clicked: ${artifact.nameKr}, ID: ${artifact.id}")
                    navController.navigate("detail/${artifact.id}")
                }
            )
        }

        // 상세 화면 - 단일 정의
        composable("detail/{artifactId}") { backStackEntry ->
            val artifactId = backStackEntry.arguments?.getString("artifactId") ?: ""

            // 수동으로 ViewModel 생성
            val authRepository = remember { AuthRepository(context) }
            val detailViewModel = remember { DetailViewModel(authRepository) }

            // ViewModel 상태 구독
            val artifactDetail by detailViewModel.artifactDetailState.collectAsState()
            val isLoading by detailViewModel.isLoading.collectAsState()
            val errorState by detailViewModel.errorState.collectAsState()

            // 화면 진입 시 데이터 로드
            LaunchedEffect(artifactId) {
                val token = preferenceManager.getJWTToken()
                if (token != null) {
                    detailViewModel.fetchArtifactDetail(artifactId, token)
                } else {
                    Log.w("AppNavigation", "JWT token is null, cannot fetch artifact detail")
                }
            }

            // 상세 화면 UI
            ArtifactDetailScreen(
                artifactDetail = artifactDetail,
                isLoading = isLoading,
                onBackClick = {
                    detailViewModel.clearDetailState()
                    navController.navigateUp()
                },
                onRelatedArtifactClick = { relatedId ->
                    detailViewModel.clearDetailState()
                    navController.navigate("detail/${relatedId}")
                }
            )

            // 에러 처리 (필요시)
            errorState?.let { error ->
                Log.e("AppNavigation", "Detail screen error: $error")
            }
        }
    }
}