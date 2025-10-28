package com.diariosociosanitario.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.diariosociosanitario.R

class SeleccionHistorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccion_historial)

        val btnDiario = findViewById<Button>(R.id.btnResumenDiario)
        val btnMensual = findViewById<Button>(R.id.btnResumenMensual)

        btnDiario.setOnClickListener {
            val prefs = getSharedPreferences("ajustes", MODE_PRIVATE)
            val modoEdicion = prefs.getBoolean("modo_edicion", false)

            val intent = Intent(this, HistorialDiarioActivity::class.java)
            intent.putExtra("modo_edicion", modoEdicion)
            startActivity(intent)
        }

        btnMensual.setOnClickListener {
            startActivity(Intent(this, HistorialMensualActivity::class.java))
        }
    }
}