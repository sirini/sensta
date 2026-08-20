# SENSTA Android

SENSTA는 [sensta.me](https://sensta.me)의 사진을 감상하고, 여러 장의 사진과 EXIF를 공유하며,
좋아요·댓글·1:1 대화·활동 알림을 이용하는 Android 네이티브 앱입니다. Android 8(API 26) 이상을
지원하며 Android 16(API 36)을 대상으로 빌드합니다.

2.0은 NUBO의 디자인 언어와 GOAPI의 API v1 계약을 기준으로 다시 만든 버전입니다. SENSTA 자체 앱인
동시에, NUBO 기반 커뮤니티 운영자가 자신의 게시판·브랜드·서버 주소를 바꾸어 전용 앱을 제작할 수 있는
참고 구현을 지향합니다.

## 주요 기능

- 로그인 없이 사진 피드·탐색·게시글·댓글 감상
- Google 또는 이메일 계정 가입과 로그인
- 최대 9장, 100MB의 사진 업로드와 EXIF 표시
- 게시글·댓글 좋아요와 댓글 작성
- 회원 간 1:1 대화, 댓글·좋아요·대화 푸시 알림
- 게시글·사용자 신고, 차단과 차단 해제
- 앱 안의 계정 및 관련 데이터 삭제
- 시스템 설정을 따르는 밝은/어두운 테마

## 개발 환경

WSL2 Ubuntu에서는 프로젝트가 제공하는 스크립트로 사용자 홈에 JDK 17과 Android SDK를 준비할 수 있습니다.

```bash
./scripts/bootstrap-wsl.sh
source ./scripts/android-env.sh
./scripts/check.sh
```

`check.sh`는 단위 테스트, Lint, 디버그·릴리스 APK와 릴리스 App Bundle을 모두 검증합니다. Firebase를
연결하지 않은 로컬 빌드는 주기적 알림 조회 방식으로 동작합니다. 실제 푸시 배포 방법은
[Firebase 설정](docs/FIREBASE_SETUP.md), Play 제출 방법은 [릴리스 안내](docs/PLAY_RELEASE.md)를 참고하세요.

## 다른 NUBO 커뮤니티에 적용하기

포크한 뒤 최소한 다음 값을 자신의 서비스에 맞게 바꿉니다.

- `data/src/main/java/me/data/env/Env.kt`: 도메인, 게시판 UID·ID, 기본 카테고리와 업로드 한도
- `app/src/main/res`: 앱 이름, 아이콘, 로고와 색상 자원
- `app/build.gradle.kts`: 고유한 `applicationId`, 버전과 서명 설정
- `app/google-services.json`: 해당 Android 패키지를 등록한 Firebase 프로젝트 설정

서버는 최신 [NUBO](https://github.com/sirini/nubo)와 [GOAPI](https://github.com/sirini/goapi)의 API v1
계약을 제공해야 합니다. 인증, 신고·차단·탈퇴, FCM 토큰과 채팅 계약은 임의로 생략하면 앱 기능 일부가
동작하지 않습니다.

## 프로젝트

- 웹사이트: https://sensta.me
- Android 소스: https://github.com/sirini/sensta
- NUBO: https://github.com/sirini/nubo
- GOAPI: https://github.com/sirini/goapi

SENSTA는 비상업적으로 운영하며 사진을 좋아하는 모든 분에게 열려 있습니다.
