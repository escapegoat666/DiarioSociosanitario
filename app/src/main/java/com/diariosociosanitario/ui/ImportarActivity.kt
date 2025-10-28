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
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class ImportarActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_IMPORTAR = 5001
    }

    private lateinit var btnImportar: Button
    private lateinit var viewModel: HistorialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_importar)

        btnImportar = findViewById(R.id.btnImportarJson)
        viewModel = ViewModelProvider(this)[HistorialViewModel::class.java]

        btnImportar.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
            }
            startActivityForResult(intent, REQUEST_CODE_IMPORTAR)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMPORTAR && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            importarDesdeJson(uri)
        }
    }

    private fun importarDesdeJson(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val contenido = reader.readText()
            reader.close()

            val gson = Gson()
            val json = JsonParser.parseString(contenido).asJsonObject

            // Validar estructura mínima
            if (!json.has("ajustes") || !json.has("visitas")) {
                Toast.makeText(this, "Archivo inválido: faltan claves 'ajustes' o 'visitas'", Toast.LENGTH_LONG).show()
                return
            }

            // Restaurar ajustes
            val ajustes = json.getAsJsonObject("ajustes")
            val prefs = getSharedPreferences("ajustes", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("idioma", ajustes.get("idioma")?.asString ?: "es")
            editor.putString("tema", ajustes.get("tema")?.asString ?: "auto")
            editor.putBoolean("edicion", ajustes.get("edicion")?.asBoolean ?: false)
            editor.apply()

            // Restaurar visitas válidas
            val visitasJson = gson.toJson(json.get("visitas"))
            val tipoVisitas = object : TypeToken<List<VisitaEntity>>() {}.type
            val visitas = gson.fromJson<List<VisitaEntity>>(visitasJson, tipoVisitas)

            val visitasValidas = visitas.filter { it.fecha.isNotBlank() && it.usuario.isNotBlank() }

            viewModel.insertarLista(visitasValidas) {
                Toast.makeText(this, "Se restauraron ${visitasValidas.size} visitas", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}