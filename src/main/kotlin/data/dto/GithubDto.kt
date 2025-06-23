package com.dpv.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RateLimitDto(
    val resources: RateLimitResources
)

@Serializable
data class RateLimitResources(
    val core: RateLimitResourcesCore
)

@Serializable
data class RateLimitResourcesCore(
    val limit: Int,
    val used: Int,
    val remaining: Int,
    val reset: Long
)