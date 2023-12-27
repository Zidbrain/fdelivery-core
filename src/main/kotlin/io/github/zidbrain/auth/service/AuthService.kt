package io.github.zidbrain.auth.service

import io.github.zidbrain.auth.model.ProfileInfo
import io.github.zidbrain.auth.model.RegisterRequestDto
import io.github.zidbrain.auth.model.Role
import io.github.zidbrain.auth.tables.UserDao
import io.github.zidbrain.auth.tables.Users
import io.ktor.util.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random

class AuthService(private val userDatabase: Database, private val adminDatabase: Database) {

    private val digest = MessageDigest.getInstance("SHA-512")
    private fun encrypt(password: String, salt: String): ByteArray = digest.digest(("$password-$salt").toByteArray())

    fun getRole(userId: UUID) = transaction(userDatabase) {
        UserDao[userId].role
    }

    fun checkPassword(login: String, password: String): UserDao? = transaction(userDatabase) {
        val user = UserDao.find { Users.login eq login }.limit(1).firstOrNull() ?: return@transaction null
        val digestedPassword = encrypt(password, user.salt)
        return@transaction if (digestedPassword.contentEquals(user.passwordHash.decodeBase64Bytes())) user else null
    }

    fun createUser(request: RegisterRequestDto) = transaction(adminDatabase) {
        if (UserDao.find { Users.login eq request.login }.firstOrNull() != null) return@transaction null

        val salt = Random.Default.nextBytes(8).encodeBase64()
        val hash = encrypt(request.password, salt).encodeBase64()
        return@transaction UserDao.new(UUID.randomUUID()) {
            login = request.login
            passwordHash = hash
            role = Role.User
            this.salt = salt
            name = request.name
            lastName = request.lastName
        }.toDto()
    }

    fun setProfileInfo(profileInfo: ProfileInfo, userId: UUID) = transaction(adminDatabase) {
        val user = UserDao.findById(userId)!!
        user.apply {
            if (profileInfo.name != null) name = profileInfo.name
            if (profileInfo.lastName != null) lastName = profileInfo.lastName
            if (profileInfo.password != null) {
                val salt = Random.Default.nextBytes(8).encodeBase64()
                val hash = encrypt(profileInfo.password, salt).encodeBase64()
                passwordHash = hash
                this.salt = salt
            }
        }
    }
}