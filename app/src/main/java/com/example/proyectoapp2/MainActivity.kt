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
                val datosSensores: DatosSensores = gson.fromJson(jsonData, DatosSensores::class.java)
                runOnUiThread {
                    binding.dato1.text = jsonData
                }
            }
        })
    }
    data class DatosSensores(
        val Temperatura: Double?,
        val Humedad: Double?,
        val PM25: Int?,
        val PM10: Int?
    )
}
