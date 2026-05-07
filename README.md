## KOIN_OWNER_MOBILE

Compose Multiplatform 기반 KOIN 사장님 앱. Android · iOS · Desktop(macOS / Windows / Linux) 단일 코드베이스.

`../KOIN_ANDROID/business`(Android-only) 모듈을 KMP로 포팅한 프로젝트이며, 학생용 `KOIN_ANDROID`와 백엔드 API를 공유한다.

### 플랫폼 상태

| 플랫폼  | 산출물          | 상태 | 비고                                 |
|---------|-----------------|------|--------------------------------------|
| Android | APK / AAB       | 정상 | minSdk 28, targetSdk 36              |
| iOS     | Xcode framework | 정상 | `iosApp/iosApp.xcodeproj` 직접 빌드  |
| Desktop | DMG / MSI / DEB | 정상 | `./gradlew :composeApp:packageDmg` 등 |

### 요구 사항

- JDK 17 (Zulu 권장)
- Android SDK (`compileSdk=36`)
- Xcode 16+ (iOS 빌드 시, macOS 호스트)
- Gradle wrapper 사용 (별도 설치 불필요)

### 처음 빌드

1. 저장소 클론 후 루트에 `local.properties` 생성:
   ```properties
   sdk.dir=/path/to/Android/Sdk
   ```
2. Android 디버그 APK
   ```
   ./gradlew :composeApp:assembleDebug
   ./gradlew :composeApp:installDebug
   ```
3. Desktop 실행
   ```
   ./gradlew :composeApp:run
   ```
   현재 OS에 맞는 패키지를 만들려면
   ```
   ./gradlew :composeApp:packageDistributionForCurrentOS
   ```
4. iOS — `iosApp/iosApp.xcodeproj`을 Xcode에서 열고 KMP 모듈 동기화 후 실행.

### 환경 분기

- `local.properties`에는 SDK 경로만 두면 충분하다. API 베이스 URL은 빌드 타입(Debug = `api.stage.koreatech.in`, Release = `api.koreatech.in`)에 따라 자동 선택된다 (`data/src/commonMain/kotlin/in/koreatech/business/data/Constants.kt`).

### 모듈 개요

- `composeApp` — 플랫폼 진입점 (Android Activity / Desktop main / iOS framework)
- `umbrella` — 루트 NavHost, App 컴포저블, DI 부트스트랩
- `core/{common,designsystem,ui}` — 공통 유틸 / 디자인 토큰 / 재사용 UI
- `domain` — 유즈케이스, 모델, 리포지토리 인터페이스
- `data` — Ktor API, 리포지토리 구현, 암호화 DataStore
- `feature/{auth,insertstore,settings,store}` — 화면별 feature 모듈

### 테스트

```
./gradlew test
```

### 명령어 빠른 참조

| 작업                              | 명령                                                |
|-----------------------------------|-----------------------------------------------------|
| 코드 스타일 검사                  | `./gradlew ktlintCheck spotlessCheck detekt`        |
| Android 디버그 빌드               | `./gradlew :composeApp:assembleDebug`               |
| Android 릴리즈(축소 적용) 빌드    | `./gradlew :composeApp:assembleRelease`             |
| Desktop 실행                      | `./gradlew :composeApp:run`                         |
| Desktop 패키지(현재 OS)           | `./gradlew :composeApp:packageDistributionForCurrentOS` |
| iOS 프레임워크 링크 (smoke)       | `./gradlew :composeApp:linkDebugFrameworkIosArm64`  |

### 커밋 컨벤션

- 형식: `<add|feat|fix|refactor|chore|build|test>: <subject>`
- scope 괄호 / 새로운 type 추가 금지
- 비자명한 변경에는 본문을 붙인다.
