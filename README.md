# History Relic

역사 지문을 촬영하여 관련 유물을 검색하고 상세 정보를 제공하는 Android 애플리케이션

## 주요 기능

- **역사 지문 촬영 및 OCR**: 카메라로 역사 지문을 촬영하고 텍스트를 자동 추출
- **유물 검색**: 추출된 텍스트를 기반으로 관련 유물 검색
- **상세 정보 제공**: 검색된 유물의 상세 정보 및 이미지 제공
- **Google OAuth 로그인**: 간편한 소셜 로그인 지원

## 기술 스택

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil
- **HTTP Client**: Retrofit2 + OkHttp3
- **Authentication**: Google Sign-In
- **OCR**: ML Kit Text Recognition (Korean)
- **Camera**: CameraX

## 프로젝트 구조

```
app/src/main/java/com/example/app_project/
├── MainActivity.kt
├── data/
│   ├── repository/
│   │   ├── AuthRepository.kt          # 인증 관리 (로그인/로그아웃)
│   │   └── ArtifactRepository.kt      # OCR + 유물 검색 처리
│   └── model/
│       ├── UiModels.kt                # UI 데이터 모델
│       ├── AuthModels.kt              # 인증 관련 모델
│       └── ApiModels.kt               # API 응답 모델
├── ui/theme/
│   ├── Color.kt                       # 색상 정의
│   ├── Theme.kt                       # 테마 설정
│   └── Type.kt                        # 타이포그래피
├── presentation/
│   ├── navigation/
│   │   ├── AppNavigation.kt           # 네비게이션 그래프
│   │   └── Screen.kt                  # 화면 경로 정의
│   ├── login/
│   │   ├── LoginViewModel.kt          # 로그인 로직
│   │   └── LoginScreen.kt             # 로그인 UI
│   ├── onboarding/
│   │   ├── OnboardingScreen1.kt       # 온보딩 1
│   │   ├── OnboardingScreen2.kt       # 온보딩 2
│   │   └── OnboardingScreen3.kt       # 온보딩 3
│   ├── home/
│   │   └── HomeScreen.kt              # 홈 화면
│   ├── camera/
│   │   ├── CameraViewModel.kt         # 카메라 로직
│   │   ├── CameraScreen.kt            # 카메라 촬영 화면
│   │   └── ResultScreen.kt            # OCR 결과 화면
│   └── history/
│       ├── DetailViewModel.kt         # 상세 정보 로직
│       ├── DetailScreen.kt            # 유물 상세 정보
│       └── HistoryScreen.kt           # 유물 검색 결과 목록
└── utils/
    ├── CameraUtils.kt                 # 카메라 유틸리티
    ├── Constants.kt                   # 상수 정의
    └── PreferenceManager.kt           # 로컬 데이터 저장

```

## 화면 플로우

```
앱 시작 → 온보딩(1-3) → 로그인 → 홈 → 카메라 → 결과 → 유물 목록 → 상세 정보
```

### 주요 화면 설명

1. **온보딩 (OnBoarding 1-3)**: 앱 소개 및 사용 방법 안내
2. **로그인 (Login)**: Google OAuth 인증
3. **홈 (Home)**: 메인 화면, 분석 시작 버튼
4. **카메라 (Camera)**: 역사 지문 촬영
5. **결과 (Result)**: OCR 텍스트 추출 결과 및 검색
6. **유물 목록 (History)**: 검색된 관련 유물 리스트
7. **상세 정보 (Detail)**: 선택한 유물의 상세 정보

## 설치 및 실행

### 필수 요구사항

- Android Studio Hedgehog | 2023.1.1 이상
- Minimum SDK: 30
- Target SDK: 36
- Kotlin 1.9+

### 설정

1. 저장소 클론
```bash
git clone https://github.com/yehee-choi/HistoryRelic.git
```

2. `local.properties` 파일에 다음 항목 추가:
```properties
AUTH_SERVER_URL=your_server_url
CLIENT_ID=your_google_oauth_client_id
```

3. Android Studio에서 프로젝트 열기

4. Sync Project with Gradle Files

5. 실행 (Shift + F10)

## API 문서

### 인증
- `POST /google-login`: Google ID 토큰 검증 및 JWT 발급

### 유물 검색
- `POST /searchByText`: 텍스트 기반 유물 검색
- `GET /detailInfo`: 유물 상세 정보 조회 (Query Parameter: `id`)

## 주요 의존성

```kotlin
// Compose
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.6")

// Network
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Image Loading
implementation("io.coil-kt:coil-compose:2.4.0")

// Google Auth
implementation("com.google.android.gms:play-services-auth:20.7.0")

// Camera
implementation("androidx.camera:camera-camera2:1.3.0")
implementation("androidx.camera:camera-lifecycle:1.3.0")
implementation("androidx.camera:camera-view:1.3.0")

// ML Kit OCR
implementation("com.google.mlkit:text-recognition-korean:16.0.0")
```

## 기여

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 라이선스

This project is licensed under the MIT License

## 개발자

- **Yehee Choi** - [yehee-choi](https://github.com/yehee-choi)

## 문의

프로젝트에 대한 문의사항은 [Issues](https://github.com/yehee-choi/HistoryRelic/issues)를 통해 남겨주세요.
