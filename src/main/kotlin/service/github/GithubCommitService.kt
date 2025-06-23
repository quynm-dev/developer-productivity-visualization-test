package com.dpv.service.github

import com.dpv.client.RestClient
import com.dpv.data.dto.CommitDto
import com.dpv.error.AppError
import com.dpv.error.GITHUB_ERROR_CODE_FACTORY
import com.dpv.helper.UniResult
import com.dpv.helper.deserializeIgnoreKeysWhen
import com.dpv.helper.err
import com.dpv.helper.ok
import com.dpv.repository.CommitRepository
import io.ktor.http.*
import io.ktor.server.application.*
import org.koin.core.annotation.Singleton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Singleton
class GithubCommitService(
    environment: ApplicationEnvironment,
    private val restClient: RestClient,
    private val commitRepository: CommitRepository
) : GithubConfiguration(environment) {
    suspend fun getCommits(
        since: LocalDateTime? = null, until: LocalDateTime? = null, url: String,
        perPage: Int = 30, page: Int = 1
    ): UniResult<List<CommitDto>> {
        val response = restClient.get(url) {
            authorization = AUTHORIZATION
            configureHeaders {
                appendAll(xGithubApiVersionHeader)
            }
            parameters {
                since?.let { append("since", since.format(DateTimeFormatter.ISO_DATE_TIME)) }
                until?.let { append("until", until.format(DateTimeFormatter.ISO_DATE_TIME)) }
                append("per_page", perPage.toString())
                append("page", page.toString())
            }
        }

        return response.deserializeIgnoreKeysWhen<List<CommitDto>> {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get commits").err()
        }.ok()
    }

    suspend fun create(commitDto: CommitDto, userId: Long): UniResult<Long> {
        return commitRepository.create(commitDto, userId).ok()
    }
}