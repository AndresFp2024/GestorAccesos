package com.andres.gestoraccesos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.andres.gestoraccesos.R
import com.andres.gestoraccesos.room.entities.Historial

class HistorialAdapter(private val datos: List<Historial>) :
    RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tipo: TextView = view.findViewById(R.id.txt_tipo)
        val fecha: TextView = view.findViewById(R.id.txt_fecha)
        val usuario: TextView = view.findViewById(R.id.txt_usuario)
        val nivel: TextView = view.findViewById(R.id.txt_nivel)
        val realizadoPor: TextView = view.findViewById(R.id.txt_realizado_por)
        val detalles: TextView = view.findViewById(R.id.txt_detalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historial = datos[position]
        holder.tipo.text = "Acción: ${historial.tipoAccion}"
        holder.fecha.text = "Fecha: ${historial.fechaHora}"
        holder.usuario.text = "Usuario: ${historial.usuarioAfectado}"
        holder.nivel.text = "Nivel: ${historial.nivelAcceso}"
        holder.realizadoPor.text = "Realizado por: ${historial.realizadoPor}"
        holder.detalles.text = historial.detalles ?: ""
    }
}
