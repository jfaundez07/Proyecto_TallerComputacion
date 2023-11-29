package com.example.proyectoapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var btnActualizar: Button
    private lateinit var tvResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnActualizar = findViewById(R.id.btnActualizar)
        tvResultado = findViewById(R.id.tvResultado)

        btnActualizar.setOnClickListener {
            realizarSolicitudHttp()
        }
    }

    private fun realizarSolicitudHttp() {

        val url = "http://44.219.124.55:8081/GETLAST"
        val request = Request.Builder()
            .url(url)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    tvResultado.text = "Error al realizar la solicitud: ${e.message}"
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
                            tvResultado.text = resultadoFormateado
                        }

                        // Verificar si la temperatura es 55 o superior
                        if (temperatura >= 35) {
                            // Mostrar una alerta o ventana emergente
                            mostrarAlerta("Temperatura Alta", "La temperatura es $temperatura °C. ¡Cuidado!")
                        }
                    } else {
                        runOnUiThread {
                            tvResultado.text = "Error al analizar la respuesta del servidor"
                        }
                    }
                }
            }

        })
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