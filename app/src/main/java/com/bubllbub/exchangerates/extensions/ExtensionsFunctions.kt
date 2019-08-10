package com.bubllbub.exchangerates.extensions

import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.bubllbub.exchangerates.enums.CurrencyRes


fun TextView.setCurrencyLeftIcon(abbreviation: String) {
    val icon = CurrencyRes.valueOf(abbreviation).getSymbolRes()
    val iconDraw = resources.getDrawable(icon, null)
    iconDraw.setBounds(0, 0, 100, 100)

    val drawables = this.compoundDrawables
    this.setCompoundDrawables(
        iconDraw,
        drawables[1],
        drawables[2],
        drawables[3]
    )
}

fun TextView.setCustomFont(font: Int) {
    this.typeface = ResourcesCompat.getFont(context, font)
}