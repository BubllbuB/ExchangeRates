package com.bubllbub.exchangerates.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.databinding.RvItemIngotBinding
import com.bubllbub.exchangerates.objects.Ingot
import kotlinx.android.synthetic.main.rv_item_ingot.view.*

class IngotsRecyclerAdapter(
    private var items: MutableList<Ingot>
) : RecyclerView.Adapter<IngotsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RvItemIngotBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])

        holder.itemView.setOnClickListener {
            holder.itemView.expandableLayout.toggle()
        }
    }

    override fun getItemCount(): Int = items.size

    fun replaceData(newItems: List<Ingot>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(private var binding: RvItemIngotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ing: Ingot) {
            binding.ingot = ing

            binding.rvSmallIngots.layoutManager = LinearLayoutManager(binding.rvSmallIngots.context)
            val adapter = SmallIngotsRecyclerAdapter(ing.rates)
            binding.rvSmallIngots.adapter = adapter
            binding.rvSmallIngots.isNestedScrollingEnabled = false

            binding.expandableLayout.setOnExpansionUpdateListener { expansionFraction, _ ->
                binding.expandableArrow.rotation = expansionFraction * 180
            }

            binding.executePendingBindings()
        }
    }
}