package com.dpv.controller

import com.dpv.service.book.BookService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.bookController() {
    val bookService by inject<BookService>()

    route("/books") {
        get {
            call.respond(bookService.getAllBooks().toString())
        }
    }
}