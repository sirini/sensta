# Google Play 재출시 안내

## 현재 빌드 기준

- 패키지: `me.sensta`
- 버전: `2.0.0` (`versionCode 20`)
- 최소 Android: 8(API 26)
- 컴파일 SDK: Android 17(API 37)
- 대상 Android: 16(API 36)
- 결과물: `app/build/outputs/bundle/release/app-release.aab`
- 권한: 인터넷, Android 13 이상의 알림
- 자동 백업과 평문 HTTP: 비활성화
- 릴리스 코드·리소스 축소: 활성화

2026-08-21에 기존 Play 업로드 키로 서명한 `versionCode 20` App Bundle을 Play Console이 수락했으며,
개인정보처리방침 URL을 `https://sensta.me/privacy`로 바로잡아 내부 테스트 심사에 제출했다. 기존 공개
버전은 `1.0.2`(`versionCode 3`)이다.

2026년 8월 31일부터 일반 모바일 앱의 신규 제출과 업데이트는 API 36 이상이 필요하므로 현재 설정은
[Google Play 대상 API 정책](https://support.google.com/googleplay/android-developer/answer/11926878)을
충족한다.

## 업로드 키 연결

비밀키와 비밀번호는 저장소에 커밋하지 않는다. 기존 Play Console의 **설정 > 앱 서명**에서 업로드 인증서를
확인하고, 기존 키를 찾을 수 없다면 Play App Signing의 업로드 키 재설정을 요청한다. 앱 서명 키 자체와
업로드 키의 역할은 [Android 앱 서명 안내](https://developer.android.com/studio/publish/app-signing)를 따른다.

다음 값을 `~/.gradle/gradle.properties`에 저장하거나 같은 이름의 환경변수로 제공한다.

```properties
SENSTA_STORE_FILE=/절대/경로/sensta-upload.jks
SENSTA_STORE_PASSWORD=저장소-비밀번호
SENSTA_KEY_ALIAS=업로드-키-별칭
SENSTA_KEY_PASSWORD=키-비밀번호
```

서명 설정 없이도 로컬 릴리스 검증은 가능하지만 Play에 올릴 AAB는 반드시 아래처럼 서명을 확인한다.

```bash
./scripts/check.sh
source ./scripts/android-env.sh
$ANDROID_HOME/build-tools/37.0.0/apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
$JAVA_HOME/bin/jarsigner -verify app/build/outputs/bundle/release/app-release.aab
```

Windows에서는 Android Studio에 포함된 `jbr/bin/jarsigner.exe`를 사용할 수 있다. AAB 검증 결과에
`jar verified.`가 있어야 하며, 자체 서명된 업로드 인증서의 PKIX chain·timestamp 경고는 Play가 최종
배포 APK를 앱 서명 키로 다시 서명하는 과정과 구분한다.

Play Console에 등록된 기존 최신 `versionCode`는 3으로 확인했다. 이후 빌드에서는 20보다 큰 코드 값을
사용한다.

## 제출 전에 외부에서 준비할 항목

- `app/google-services.json`: `me.sensta`를 등록한 운영 Firebase 프로젝트 파일
- GOAPI 서버의 Firebase 서비스 계정과 FCM 전송 환경변수
- 앱 아이콘, 기능 그래픽, 휴대전화 스크린샷과 한국어 스토어 설명
- 문의 이메일, 개인정보 처리방침 `https://sensta.me/privacy`
- 계정 삭제 웹 경로 `https://sensta.me/delete-account`
- 실제 Android 8, 13, 16 기기의 사진 선택·업로드·알림·딥 링크 검증

## Play Console 정책 점검

앱 안에서 계정을 생성할 수 있으므로 앱 내부 삭제와 외부 웹 삭제 경로를 모두 유지해야 한다. 현재 앱은
프로필에서 즉시 삭제할 수 있고 NUBO는 별도 삭제 페이지를 제공한다. 제출 시
[계정 삭제 요건](https://support.google.com/googleplay/android-developer/answer/13327111)에 맞춰 Data safety의
삭제 질문과 URL을 함께 갱신한다.

Data safety에는 실제 운영 서버 동작을 다시 확인한 뒤 적어도 다음 범주를 검토한다.

| 데이터 | 사용 목적 예시 | 처리 확인 사항 |
| --- | --- | --- |
| 이메일·표시 이름·프로필 | 계정 관리, 앱 기능 | Google 로그인 포함 여부와 삭제 정책 |
| 사진·게시글·댓글 | 사용자 생성 콘텐츠 | 공개 공유와 신고 처리 |
| 1:1 메시지 | 앱 기능 | 전송·서버 저장과 계정 삭제 |
| 좋아요·차단·신고 | 앱 기능, 안전 | 공개 여부와 운영상 보존 기간 |
| Firebase Installation ID | 푸시 알림 | 서비스 제공자 Firebase 명시 |

광고·위치·연락처·전화·SMS 권한은 사용하지 않는다. 모든 전송은 HTTPS이며 기기 자동 백업은 차단한다.

## 권장 출시 순서

1. [운영 서버 배포](SERVER_DEPLOYMENT.md)에 따라 Sensta 2.0 계약을 포함한 NUBO·GOAPI 릴리스를
   `sensta.me`에 먼저 반영한다.
2. 기존 Play 앱의 패키지, 최신 버전 코드, 앱 서명·업로드 인증서를 확인한다.
3. Firebase 운영 설정을 연결하고 서명된 AAB를 만든다.
4. [Galaxy 실제 기기 테스트](DEVICE_TESTING.md)와 Play 내부 테스트에서 핵심 시나리오와 비정상 종료·ANR을 확인한다.
5. 회사 사진 사용자 그룹으로 비공개 테스트와 피드백 수집을 진행한다.
6. Data safety, 콘텐츠 등급, 앱 액세스, UGC 정책과 스토어 등록정보를 제출한다.
7. 단계적 배포로 시작하고 Android vitals와 서버 오류를 관찰한다.

2023년 11월 13일 이후 개설한 개인 개발자 계정에만 적용되는 별도 제작 앱이라면, 프로덕션 접근 전에
[12명 이상이 14일 연속 참여하는 비공개 테스트](https://support.google.com/googleplay/android-developer/answer/14151465)가
필요하다. 기존 계정·기존 앱 업데이트인지 Play Console에서 확인한다.
