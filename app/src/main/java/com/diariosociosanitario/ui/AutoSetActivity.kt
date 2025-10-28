package com.diariosociosanitario.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.diariosociosanitario.R

class AutoSetActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private var idioma: String = "es"

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Aplicar tema visual
        prefs = getSharedPreferences("ajustes", Context.MODE_PRIVATE)
        when (prefs.getString("tema", "auto")) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_set)

        // ✅ Cargar idioma y nombre
        idioma = prefs.getString("idioma", "es") ?: "es"
        val nombre = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE)
            .getString("nombre_profesional", "Profesional")

        val txtNombre = findViewById<TextView>(R.id.nombreProfesional)
        txtNombre.text = if (idioma == "es") "Bienvenido, $nombre" else "Welcome, $nombre"
    }
}