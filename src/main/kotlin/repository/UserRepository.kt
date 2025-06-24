package com.dpv.repository

import com.dpv.data.dto.github.UserDto
import com.dpv.data.entity.UserEntity
import com.dpv.data.entity.Users
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.annotation.Singleton
import java.time.LocalDateTime

@Singleton
class UserRepository {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    suspend fun findIdByUsername(username: String): Long? {
        return newSuspendedTransaction {
            logger.info { "[UserRepository:findIdByUsername] with username: $username" }
            UserEntity.find { Users.username eq username }.singleOrNull()?.id?.value
        }
    }

    suspend fun validateExistence(id: Long): Boolean {
        return newSuspendedTransaction {
            logger.info { "[UserRepository:validateExistence] with id: $id" }
            UserEntity.findById(id) != null
        }
    }

    suspend fun create(userDto: UserDto): Long {
        return newSuspendedTransaction {
            logger.info { "[UserRepository:create]" }
            Users.insert {
                it[id] = userDto.id
                it[username] = userDto.name
                it[githubUrl] = userDto.url
                it[avatarUrl] = userDto.avatarUrl
                it[githubId] = userDto.id
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }

            userDto.id
        }
    }
}