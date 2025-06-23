package com.dpv.service.github

import com.dpv.client.RestClient
import com.dpv.data.dto.UserDto
import com.dpv.error.AppError
import com.dpv.error.GITHUB_ERROR_CODE_FACTORY
import com.dpv.helper.UniResult
import com.dpv.helper.deserializeIgnoreKeysWhen
import com.dpv.helper.err
import com.dpv.helper.ok
import com.dpv.repository.UserRepository
import io.ktor.server.application.*
import mu.KotlinLogging
import org.koin.core.annotation.Singleton

@Singleton
class GithubUserService(
    environment: ApplicationEnvironment,
    private val restClient: RestClient,
    private val userRepository: UserRepository,
) : GithubConfiguration(environment) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    suspend fun findIdByUsername(username: String): UniResult<Long> {
        val user = userRepository.findIdByUsername(username)
        if (user == null) {
            logger.warn("[GithubUserService:findIdByUsername] User with username: $username not found")
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND, "User with username $username not found").err()
        }

        return user.ok()
    }

    suspend fun validateExistence(id: Long): UniResult<Boolean> {
        val exist = userRepository.validateExistence(id)
        if (!exist) {
            logger.warn("[GithubUserService:validateExistence] User with id: $id does not exist")
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND, "User with id $id does not exist").err()
        }

        return exist.ok()
    }

    suspend fun create(userDto: UserDto): UniResult<Long> {
        return userRepository.create(userDto).ok()
    }

    suspend fun getUser(username: String): UniResult<UserDto> {
        val response = restClient.get(BASE_URL + USERS_PREFIX + username) {
            authorization = AUTHORIZATION
            configureHeaders {
                appendAll(xGithubApiVersionHeader)
            }
        }

        return response.deserializeIgnoreKeysWhen<UserDto> {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get user").err()
        }.ok()
    }
}