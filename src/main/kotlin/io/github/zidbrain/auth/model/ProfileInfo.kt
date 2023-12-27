package io.github.zidbrain.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileInfo(
    val name: String? = null,
    val lastName: String? = null,
    val password: String? = null
)