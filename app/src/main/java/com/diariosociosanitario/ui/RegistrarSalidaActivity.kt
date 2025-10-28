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
import java.text.SimpleDateFormat
import java.util.*

class RegistrarSalidaActivity : AppCompatActivity() {

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var edtObservaciones: EditText
    private lateinit var btnRegistrarSalida: Button
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
        setContentView(R.layout.activity_registrar_salida)

        idioma = prefs.getString("idioma", "es") ?: "es"

        edtObservaciones = findViewById(R.id.edtObservaciones)
        btnRegistrarSalida = findViewById(R.id.btnRegistrarSalida)

        btnRegistrarSalida.text = if (idioma == "es") "Guardar salida" else "Save exit"

        btnRegistrarSalida.setOnClickListener {
            val observaciones = edtObservaciones.text.toString().trim()
            val horaActual = obtenerHoraActual()
            val fechaActual = obtenerFechaActual()

            viewModel.obtenerUltimaEntradaSinSalida { visita ->
                if (visita != null) {
                    visita.horaSalida = horaActual
                    visita.fecha = fechaActual
                    visita.observaciones = observaciones

                    viewModel.actualizar(visita) {
                        val mensaje = if (idioma == "es") "Salida registrada" else "Exit saved"
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    val mensaje = if (idioma == "es") "No hay entrada pendiente" else "No pending entry found"
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun obtenerFechaActual(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun obtenerHoraActual(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}