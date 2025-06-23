package com.dpv.service.github

import com.dpv.client.RestClient
import com.dpv.data.dto.github.RateLimitDto
import com.dpv.data.model.RepositoryModel
import com.dpv.error.AppError
import com.dpv.error.GITHUB_ERROR_CODE_FACTORY
import com.dpv.helper.*
import com.dpv.service.CommitService
import com.dpv.service.PullService
import com.dpv.service.RepositoryService
import com.dpv.service.UserService
import com.github.michaelbull.result.getOrElse
import io.ktor.server.application.*
import org.koin.core.annotation.Singleton
import java.time.LocalDateTime

@Singleton
class GithubService(
    environment: ApplicationEnvironment,
    private val restClient: RestClient,
    private val repoService: RepositoryService,
    private val commitService: CommitService,
    private val userService: UserService,
    private val pullService: PullService,
    private val githubRepoService: GithubRepositoryService,
    private val githubCommitService: GithubCommitService,
    private val githubUserService: GithubUserService,
    private val githubPullService: GithubPullService
) : GithubConfiguration(environment) {
    suspend fun getRateLimit(): UniResult<RateLimitDto> {
        val response = restClient.get(BASE_URL) {
            authorization = AUTHORIZATION
            path(RATE_LIMIT_PATH)
            configureHeaders {
                appendAll(xGithubApiVersionHeader)
            }
        }

        return response.deserializeIgnoreKeysWhen<RateLimitDto> {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get rate limit").err()
        }.ok()
    }

    suspend fun sync(repoName: String): UniResult<Unit> {
        // check rate limit
        val repo = getOrCreateRepoByName(repoName).getOrElse { getOrCreateRepoErr ->
            return getOrCreateRepoErr.err()
        }

        val (since, until) = if(repo.lastSyncAt != null) getSyncTimeFrame() else Pair(null, null)
        syncCommits(since, until, repo.commitsUrl).getOrElse { syncCommitsErr ->
            return syncCommitsErr.err()
        }

        syncPulls(repo.pullsUrl).getOrElse { syncPullsErr ->
            return syncPullsErr.err()
        }

        return Unit.ok()
    }

    suspend fun syncCommits(since: LocalDateTime? = null, until: LocalDateTime? = null, commitsUrl: String): UniResult<Unit> {
        val commits = githubCommitService.getCommits(since, until, commitsUrl).getOrElse { getCommitsErr ->
            return getCommitsErr.err()
        }

        // resolve n + 1
        commits.forEach { commit ->
            val userId = userService.findIdByUsername(commit.commit.author.name).getOrElse { findIdByUsernameErr ->
                if (!findIdByUsernameErr.hasCode(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND)) {
                    return findIdByUsernameErr.err()
                }

                val user = githubUserService.getUser(commit.commit.author.name).getOrElse { getUserErr ->
                    return getUserErr.err()
                }

                userService.create(user).getOrElse { createErr ->
                    return createErr.err()
                }

                user.id
            }

            commitService.create(commit, userId).getOrElse { createErr ->
                return createErr.err()
            }
        }

        return Unit.ok()
    }

    suspend fun syncPulls(pullsUrl: String): UniResult<Unit> {
        val pulls = githubPullService.getPulls(pullsUrl).getOrElse {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get pulls").err()
        }

        pulls.forEach { pull ->
            userService.validateExistence(pull.user.id).getOrElse { validateExistenceErr ->
                if (!validateExistenceErr.hasCode(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND)) {
                    return validateExistenceErr.err()
                }

                val user = githubUserService.getUser(pull.user.username).getOrElse { getUserErr ->
                    return getUserErr.err()
                }

                userService.create(user).getOrElse { createErr ->
                    return createErr.err()
                }
            }

            pullService.create(pull).getOrElse { createErr ->
                return createErr.err()
            }
        }

        return Unit.ok()
    }

    suspend fun getOrCreateRepoByName(name: String): UniResult<RepositoryModel> {
        return repoService.findByName(name).getOrElse { findByNameErr ->
            if (!findByNameErr.hasCode(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND)) {
                return findByNameErr.err()
            }

            val repo = githubRepoService.getRepo(name).getOrElse { getRepoErr ->
                return getRepoErr.err()
            }

            userService.validateExistence(repo.owner.id).getOrElse { validateExistErr ->
                if (!validateExistErr.hasCode(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND)) {
                    return validateExistErr.err()
                }

                userService.create(repo.owner).getOrElse { createUserErr ->
                    return createUserErr.err()
                }
            }

            val repoId = repoService.create(repo).getOrElse { createRepoErr ->
                return createRepoErr.err()
            }

            repoService.findById(repoId).getOrElse { findByIdErr ->
                return findByIdErr.err()
            }
        }.ok()
    }
}