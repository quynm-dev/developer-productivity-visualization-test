package com.dpv.controller

import com.dpv.helper.respondError
import com.dpv.service.book.BookService
import com.github.michaelbull.result.mapBoth
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.bookController() {
    val bookService by inject<BookService>()

    route("/books") {
        get {
            bookService.getAllBooks().mapBoth(
                success = { call.respond(it.toString()) },
                failure = { call.respondError(it) }
            )
        }
    }
}