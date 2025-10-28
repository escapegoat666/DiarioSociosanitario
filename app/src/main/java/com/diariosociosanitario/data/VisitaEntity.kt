package com.diariosociosanitario.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "visitas")
class VisitaEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var fecha: String,
    var usuario: String,
    var horaEntrada: String,
    var horaSalida: String? = null,
    var desplazamiento: String? = null,
    var observaciones: String? = null
) {
    @Ignore
    var enEdicion: Boolean = false

    // ✅ Constructor vacío requerido por Room
    @Ignore
    constructor() : this(0, "", "", "")
}