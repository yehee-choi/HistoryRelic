package com.example.app_project.data.model

// ===== 인증 및 API 요청/응답 모델들 =====

// 구글 로그인 토큰 요청
data class TokenRequest(
    val id_token: String
)

// 서버에서 JWT 토큰 응답
data class JwtTokenResponse(
    val access_token: String
)

// JWT 토큰 테스트 응답
data class JwtTestResponse(
    val msg: String
)

// 검색 요청
data class SearchRequest(
    val metadata: String?,
    val data: String
)

// 사용자 정보
data class User(
    val id: String,
    val email: String,
    val name: String,
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)