# Firebase 실시간 알림 설정

Sensta는 Firebase 설정이 있는 배포 빌드에서는 FID 기반 실시간 알림을 사용하고, 설정이 없는 개발 빌드에서는 WorkManager 주기 조회로 자동 대체합니다.

## Android 앱

1. Firebase Console에서 출시 앱 `me.sensta`와 실제 기기 테스트 앱 `me.sensta.debug`를 같은 프로젝트에
   각각 등록합니다.
2. 두 패키지의 Android client가 포함된 `google-services.json`을 `app/google-services.json`에 둡니다.
   파일 안의 `package_name`에 두 값이 모두 있는지 확인합니다. 이 파일은 Git에서 제외됩니다.
   Firebase Console에서 각 앱별로 내려받은 파일이 서로 동일하고 두 패키지를 모두 포함한다면 한 파일만
   보관합니다. `rg '"package_name"' app/google-services.json`으로 `me.sensta`와
   `me.sensta.debug`가 모두 나오는지 확인할 수 있습니다.
3. Firebase Cloud Messaging API가 활성화되었는지 확인합니다.
4. `source scripts/android-env.sh && ./gradlew assembleDebug assembleRelease`로 두 variant의 Google Services
   리소스 생성을 확인합니다.

Google 로그인용 Android OAuth client는 패키지와 실제 설치본의 SHA-1 조합마다 필요합니다. 개발 PC의
기본 debug 키가 달라지면 `me.sensta.debug`에 새 SHA-1을 추가하고, Play 배포본 `me.sensta`에는 업로드
키가 아니라 Play Console의 앱 서명 키 SHA-1이 등록되어 있어야 합니다.

앱은 2026년 권장 계약인 Firebase Installation ID(FID)를 `/push/device`의 `token` 필드로 등록합니다. 로그인·토큰 갱신·FID 회전 시 등록하고, 로그아웃 시 서버와 FCM에서 모두 해제합니다.

## GOAPI 서버

Firebase Console의 프로젝트 설정에서 프로젝트 ID를 확인하고, 서비스 계정 JSON을 저장소 및 웹 공개 경로 밖에 보관합니다. GOAPI 환경 파일에 다음 값을 설정합니다.

```dotenv
FIREBASE_PROJECT_ID=my-firebase-project
FIREBASE_CREDENTIALS_FILE=/etc/nubo/firebase-service-account.json
```

기존 서버에서는 GOAPI 실행 파일을 직접 교체하거나 수동으로 `install`하지 않습니다. 새 GOAPI를 포함한
NUBO 통합 릴리스를 게시하고 `nuboctl update` 절차로 적용하면 additive migration이 `push_device` 테이블을
준비하고 서비스를 다시 시작합니다. 자세한 순서는 [운영 서버 배포](SERVER_DEPLOYMENT.md)를 따릅니다.

실제 기기 검증에서는 댓글·사진/댓글 좋아요·1:1 대화를 각각 발생시켜 포그라운드와 백그라운드 수신,
알림 탭 이동, 로그아웃 후 미수신을 확인합니다.

서비스 계정 JSON과 출시 서명 키는 절대로 Git에 커밋하지 않습니다.
