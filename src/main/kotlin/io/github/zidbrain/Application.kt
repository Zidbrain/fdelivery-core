package io.github.zidbrain

import io.github.zidbrain.auth.service.AuthService
import io.github.zidbrain.delivery.service.DeliveryService
import io.github.zidbrain.items.service.ItemsService
import io.github.zidbrain.order.service.ConnectionHandler
import io.github.zidbrain.order.service.OrderService
import io.github.zidbrain.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val authService = AuthService(Databases.user, Databases.admin)
    val itemsService = ItemsService(Databases.user, Databases.admin)
    val connectionHandler = ConnectionHandler()
    val deliveryService = DeliveryService(Databases.admin, connectionHandler)
    val orderService = OrderService(deliveryService, Databases.admin)

    configureSockets()
    configureCors()
    configureSerialization()
    configureMonitoring()
    configureSecurity(authService)
    configureRouting(authService, itemsService, orderService, deliveryService, connectionHandler)
}
