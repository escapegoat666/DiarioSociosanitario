package com.diariosociosanitario.ui

import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.diariosociosanitario.R
import com.diariosociosanitario.data.VisitaEntity
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportarPdfActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_PDF = 3001
    }

    private lateinit var spinnerMes: Spinner
    private lateinit var spinnerAnio: Spinner
    private lateinit var btnExportarPdf: Button
    private lateinit var viewModel: HistorialViewModel
    private var contenidoPdf: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exportar_pdf)

        spinnerMes = findViewById(R.id.spinnerMes)
        spinnerAnio = findViewById(R.id.spinnerAnio)
        btnExportarPdf = findViewById(R.id.btnExportarPdf)
        viewModel = ViewModelProvider(this)[HistorialViewModel::class.java]

        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        spinnerMes.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, meses)

        val añoActual = Calendar.getInstance().get(Calendar.YEAR)
        val años = (2023..añoActual).toList()
        spinnerAnio.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, años)

        val mesActual = Calendar.getInstance().get(Calendar.MONTH)
        spinnerMes.setSelection(mesActual)
        spinnerAnio.setSelection(años.indexOf(añoActual))

        btnExportarPdf.setOnClickListener {
            val mes = spinnerMes.selectedItemPosition + 1
            val año = spinnerAnio.selectedItem as Int

            viewModel.obtenerPorMes(mes, año) { visitas, resumenMensual ->
                contenidoPdf = generarContenidoPdf(visitas, resumenMensual)
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_TITLE, "visitas_${mes}_${año}.pdf")
                }
                startActivityForResult(intent, REQUEST_CODE_PDF)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PDF && resultCode == RESULT_OK) {
            val uri: Uri = data?.data ?: return
            generarPdf(uri)
        }
    }

    private fun generarPdf(uri: Uri) {
        val documento = PdfDocument()
        val paginaInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val pagina = documento.startPage(paginaInfo)

        val canvas = pagina.canvas
        val paint = Paint().apply { textSize = 14f }

        val lineas = contenidoPdf.split("\n")
        var y = 50f
        for (linea in lineas) {
            canvas.drawText(linea, 40f, y, paint)
            y += 24f
            if (y > 800f) break
        }

        documento.finishPage(pagina)

        try {
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            outputStream?.use {
                documento.writeTo(it)
            }
            Toast.makeText(this, "PDF exportado correctamente", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            documento.close()
        }
    }

    private fun generarContenidoPdf(visitas: List<VisitaEntity>, resumenMensual: HistorialViewModel.ResumenMes): String {
        val builder = StringBuilder()
        builder.appendLine("DIARIO SOCIOSANITARIO")
        builder.appendLine("=====================")
        builder.appendLine()

        val visitasPorDia = visitas.groupBy { it.fecha }
        val fechasOrdenadas = visitasPorDia.keys.sorted()

        for (fecha in fechasOrdenadas) {
            builder.appendLine("📅 Día: $fecha")
            builder.appendLine("------------------")

            val visitasDelDia = visitasPorDia[fecha]!!.sortedBy { it.horaEntrada }

            for (visita in visitasDelDia) {
                val entrada = visita.horaEntrada ?: "-"
                val salida = visita.horaSalida ?: "-"
                val observaciones = visita.observaciones ?: "-"
                builder.appendLine("- ${visita.usuario}: $entrada → $salida | Observaciones: $observaciones")
            }

            val resumenDiario = calcularResumen(visitasDelDia)
            val ht = String.format("%.2f", resumenDiario.horasTrabajadasMin / 60.0)
            val dt = String.format("%.2f", resumenDiario.desplazamientoMin / 60.0)
            val tt = String.format("%.2f", resumenDiario.totalMin / 60.0)

            builder.appendLine()
            builder.appendLine("🧮 Resumen diario:")
            builder.appendLine("Usuarios visitados: ${resumenDiario.totalUsuarios}")
            builder.appendLine("Horas trabajadas: ${resumenDiario.horasTrabajadasMin / 60}h ${resumenDiario.horasTrabajadasMin % 60}m ($ht h)")
            builder.appendLine("Desplazamiento: ${resumenDiario.desplazamientoMin / 60}h ${resumenDiario.desplazamientoMin % 60}m ($dt h)")
            builder.appendLine("Total: ${resumenDiario.totalMin / 60}h ${resumenDiario.totalMin % 60}m ($tt h)")
            builder.appendLine()
        }

        val ht = String.format("%.2f", resumenMensual.horasTrabajadasMin / 60.0)
        val dt = String.format("%.2f", resumenMensual.desplazamientoMin / 60.0)
        val tt = String.format("%.2f", resumenMensual.totalMin / 60.0)

        builder.appendLine("📊 Resumen mensual")
        builder.appendLine("==================")
        builder.appendLine("Usuarios totales: ${resumenMensual.totalUsuarios}")
        builder.appendLine("Horas trabajadas: ${resumenMensual.horasTrabajadasMin / 60}h ${resumenMensual.horasTrabajadasMin % 60}m ($ht h)")
        builder.appendLine("Desplazamiento: ${resumenMensual.desplazamientoMin / 60}h ${resumenMensual.desplazamientoMin % 60}m ($dt h)")
        builder.appendLine("Total acumulado: ${resumenMensual.totalMin / 60}h ${resumenMensual.totalMin % 60}m ($tt h)")

        return builder.toString()
    }

    private fun calcularResumen(visitas: List<VisitaEntity>): HistorialViewModel.ResumenMes {
        val usuariosUnicos = visitas.map { it.usuario }.toSet().size
        var trabajadas = 0
        var desplazamiento = 0

        val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
        val ordenadas = visitas.sortedBy { it.horaEntrada }

        for (i in ordenadas.indices) {
            val visita = ordenadas[i]
            val entrada = visita.horaEntrada
            val salida = visita.horaSalida

            if (entrada != null && salida != null) {
                val hEntrada = formato.parse(entrada)
                val hSalida = formato.parse(salida)
                val minutos = ((hSalida.time - hEntrada.time) / 60000).toInt()
                if (minutos > 0) trabajadas += minutos
            }

            if (i > 0) {
                val anterior = ordenadas[i - 1]
                val salidaAnterior = anterior.horaSalida
                if (salidaAnterior != null && entrada != null) {
                    val hSalidaAnterior = formato.parse(salidaAnterior)
                    val hEntradaActual = formato.parse(entrada)
                    val entreVisitas = ((hEntradaActual.time - hSalidaAnterior.time) / 60000).toInt()
                    if (entreVisitas > 0) desplazamiento += entreVisitas
                }
            }
        }

        val total = trabajadas + desplazamiento
        return HistorialViewModel.ResumenMes(usuariosUnicos, trabajadas, desplazamiento, total)
    }
}