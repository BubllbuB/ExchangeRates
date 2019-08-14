package com.bubllbub.exchangerates.adapters

import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.databinding.RvItemConverterBinding
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.ui.recyclerview.ConverterTextWatcher

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
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        return items[position].curId.toLong()
    }

    fun replaceData(newItems: List<Currency>, updateObserver: Boolean = true) {
        if (updateObserver) {
            val byn = items.find { it.curAbbreviation == "BYN" }
            if (byn == null) {
                diffCalc(recalculateAmount(10.0, newItems))
            } else {
                diffCalc(recalculateAmount(byn.calcAmount, newItems))
            }
        } else {
            diffCalc(newItems)
        }
    }

    private fun diffCalc(newList: List<Currency>) {
        val diffResult = DiffUtil.calculateDiff(ConverterDiffCallback(items, newList))
        diffResult.dispatchUpdatesTo(this)
        items.clear()
        items.addAll(newList)
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

    private fun recalculateAmount(amount: Double, list: List<Currency>): List<Currency> {
        val newList = list.map { it.copy() }

        val activeCurrency = newList[activePosition]
        val activeInBYN = activeCurrency.curOfficialRate * amount
        activeCurrency.calcAmount = amount

        newList.forEach { currencyConv ->
            if (currencyConv.curAbbreviation == "BYN") {
                currencyConv.calcAmount = activeInBYN
            } else if (currencyConv != activeCurrency) {
                currencyConv.calcAmount =
                    when (currencyConv.scale) {
                        1 -> activeInBYN * (1 / currencyConv.curOfficialRate)
                        else -> activeInBYN / (currencyConv.curOfficialRate / currencyConv.scale)
                    }
            }
        }
        return newList
    }

    inner class ViewHolder(private var binding: RvItemConverterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var withWatcher = false

        fun bind(curr: Currency) {
            binding.currency = curr
            binding.executePendingBindings()

            if (!withWatcher) {
                binding.currencyAmountTe.addTextChangedListener(object :
                    ConverterTextWatcher() {
                    override fun afterTextChanged(s: Editable?) {
                        super.afterTextChanged(s)
                        if (!s?.toString().isNullOrBlank() && binding.currencyAmountTe.tag == null) {
                            mRecyclerView.post {
                                activePosition = layoutPosition
                                replaceData(
                                    recalculateAmount(
                                        s?.toString()?.toDouble() ?: 1.0,
                                        items
                                    ), updateObserver = false
                                )
                                binding.currencyAmountTe.requestFocus()
                            }
                        }
                    }
                })
                withWatcher = true
            }
        }
    }
}