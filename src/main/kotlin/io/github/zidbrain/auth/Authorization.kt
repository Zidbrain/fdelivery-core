package io.github.zidbrain.auth

import io.github.zidbrain.auth.model.LoginRequestDto
import io.github.zidbrain.auth.model.LoginResponseDto
import io.github.zidbrain.auth.model.ProfileInfo
import io.github.zidbrain.auth.model.RegisterRequestDto
import io.github.zidbrain.auth.service.AuthService
import io.github.zidbrain.plugins.authenticateAnyRole
import io.github.zidbrain.plugins.createJwtFor
import io.github.zidbrain.plugins.getUserIdFromJWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.authorization(service: AuthService) {
    post("/api/auth/login") { request: LoginRequestDto ->
        val user = service.checkPassword(request.login, request.password)
        if (user == null) {
            call.response.status(HttpStatusCode.Unauthorized)
            return@post
        }

        val jwt = createJwtFor(user.id.value)
        call.respond(LoginResponseDto(jwt, user.role, user.name, user.lastName))
    }
    post("/api/auth/register") { request: RegisterRequestDto ->
        service.createUser(request)?.let {
            call.respond(HttpStatusCode.Created, it)
        } ?: call.response.status(HttpStatusCode.Conflict)
    }
    authenticateAnyRole {
        put("/api/auth/profileInfo") { request: ProfileInfo ->
            val userId = getUserIdFromJWT()
            service.setProfileInfo(request, userId)
            call.response.status(HttpStatusCode.OK)
        }
    }
}