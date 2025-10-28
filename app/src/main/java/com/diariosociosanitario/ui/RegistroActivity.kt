package com.diariosociosanitario.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.diariosociosanitario.R

class RegistroActivity : AppCompatActivity() {

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var edtUsuario: EditText
    private lateinit var btnRegistrarEntrada: Button
    private lateinit var btnRegistrarSalida: Button
    private lateinit var prefs: SharedPreferences
    private var idioma: String = "es"

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Aplicar tema visual
        prefs = getSharedPreferences("ajustes", MODE_PRIVATE)
        when (prefs.getString("tema", "auto")) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        idioma = prefs.getString("idioma", "es") ?: "es"

        edtUsuario = findViewById(R.id.edtUsuario)
        btnRegistrarEntrada = findViewById(R.id.btnRegistrarEntrada)
        btnRegistrarSalida = findViewById(R.id.btnRegistrarSalida)

        // ✅ Traducir botones con flechas escritas
        btnRegistrarEntrada.text = if (idioma == "es") "→ Registrar entrada" else "→ Check in"
        btnRegistrarSalida.text = if (idioma == "es") "← Registrar salida" else "← Check out"
        edtUsuario.hint = if (idioma == "es") "Nombre del usuario" else "User name"

        btnRegistrarEntrada.setOnClickListener {
            val usuario = edtUsuario.text.toString().trim()
            if (usuario.isNotEmpty()) {
                viewModel.registrarEntrada(usuario) {
                    val mensaje = if (idioma == "es") "Entrada registrada correctamente" else "Check-in recorded"
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                val mensaje = if (idioma == "es") "Introduce el nombre del usuario" else "Enter the user's name"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            }
        }

        btnRegistrarSalida.setOnClickListener {
            mostrarDialogoObservaciones()
        }

        // ✅ Enlazar iconos inferiores
        findViewById<ImageView>(R.id.btnCreditos).setOnClickListener {
            startActivity(Intent(this, CreditosActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnAjustes).setOnClickListener {
            startActivity(Intent(this, AjustesActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnHistorial).setOnClickListener {
            startActivity(Intent(this, SeleccionHistorialActivity::class.java))
        }
    }

    private fun mostrarDialogoObservaciones() {
        val input = EditText(this)
        input.hint = if (idioma == "es") "Observaciones (opcional)" else "Observations (optional)"

        AlertDialog.Builder(this)
            .setTitle(if (idioma == "es") "Registrar salida" else "Register check-out")
            .setMessage(if (idioma == "es") "¿Deseas añadir alguna observación?" else "Would you like to add any observations?")
            .setView(input)
            .setPositiveButton(if (idioma == "es") "Registrar" else "Register") { _, _ ->
                val observaciones = input.text.toString().trim()
                viewModel.registrarSalida(observaciones) {
                    val mensaje = if (idioma == "es") "Salida registrada correctamente" else "Check-out recorded"
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton(if (idioma == "es") "Cancelar" else "Cancel", null)
            .show()
    }
}