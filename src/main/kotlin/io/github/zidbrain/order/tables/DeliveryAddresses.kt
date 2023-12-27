package io.github.zidbrain.order.tables

import io.github.zidbrain.order.model.DeliveryAddressDto
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object DeliveryAddresses : UUIDTable("delivery_addresses") {
    val destination = varchar("destination", 50)
    val apartment = integer("apartment")
    val entrance = integer("entrance")
    val floor = integer("floor")
}

class DeliveryAddressDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<DeliveryAddressDao>(DeliveryAddresses)

    val order by OrderDao backReferencedOn Orders.id
    var destination by DeliveryAddresses.destination
    var apartment by DeliveryAddresses.apartment
    var entrance by DeliveryAddresses.entrance
    var floor by DeliveryAddresses.floor

    fun fromDto(deliveryAddressDto: DeliveryAddressDto) {
        destination = deliveryAddressDto.destination
        apartment = deliveryAddressDto.apartment
        entrance = deliveryAddressDto.entrance
        floor = deliveryAddressDto.floor
    }

    fun toDto(): DeliveryAddressDto = DeliveryAddressDto(
        destination = this.destination,
        apartment = this.apartment,
        entrance = this.entrance,
        floor = this.floor
    )
}