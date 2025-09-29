package com.example.app_project.presentation.history

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// AuthRepository에서 정의된 DashboardArtifact를 import
import com.example.app_project.data.model.DashboardArtifact

// 색상 정의
private val BackgroundColor = Color(0xFFF6F7F8)
private val PrimaryColor = Color(0xFF1193D4)
private val ForegroundColor = Color(0xFF111618)
private val SubtleColor = Color(0xFF617C89)
private val BorderColor = Color(0xFFF0F3F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    artifacts: List<DashboardArtifact> = emptyList(), // DashboardArtifact 사용
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onArtifactClick: (DashboardArtifact) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "분석 결과",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor.copy(alpha = 0.8f),
                    titleContentColor = ForegroundColor,
                    navigationIconContentColor = ForegroundColor
                )
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderSection(totalCount = artifacts.size)
            }

            if (isLoading) {
                items(5) { // 로딩 중일 때 스켈레톤 UI 표시
                    LoadingArtifactCard()
                }
            } else if (artifacts.isEmpty()) {
                item {
                    EmptyStateCard()
                }
            } else {
                items(artifacts) { artifact ->
                    DashboardArtifactCard(
                        artifact = artifact,
                        onClick = { onArtifactClick(artifact) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(totalCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "발견된 유물",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ForegroundColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (totalCount > 0) {
                "총 ${totalCount}개의 관련 유물을 발견했습니다."
            } else {
                "관련된 유물을 검색중입니다."
            },
            fontSize = 14.sp,
            color = SubtleColor,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
private fun DashboardArtifactCard(
    artifact: DashboardArtifact,
    onClick: () -> Unit
) {
    // 디버깅용 로그 추가
    Log.d("HistoryScreen", "DashboardArtifactCard - artifact: ${artifact.nameKr}")
    Log.d("HistoryScreen", "이미지 URL: ${artifact.imgUri}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이미지 - 에러 처리 추가
            AsyncImage(
                model = artifact.imgUri,
                contentDescription = artifact.nameKr,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BorderColor), // 배경색 추가
                contentScale = ContentScale.Crop,
                onError = { error ->
                    Log.e("HistoryScreen", "이미지 로드 실패: ${artifact.imgUri}", error.result.throwable)
                },
                onSuccess = {
                    Log.d("HistoryScreen", "이미지 로드 성공: ${artifact.nameKr}")
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 텍스트 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = artifact.nameKr,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ForegroundColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 시기 정보 표시
                if (!artifact.era.isNullOrBlank()) {
                    Text(
                        text = artifact.era,
                        fontSize = 14.sp,
                        color = PrimaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 화살표 아이콘
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SubtleColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
@Composable
private fun LoadingArtifactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이미지 스켈레톤
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BorderColor)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 텍스트 스켈레톤
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .background(BorderColor, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .background(BorderColor, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "관련 유물을 찾을 수 없습니다",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ForegroundColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "다른 키워드로 분석을 시도해보세요.",
                fontSize = 14.sp,
                color = SubtleColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

// 프리뷰용
@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    val mockArtifacts = listOf(
        DashboardArtifact(
            id = "1",
            imgUri = "https://via.placeholder.com/150",
            nameKr = "三穴砲",
            era = "조선시대"
        ),
        DashboardArtifact(
            id = "2",
            imgUri = "https://via.placeholder.com/150",
            nameKr = "불랑기포",
            era = "조선 후기"
        ),
        DashboardArtifact(
            id = "3",
            imgUri = "https://via.placeholder.com/150",
            nameKr = "대완구",
            era = "조선시대"
        )
    )

    HistoryScreen(
        artifacts = mockArtifacts,
        isLoading = false
    )
}