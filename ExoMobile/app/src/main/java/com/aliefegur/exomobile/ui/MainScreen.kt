package com.aliefegur.exomobile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onConnectClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onStopClick: () -> Unit = {}
) {
    var servo1Angle by remember { mutableFloatStateOf(0f) }
    var servo2Angle by remember { mutableFloatStateOf(0f) }

    Scaffold() { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Finger Exoskeleton Controller",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = onConnectClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect")
            }

            HorizontalDivider()

            ServoControl(
                title = "Servo 1",
                angle = servo1Angle,
                onAngleChange = { servo1Angle = it }
            )

            ServoControl(
                title = "Servo 2",
                angle = servo2Angle,
                onAngleChange = { servo2Angle = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onStartClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start")
                }

                Button(
                    onClick = onStopClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Stop")
                }
            }
        }
    }
}

@Composable
private fun ServoControl(
    title: String,
    angle: Float,
    onAngleChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                modifier = Modifier.weight(1f)
            )

            Text("${angle.toInt()}°")
        }

        Slider(
            value = angle,
            onValueChange = onAngleChange,
            valueRange = 0f..180f
        )
    }
}
