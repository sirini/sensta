#!/usr/bin/env bash
set -euo pipefail

# sudo 없이 WSL 사용자 홈에 재현 가능한 Android 빌드 도구를 준비한다.
JDKS_DIR="${XDG_DATA_HOME:-$HOME/.local/share}/jdks"
JDK_LINK="$JDKS_DIR/temurin-17"
SDK_DIR="${ANDROID_HOME:-$HOME/Android/Sdk}"
TOOLS_DIR="$SDK_DIR/cmdline-tools/latest"
TEMP_DIR="$(mktemp -d)"

cleanup() {
  rm -rf -- "$TEMP_DIR"
}
trap cleanup EXIT

mkdir -p "$JDKS_DIR" "$SDK_DIR/cmdline-tools"

if [[ ! -x "$JDK_LINK/bin/java" ]]; then
  curl -fL --retry 3 \
    -o "$TEMP_DIR/jdk17.tar.gz" \
    "https://api.adoptium.net/v3/binary/latest/17/ga/linux/x64/jdk/hotspot/normal/eclipse"
  tar -xzf "$TEMP_DIR/jdk17.tar.gz" -C "$JDKS_DIR"
  JDK_DIR="$(find "$JDKS_DIR" -maxdepth 1 -type d -name 'jdk-17*' | sort -V | tail -1)"
  ln -sfn "$JDK_DIR" "$JDK_LINK"
fi

if [[ ! -x "$TOOLS_DIR/bin/android" ]]; then
  curl -fL --retry 3 \
    -o "$TEMP_DIR/command-line-tools.zip" \
    "https://dl.google.com/android/repository/commandlinetools-linux-16111833_latest.zip"
  mkdir -p "$TEMP_DIR/command-line-tools"
  (
    cd "$TEMP_DIR/command-line-tools"
    "$JDK_LINK/bin/jar" -xf "$TEMP_DIR/command-line-tools.zip"
  )
  mv "$TEMP_DIR/command-line-tools/cmdline-tools" "$TOOLS_DIR"
  chmod +x "$TOOLS_DIR/bin/"*
fi

JAVA_HOME="$JDK_LINK" ANDROID_HOME="$SDK_DIR" \
  "$TOOLS_DIR/bin/android" sdk install \
  "platforms/android-37.0" \
  "build-tools/37.0.0" \
  "platform-tools"

echo "Android 빌드 환경 준비를 마쳤습니다."
echo "다음 명령으로 현재 셸에 적용하세요: source scripts/android-env.sh"
