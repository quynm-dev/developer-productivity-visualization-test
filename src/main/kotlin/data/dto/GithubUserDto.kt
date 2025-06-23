package com.dpv.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    @SerialName("avatar_url")
    val avatarUrl: String,
    val url: String,
    @SerialName("login")
    val name: String
)