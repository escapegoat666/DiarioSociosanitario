package com.diariosociosanitario.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.diariosociosanitario.R

class AjustesActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        prefs = getSharedPreferences("ajustes", MODE_PRIVATE)

        val rbClaro = findViewById<RadioButton>(R.id.rbClaro)
        val rbOscuro = findViewById<RadioButton>(R.id.rbOscuro)
        val rbAuto = findViewById<RadioButton>(R.id.rbAuto)
        val switchEdicion = findViewById<Switch>(R.id.switchEdicion)

        // Cargar valores guardados
        when (prefs.getString("tema", "auto")) {
            "claro" -> rbClaro.isChecked = true
            "oscuro" -> rbOscuro.isChecked = true
            else -> rbAuto.isChecked = true
        }

        switchEdicion.isChecked = prefs.getBoolean("modo_edicion", false)

        // Guardar automáticamente el cambio del switch
        switchEdicion.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("modo_edicion", isChecked).apply()
        }

        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val editor = prefs.edit()
            editor.putString("tema", when {
                rbClaro.isChecked -> "claro"
                rbOscuro.isChecked -> "oscuro"
                else -> "auto"
            })
            editor.apply()
            Toast.makeText(this, "Reinicia la app para aplicar los ajustes", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.btnExportarPDF).setOnClickListener {
            startActivity(Intent(this, ExportarPdfActivity::class.java))
        }

        findViewById<Button>(R.id.btnBackup).setOnClickListener {
            startActivity(Intent(this, ExportarActivity::class.java))
        }
    }
}