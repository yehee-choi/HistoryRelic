package com.example.app_project.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.app_project.data.model.ArtifactDetail

// 색상 정의
private val BackgroundColor = Color(0xFFF6F7F8)
private val PrimaryColor = Color(0xFF1193D4)
private val ForegroundColor = Color(0xFF111618)
private val SubtleColor = Color(0xFF617C89)
private val BorderColor = Color(0xFFF0F3F4)
private val CardColor = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtifactDetailScreen(
    artifactDetail: ArtifactDetail?,
    isLoading: Boolean = false,
    onBackClick: () -> Unit = {},
    onRelatedArtifactClick: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = artifactDetail?.nameKr ?: "상세 정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
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
        when {
            isLoading -> LoadingContent(paddingValues)
            artifactDetail == null -> EmptyContent(paddingValues)
            else -> DetailContent(
                artifactDetail = artifactDetail,
                paddingValues = paddingValues,
                onRelatedArtifactClick = onRelatedArtifactClick
            )
        }
    }
}

@Composable
private fun DetailContent(
    artifactDetail: ArtifactDetail,
    paddingValues: PaddingValues,
    onRelatedArtifactClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 이미지 섹션
        item {
            ImageSection(artifactDetail.imageList)
        }

        // 기본 정보 테이블
        item {
            InfoTableSection(artifactDetail)
        }

        // 설명 섹션
        item {
            DescriptionSection(artifactDetail.description)
        }

        // 하단 여백
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ImageSection(imageList: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (imageList.size > 1) {
            val pagerState = rememberPagerState(pageCount = { imageList.size })

            Box {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.height(300.dp)
                ) { page ->
                    AsyncImage(
                        model = imageList[page],
                        contentDescription = "유물 이미지 ${page + 1}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                if (imageList.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(imageList.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == pagerState.currentPage) {
                                            Color.White
                                        } else {
                                            Color.White.copy(alpha = 0.5f)
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        } else {
            AsyncImage(
                model = imageList.firstOrNull() ?: "",
                contentDescription = "유물 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun InfoTableSection(artifactDetail: ArtifactDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "기본 정보",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ForegroundColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InfoRow("유물명", artifactDetail.nameKr)
            InfoRow("재료", artifactDetail.materialName)
            InfoRow("국적/시대", artifactDetail.nationalityName)
            InfoRow("용도", artifactDetail.purposeName)
            InfoRow("소장기관", artifactDetail.museumName)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = SubtleColor,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = ForegroundColor,
            modifier = Modifier.weight(1f)
        )
    }
    HorizontalDivider(
        color = BorderColor,
        thickness = 0.5.dp
    )
}

@Composable
private fun DescriptionSection(description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "상세 설명",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ForegroundColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = ForegroundColor,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun LoadingContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = PrimaryColor)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "유물 정보를 불러오는 중...",
                fontSize = 14.sp,
                color = SubtleColor
            )
        }
    }
}

@Composable
private fun EmptyContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "해당 유물에 대한",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = ForegroundColor
            )
            Text(
                text = "상세정보가 존재하지 않습니다",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = ForegroundColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "잠시 후 다시 시도해주세요",
                fontSize = 14.sp,
                color = SubtleColor
            )
        }
    }
}