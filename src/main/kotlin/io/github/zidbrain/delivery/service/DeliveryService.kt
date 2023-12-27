package io.github.zidbrain.delivery.service

import io.github.zidbrain.auth.model.DeliveryManStatus
import io.github.zidbrain.auth.model.ExposedUserDto
import io.github.zidbrain.auth.tables.DeliveryManDao
import io.github.zidbrain.auth.tables.DeliveryMen
import io.github.zidbrain.order.model.OrderDto
import io.github.zidbrain.order.model.OrderStatus
import io.github.zidbrain.order.model.OrderStatusDto
import io.github.zidbrain.order.service.ConnectionHandler
import io.github.zidbrain.order.tables.OrderDao
import io.github.zidbrain.order.tables.Orders
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class DeliveryService(
    private val adminDatabase: Database,
    private val connectionHandler: ConnectionHandler
) {

    private suspend fun assignOrder(orderId: UUID, deliveryManId: UUID) {
        val notif = transaction(adminDatabase) {
            val order = OrderDao.findById(orderId)!!
            val deliveryMan = DeliveryManDao.findById(deliveryManId)!!
            order.assignedTo = deliveryMan
            order.status = OrderStatus.Created
            deliveryMan.status = DeliveryManStatus.Busy
            return@transaction order
        }
        notifyUser(notif)
    }

    private suspend fun notifyUser(order: OrderDao) {
        val (id, assignedToId, orderDto, orderStatus) = transaction(adminDatabase) {
            listOf(
                order.createdBy.id.value, order.assignedTo?.id?.value, order.toDto(), OrderStatusDto(
                    orderId = order.id.value.toString(),
                    status = order.status,
                    assignedTo = ExposedUserDto(
                        name = order.assignedTo!!.user.name,
                        lastName = order.assignedTo!!.user.lastName
                    )
                )
            )
        }
        connectionHandler.connections[id]?.notifyOrder(orderStatus as OrderStatusDto)
        assignedToId?.let {
            connectionHandler.connections[it]?.notifyDelivery(orderDto as OrderDto)
        }
    }

    suspend fun escalateOrderStatus(deliveryManId: UUID) {
        val order = transaction(adminDatabase) {
            val deliveryMan = DeliveryManDao.findById(deliveryManId)!!
            val order = deliveryMan.currentlyAssignedOrder!!
            val next = when (order.status) {
                OrderStatus.Created -> OrderStatus.Assembling
                OrderStatus.Assembling -> OrderStatus.Delivering
                OrderStatus.Delivering -> {
                    deliveryMan.status = DeliveryManStatus.NotAvailable
                    OrderStatus.Delivered
                }

                OrderStatus.Delivered -> throw IllegalStateException()
                OrderStatus.Canceled -> throw IllegalStateException()
            }
            order.status = next
            return@transaction order
        }
        notifyUser(order)
    }

    fun getStatus(deliveryManId: UUID) = transaction(adminDatabase) {
        val deliveryMan = DeliveryManDao.findById(deliveryManId)!!
        return@transaction deliveryMan.status
    }

    suspend fun setStatus(deliverManId: UUID, status: DeliveryManStatus) {
        val (order, deliveryMan) = transaction(adminDatabase) {
            val deliveryMan = DeliveryManDao.findById(deliverManId)!!
            deliveryMan.status = status
            val order = if (status == DeliveryManStatus.Available)
                if (deliveryMan.currentlyAssignedOrder != null) deliveryMan.currentlyAssignedOrder else OrderDao.find { Orders.status eq OrderStatus.Created }
                    .orderBy(Orders.creationDate to SortOrder.ASC)
                    .firstOrNull()
            else null
            return@transaction order to deliveryMan
        }
        order?.let {
            assignOrder(it.id.value, deliveryMan.id.value)
        }
    }

    suspend fun tryAssignOrder(order: OrderDao) {
        transaction(adminDatabase) {
            DeliveryManDao.find { DeliveryMen.status eq DeliveryManStatus.Available }.firstOrNull()
        }?.let {
            assignOrder(order.id.value, it.id.value)
        }
    }
}