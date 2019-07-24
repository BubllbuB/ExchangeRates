package com.bubllbub.exchangerates.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.databinding.RvItemCurrencyBinding
import com.bubllbub.exchangerates.databinding.RvItemIngotBinding
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot

class IngotsRecyclerAdapter(
    private var items: ArrayList<Ingot>,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<IngotsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RvItemIngotBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], listener)
        if (holder.adapterPosition % 2 == 0)
            holder.itemView.setBackgroundColor(Color.parseColor("#009688"))
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#00695c"))
    }

    override fun getItemCount(): Int = items.size

    fun replaceData(newItems: ArrayList<Ingot>) {
        items = newItems
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(private var binding: RvItemIngotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ing: Ingot, listener: OnItemClickListener?) {
            binding.ingot = ing
            if (listener != null) {
                binding.root.setOnClickListener { listener.onItemClick(layoutPosition) }
            }

            binding.executePendingBindings()
        }
    }
}