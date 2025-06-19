package com.dpv.client

import io.ktor.client.statement.*
import io.ktor.http.*

interface IRestClient {
    suspend fun get(url: String, config: RestConfig<Unit>.() -> Unit = {}) = exec(url, HttpMethod.Get, null, config)

    suspend fun <T> post(url: String, body: T, config: RestConfig<T>.() -> Unit = {}) = exec(url, HttpMethod.Post, body, config)

    suspend fun <T> exec(url: String, method: HttpMethod, body: T?, config: RestConfig<T>.() -> Unit): HttpResponse
}