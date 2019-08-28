package com.bubllbub.exchangerates.adapters

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.databinding.RvItemConverterBinding
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.ui.recyclerview.ConverterTextWatcher
import com.bubllbub.exchangerates.views.fragments.RECYCLER_EDITTEXT_SELECTION_POSITION_END
import com.bubllbub.exchangerates.views.fragments.RECYCLER_EDITTEXT_SELECTION_POSITION_START
import com.bubllbub.exchangerates.views.fragments.RECYCLER_POSITION

class ConverterRecyclerAdapter(
    private var items: MutableList<Currency>,
    private val listener: OnConverterCurrencyCalcListener
) : RecyclerView.Adapter<ConverterRecyclerAdapter.ViewHolder>(), SwipeAdapter {
    private lateinit var removedItem: Currency
    private var removedPosition = 0
    private lateinit var mRecyclerView: RecyclerView

    private var scrollPosition: Int = 0
    private var selectionPositionStart: Int = 0
    private var selectionPositionEnd: Int = 0
    private var isRestored = false

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
        holder.bind(items[position], listener)
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

    fun savedFocusToInstanceState(outState: Bundle) {
        outState.putInt(RECYCLER_POSITION, scrollPosition)
        outState.putInt(RECYCLER_EDITTEXT_SELECTION_POSITION_START, selectionPositionStart)
        outState.putInt(RECYCLER_EDITTEXT_SELECTION_POSITION_END, selectionPositionEnd)
    }

    fun restoreFocusFromInstanceState(savedInstanceState: Bundle?) {
        scrollPosition = savedInstanceState?.getInt(RECYCLER_POSITION) ?: 0
        selectionPositionStart =
            savedInstanceState?.getInt(RECYCLER_EDITTEXT_SELECTION_POSITION_START) ?: 0
        selectionPositionEnd =
            savedInstanceState?.getInt(RECYCLER_EDITTEXT_SELECTION_POSITION_END) ?: 0
        isRestored = true
    }

    inner class ViewHolder(private var binding: RvItemConverterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var withWatcher = false

        fun bind(curr: Currency, listener: OnConverterCurrencyCalcListener?) {
            binding.currency = curr
            binding.executePendingBindings()

            if (!withWatcher) {
                binding.currencyAmountTe.addTextChangedListener(object :
                    ConverterTextWatcher() {
                    override fun afterTextChanged(s: Editable?) {
                        super.afterTextChanged(s)
                        if (!s?.toString().isNullOrBlank() && binding.currencyAmountTe.tag == null) {
                            listener?.recalculateAmounts(
                                s?.toString()?.toDouble() ?: 1.0,
                                curr
                            )
                            scrollPosition = layoutPosition
                            selectionPositionStart = binding.currencyAmountTe.selectionStart
                            selectionPositionEnd = binding.currencyAmountTe.selectionEnd
                        }
                    }
                })
                withWatcher = true
            }

            if (isRestored && scrollPosition == layoutPosition) {
                mRecyclerView.scrollToPosition(scrollPosition)
                binding.currencyAmountTe.requestFocus()
                binding.currencyAmountTe.setSelection(selectionPositionStart, selectionPositionEnd)
                isRestored = false
            }
        }
    }

    interface OnConverterCurrencyCalcListener {
        fun recalculateAmounts(amount: Double, activeCurr: Currency)
    }
}