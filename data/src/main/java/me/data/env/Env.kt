package me.data.env

object Env {
    const val TITLE = "SENSTA"                      // 앱 타이틀
    const val DOMAIN = "https://sensta.me"          // TSBOARD로 개발된 웹사이트 주소
    const val BOARD_ID = "photo"                    // Board ID on TSBOARD
    const val BOARD_UID = 2                         // Board UID on TSBOARD
    const val CATEGORY_UID = 5                      // 기본 카테고리 고유 번호, 게시판 설정에서 카테고리 안쓸 경우 무시됨
    const val MAX_UPLOAD_COUNT = 9                  // 한 번에 9장까지 업로드 가능
    const val MAX_UPLOAD_SIZE = 100L * 1024 * 1024  // 한 번에 최대 100MB까지 업로드 가능

    const val MIN_TSBOARD_VER = "≥ v1.0.5"          // 이 앱은 운용중인 서버의 TSBOARD가 v1.0.5 이상이어야 동작합니다
    const val MIN_ANDROID_VER = "≥ 14"              // 안드로이드 14 (Samsung Galaxy S23 시리즈 이후) 이상 버전 필요
    const val GITHUB_URL = "https://github.com/sirini/sensta" // 이 앱의 전체 소스코드 GitHub 주소
}