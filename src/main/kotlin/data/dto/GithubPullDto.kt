package com.dpv.data.dto

import com.dpv.data.serialization.ISO8601LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class PullDto(
    val url: String,
    val id: Long,
    val state: String,
    val title: String,
    val user: PullUser,
    @Serializable(with = ISO8601LocalDateTimeSerializer::class)
    val closedAt: LocalDateTime? = null,
    @Serializable(with = ISO8601LocalDateTimeSerializer::class)
    val mergedAt: LocalDateTime? = null,
    @Serializable(with = ISO8601LocalDateTimeSerializer::class)
    @SerialName("created_at")
    val createdAt: LocalDateTime,
    @Serializable(with = ISO8601LocalDateTimeSerializer::class)
    @SerialName("updated_at")
    val updatedAt: LocalDateTime


)

@Serializable
data class PullUser(
    val id: Long,
    @SerialName("login")
    val username: String
)