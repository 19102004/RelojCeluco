package com.example.appreloj.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.*
import java.nio.charset.StandardCharsets

@Composable
fun HeartRateScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val heartRateSensor = remember { HeartRateSensor(context) }

    var heartRate by remember { mutableStateOf(0f) }
    var isMeasuring by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }

    // Pedir permiso para BODY_SENSORS
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BODY_SENSORS),
                1001
            )
        }
    }

    // Temporizador 1 min
    LaunchedEffect(isMeasuring) {
        if (isMeasuring) {
            kotlinx.coroutines.delay(60_000)
            isMeasuring = false
            isFinished = true
        }
    }

    DisposableEffect(isMeasuring) {
        if (isMeasuring) {
            heartRateSensor.onHeartRateChanged = { hr ->
                heartRate = hr
            }
            heartRateSensor.start()
        } else {
            heartRateSensor.stop()
        }

        onDispose {
            heartRateSensor.stop()
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
                text = "Ritmo Cardíaco:",
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isMeasuring) "${heartRate.toInt()} bpm"
                else if (isFinished) "Final: ${heartRate.toInt()} bpm"
                else "-- bpm",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    isMeasuring = true
                    isFinished = false
                    heartRate = 0f
                },
                enabled = !isMeasuring,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(36.dp)
            ) {
                Text(
                    text = "Medir (1 min)",
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(36.dp)
            ) {
                Text("Volver", fontSize = 10.sp)
            }
        }
    }
}
