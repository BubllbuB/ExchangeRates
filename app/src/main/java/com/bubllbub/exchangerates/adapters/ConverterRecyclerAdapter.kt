package com.bubllbub.exchangerates.adapters

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.databinding.RvItemConverterBinding
import com.bubllbub.exchangerates.objects.Currency

class ConverterRecyclerAdapter(
    private var items: MutableList<Currency>
) : RecyclerView.Adapter<ConverterRecyclerAdapter.ViewHolder>(), SwipeAdapter {
    private lateinit var removedItem: Currency
    private var removedPosition = 0
    private var activePosition = 0
    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RvItemConverterBinding.inflate(layoutInflater, parent, false)
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

    override fun getItemId(position: Int): Long {
        return items[position].curId.toLong()
    }

    fun replaceData(newItems: List<Currency>) {
        val diffResult = DiffUtil.calculateDiff(ConverterDiffCallback(items, newItems))
        diffResult.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newItems)
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

    private fun recalculateAmount(amount: Double) {
        val newList = items.map{it.copy()}

        val activeCurrency = newList[activePosition]
        val activeInBYN = activeCurrency.curOfficialRate * amount
        activeCurrency.calcAmount = amount

        newList.forEach { currency ->
            if (currency.curAbbreviation == "BYN") {
                currency.calcAmount = activeInBYN
            } else if (currency != activeCurrency) {
                currency.calcAmount =
                    when (currency.scale) {
                        1 -> activeInBYN * (1 / currency.curOfficialRate)
                        else -> activeInBYN / (currency.curOfficialRate / currency.scale)
                    }
            }
        }

        replaceData(newList)
    }

    inner class ViewHolder(private var binding: RvItemConverterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(curr: Currency) {
            binding.currency = curr
            binding.executePendingBindings()

            binding.currencyAmountTe.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (!s?.toString().isNullOrBlank() && binding.currencyAmountTe.isFocused) {
                        mRecyclerView.post {
                            activePosition = layoutPosition
                            recalculateAmount(s?.toString()?.toDouble() ?: 1.0)
                            binding.currencyAmountTe.requestFocus()
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }
            })
        }
    }
}