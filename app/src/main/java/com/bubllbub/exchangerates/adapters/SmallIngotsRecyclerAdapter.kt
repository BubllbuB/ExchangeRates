package com.bubllbub.exchangerates.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.databinding.RvItemSmallIngotBinding
import com.bubllbub.exchangerates.objects.Ingot

class SmallIngotsRecyclerAdapter(
    private var items: List<Ingot>
) : RecyclerView.Adapter<SmallIngotsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RvItemSmallIngotBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
        if (holder.adapterPosition % 2 == 0)
            holder.itemView.setBackgroundColor(Color.parseColor("#009688"))
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#00695c"))
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private var binding: RvItemSmallIngotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ing: Ingot) {
            binding.smallIngot = ing
            binding.executePendingBindings()
        }
    }
}