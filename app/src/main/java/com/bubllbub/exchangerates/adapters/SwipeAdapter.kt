package com.bubllbub.exchangerates.adapters

import com.bubllbub.exchangerates.objects.Currency

interface SwipeAdapter {
    fun removeItem(position: Int): Currency
    fun restoreItem()
}