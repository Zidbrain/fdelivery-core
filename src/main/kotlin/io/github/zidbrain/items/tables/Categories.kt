package io.github.zidbrain.items.tables

import io.github.zidbrain.items.model.CategoryDto
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Categories : UUIDTable("categories", "id") {
    val name = varchar("name", 50)
}

class CategoryDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<CategoryDao>(Categories)

    var name by Categories.name
    val items by ItemDao referrersOn Items.categoryId

    fun toDto(): CategoryDto = CategoryDto(
        id = id.value.toString(),
        name = name,
        items = items.map { it.toDto() }
    )
}