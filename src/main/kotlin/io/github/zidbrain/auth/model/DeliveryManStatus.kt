package io.github.zidbrain.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DeliveryManStatus {
    @SerialName("notAvailable")
    NotAvailable,
    @SerialName("busy")
    Busy,
    @SerialName("available")
    Available
}