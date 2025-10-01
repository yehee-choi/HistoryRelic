package com.example.app_project.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 색상 정의
private val PrimaryColor = Color(0xFF4A90E2)
private val BackgroundColor = Color(0xFFF7F8FA)
private val CardColor = Color(0xFFFFFFFF)
private val TextPrimaryColor = Color(0xFF212121)
private val TextSecondaryColor = Color(0xFF757575)
private val GreenAccent = Color(0xFF4CAF50)
private val BlueAccent = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    //분석 시작 버튼
    onStartAnalysisClick: () -> Unit = {}

) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "홈",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                ,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor.copy(alpha = 0.8f),
                    titleContentColor = TextPrimaryColor
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
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                TextAnalysisCard(onStartAnalysisClick = onStartAnalysisClick)
            }


            item {
                RecentActivitySection()
            }
        }
    }
}

@Composable
private fun TextAnalysisCard(onStartAnalysisClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 아이콘
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(PrimaryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 제목
            Text(
                text = "텍스트 분석 시작",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 설명
            Text(
                text = "문서를 업로드하거나 텍스트를 붙여넣어 역사적 맥락을 분석하고 유물을 식별하세요.",
                fontSize = 14.sp,
                color = TextSecondaryColor,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 버튼
            Button(
                onClick = onStartAnalysisClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "분석 시작하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RecentActivitySection() {
    Column {
        Text(
            text = "최근 활동",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryColor
        )

    }
}

@Composable
private fun ActivityItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    date: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 텍스트 영역
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    fontSize = 14.sp,
                    color = TextSecondaryColor
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onAnalysisClick: () -> Unit,
    onArtifactsClick: () -> Unit,
    onActivityClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundColor.copy(alpha = 0.9f),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "홈",
                isSelected = true,
                onClick = onHomeClick
            )
            BottomNavItem(
                icon = Icons.Default.Search,
                label = "분석",
                isSelected = false,
                onClick = onAnalysisClick
            )
            BottomNavItem(
                icon = Icons.Default.Inventory2,
                label = "유물",
                isSelected = false,
                onClick = onArtifactsClick
            )
            BottomNavItem(
                icon = Icons.Default.History,
                label = "활동",
                isSelected = false,
                onClick = onActivityClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) PrimaryColor else TextSecondaryColor

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}