package io.github.zidbrain.order.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus {
    Created,
    Assembling,
    Delivering,
    Delivered,
    Canceled;

    val isComplete get() = this == Delivered || this == Canceled
}