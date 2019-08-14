package com.bubllbub.exchangerates.ui.charts

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*


class DateAxisValueFormatter(private val referenceTimestamp: Long) : ValueFormatter() {
    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    private val date = Date()

    override fun getFormattedValue(value: Float): String {
        val originalTimestamp = referenceTimestamp + value.toLong()
        return getDateString(originalTimestamp)
    }

    private fun getDateString(timestamp: Long): String {
        date.time = timestamp
        return dateFormat.format(date)
    }
}