package com.aliefegur.exomobile.communication

data class ConnectionState(
    val isConnected: Boolean = false,
    val lastPingSuccess: Boolean = false,
    val lastError: String? = null
)
