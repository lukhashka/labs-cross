package com.example.block_3.data.common

import com.example.block_3.core.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException

// Converts HTTP response to Result: 2xx -> Success(body), otherwise -> Failure.
suspend inline fun <reified T> HttpResponse.handleResponse(): Result<T> {
    return when (status.value) {
        in 200..299 -> {
            try {
                val result = if (T::class == Unit::class) Unit as T else body<T>()
                Result.Success(result)
            } catch (e: SerializationException) {
                Result.Failure("Serialization error: ${e.message}")
            }
        }
        else -> Result.Failure("Error ${status.value}: ${bodyAsText()}")
    }
}

// Wraps a Ktor call so that network exceptions never leave the data layer.
suspend inline fun <reified T> HttpClient.safeRequest(
    crossinline block: suspend HttpClient.() -> HttpResponse
): Result<T> {
    return try {
        block().handleResponse<T>()
    } catch (e: Exception) {
        Result.Failure("Network error: ${e.message}")
    }
}
