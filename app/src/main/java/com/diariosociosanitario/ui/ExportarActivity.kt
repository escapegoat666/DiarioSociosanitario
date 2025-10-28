package com.diariosociosanitario.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.diariosociosanitario.R
import com.diariosociosanitario.data.VisitaEntity
import com.google.gson.Gson
import java.io.OutputStream

class ExportarActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_BACKUP = 4001
    }

    private lateinit var btnExportar: Button
    private lateinit var btnImportar: Button
    private lateinit var viewModel: HistorialViewModel
    private var contenidoBackup: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exportar)

        btnExportar = findViewById(R.id.btnExportar)
        btnImportar = findViewById(R.id.btnImportarJson)
        viewModel = ViewModelProvider(this)[HistorialViewModel::class.java]

        btnExportar.setOnClickListener {
            generarContenidoBackup()
        }

        btnImportar.setOnClickListener {
            val intent = Intent(this, ImportarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun generarContenidoBackup() {
        val prefs = getSharedPreferences("ajustes", MODE_PRIVATE)
        val ajustes = mapOf(
            "idioma" to prefs.getString("idioma", "es"),
            "tema" to prefs.getString("tema", "auto"),
            "edicion" to prefs.getBoolean("edicion", false)
        )

        viewModel.obtenerTodo { visitas ->
            val backup = mapOf(
                "ajustes" to ajustes,
                "visitas" to visitas
            )
            contenidoBackup = Gson().toJson(backup)
            solicitarRutaBackup()
        }
    }

    private fun solicitarRutaBackup() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "copia_seguridad_${System.currentTimeMillis()}.json")
        }
        startActivityForResult(intent, REQUEST_CODE_BACKUP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_BACKUP && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            try {
                val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                outputStream?.use {
                    it.write(contenidoBackup.toByteArray())
                }
                Toast.makeText(this, "Copia de seguridad exportada", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}