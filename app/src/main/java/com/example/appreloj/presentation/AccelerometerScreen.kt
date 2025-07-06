package com.example.appreloj.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

@Composable
fun AccelerometerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val accelerometerSensor = remember { AccelerometerSensor(context) }

    var magnitude by remember { mutableStateOf(0f) }
    var isMeasuring by remember { mutableStateOf(false) }

    DisposableEffect(isMeasuring) {
        if (isMeasuring) {
            accelerometerSensor.onMagnitudeChanged = { mag ->
                magnitude = mag
                accelerometerSensor.enviarDatoAlTelefono("ACCEL:$mag")
            }
            accelerometerSensor.start()
        } else {
            accelerometerSensor.stop()
        }

        onDispose {
            accelerometerSensor.stop()
        }
    }

    Scaffold(
        timeText = { TimeText() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Magnitud Aceleración",
                fontSize = 14.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = String.format("%.2f", magnitude),
                fontSize = 24.sp,
                color = Color.Cyan
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { isMeasuring = !isMeasuring }) {
                Text(text = if (isMeasuring) "Detener" else "Iniciar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}
