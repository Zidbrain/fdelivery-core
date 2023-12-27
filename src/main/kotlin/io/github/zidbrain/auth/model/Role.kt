package io.github.zidbrain.auth.model

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    User,
    DeliveryMan,
    Admin
}