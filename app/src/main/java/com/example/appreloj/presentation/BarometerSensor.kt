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

class BarometerSensor(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val pressureSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    var onPressureChanged: ((Float) -> Unit)? = null

    fun start() {
        pressureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PRESSURE) {
            val pressure = event.values[0]
            Log.d("BarometerSensor", "📡 Presión: $pressure")
            onPressureChanged?.invoke(pressure)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun enviarDatoAlTelefono(dato: String) {
        val messageClient: MessageClient = Wearable.getMessageClient(context)
        Wearable.getNodeClient(context).connectedNodes
            .addOnSuccessListener { nodes ->
                for (node in nodes) {
                    messageClient.sendMessage(node.id, "/sensor_data", dato.toByteArray(StandardCharsets.UTF_8))
                        .addOnSuccessListener {
                            Log.d("BarometerSensor", "✅ Enviado: $dato")
                        }
                        .addOnFailureListener {
                            Log.e("BarometerSensor", "❌ Error al enviar: $it")
                        }
                }
            }
    }
}
