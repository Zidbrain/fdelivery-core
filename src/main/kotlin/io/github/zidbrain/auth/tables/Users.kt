package io.github.zidbrain.auth.tables

import io.github.zidbrain.auth.model.ExposedUserDto
import io.github.zidbrain.auth.model.Role
import io.github.zidbrain.auth.model.UserDto
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Users : UUIDTable("users", "id") {
    val login = varchar("login", 25).uniqueIndex()
    val passwordHash = varchar("password_hash", 88)
    val salt = varchar("salt", 12)
    val role = enumeration<Role>("role")
    val name = varchar("name", 30)
    val lastName = varchar("last_name", 30)
}

class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(Users)

    var login by Users.login
    var passwordHash by Users.passwordHash
    var salt by Users.salt
    var role by Users.role
    var name by Users.name
    var lastName by Users.lastName

    val deliveryMan by DeliveryManDao backReferencedOn DeliveryMen.id

    fun toDto(): UserDto = UserDto(login, role, name, lastName)

    fun toExposedDto(): ExposedUserDto = ExposedUserDto(name = name, lastName = lastName)
}