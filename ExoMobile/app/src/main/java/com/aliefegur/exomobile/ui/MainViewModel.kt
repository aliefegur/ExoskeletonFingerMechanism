package com.aliefegur.exomobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliefegur.exomobile.communication.CommandQueue
import com.aliefegur.exomobile.communication.MotionCommand
import com.aliefegur.exomobile.communication.MotionMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class UiState(
    val mode: MotionMode = MotionMode.CLOSE,
    val duration: Float = 3f,
    val isSending: Boolean = false,
    val lastStatus: String? = null
)

class MainViewModel(
    private val commandQueue: CommandQueue
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    // -------------------------
    // UI INPUTS
    // -------------------------

    fun onModeSelected(mode: MotionMode) {
        _uiState.update {
            it.copy(mode = mode)
        }
    }

    fun onDurationChanged(duration: Float) {
        _uiState.update {
            it.copy(duration = duration)
        }
    }

    // -------------------------
    // MAIN ACTION
    // -------------------------

    fun onRunClicked() {
        val state = _uiState.value

        val command = MotionCommand(
            mode = state.mode,
            duration = state.duration
        )

        _uiState.update {
            it.copy(
                isSending = true,
                lastStatus = "Sending..."
            )
        }

        commandQueue.send(command)

        _uiState.update {
            it.copy(
                isSending = false,
                lastStatus = "Command sent"
            )
        }
    }

    fun onStopClicked() {
        commandQueue.clear()

        _uiState.update {
            it.copy(
                isSending = false,
                lastStatus = "Stopped"
            )
        }
    }
}
