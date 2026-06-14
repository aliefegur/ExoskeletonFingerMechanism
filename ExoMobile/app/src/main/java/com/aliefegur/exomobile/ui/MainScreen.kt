package com.aliefegur.exomobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aliefegur.exomobile.communication.MotionMode

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Exoskeleton Controller",
                style = MaterialTheme.typography.headlineSmall
            )

            // -------------------------
            // MODE SELECTION
            // -------------------------

            Text("Motion Mode")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                FilterChip(
                    selected = state.mode == MotionMode.OPEN,
                    onClick = { viewModel.onModeSelected(MotionMode.OPEN) },
                    label = { Text("OPEN") }
                )

                FilterChip(
                    selected = state.mode == MotionMode.CLOSE,
                    onClick = { viewModel.onModeSelected(MotionMode.CLOSE) },
                    label = { Text("CLOSE") }
                )
            }

            // -------------------------
            // DURATION
            // -------------------------

            Text("Duration: ${"%.1f".format(state.duration)} sec")

            Slider(
                value = state.duration,
                onValueChange = { viewModel.onDurationChanged(it) },
                valueRange = 0.5f..10f
            )

            // -------------------------
            // ACTIONS
            // -------------------------

            Button(
                onClick = { viewModel.onRunClicked() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSending
            ) {
                Text(
                    if (state.isSending) "Sending..."
                    else "RUN MOVEMENT"
                )
            }

            OutlinedButton(
                onClick = { viewModel.onStopClicked() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("STOP")
            }

            // -------------------------
            // STATUS
            // -------------------------

            state.lastStatus?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
