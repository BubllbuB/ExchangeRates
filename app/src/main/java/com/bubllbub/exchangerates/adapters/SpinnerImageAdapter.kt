package com.bubllbub.exchangerates.adapters

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter


class SpinnerImageAdapter(private val context: Context, items: ArrayList<String>) :
    MaterialSpinnerAdapter<String>(context, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getTextViewWithIcon(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getTextViewWithIcon(position)
    }

    private fun getTextViewWithIcon(position: Int): View {
        val item = getItem(position)
        val textView = TextView(context)
        val dpSize = context.resources.displayMetrics.density

        textView.textSize = 14f
        textView.text = item
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))

        textView.setPadding((8 * dpSize).toInt(), 0, 0, 0)

        textView.setCurrencyLeftIcon(item)
        textView.compoundDrawablePadding = (8 * dpSize).toInt()
        return textView
    }
}