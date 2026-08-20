# Firebase 실시간 알림 설정

Sensta는 Firebase 설정이 있는 배포 빌드에서는 FID 기반 실시간 알림을 사용하고, 설정이 없는 개발 빌드에서는 WorkManager 주기 조회로 자동 대체합니다.

## Android 앱

1. Firebase Console에서 Android 앱을 추가하고 패키지 이름을 `me.sensta`로 지정합니다.
2. 내려받은 `google-services.json`을 `app/google-services.json`에 둡니다. 이 파일은 Git에서 제외됩니다.
3. Firebase Cloud Messaging API가 활성화되었는지 확인합니다.
4. `source scripts/android-env.sh && ./gradlew assembleDebug`로 Google Services 리소스 생성을 확인합니다.

앱은 2026년 권장 계약인 Firebase Installation ID(FID)를 `/push/device`의 `token` 필드로 등록합니다. 로그인·토큰 갱신·FID 회전 시 등록하고, 로그아웃 시 서버와 FCM에서 모두 해제합니다.

## GOAPI 서버

Firebase Console의 프로젝트 설정에서 프로젝트 ID를 확인하고, 서비스 계정 JSON을 저장소 및 웹 공개 경로 밖에 보관합니다. GOAPI 환경 파일에 다음 값을 설정합니다.

```dotenv
FIREBASE_PROJECT_ID=my-firebase-project
FIREBASE_CREDENTIALS_FILE=/etc/nubo/firebase-service-account.json
```

기존 서버는 새 GOAPI 실행 파일로 `install` 명령을 한 번 실행해 `push_device` 테이블을 준비한 뒤 서비스를 재시작합니다. 실제 기기 검증에서는 댓글·사진/댓글 좋아요·1:1 대화를 각각 발생시켜 포그라운드와 백그라운드 수신, 알림 탭 이동, 로그아웃 후 미수신을 확인합니다.

서비스 계정 JSON과 출시 서명 키는 절대로 Git에 커밋하지 않습니다.
