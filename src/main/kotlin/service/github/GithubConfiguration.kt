package com.dpv.service.github

import com.dpv.helper.getProperty
import io.ktor.server.application.*

abstract class GithubConfiguration(environment: ApplicationEnvironment) {
    protected val PAT = environment.getProperty("github.pat")
    protected val BASE_URL = environment.getProperty("github.baseUrl")
    protected val X_GITHUB_API_VERSION = "2022-11-28"

    protected val RATE_LIMIT_PATH = "/rate_limit"
}