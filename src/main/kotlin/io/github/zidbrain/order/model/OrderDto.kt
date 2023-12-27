package io.github.zidbrain.order.model

import io.github.zidbrain.auth.model.ExposedUserDto
import io.github.zidbrain.items.model.BigDecimalSerializer
import io.github.zidbrain.items.model.ItemDto
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.OffsetDateTime

@Serializable
data class OrderDto(
    val id: String,
    val deliveryAddress: DeliveryAddressDto,
    val order: List<ItemWithAmountDto>,
    val status: OrderStatus,
    val assignedTo: ExposedUserDto?,
    @Serializable(with = BigDecimalSerializer::class)
    val totalCost: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val deliveryFee: BigDecimal,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime
)

@Serializable
data class OrderRequestDto(
    val deliveryAddress: DeliveryAddressDto,
    val order: List<OrderItemDto>
)

@Serializable
data class OrderItemDto(
    val id: String,
    val amount: Int
)

@Serializable
data class ItemWithAmountDto(
    val item: ItemDto?,
    val amount: Int
)