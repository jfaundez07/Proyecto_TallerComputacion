package com.example.proyectojota

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.LayoutInflater
import com.example.proyectoapp2.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding = ActivityMainBinding.inflate(layoutInflater)

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
                    binding.dato1.text = "Error al realizar la solicitud: ${e.message}"
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
                        binding.dato2.text = formatData(datosSensoresList[1])
                        binding.dato3.text = formatData(datosSensoresList[2])
                        binding.dato4.text = formatData(datosSensoresList[2])
                        binding.dato5.text = formatData(datosSensoresList[2])
                        binding.dato6.text = formatData(datosSensoresList[2])
                    } else {
                        binding.dato1.text = "No se encontraron suficientes datos"
                    }
                }
            }
        })
    }

    // Método para formatear los datos
    private fun formatData(data: DatosSensores): String {
        return "Ultima lectura obtenida:\n\n" +
                "-> Temperatura: ${data.Temperatura} °C\n" +
                "-> Humedad: ${data.Humedad} %\n" +
                "-> PM25: ${data.PM25} [ug/m3]\n" +
                "-> PM10: ${data.PM10} [ug/m3]"
    }
    data class DatosSensores(
        val Temperatura: Double?,
        val Humedad: Double?,
        val PM25: Int?,
        val PM10: Int?
    )
}
