package com.andres.gestoraccesos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andres.gestoraccesos.adapters.UsuarioAdapter
import com.andres.gestoraccesos.room.db.UsuariosDatabase
import com.andres.gestoraccesos.room.entities.Usuario
import com.andres.gestoraccesos.room.entities.Historial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var base: UsuariosDatabase
    lateinit var txt_nombre: EditText
    lateinit var txt_acceso: EditText
    lateinit var txt_mensaje: TextView
    lateinit var txt_mensaje2: TextView
    lateinit var usuariosArray: List<Usuario>
    lateinit var rv_lista: RecyclerView
    lateinit var ll_principal: LinearLayout
    lateinit var btn_guardar: Button
    lateinit var btn_guardar_cambios: Button
    lateinit var btn_cancelar: Button
    lateinit var btn_eliminar: Button

    var idUsuario: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        base = UsuariosDatabase.getDatabbase(applicationContext)
        initControlsUi()
        llenarLista()
    }

    private fun initControlsUi() {
        txt_nombre = findViewById(R.id.txt_nombre)
        txt_acceso = findViewById(R.id.txt_acceso)
        txt_mensaje = findViewById(R.id.txt_mensaje)
        rv_lista = findViewById(R.id.rv_lista)
        ll_principal = findViewById(R.id.ll_principal)
        btn_guardar = findViewById(R.id.btn_guardar)
        btn_guardar_cambios = findViewById(R.id.btn_guardar_cambios)
        btn_cancelar = findViewById(R.id.btn_cancelar)
        btn_eliminar = findViewById(R.id.btn_eliminar)

        btn_cancelar.visibility = View.GONE
        btn_guardar_cambios.visibility = View.GONE
        btn_eliminar.visibility = View.GONE


        if (intent.extras?.isEmpty == false) {
            txt_nombre.setText(intent.extras?.getString("nombre", ""))
            txt_acceso.setText(intent.extras?.getInt("nivelAcceso", 0).toString())
            idUsuario = intent.extras!!.getLong("id", 0)

            btn_cancelar.visibility = View.VISIBLE
            btn_eliminar.visibility = View.VISIBLE
            btn_guardar_cambios.visibility = View.VISIBLE
            btn_guardar.visibility = View.GONE
        }
    }

    fun cancelar(v: View) {
        limpiarCampos()
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
        val usuario = Usuario(nombre = nombre, nivelAcceso = acceso.toInt())
        GlobalScope.launch {
            base.usuarioDao().insert(usuario)
            registrarAccion("crear", usuario.nombre, usuario.nivelAcceso)
            runOnUiThread {
                txt_mensaje.text = "Usuario guardado"
                txt_mensaje.setTextColor(Color.GREEN)
                limpiarCampos()
                llenarLista()
            }
        }
    }

    fun guardarCambios(v: View) {
        val nuevoNombre = txt_nombre.text.toString()
        val nuevoNivel = txt_acceso.text.toString().toInt()

        if (nuevoNombre.length < 2) {
            txt_nombre.error = "Debe ingresar un nombre válido"
        } else if (txt_acceso.text.toString().isEmpty()) {
            txt_acceso.error = "Debe ingresar un acceso válido"
        } else {
            GlobalScope.launch {
                val usuarioAnterior = usuariosArray.find { it.id == idUsuario }
                val nivelAnterior = usuarioAnterior?.nivelAcceso ?: 0

                base.usuarioDao().update(
                    Usuario(
                        id = idUsuario,
                        nombre = nuevoNombre,
                        nivelAcceso = nuevoNivel
                    )
                )

                val detalles = "Acceso anterior: $nivelAnterior → nuevo: $nuevoNivel"
                registrarAccion("editar", nuevoNombre, nuevoNivel, detalles)

                runOnUiThread {
                    txt_mensaje.text = "¡Actualizado con éxito!"
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
                    val nombre = txt_nombre.text.toString()
                    val nivel = txt_acceso.text.toString().toInt()
                    registrarAccion("eliminar", nombre, nivel)

                    runOnUiThread {
                        txt_mensaje.text = "¡Eliminado con éxito!"
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
                    txt_nombre.setText(usuario.nombre)
                    txt_acceso.setText(usuario.nivelAcceso.toString())
                    idUsuario = usuario.id

                    ll_principal.visibility = View.VISIBLE

                    btn_guardar.visibility = View.GONE
                    btn_guardar_cambios.visibility = View.VISIBLE
                    btn_eliminar.visibility = View.VISIBLE
                    btn_cancelar.visibility = View.VISIBLE
                }
            }
        }
    }

    fun verHistorial(view: View) {
        val intent = Intent(this, HistorialActivity::class.java)
        startActivity(intent)
    }

    private fun registrarAccion(tipo: String, usuario: String, nivel: Int, detalles: String? = null) {
        val fechaHora = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())
        val historial = Historial(
            tipoAccion = tipo,
            fechaHora = fechaHora,
            usuarioAfectado = usuario,
            nivelAcceso = nivel,
            realizadoPor = "admin",
            detalles = detalles
        )

        GlobalScope.launch {
            base.historialDao().insert(historial)
        }
    }

    fun logout(view: View) {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("iniciado", false).apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
