package com.diariosociosanitario.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.diariosociosanitario.R

class CreditosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creditos)
        title = "Créditos"

        val txtCreditos = findViewById<TextView>(R.id.txtCreditos)
        val version = packageManager.getPackageInfo(packageName, 0).versionName

        val contenido = """
            Diario Sociosanitario
            Versión: $version

            Desarrollado por: Héctor López (escapegoat)
            Contacto: helobe@outlook.com
            Año: 2025

            Esta aplicación ha sido creada para facilitar el registro de visitas, tiempos de trabajo y desplazamientos en el ámbito sociosanitario. Todos los datos se almacenan localmente y pueden exportarse en formato PDF o JSON.

            Tecnologías utilizadas:
            - Kotlin
            - Android Jetpack (ViewModel, LiveData, Room, Navigation)
            - Coroutines
            - Material Design
            - Fragments
            - RecyclerView
            - SharedPreferences
            - iText PDF

            Licencias y dependencias:
            - iText PDF (AGPL v3): generación de documentos PDF
            - AndroidX Room: persistencia de datos locales
            - Kotlin Coroutines: operaciones asincrónicas
            - Android Jetpack: arquitectura MVVM y componentes de UI

            Agradecimientos:
            A Agnieszka por enseñarme el mundo de los yayos.

            Gracias por usarla.
        """.trimIndent()

        txtCreditos.text = contenido
    }
}