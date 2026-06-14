package com.aliefegur.exomobile.communication

class Repository(
    private val sessionManager: SessionManager
) {

    suspend fun ping(): Boolean {
        return sessionManager.ping()
    }

    suspend fun sendMotionCommand(
        mode: String,
        duration: Float
    ): Boolean {
        return sessionManager.sendCommandWithAck(mode, duration)
    }
}
