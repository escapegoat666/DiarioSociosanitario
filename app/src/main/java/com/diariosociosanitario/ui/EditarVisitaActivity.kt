package com.diariosociosanitario.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.diariosociosanitario.R
import com.diariosociosanitario.data.VisitaEntity

class EditarVisitaActivity : AppCompatActivity() {

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var spinnerVisitas: Spinner
    private lateinit var edtUsuario: EditText
    private lateinit var edtEntrada: EditText
    private lateinit var edtSalida: EditText
    private lateinit var edtObservaciones: EditText
    private lateinit var btnGuardar: Button

    private var visitaSeleccionada: VisitaEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_visita)

        spinnerVisitas = findViewById(R.id.spinnerVisitas)
        edtUsuario = findViewById(R.id.edtUsuario)
        edtEntrada = findViewById(R.id.edtEntrada)
        edtSalida = findViewById(R.id.edtSalida)
        edtObservaciones = findViewById(R.id.edtObservaciones)
        btnGuardar = findViewById(R.id.btnGuardar)

        viewModel.obtenerTodo { visitas ->
            runOnUiThread {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    visitas.map { "${it.fecha} - ${it.usuario}" }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerVisitas.adapter = adapter

                spinnerVisitas.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                        visitaSeleccionada = visitas[position]
                        visitaSeleccionada?.let {
                            edtUsuario.setText(it.usuario)
                            edtEntrada.setText(it.horaEntrada)
                            edtSalida.setText(it.horaSalida ?: "")
                            edtObservaciones.setText(it.observaciones ?: "")
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })
            }
        }

        btnGuardar.setOnClickListener {
            visitaSeleccionada?.let {
                it.usuario = edtUsuario.text.toString().trim()
                it.horaEntrada = edtEntrada.text.toString().trim()
                it.horaSalida = edtSalida.text.toString().trim()
                it.observaciones = edtObservaciones.text.toString().trim()

                viewModel.actualizar(it) {
                    runOnUiThread {
                        Toast.makeText(this, "Visita actualizada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}