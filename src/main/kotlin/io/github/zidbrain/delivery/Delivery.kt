package io.github.zidbrain.delivery

import io.github.zidbrain.auth.model.DeliveryManStatus
import io.github.zidbrain.auth.model.Role
import io.github.zidbrain.auth.service.AuthService
import io.github.zidbrain.delivery.model.DeliveryManSetStatusRequest
import io.github.zidbrain.delivery.model.DeliveryManStatusDto
import io.github.zidbrain.delivery.model.DeliveryOrderResponse
import io.github.zidbrain.delivery.service.DeliveryService
import io.github.zidbrain.order.service.ConnectionHandler
import io.github.zidbrain.order.service.OrderService
import io.github.zidbrain.plugins.authenticate
import io.github.zidbrain.plugins.getUserIdFromJWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.awaitCancellation

fun Routing.delivery(
    service: DeliveryService,
    authService: AuthService,
    orderService: OrderService,
    handler: ConnectionHandler
) {
    authenticate(Role.DeliveryMan) {
        get("/api/delivery/order") {
            val userID = getUserIdFromJWT()
            val order = orderService.getDeliveryManOrder(userID)
            call.respond(
                DeliveryOrderResponse(
                    order = order
                )
            )
        }
        post("/api/delivery/escalate-status") {
            val userID = getUserIdFromJWT()
            service.escalateOrderStatus(userID)
            call.response.status(HttpStatusCode.OK)
        }
        post("/api/delivery/status") { request: DeliveryManSetStatusRequest ->
            val userID = getUserIdFromJWT()
            service.setStatus(
                userID,
                if (request.isReady) DeliveryManStatus.Available else DeliveryManStatus.NotAvailable
            )
            call.response.status(HttpStatusCode.OK)
        }
        get("/api/delivery/status") {
            val userId = getUserIdFromJWT()
            call.respond(
                DeliveryManStatusDto(
                    status = service.getStatus(userId)
                )
            )
        }
    }

    webSocket("/api/delivery/deliveryStatus") {
        val userID = authenticate(authService, Role.DeliveryMan)

        try {
            handler.make(userID, this)
            awaitCancellation()
        } finally {
            handler.delete(userID)
        }
    }
}