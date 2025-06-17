package com.dpv.data.model

import java.time.LocalDateTime

data class BookModel(
    val id: Int,
    val title: String,
    val author: String,
    val description: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)