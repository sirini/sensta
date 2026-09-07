# Sensta 2.1 프로젝트 상태

## 현재 목표

- 댓글 답글·세션 복원과 피드 상단 계정/알림, 중앙 업로드·탐색, 롱터치 배경화면 동작을 반영한
  2.1.5(`versionCode 28`)의 Google Play 업데이트를 완료했다. 이후 1:1 메시지 개선을 담은
  2.1.6(`versionCode 29`)의 서명된 AAB를 Play Console에 업로드했으며 심사·배포 상태와 안정성 지표를
  확인한다. 운영에서 확인한 Android 사진 업로드 응답 제한 문제는 2.1.7(`versionCode 30`)에서
  교정했으며 실제 기기 재검증 뒤 Play Console에 올린다.

## 결정

- 현재 Android 유지보수의 기준은 Windows checkout의 `main`이며 기능 단위로 검증·커밋·푸시한다.
- 빌드 기반은 JDK 17, Gradle 9.5, AGP 9.3, Kotlin 2.4.10, compile SDK 37과 target SDK 36으로 고정한다.
- 최소 지원 버전은 Android 8(API 26)로 낮춘다.
- PC 장애에 대비해 검증된 기능 단위마다 작게 커밋하고 즉시 GitHub 원격 브랜치에 푸시한다.
- 실제 기기용 debug 앱은 `me.sensta.debug`로 설치해 Play 앱 `me.sensta`와 데이터·서명을 분리한다.
- 500px·Unsplash를 참고해 사진 중심 정보 구조와 시각 밀도로 홈 피드와 사진가 프로필을 개편한다.
- NUBO·GOAPI와 함께 계약을 바꾸는 작업은 세 저장소의 상태·API·배포 문서를 같은 세션에서 갱신한다.
- Kotlin annotation processing은 KAPT 대신 KSP를 사용한다.
- 최신 Nubo와 GOAPI의 API contract v1을 Android 네트워크 계층의 기준으로 삼는다.
- 새로 작성하거나 의미를 바로잡는 코드 주석은 한국어로 작성한다.
- 2.0.1 교정판부터 기능별 API·세션·인증 책임을 분리하고 축소 QA를 출시 전 필수 단계로 둔다.
- 2.1 사진 자르기는 원본·4:5·3:4 비율만 제공하고 필터는 원본 포함 6종과 0~100% 강도로 제한한다.
- 원본 파일은 수정하지 않고 편집본만 앱 캐시에 만들며 촬영·노출 EXIF는 유지하되 공개 업로드에는
  정확한 GPS 위치를 승계하지 않는다. 위치 정보가 없는 무편집 사진은 재인코딩하지 않는다.
- 2.1 이후 경쟁력 개선은 먼저 Sensta Android 앱만으로 끝낼 수 있는 공유·접근성·업로드 신뢰성부터
  진행하고, 팔로우·피드백·전시 커뮤니티처럼 서버 데이터 모델이 필요한 기능은 별도 검토한다.
- 사용자가 직접 누르는 Google 로그인 버튼에는 Credential Manager의 button flow를 사용하고,
  로그아웃과 계정 삭제 시 credential provider의 활성 상태도 함께 초기화한다.
- 현재 `sensta.me`의 수동 GOAPI 운영은 `/var/www/sensta.me`에서
  `NUBO_ENV_FILE="$PWD/.env" ./bin/goapi`로 실행하며 `/etc/nubo/nubo.env`는 사용하지 않는다.

## 완료

- WSL 사용자 홈에 Temurin JDK 17과 Android SDK 37, Build Tools 37, Platform Tools를 설치했다.
- API 37로 컴파일하고 API 36 및 Android 8 이상을 대상으로 세 모듈을 현대화했다.
- Hilt·Room annotation processing을 KSP로 이관했다.
- Android 13 미만에서 알림 권한을 잘못 요청하던 경로와 프로필 사진의 불필요한 저장소 권한 요청을 제거했다.
- GOAPI에 네이티브 앱용 `POST /auth/android/refresh` 토큰 회전 계약을 추가하고 서버 테스트를 통과시켰다(`goapi` `dc27f53`).
- Nubo API 계약 문서에 Android 로그인·토큰 갱신 기준을 반영했다(`nubo` `20775ec`).
- 로그인·회원가입·이메일 인증·중복 검사·토큰 갱신을 최신 API 계약으로 이관했다.
- 비밀번호를 클라이언트에서 SHA-256 처리하던 오래된 동작을 제거하고 TLS를 통해 원문을 전달해 서버의 bcrypt 검증과 일치시켰다.
- 액세스 토큰을 갱신할 때 회전된 리프레시 토큰도 함께 저장하도록 화면과 백그라운드 작업을 수정했다.
- 인증 응답 계약 회귀 테스트 5개를 추가했다.
- 삭제된 갤러리 전용 API 대신 최신 `GET /board/list` 페이지 계약으로 홈 피드와 탐색 화면을 통합했다.
- 목록에서는 최적화된 대표 이미지를 사용하고 게시글 상세에서는 전체 이미지 캐러셀과 EXIF를 유지한다.
- 게시글·댓글 좋아요와 게시글 삭제 요청을 GOAPI가 요구하는 JSON body 계약으로 수정했다.
- 댓글, 최신 글, 알림, 사용자 정보 경로와 파라미터를 최신 API 계약으로 이관했다.
- 알 수 없는 응답 필드가 추가되어도 기존 앱이 중단되지 않도록 JSON 역직렬화를 전방 호환 방식으로 설정했다.
- 공개 읽기 요청의 익명 사용자 UID를 GOAPI에서 0으로 정규화했다(`goapi` `e7c4c0d`).
- 게시글 목록과 좋아요 요청 계약 회귀 테스트 3개를 추가했다.
- 라이브 게시글 상세의 다중 이미지·EXIF와 댓글 목록 응답을 검증하고 계약 테스트 3개를 추가했다.
- 글쓰기 오류 응답에 `result`가 없어도 정상 처리하도록 응답 모델을 보강했다.
- 사진·프로필 업로드용 콘텐츠 URI 복사를 I/O 스레드로 옮기고 입력 스트림과 임시 캐시 파일을 성공·실패 모두 정리하도록 안정화했다.
- 채팅 전송을 최신 JSON body 계약으로 이관하고 대화 기록의 서버 시간순 정렬을 보존하도록 수정했다.
- 전송 성공 시 서버가 반환한 실제 메시지 UID를 화면 상태에 반영하고, 실패한 메시지는 입력창에 남도록 개선했다.
- 알림·채팅·사용자 정보의 오류 응답에 `result`가 없어도 안전하게 처리하고 계약 테스트 5개를 추가했다.
- GOAPI 채팅 입력에서 공백·2,000자 초과·자기 자신 대상 메시지를 거부하고 이중 HTML 이스케이프를 제거했다(`goapi` `0081bc5`).
- NUBO 계정 삭제·신고·차단 계약과 앱 내 안전 기능을 구현했다.
- Firebase FID 기반 푸시와 알림 딥 링크, 대화 중 실시간 갱신을 구현했다.
- NUBO의 따뜻한 색상과 사진 중심 레이아웃을 반영한 밝은·어두운 디자인 시스템을 적용했다.
- 버전을 2.0.0으로 올리고 자동 백업·평문 통신을 차단했으며 릴리스 축소와 외부 서명 주입을 구성했다.
- Play 제출 체크리스트와 NUBO 커뮤니티 포크 안내를 문서화했다.
- 손상된 Gradle 다운로드를 확인하고 Wrapper SHA-256·재시도를 추가했으며 AGP 9 호환 Kotlin·Hilt로 갱신했다.
- Galaxy 실제 기기 테스트와 NUBO·GOAPI 통합 배포 절차를 문서화했다.
- GOAPI `42481c5`를 고정한 NUBO v1.2.16을 게시하고 GitHub Actions run `32437963579`의 전체 게이트·Ubuntu 22.04/24.04 fresh-install·Release 게시와 공개 asset SHA-256을 확인했다.
- NUBO v1.2.16과 Firebase 서비스 계정을 `sensta.me` 운영 서버에 반영했다.
- Firebase 프로젝트에 `me.sensta`와 `me.sensta.debug`를 연결하고 Galaxy S25 Edge에서 Google 로그인과 웹 로그인의 공존을 확인했다.
- 홈을 preview 이미지 기반 전체 화면 세로 피드로 개편하고 상세 화면을 다녀와도 피드 위치를 유지하도록 했다.
- 사진가 페이지를 최근 작품 헤더, 접히는 프로필과 사진·메시지 탭으로 개편했다.
- 상세 이미지의 좌우 letterbox를 제거하고 태그를 한 줄 가로 스크롤로 정리했다.
- 카메라 조리개와 빛의 흐름을 결합한 새 adaptive launcher 아이콘을 적용했다.
- README를 2.0 기능, 아키텍처, 개발 환경, Firebase, 실기기 테스트와 출시 흐름 중심으로 전면 개편했다.
- 기존 PKCS12 업로드 키와 별칭 `sensta`를 복구하고 Windows 사용자 Gradle 설정으로 외부 서명을 연결했다.
- `versionCode 20`의 서명된 release AAB를 생성하고 RSA 2048·SHA-256 서명과 2052-09-22까지의 인증서 유효기간을 확인했다.
- Play Console의 기존 최신 `versionCode 3`보다 큰 `versionCode 20` App Bundle을 등록했다.
- Play 개인정보처리방침 URL을 잘못된 `/policy`에서 공개 경로 `https://sensta.me/privacy`로 바로잡고 내부 테스트 변경사항을 심사에 제출했다.
- 코드와 파일에 남아 있던 이전 백엔드 명칭을 현재 연동 대상인 `Nubo`로 교체했다.
- API 인터페이스를 인증·게시글·알림·사용자 기능으로 분리하고 로컬 세션 저장소와 Google Credential 처리를 분리했다.
- API·화면·이미지 실패 진단 로그와 재시도 UI, release와 같은 R8 설정의 설치용 `qa` 빌드를 추가했다.
- Play v20의 사진 미표시 원인을 Retrofit suspend 제네릭 서명이 제거되는 R8 문제로 확인하고 보존 규칙을 추가했다.
- `versionCode 21` 교정판을 대한민국 대상 프로덕션 트랙에 100% 출시했다. Play Console에서 2026-08-22
  16:10(KST) 게시, `Google Play에 제공됨`, 지원 Android 기기 17,678대를 확인했다.
- GOAPI 1.2.26부터 게시글 상세 응답에서 원본 저장 경로를 숨기는 계약에 맞춰 이미지 파일 모델을 UID만
  요구하도록 수정하고, 상세 화면이 역직렬화 오류로 통신 실패처럼 보이던 회귀를 고쳤다.
- 홈 피드와 화면 상단의 SENSTA 워드마크, 가입 완료 제목을 Oleo Script Bold로 통일했다. 피드에서는
  그림자와 자간을 없애고 낮은 불투명도를 유지했으며 공식 Google Fonts 파일과 OFL 1.1 라이선스를 앱에 포함했다.
- 모든 화면 상단의 버전 칩을 제거하고, 로그인 후 내정보의 `앱 정보 > 버전`에서 기존 상세 버전·정책
  화면으로 이동하도록 정보 구조를 정리했다.
- 게시글 상세 계약 복구와 브랜드 개선을 Google Play에 배포하기 위해 버전을 2.0.2(`versionCode 22`)로 올렸다.
- 다중 사진별 자르기·회전·좌우 반전·초기화와 필터 강도 조절 화면을 업로드 흐름에 추가했다.
- 편집본을 최대 3,072픽셀 작업 비트맵에서 JPEG 품질 95로 한 번만 렌더링하고 주요 EXIF와 결과 크기·방향을 기록하도록 했다.
- 파일 URI 편집본도 기존 multipart 업로드 준비 과정에서 안전하게 이름과 확장자를 처리하도록 보강했다.
- 홈 피드와 게시글 상세에 Android 시스템 공유를 연결해 제목과 공개 웹 주소를 다른 앱으로 전달하도록 했다.
- 게시글 상세와 전체 화면 사진의 TalkBack 설명에 서버의 AI 이미지 설명을 사용하고, 설명이 없으면 제목과
  사진 순번으로 대체하도록 했다.
- GPS가 있는 무편집 JPEG는 원본 해상도와 이미지 데이터를 유지한 사본에서 정확한 위치 EXIF만 제거하고,
  직접 수정하기 어려운 형식은 방향을 픽셀에 반영한 JPEG로 렌더링하도록 보강했다. GPS가 없는 무편집
  사진은 원본 바이트를 그대로 유지한다.
- Google Play 2.1.0의 Google 로그인 실패가 운영 GOAPI의 실제 `.env`에 Android ID token audience가
  빠져 웹용 client ID로 fallback한 문제임을 확인하고 `OAUTH_GOOGLE_ANDROID_CLIENT_ID`를 추가했다.
- GOAPI의 인증 기반 `GET /board/my/studio` 계약을 Android 데이터·도메인 계층에 연결하고, 내정보의
  기본 화면을 프로필·누적 성과·작품별 조회/좋아요/댓글/업로드일과 네 가지 정렬을 제공하는 스튜디오로
  개편했다. 기존 계정 상세와 수정·세션·탈퇴 기능은 `내 정보` 탭에 유지했다.
- GOAPI의 영구 업적 계약을 연결해 피드·상세 작성자 인라인 배지, 새 업적 순차 축하 화면과 본인·다른
  사용자 프로필 진열장을 추가했다. 실기기에서 관리자 수여 `유지보수상`의 1회 축하, 확인 저장과
  프로필 반영을 검증했다.
- 내정보를 `작품·정보·업적` 세 탭으로 재구성하고 업적은 전용 2열 진열장으로 옮겼다. 상단 요약에서
  누적 조회·댓글을 제거해 작품 목록 공간을 넓히고, 축하창의 진열장 버튼은 업적 탭으로 바로 이동한다.
- iOS와 같은 `POST /comment/reply`·`replyTargetUid` 계약을 연결하고, 댓글별 답글 버튼과 대상 작성자·
  원문 미리보기, 답글 들여쓰기 표시를 추가했다. 삭제된 댓글에는 답글 버튼을 노출하지 않는다.
- 댓글과 답글 작성 시각을 `yy/MM/dd HH:mm`으로 줄이고, 좋아요 수를 본문 하단 대신 우측 상단 하트
  옆에 표시했다. 답글 카드의 시작 여백은 48dp로 늘려 일반 댓글과 계층을 명확히 구분했다.
- 저장된 로그인 정보를 앱 시작 시 refresh token으로 검증·회전하고, 한 시간 이상 지난 세션은 앱 복귀
  시 다시 갱신한다. 불완전하거나 서버에서 거부된 세션은 사용자와 로그인 단계 상태를 함께 비워
  이름 없는 `WELCOME BACK` 화면 대신 이메일 로그인 화면을 표시한다.
- 전체 화면 피드의 우상단에 계정·알림을 모으고, 하단 중앙 업로드와 우측 탐색으로 핵심 동작을 정리했다.
  프로필 사진 없는 작성자는 빈 avatar 자리를 만들지 않으며 상태·내비게이션 bar 영역의 가독성을 보강했다.
- 피드 사진을 길게 누르면 Android 공개 `WallpaperManager`로 홈·잠금·양쪽 배경화면을 선택해 적용한다.
  이미지 로드·미지원·정책 거부·실패를 구분해 안내하고 적용 중 중복 요청을 막는다.

## 검증

- 2.1.7 `./scripts/check.sh`의 전체 `test`, `lintDebug`, Debug·QA·Release APK와 Release AAB build를
  통과했다. 업로드 전용 120초 읽기·쓰기 제한과 일반 요청의 기존 10초 제한을 회귀 테스트로 확인했다.
  Release APK는 `me.sensta`, versionCode 30·versionName 2.1.7과 v2 서명을 확인했고 AAB는
  `jar verified.`를 통과했다. AAB SHA-256은
  `d5ed92213008256b3a28f3e90fe5381c2c4b56bc80bb0fba2746992ecbf02e19`이다.
- 2.1.6 `./scripts/check.sh`의 전체 `test`, `lintDebug`, Debug·QA·Release APK와 Release AAB build를
  통과했다. Release APK는 `me.sensta`, versionCode 29·versionName 2.1.6과 v2 서명을 확인했고 AAB는
  `jar verified.`를 통과했다. AAB SHA-256은
  `c259b44443f0907bb59cabd421c35a983de9046f952294e51c5e2a8c23d17441`이다.
- 2.1.5 `./scripts/check.sh`의 전체 `test`, `lintDebug`, Debug·QA·Release APK와 Release AAB build를
  통과했다. Release APK는 `me.sensta`, versionCode 28·versionName 2.1.5와 v2 서명을 확인했고 AAB는
  `jar verified.`를 통과했다. AAB SHA-256은
  `57d691e19b316ff91faa2f4d76e0e9adb0672398f5a2fb893ab5ed33ada7dc76`이다.
- 2.1.4 전체 `test`, `lintDebug`, `assembleDebug`, `assembleQa`, `assembleRelease`, `bundleRelease`를
  통과했다. Release APK는 `me.sensta`, versionCode 27·versionName 2.1.4와 v2 서명을 확인했고 AAB는
  `jar verified.`를 통과했다. AAB SHA-256은
  `c5b65aa69b96e45f19c912b21deaddbff316456a294db3620ec99e47fa6b8046`이다.
- Galaxy S25 Edge에 2.1.4 debug APK를 덮어 설치해 댓글의 짧은 작성 시각, 하트 옆 좋아요 수와
  48dp 답글 들여쓰기를 확인했다. Android 로그에 비정상 종료는 없었다.
- 2.1.3 전체 `test`, `lintDebug`, `assembleDebug`, `assembleQa`, `assembleRelease`, `bundleRelease`를
  통과했다. 서명된 Release APK는 v2 서명과 versionCode 26·versionName 2.1.3을 확인했고 AAB는
  `jar verified.`를 통과했다. AAB SHA-256은
  `5bf5ee97dcd60970d58e75e1788c9291cd2d89f6aaaface80876d2003fd9e1f1`이다.
- Galaxy S25 Edge의 2.1.3 debug 앱에서 관리자 수여 `유지보수상`의 축하창, HTTP 200 확인 저장,
  프로필 업적 2개에서 3개로 증가와 재실행 시 미반복을 확인했다. 3탭 프로필 개편 뒤 APK도 설치했지만
  기기가 잠금·절전 상태여서 최종 시각 확인은 다음 QA로 남겼다.
- Android 답글·세션 복원 회귀 테스트를 포함한 단위 테스트 56개, Debug Lint와 Debug·QA·Release APK,
  Release App Bundle 빌드를 macOS에서 통과했다. Galaxy S25 Edge에 새 debug APK를 설치해 로그아웃
  내정보가 이메일 로그인 화면을 표시하고, 댓글의 답글 버튼과 대상 작성자·원문 미리보기·답글 등록
  대화상자가 표시되는 것을 확인했다. 운영 데이터에 실제 답글을 등록하는 동작은 실행하지 않았다.
- Firebase Android 설정에 Mac debug SHA-1을 추가하고 두 패키지가 든 `app/google-services.json`을
  적용했다. Galaxy S25 Edge에서 Google ID token 발급, `/auth/android/google`, `/push/device`, 사용자
  정보·업적·내 작품 조회가 모두 HTTP 200으로 완료됐다.
- `./scripts/check.sh`(`test`, `lintDebug`, `assembleDebug`, `assembleRelease`, `bundleRelease`) 성공.
- debug APK의 패키지 `me.sensta.debug`, 버전 `2.0.0-debug`, compile SDK 37과 target SDK 36을 확인했다.
- `:data:testDebugUnitTest` 인증 계약 테스트 성공.
- 라이브 `sensta.me/goapi/board/list`에서 사진 게시판 1페이지 32개 응답과 게시판 UID 2를 확인했다.
- 라이브 게시글 7522의 이미지 2장·EXIF 응답과 게시글 7520의 댓글 응답을 확인했다.
- Galaxy S25 Edge debug 앱과 `sensta.me` 웹에서 Google 로그인을 각각 확인했다.
- 디자인·아이콘·README 반영 후 `./scripts/check.sh`의 단위 테스트, Lint, debug·release APK와 release App Bundle 빌드가 모두 성공했다.
- Windows release AAB에서 `jarsigner -verify`의 `jar verified.` 결과를 확인했다.
- Galaxy S25 Edge에서 비디버그·축소 QA APK의 `/goapi/board/list` HTTP 200과 사진 표시를 확인했다.
- 운영 `/goapi/board/view` 응답의 `images[].file`이 UID만 포함하는 형태를 확인하고 같은 형태의 상세 이미지
  계약 테스트를 추가했다.
- 원본 경로 비노출 계약 수정 후 `./scripts/check.sh`의 전체 단위 테스트, Debug Lint, Debug·QA·Release
  APK와 Release App Bundle 빌드를 통과했다.
- 홈 워드마크 변경 후 Debug APK 빌드와 Debug Lint를 통과하고 Galaxy S25 Edge에 같은 개발 키로 덮어썼다.
- 상단 워드마크와 버전 정보 위치 변경 후 Debug APK 빌드·Lint를 통과하고 Galaxy S25 Edge에서 로그인 전
  `SENSTA / PROFILE` 조합과 상단 버전 제거를 확인했다.
- 2.0.2는 `./scripts/check.sh`의 전체 단위 테스트, Debug Lint, Debug·QA·Release APK와 Release AAB
  빌드를 통과했다. Release APK/AAB를 기존 Play 업로드 인증서로 다시 서명해 APK v2 서명과 AAB
  `jar verified.`, versionCode 22·versionName 2.0.2를 확인했다.
- 서명된 2.0.2 AAB SHA-256은 `4811a4942e5278e823c8bda02b16d65eb0dcd2d0ae35a3dd82a87f404eb00b42`이며,
  Galaxy S25 Edge에는 2.0.2-debug(`versionCode 22`)를 기존 개발 키로 덮어썼다.
- 2.1.0 사진 편집 구현 후 전체 단위 테스트, Debug Lint와 Debug·QA·Release APK 및 Release AAB 빌드를 통과했다.
- Galaxy S25 Edge에서 사진 렌더러 계측 테스트 2개와 테스트 전용 이미지의 선택 → 4:5 자르기 →
  회전·반전 → 따뜻함 54% → 제목 입력 전환을 확인했다. 축소 QA 앱에서도 3:4 자르기와 필름
  76% 조합으로 같은 흐름을 통과했다. 결과 JPEG 크기·방향과 뒤로 이동 시 편집 상태 유지도 확인했으며
  운영 업로드는 실행하지 않았다.
- 기존 Play 업로드 키로 서명한 2.1.0 Release APK·AAB의 인증서를 확인하고 Galaxy S25 Edge를
  비디버그·축소 `2.1.0-qa`로 복원했다.
- 게시물 공유·AI 이미지 설명 접근성·GPS 제거 반영 후 `:app:testDebugUnitTest`의 단위 테스트 10개와
  `:app:lintDebug`, Debug APK 및 Debug AndroidTest APK 빌드를 통과했다.
- Windows ADB로 연결된 Galaxy S25 Edge에서 GPS 없는 원본 보존, GPS 포함 무편집 JPEG의 무손실
  위치 제거, 편집 JPEG의 안전한 EXIF 유지와 위치 제거를 검증하는 계측 테스트 3개를 통과했다. 테스트
  패키지를 제거하고 같은 개발 키로 서명한 최신 `2.1.0-qa`를 데이터 삭제 없이 복원했다.
- 재시작된 운영 GOAPI PID의 CWD가 `/var/www/sensta.me`, 실행 파일이 `bin/goapi`, `NUBO_ENV_FILE`이
  `/var/www/sensta.me/.env`임을 `/proc`에서 확인하고 공개 `/ready` 응답도 확인했다.
- Galaxy S25 Edge의 축소 `2.1.1-qa`와 Google Play 설치본 `2.1.0`에서 Google 로그인을 각각 다시
  실행했다. 두 앱 모두 `/goapi/auth/android/google`, push 기기 등록과 후속 조회가 HTTP 200으로
  완료되어 기존 Play 배포판도 서버 설정만으로 복구됐음을 확인했다.
- Google 로그인 복구 직후의 2.1.1 후보는 전체 단위 테스트, Debug Lint, Debug·QA·Release APK와 AndroidTest APK, Release
  AAB 빌드를 통과했다. Release APK의 v2 서명과 AAB의 `jar verified.`, versionCode 24·versionName
  2.1.1을 확인했으며 AAB SHA-256은
  `228d12f30995a98f669a42cc1b90d19ed6e68271d812be64fc8f3617376f0532`다.
- 운영 GOAPI 프로세스가 `/var/www/sensta.me/bin/goapi`를 `/var/www/sensta.me`에서 실행하고
  `NUBO_ENV_FILE=/var/www/sensta.me/.env`를 읽는 것을 확인했다. 인증 없는 스튜디오 직접 요청은 401,
  Galaxy S25 Edge의 축소 QA 앱은 `photo`, `limit=20`으로 recent/views/likes/comments 네 정렬과
  89개 작품의 1~5페이지를 모두 HTTP 200으로 받았다. 마지막 페이지 뒤에는 6페이지를 요청하지 않았다.
- 같은 기기에서 프로필 요약과 작품 썸네일·사진 수·업로드일·조회·좋아요·댓글, 정렬별 첫 작품 변경,
  작품 상세 이동, 기존 `내 정보` 탭을 확인했다. Android 로그에 비정상 종료는 없었다.
- 내 작품 스튜디오와 다크 테마 대비 보정 후 `test`, `lintDebug`, `assembleDebug`, `assembleQa`,
  `assembleRelease`, `bundleRelease` 전체 게이트를 Windows PowerShell에서 다시 통과했다. 최종 QA APK는
  기존 데이터 삭제 없이 Galaxy S25 Edge에 덮어쓰고 운영 스튜디오 요청 HTTP 200을 재확인했다.

## 1:1 메시지 계약 동기화 (2026-09-06)

- GOAPI `f6250c1`의 `readAt` 및 수신자 전용 `PATCH /chat/read` 계약을 Android 데이터·도메인 계층에
  반영했다. 기존 필드만 보내는 서버와도 호환되도록 누락된 `readAt`은 `0`으로 처리한다.
- 1:1 메시지 탭이 화면에 표시되고 앱 생명주기가 STARTED 이상일 때만 12초 간격으로 대화 내역을
  가볍게 갱신한다. 기존 Firebase 메시지 알림에 따른 즉시 갱신도 유지하며, 중복 요청과 느린 응답이
  방금 전송한 로컬 메시지를 덮어쓰는 경우를 막았다.
- 현재 화면에서 확인한 상대방의 마지막 메시지를 `throughUid`로 읽음 처리하고, 내가 마지막으로 보낸
  메시지 한 건에만 `전송됨` 또는 `읽음` 상태를 표시한다.
- 등록된 프로필 이미지는 원형으로 표시하고 미등록·불러오기 실패 시 기본 프로필 아이콘으로 대체한다.
  메시지의 한글·영문·숫자·밑줄 해시태그를 링크로 만들어 기존 태그 탐색 결과로 바로 이동시킨다.
- 일일 이메일, WebSocket, 사진 첨부는 이번 범위에 포함하지 않는다.

## 1:1 메시지 화면 플랫폼 정합성 보정 (2026-09-06)

- Android 작성 영역에 개인정보 공유 주의 문구와 Unicode 기준 `현재/2,000` 글자 수를 항상 표시한다.
  제한을 넘거나 공백뿐인 메시지는 화면과 ViewModel 양쪽에서 전송하지 않는다.
- 전송 아이콘을 iOS와 같은 위쪽 화살표로 통일하고 활성 상태에는 기존 Sensta 테라코타 primary 색을
  사용한다. 작성 영역은 navigation bar와 IME inset을 소비해 Samsung 키보드가 입력란을 가리지 않게 했다.
- 양쪽 말풍선에 한국어 전송 시각을 표시하고, 마지막 발신 메시지의 `전송됨`·`읽음`은 시각과 같은
  메타데이터 행의 오른쪽에 배치했다. 마지막 메시지 아래에는 입력 영역과 별도의 여백을 둔다.
- 대화 탭에 다시 진입하거나 새 메시지가 추가되면 전용 하단 항목까지 즉시 이동하므로 기존 메시지 수가
  같아도 오래된 스크롤 위치에 머물지 않는다.
- 전체 저장소 게이트의 단위 테스트·Debug lint·Debug/QA/Release APK와 Release AAB 빌드를 통과했고,
  최신 Debug APK를 연결된 Galaxy S25 Edge에 데이터 삭제 없이 설치·실행했다.

## Google Play 2.1.5 반영 (2026-09-06)

- 제품 소유자가 2.1.5(`versionCode 28`)의 Google Play 업데이트 반영을 완료했다. 사전 검증을 통과한
  Release AAB의 SHA-256은 `57d691e19b316ff91faa2f4d76e0e9adb0672398f5a2fb893ab5ed33ada7dc76`이다.
- 후속 작업은 새 업로드가 아니라 Play 배포판의 피드 핵심 동작·알림·업로드·딥 링크·안전 기능·접근성과
  비정상 종료·ANR 지표 확인이다.

## Google Play 2.1.6 업로드 (2026-09-06)

- 제품 소유자가 1:1 메시지 개선을 담은 2.1.6(`versionCode 29`) Release AAB를 Play Console에
  업로드했다. 업로드한 AAB의 SHA-256은
  `c259b44443f0907bb59cabd421c35a983de9046f952294e51c5e2a8c23d17441`이다.
- 읽음 처리 등 새 메시지 계약을 제공하는 운영 백엔드는 앞선 iOS 앱 테스트에서 확인했다. 심사 제출과
  프로덕션 배포 완료 여부는 이번 기록에 포함하지 않는다.

## Android 사진 업로드 응답 제한 교정 (2026-09-07)

- 운영 Nginx에서 Android `POST /goapi/editor/write`가 HTTP 499로 끝난 뒤에도 같은 게시물의 서버 AI
  사진 설명 생성이 계속 완료된 기록을 확인했다. GOAPI는 게시물과 첨부를 정상 저장했지만 Android의
  OkHttp 기본 10초 읽기 제한이 먼저 연결을 닫아 앱만 실패로 판단한 것이 원인이었다.
- iOS의 같은 요청에 적용된 120초와 맞춰 Android도 `/editor/write` POST의 읽기·쓰기 제한만 120초로
  늘렸다. 일반 API는 기존 제한을 유지하며 업로드를 자동 재시도하지 않아 중복 게시 위험을 늘리지 않는다.
- 2.1.7(`versionCode 30`) 서명·축소 산출물을 만들었고 GOAPI 계약·운영 runtime 변경은 없다.

## 다음 작업

- Galaxy 실제 기기에서 3장 이상 사진을 운영 업로드해 10초 이후에도 진행 화면을 유지하고 성공 화면과
  새 게시물 UID로 마무리되는지 확인한 뒤 2.1.7 AAB를 Play Console에 업로드한다.
- Google Play 2.1.5 배포판에서 피드의 계정·알림·업로드·탐색 배치와 홈/잠금/양쪽 배경화면 적용을
  최종 확인한다.
- Google Play에서 2.1.6을 설치할 수 있게 되면 대화 자동 갱신·읽음 상태·해시태그 이동과 키보드 레이아웃을
  최종 확인한다.
- Galaxy 잠금 해제 후 `작품·정보·업적` 탭 전환, 누적 조회·댓글 요약 제거, 2열 업적 진열장과 축하창의
  업적 탭 이동을 최종 확인한다.
- Galaxy에서 삭제된 원댓글의 자리 보존과 만료된 실제 세션의 이메일 로그인 화면 전환을 추가 확인한다.
- Android 앱 안에서 업로드 초안과 대기열을 영속화하고 WorkManager 기반 백그라운드 재시도·진행 상태를
  제공할 수 있는지 현재 multipart 업로드 흐름을 기준으로 설계한다.
- 시스템 공유 선택 화면, TalkBack 이미지 설명과 GPS 포함 실제 카메라 사진의 운영 업로드 결과를
  실제 기기에서 수동 검증한다.
- 기존 목록 API가 이미 제공하는 공지 데이터를 활용해 다음 달 사진전 안내를 앱 홈에서 노출하는 방안을
  검토한다.
- Galaxy 원본 HEIF와 EXIF가 풍부한 실제 JPEG, 9장 조합의 메모리 사용량과 100MB 경계를 추가 검증한다.
- 운영 업로드가 허용된 테스트 계정으로 서버 변환과 상세 화면 EXIF를 확인한 뒤 Play 내부 테스트 배포 여부를 결정한다.
- 2.1.6 배포 뒤 비정상 종료, ANR과 메시지 갱신·읽음 처리 실패 지표를 계속 확인한다.
- Play 배포판에서 업로드·알림·딥 링크·안전 기능·접근성을 통합 검증한다.
- Sensta Android, Google 로그인, Firebase, 사진·EXIF·메시지와 삭제 정책을 포함하도록 운영 개인정보처리방침 내용을 보강한다.
- 스토어 기능 그래픽·스크린샷·설명문과 Data safety·앱 액세스·UGC 정책 응답을 지속해서 점검한다.
- Play Console에서 `versionCode 29`의 심사·배포 상태와 2.1.7(`versionCode 30`) 교정판 제출 여부,
  비정상 종료·ANR과 API 36 정책 상태를 확인한다.

## 백엔드 검토가 필요한 후순위

- 팔로우·팔로잉 관계와 팔로잉 전용 피드
- 계정·기기 사이에 동기화되는 북마크와 컬렉션
- 게시물별 피드백 허용 여부와 서버 검증
- 전시 참가·작가·작품 묶음 등 사진전 전용 커뮤니티 데이터
- 청크·재개 업로드처럼 서버 업로드 세션이 필요한 네트워크 복구

## 1:1 메시지 화면 높이·키보드 보정 (2026-09-06)

- 상대 프로필에서 `1:1 메시지` 탭을 선택하면 큰 프로필 헤더를 즉시 접어 대화 목록이 화면 높이를
  확보하도록 변경했다. 메시지 탭에서는 대화 스크롤로 프로필 헤더가 다시 열리지 않으며, 사진 탭으로
  돌아가면 프로필 헤더를 복원한다.
- `MainActivity`의 키보드 동작을 `adjustResize`로 명시해 Samsung 키보드가 화면을 임의로 위로 미는
  `adjustPan` 동작을 차단했다. 키보드가 열리면 루트 하단 탐색 바의 예약 공간을 해제하고, IME 인셋은 대화
  화면 전체가 아니라 입력 바에서 한 번만 소비한다. 따라서 대화 목록과 입력 바 사이에 키보드 높이만큼 빈
  공간이 생기지 않으면서 입력란과 개인정보 경고·글자 수는 키보드 바로 위에 남는다.
- 전체 단위 테스트·lint와 Debug/QA/Release APK, Release AAB 빌드를 통과했으며 Debug 앱을 Galaxy S25
  Edge에 덮어 설치하고 실행했다. GOAPI 계약이나 서버 변경은 없다.
