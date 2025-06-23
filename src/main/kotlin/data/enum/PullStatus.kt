package com.dpv.data.enum

enum class PullStatus {
    OPEN,
    CLOSED;

    companion object {
        fun fromString(value: String): PullStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) }!!
        }
    }
}