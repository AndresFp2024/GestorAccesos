package com.andres.gestoraccesos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.andres.gestoraccesos.R
import com.andres.gestoraccesos.room.entities.Usuario

class UsuarioAdapter(
    private val datos: List<Usuario>,
    private val onItemClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.txt_item_nombre)
        val acceso: TextView = view.findViewById(R.id.txt_item_acceso)
        val mycard: CardView = view.findViewById(R.id.my_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuarios, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = datos[position]
        holder.nombre.text = usuario.nombre
        holder.acceso.text = "Nivel de Acceso: ${usuario.nivelAcceso}"
        holder.mycard.setOnClickListener {
            onItemClick(usuario)
        }
    }
}
