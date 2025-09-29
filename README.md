###History Relic


"""
# Project Structure

app/src/main/java/com/example/app_project/
├── MainActivity.kt
├── data/
│   ├── repository/
│   │   ├── AuthRepository.kt          # 로그인/로그아웃만
│   │   └── ArtifactRepository.kt      # OCR + 유물 검색 모두 처리
│   └── model/
│       ├── UiModels.kt                    # Ui
│       ├── AuthModels.kt                  # Login(OAuTH)
│       └── ApiModels.kt                   # Api
├── ui/theme/                          # 공통 부분
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
├── presentation/
│   ├── navigation/
│   │   ├── AppNavigation.kt           #  로그인 체크 추가
│   │   └── Screen.kt                  #  LoginScreen 추가
│   ├── login/                         #  로그인만
│   │   ├── LoginViewModel.kt
│   │   └── LoginScreen.kt
│   ├── onboarding/                    # 기존과 동일
│   │   ├── OnboardingViewModel.kt     # 현재는 생성 X, 추후 온보딩 화면간 데이터 전달이 필요할 경우 사용
│   │   ├── OnboardingScreen1.kt
│   │   ├── OnboardingScreen2.kt
│   │   └── OnboardingScreen3.kt
│   ├── home/                          # 홈화면
│   │   ├── HomeViewModel.kt           # 현재는 없음.
│   │   └── HomeScreen.kt
│   ├── camera/                        # 카메라 촬영(역사 지문 촬영 용도)
│   │   ├── CameraViewModel.kt
│   │   ├── CameraScreen.kt            # 역사 지문 촬영
│   │   └── ResultScreen.kt            # 촬영된 사진 + OCR결과(텍스트 형식)
│   └── history/                       # 관련 유물 및 상세정보 페이지
│       ├── HistoryViewModel.kt
│       ├── DetailScreen.kt.           # 관련 유물에 대한 상세 정보 페이지
│       └── HistoryScreen.kt.          # 관련 유물 리스트(대시보드 형태)
└── utils/
    ├── CameraUtils.kt
    ├── Constants.kt
    └── PreferenceManager.kt           # 🆕 로그인 상태 저장용

"""
