package com.dpv.service.github

import com.dpv.client.RestClient
import com.dpv.data.dto.RepositoryDto
import com.dpv.data.model.RepositoryModel
import com.dpv.error.AppError
import com.dpv.error.GITHUB_ERROR_CODE_FACTORY
import com.dpv.helper.UniResult
import com.dpv.helper.deserializeIgnoreKeysWhen
import com.dpv.helper.err
import com.dpv.helper.ok
import com.dpv.repository.RepositoryRepository
import io.ktor.server.application.*
import mu.KotlinLogging
import org.koin.core.annotation.Singleton

@Singleton
class GithubRepositoryService(
    environment: ApplicationEnvironment,
    private val restClient: RestClient,
    private val repoRepository: RepositoryRepository,
) : GithubConfiguration(environment) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    suspend fun findById(id: Long): UniResult<RepositoryModel> {
        val repo = repoRepository.findById(id)
        if (repo == null) {
            logger.warn("[GithubRepositoryService:findById] Repo with id: $id not found")
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND, "Repository with id $id not found").err()
        }

        return repo.ok()
    }

    suspend fun findByName(name: String): UniResult<RepositoryModel> {
        val repo = repoRepository.findByName(name)
        if (repo == null) {
            logger.warn("[GithubRepositoryService:findByName] Repo with name: $name not found")
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND, "Repository with name $name not found").err()
        }

        return repo.ok()
    }

    suspend fun getRepo(name: String): UniResult<RepositoryDto> {
        val response = restClient.get(BASE_URL + REPOS_PREFIX + name) {
            authorization = AUTHORIZATION
            configureHeaders {
                appendAll(xGithubApiVersionHeader)
            }
        }

        return response.deserializeIgnoreKeysWhen<RepositoryDto> {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get repository").err()
        }.ok()
    }

    suspend fun create(repoDto: RepositoryDto): UniResult<Long> {
        return repoRepository.create(repoDto).ok()
    }
}