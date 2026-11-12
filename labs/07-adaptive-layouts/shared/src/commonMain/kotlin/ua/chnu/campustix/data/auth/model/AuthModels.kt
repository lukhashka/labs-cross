package ua.chnu.campustix.data.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val expiresInMins: Int = TOKEN_LIFETIME_MINUTES,
)

@Serializable
data class RefreshRequest(
    val refreshToken: String,
    val expiresInMins: Int = TOKEN_LIFETIME_MINUTES,
)

@Serializable
data class TokensResponse(
    val accessToken: String,
    val refreshToken: String,
)

const val TOKEN_LIFETIME_MINUTES = 30
