package com.andres.gestoraccesos.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Historial(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val tipoAccion: String,
    val fechaHora: String,
    val usuarioAfectado: String,
    val nivelAcceso: Int,
    val realizadoPor: String = "admin",
    val detalles: String? = null
)
