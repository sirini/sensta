#!/usr/bin/env bash
set -euo pipefail

# 커밋 전에 실행하는 기본 품질 게이트다.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

source "$SCRIPT_DIR/android-env.sh"
cd "$PROJECT_DIR"
# Hilt 생성 소스를 린트와 릴리스 컴파일이 동시에 갱신하면 AGP 린트가 간헐적으로 실패하므로 단계를 분리한다.
./gradlew test
./gradlew lintDebug
./gradlew assembleDebug assembleRelease bundleRelease
