package com.example.block_3.core

// Result of any operation: either data or an error message.
// `out T` makes Failure (Result<Nothing>) a subtype of any Result<T>.
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val errorMessage: String) : Result<Nothing>()
}

// Transforms the data of Success, Failure is passed through unchanged.
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Failure -> this
    }
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

inline fun <T> Result<T>.onFailure(action: (String) -> Unit): Result<T> {
    if (this is Result.Failure) {
        action(errorMessage)
    }
    return this
}
