package com.example.app_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.app_project.presentation.camera.CameraScreen
import com.example.app_project.presentation.login.LoginViewModel
import com.example.app_project.presentation.navigation.AppNavigation
import com.example.app_project.presentation.onboarding.OnBoardingScreen1
import com.example.app_project.ui.theme.App_ProjectTheme
import com.example.app_project.utils.PreferenceManager


class MainActivity : ComponentActivity() {


    private lateinit var preferenceManager: PreferenceManager
    private var currentLoginViewModel: LoginViewModel? = null

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        currentLoginViewModel?.handleGoogleSignInResult(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferenceManager = PreferenceManager(this)

        setContent {
            App_ProjectTheme {

                AppNavigation(
                    onGoogleLogin = { loginViewModel ->
                        startGoogleSignIn(loginViewModel)
                    },
                    preferenceManager = preferenceManager
                )
            }
        }
    }
    private fun startGoogleSignIn(loginViewModel: LoginViewModel) {
        currentLoginViewModel = loginViewModel
        val signInIntent = loginViewModel.getGoogleSignInIntent()
        googleSignInLauncher.launch(signInIntent)
    }
}
@Composable
fun MainScreen() {
    OnBoardingScreen1()
}

@Preview
@Composable
fun MainPreview(){
    MainScreen()
}