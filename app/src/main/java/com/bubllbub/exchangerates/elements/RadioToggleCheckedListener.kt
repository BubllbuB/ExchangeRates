package com.bubllbub.exchangerates.elements

import com.google.android.material.button.MaterialButtonToggleGroup

open class RadioToggleCheckedListener: MaterialButtonToggleGroup.OnButtonCheckedListener {
    private var prevChecked = 0

    override fun onButtonChecked(
        group: MaterialButtonToggleGroup,
        checkedId: Int,
        isChecked: Boolean
    ) {
        checkPrevExecute(checkedId)


    }

    fun checkPrevExecute(checkedId: Int): Boolean {
        return if(prevChecked == 0 || prevChecked != checkedId) {
            prevChecked = checkedId
            false
        } else {
            true
        }
    }
}