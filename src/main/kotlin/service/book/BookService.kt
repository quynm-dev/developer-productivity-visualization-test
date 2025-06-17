package com.dpv.service.book

import com.dpv.data.dto.BookDto
import com.dpv.error.*
import com.dpv.helper.UniResult
import com.dpv.helper.err
import com.dpv.helper.ok
import com.dpv.mapper.toDto
import com.dpv.repository.book.BookRepository
import org.koin.core.annotation.Singleton

@Singleton
class BookService(
    private val bookRepository: BookRepository
) {
    suspend fun getAllBooks(): UniResult<List<BookDto>> {
        return try {
            bookRepository.getAll().map { it.toDto() }.ok()
        } catch (ex: Exception) {
            AppError.wrap(BOOK_ERROR_CODE_FACTORY.INTERNAL_SERVER_ERROR, ex).err()
        }
    }
}