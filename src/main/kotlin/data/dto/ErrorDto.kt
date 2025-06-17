package com.dpv.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val code: String,
    val message: String,
    val cause: String? = null
)