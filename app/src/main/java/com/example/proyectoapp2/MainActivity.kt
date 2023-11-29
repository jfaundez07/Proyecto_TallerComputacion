    package com.example.proyectoapp2

    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper

    import android.widget.Button
    import android.widget.TextView
    import androidx.appcompat.app.AlertDialog
    import com.google.gson.Gson
    import okhttp3.*
    import java.io.IOException


    class MainActivity : AppCompatActivity() {

        private lateinit var btnActualizar: Button
        private lateinit var btnHistorial: Button
        private lateinit var tvResultado: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            btnActualizar = findViewById(R.id.btnActualizar)
            btnHistorial = findViewById(R.id.btnHistorial)
            tvResultado = findViewById(R.id.tvResultado)

            btnActualizar.setOnClickListener {
                realizarSolicitudHttp()
            }

            btnHistorial.setOnClickListener {
                // Crear un Intent para iniciar la actividad de historial
                val intent = Intent(this, MainActivity::class.java)

                // Puedes agregar datos adicionales al intent si es necesario
                // intent.putExtra("clave", valor)

                // Iniciar la actividad de historial
                startActivity(intent)
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

                            if (temperatura >= 50) {
                                val handler = Handler(Looper.getMainLooper())
                                handler.post {
                                    mostrarAlerta("Temperatura Alta", "La temperatura es $temperatura °C")
                                }
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
