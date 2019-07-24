package com.bubllbub.exchangerates.extensions

import android.widget.TextView
import com.bubllbub.exchangerates.enums.CurrencyRes

fun TextView.setCurrencyLeftIcon(abbreviation: String) {
    val icon = CurrencyRes.valueOf(abbreviation.substring(0, 3)).getSymbolRes()
    val drawables = this.compoundDrawables
    drawables[0] = resources.getDrawable(icon, null)
    this.setCompoundDrawablesWithIntrinsicBounds(
        drawables[0],
        drawables[1],
        drawables[2],
        drawables[3]
    )
}