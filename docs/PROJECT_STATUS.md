# Sensta 2.0 프로젝트 상태

## 현재 목표

- API 36 빌드 기반을 안정화하고 최신 Nubo API v1 계약으로 앱 기능을 이관한다.

## 결정

- 2.0 개발은 `feat/sensta-2.0` 브랜치에서 기능 단위로 검증·커밋·푸시한다.
- 빌드 기반은 JDK 17, Gradle 8.13, AGP 8.13.2, Kotlin 2.3.21, compile/target SDK 36으로 고정한다.
- 최소 지원 버전은 Android 8(API 26)로 낮춘다.
- Kotlin annotation processing은 KAPT 대신 KSP를 사용한다.
- 최신 Nubo와 GOAPI의 API contract v1을 Android 네트워크 계층의 기준으로 삼는다.
- 새로 작성하거나 의미를 바로잡는 코드 주석은 한국어로 작성한다.
- 모든 기능 변경과 출시 검증 후 기능 동일성을 유지하는 별도 최종 리팩터링을 수행한다.

## 완료

- WSL 사용자 홈에 Temurin JDK 17과 Android SDK 36, Build Tools 36.1, Platform Tools를 설치했다.
- API 36 및 Android 8 이상을 대상으로 세 모듈을 현대화했다.
- Hilt·Room annotation processing을 KSP로 이관했다.
- Android 13 미만에서 알림 권한을 잘못 요청하던 경로와 프로필 사진의 불필요한 저장소 권한 요청을 제거했다.
- GOAPI에 네이티브 앱용 `POST /auth/android/refresh` 토큰 회전 계약을 추가하고 서버 테스트를 통과시켰다(`goapi` `dc27f53`).
- Nubo API 계약 문서에 Android 로그인·토큰 갱신 기준을 반영했다(`nubo` `20775ec`).
- 로그인·회원가입·이메일 인증·중복 검사·토큰 갱신을 최신 API 계약으로 이관했다.
- 비밀번호를 클라이언트에서 SHA-256 처리하던 오래된 동작을 제거하고 TLS를 통해 원문을 전달해 서버의 bcrypt 검증과 일치시켰다.
- 액세스 토큰을 갱신할 때 회전된 리프레시 토큰도 함께 저장하도록 화면과 백그라운드 작업을 수정했다.
- 인증 응답 계약 회귀 테스트 5개를 추가했다.

## 검증

- `./scripts/check.sh`(`test`, `lintDebug`, `assembleDebug`, `assembleRelease`) 성공.
- `:data:testDebugUnitTest` 인증 계약 테스트 성공.

## 다음 작업

- GOAPI 피드·게시글·댓글·알림·대화 요청/응답 모델을 Android 계약 테스트로 고정한다.
- 홈 피드와 게시글 상세 화면을 최신 `/home/latest/:id`, `/board/*` 계약으로 이관한다.
