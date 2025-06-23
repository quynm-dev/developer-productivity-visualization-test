package com.dpv.error

data class AppError(
    val code: ErrorCode,
    val message: String,
    val cause: String? = null
) {
    companion object {
        fun wrap(code: ErrorCode, ex: Exception): AppError {
            return AppError(
                code = code,
                message = ex.message.toString(),
                cause = ex.cause?.stackTraceToString()
            )
        }

        fun new(code: ErrorCode, message: String): AppError {
            return AppError(
                code = code,
                message = message
            )
        }
    }


    fun hasCode(code: ErrorCode): Boolean {
        return this.code == code
    }
}