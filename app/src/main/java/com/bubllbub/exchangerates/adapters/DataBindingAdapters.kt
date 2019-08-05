package com.bubllbub.exchangerates.adapters

import android.graphics.Color
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bubllbub.exchangerates.charts.DateAxisValueFormatter
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Rate
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("android:text")
fun setTextFromDate(textView: TextView, date: DateTime) {
    val dateFormat = DateTimeFormat.forPattern("dd.MM.yyyy")
    val dateString = dateFormat.print(date)
    textView.text = dateString
}

@BindingAdapter("android:text")
fun setTextFromDouble(view: EditText, value: Double?) {
    if (view.text.toString() != value.toString()) {
        view.clearFocus()
        view.tag = "Binding"
        view.setText(DecimalFormat("#.####").format(value))
        view.setSelection(view.text.length)
        view.tag = null
    }
}

@BindingAdapter("android:setLinesData")
fun setLinesData(chart: LineChart, data: List<Rate>?) {
    if (data == null) return

    val startTimestamp = data[0].date.millis

    val entries = mutableListOf<Entry>()
    data.forEach { currency ->
        entries.add(
            Entry(
                (currency.date.millis - startTimestamp).toFloat(),
                currency.curOfficialRate.toFloat()
            )
        )
    }

    val dataSet = LineDataSet(entries, "Label")
    dataSet.setDrawValues(false)
    dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    dataSet.setDrawCircles(false)
    dataSet.setDrawFilled(true)
    dataSet.color = Color.parseColor("#009688")
    dataSet.fillAlpha = 255
    dataSet.fillColor = Color.parseColor("#009688")

    val xAxisFormatter = DateAxisValueFormatter(startTimestamp)
    val xAxis = chart.xAxis
    xAxis.valueFormatter = xAxisFormatter
    xAxis.setLabelCount(4, true)
    xAxis.setDrawGridLines(false)
    xAxis.position = XAxis.XAxisPosition.BOTTOM

    chart.axisRight.isEnabled = false
    chart.setDrawGridBackground(false)
    chart.axisLeft.setDrawGridLines(false)
    chart.xAxis.setDrawGridLines(false)
    chart.xAxis.textSize = 12f
    chart.axisLeft.textSize = 14f
    chart.xAxis.labelRotationAngle = -15f
    chart.setExtraOffsets(0f, 0f, 25f, 12f)
    chart.legend.isEnabled = false
    chart.description.isEnabled = false
    chart.animateX(700, Easing.Linear)

    chart.data = LineData(dataSet)
    chart.invalidate()
}
