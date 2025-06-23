package com.dpv.controller

import com.dpv.helper.respondError
import com.dpv.service.github.GithubService
import com.github.michaelbull.result.mapBoth
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.githubController() {
    val githubService by inject<GithubService>()

    route("/sync") {
        get {
            githubService.sync("repoName").mapBoth(
                success = { call.respond(it) },
                failure = { call.respondError(it) }
            )
        }
    }
}