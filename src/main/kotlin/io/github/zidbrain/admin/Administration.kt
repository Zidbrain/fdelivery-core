package io.github.zidbrain.admin

import io.github.zidbrain.admin.model.DeliveryFeeRequest
import io.github.zidbrain.admin.model.UploadImageResponse
import io.github.zidbrain.auth.model.Role
import io.github.zidbrain.items.model.CategoryDto
import io.github.zidbrain.items.service.ItemsService
import io.github.zidbrain.plugins.authenticate
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Routing.administration(itemsService: ItemsService) = authenticate(Role.Admin) {
    post("/api/admin/upload-image") {
        val form = call.receiveMultipart()
        val parts = form.readAllParts()
        try {
            parts.forEach {
                if (it.name == "image" && it.contentType!!.match(ContentType.Image.Any) && it is PartData.FileItem) {
                    val name = UUID.randomUUID().toString() + "." + it.originalFileName!!.substringAfterLast('.')
                    val path = System.getenv("STATIC_PATH")
                    val file = File("$path/$name")
                    it.streamProvider().use { stream ->
                        file.outputStream().buffered().use { output ->
                            stream.copyTo(output)
                        }
                    }
                    call.respond(HttpStatusCode.Created, UploadImageResponse("/static/$name"))
                    return@post
                }
            }
        } catch (ex: Exception) {
            println(ex)
            call.response.status(HttpStatusCode.BadRequest)
        } finally {
            parts.forEach { it.dispose() }
        }
    }
    put("/api/categories") { request: List<CategoryDto> ->
        itemsService.updateCategories(request)
        call.response.status(HttpStatusCode.OK)
    }
    put("/api/deliveryFee") { request: DeliveryFeeRequest ->
        itemsService.setDeliveryFee(request.deliveryFee)
        call.respond(request)
    }
}