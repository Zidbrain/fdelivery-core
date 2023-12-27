package io.github.zidbrain.order.service

import io.github.zidbrain.admin.tables.SettingDao
import io.github.zidbrain.auth.tables.DeliveryManDao
import io.github.zidbrain.auth.tables.UserDao
import io.github.zidbrain.delivery.service.DeliveryService
import io.github.zidbrain.items.tables.ItemDao
import io.github.zidbrain.order.model.DeliveryAddressDto
import io.github.zidbrain.order.model.OrderDto
import io.github.zidbrain.order.model.OrderStatus
import io.github.zidbrain.order.tables.DeliveryAddressDao
import io.github.zidbrain.order.tables.OrderDao
import io.github.zidbrain.order.tables.OrderedItemDao
import io.github.zidbrain.order.tables.Orders
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

class OrderService(private val deliveryService: DeliveryService, private val adminDatabase: Database) {

    suspend fun createOrder(order: Map<UUID, Int>, userId: UUID, deliveryAddress: DeliveryAddressDto): UUID {
        val orderDao = transaction(adminDatabase) {
            val deliveryFee = SettingDao["deliveryFee"].value!!
            val user = UserDao.findById(userId)!!
            val id = UUID.randomUUID()
            val totalCost = ItemDao.forIds(order.keys.toList()).zip(order.values).sumOf { (item, amount) ->
                item.price * BigDecimal(amount)
            }
            val orderDao = OrderDao.new(id) {
                creationDate = OffsetDateTime.now()
                status = OrderStatus.Created
                assignedTo = null
                this.deliveryFee = BigDecimal(deliveryFee)
                createdBy = user
                this.totalCost = totalCost
                this.deliveryAddress = DeliveryAddressDao.new(id) {
                    fromDto(deliveryAddress)
                }
            }

           order.forEach { (id, amount) ->
                OrderedItemDao.new {
                    this.item = ItemDao.findById(id)!!
                    this.order = orderDao
                    this.orderedAmount = amount
                }
            }

            return@transaction orderDao
        }
        deliveryService.tryAssignOrder(orderDao)
        return orderDao.id.value
    }

    fun getOrders(userId: UUID): List<OrderDto> = transaction(adminDatabase) {
        return@transaction OrderDao.find { Orders.createdBy eq userId }.orderBy(Orders.creationDate to SortOrder.DESC)
            .map {
                getOrder(it)
            }
    }

    fun getDeliveryManOrder(deliveryManId: UUID): OrderDto? {
        val order = transaction(adminDatabase) {
            DeliveryManDao.findById(deliveryManId)!!.currentlyAssignedOrder
        }
        return order?.id?.let {
            getOrder(it.value)
        }
    }

    private fun getOrder(orderDao: OrderDao): OrderDto = orderDao.toDto()

    fun getOrder(orderID: UUID) = transaction(adminDatabase) {
        val orderDao = OrderDao.findById(orderID)
        return@transaction orderDao?.let { getOrder(it) }
    }
}