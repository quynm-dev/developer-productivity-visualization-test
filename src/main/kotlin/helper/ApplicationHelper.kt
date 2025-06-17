package com.dpv.helper

import com.dpv.error.AppError
import com.dpv.mapper.toDto
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondError(error: AppError) {
    this.respond(error.toDto())
}