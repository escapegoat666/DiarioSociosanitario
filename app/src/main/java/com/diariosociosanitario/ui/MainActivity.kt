package com.diariosociosanitario.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.diariosociosanitario.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Aplicar tema visual antes de mostrar la pantalla
        val prefs: SharedPreferences = getSharedPreferences("ajustes", MODE_PRIVATE)
        when (prefs.getString("tema", "auto")) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Aplicar idioma en fragmentos (se usará en MainFragment)
        // No se asignan textos aquí porque los botones ya no están en este layout
        // El idioma se recuperará desde SharedPreferences dentro del fragmento
    }
}