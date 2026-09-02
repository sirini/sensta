# Android 실제 기기 테스트

debug 앱은 `me.sensta.debug`로 빌드되므로 Play 앱 `me.sensta`를 지우지 않고 Galaxy S25 Edge에 함께
설치할 수 있습니다. debug 앱도 기본적으로 실제 `https://sensta.me`를 사용하므로 테스트 사진과 계정은
운영 데이터로 취급합니다.

## 1. 안전한 테스트 준비

- 개인 사진 대신 공개해도 되는 테스트 이미지를 사용합니다.
- 별도 테스트 계정을 만들고 게시글 제목 앞에 `[S25 TEST]`를 붙입니다.
- 테스트 종료 후 게시글과 테스트 계정을 삭제합니다.
- 전체 기능 판정은 [운영 서버 배포](SERVER_DEPLOYMENT.md)가 끝난 뒤 진행합니다.
- 사진 업로드만 먼저 점검할 수 있지만, 기존 서버에서 인증·푸시·안전 기능 결과는 최종 결과로 보지 않습니다.

FCM 없이 이미지 업로드만 시험할 때는 `google-services.json`이 없어도 됩니다. FCM까지 시험하려면
[Firebase 설정](FIREBASE_SETUP.md)에 따라 `me.sensta.debug`를 등록합니다.

## 2. Galaxy 개발자 옵션 연결

Galaxy에서 **설정 > 휴대전화 정보 > 소프트웨어 정보 > 빌드번호**를 7번 누르고 잠금 인증을 합니다.
설정 첫 화면의 **개발자 옵션**에서 **USB 디버깅**을 켭니다.
Android 공식 안내는 [실제 기기 연결](https://developer.android.com/studio/run/device)과
[ADB 명령](https://developer.android.com/tools/adb)에서 확인할 수 있습니다.

USB 연결 후 휴대전화에 표시되는 PC RSA 지문을 확인해 허용하고 다음 명령으로 연결을 확인합니다.

```bash
source ./scripts/android-env.sh
adb devices
```

아래 설치 명령의 `adb -d`는 USB 기기를 뜻합니다. 무선 연결에서는 `adb -d` 대신
`adb -s PHONE_IP:CONNECT_PORT`를 사용합니다.

WSL2에서 USB 기기가 직접 보이지 않으면 Galaxy와 PC를 같은 Wi-Fi에 연결하고 개발자 옵션의 **무선
디버깅 > 페어링 코드로 기기 페어링**을 사용합니다. 화면에 표시되는 pair port와 connect port는 서로
다를 수 있습니다.

```bash
adb pair PHONE_IP:PAIR_PORT
adb connect PHONE_IP:CONNECT_PORT
adb devices
```

## 3. debug APK 설치

```bash
source ./scripts/android-env.sh
./gradlew :app:assembleDebug
adb -d install -r app/build/outputs/apk/debug/app-debug.apk
adb -d shell monkey -p me.sensta.debug 1
```

기기가 둘 이상 연결되어 대상이 모호하면 `adb devices`에서 serial을 확인한 뒤 모든 명령에
`adb -s SERIAL`을 사용합니다. 앱 목록에는 Play 앱과 debug 앱이 같은 이름으로 보일 수 있으므로 앱
정보의 패키지 이름으로 구분합니다.

### 축소·난독화 QA APK

Play와 같은 R8 설정을 로컬에서 먼저 확인할 때는 `qa`를 사용합니다. `qa`는 `me.sensta.debug`로
설치되지만 release처럼 비디버그·코드 축소·리소스 축소가 활성화됩니다.

Firebase에 등록한 인증서가 Windows Android Studio의 debug 키라면 WSL 빌드에 그 키를 지정합니다.

```bash
export SENSTA_QA_STORE_FILE=/mnt/c/Users/사용자명/.android/debug.keystore
./gradlew :app:assembleQa
adb -d install -r app/build/outputs/apk/qa/app-qa.apk
```

기존 debug 앱이 다른 키로 서명됐다면 제거 후 설치해야 하며, 이때 debug 앱의 로컬 세션은 삭제됩니다.
Play 앱 `me.sensta`와 운영 데이터에는 영향이 없습니다.

## 4. 사진 업로드 시나리오

한 번에 최대 9장, 합계 100MB까지 선택할 수 있습니다. 다음 순서로 각각 새 게시글을 만듭니다.

1. 일반 JPEG 1장: 선택, 미리보기, 제목·본문·태그, 운영 원칙 동의와 업로드를 확인합니다.
2. 세로 사진 1장: 상세 화면의 회전 방향, 잘림, 확대 보기와 EXIF를 확인합니다.
3. Galaxy 고효율 사진(HEIF/HEIC) 1장: 선택·업로드·서버 변환 실패 여부를 확인합니다.
4. 서로 다른 방향의 사진 2~3장: 순서와 상세 캐러셀 이동을 확인합니다.
5. 9장: 선택 제한, 썸네일 그리드, 업로드 시간과 완료 후 상세 화면을 확인합니다.
6. 100MB에 가까운 조합과 100MB 초과 조합: 정상 업로드와 크기 초과 안내를 각각 확인합니다.
7. 업로드 중 Wi-Fi를 끊었다가 복구해 오류 안내, 재시도와 중복 게시 여부를 확인합니다.

사진 편집은 원본 파일과 편집본을 비교하며 다음 항목을 추가로 확인합니다.

1. 원본·4:5·3:4 자르기에서 선택한 구도와 업로드 결과가 일치해야 합니다.
2. 선명·따뜻함·차가움·필름·흑백 필터를 0%, 50%, 100%로 적용하고 미리보기와 결과 색상을 비교합니다.
3. 90도 회전과 좌우 반전을 조합한 뒤 결과 방향과 EXIF 방향 값이 정상인지 확인합니다.
4. GPS가 없는 사진은 초기화하면 자르기와 모든 보정이 사라지고 원본 바이트가 그대로 업로드되는지 확인합니다.
5. 카메라·촬영일·ISO·조리개·셔터 속도·초점거리는 편집 뒤에도 같고, 정확한 GPS 위치는 결과에서
   제거되는지 확인합니다.
6. JPEG·PNG·HEIF와 EXIF가 없는 이미지에서 편집 완료와 서버 변환이 모두 성공해야 합니다.

### 2026-08-27 2.1.0 실기기 확인

- Galaxy S25 Edge에서 테스트 전용 PNG를 선택하고 4:5 자르기, 90도 회전, 좌우 반전과 따뜻함 54%를
  조합해 제목 입력 화면까지 진행했다. 운영 게시물은 등록하지 않았다.
- 비디버그·축소 QA 앱에서도 3:4 자르기, 회전·반전과 필름 76%를 조합해 제목 입력 화면까지
  동일하게 진행했다.
- 최종 편집본은 JPEG `1000×800`, EXIF 방향 `upper-left`로 생성됐고 미리보기와 결과 구도가 일치했다.
- 제목 입력 화면에서 돌아왔을 때 자르기·회전·반전·필터 강도가 유지됐다.
- 합성 JPEG의 원본 무편집 바이트 보존과 편집 후 카메라·촬영일·노출·초점거리·ISO·GPS 승계를
  검증하는 기기 테스트 2개가 통과했다.
- 테스트 후 전용 사진, 앱 편집 캐시와 계측 테스트 패키지를 제거하고 축소 QA 앱 `2.1.0-qa` 홈으로 복원했다.

### 2026-08-28 Android 전용 개선 빌드 확인

- 게시물 공유 문구와 공개 주소 단위 테스트를 추가하고 전체 단위 테스트 10개를 통과했다.
- GPS가 없는 무편집 원본 보존, GPS가 있는 무편집 JPEG의 원본 해상도·방향 유지 및 위치 제거,
  편집본의 안전한 EXIF 유지와 GPS 제거를 검증하는 계측 테스트 3개가 Galaxy S25 Edge에서 통과했다.
- Debug Lint와 Debug APK·AndroidTest APK 빌드를 통과했다. 테스트 APK는 Windows 디버그 키로 임시
  재서명해 Windows ADB로 실행했으며, 완료 후 테스트 패키지를 제거하고 최신 `2.1.0-qa`로 복원했다.
- 시스템 공유 선택 화면과 TalkBack 이미지 설명의 수동 확인은 다음 기기 QA에서 진행한다.

### 2026-08-30 Google 로그인 복구 확인

- Windows ADB로 Galaxy S25 Edge의 축소 `2.1.1-qa`와 Google Play 설치본 `2.1.0`을 함께 확인했다.
- ID token은 올바른 audience, issuer와 email verification claim을 포함했지만 운영 GOAPI가
  `invalid google token claims`를 반환하는 것을 Logcat에서 확인했다.
- 수동 GOAPI가 실제로 읽는 `/var/www/sensta.me/.env`에 `OAUTH_GOOGLE_ANDROID_CLIENT_ID`를 추가하고
  재시작한 뒤 QA 앱과 기존 Play 앱에서 Google 로그인을 각각 다시 실행했다.
- 두 앱 모두 Google 로그인과 push 기기 등록이 HTTP 200으로 끝났고, Play 앱은 알림·게시물 후속
  조회도 HTTP 200으로 완료됐다.

### 2026-08-30 내 작품 스튜디오 확인

- 축소·비디버그 `2.1.1-qa`를 데이터 삭제 없이 덮어쓰고 로그인 세션이 유지되는 것을 확인했다.
- 내정보 진입 시 `GET /goapi/board/my/studio?id=photo&page=1&limit=20&sort=recent`가 HTTP 200으로
  완료되고 프로필, 누적 작품·사진·조회·좋아요·댓글과 작품별 썸네일·사진 수·업로드일·성과가 표시됐다.
- 조회순·좋아요순·댓글순·최신순을 차례로 눌러 각각 정확한 query와 HTTP 200, 선택 표시와 첫 작품의
  변경을 확인했다.
- 최신순 목록을 끝까지 내려 89개 작품을 20개씩 1~5페이지로 받았고 `hasNext=false` 이후에는
  6페이지 요청이 발생하지 않았다.
- 작품 카드를 눌러 기존 상세 화면으로 이동하고 뒤로 돌아온 뒤, `내 정보` 탭에서 기존 기본 정보와
  서명·계정·앱 정보 화면이 유지되는 것을 확인했다.

### 2026-09-03 2.1.3 업적 확인

- 2.1.3 debug APK를 Galaxy S25 Edge에 데이터 삭제 없이 덮어쓰고 기존 로그인 세션을 유지했다.
- 관리 화면에서 수동 생성한 `유지보수상`을 수여한 뒤 앱의 새 업적 축하창이 한 번 표시되고 확인 요청이
  HTTP 200으로 완료되는 것을 확인했다. 프로필의 업적 수는 2개에서 3개로 늘었고 수동 배지도 진열장에
  표시됐다.
- 앱을 다시 실행했을 때 확인한 축하창이 반복되지 않아 서버의 `announced_at` 계약과 앱의 확인 처리가
  함께 동작함을 확인했다. Android 로그에는 비정상 종료나 ANR이 없었다.
- 이후 내 프로필을 `작품·정보·업적` 3개 탭, 업적 2열 진열장 구조로 개편한 최신 debug APK도 같은
  기기에 설치했다. 다만 기기가 잠금·절전 상태여서 개편 뒤 최종 화면 확인은 완료하지 못했다. 잠금 해제 후
  세 탭 전환, 누적 조회·댓글 요약 제거, 축하창의 `내 업적 진열장` 버튼이 업적 탭으로 이동하는지 확인한다.

각 성공 게시글은 홈·탐색·상세에서 대표 이미지, 전체 사진, 제목·본문·태그, EXIF가 일치해야 합니다.
다른 계정에서 댓글과 좋아요를 남기고 알림 화면과 딥 링크도 확인합니다.

## 5. 전체 회귀 체크

- 이메일/Google 로그인, 앱 재시작 후 세션과 토큰 갱신
- 알림 권한 허용·거부, 포그라운드·백그라운드 FCM 수신
- 알림에서 게시글·대화 화면으로 이동
- 1:1 대화 전송, 수신, 실패 시 입력 보존
- 신고와 차단 후 피드·댓글·대화 숨김, 차단 해제
- 밝은/어두운 테마, 글꼴 크기 확대, 세로/가로 전환
- 새 업적 축하창의 1회 표시·확인 재시도와 프로필 `작품·정보·업적` 탭 전환
- 전용 일회성 계정의 앱 내 계정 삭제와 재로그인 거부

문제가 생기면 재현 직전에 로그를 비우고 같은 동작을 한 번만 반복해 로그를 저장합니다.

```bash
adb -d logcat -c
adb -d logcat | rg 'Sensta-Nubo|Sensta-App|Sensta-GoogleAuth|AndroidRuntime|FATAL EXCEPTION'
```

`Sensta-Nubo`는 본문·인증값 없이 API 경로, HTTP 상태와 소요 시간만 기록합니다. `Sensta-App`은 화면
작업과 이미지 로딩 실패를 기록하므로 목록 요청, 응답 변환, 이미지 다운로드 문제를 구분할 수 있습니다.

테스트가 끝나면 debug 앱만 제거할 수 있습니다.

```bash
adb -d uninstall me.sensta.debug
```
