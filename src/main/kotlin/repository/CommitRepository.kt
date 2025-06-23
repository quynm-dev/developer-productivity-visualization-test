package com.dpv.repository

import com.dpv.data.dto.github.CommitDto
import com.dpv.data.entity.Commits
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.annotation.Singleton
import java.time.LocalDateTime

@Singleton
class CommitRepository {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    suspend fun create(commitDto: CommitDto, userId: Long): Long {
        return newSuspendedTransaction {
            logger.info { "[RepositoryRepository:create]" }
            Commits.insert {
                it[hash] = commitDto.sha
                it[this.userId] = userId
                it[githubUrl] = commitDto.commit.url
                it[message] = commitDto.commit.message
                it[commitedAt] = commitDto.commit.author.date
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }[Commits.id].value
        }
    }
}