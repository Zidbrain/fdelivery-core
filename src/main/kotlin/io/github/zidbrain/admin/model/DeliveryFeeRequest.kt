package io.github.zidbrain.admin.model

import io.github.zidbrain.items.model.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class DeliveryFeeRequest(
    @Serializable(BigDecimalSerializer::class)
    val deliveryFee: BigDecimal
)