package me.domain.repository

// 화면까지 전달할 NUBO 요청 상태다. cause는 진단에만 쓰고 사용자에게 직접 노출하지 않는다.
sealed class NuboResponse<out T> {
    object Loading : NuboResponse<Nothing>()
    data class Success<T>(val data: T) : NuboResponse<T>()
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : NuboResponse<Nothing>()
}

// 성공·실패를 한곳에서 처리해 오류가 조용히 사라지지 않게 한다.
suspend fun <T> NuboResponse<T>.handle(
    onError: suspend (NuboResponse.Error) -> Unit = {},
    onSuccess: suspend (T) -> Unit
) {
    when (this) {
        is NuboResponse.Loading -> Unit
        is NuboResponse.Error -> onError(this)
        is NuboResponse.Success -> onSuccess(data)
    }
}
