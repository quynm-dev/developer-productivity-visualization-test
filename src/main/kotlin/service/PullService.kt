package com.dpv.service

import com.dpv.data.dto.github.PullDto
import com.dpv.error.AppError
import com.dpv.error.GITHUB_ERROR_CODE_FACTORY
import com.dpv.helper.UniResult
import com.dpv.helper.err
import com.dpv.helper.ok
import com.dpv.repository.PullRepository
import org.koin.core.annotation.Singleton

@Singleton
class PullService(
    private val pullRepository: PullRepository
) {
    companion object {
        private val logger = mu.KotlinLogging.logger {}
    }

    suspend fun create(pullDto: PullDto): UniResult<Long> {
        return pullRepository.create(pullDto).ok()
    }

    suspend fun bulkCreate(pullDtos: List<PullDto>): UniResult<Boolean> {
        return pullRepository.bulkCreate(pullDtos).ok()
    }

    suspend fun validateExistence(id: Long): UniResult<Boolean> {
        val exist = pullRepository.validateExistence(id)
        if (!exist) {
            logger.warn("[PullService:validateExistence] Pull with id: $id does not exist")
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND, "Pull with id: $id does not exist").err()
        }

        return exist.ok()
    }
}