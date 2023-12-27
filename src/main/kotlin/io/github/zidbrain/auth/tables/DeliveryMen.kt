package io.github.zidbrain.auth.tables

import io.github.zidbrain.auth.model.DeliveryManStatus
import io.github.zidbrain.order.tables.OrderDao
import io.github.zidbrain.order.tables.Orders
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object DeliveryMen : IdTable<UUID>("delivery_men") {
    override val id = uuid("id").entityId() references Users.id
    val status = enumeration<DeliveryManStatus>("status")
}

class DeliveryManDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<DeliveryManDao>(DeliveryMen)

    var status by DeliveryMen.status
    val user by UserDao referencedOn DeliveryMen.id
    val orders by OrderDao optionalReferrersOn Orders.assignedToId

    val currentlyAssignedOrder: OrderDao?
        get() = transaction {
            orders.find { !it.status.isComplete }
        }
}