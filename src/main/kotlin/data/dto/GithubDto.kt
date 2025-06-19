package com.dpv.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetRateLimitDto(
    val resources: GetRateLimitResources
)

@Serializable
data class GetRateLimitResources(
    val core: GetRateLimitResourcesCore
)

@Serializable
data class GetRateLimitResourcesCore(
    val limit: Int,
    val used: Int,
    val remaining: Int,
    val reset: Long
)