package com.dpv.data.dto.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryDto(
    val id: Long,
    @SerialName("full_name")
    val name: String,
    val url: String,
    val language: String,
    @SerialName("pulls_url")
    val pullsUrl: String,
    @SerialName("commits_url")
    val commitsUrl: String,
    val owner: UserDto
)