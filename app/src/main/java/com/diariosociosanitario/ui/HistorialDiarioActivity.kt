package com.diariosociosanitario.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diariosociosanitario.databinding.ActivityHistorialDiarioBinding
import java.util.*

class HistorialDiarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialDiarioBinding
    private lateinit var viewModel: HistorialViewModel
    private lateinit var adapter: VisitaAdapter

    private var fechaSeleccionada: String? = null // ✅ NUEVO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialDiarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HistorialViewModel::class.java]

        val modoEdicion = intent.getBooleanExtra("modo_edicion", false)

        adapter = VisitaAdapter(
            modoEdicion,
            onGuardar = { visitaEditada ->
                viewModel.actualizar(visitaEditada) {
                    recargarResumenDiario()
                }
            },
            onEliminar = { visitaEliminada ->
                viewModel.eliminar(visitaEliminada) {
                    recargarResumenDiario()
                }
            }
        )

        binding.recyclerVisitas.layoutManager = LinearLayoutManager(this)
        binding.recyclerVisitas.adapter = adapter

        binding.btnSeleccionarFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    val fecha = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    fechaSeleccionada = fecha // ✅ GUARDAMOS LA FECHA
                    cargarResumenParaFecha(fecha)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun cargarResumenParaFecha(fecha: String) {
        viewModel.obtenerPorFecha(fecha) { visitas, resumen ->
            binding.txtResumen.text = resumenTexto(resumen)
            binding.txtResumen.visibility = View.VISIBLE
            adapter.submitList(visitas)
            binding.recyclerVisitas.visibility = View.VISIBLE
        }
    }

    private fun recargarResumenDiario() {
        binding.txtResumen.visibility = View.GONE
        binding.recyclerVisitas.visibility = View.GONE
        fechaSeleccionada?.let {
            cargarResumenParaFecha(it) // ✅ RECARGAMOS SIN PEDIR FECHA
        } ?: binding.btnSeleccionarFecha.performClick()
    }

    private fun resumenTexto(resumen: HistorialViewModel.ResumenMes): String {
        val horasTrabajadasDecimal = String.format("%.2f", resumen.horasTrabajadasMin / 60.0)
        val desplazamientoDecimal = String.format("%.2f", resumen.desplazamientoMin / 60.0)
        val totalDecimal = String.format("%.2f", resumen.totalMin / 60.0)

        return """
            📊 Resumen diario:
            Total de usuarios visitados: ${resumen.totalUsuarios}
            Horas trabajadas: ${resumen.horasTrabajadasMin / 60}h ${resumen.horasTrabajadasMin % 60}m (${horasTrabajadasDecimal} h)
            Desplazamiento: ${resumen.desplazamientoMin / 60}h ${resumen.desplazamientoMin % 60}m (${desplazamientoDecimal} h)
            Total de horas: ${resumen.totalMin / 60}h ${resumen.totalMin % 60}m (${totalDecimal} h)
        """.trimIndent()
    }
}