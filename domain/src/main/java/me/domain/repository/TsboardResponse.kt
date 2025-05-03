package me.domain.repository

// 서버로 받은 응답 상태 정의
sealed class TsboardResponse<out T> {
    object Loading : TsboardResponse<Nothing>()
    data class Success<T>(val data: T) : TsboardResponse<T>()
    data class Error(val message: String) : TsboardResponse<Nothing>()
}

// 서버로부터 받은 응답을 상태에 따라 처리하는 확장 함수
suspend fun <T> TsboardResponse<T>.handle(onSuccess: suspend (T) -> Unit) {
    when (this) {
        is TsboardResponse.Loading -> {}
        is TsboardResponse.Error -> {}
        is TsboardResponse.Success -> {
            onSuccess(this.data)
        }
    }
}