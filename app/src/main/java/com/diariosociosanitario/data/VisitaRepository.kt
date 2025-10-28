package com.diariosociosanitario.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VisitaRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).visitaDao()

    suspend fun insertar(visita: VisitaEntity) {
        withContext(Dispatchers.IO) {
            dao.insertar(visita)
        }
    }

    suspend fun insertarLista(visitas: List<VisitaEntity>) {
        withContext(Dispatchers.IO) {
            dao.insertarLista(visitas)
        }
    }

    suspend fun actualizar(visita: VisitaEntity) {
        withContext(Dispatchers.IO) {
            dao.actualizar(visita)
        }
    }

    suspend fun eliminar(visita: VisitaEntity) {
        withContext(Dispatchers.IO) {
            dao.eliminar(visita)
        }
    }

    suspend fun obtenerUltimaSalida(): VisitaEntity? {
        return withContext(Dispatchers.IO) {
            dao.obtenerUltimaSalida()
        }
    }

    suspend fun obtenerUltimaEntradaSinSalida(): VisitaEntity? {
        return withContext(Dispatchers.IO) {
            dao.obtenerUltimaEntradaSinSalida()
        }
    }

    suspend fun obtenerPorFecha(fecha: String): List<VisitaEntity> {
        return withContext(Dispatchers.IO) {
            dao.obtenerTodo().filter { it.fecha == fecha }
        }
    }

    suspend fun obtenerPorMes(mes: Int, año: Int): List<VisitaEntity> {
        val mesStr = mes.toString().padStart(2, '0')
        val añoStr = año.toString()
        return withContext(Dispatchers.IO) {
            dao.obtenerPorMes(mesStr, añoStr)
        }
    }

    suspend fun obtenerEntreFechas(inicio: LocalDate, fin: LocalDate): List<VisitaEntity> {
        val f1 = inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val f2 = fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        return withContext(Dispatchers.IO) {
            dao.obtenerEntreFechas(f1, f2)
        }
    }

    suspend fun obtenerTodo(): List<VisitaEntity> {
        return withContext(Dispatchers.IO) {
            dao.obtenerTodo()
        }
    }
}