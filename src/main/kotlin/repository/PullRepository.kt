package com.dpv.repository

import com.dpv.data.dto.PullDto
import com.dpv.data.entity.Pulls
import com.dpv.data.enum.PullStatus
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.annotation.Singleton
import java.time.LocalDateTime

@Singleton
class PullRepository {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    suspend fun create(pullDto: PullDto): Long {
        return newSuspendedTransaction {
            logger.info { "[PullRepository:create]" }
            Pulls.insert {
                it[id] = pullDto.id
                it[githubUrl] = pullDto.url
                it[state] = PullStatus.fromString(pullDto.state)
                it[title] = pullDto.title
                it[userId] = pullDto.user.id
                it[closedAt] = pullDto.closedAt
                it[mergedAt] = pullDto.mergedAt
                it[githubCreatedAt] = pullDto.createdAt
                it[githubUpdatedAt] = pullDto.updatedAt
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }[Pulls.id].value
        }
    }
}