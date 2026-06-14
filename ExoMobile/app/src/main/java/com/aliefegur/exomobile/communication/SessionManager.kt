package com.aliefegur.exomobile.communication

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SessionManager(
    private val apiClient: ApiClient
) {

    private val mutex = Mutex()

    private val _state = MutableStateFlow(ConnectionState())
    val state: StateFlow<ConnectionState> = _state

    // -------------------------
    // CONNECTION CHECK
    // -------------------------

    suspend fun ping(): Boolean = mutex.withLock {

        return try {
            val ok = apiClient.ping()

            _state.value = _state.value.copy(
                isConnected = ok,
                lastPingSuccess = ok,
                lastError = if (!ok) "Ping failed" else null
            )

            ok

        } catch (e: Exception) {

            _state.value = _state.value.copy(
                isConnected = false,
                lastPingSuccess = false,
                lastError = e.message
            )

            false
        }
    }

    // -------------------------
    // COMMAND WITH ACK
    // -------------------------

    suspend fun sendCommandWithAck(
        mode: String,
        duration: Float
    ): Boolean = mutex.withLock {

        return try {
            val response = apiClient.sendMotionCommand(mode, duration)

            val success = response == "OK"

            _state.value = _state.value.copy(
                isConnected = success,
                lastError = if (!success) "ACK failed" else null
            )

            success

        } catch (e: Exception) {

            e.printStackTrace()

            _state.value = _state.value.copy(
                isConnected = false,
                lastError = e.message
            )

            false
        }
    }
}
