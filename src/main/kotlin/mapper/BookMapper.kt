package com.dpv.mapper

import com.dpv.data.dto.BookDto
import com.dpv.data.entity.BookEntity
import com.dpv.data.model.BookModel

fun BookEntity.toModel(): BookModel {
    return BookModel(
        id = this.id.value,
        title = this.title,
        author = this.author,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun BookModel.toDto(): BookDto {
    return BookDto(
        id = this.id,
        title = this.title,
        author = this.author,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}