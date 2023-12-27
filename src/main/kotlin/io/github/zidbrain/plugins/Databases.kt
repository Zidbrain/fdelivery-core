package io.github.zidbrain.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

object Databases {

    val user: Database
        get() = Database.connect(connect("user", ""))
    val admin: Database
        get() = Database.connect(connect("admin", "123"))

    private fun connect(user: String, password: String): DataSource {
        val host = System.getenv("DATABASE_HOST")
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://$host/fdelivery"
            username = user
            this.password = password
        }

        return HikariDataSource(config)
    }
}
