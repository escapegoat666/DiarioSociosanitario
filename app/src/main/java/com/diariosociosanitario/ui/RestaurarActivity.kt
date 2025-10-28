package com.diariosociosanitario.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.diariosociosanitario.R
import com.diariosociosanitario.data.VisitaEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class RestaurarActivity : AppCompatActivity() {

    private lateinit var btnRestaurar: Button
    private lateinit var viewModel: HistorialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurar)

        btnRestaurar = findViewById(R.id.btnRestaurar)
        viewModel = ViewModelProvider(this)[HistorialViewModel::class.java]

        btnRestaurar.setOnClickListener {
            abrirSelectorDeArchivo()
        }
    }

    private val selectorArchivo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                restaurarDesdeJson(uri)
            } else {
                Toast.makeText(this, "No se seleccionó ningún archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirSelectorDeArchivo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        selectorArchivo.launch(intent)
    }

    private fun restaurarDesdeJson(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val json = reader.readText()
            reader.close()

            val tipoLista = object : TypeToken<List<VisitaEntity>>() {}.type
            val visitas: List<VisitaEntity> = Gson().fromJson(json, tipoLista)

            viewModel.insertarLista(visitas) {
                Toast.makeText(this, "Restauración completada (${visitas.size} registros)", Toast.LENGTH_LONG).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al restaurar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}