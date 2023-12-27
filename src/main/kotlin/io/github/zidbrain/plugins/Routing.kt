package io.github.zidbrain.plugins

import io.github.zidbrain.admin.administration
import io.github.zidbrain.auth.authorization
import io.github.zidbrain.auth.service.AuthService
import io.github.zidbrain.delivery.delivery
import io.github.zidbrain.delivery.service.DeliveryService
import io.github.zidbrain.items.itemsWithCategories
import io.github.zidbrain.items.service.ItemsService
import io.github.zidbrain.order.order
import io.github.zidbrain.order.service.ConnectionHandler
import io.github.zidbrain.order.service.OrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    authService: AuthService,
    itemsService: ItemsService,
    orderService: OrderService,
    deliveryService: DeliveryService,
    connectionHandler: ConnectionHandler
) {
    routing {
        authorization(authService)
        itemsWithCategories(itemsService)
        order(orderService, authService, connectionHandler)
        delivery(deliveryService, authService, orderService, connectionHandler)
        administration(itemsService)
        staticResources("/", "app") {
            cacheControl {
                listOf(CacheControl.NoCache(null))
            }
            extensions("html")
        }
        staticResources("/static", "static") {
            cacheControl {
                listOf(CacheControl.MaxAge(31536000))
            }
        }
    }
}
