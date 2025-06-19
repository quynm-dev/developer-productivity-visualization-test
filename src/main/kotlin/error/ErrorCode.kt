package com.dpv.error

import io.ktor.http.*

val GITHUB_ERROR_CODE_FACTORY = CommonErrorCodeFactory(ErrorDomain.GITHUB)
val GENERIC_ERROR_CODE_FACTORY = CommonErrorCodeFactory(ErrorDomain.GENERIC)

data class ErrorCode(
    val statusCode: HttpStatusCode,
    val domain: ErrorDomain
)

open class CommonErrorCodeFactory(private val domain: ErrorDomain) {
    val BAD_REQUEST = ErrorCode(
        statusCode = HttpStatusCode.BadRequest,
        domain = this.domain
    )
    val FORBIDDEN = ErrorCode(
        statusCode = HttpStatusCode.Forbidden,
        domain = this.domain
    )
    val NOT_FOUND = ErrorCode(
        statusCode = HttpStatusCode.NotFound,
        domain = this.domain
    )
    val ALREADY_EXIST = ErrorCode(
        statusCode = HttpStatusCode.Conflict,
        domain = this.domain
    )
    val INTERNAL_SERVER_ERROR = ErrorCode(
        statusCode = HttpStatusCode.InternalServerError,
        domain = this.domain
    )
}