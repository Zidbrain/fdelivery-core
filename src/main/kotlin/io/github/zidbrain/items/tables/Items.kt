package io.github.zidbrain.items.tables

import io.github.zidbrain.items.model.ItemDto
import io.github.zidbrain.order.tables.OrderedItemDao
import io.github.zidbrain.order.tables.OrderedItems
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Items : UUIDTable("items", "id") {
    val name = varchar("name", 50)
    val categoryId = reference("category_id", Categories)
    val price = decimal("price", 10, 2)
    val imagePath = varchar("image_path", 50)
}

class ItemDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ItemDao>(Items)

    var name by Items.name
    var category by CategoryDao referencedOn Items.categoryId
    var price by Items.price
    var imagePath by Items.imagePath
    val orderedItems by OrderedItemDao optionalReferrersOn OrderedItems.itemId

    fun toDto(): ItemDto = ItemDto(
        id = id.value.toString(),
        name = name,
        price = price,
        imagePath = imagePath
    )
}