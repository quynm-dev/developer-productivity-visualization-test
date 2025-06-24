package com.dpv.service

import com.dpv.data.dto.github.CommitDto
import com.dpv.helper.UniResult
import com.dpv.helper.ok
import com.dpv.repository.CommitRepository
import org.koin.core.annotation.Singleton

@Singleton
class CommitService(
    private val commitRepository: CommitRepository
) {
    suspend fun create(commitDto: CommitDto, userId: Long): UniResult<Long> {
        return commitRepository.create(commitDto, userId).ok()
    }

    suspend fun bulkCreate(commitDtos: List<CommitDto>): UniResult<Boolean> {
        return commitRepository.bulkCreate(commitDtos).ok()
    }
}