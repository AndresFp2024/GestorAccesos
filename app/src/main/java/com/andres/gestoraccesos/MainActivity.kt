package com.andres.gestoraccesos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andres.gestoraccesos.adapters.UsuarioAdapter
import com.andres.gestoraccesos.room.db.UsuariosDatabase
import com.andres.gestoraccesos.room.entities.Usuario
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var base:UsuariosDatabase
    lateinit var txt_nombre:TextInputEditText
    lateinit var txt_acceso:TextInputEditText
    lateinit var txt_mensaje: TextView
    lateinit var txt_mensaje2: TextView
    lateinit var usuariosArray:List<Usuario>
    lateinit var rv_lista:RecyclerView
    lateinit var ll_inicio:LinearLayout
    lateinit var ll_principal:LinearLayout
    lateinit var btn_guardar:Button
    lateinit var btn_guardar_cambios:Button
    lateinit var btn_cancelar:Button
    lateinit var btn_eliminar:Button
    lateinit var btn_entrar:Button

    var iniciado:Boolean=false
    var idUsuario:Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        base = UsuariosDatabase.getDatabbase(applicationContext)

        initControlsUi()

        llenarLista()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initControlsUi() {
        txt_nombre = findViewById(R.id.txt_nombre)
        txt_acceso = findViewById(R.id.txt_acceso)
        txt_mensaje = findViewById(R.id.txt_mensaje)
        txt_mensaje2 = findViewById(R.id.txt_mensaje2)
        rv_lista = findViewById(R.id.rv_lista)
        ll_inicio = findViewById(R.id.ll_inicio)
        ll_principal = findViewById(R.id.ll_principal)
        btn_guardar = findViewById(R.id.btn_guardar)
        btn_guardar_cambios = findViewById(R.id.btn_guardar_cambios)
        btn_cancelar = findViewById(R.id.btn_cancelar)
        btn_eliminar = findViewById(R.id.btn_eliminar)
        btn_entrar = findViewById(R.id.btn_entrar)

        val txt_usuario: TextInputEditText = findViewById(R.id.txt_usuario)
        val txt_password: TextInputEditText = findViewById(R.id.txt_password)


        btn_cancelar.visibility = View.GONE
        btn_guardar_cambios.visibility = View.GONE
        btn_eliminar.visibility = View.GONE

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        iniciado = prefs.getBoolean("iniciado", false)
        if (iniciado) {
            ll_inicio.visibility = View.GONE
            ll_principal.visibility = View.VISIBLE
        } else {
            ll_inicio.visibility = View.VISIBLE
            ll_principal.visibility = View.GONE
        }

        if(intent.extras?.isEmpty == false){
            txt_nombre.setText(intent.extras?.getString("nombre", ""))
            txt_acceso.setText(intent.extras?.getInt("nivelAcceso", 0).toString())
            idUsuario = intent.extras!!.getLong("id", 0)

            btn_cancelar.visibility = View.VISIBLE
            btn_eliminar.visibility = View.VISIBLE
            btn_guardar_cambios.visibility = View.VISIBLE
            btn_guardar.visibility = View.GONE
        }
    }
    fun entrar(view: View) {
        val usuario = findViewById<TextInputEditText>(R.id.txt_usuario).text.toString()
        val password = findViewById<TextInputEditText>(R.id.txt_password).text.toString()

        if (usuario == "admin" && password == "admin") {
            ll_inicio.visibility = View.GONE
            ll_principal.visibility = View.VISIBLE
            iniciado = true
        } else {
            txt_mensaje2.text = "Credenciales incorrectas"
            txt_mensaje.setTextColor(Color.RED)
        }
    }


    fun cancelar(v: View) {
        limpiarCampos()

        ll_inicio.visibility = View.GONE
        ll_principal.visibility = View.VISIBLE

        btn_guardar.visibility = View.VISIBLE
        btn_guardar_cambios.visibility = View.GONE
        btn_eliminar.visibility = View.GONE
        btn_cancelar.visibility = View.GONE

        txt_mensaje.text = ""
    }


    fun guardar(view: View) {
        val nombre = txt_nombre.text.toString()
        val acceso = txt_acceso.text.toString()

        if (nombre.isBlank() || acceso.isBlank()) {
            txt_mensaje.text = "Completa todos los campos"
            txt_mensaje.setTextColor(Color.RED)
            return
        }

        val usuario = Usuario(
            nombre = nombre,
            nivelAcceso = acceso.toInt()
        )

        GlobalScope.launch {
            base.usuarioDao().insert(usuario)
            runOnUiThread {
                txt_mensaje.text = "Usuario guardado"
                txt_mensaje.setTextColor(Color.GREEN)
                limpiarCampos()
                llenarLista() // ✅ Esto recarga la lista
            }
        }
    }


    fun guardarCambios(v:View){
        if(txt_nombre.text.toString().length<2){
            txt_nombre.error = "Debe ingresar un nombre válido"
        } else if(txt_acceso.text.toString().isEmpty()){
            txt_acceso.error = "Debe ingresar un acceso válido"
        } else {
            GlobalScope.launch {
                base.usuarioDao().update(
                    Usuario(
                        id = idUsuario,
                        nombre = txt_nombre.text.toString(),
                        nivelAcceso = txt_acceso.text.toString().toInt()
                    )
                )
                runOnUiThread{
                    txt_mensaje.setText("¡Actualizado con éxito!")
                    txt_mensaje.setTextColor(Color.GREEN)
                    limpiarCampos()

                    btn_cancelar.visibility = View.GONE
                    btn_eliminar.visibility = View.GONE
                    btn_guardar_cambios.visibility = View.GONE
                    btn_guardar.visibility = View.VISIBLE

                    llenarLista()
                }


            }
        }
    }

    fun eliminar(v: View) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Seguro que desea eliminar al usuario?")
            .setPositiveButton("Sí") { _, _ ->
                GlobalScope.launch {
                    base.usuarioDao().delete(
                        Usuario(
                            id = idUsuario,
                            nombre = txt_nombre.text.toString(),
                            nivelAcceso = txt_acceso.text.toString().toInt()
                        )
                    )
                    runOnUiThread {
                        txt_mensaje.setText("¡Eliminado con éxito!")
                        txt_mensaje.setTextColor(Color.GREEN)
                        limpiarCampos()

                        btn_cancelar.visibility = View.GONE
                        btn_eliminar.visibility = View.GONE
                        btn_guardar_cambios.visibility = View.GONE
                        btn_guardar.visibility = View.VISIBLE
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }


    private fun limpiarCampos() {
        txt_nombre.text?.clear()
        txt_acceso.text?.clear()
    }

    private fun llenarLista() {
        GlobalScope.launch {
            usuariosArray = base.usuarioDao().getAll()
            runOnUiThread {
                rv_lista.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_lista.adapter = UsuarioAdapter(usuariosArray) { usuario ->
                    // Al hacer clic en un usuario, se llenan los campos y se muestran los botones de edición
                    txt_nombre.setText(usuario.nombre)
                    txt_acceso.setText(usuario.nivelAcceso.toString())
                    idUsuario = usuario.id

                    ll_inicio.visibility = View.GONE
                    ll_principal.visibility = View.VISIBLE

                    btn_guardar.visibility = View.GONE
                    btn_guardar_cambios.visibility = View.VISIBLE
                    btn_eliminar.visibility = View.VISIBLE
                    btn_cancelar.visibility = View.VISIBLE
                }
            }
        }
    }


    fun salir(v: View) {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("iniciado", false).apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}