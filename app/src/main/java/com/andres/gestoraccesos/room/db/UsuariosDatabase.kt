package com.andres.gestoraccesos.room.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.andres.gestoraccesos.room.daos.UsuarioDao
import com.andres.gestoraccesos.room.entities.Usuario

@Database(entities = [Usuario::class], version = 1)
abstract class UsuariosDatabase:RoomDatabase() {
    abstract fun usuarioDao():UsuarioDao

    companion object{
        fun getDatabbase(ctx:Context):UsuariosDatabase{
            val db = Room.databaseBuilder(ctx,UsuariosDatabase::class.java, "usuariosDatabase").build()
            return db
        }
    }
}