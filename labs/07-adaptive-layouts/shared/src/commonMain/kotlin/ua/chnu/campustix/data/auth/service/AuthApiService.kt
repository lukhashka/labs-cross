package ua.chnu.campustix.data.auth.service

import ua.chnu.campustix.core.Result
import ua.chnu.campustix.data.auth.model.TokensResponse

const val BASE_URL = "https://dummyjson.com"
const val AUTH_API = "auth"
const val LOGIN = "login"
const val REFRESH = "refresh"

interface AuthApiService {
    suspend fun login(username: String, password: String): Result<TokensResponse>
    suspend fun refresh(refreshToken: String): Result<TokensResponse>
}
