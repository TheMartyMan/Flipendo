package com.csucsu.flipendo.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.csucsu.flipendo.R
import com.csucsu.flipendo.database.FileHistory

class HistoryAdapter(
    private val onClick: (FileHistory) -> Unit
) : ListAdapter<FileHistory, HistoryAdapter.VH>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FileHistory>() {
            override fun areItemsTheSame(old: FileHistory, new: FileHistory) =
                old.id == new.id
            override fun areContentsTheSame(old: FileHistory, new: FileHistory) =
                old == new
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.textItem)

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onClick(getItem(pos))
                }
            }
        }

        fun bind(item: FileHistory) {
            tvName.text = item.displayName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}

