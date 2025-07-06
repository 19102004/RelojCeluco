package com.example.appreloj

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.gms.wearable.*
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    private lateinit var heartRateText: TextView
    private lateinit var barometerText: TextView
    private lateinit var accelerometerText: TextView
    private lateinit var saveButton: Button

    private var lastHeartRate: Float? = null
    private var lastBarometer: Float? = null
    private var lastAccelerometer: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        heartRateText = findViewById(R.id.heartRateText)
        barometerText = findViewById(R.id.barometerText)
        accelerometerText = findViewById(R.id.accelerometerText)
        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            Log.d("BOTON", "🔘 Botón Almacenar presionado")
            enviarDatosAlServidor()
        }
    }

    override fun onResume() {
        super.onResume()
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/sensor_data") {
            val mensaje = String(event.data, Charsets.UTF_8)
            Log.d("CELUCO", "📩 Mensaje recibido: ${event.path} -> $mensaje")

            runOnUiThread {
                when {
                    mensaje.startsWith("HEART") -> {
                        val valor = mensaje.split(":").getOrNull(1)?.toFloatOrNull()
                        valor?.let {
                            lastHeartRate = it
                            heartRateText.text = "Frecuencia cardíaca: $it bpm"
                        }
                    }
                    mensaje.startsWith("PRESSURE") -> {
                        val valor = mensaje.split(":").getOrNull(1)?.toFloatOrNull()
                        valor?.let {
                            lastBarometer = it
                            barometerText.text = "Presión barométrica: $it hPa"
                        }
                    }
                    mensaje.startsWith("ACCEL") -> {
                        val valor = mensaje.split(":").getOrNull(1)?.toFloatOrNull()
                        valor?.let {
                            lastAccelerometer = it
                            accelerometerText.text = "Acelerómetro: $it"
                        }
                    }
                }
            }
        }
    }

    private fun enviarDatosAlServidor() {
        val heart = lastHeartRate
        val pressure = lastBarometer
        val accel = lastAccelerometer

        Log.d("SAVE", "Datos actuales -> HR: $heart | PRES: $pressure | ACC: $accel")

        if (heart == null && pressure == null && accel == null) {
            Log.w("SAVE", "❌ No hay datos para enviar")
            runOnUiThread {
                Toast.makeText(this, "No hay datos para enviar", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val json = JSONObject().apply {
            heart?.let { put("heartRate", it) }
            pressure?.let { put("pressure", it) }
            accel?.let { put("acceleration", it) }
        }

        thread {
            try {
                val url = URL("http://192.168.8.10:4000/api/sensory")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val output: OutputStream = conn.outputStream
                output.write(json.toString().toByteArray(Charsets.UTF_8))
                output.flush()
                output.close()

                val responseCode = conn.responseCode
                Log.d("SAVE", "✅ Enviado a MongoDB. Código: $responseCode")

                runOnUiThread {
                    if (responseCode in 200..299) {
                        Toast.makeText(this, "Enviado con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al enviar: Código $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

                conn.disconnect()
            } catch (e: Exception) {
                Log.e("SAVE", "❌ Error al enviar: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error al enviar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
