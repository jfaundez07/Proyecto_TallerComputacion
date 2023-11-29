package com.example.proyectoapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.example.proyectoapp2.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 5000L // Intervalo de actualización en milisegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        realizarSolicitudHttp()

        binding.btnHistorial.setOnClickListener {

            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        binding.btnActualizar.setOnClickListener {
            realizarSolicitudHttp()
        }
        iniciarActualizacionAutomatica()

    }

    private fun realizarSolicitudHttp() {

        val url = "http://44.219.124.55:8081/GETLAST"
        val request = Request.Builder()
            .url(url)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.tvResultado.text = "Error al realizar la solicitud: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()


                if (jsonData != null) {
                    val gson = Gson()
                    val datosSensores = gson.fromJson(jsonData, DatosSensores::class.java)


                    val temperatura = datosSensores.Temperatura


                    if (temperatura != null) {
                        val resultadoFormateado =
                            " Ultima lectura obtenida: \n" +
                                    "\n" +
                                    "-> Temperatura:   $temperatura °C\n" +
                                    "-> Humedad:        ${datosSensores.Humedad} %\n" +
                                    "-> PM25:               ${datosSensores.PM25} [ug/m3]\n" +
                                    "-> PM10:               ${datosSensores.PM10} [ug/m3]"


                        runOnUiThread {
                            binding.tvResultado.text = resultadoFormateado
                        }


                        if (temperatura >= 30) {
                            val handler = Handler(Looper.getMainLooper())
                            handler.post {
                                mostrarAlerta("Temperatura Alta", "La temperatura es $temperatura °C")
                            }
                        }
                    } else {
                        runOnUiThread {
                            binding.tvResultado.text = "Error al analizar la respuesta del servidor"
                        }
                    }
                }
            }
        })
    }

    private fun iniciarActualizacionAutomatica() {
        // Ejecutar la solicitud HTTP automáticamente cada 5 segundos
        handler.postDelayed(object : Runnable {
            override fun run() {
                realizarSolicitudHttp()
                handler.postDelayed(this, updateInterval) // Llamar nuevamente después del intervalo
            }
        }, updateInterval) // Iniciar después del intervalo
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la actualización automática al destruir la actividad
        handler.removeCallbacksAndMessages(null)
    }

    private fun mostrarAlerta(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }


    data class DatosSensores(
        val Temperatura: Double?,
        val Humedad: Double?,
        val PM25: Int?,
        val PM10: Int?
    )
}