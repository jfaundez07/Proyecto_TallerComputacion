package com.example.proyectoapp2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding : HistorialActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HistorialActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnActualizar.setOnClickListener {
            realizarSolicitudHttp()
        }
    }

    private fun realizarSolicitudHttp() {
        val url = "http://44.219.124.55:8081/GETLASTTEN"
        val request = Request.Builder()
            .url(url)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding. = "Error al realizar la solicitud: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                val gson = Gson()
                val datosSensoresList: List<DatosSensores> = gson.fromJson(
                    jsonData,
                    object : TypeToken<List<DatosSensores>>() {}.type
                )

                runOnUiThread {
                    if (datosSensoresList.size >= 10) {
                        binding.dato1.text = formatData(datosSensoresList[0])
                        binding.dato1.textSize = 14f
                        binding.dato2.text = formatData(datosSensoresList[1])
                        binding.dato2.textSize = 14f
                        binding.dato3.text = formatData(datosSensoresList[2])
                        binding.dato3.textSize = 14f
                        binding.dato4.text = formatData(datosSensoresList[3])
                        binding.dato4.textSize = 14f
                        binding.dato5.text = formatData(datosSensoresList[4])
                        binding.dato5.textSize = 14f
                        binding.dato6.text = formatData(datosSensoresList[5])
                        binding.dato6.textSize = 14f
                        binding.dato7.text = formatData(datosSensoresList[7])
                        binding.dato7.textSize = 14f
                        binding.dato8.text = formatData(datosSensoresList[7])
                        binding.dato8.textSize = 14f
                    } else {
                        binding.dato1.text = "No se encontraron suficientes datos"
                    }
                }
            }
        })
    }

    // Método para formatear los datos
    private fun formatData(data: DatosSensores): String {
        return "Temperatura: ${data.Temperatura} °C" +
                "Humedad: ${data.Humedad} %\n" +
                "PM25: ${data.PM25} [ug/m3]" +
                "PM10: ${data.PM10} [ug/m3]"
    }
    data class DatosSensores(
        val Temperatura: Double?,
        val Humedad: Double?,
        val PM25: Int?,
        val PM10: Int?
    )
}