package io.github.zidbrain.items

import io.github.zidbrain.items.model.GetCategoriesResponse
import io.github.zidbrain.items.service.ItemsService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.math.BigDecimal

fun Routing.itemsWithCategories(itemsService: ItemsService) {
    get("/api/categories") {
        call.respond(
            GetCategoriesResponse(
                categories = itemsService.getCategories(),
                deliveryFee = BigDecimal(itemsService.getDeliveryFee())
            )
        )
    }
}