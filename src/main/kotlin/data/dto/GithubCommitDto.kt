package com.dpv.data.dto

import com.dpv.data.serialization.ISO8601LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CommitDto(
    val sha: String,
    val commit: CommitCommit,
)

@Serializable
data class CommitCommit(
    val message: String,
    val author: CommitCommitAuthor,
    val url: String
)

@Serializable
data class CommitCommitAuthor(
    val name: String,
    val email: String,
    @Serializable(with = ISO8601LocalDateTimeSerializer::class)
    val date: LocalDateTime
)