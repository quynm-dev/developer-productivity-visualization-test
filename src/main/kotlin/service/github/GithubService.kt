package com.dpv.service.github

import com.dpv.client.RestClient
import com.dpv.data.dto.GetRateLimitDto
import com.dpv.error.AppError
import com.dpv.error.GITHUB_ERROR_CODE_FACTORY
import com.dpv.helper.*
import io.ktor.http.*
import io.ktor.server.application.*
import org.koin.core.annotation.Singleton

@Singleton
class GithubService(
    environment: ApplicationEnvironment,
    private val restClient: RestClient
) : GithubConfiguration(environment) {
    suspend fun getRateLimit(): UniResult<GetRateLimitDto> {
        val response = restClient.get(BASE_URL) {
            authorization = "Bearer $PAT"
            path(RATE_LIMIT_PATH)
            configureHeaders {
                appendAll(xGithubApiVersionHeader)
            }
        }

        return response.deserializeIgnoreKeysWhen<GetRateLimitDto> {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get github rate limit").err()
        }.ok()
    }

    private val xGithubApiVersionHeader: Headers
        get() = Headers.build { append("X-GitHub-Api-Version", X_GITHUB_API_VERSION) }
}