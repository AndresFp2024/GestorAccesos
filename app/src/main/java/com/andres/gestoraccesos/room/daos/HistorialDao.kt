package com.andres.gestoraccesos.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.andres.gestoraccesos.room.entities.Historial

@Dao
interface HistorialDao {

    @Insert
    suspend fun insert(historial: Historial)

    @Query("SELECT * FROM Historial ORDER BY id DESC")
    suspend fun getAll(): List<Historial>
}
