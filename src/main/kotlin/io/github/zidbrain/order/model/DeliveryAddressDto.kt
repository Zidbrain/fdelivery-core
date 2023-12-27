package io.github.zidbrain.order.model

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryAddressDto(
    val destination: String,
    val apartment: Int,
    val entrance: Int,
    val floor: Int
)