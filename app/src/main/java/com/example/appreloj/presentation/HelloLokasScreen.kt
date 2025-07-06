package com.example.appreloj.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

@Composable
fun HelloLokasScreen(
    onNavigateToSecond: () -> Unit,
    onNavigateToHeartRate: () -> Unit,
    onNavigateToAccelerometer: () -> Unit,
    onNavigateToBarometer: () -> Unit
) {
    var greeting by remember { mutableStateOf("Presiona un ícono") }

    Scaffold(
        timeText = { TimeText() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.title3.copy(fontSize = 14.sp),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconSensorButton(
                        icon = Icons.Default.Favorite,
                        description = "Ritmo",
                        onClick = {
                            greeting = "Midiendo ritmo..."
                            onNavigateToHeartRate()
                        }
                    )

                    IconSensorButton(
                        icon = Icons.Default.DirectionsRun,
                        description = "Movimiento",
                        onClick = {
                            greeting = "Midiendo Movimiento..."
                            onNavigateToAccelerometer()
                        }
                    )

                    IconSensorButton(
                        icon = Icons.Default.Speed,
                        description = "Presión",
                        onClick = {
                            greeting = "Midiendo presión..."
                            onNavigateToBarometer()
                        }
                    )
                }

                IconSensorButton(
                    icon = Icons.Default.ArrowForward,
                    description = "Chismoso",
                    onClick = {
                        greeting = "Hola lokas"
                        onNavigateToSecond()
                    }
                )
            }
        }
    }
}

@Composable
fun IconSensorButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 6.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 11.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 50.dp)
        )
    }
}
