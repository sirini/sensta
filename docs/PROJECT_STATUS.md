# Sensta 2.0 프로젝트 상태

## 현재 목표

- 실제 기기 통합 테스트와 스토어 자산을 준비하고 Sensta 2.0을 Google Play에 재출시한다.

## 결정

- 2.0 개발은 `feat/sensta-2.0` 브랜치에서 기능 단위로 검증·커밋·푸시한다.
- 빌드 기반은 JDK 17, Gradle 9.5, AGP 9.3, Kotlin 2.4.10, compile SDK 37과 target SDK 36으로 고정한다.
- 최소 지원 버전은 Android 8(API 26)로 낮춘다.
- PC 장애에 대비해 검증된 기능 단위마다 작게 커밋하고 즉시 GitHub 원격 브랜치에 푸시한다.
- 실제 기기용 debug 앱은 `me.sensta.debug`로 설치해 Play 앱 `me.sensta`와 데이터·서명을 분리한다.
- 500px·Unsplash를 참고해 사진 중심 정보 구조와 시각 밀도로 홈 피드와 사진가 프로필을 개편한다.
- `feat/sensta-2.0`을 `main`에 병합할 때 NUBO·GOAPI README 수준으로 Sensta README의 구조·설치·설정·테스트·배포 문서를 전면 개편한다.
- Kotlin annotation processing은 KAPT 대신 KSP를 사용한다.
- 최신 Nubo와 GOAPI의 API contract v1을 Android 네트워크 계층의 기준으로 삼는다.
- 새로 작성하거나 의미를 바로잡는 코드 주석은 한국어로 작성한다.
- 모든 기능 변경과 출시 검증 후 기능 동일성을 유지하는 별도 최종 리팩터링을 수행한다.

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

## 검증

- `./scripts/check.sh`(`test`, `lintDebug`, `assembleDebug`, `assembleRelease`, `bundleRelease`) 성공.
- debug APK의 패키지 `me.sensta.debug`, 버전 `2.0.0-debug`, compile SDK 37과 target SDK 36을 확인했다.
- `:data:testDebugUnitTest` 인증 계약 테스트 성공.
- 라이브 `sensta.me/goapi/board/list`에서 사진 게시판 1페이지 32개 응답과 게시판 UID 2를 확인했다.
- 라이브 게시글 7522의 이미지 2장·EXIF 응답과 게시글 7520의 댓글 응답을 확인했다.
- Galaxy S25 Edge debug 앱과 `sensta.me` 웹에서 Google 로그인을 각각 확인했다.
- 디자인·아이콘·README 반영 후 `./scripts/check.sh`의 단위 테스트, Lint, debug·release APK와 release App Bundle 빌드가 모두 성공했다.

## 다음 작업

- Windows 빌드 환경에 기존 Play 업로드 키를 연결하고 서명된 release AAB의 인증서를 확인한다.
- Galaxy S25 Edge와 에뮬레이터에서 업로드·알림·딥 링크·안전 기능·접근성을 통합 검증한다.
- Play Console의 기존 최신 `versionCode`와 업로드 인증서를 확인한다.
- 스토어 기능 그래픽·스크린샷·설명문과 Data safety·앱 액세스·UGC 정책 응답을 준비한다.
- 모든 기능이 확정된 뒤 기능 동일성을 유지하는 최종 구조 리팩터링을 수행한다.
- release 후보를 내부 테스트에 게시한 뒤 `feat/sensta-2.0`을 `main`에 병합한다.
