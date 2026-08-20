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

## 검증

- `./gradlew test lintDebug assembleDebug assembleRelease` 성공.

## 다음 작업

- GOAPI 인증·피드·게시글·댓글·알림·대화 요청/응답 모델을 Android 계약 테스트로 고정한다.
- 모바일 refresh token 계약을 설계하고 GOAPI와 Sensta에 함께 반영한다.
