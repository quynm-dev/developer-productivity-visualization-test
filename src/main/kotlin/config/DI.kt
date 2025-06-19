package com.dpv.config

import io.ktor.server.application.*
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(environmentModule(), defaultModule)
    }
}

fun Application.environmentModule() = module {
    single { environment } bind(ApplicationEnvironment::class)
}