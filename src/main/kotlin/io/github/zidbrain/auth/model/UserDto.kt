package io.github.zidbrain.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val login: String,
    val role: Role,
    val name: String,
    val lastName: String
)

@Serializable
data class ExposedUserDto(
    val name: String,
    val lastName: String
)