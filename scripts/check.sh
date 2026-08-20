#!/usr/bin/env bash
set -euo pipefail

# 커밋 전에 실행하는 기본 품질 게이트다.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

source "$SCRIPT_DIR/android-env.sh"
cd "$PROJECT_DIR"
./gradlew test lintDebug assembleDebug assembleRelease
