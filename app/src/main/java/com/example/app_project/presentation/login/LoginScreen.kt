package com.example.app_project.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_project.presentation.login.LoginState

// 색상 정의
private val PrimaryColor = Color(0xFF1193D4)
private val BackgroundColor = Color(0xFFF6F7F8)
private val TextColor = Color(0xFF1F2937)
private val SubtleColor = Color(0xFF9CA3AF)
private val GoogleColor = Color(0xFF4285F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginState: LoginState = LoginState.Idle,
    onBackClick: () -> Unit = {},
    onGoogleLogin: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onEmailLoginClick: () -> Unit = {}
) {
    // 로그인 성공 시 화면 전환
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // 헤더
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "로그인",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 48.dp)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = TextColor
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = TextColor
            )
        )

        // 메인 콘텐츠
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 앱 로고 영역
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = PrimaryColor,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOGO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "구글 계정으로 간편하게\n로그인하세요",
                    fontSize = 18.sp,
                    color = TextColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 26.sp
                )
            }

            // 로딩 상태 표시
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(
                    color = PrimaryColor,
                    modifier = Modifier.padding(16.dp)
                )
            }
            // 에러 메시지 표시
            if (loginState is LoginState.Error) {
                Text(
                    text = loginState.message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            // 구글 로그인 버튼
            Button(
                onClick = onGoogleLogin,
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = TextColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 구글 로고 (실제 구글 아이콘으로 교체)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                GoogleColor,
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Google로 로그인",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 또는 구분선
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE5E7EB),
                    thickness = 1.dp
                )
                Text(
                    text = "또는",
                    fontSize = 14.sp,
                    color = SubtleColor
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE5E7EB),
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 이메일로 로그인 버튼
            OutlinedButton(
                onClick = onEmailLoginClick,
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryColor
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    PrimaryColor
                )
            ) {
                Text(
                    text = "이메일로 로그인",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 푸터
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "로그인 시 서비스 이용약관 및 개인정보처리방침에 동의하게 됩니다.",
                fontSize = 12.sp,
                color = SubtleColor,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

