# Sensta 2.0 운영 서버 배포

Sensta 2.0은 Android 앱과 NUBO Web, GOAPI가 같은 API v1 계약을 사용해야 완전하게 동작합니다. 앱 출시
전에 인증 토큰 회전, FCM 기기 등록, 신고·차단, 계정 삭제가 포함된 GOAPI를 `sensta.me` 운영 서버에
반영해야 합니다.

## 현재 릴리스 상태

2026-08-21에 [NUBO v1.2.16](https://github.com/sirini/nubo/releases/tag/v1.2.16)이 정식 게시됐습니다.
NUBO `ade5ac7`과 GOAPI `42481c5`를 고정하며 다음 Sensta Android 계약을 모두 포함합니다.

- `POST /auth/android/refresh` 리프레시 토큰 회전
- Firebase FID 등록·해제와 실시간 알림
- 채팅 입력 계약 보강
- 사용자 신고·차단과 계정 완전 삭제

릴리스 workflow run `32437963579`에서 NUBO·GOAPI 게이트, Ubuntu 22.04/24.04 fresh-install과
게시를 모두 통과했고, 공개 archive를 다시 내려받아 SHA-256을 확인했습니다. manifest의
NUBO·nuboctl `dirty=true`는 CI workspace 안의 별도 GOAPI checkout을 오인한 기존 표기 오류입니다.
기록된 commit·checksum·fresh-install은 정상이며 NUBO `dc306ab`에서 다음 릴리스용 표기를
바로잡았습니다. 운영 서버에서 GOAPI를 따로 빌드·교체하지 말고 이 통합 릴리스를 적용합니다.

## 운영 서버 업데이트

먼저 DB dump와 전체 upload 디렉터리를 서버 밖의 저장소에도 백업합니다. 업데이트는 additive DB
migration을 적용하며 readiness 실패 시 실행 파일과 프로세스는 이전 릴리스로 복구하지만 DB migration은
자동으로 되돌리지 않습니다.

운영 서버의 NUBO source checkout에서 다음 순서로 실행합니다. 실제 경로와 서비스 계정은 서버 구성에
맞춥니다.

```bash
cd /path/to/nubo
git pull --ff-only
nuboctl status
nuboctl update --dry-run
nuboctl update
```

실행 중 외부 백업 완료 여부를 물으면 백업을 직접 확인한 뒤 빈 입력으로 진행합니다. `.env`, upload,
DB와 기존 Nginx/TLS를 임의로 덮어쓰지 않습니다.

현재 `sensta.me`는 systemd 설치 경로가 아니라 `/var/www/sensta.me`의 tmux 세션에서 GOAPI를 수동
운영합니다. 환경변수는 같은 디렉터리의 `.env`에 두고 다음처럼 실행합니다.

```bash
cd /var/www/sensta.me
NUBO_ENV_FILE="$PWD/.env" ./bin/goapi
```

이 구성에서는 `/etc/nubo/nubo.env`를 사용하지 않습니다. Firebase 서비스 계정 JSON은 웹 공개 경로와
Git 저장소 밖에 두고, 실제로 읽는 `/var/www/sensta.me/.env`에 다음 값을 설정합니다. Google Android
로그인의 ID token은 Web application OAuth client ID를 audience로 사용하므로 해당 값도 명시합니다.

```dotenv
FIREBASE_PROJECT_ID=실제-Firebase-project-id
FIREBASE_CREDENTIALS_FILE=/etc/nubo/firebase-service-account.json
OAUTH_GOOGLE_ANDROID_CLIENT_ID=Web-application-OAuth-client-id
```

`.env`는 프로세스 시작 시 읽으므로 값을 바꾼 뒤 tmux의 GOAPI 프로세스를 다시 시작합니다. 시작 후
실제 경로는 다음처럼 확인할 수 있습니다.

```bash
pid="$(pgrep -n -x goapi)"
readlink -f "/proc/$pid/cwd"
tr '\0' '\n' <"/proc/$pid/environ" | grep '^NUBO_ENV_FILE='
```

## 배포 후 확인

```bash
pgrep -a -x goapi
curl -fsS https://sensta.me/ready
curl -fsS https://sensta.me/version
```

systemd 통합 배포로 다시 전환한 경우에만 `systemctl status nubo nubo-goapi nubo-web`, `nuboctl status`,
`nuboctl doctor`와 journal을 추가로 확인합니다.

`/version`의 release와 GOAPI commit이 방금 게시한 통합 릴리스와 일치해야 합니다. 이어서 별도 테스트
계정으로 로그인·토큰 갱신, 사진 업로드, 댓글·좋아요, 1:1 대화, 신고·차단·해제, 로그아웃, FCM 수신을
확인합니다. 계정 삭제는 전용 일회성 계정으로만 검증합니다.

서버 접근 정보, DB 백업 위치, Firebase 비밀값이 준비되기 전에는 실제 업데이트를 실행하지 않습니다.
