package com.softcode.mymagicapp.core.domain.results

sealed interface OperationResult<out T, out F, out E> {
    data class Success<out T>(val data: T) : OperationResult<T, Nothing, Nothing>
    data class Failure<out F>(val reason: F) : OperationResult<Nothing, F, Nothing>
    data class Error<out E>(val error: E) : OperationResult<Nothing, Nothing, E>
}