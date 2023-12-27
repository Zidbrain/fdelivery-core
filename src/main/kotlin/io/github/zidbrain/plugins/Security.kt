package io.github.zidbrain.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.zidbrain.auth.model.Role
import io.github.zidbrain.auth.service.AuthService
import io.github.zidbrain.util.toUUID
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import java.util.*
import javax.naming.AuthenticationException

// Please read the jwt property from the config file if you are using EngineMain
private const val jwtAudience = "https://zidbrain.io.github"
private const val jwtDomain = "https://zidbrain.io.github"
private const val jwtRealm = "F-delivery"
private const val jwtSecret = "secret"

fun Application.configureSecurity(service: AuthService) {
    authentication {
        jwtForAnyRole()
        Role.entries.forEach {
            jwtForRole(service, it)
        }
    }
}

fun Routing.authenticate(role: Role, build: Route.() -> Unit) = authenticate(role.name, build = build)
fun Routing.authenticateAnyRole(build: Route.() -> Unit) = authenticate("forAny", build = build)

fun PipelineContext<Unit, ApplicationCall>.getUserIdFromJWT(): UUID =
    call.principal<JWTPrincipal>()!!.subject!!.toUUID()

fun createJwtFor(userId: UUID): String =
    JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withSubject(userId.toString())
        .sign(Algorithm.HMAC512(jwtSecret))

val jwtVerifier = JWT
    .require(Algorithm.HMAC512(jwtSecret))
    .withAudience(jwtAudience)
    .withIssuer(jwtDomain)
    .build()

suspend fun DefaultWebSocketServerSession.authenticate(service: AuthService, role: Role): UUID {
    val jwt = receiveDeserialized<String>()
    val token = jwtVerifier.verify(jwt)
    val id = token.subject!!.toUUID()
    if (service.getRole(id) != role) throw AuthenticationException()
    return token.subject.toUUID()
}

private fun AuthenticationConfig.jwtForAnyRole() = jwt("forAny") {
    realm = jwtRealm
    verifier(jwtVerifier)
    validate { JWTPrincipal(it.payload) }
}

private fun AuthenticationConfig.jwtForRole(service: AuthService, role: Role) = jwt(role.name) {
    realm = jwtRealm
    verifier(jwtVerifier)
    validate { credential ->
        if (service.getRole(credential.subject!!.toUUID()) == role) JWTPrincipal(credential.payload) else null
    }
}
