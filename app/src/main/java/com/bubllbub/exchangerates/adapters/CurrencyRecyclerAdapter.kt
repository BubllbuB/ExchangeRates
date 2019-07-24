package com.bubllbub.exchangerates.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.databinding.RvItemCurrencyBinding
import com.bubllbub.exchangerates.objects.Currency

class CurrencyRecyclerAdapter(
    private var items: MutableList<Currency>,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<CurrencyRecyclerAdapter.ViewHolder>(), SwipeAdapter {
    private lateinit var removedItem: Currency
    private var removedPosition = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RvItemCurrencyBinding.inflate(layoutInflater, parent, false)
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

    fun replaceData(newItems: List<Currency>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun removeItem(position: Int): Currency {
        removedItem = items.removeAt(position)
        removedPosition = position
        notifyItemRemoved(position)
        return removedItem
    }

    override fun restoreItem() {
        items.add(removedPosition, removedItem)
        notifyItemInserted(removedPosition)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(private var binding: RvItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(curr: Currency, listener: OnItemClickListener?) {
            binding.currency = curr
            if (listener != null) {
                binding.root.setOnClickListener { listener.onItemClick(layoutPosition) }
            }

            binding.executePendingBindings()
        }
    }
}