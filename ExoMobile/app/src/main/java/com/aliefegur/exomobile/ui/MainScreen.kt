package com.aliefegur.exomobile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class MotionMode {
    OPEN, CLOSE
}

@Composable
fun MainScreen(
    onRun: (MotionMode, Float) -> Unit = { _, _ -> }
) {
    var selectedMode by remember { mutableStateOf(MotionMode.CLOSE) }
    var duration by remember { mutableFloatStateOf(3f) }
    var isRunning by remember { mutableStateOf(false) }

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
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Select Motion Mode",
                style = MaterialTheme.typography.titleMedium
            )

            // MODE SELECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FilterChip(
                    selected = selectedMode == MotionMode.OPEN,
                    onClick = { selectedMode = MotionMode.OPEN },
                    label = { Text("OPEN (Finger Extend)") }
                )

                FilterChip(
                    selected = selectedMode == MotionMode.CLOSE,
                    onClick = { selectedMode = MotionMode.CLOSE },
                    label = { Text("CLOSE (Finger Flex)") }
                )
            }

            HorizontalDivider()

            // DURATION CONTROL
            Text(
                text = "Movement Duration: ${"%.1f".format(duration)} sec",
                style = MaterialTheme.typography.titleMedium
            )

            Slider(
                value = duration,
                onValueChange = { duration = it },
                valueRange = 0.5f..10f,
                steps = 18
            )

            Spacer(modifier = Modifier.height(8.dp))

            // RUN BUTTON
            Button(
                onClick = {
                    isRunning = true
                    onRun(selectedMode, duration)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isRunning
            ) {
                Text(if (isRunning) "Running..." else "RUN MOVEMENT")
            }

            // STOP BUTTON (future expansion)
            OutlinedButton(
                onClick = {
                    isRunning = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("STOP")
            }

            Spacer(modifier = Modifier.weight(1f))

            // STATUS
            Text(
                text = "Mode: ${selectedMode.name}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
