package com.bubllbub.exchangerates.ui.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.SwipeAdapter
import com.bubllbub.exchangerates.extensions.setCustomFont
import com.bubllbub.exchangerates.objects.Currency
import com.google.android.material.snackbar.Snackbar


class SwipeDeleteHelper(
    private val adapter: SwipeAdapter,
    private val context: Context,
    private val snackBarLayout: NestedScrollView,
    private val swipeCallback: SwipeDeleteCallback
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val icon: Drawable? = ContextCompat.getDrawable(
        context,
        R.drawable.ic_delete
    )
    private val background: ColorDrawable =
        ColorDrawable(ContextCompat.getColor(context, R.color.deleteItemColor))

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if(position>0) {
            val removedItem = adapter.removeItem(position)
            swipeCallback.onDeleteFromSwipe(removedItem)

            val snackbar = Snackbar
                .make(
                    snackBarLayout,
                    "${removedItem.curAbbreviation} removed",
                    Snackbar.LENGTH_LONG
                )
            snackbar.setActionTextColor(Color.WHITE)
            snackbar.setAction("UNDO") {
                adapter.restoreItem()
                swipeCallback.onRestoreFromSwipe(removedItem)
            }
            snackbar.show()
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder.adapterPosition > 0) {

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            val itemView = viewHolder.itemView
            val backgroundCornerOffset = 20

            val layout = LinearLayout(context)

            val textView = TextView(context)
            textView.visibility = View.VISIBLE
            textView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            textView.text = context.getString(R.string.swipeDeleteItemText)
            textView.isAllCaps = false
            textView.setCustomFont(R.font.open_sans_semibold)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            layout.addView(textView)

            layout.measure(c.width, c.height)
            layout.layout(0, 0, c.width, c.height)


            icon?.let {
                val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight

                val textViewTop = itemView.top + (itemView.height - textView.measuredHeight) / 2

                when {
                    dX < 0 -> {
                        val iconLeft =  itemView.right - iconMargin - icon.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                        background.setBounds(
                            itemView.right + dX.toInt() - backgroundCornerOffset,
                            itemView.top, itemView.right, itemView.bottom
                        )

                        background.draw(c)
                        c.translate(
                            (iconLeft - textView.measuredWidth - iconMargin).toFloat(),
                            textViewTop.toFloat()
                        )
                        layout.draw(c)
                        c.restore()
                        icon.draw(c)
                    }
                    else ->
                        background.setBounds(0, 0, 0, 0)
                }
            }
        }
    }

    interface SwipeDeleteCallback {
        fun onDeleteFromSwipe(currency: Currency)
        fun onRestoreFromSwipe(currency: Currency)
    }
}
