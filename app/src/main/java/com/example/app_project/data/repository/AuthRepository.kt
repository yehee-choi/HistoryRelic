package com.example.app_project.data.repository

import okhttp3.logging.HttpLoggingInterceptor
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import com.example.app_project.data.model.*
import com.example.app_project.BuildConfig
import retrofit2.http.Query

class AuthRepository(private val context: Context) {
    companion object {
        private const val TAG = "AuthRepository"
        private val SERVER_URL = BuildConfig.AUTH_SERVER_URL
        private val CLIENT_ID = BuildConfig.CLIENT_ID
//        private  val SERVER_URL = "http://43.200.37.252:5000"
//        private val CLIENT_ID = "884594432923-ss4mv9cgo0b6j3u7e9rqhm4268n3vben.apps.googleusercontent.com"
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val backendApi = createBackendApi()




    fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun handleSignInResult(data: Intent?): Result<String> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.await()
            val idToken = account?.idToken

            if (idToken != null) {
                Result.success(idToken)
            } else {
                Result.failure(Exception("ID 토큰을 받지 못했습니다"))
            }
        } catch (e: ApiException) {
            Result.failure(Exception("구글 로그인 실패: ${e.statusCode}"))
        }
    }

    suspend fun sendTokenToBackend(idToken: String): Result<String> {
        return try {
            val request = TokenRequest(idToken)
            val response = backendApi.verifyGoogleToken(request)

            Result.success(response.access_token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createBackendApi(): BackendApi {

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("HTTP_LOG", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY // 요청/응답 본문까지 모두 로그
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(200, TimeUnit.SECONDS)
            .writeTimeout(200, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(BackendApi::class.java)
    }

    // 검색 API 호출 - 상세 로깅 포함
    suspend fun searchArtifacts(token: String, query: String): Result<SearchApiResponse> {
        return try {
            Log.d(TAG, "검색 API 호출 시작: query='$query'")

            val request = SearchRequest(null, query)
            val response = backendApi.search("Bearer $token", request)

            // 응답 상세 로깅
            Log.d(TAG, "=== API 응답 상세 정보 ===")
            Log.d(TAG, "응답 객체: $response")
            Log.d(TAG, "응답이 null인가?: ${response == null}")

            if (response != null) {
                Log.d(TAG, "응답 객체 타입: ${response::class.java.simpleName}")
                Log.d(TAG, "success: ${response.success}")
                Log.d(TAG, "message: ${response.message}")
                Log.d(TAG, "userId: ${response.userId}")
                Log.d(TAG, "data: ${response.data}")
                Log.d(TAG, "data.totalCount: ${response.data.totalCount}")
                Log.d(TAG, "data.brief_info_list 크기: ${response.data.brief_info_list.size}")

                // brief_info_list 상세 정보
                response.data.brief_info_list.forEachIndexed { index, briefInfo ->
                    Log.d(TAG, "=== brief_info_list[$index] ===")
                    Log.d(TAG, "id: ${briefInfo.id}")
                    Log.d(TAG, "nameKr: ${briefInfo.nameKr}")
                    Log.d(TAG, "museumName1: ${briefInfo.museumName1}")
                    Log.d(TAG, "museumName2: ${briefInfo.museumName2}")
                    Log.d(TAG, "museumName3: ${briefInfo.museumName3}")
                    Log.d(TAG, "materialName: ${briefInfo.materialName}")
                    Log.d(TAG, "nationalityName: ${briefInfo.nationalityName}")
                    Log.d(TAG, "purposeName: ${briefInfo.purposeName}")
                    Log.d(TAG, "imgUri: ${briefInfo.imgUri}")
                    Log.d(TAG, "imgThumUriM: ${briefInfo.imgThumUriM}")
                    Log.d(TAG, "전체 briefInfo: $briefInfo")
                }

                // toDashboardArtifactList() 변환 테스트
                val dashboardList = response.toDashboardArtifactList()
                Log.d(TAG, "변환된 DashboardArtifact 리스트:")
                Log.d(TAG, "변환된 리스트 크기: ${dashboardList.size}")
                dashboardList.forEachIndexed { index, artifact ->
                    Log.d(TAG, "DashboardArtifact[$index]: $artifact")
                }
            }

            Log.d(TAG, "검색 API 성공")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "=== 검색 API 호출 중 예외 발생 ===")
            Log.e(TAG, "예외 타입: ${e.javaClass.simpleName}")
            Log.e(TAG, "예외 메시지: ${e.message}")
            Log.e(TAG, "스택 트레이스:", e)
            Result.failure(e)
        }
    }


    suspend fun testJwtToken(token: String): Result<String> {
        return try {
            val response = backendApi.testJwtToken("Bearer $token")
            Log.d(TAG, "JWT 토큰 테스트 결과: ${response.msg}")
            Result.success(response.msg)
        } catch (e: Exception) {
            Log.e(TAG, "JWT 토큰 테스트 실패", e)
            Result.failure(e)
        }
    }

    suspend fun getArtifactDetail(token: String, artifactId: String): Result<DetailApiResponse> {
        return try {
            Log.d(TAG, "상세 정보 API 호출 시작: id='$artifactId'")

            val response = backendApi.detail("Bearer $token", artifactId)

            // 성공 응답 로그
            Log.d(TAG, "=== API 응답 성공 ===")
            Log.d(TAG, "Response success: ${response.success}")
            Log.d(TAG, "Response message: ${response.message}")
            Log.d(TAG, "Response data size: ${response.data?.detail_info_list?.size}")

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "=== 상세 정보 API 호출 중 예외 발생 ===", e)
            Log.e(TAG, "Error type: ${e.javaClass.simpleName}")
            Log.e(TAG, "Error message: ${e.message}")

            // HttpException인 경우 응답 본문도 확인
            if (e is retrofit2.HttpException) {
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(TAG, "Error response body: $errorBody")
                } catch (bodyException: Exception) {
                    Log.e(TAG, "Failed to read error body", bodyException)
                }
            }

            Result.failure(e)
        }
    }

    // 상세 정보 API 호출
//    suspend fun getArtifactDetail(token: String, artifactId: String): Result<DetailApiResponse> {
//        return try {
//            Log.d(TAG, "상세 정보 API 호출 시작: id='$artifactId'")
//            val response = backendApi.detail("Bearer $token", artifactId)
//            Log.d(TAG, "상세 정보 API 응답: $response")
//            Result.success(response)
//        } catch (e: Exception) {
//            Log.e(TAG, "=== 상세 정보 API 호출 중 예외 발생 ===", e)
//            Result.failure(e)
//        }
//    }

}

interface BackendApi {
    @POST("/google-login")
    suspend fun verifyGoogleToken(@Body request: TokenRequest): JwtTokenResponse

    @GET("/test/jwtTokenTest")
    suspend fun testJwtToken(@Header("Authorization") authorization: String): JwtTestResponse

    @POST("/searchByText")
    suspend fun search(
        @Header("Authorization") authorization: String,
        @Body request: SearchRequest
    ): SearchApiResponse


//    @GET("/detailInfo")
//    suspend fun detail(
//        @Header("Authorization") authorization: String,
//        @Header("id") id: String
//    ): DetailApiResponse
    @GET("/detailInfo")
    suspend fun detail(
        @Header("Authorization") authorization: String,
        @Query("id") id: String
    ): DetailApiResponse
}

