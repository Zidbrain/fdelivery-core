package io.github.zidbrain.order.service

import io.github.zidbrain.order.model.OrderDto
import io.github.zidbrain.order.model.OrderStatusDto
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class ConnectionHandler {
    private val _connections = mutableMapOf<UUID, OrderStatusConnection>()
    val connections: Map<UUID, OrderStatusConnection> = _connections

    fun make(userID: UUID, session: DefaultWebSocketSession) {
        _connections[userID] = OrderStatusConnection(session)
    }

    fun delete(userID: UUID) {
        _connections.remove(userID)
    }
}

class OrderStatusConnection(private val session: DefaultWebSocketSession) {
    suspend fun notifyOrder(orderDto: OrderStatusDto) {
        if (!session.isActive)
            return
        val value = Json.encodeToString(orderDto)
        session.send(value)
    }

    suspend fun notifyDelivery(orderDto: OrderDto) {
        if (!session.isActive)
            return
        val value = Json.encodeToString(orderDto)
        session.send(value)
    }
}