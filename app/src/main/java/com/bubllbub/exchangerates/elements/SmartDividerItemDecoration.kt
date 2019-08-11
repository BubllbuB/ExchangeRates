package com.bubllbub.exchangerates.elements

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.extensions.toDp


class SmartDividerItemDecoration(private val divider: Drawable?) : RecyclerView.ItemDecoration() {
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft + 10 + App.appContext().resources.getDimension(R.dimen.itemMarginDefault).toInt()
        val dividerRight = parent.width - App.appContext().resources.getDimension(R.dimen.itemMarginDefault).toInt() - 10

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val dividerTop = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + (divider?.intrinsicHeight ?: 0)

            divider?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider?.draw(c)
        }
    }
}