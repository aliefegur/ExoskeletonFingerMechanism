package com.aliefegur.exomobile.communication

class Repository(private val apiClient: ApiClient) {
    suspend fun sendMotionCommand(
        mode: String,
        duration: Float
    ): Boolean {
        return apiClient.sendMotionCommand(mode, duration)
    }
}
