package com.example.app_project.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.app_project.ui.theme.*
import com.example.app_project.R




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen3(
    onStartClick: () -> Unit = {}
) {

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val minHeight = maxOf(884.dp, screenHeight)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .heightIn(min = minHeight)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                // 배경 이미지
                AsyncImage(
                    model = R.drawable.onboarding3,
                    contentDescription = "역사적 유물과 텍스트 콜라주",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 그라데이션 오버레이
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    BackgroundColor.copy(alpha = 0.8f),
                                    BackgroundColor
                                )
                            )
                        )
                )
            }

            // 중간 콘텐츠 섹션
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "역사 속으로 떠날 준비가 되셨나요?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "이미지를 업로드하여 역사 지문을 분석하고 숨겨진 유물을 찾아보세요.",
                    fontSize = 18.sp,
                    color = SubtitleColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }

            // 하단 버튼 섹션 : 메인 화면으로 이동
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                // 시작하기 버튼
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "시작하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 884)
@Composable
fun OnBoardingScreen3Preview() {
    OnBoardingScreen3()
}