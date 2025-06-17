package com.dpv.helper

import com.dpv.error.AppError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

fun <T: Any> T.ok() = Ok(this)

fun AppError.err() = Err(this)