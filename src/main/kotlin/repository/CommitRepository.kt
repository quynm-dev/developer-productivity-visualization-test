package com.dpv.repository

import com.dpv.data.dto.github.CommitDto
import com.dpv.data.entity.Commits
import mu.KotlinLogging
import org.jetbrains.exposed.sql.batchInsert
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
            logger.info { "[CommitRepository:create]" }
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

    suspend fun bulkCreate(commitDtos: List<CommitDto>): Boolean {
        return newSuspendedTransaction {
            logger.info { "[CommitRepository:bulkCreate]" }
            Commits.batchInsert(commitDtos) { commitDto ->
                this[Commits.hash] = commitDto.sha
                this[Commits.userId] = commitDto.author.id
                this[Commits.githubUrl] = commitDto.commit.url
                this[Commits.message] = commitDto.commit.message
                this[Commits.commitedAt] = commitDto.commit.author.date
                this[Commits.createdAt] = LocalDateTime.now()
                this[Commits.updatedAt] = LocalDateTime.now()
            }.isNotEmpty()
        }
    }
}