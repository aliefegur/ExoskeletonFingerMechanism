package com.aliefegur.exomobile.communication

data class MotionCommand(
    val mode: MotionMode,
    val duration: Float,
    val timestamp: Long = System.currentTimeMillis()
)
