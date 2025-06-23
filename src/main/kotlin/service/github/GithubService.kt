package com.dpv.service.github

import com.dpv.client.RestClient
import com.dpv.data.dto.github.RateLimitDto
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
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get github rate limit").err()
        }.ok()
    }

    suspend fun sync(repoName: String): UniResult<Boolean> {
        // check rate limit
        val repo = repoService.findByName(repoName).getOrElse { findByNameErr ->
            if (!findByNameErr.hasCode(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND)) {
                return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to find repository").err()
            }

            val repo = githubRepoService.getRepo(repoName).getOrElse {
                return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get github repo").err()
            }

            userService.validateExistence(repo.owner.id).getOrElse { validateExistErr ->
                if (validateExistErr.hasCode(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND)) {
                    userService.create(repo.owner).getOrElse {
                        return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to create user").err()
                    }
                }
            }

            val repoId = repoService.create(repo).getOrElse {
                return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to create repository").err()
            }

            repoService.findById(repoId).getOrElse {
                return AppError.new(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND, "Failed to find repository by id").err()
            }
        }

        val (since, until) = if(repo.lastSyncAt != null) getSyncTimeFrame() else Pair(null, null)
        syncCommits(since, until, repo.commitsUrl).getOrElse {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to sync commits").err()
        }

        syncPulls(repo.pullsUrl).getOrElse {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get pulls").err()
        }

        return true.ok()
    }

    suspend fun syncCommits(since: LocalDateTime? = null, until: LocalDateTime? = null, commitsUrl: String): UniResult<Boolean> {
        val commits = githubCommitService.getCommits(since, until, commitsUrl).getOrElse {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get commits").err()
        }

        commits.forEach { commit ->
            val userId = userService.findIdByUsername(commit.commit.author.name).getOrElse {
                if (!it.hasCode(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND)) {
                    return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to find user with username: ${commit.commit.author.name}").err()
                }

                val user = githubUserService.getUser(commit.commit.author.name).getOrElse {
                    return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get user").err()
                }

                userService.create(user).getOrElse {
                    return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to create user").err()
                }

                user.id
            }

            commitService.create(commit, userId).getOrElse {
                return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to create commit").err()
            }
        }

        return true.ok()
    }

    suspend fun syncPulls(pullsUrl: String): UniResult<Boolean> {
        val pulls = githubPullService.getPulls(pullsUrl).getOrElse {
            return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get pulls").err()
        }

        pulls.forEach { pull ->
            userService.validateExistence(pull.user.id).getOrElse {
                if (!it.hasCode(GITHUB_ERROR_CODE_FACTORY.NOT_FOUND)) {
                    return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to find user with id: ${pull.user.id}").err()
                }

                val user = githubUserService.getUser(pull.user.username).getOrElse {
                    return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to get user").err()
                }

                userService.create(user).getOrElse {
                    return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to create user").err()
                }
            }

            pullService.create(pull).getOrElse {
                return AppError.new(GITHUB_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, "Failed to create pull request").err()
            }
        }

        return true.ok()
    }
}