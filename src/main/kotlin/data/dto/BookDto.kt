package com.dpv.data.dto

import com.dpv.data.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class BookDto(
    val id: Int,
    val title: String,
    val author: String,
    val description: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)