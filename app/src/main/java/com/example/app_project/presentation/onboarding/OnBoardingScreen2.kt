package com.example.app_project.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_project.presentation.onboarding.OnboardingColors.PrimaryAlpha20

// 색상 정의
object OnboardingColors {
    val Primary = Color(0xFF1193D4)
    val BackgroundLight = Color(0xFFF6F7F8)
    val BackgroundDark = Color(0xFF101C22)
    val PrimaryAlpha10 = Color(0x1A1193D4)
    val PrimaryAlpha20 = Color(0x331193D4)
    val PrimaryAlpha30 = Color(0x4D1193D4)
    val PrimaryAlpha40 = Color(0x661193D4)
    val PrimaryAlpha90 = Color(0xE61193D4)
    val TextSecondary = Color(0xB3000000) // 70% 불투명도
    val TextSecondaryDark = Color(0xB3FFFFFF) // 70% 불투명도
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen2(
    onSkipClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    val backgroundColor = OnboardingColors.BackgroundLight
    val textColor = OnboardingColors.BackgroundDark
    val textSecondaryColor = OnboardingColors.TextSecondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 메인 컨텐츠
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                // 아이콘 원형 배경
                Box(
                    modifier = Modifier
                        .size(256.dp)
                        .clip(CircleShape)
                        .background(OnboardingColors.PrimaryAlpha10
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(192.dp)
                            .clip(CircleShape)
                            .background(
                                PrimaryAlpha20
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DocumentScanner,
                            contentDescription = "문서 스캐너",
                            tint = OnboardingColors.Primary,
                            modifier = Modifier.size(128.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // 제목
                Text(
                    text = "앱 작동 방식",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 설명
                Text(
                    text = "OCR로 지문에서 텍스트를 추출하고, 키워드 추출 및 시대/국가 추론을 통해 유물을 찾습니다.",
                    fontSize = 16.sp,
                    color = textSecondaryColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 280.dp),
                    lineHeight = 22.sp
                )
            }

            // 하단 영역
            Column {
                // 페이지 인디케이터
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 비활성 인디케이터
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                 OnboardingColors.PrimaryAlpha30
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // 활성 인디케이터
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(OnboardingColors.Primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // 비활성 인디케이터 2
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                OnboardingColors.PrimaryAlpha30
                            )
                    )
                }

                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    // 건너뛰기 버튼
                    Button(
                        onClick = onSkipClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryAlpha20
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "건너뛰기",
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }

                    // 다음 버튼
                    Button(
                        onClick = onNextClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OnboardingColors.Primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "다음",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnBoardingScreen2()
}
