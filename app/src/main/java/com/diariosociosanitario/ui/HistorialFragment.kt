package com.diariosociosanitario.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diariosociosanitario.data.VisitaEntity
import com.diariosociosanitario.databinding.FragmentHistorialBinding
import java.util.*
import kotlin.math.roundToInt

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HistorialViewModel
    private lateinit var adapter: VisitaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[HistorialViewModel::class.java]

        val modoEdicion = obtenerModoEdicion(requireContext())

        adapter = VisitaAdapter(
            modoEdicion,
            onGuardar = { visitaEditada ->
                val mesSeleccionado = binding.spinnerMes.selectedItemPosition + 1
                val añoSeleccionado = binding.spinnerAnio.selectedItem as Int
                viewModel.actualizar(visitaEditada) {
                    viewModel.obtenerPorMes(mesSeleccionado, añoSeleccionado) { visitas, resumen ->
                        adapter.submitList(visitas)
                        mostrarResumen(resumen)
                    }
                }
            },
            onEliminar = { visitaEliminada ->
                val mesSeleccionado = binding.spinnerMes.selectedItemPosition + 1
                val añoSeleccionado = binding.spinnerAnio.selectedItem as Int
                viewModel.eliminar(visitaEliminada) {
                    viewModel.obtenerPorMes(mesSeleccionado, añoSeleccionado) { visitas, resumen ->
                        adapter.submitList(visitas)
                        mostrarResumen(resumen)
                    }
                }
            }
        )

        binding.recyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistorial.adapter = adapter

        viewModel.obtenerTodo { visitas ->
            adapter.submitList(visitas)
            binding.txtResumenMes.text = ""
        }

        val meses = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        val mesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, meses)
        binding.spinnerMes.adapter = mesAdapter

        val añoActual = Calendar.getInstance().get(Calendar.YEAR)
        val años = (2023..añoActual).toList()
        val añoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, años)
        binding.spinnerAnio.adapter = añoAdapter

        binding.btnBuscarMes.setOnClickListener {
            val mesSeleccionado = binding.spinnerMes.selectedItemPosition + 1
            val añoSeleccionado = binding.spinnerAnio.selectedItem as Int

            viewModel.obtenerPorMes(mesSeleccionado, añoSeleccionado) { visitas, resumen ->
                adapter.submitList(visitas)
                mostrarResumen(resumen)
            }
        }
    }

    private fun mostrarResumen(resumen: HistorialViewModel.ResumenMes) {
        val resumenTexto = """
            📊 Resumen del mes:
            Total de usuarios visitados: ${resumen.totalUsuarios}
            Horas trabajadas: ${resumen.horasTrabajadasMin / 60}h ${resumen.horasTrabajadasMin % 60}m (${(resumen.horasTrabajadasMin / 60.0).roundToInt().toDouble()} h)
            Desplazamiento: ${resumen.desplazamientoMin / 60}h ${resumen.desplazamientoMin % 60}m (${(resumen.desplazamientoMin / 60.0).roundToInt().toDouble()} h)
            Total de horas: ${resumen.totalMin / 60}h ${resumen.totalMin % 60}m (${(resumen.totalMin / 60.0).roundToInt().toDouble()} h)
        """.trimIndent()

        binding.txtResumenMes.text = resumenTexto
    }

    private fun obtenerModoEdicion(context: Context): Boolean {
        val prefs = context.getSharedPreferences("ajustes", Context.MODE_PRIVATE)
        return prefs.getBoolean("modo_edicion", false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}