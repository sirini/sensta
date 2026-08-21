# Sensta 2.0 운영 서버 배포

Sensta 2.0은 Android 앱과 NUBO Web, GOAPI가 같은 API v1 계약을 사용해야 완전하게 동작합니다. 앱 출시
전에 인증 토큰 회전, FCM 기기 등록, 신고·차단, 계정 삭제가 포함된 GOAPI를 `sensta.me` 운영 서버에
반영해야 합니다.

## 현재 릴리스 상태

2026-08-21 기준 최신 NUBO 릴리스 `v1.2.15`는 GOAPI `fc430b8`을 고정합니다. Android 계약 작업은 그
이후 GOAPI `42481c5`까지 이어졌으므로 `v1.2.15`만 설치한 서버에는 다음 기능이 모두 들어 있지 않습니다.

- `POST /auth/android/refresh` 리프레시 토큰 회전
- Firebase FID 등록·해제와 실시간 알림
- 채팅 입력 계약 보강
- 사용자 신고·차단과 계정 완전 삭제

따라서 다음 NUBO 패치 릴리스(예: `v1.2.16`)에서 GOAPI `42481c5` 이상을 고정해 통합 릴리스를 먼저
게시해야 합니다. GOAPI 바이너리를 운영 서버에서 직접 빌드하거나 단독 교체하지 않습니다.

## 통합 릴리스 게시

릴리스 작업은 NUBO와 GOAPI의 깨끗한 `main`에서 진행합니다.

1. GOAPI의 전체 테스트와 vet를 통과시키고 `main`을 푸시합니다.
2. NUBO `deploy/release-sources.json`의 GOAPI commit을 검증한 최신 commit으로 고정합니다.
3. NUBO의 `env.sample`, README, 배포 문서와 release source 버전을 같은 patch 버전으로 올립니다.
4. NUBO test, lint, typecheck, build와 양쪽 API contract 일치를 검증해 커밋·푸시합니다.
5. 같은 버전의 annotated tag를 만들고 푸시합니다.

```bash
git tag -a v1.2.16 -m "NUBO v1.2.16"
git push origin main
git push origin v1.2.16
```

태그 푸시는 `Publish Linux release` GitHub Actions를 실행합니다. `build-release`, Ubuntu 22.04/24.04
`fresh-install`, `publish`가 모두 성공하고 GitHub Release에 통합 archive와 SHA-256이 게시된 뒤에만
운영 서버 업데이트를 시작합니다. Release의 `manifest.json`에는 dirty가 아닌 NUBO와 GOAPI commit이
기록되어야 합니다.

## 운영 서버 업데이트

먼저 DB dump와 전체 upload 디렉터리를 서버 밖의 저장소에도 백업합니다. 업데이트는 additive DB
migration을 적용하며 readiness 실패 시 실행 파일과 프로세스는 이전 릴리스로 복구하지만 DB migration은
자동으로 되돌리지 않습니다.

운영 서버의 NUBO source checkout에서 다음 순서로 실행합니다. 실제 경로와 서비스 계정은 서버 구성에
맞춥니다.

```bash
cd /path/to/nubo
git pull --ff-only
sudo /opt/nubo/current/nuboctl status
npm run server:update -- --dry-run
npm run server:update
```

실행 중 외부 백업 완료 여부를 물으면 백업을 직접 확인한 뒤 빈 입력으로 진행합니다. `.env`, upload,
DB와 기존 Nginx/TLS를 임의로 덮어쓰지 않습니다.

Firebase 실시간 알림을 사용할 때는 서비스 계정 JSON을 웹 공개 경로와 Git 저장소 밖에 두고
`/etc/nubo/nubo.env`에 다음 값을 설정합니다. 파일은 GOAPI systemd 서비스 계정이 읽을 수 있어야 합니다.

```dotenv
FIREBASE_PROJECT_ID=실제-Firebase-project-id
FIREBASE_CREDENTIALS_FILE=/etc/nubo/firebase-service-account.json
```

설정 후 대표 서비스만 다시 시작합니다.

```bash
sudo systemctl restart nubo
```

## 배포 후 확인

```bash
sudo systemctl status nubo nubo-goapi nubo-web
sudo /opt/nubo/current/nuboctl status
sudo /opt/nubo/current/nuboctl doctor
sudo journalctl -u nubo-goapi -u nubo-web --since "10 minutes ago"
curl -fsS https://sensta.me/ready
curl -fsS https://sensta.me/version
```

`/version`의 release와 GOAPI commit이 방금 게시한 통합 릴리스와 일치해야 합니다. 이어서 별도 테스트
계정으로 로그인·토큰 갱신, 사진 업로드, 댓글·좋아요, 1:1 대화, 신고·차단·해제, 로그아웃, FCM 수신을
확인합니다. 계정 삭제는 전용 일회성 계정으로만 검증합니다.

서버 접근 정보, DB 백업 위치, Firebase 비밀값이 준비되기 전에는 실제 업데이트를 실행하지 않습니다.
