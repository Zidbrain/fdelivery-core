package io.github.zidbrain.delivery.model

import io.github.zidbrain.auth.model.DeliveryManStatus
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryManStatusDto(
    val status: DeliveryManStatus
)

@Serializable
data class DeliveryManSetStatusRequest(
    val isReady: Boolean
)