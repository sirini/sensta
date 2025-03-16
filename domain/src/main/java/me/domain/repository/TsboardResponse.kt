package me.domain.repository

sealed class TsboardResponse<out T> {
    object Loading : TsboardResponse<Nothing>()
    data class Success<T>(val data: T) : TsboardResponse<T>()
    data class Error(val message: String) : TsboardResponse<Nothing>()
}