package com.dpv.repository.book

import com.dpv.data.entity.BookEntity
import com.dpv.data.entity.Books
import com.dpv.data.model.BookModel
import com.dpv.mapper.toModel
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.annotation.Singleton

@Singleton
class BookRepository {
    suspend fun getAll(): List<BookModel> {
        return newSuspendedTransaction {
            Books.selectAll().map { BookEntity.wrapRow(it).toModel() }
        }
    }
}