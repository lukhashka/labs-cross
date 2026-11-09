package ua.chnu.campustix.data.user

import kotlinx.serialization.Serializable
import ua.chnu.campustix.domain.user.User

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
)

fun UserResponse.toDomain(): User = User(
    id = id,
    username = username,
    fullName = "$firstName $lastName",
)
