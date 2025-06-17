package com.dpv.mapper

import com.dpv.data.dto.ErrorDto
import com.dpv.error.AppError

fun AppError.toDto(): ErrorDto {
    return ErrorDto(
        code = "${this.code.domain.symbol}-${this.code.statusCode.value}",
        message = this.message,
        cause = this.cause
    )
}