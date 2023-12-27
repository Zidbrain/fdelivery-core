package io.github.zidbrain.order

import io.github.zidbrain.auth.model.Role
import io.github.zidbrain.auth.service.AuthService
import io.github.zidbrain.order.model.OrderRequestDto
import io.github.zidbrain.order.model.OrderResponseDto
import io.github.zidbrain.order.service.ConnectionHandler
import io.github.zidbrain.order.service.OrderService
import io.github.zidbrain.plugins.authenticate
import io.github.zidbrain.plugins.authenticateAnyRole
import io.github.zidbrain.plugins.getUserIdFromJWT
import io.github.zidbrain.util.toUUID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.awaitCancellation

fun Routing.order(orderService: OrderService, authService: AuthService, handler: ConnectionHandler) {
    authenticate(Role.User) {
        post("/api/orders") { request: OrderRequestDto ->
            val userId = call.principal<JWTPrincipal>()!!.subject!!
            val id = orderService.createOrder(
                order = request.order.associate { it.id.toUUID() to it.amount },
                userId = userId.toUUID(),
                deliveryAddress = request.deliveryAddress
            )
            call.respond(HttpStatusCode.Created, OrderResponseDto(id = id.toString()))
        }
        get("/api/orders/{orderId}") {
            val orderId = call.parameters["orderId"]!!.toUUID()
            orderService.getOrder(orderId)?.let {
                call.respond(it)
            } ?: call.response.status(HttpStatusCode.NotFound)
        }
    }
    authenticateAnyRole {
        get("/api/orders") {
            val userId = getUserIdFromJWT()
            call.respond(orderService.getOrders(userId))
        }
    }

    webSocket("/api/orderStatus") {
        val userID = authenticate(authService, Role.User)

        try {
            handler.make(userID, this)
            awaitCancellation()
        } finally {
            handler.delete(userID)
        }
    }
}