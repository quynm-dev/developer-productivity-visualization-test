package com.dpv.service.book

import com.dpv.data.dto.BookDto
import com.dpv.mapper.toDto
import com.dpv.repository.book.BookRepository
import org.koin.core.annotation.Singleton

@Singleton
class BookService(
    private val bookRepository: BookRepository
) {
    suspend fun getAllBooks(): List<BookDto> {
        return bookRepository.getAll().map { it.toDto() }
    }
}