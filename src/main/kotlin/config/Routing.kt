package com.dpv.config

import com.dpv.controller.bookController
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        bookController()
    }
}
