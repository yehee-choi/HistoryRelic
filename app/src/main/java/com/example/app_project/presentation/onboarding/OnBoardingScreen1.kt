package com.example.app_project.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.app_project.R


// 색상 정의
val PrimaryColor = Color(0xFF1193D4)
val BackgroundLight = Color(0xFFF6F7F8)
val BackgroundDark = Color(0xFF101C22)

// Work Sans 폰트 패밀리 (res/font/ 폴더에 work_sans_regular.ttf, work_sans_medium.ttf, work_sans_bold.ttf 파일이 필요함)
val WorkSansFamily = FontFamily(
    Font(R.font.worksans_regular, FontWeight.Normal),
    Font(R.font.worksans_medium, FontWeight.Medium),
    Font(R.font.worksans_bold, FontWeight.Bold)
)

@Composable
fun OnBoardingScreen1(
    onNextClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight) // 다크모드 지원 시 isSystemInDarkTheme()로 변경 가능
    ) {
        // 상단 이미지
        AsyncImage(
            model = R.drawable.onboarding1,
            contentDescription = "History exploration background",
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentScale = ContentScale.Crop
        )

        // 중앙 컨텐츠
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "역사 지문 분석으로 \n 새로운 유물을 찾으세요!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = WorkSansFamily,
                textAlign = TextAlign.Center,
                color = Color.Black,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "역사 지문을 읽고 유물을 찾는 앱의 주요 기능을 소개합니다.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = WorkSansFamily,
                textAlign = TextAlign.Center,
                color = Color.Black.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
        }

        // 하단 버튼
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Next",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = WorkSansFamily
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun OnBoarding_1Preview() {
//    OnBoarding_1()
//}
