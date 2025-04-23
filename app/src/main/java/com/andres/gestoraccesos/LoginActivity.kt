package com.andres.gestoraccesos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.andres.gestoraccesos.room.db.UsuariosDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    lateinit var txt_usuario: EditText
    lateinit var txt_password: EditText
    lateinit var txt_mensaje: TextView
    lateinit var db: UsuariosDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        txt_usuario = findViewById(R.id.txt_usuario)
        txt_password = findViewById(R.id.txt_password)
        txt_mensaje = findViewById(R.id.txt_mensaje)

        db = UsuariosDatabase.getDatabbase(applicationContext)
    }

    fun entrar(view: View) {
        val usuario = txt_usuario.text.toString()
        val password = txt_password.text.toString()

        if (usuario == "admin" && password == "admin") {
            registrarAccionLogin()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            txt_mensaje.text = "Credenciales incorrectas"
            txt_mensaje.setTextColor(Color.RED)
        }
    }

    private fun registrarAccionLogin() {
        val fechaHora = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val historial = com.andres.gestoraccesos.room.entities.Historial(
            tipoAccion = "login",
            fechaHora = fechaHora,
            usuarioAfectado = "admin",
            nivelAcceso = 0,
            realizadoPor = "admin"
        )
        GlobalScope.launch {
            db.historialDao().insert(historial)
        }
    }
}
