package com.andres.gestoraccesos.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.andres.gestoraccesos.MainActivity
import com.andres.gestoraccesos.R
import com.andres.gestoraccesos.room.entities.Usuario

class UsuarioAdapter(private val datos:List<Usuario>):
    RecyclerView.Adapter<UsuarioAdapter.ViewHolder>()
{
    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val nombre:TextView
        val acceso:TextView
        val mycard:CardView

        init {
            nombre = view.findViewById(R.id.txt_item_nombre)
            acceso = view.findViewById(R.id.txt_item_acceso)
            mycard = view.findViewById(R.id.my_card)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuarios, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nombre.text = datos[position].nombre
        holder.acceso.text = "Nivel de Acceso: " + datos[position].nivelAcceso.toString()

        holder.mycard.setOnClickListener {
            val intent = Intent(holder.mycard.context.applicationContext,MainActivity::class.java)
            intent.putExtra("nombre",datos[position].nombre)
            intent.putExtra("nivelAcceso",datos[position].nivelAcceso)
            intent.putExtra("id",datos[position].id)
            holder.mycard.context.startActivity(intent)
        }

    }

}