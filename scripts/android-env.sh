#!/usr/bin/env bash

# 프로젝트에서 검증한 사용자 영역 JDK와 Android SDK를 현재 셸에 연결한다.
export JAVA_HOME="${JAVA_HOME:-$HOME/.local/share/jdks/temurin-17}"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
