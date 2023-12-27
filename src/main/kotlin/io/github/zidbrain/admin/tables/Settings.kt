package io.github.zidbrain.admin.tables

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object Settings : IdTable<String>("settings") {
    override val id: Column<EntityID<String>> = varchar("key", 50).entityId()
    val value = varchar("value", 50).nullable()
}

class SettingDao(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, SettingDao>(Settings)

    var value by Settings.value
}