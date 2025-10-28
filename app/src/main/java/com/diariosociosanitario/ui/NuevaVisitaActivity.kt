package com.diariosociosanitario.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.diariosociosanitario.R
import com.diariosociosanitario.data.VisitaEntity
import java.text.SimpleDateFormat
import java.util.*

class NuevaVisitaActivity : AppCompatActivity() {

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var edtFecha: EditText
    private lateinit var edtUsuario: EditText
    private lateinit var edtEntrada: EditText
    private lateinit var edtSalida: EditText
    private lateinit var edtDesplazamiento: EditText
    private lateinit var edtObservaciones: EditText
    private lateinit var btnGuardar: Button
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
        setContentView(R.layout.activity_nueva_visita)

        idioma = prefs.getString("idioma", "es") ?: "es"

        edtFecha = findViewById(R.id.edtFecha)
        edtUsuario = findViewById(R.id.edtUsuario)
        edtEntrada = findViewById(R.id.edtEntrada)
        edtSalida = findViewById(R.id.edtSalida)
        edtDesplazamiento = findViewById(R.id.edtDesplazamiento)
        edtObservaciones = findViewById(R.id.edtObservaciones)
        btnGuardar = findViewById(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            val fecha = edtFecha.text.toString().trim()
            val usuario = edtUsuario.text.toString().trim()
            val entrada = edtEntrada.text.toString().trim()
            val salida = edtSalida.text.toString().trim()
            val desplazamiento = edtDesplazamiento.text.toString().trim()
            val observaciones = edtObservaciones.text.toString().trim()

            if (fecha.isNotEmpty() && usuario.isNotEmpty() && entrada.isNotEmpty()) {
                val nuevaVisita = VisitaEntity(
                    fecha = fecha,
                    usuario = usuario,
                    horaEntrada = entrada,
                    horaSalida = if (salida.isNotEmpty()) salida else null,
                    desplazamiento = if (desplazamiento.isNotEmpty()) desplazamiento else null,
                    observaciones = if (observaciones.isNotEmpty()) observaciones else null
                )

                viewModel.registrarVisitaCompleta(nuevaVisita) {
                    runOnUiThread {
                        val mensaje = if (idioma == "es") "Visita guardada" else "Visit saved"
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                val mensaje = if (idioma == "es") "Completa los campos obligatorios" else "Please fill in required fields"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }
}