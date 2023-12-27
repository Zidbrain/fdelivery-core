package io.github.zidbrain.order.tables

import io.github.zidbrain.items.tables.ItemDao
import io.github.zidbrain.items.tables.Items
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object OrderedItems : UUIDTable("ordered_items") {
    val orderId = reference("order_id", Orders.id)
    val itemId = optReference("item_id", Items.id)
    val orderedAmount = integer("ordered_amount")
}

class OrderedItemDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<OrderedItemDao>(OrderedItems)

    var order by OrderDao referencedOn OrderedItems.orderId
    var item by ItemDao optionalReferencedOn OrderedItems.itemId
    var orderedAmount by OrderedItems.orderedAmount
}