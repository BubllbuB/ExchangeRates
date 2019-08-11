package com.bubllbub.exchangerates.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.databinding.RvDialogItemBinding
import com.bubllbub.exchangerates.objects.Currency
import kotlinx.android.synthetic.main.rv_dialog_item.view.*

class DialogRecyclerAdapter(
    private var items: MutableList<Currency>
) : RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder>() {
    private var selectedPos = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RvDialogItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])

        if (selectedPos == position) {
            holder.itemView.dialog_item_cv.background =
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.rv_dialog_add_selected_bg
                )
            holder.itemView.rv_item_dialog_currency_name.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.white
                )
            )
        } else {
            holder.itemView.dialog_item_cv.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.rv_dialog_add_bg)
            holder.itemView.rv_item_dialog_currency_name.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.textDarkGrey
                )
            )
        }

    }

    override fun getItemId(position: Int): Long {
        return items[position].curId.toLong()
    }

    override fun getItemCount(): Int = items.size

    fun replaceData(newItems: List<Currency>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getSelectedCurrency(): Currency? {
        return if (selectedPos != RecyclerView.NO_POSITION) {
            items[selectedPos]
        } else {
            null
        }
    }

    inner class ViewHolder(private var binding: RvDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(curr: Currency) {
            binding.currency = curr
            binding.executePendingBindings()

            binding.dialogItemCv.setOnClickListener {
                notifyItemChanged(selectedPos)
                selectedPos = layoutPosition
                notifyItemChanged(selectedPos)
            }
        }
    }
}