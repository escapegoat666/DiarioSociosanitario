package com.diariosociosanitario.data

import androidx.room.*

@Dao
interface VisitaDao {

    @Insert
    suspend fun insertar(visita: VisitaEntity)

    @Insert
    suspend fun insertarLista(visitas: List<VisitaEntity>)

    @Update
    suspend fun actualizar(visita: VisitaEntity)

    @Delete
    suspend fun eliminar(visita: VisitaEntity)

    @Query("SELECT * FROM visitas WHERE horaSalida IS NOT NULL ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUltimaSalida(): VisitaEntity?

    @Query("SELECT * FROM visitas WHERE horaSalida IS NULL ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUltimaEntradaSinSalida(): VisitaEntity?

    @Query("SELECT * FROM visitas WHERE fecha = :fecha ORDER BY horaEntrada ASC")
    suspend fun obtenerPorFecha(fecha: String): List<VisitaEntity>

    @Query("SELECT * FROM visitas WHERE strftime('%m', fecha) = :mes AND strftime('%Y', fecha) = :año ORDER BY fecha ASC")
    suspend fun obtenerPorMes(mes: String, año: String): List<VisitaEntity>

    @Query("SELECT * FROM visitas WHERE fecha BETWEEN :inicio AND :fin ORDER BY fecha ASC")
    suspend fun obtenerEntreFechas(inicio: String, fin: String): List<VisitaEntity>

    @Query("SELECT * FROM visitas ORDER BY fecha ASC")
    suspend fun obtenerTodo(): List<VisitaEntity>
}