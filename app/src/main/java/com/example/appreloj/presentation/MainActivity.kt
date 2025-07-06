package com.example.appreloj.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.appreloj.presentation.theme.AppRelojTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppRelojTheme {
                var currentScreen by remember { mutableStateOf("main") }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    when (currentScreen) {
                        "main" -> HelloLokasScreen(
                            onNavigateToSecond = { currentScreen = "second" },
                            onNavigateToHeartRate = { currentScreen = "heartrate" },
                            onNavigateToAccelerometer = { currentScreen = "accelerometer" },
                            onNavigateToBarometer = { currentScreen = "barometer" }
                        )
                        "second" -> SecondScreenMain()
                        "heartrate" -> HeartRateScreen(onBack = { currentScreen = "main" })
                        "accelerometer" -> AccelerometerScreen(onBack = { currentScreen = "main" })
                        "barometer" -> BarometerScreen(onBack = { currentScreen = "main" })
                    }
                }
            }
        }
    }
}
