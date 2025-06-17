package com.dpv.config

import com.dpv.helper.getProperty
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jetbrains.exposed.sql.Database

private const val CHANGELOG_FILE_PATH = "db/changelog.yaml"

data class DBConfig(
    val username: String,
    val password: String,
    val url: String,
    val driver: String
) {
    fun toHikariConfig(): HikariConfig {
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = this.url
        hikariConfig.username = this.username
        hikariConfig.password = this.password
        hikariConfig.driverClassName = this.driver
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true")
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250")
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        return hikariConfig
    }
}

fun Application.configureDB(): HikariDataSource {
    val dbConfig = DBConfig(
        username = environment.getProperty("dpv.db.username"),
        password = environment.getProperty("dpv.db.password"),
        url = environment.getProperty("dpv.db.url"),
        driver = environment.getProperty("dpv.db.driver")
    )
    val dataSource = createHikariDatasource(dbConfig)

    Database.connect(dataSource)
    Scope.child(Scope.Attr.resourceAccessor, ClassLoaderResourceAccessor()) {
        CommandScope("update")
            .addArgumentValue("changeLogFile", CHANGELOG_FILE_PATH)
            .addArgumentValue("database", DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection)))
            .execute()
    }

    return dataSource
}

private fun createHikariDatasource(config: DBConfig): HikariDataSource = HikariDataSource(config.toHikariConfig())