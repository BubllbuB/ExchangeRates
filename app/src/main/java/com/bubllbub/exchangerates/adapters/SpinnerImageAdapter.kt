package com.bubllbub.exchangerates.adapters

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.bubllbub.exchangerates.extensions.setCustomFont
import com.bubllbub.exchangerates.extensions.setSpinnerTextCurrency
import com.bubllbub.exchangerates.extensions.toDp
import com.bubllbub.exchangerates.objects.Currency
import com.jaredrummler.materialspinner.MaterialSpinnerAdapter


class SpinnerImageAdapter(private val context: Context, items: List<Currency>) :
    MaterialSpinnerAdapter<Currency>(context, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getTextViewWithIcon(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getTextViewWithIcon(position)
    }

    private fun getTextViewWithIcon(position: Int): View {
        val item = getItem(position)
        val textView = TextView(context)

        textView.textSize = 15f
        textView.setSpinnerTextCurrency(item)
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setCustomFont(R.font.open_sans_bold)
        textView.setTextColor(ContextCompat.getColor(context, R.color.textDarkGrey))

        textView.setPadding(
            context.resources.getDimension(R.dimen.spinnerPaddingDefault).toInt(),
            context.resources.getDimension(R.dimen.spinnerPaddingHeightDefault).toInt(),
            0,
            context.resources.getDimension(R.dimen.spinnerPaddingHeightDefault).toInt()
        )

        textView.setCurrencyLeftIcon(item.curAbbreviation)
        textView.compoundDrawablePadding = 8.toDp()
        return textView
    }
}