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
        txt_mensaje = findViewById(R.id.txt_mansaje)
        rv_lista = findViewById(R.id.rv_lista)
        ll_inicio = findViewById(R.id.ll_inicio)
        ll_principal = findViewById(R.id.ll_principal)
        btn_guardar = findViewById(R.id.btn_guardar)
        btn_guardar_cambios = findViewById(R.id.btn_guardar_cambios)
        btn_cancelar = findViewById(R.id.btn_cancelar)
        btn_eliminar = findViewById(R.id.btn_eliminar)
        btn_entrar = findViewById(R.id.btn_entrar)

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
    fun entrar(v: View) {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("iniciado", true).apply()

        ll_principal.visibility = View.VISIBLE
        ll_inicio.visibility = View.GONE
    }

    fun cancelar(v:View){
        startActivity((Intent(applicationContext,MainActivity::class.java)))
        finish()
    }

    fun guardar(v:View){
        if(txt_nombre.text.toString().length<2){
            txt_nombre.error = "Debe ingresar un nombre válido"
        } else if(txt_acceso.text.toString().isEmpty()||
            txt_acceso.text.toString().toIntOrNull() == null){
            txt_acceso.error = "Debe ingresar un número válido"
        } else {
            GlobalScope.launch {
                base.usuarioDao().insert(
                    Usuario(
                        nombre = txt_nombre.text.toString(),
                        nivelAcceso = txt_acceso.text.toString().toInt()
                    )
                )
                runOnUiThread{
                    txt_mensaje.setText("¡Guardado con éxito!")
                    txt_mensaje.setTextColor(Color.GREEN)
                    limpiarCampos()
                }


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
                }


            }
        }
    }

    fun eliminar(v:View){
        GlobalScope.launch {
            base.usuarioDao().delete(
                Usuario(
                    id = idUsuario,
                    nombre = txt_nombre.text.toString(),
                    nivelAcceso = txt_acceso.text.toString().toInt()
                )
            )
            runOnUiThread{
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

    private fun limpiarCampos() {
        txt_nombre.text?.clear()
        txt_acceso.text?.clear()
    }

    fun llenarLista(){
        try {
            GlobalScope.launch {
                val resultado = base.usuarioDao().getAll()
                runOnUiThread {
                    resultado.observe(this@MainActivity){
                        usuariosArray = arrayListOf()
                        usuariosArray = it

                        val adaptador = UsuarioAdapter(usuariosArray)
                        rv_lista.layoutManager = LinearLayoutManager(applicationContext)
                        rv_lista.adapter = adaptador
                    }
                }

            }
        } catch (e:Exception){
            txt_mensaje.text=e.message
            txt_mensaje.setTextColor(Color.RED)
        }
    }

    fun salir(v: View) {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("iniciado", false).apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}