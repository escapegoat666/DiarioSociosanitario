package com.diariosociosanitario.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diariosociosanitario.data.VisitaEntity
import com.diariosociosanitario.data.VisitaRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VisitaRepository(application)

    fun obtenerTodo(callback: (List<VisitaEntity>) -> Unit) {
        viewModelScope.launch {
            val visitas = repository.obtenerTodo()
            callback(visitas)
        }
    }

    fun obtenerPorFecha(fecha: String, callback: (List<VisitaEntity>, ResumenMes) -> Unit) {
        viewModelScope.launch {
            val visitas = repository.obtenerPorFecha(fecha)
            val resumen = calcularResumen(visitas)
            callback(visitas, resumen)
        }
    }

    fun obtenerPorMes(mes: Int, año: Int, callback: (List<VisitaEntity>, ResumenMes) -> Unit) {
        viewModelScope.launch {
            val visitas = repository.obtenerTodo().filter { visita ->
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fecha = formato.parse(visita.fecha)
                val cal = Calendar.getInstance().apply { time = fecha }
                cal.get(Calendar.MONTH) + 1 == mes && cal.get(Calendar.YEAR) == año
            }

            val resumen = calcularResumen(visitas)
            callback(visitas, resumen)
        }
    }

    fun actualizar(visita: VisitaEntity) {
        viewModelScope.launch {
            repository.actualizar(visita)
        }
    }

    fun actualizar(visita: VisitaEntity, callback: () -> Unit) {
        viewModelScope.launch {
            repository.actualizar(visita)
            callback()
        }
    }

    fun eliminar(visita: VisitaEntity, callback: () -> Unit) {
        viewModelScope.launch {
            repository.eliminar(visita)
            callback()
        }
    }

    fun registrarEntrada(usuario: String, callback: () -> Unit) {
        val nueva = VisitaEntity(
            fecha = obtenerFechaActual(),
            usuario = usuario,
            horaEntrada = obtenerHoraActual()
        )
        viewModelScope.launch {
            repository.insertar(nueva)
            callback()
        }
    }

    fun registrarVisitaCompleta(visita: VisitaEntity, callback: () -> Unit) {
        viewModelScope.launch {
            repository.insertar(visita)
            callback()
        }
    }

    fun insertarLista(visitas: List<VisitaEntity>, callback: () -> Unit) {
        viewModelScope.launch {
            repository.insertarLista(visitas)
            callback()
        }
    }

    fun registrarSalida(observaciones: String, callback: () -> Unit) {
        viewModelScope.launch {
            val visita = repository.obtenerUltimaEntradaSinSalida()
            if (visita != null) {
                visita.horaSalida = obtenerHoraActual()
                visita.observaciones = observaciones
                visita.fecha = obtenerFechaActual()
                repository.actualizar(visita)
            }
            callback()
        }
    }

    fun obtenerUltimaEntradaSinSalida(callback: (VisitaEntity?) -> Unit) {
        viewModelScope.launch {
            val visita = repository.obtenerUltimaEntradaSinSalida()
            callback(visita)
        }
    }

    private fun obtenerFechaActual(): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun obtenerHoraActual(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    data class ResumenMes(
        val totalUsuarios: Int,
        val horasTrabajadasMin: Int,
        val desplazamientoMin: Int,
        val totalMin: Int
    )

    private fun calcularResumen(visitas: List<VisitaEntity>): ResumenMes {
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
        return ResumenMes(usuariosUnicos, trabajadas, desplazamiento, total)
    }
}