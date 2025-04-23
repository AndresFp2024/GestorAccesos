package com.andres.gestoraccesos

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andres.gestoraccesos.adapters.HistorialAdapter
import com.andres.gestoraccesos.room.db.UsuariosDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HistorialActivity : AppCompatActivity() {

    lateinit var rv_historial: RecyclerView
    lateinit var db: UsuariosDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        rv_historial = findViewById(R.id.rv_historial)
        db = UsuariosDatabase.getDatabbase(applicationContext)

        GlobalScope.launch {
            val historial = db.historialDao().getAll()
            runOnUiThread {
                rv_historial.layoutManager = LinearLayoutManager(this@HistorialActivity)
                rv_historial.adapter = HistorialAdapter(historial)
            }
        }
    }
    fun volverAtras(view: View) {
        finish() // Cierra esta actividad y vuelve a MainActivity
    }
}
