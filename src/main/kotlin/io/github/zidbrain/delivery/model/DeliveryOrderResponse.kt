package io.github.zidbrain.delivery.model

import io.github.zidbrain.order.model.OrderDto
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryOrderResponse(
    val order: OrderDto?
)