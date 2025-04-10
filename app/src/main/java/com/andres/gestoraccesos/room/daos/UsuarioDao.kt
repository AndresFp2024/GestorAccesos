package com.andres.gestoraccesos.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.andres.gestoraccesos.room.entities.Usuario
@Dao

interface UsuarioDao {
        @Insert
        suspend fun insert(usuario: Usuario)

        @Update
        suspend fun update(usuario: Usuario)

        @Delete
        suspend fun delete(usuario: Usuario)

        @Query("SELECT * FROM usuario")
        fun getAll():LiveData<List<Usuario>>

        @Query("SELECT * FROM usuario WHERE id = :id")
        fun getById(id:Long):LiveData<List<Usuario>>
}