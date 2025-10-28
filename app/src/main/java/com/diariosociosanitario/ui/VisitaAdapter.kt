package com.diariosociosanitario.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diariosociosanitario.data.VisitaEntity
import com.diariosociosanitario.databinding.ItemVisitaBinding

class VisitaAdapter(
    private val modoEdicion: Boolean,
    private val onGuardar: (VisitaEntity) -> Unit,
    private val onEliminar: (VisitaEntity) -> Unit
) : ListAdapter<VisitaEntity, VisitaAdapter.VisitaViewHolder>(DiffCallback()) {

    inner class VisitaViewHolder(val binding: ItemVisitaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitaViewHolder {
        val binding = ItemVisitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VisitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VisitaViewHolder, position: Int) {
        val visita = getItem(position)

        holder.binding.txtUsuario.text = "👤 Usuario: ${visita.usuario}"
        holder.binding.txtFecha.text = "📅 Fecha: ${visita.fecha}"

        if (modoEdicion) {
            if (visita.enEdicion) {
                // Mostrar campos editables
                holder.binding.txtHoras.visibility = View.GONE
                holder.binding.txtObs.visibility = View.GONE
                holder.binding.btnEditar.visibility = View.GONE
                holder.binding.btnEliminar.visibility = View.GONE
                holder.binding.txtUsuario.visibility = View.GONE
                holder.binding.txtFecha.visibility = View.GONE

                holder.binding.editUsuario.visibility = View.VISIBLE
                holder.binding.editFecha.visibility = View.VISIBLE
                holder.binding.editEntrada.visibility = View.VISIBLE
                holder.binding.editSalida.visibility = View.VISIBLE
                holder.binding.editObs.visibility = View.VISIBLE
                holder.binding.btnGuardar.visibility = View.VISIBLE

                holder.binding.editUsuario.setText(visita.usuario)
                holder.binding.editFecha.setText(visita.fecha)
                holder.binding.editEntrada.setText(visita.horaEntrada)
                holder.binding.editSalida.setText(visita.horaSalida ?: "")
                holder.binding.editObs.setText(visita.observaciones ?: "")

                holder.binding.btnGuardar.setOnClickListener {
                    val usuario = holder.binding.editUsuario.text.toString().trim()
                    val fecha = holder.binding.editFecha.text.toString().trim()
                    val entrada = holder.binding.editEntrada.text.toString().trim()
                    val salida = holder.binding.editSalida.text.toString().trim()
                    val obs = holder.binding.editObs.text.toString().trim()

                    val fechaValida = fecha.matches(Regex("""\d{2}/\d{2}/\d{4}"""))
                    val horaValida = entrada.matches(Regex("""\d{2}:\d{2}""")) &&
                            salida.matches(Regex("""\d{2}:\d{2}"""))

                    if (!fechaValida || !horaValida || usuario.isEmpty()) {
                        Toast.makeText(holder.itemView.context, "Formato incorrecto o campos vacíos", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    visita.usuario = usuario
                    visita.fecha = fecha
                    visita.horaEntrada = entrada
                    visita.horaSalida = salida
                    visita.observaciones = obs
                    visita.enEdicion = false

                    notifyItemChanged(position)
                    onGuardar(visita)
                }

            } else {
                // Mostrar vista normal con botones
                holder.binding.txtHoras.text = "🕒 Entrada: ${visita.horaEntrada} — Salida: ${visita.horaSalida ?: "—"}"
                holder.binding.txtObs.text = "📝 Observaciones: ${visita.observaciones ?: "—"}"

                holder.binding.txtUsuario.visibility = View.VISIBLE
                holder.binding.txtFecha.visibility = View.VISIBLE
                holder.binding.txtHoras.visibility = View.VISIBLE
                holder.binding.txtObs.visibility = View.VISIBLE
                holder.binding.btnEditar.visibility = View.VISIBLE
                holder.binding.btnEliminar.visibility = View.VISIBLE

                holder.binding.editUsuario.visibility = View.GONE
                holder.binding.editFecha.visibility = View.GONE
                holder.binding.editEntrada.visibility = View.GONE
                holder.binding.editSalida.visibility = View.GONE
                holder.binding.editObs.visibility = View.GONE
                holder.binding.btnGuardar.visibility = View.GONE

                holder.binding.btnEditar.setOnClickListener {
                    visita.enEdicion = true
                    notifyItemChanged(position)
                }

                holder.binding.btnEliminar.setOnClickListener {
                    onEliminar(visita)
                }
            }
        } else {
            // Modo solo lectura
            holder.binding.txtHoras.text = "🕒 Entrada: ${visita.horaEntrada} — Salida: ${visita.horaSalida ?: "—"}"
            holder.binding.txtObs.text = "📝 Observaciones: ${visita.observaciones ?: "—"}"

            holder.binding.txtUsuario.visibility = View.VISIBLE
            holder.binding.txtFecha.visibility = View.VISIBLE
            holder.binding.txtHoras.visibility = View.VISIBLE
            holder.binding.txtObs.visibility = View.VISIBLE
            holder.binding.btnEditar.visibility = View.GONE
            holder.binding.btnEliminar.visibility = View.GONE

            holder.binding.editUsuario.visibility = View.GONE
            holder.binding.editFecha.visibility = View.GONE
            holder.binding.editEntrada.visibility = View.GONE
            holder.binding.editSalida.visibility = View.GONE
            holder.binding.editObs.visibility = View.GONE
            holder.binding.btnGuardar.visibility = View.GONE
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<VisitaEntity>() {
        override fun areItemsTheSame(oldItem: VisitaEntity, newItem: VisitaEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VisitaEntity, newItem: VisitaEntity): Boolean {
            return oldItem == newItem
        }
    }
}