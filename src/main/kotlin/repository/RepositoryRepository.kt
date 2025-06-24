package com.dpv.repository

import com.dpv.data.dto.github.RepositoryDto
import com.dpv.data.entity.Repositories
import com.dpv.data.entity.RepositoryEntity
import com.dpv.data.model.RepositoryModel
import com.dpv.mapper.toModel
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.annotation.Singleton
import java.time.LocalDateTime

@Singleton
class RepositoryRepository {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    suspend fun findById(id: Long): RepositoryModel? {
        return newSuspendedTransaction {
            logger.info { "[RepositoryRepository:findById] with id: $id" }
            RepositoryEntity.findById(id)?.toModel()
        }
    }

    suspend fun findByName(name: String): RepositoryModel? {
        return newSuspendedTransaction {
            logger.info { "[RepositoryRepository:findByName] with name: $name" }
            RepositoryEntity.find { Repositories.name eq name }.singleOrNull()?.toModel()
        }
    }

    suspend fun create(repo: RepositoryDto): Long {
        return newSuspendedTransaction {
            logger.info { "[RepositoryRepository:create]" }
            Repositories.insert {
                it[id] = repo.id
                it[name] = repo.name
                it[githubUrl] = repo.url
                it[userId] = repo.owner.id
                it[language] = repo.language
                it[pullsUrl] = repo.pullsUrl.substringBefore("{")
                it[commitsUrl] = repo.commitsUrl.substringBefore("{")
                it[lastSyncAt] = null
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }

            repo.id
        }
    }
}