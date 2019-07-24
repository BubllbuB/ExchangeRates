package com.bubllbub.exchangerates.adapters

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.bubllbub.exchangerates.objects.Currency


class ConverterDiffCallback(
    private val oldList: List<Currency>,
    private val newList: List<Currency>
) : DiffUtil.Callback() {
    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].curId == oldList[oldItemPosition].curId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].calcAmount == oldList[oldItemPosition].calcAmount
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val diff = Bundle()

        if (newList[newItemPosition].calcAmount != oldList[oldItemPosition].calcAmount) {
            diff.putDouble("calcAmount", newList[newItemPosition].calcAmount)
        }
        return if (diff.size() == 0) {
            null
        } else diff
    }
}