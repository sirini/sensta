package me.data.env

object Env {
    const val TITLE = "SENSTA"                      // 앱 타이틀
    const val DOMAIN = "https://sensta.me"          // NUBO로 운영하는 웹사이트 주소
    const val BOARD_ID = "photo"                    // NUBO 게시판 ID
    const val BOARD_UID = 2                         // NUBO 게시판 고유 번호
    const val CATEGORY_UID = 5                      // 기본 카테고리 고유 번호, 게시판 설정에서 카테고리 안쓸 경우 무시됨
    const val MAX_UPLOAD_COUNT = 9                  // 한 번에 9장까지 업로드 가능
    const val MAX_UPLOAD_SIZE = 100L * 1024 * 1024  // 한 번에 최대 100MB까지 업로드 가능

    const val API_CONTRACT_VERSION = "NUBO API v1" // 앱과 서버가 공유하는 공개 API 계약
    const val MIN_ANDROID_VER = "Android 8 이상"   // 최소 지원 버전은 API 26이다
    const val GITHUB_URL = "https://github.com/sirini/sensta" // 이 앱의 전체 소스코드 GitHub 주소
}
