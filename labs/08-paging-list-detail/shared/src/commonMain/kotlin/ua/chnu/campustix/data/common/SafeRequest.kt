package ua.chnu.campustix.data.common

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import ua.chnu.campustix.core.DataError
import ua.chnu.campustix.core.Result

suspend inline fun <reified T> HttpResponse.handleResponse(): Result<T> {
    return when (status.value) {
        in 200..299 -> {
            try {
                val result = if (T::class == Unit::class) Unit as T else body<T>()
                Result.Success(result)
            } catch (e: ContentConvertException) {
                Result.Failure(DataError.Serialization)
            } catch (e: SerializationException) {
                Result.Failure(DataError.Serialization)
            }
        }
        // dummyjson answers 400 to wrong credentials and 403 to a bad refresh token
        400, 401, 403 -> Result.Failure(DataError.Unauthorized)
        in 500..599 -> Result.Failure(DataError.Server)
        else -> Result.Failure(DataError.Unknown)
    }
}

suspend inline fun <reified T> HttpClient.safeRequest(
    crossinline block: suspend HttpClient.() -> HttpResponse
): Result<T> {
    return try {
        block().handleResponse<T>()
    } catch (e: CancellationException) {
        // cancellation must reach the coroutine, it is not a network error
        throw e
    } catch (e: Throwable) {
        // Throwable, not Exception: in the browser Ktor reports a failed fetch as kotlin.Error
        Result.Failure(DataError.NoInternet)
    }
}
