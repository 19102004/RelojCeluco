package com.example.appreloj.presentation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay

@Composable
fun BarometerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val barometerSensor = remember { BarometerSensor(context) }

    var pressure by remember { mutableStateOf(0f) }
    var isMeasuring by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    // Iniciar temporizador de 6 segundos al medir
    LaunchedEffect(isMeasuring) {
        if (isMeasuring) {
            delay(6000)
            isMeasuring = false
            isFinished = true
        }
    }

    // Manejo del sensor
    DisposableEffect(isMeasuring) {
        if (isMeasuring) {
            barometerSensor.onPressureChanged = { p ->
                pressure = p
                barometerSensor.enviarDatoAlTelefono("PRESSURE:$pressure")
            }
            barometerSensor.start()
        } else {
            barometerSensor.stop()
        }

        onDispose {
            barometerSensor.stop()
        }
    }

    Scaffold(timeText = { TimeText() }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Presión Atmosférica:",
                color = Color.Black,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when {
                    isMeasuring -> "${pressure} hPa"
                    isFinished -> "Final: ${pressure} hPa"
                    else -> "--"
                },
                color = Color.Black,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    isMeasuring = true
                    isFinished = false
                    pressure = 0f
                },
                enabled = !isMeasuring,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(36.dp)
            ) {
                Text("Medir (6 seg)", fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(36.dp)
            ) {
                Text("Volver", fontSize = 10.sp)
            }
        }
    }
}
