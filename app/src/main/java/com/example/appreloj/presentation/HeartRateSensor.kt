package com.example.appreloj.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import java.nio.charset.StandardCharsets

class HeartRateSensor(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    var onHeartRateChanged: ((Float) -> Unit)? = null

    fun start() {
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val heartRate = event.values[0]

            if (heartRate > 0) {
                Log.d("HeartRateSensor", "💓 Ritmo cardíaco válido: $heartRate")
                onHeartRateChanged?.invoke(heartRate)
                enviarDatoAlTelefono("HEART: $heartRate")
            } else {
                Log.d("HeartRateSensor", "⚠ Ritmo cardíaco no válido (0 o negativo), no se envía")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun enviarDatoAlTelefono(dato: String) {
        val messageClient: MessageClient = Wearable.getMessageClient(context)
        Wearable.getNodeClient(context).connectedNodes
            .addOnSuccessListener { nodes ->
                for (node in nodes) {
                    messageClient.sendMessage(node.id, "/sensor_data", dato.toByteArray(StandardCharsets.UTF_8))
                        .addOnSuccessListener {
                            Log.d("HeartRateSensor", "✅ Enviado: $dato")
                        }
                        .addOnFailureListener {
                            Log.e("HeartRateSensor", "❌ Error al enviar: $it")
                        }
                }
            }
    }
}
