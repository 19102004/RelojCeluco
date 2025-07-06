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
import kotlin.math.sqrt

class AccelerometerSensor(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var onMagnitudeChanged: ((Float) -> Unit)? = null
    private val alpha = 0.8f
    private var gravity = FloatArray(3) { 0f }

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            gravity[0] = alpha * gravity[0] + (1 - alpha) * x
            gravity[1] = alpha * gravity[1] + (1 - alpha) * y
            gravity[2] = alpha * gravity[2] + (1 - alpha) * z

            val linearX = x - gravity[0]
            val linearY = y - gravity[1]
            val linearZ = z - gravity[2]

            val magnitude = sqrt(linearX * linearX + linearY * linearY + linearZ * linearZ)

            if (magnitude > 0.5f) { // filtro para evitar ruido muy bajo
                Log.d("AccelerometerSensor", "📡 Magnitud: $magnitude")
                onMagnitudeChanged?.invoke(magnitude)
            }
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
                            Log.d("AccelerometerSensor", "✅ Enviado: $dato")
                        }
                        .addOnFailureListener {
                            Log.e("AccelerometerSensor", "❌ Error al enviar: $it")
                        }
                }
            }
    }
}
