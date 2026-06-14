package com.aliefegur.exomobile.communication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ConnectionState(
    val isConnected: Boolean = false,
    val lastSuccess: Boolean = true,
    val lastError: String? = null
)

class SessionManager(private val apiClient: ApiClient) {
    private val mutex = Mutex()

    private val _state = MutableStateFlow(ConnectionState())
    val state: StateFlow<ConnectionState> = _state

    suspend fun sendMotionCommand(
        mode: String,
        duration: Float
    ): Boolean = mutex.withLock {

        return try {
            val result = apiClient.sendMotionCommand(mode, duration)

            _state.value = _state.value.copy(
                isConnected = result,
                lastSuccess = result,
                lastError = if (!result) "Command failed" else null
            )

            result

        } catch (e: Exception) {

            _state.value = _state.value.copy(
                isConnected = false,
                lastSuccess = false,
                lastError = e.message
            )

            false
        }
    }
}
