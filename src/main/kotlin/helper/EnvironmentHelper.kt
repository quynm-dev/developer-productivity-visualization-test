package com.dpv.helper

import io.ktor.server.application.*

fun ApplicationEnvironment.getProperty(key: String) = config.propertyOrNull(key)?.getString()!!

fun ApplicationEnvironment.getPropertyOrNull(key: String) = config.propertyOrNull(key)?.getString()