package com.dpv.service

import com.dpv.data.dto.github.PullDto
import com.dpv.helper.UniResult
import com.dpv.helper.ok
import com.dpv.repository.PullRepository
import org.koin.core.annotation.Singleton

@Singleton
class PullService(
    private val pullRepository: PullRepository
) {
    suspend fun create(pullDto: PullDto): UniResult<Long> {
        return pullRepository.create(pullDto).ok()
    }
}