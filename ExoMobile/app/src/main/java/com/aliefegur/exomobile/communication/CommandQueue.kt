package com.aliefegur.exomobile.communication

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

class CommandQueue(private val repository: Repository) {

    private val mutex = Mutex()
    private var lastCommand: MotionCommand? = null
    private var job: Job? = null

    fun send(command: MotionCommand) {
        job?.cancel()

        job = CoroutineScope(Dispatchers.IO).launch {
            delay(150)

            val ok = repository.sendMotionCommand(
                command.mode.name,
                command.duration
            )

            if (!ok) {
                // future: retry logic
            }
        }
    }

    fun clear() {
        job?.cancel()
        job = null
        lastCommand = null
    }
}
