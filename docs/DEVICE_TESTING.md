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

각 성공 게시글은 홈·탐색·상세에서 대표 이미지, 전체 사진, 제목·본문·태그, EXIF가 일치해야 합니다.
다른 계정에서 댓글과 좋아요를 남기고 알림 화면과 딥 링크도 확인합니다.

## 5. 전체 회귀 체크

- 이메일/Google 로그인, 앱 재시작 후 세션과 토큰 갱신
- 알림 권한 허용·거부, 포그라운드·백그라운드 FCM 수신
- 알림에서 게시글·대화 화면으로 이동
- 1:1 대화 전송, 수신, 실패 시 입력 보존
- 신고와 차단 후 피드·댓글·대화 숨김, 차단 해제
- 밝은/어두운 테마, 글꼴 크기 확대, 세로/가로 전환
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
