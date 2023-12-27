package io.github.zidbrain.order.tables

import io.github.zidbrain.auth.tables.DeliveryManDao
import io.github.zidbrain.auth.tables.DeliveryMen
import io.github.zidbrain.auth.tables.UserDao
import io.github.zidbrain.auth.tables.Users
import io.github.zidbrain.order.model.ItemWithAmountDto
import io.github.zidbrain.order.model.OrderDto
import io.github.zidbrain.order.model.OrderStatus
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import java.util.*

object Orders : IdTable<UUID>("orders") {
    val creationDate = timestampWithTimeZone("creation_date")
    val status = enumeration<OrderStatus>("status")
    val assignedToId = optReference("assigned_to_id", DeliveryMen)
    val deliveryFee = decimal("delivery_fee", 10, 2)
    val createdBy = reference("created_by_id", Users)
    val totalCost = decimal("total_cost", 10, 2)
    override val id: Column<EntityID<UUID>> = reference("id", DeliveryAddresses)
}

class OrderDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<OrderDao>(Orders)

    var creationDate by Orders.creationDate
    var status by Orders.status
    var assignedTo by DeliveryManDao optionalReferencedOn Orders.assignedToId
    var deliveryFee by Orders.deliveryFee
    var createdBy by UserDao referencedOn Orders.createdBy
    var deliveryAddress by DeliveryAddressDao referencedOn Orders.id
    val orderedItems by OrderedItemDao referrersOn OrderedItems.orderId
    var totalCost by Orders.totalCost

    fun toDto(): OrderDto = OrderDto(
        id = id.value.toString(),
        deliveryAddress = deliveryAddress.toDto(),
        order = orderedItems.map {
            ItemWithAmountDto(
                item = it.item?.toDto(),
                amount = it.orderedAmount
            )
        },
        status = status,
        assignedTo = assignedTo?.user?.toExposedDto(),
        totalCost =  totalCost,
        deliveryFee = deliveryFee,
        createdAt = creationDate
    )
}