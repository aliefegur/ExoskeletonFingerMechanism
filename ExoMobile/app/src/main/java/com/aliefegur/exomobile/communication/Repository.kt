package com.aliefegur.exomobile.communication

class Repository(private val sessionManager: SessionManager) {
    suspend fun sendMotionCommand(
        mode: String,
        duration: Float
    ): Boolean {
        return sessionManager.sendMotionCommand(mode, duration)
    }
}
