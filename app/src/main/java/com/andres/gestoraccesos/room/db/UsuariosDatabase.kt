package com.andres.gestoraccesos.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.andres.gestoraccesos.room.daos.UsuarioDao
import com.andres.gestoraccesos.room.daos.HistorialDao
import com.andres.gestoraccesos.room.entities.Usuario
import com.andres.gestoraccesos.room.entities.Historial

@Database(entities = [Usuario::class, Historial::class], version = 2)
abstract class UsuariosDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun historialDao(): HistorialDao

    companion object {
        fun getDatabbase(ctx: Context): UsuariosDatabase {
            val db = Room.databaseBuilder(
                ctx,
                UsuariosDatabase::class.java,
                "usuariosDatabase"
            ).build()
            return db
        }
    }
}
