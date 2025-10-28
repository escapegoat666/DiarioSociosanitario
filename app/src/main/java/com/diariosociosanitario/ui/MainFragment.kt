package com.diariosociosanitario.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diariosociosanitario.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("registros", AppCompatActivity.MODE_PRIVATE)
        val viewModel = ViewModelProvider(requireActivity())[HistorialViewModel::class.java]

        binding.btnEntrada.setOnClickListener {
            val estado = prefs.getString("estado_visita", "salida")
            if (estado == "entrada") {
                Toast.makeText(requireContext(), "Ya hay una entrada registrada. Debes registrar una salida antes.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val input = EditText(requireContext())
            input.hint = "Nombre del usuario a visitar"

            AlertDialog.Builder(requireContext())
                .setTitle("Registrar entrada")
                .setView(input)
                .setPositiveButton("Registrar") { _, _ ->
                    val nombre = input.text.toString().trim()
                    if (nombre.isEmpty()) {
                        Toast.makeText(requireContext(), "Nombre obligatorio", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    viewModel.registrarEntrada(nombre) {
                        val fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                        val mensaje = "Entrada registrada: $fechaHora\nVisitando: $nombre"
                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()

                        prefs.edit()
                            .putString("ultima_entrada", mensaje)
                            .putString("estado_visita", "entrada")
                            .apply()
                    }
                }
                .show()
        }

        binding.btnSalida.setOnClickListener {
            val estado = prefs.getString("estado_visita", "salida")
            if (estado == "salida") {
                Toast.makeText(requireContext(), "No hay ninguna entrada activa. Registra una entrada primero.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val input = EditText(requireContext())
            input.hint = "Observaciones (opcional)"
            input.minLines = 2

            AlertDialog.Builder(requireContext())
                .setTitle("Registrar salida")
                .setView(input)
                .setPositiveButton("Registrar") { _, _ ->
                    val observaciones = input.text.toString().trim()
                    viewModel.registrarSalida(observaciones) {
                        val fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                        val mensaje = if (observaciones.isNotEmpty()) {
                            "Salida registrada: $fechaHora\nObservaciones: $observaciones"
                        } else {
                            "Salida registrada: $fechaHora"
                        }

                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()

                        prefs.edit()
                            .putString("ultima_salida", mensaje)
                            .putString("estado_visita", "salida")
                            .apply()
                    }
                }
                .setNeutralButton("No hay observaciones") { _, _ ->
                    viewModel.registrarSalida("") {
                        val fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                        val mensaje = "Salida registrada: $fechaHora"
                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()

                        prefs.edit()
                            .putString("ultima_salida", mensaje)
                            .putString("estado_visita", "salida")
                            .apply()
                    }
                }
                .show()
        }

        binding.btnHistorial.setOnClickListener {
            startActivity(Intent(requireContext(), SeleccionHistorialActivity::class.java))
        }

        binding.btnAjustes.setOnClickListener {
            startActivity(Intent(requireContext(), AjustesActivity::class.java))
        }

        binding.btnCreditos.setOnClickListener {
            startActivity(Intent(requireContext(), CreditosActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}