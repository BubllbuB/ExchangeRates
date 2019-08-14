package com.bubllbub.exchangerates.ui.listeners

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.bubllbub.exchangerates.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

open class RadioToggleCheckedListener(private val ctx: Context) :
    MaterialButtonToggleGroup.OnButtonCheckedListener {
    private var prevChecked = 0

    override fun onButtonChecked(
        group: MaterialButtonToggleGroup,
        checkedId: Int,
        isChecked: Boolean
    ) {
        if (isChecked) {
            for (index in 0 until group.childCount) {
                val button = group.getChildAt(index) as MaterialButton

                if (button.id == checkedId) {
                    button.setBackgroundColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.toggleButtonColor
                        )
                    )
                    button.setTextColor(Color.parseColor("#ffffff"))
                    if (!checkPrevExecute(checkedId)) {
                        executeOnCheck(button)
                    }
                } else {
                    button.setBackgroundColor(0x00000000)
                    button.setTextColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.toggleButtonColor
                        )
                    )
                }
            }
        }
    }

    private fun checkPrevExecute(checkedId: Int): Boolean {
        return if (prevChecked == 0 || prevChecked != checkedId) {
            prevChecked = checkedId
            false
        } else {
            true
        }
    }

    open fun executeOnCheck(button: MaterialButton) {

    }
}