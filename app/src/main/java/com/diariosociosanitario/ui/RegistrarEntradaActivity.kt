package com.diariosociosanitario.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.diariosociosanitario.R

class RegistrarEntradaActivity : AppCompatActivity() {

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var edtUsuarioEntrada: EditText
    private lateinit var btnRegistrarEntrada: Button
    private lateinit var prefs: SharedPreferences
    private var idioma: String = "es"

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = getSharedPreferences("ajustes", MODE_PRIVATE)
        when (prefs.getString("tema", "auto")) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_entrada)

        idioma = prefs.getString("idioma", "es") ?: "es"

        edtUsuarioEntrada = findViewById(R.id.edtUsuarioEntrada)
        btnRegistrarEntrada = findViewById(R.id.btnRegistrarEntrada)

        btnRegistrarEntrada.text = if (idioma == "es") "Guardar entrada" else "Save entry"

        btnRegistrarEntrada.setOnClickListener {
            val usuario = edtUsuarioEntrada.text.toString().trim()

            if (usuario.isEmpty()) {
                val mensaje = if (idioma == "es") "Introduce el nombre del usuario" else "Enter user name"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.registrarEntrada(usuario) {
                val mensaje = if (idioma == "es") "Entrada registrada" else "Entry saved"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}