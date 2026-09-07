# Google Play 재출시 안내

## 현재 빌드 기준

- 패키지: `me.sensta`
- 버전: `2.1.7` (`versionCode 30`)
- 최소 Android: 8(API 26)
- 컴파일 SDK: Android 17(API 37)
- 대상 Android: 16(API 36)
- 결과물: `app/build/outputs/bundle/release/app-release.aab`
- AAB SHA-256: `bcf7fd94a4adbae13156bac993facc8a0c508d249e9774968cf2150a91476f09`
- 권한: 인터넷, Android 13 이상의 알림
- 자동 백업과 평문 HTTP: 비활성화
- 릴리스 코드·리소스 축소: 활성화

## 2.1.7 변경사항 노트

권장 출시명은 `2.1.7 (30)`이다. 아래 문안을 Play Console의 한국어 변경사항에 사용한다.

```text
<ko-KR>
업로드 안정성과 핵심 편의 기능을 보강했습니다.

• 업로드 전 세션 갱신과 서버별 게시판·분류 설정 반영
• 비밀번호 재설정, 가입 정책·초대 코드와 안전한 로그아웃
• 1:1 메시지 목록과 사진가 공개 활동 통계
• 업로드·수정 화면의 태그 자동 완성
• 긴 사진 처리 응답을 충분히 기다리도록 개선
</ko-KR>
```

## 2.1.6 변경사항 노트

권장 출시명은 `2.1.6 (29)`이다. 아래 문안을 Play Console의 한국어 변경사항에 사용한다.

```text
<ko-KR>
더 편안하고 정확한 1:1 대화를 만나보세요.

• 대화 중 새 메시지 자동 갱신 및 읽음 상태 표시
• 프로필 사진, 전송 시각과 전송됨·읽음 표시 개선
• 메시지의 해시태그를 눌러 관련 사진 탐색
• 2,000자 입력 제한과 개인정보 공유 주의 안내 추가
• 키보드가 입력창을 가리거나 빈 공간이 생기는 문제 수정
</ko-KR>
```

## 2.1.4 변경사항 노트

권장 출시명은 `2.1.4 (27)`이다. 아래 문안을 Play Console의 한국어 변경사항에 사용한다.

```text
<ko-KR>
댓글과 답글을 더 편리하게 이용해 보세요.

• 댓글에 답글 작성 기능 추가
• 답글 대상과 원문 미리보기 제공
• 작성 시각과 답글 구분 표시 개선
• 댓글 좋아요 수를 하트 옆에 표시
• 로그인 세션과 Google 로그인 안정성 개선
</ko-KR>
```

## 2.1.3 변경사항 노트

권장 출시명은 `2.1.3 (26)`이다. 아래 문안을 Play Console의 한국어 변경사항에 사용한다.

```text
<ko-KR>
프로필에서 나만의 업적을 모아보세요.

• 작품·정보·업적 3개 탭으로 프로필 화면 개편
• 획득한 업적을 한눈에 보는 전용 진열장 추가
• 새 업적 획득 시 축하 화면 제공
• SENSTA 앱으로 올린 사진에 앱 사용자 배지 표시
• 프로필 화면 공간과 전반적인 사용성 개선
</ko-KR>
```

## 2.1.2 변경사항 노트

권장 출시명은 `2.1.2 (25)`다. 아래 문안을 Play Console의 한국어 변경사항에 사용한다.

```text
<ko-KR>
게시글과 댓글 수정·삭제 기능을 개선했습니다.

• 좋아요와 댓글이 홈 및 상세 화면에 즉시 반영되도록 개선
• 탐색 화면 진입 시 최신 게시글 자동 갱신
• 게시글과 댓글 수정·삭제 안정성 개선
• 전반적인 오류 처리와 사용성 개선
</ko-KR>
```

## 2.1.1 변경사항 노트

권장 출시명은 `2.1.1 (24)`다. 아래 문안을 Play Console의 한국어 변경사항에 사용한다.

```text
<ko-KR>
내 작품의 활동을 한눈에 확인해 보세요.

• 내정보 첫 화면에 내 작품 스튜디오 추가
• 작품·사진 수와 누적 조회·좋아요·댓글 제공
• 작품별 성과와 업로드 날짜 확인
• 최신순·조회순·좋아요순·댓글순 정렬
• Google 로그인과 세션 안정성 개선
• 다크 테마와 오류 처리 개선
</ko-KR>
```

### Google 로그인 장애 확인 결과

2026-08-30 Galaxy S25 Edge에서 Google Play 2.1.0과 축소 2.1.1 QA 앱의 로그인을 확인했다. 앱이
발급받은 ID token의 audience는 운영 Web OAuth client ID와 일치했지만, 수동 실행 중인 GOAPI가 읽는
`/var/www/sensta.me/.env`에 `OAUTH_GOOGLE_ANDROID_CLIENT_ID`가 없어 다른 웹 client ID로 fallback하고
있었다. 실제 `.env`에 Android ID token audience를 추가하고 GOAPI를 재시작한 뒤 두 앱 모두 Google
로그인, push 기기 등록과 후속 API 요청이 HTTP 200으로 완료됐다.

현재 수동 운영 명령은 `/var/www/sensta.me`에서 아래와 같다. `/etc/nubo/nubo.env`는 이 프로세스가
읽지 않으므로 OAuth 값을 그 파일에만 넣어서는 안 된다.

```bash
NUBO_ENV_FILE="$PWD/.env" ./bin/goapi
```

## 2.1.0 변경사항 노트

권장 출시명은 `2.1.0 (23)`이다. 아래 문안을 Play Console의 한국어 변경사항에 그대로 사용한다.

```text
<ko-KR>
사진을 원하는 분위기로 다듬어 바로 공유해 보세요.

• 원본, 4:5, 3:4 비율 자르기
• 90도 회전, 좌우 반전과 초기화
• 선명, 따뜻함, 차가움, 필름, 흑백 필터
• 필터 강도 0~100% 조절
• 주요 촬영 정보(EXIF) 유지 및 정확한 위치 정보 제거
• 게시물 공유와 사진 설명 접근성 개선

편집본만 생성하므로 원본 사진은 그대로 보존됩니다. 사진 업로드의 안정성과 사용성도 함께 개선했습니다.
</ko-KR>
```

2026-08-21에 기존 Play 업로드 키로 서명한 `versionCode 20` App Bundle을 Play Console이 수락했으며,
개인정보처리방침 URL을 `https://sensta.me/privacy`로 바로잡아 내부 테스트 심사에 제출했다. 당시 공개
버전은 `1.0.2`(`versionCode 3`)였다.

`versionCode 20`에서는 Retrofit 2.9.0의 불완전한 R8 full mode 규칙 때문에 suspend API 반환 타입이
제거되어 앱 시작 시 사진 목록 요청 자체가 만들어지지 않았다. 2.0.1은 누락된 제네릭 보존 규칙을
추가하고 로컬 축소 QA APK에서 목록 HTTP 200과 사진 표시를 확인한 뒤 올리는 교정 버전이다.

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

## 현재 Play 배포 상태

2.1.5(`versionCode 28`)는 2026-09-06에 Google Play 업데이트 반영을 완료했다. 이후 1:1 메시지 개선을
담은 2.1.6(`versionCode 29`)의 서명·축소 Release AAB를 빌드·검증하고 Play Console에 업로드했다.
심사 제출과 프로덕션 배포 완료 여부는 별도로 확인한다. AAB SHA-256은
`c259b44443f0907bb59cabd421c35a983de9046f952294e51c5e2a8c23d17441`이다.

Android의 사진 업로드 응답 제한 교정과 iOS 핵심 기능 정합성을 반영한 2.1.7(`versionCode 30`)의
서명·축소 Release AAB도 빌드·검증했다. 실제 기기에서 핵심 흐름을 재확인한 뒤 Play Console에 올린다.
AAB SHA-256은 `bcf7fd94a4adbae13156bac993facc8a0c508d249e9774968cf2150a91476f09`이다.

`versionCode 21`은 2026-08-22 16:10(KST)에 대한민국 대상 프로덕션 트랙으로 100% 게시되었다. Play
Console에서 트랙 `활성`, `Google Play에 제공됨`, 대상 국가/지역 대한민국, 지원 Android 기기 17,678대를
확인했다.

`versionCode 22`는 GOAPI의 원본 경로 비노출 계약에 맞춘 게시글 상세 복구, Oleo Script 워드마크와
버전 정보 위치 개선을 담은 2.0.2 업데이트로 Google Play에 출시됐다. `versionCode 23`은 사진별 자르기,
회전·반전과 강도 조절 필터를 추가한 2.1.0 업데이트다. 서명·축소 빌드와 Galaxy S25 Edge 검증을
마쳤으며 AAB SHA-256은
`7ae29037391e2e24194ecd7c97971ac06d8a4a6d0c45ed680fbbd424d0971484`다.

게시 당일 저녁에는 한국 대상 공개 상세 URL도 아직 HTTP 404를 반환했다. 설정을 다시 변경하거나 새 출시를
만들기보다 먼저 24시간 동안 스토어 전파를 기다린다. 게시 후 24~48시간이 지나도 직접 링크가 열리지 않으면
패키지 `me.sensta`, 버전 코드 21, 게시 시각과 Play Console 상태를 첨부해 지원팀에 문의한다. 직접 링크가
열린 뒤에는 별도로 Play 검색 색인 반영 여부를 확인한다.

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
2. 기존 Play 앱의 패키지와 최신 버전 코드를 확인하고 2.1.7의 `versionCode 30`이 더 큰지 확인한다.
3. Firebase 운영 설정, 업로드 인증서와 위 SHA-256이 일치하는 서명된 AAB를 사용한다.
4. [Galaxy 실제 기기 테스트](DEVICE_TESTING.md)와 Play 내부 테스트에서 핵심 시나리오와 비정상 종료·ANR을 확인한다.
5. 회사 사진 사용자 그룹으로 비공개 테스트와 피드백 수집을 진행한다.
6. Data safety, 콘텐츠 등급, 앱 액세스, UGC 정책과 스토어 등록정보를 제출한다.
7. 단계적 배포로 시작하고 Android vitals와 서버 오류를 관찰한다.

2023년 11월 13일 이후 개설한 개인 개발자 계정에만 적용되는 별도 제작 앱이라면, 프로덕션 접근 전에
[12명 이상이 14일 연속 참여하는 비공개 테스트](https://support.google.com/googleplay/android-developer/answer/14151465)가
필요하다. 기존 계정·기존 앱 업데이트인지 Play Console에서 확인한다.
