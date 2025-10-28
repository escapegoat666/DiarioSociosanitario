package com.diariosociosanitario.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diariosociosanitario.databinding.ActivityHistorialMensualBinding
import java.util.*

class HistorialMensualActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialMensualBinding
    private lateinit var viewModel: HistorialViewModel
    private lateinit var adapter: VisitaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialMensualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HistorialViewModel::class.java]
        val modoEdicion = obtenerModoEdicion()

        adapter = VisitaAdapter(
            modoEdicion,
            onGuardar = { visitaEditada ->
                recargarResumenMensual()
            },
            onEliminar = { visitaEliminada ->
                viewModel.eliminar(visitaEliminada) {
                    recargarResumenMensual()
                }
            }
        )

        binding.recyclerVisitas.layoutManager = LinearLayoutManager(this)
        binding.recyclerVisitas.adapter = adapter

        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        binding.spinnerMes.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, meses)

        val añoActual = Calendar.getInstance().get(Calendar.YEAR)
        val años = (2025..añoActual).toList()
        binding.spinnerAnio.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, años)

        val mesActual = Calendar.getInstance().get(Calendar.MONTH)
        binding.spinnerMes.setSelection(mesActual)
        binding.spinnerAnio.setSelection(años.indexOf(añoActual))

        binding.btnCargarMes.setOnClickListener {
            recargarResumenMensual()
        }

        recargarResumenMensual()
    }

    private fun recargarResumenMensual() {
        val mes = binding.spinnerMes.selectedItemPosition + 1
        val año = binding.spinnerAnio.selectedItem as Int

        viewModel.obtenerPorMes(mes, año) { visitas, resumen ->
            adapter.submitList(visitas)
            mostrarResumen(resumen)
        }
    }

    private fun mostrarResumen(resumen: HistorialViewModel.ResumenMes) {
        val horasTrabajadasDecimal = String.format("%.2f", resumen.horasTrabajadasMin / 60.0)
        val desplazamientoDecimal = String.format("%.2f", resumen.desplazamientoMin / 60.0)
        val totalDecimal = String.format("%.2f", resumen.totalMin / 60.0)

        val texto = """
            📊 Resumen del mes:
            Total de usuarios visitados: ${resumen.totalUsuarios}
            Horas trabajadas: ${resumen.horasTrabajadasMin / 60}h ${resumen.horasTrabajadasMin % 60}m (${horasTrabajadasDecimal} h)
            Desplazamiento: ${resumen.desplazamientoMin / 60}h ${resumen.desplazamientoMin % 60}m (${desplazamientoDecimal} h)
            Total de horas: ${resumen.totalMin / 60}h ${resumen.totalMin % 60}m (${totalDecimal} h)
        """.trimIndent()

        binding.txtResumen.text = texto
        binding.txtResumen.visibility = android.view.View.VISIBLE
    }

    private fun obtenerModoEdicion(): Boolean {
        val prefs = getSharedPreferences("ajustes", MODE_PRIVATE)
        return prefs.getBoolean("modo_edicion", false)
    }
}