package com.example.cryptopayment.data

import java.util.*

enum class StatusUpdateType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

data class StatusUpdate(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val type: StatusUpdateType,
    val timestamp: Date = Date()
)