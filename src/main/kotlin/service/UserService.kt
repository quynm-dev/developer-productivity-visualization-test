package com.dpv.service

import com.dpv.data.dto.github.UserDto
import com.dpv.error.AppError
import com.dpv.error.GITHUB_ERROR_CODE_FACTORY
import com.dpv.helper.UniResult
import com.dpv.helper.err
import com.dpv.helper.ok
import com.dpv.repository.UserRepository
import mu.KotlinLogging
import org.koin.core.annotation.Singleton

@Singleton
class UserService(
    private val userRepository: UserRepository
) {
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
}