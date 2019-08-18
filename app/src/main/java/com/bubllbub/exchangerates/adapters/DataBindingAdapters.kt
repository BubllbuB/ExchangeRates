package com.bubllbub.exchangerates.adapters

import android.graphics.Color
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.ui.charts.DateAxisValueFormatter
import com.bubllbub.exchangerates.ui.charts.LineChatGradient
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.objects.Rate
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.DecimalFormat
import java.util.*


const val LOCALE_RU = "ru_RU"
const val LOCALE_BY = "be_BY"

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

@BindingAdapter("android:textQuotLocale")
fun setTextQuotFromLocale(view: TextView, value: Currency?) {
    view.text = when {
        Locale.getDefault().toString() == LOCALE_RU -> value?.curQuotName
        Locale.getDefault().toString() == LOCALE_BY -> value?.curQuotNameBel
        else -> value?.curQuotNameEng
    }
}

@BindingAdapter("android:textNameLocale")
fun setTextNameFromLocale(view: TextView, value: Currency?) {
    view.text = when {
        Locale.getDefault().toString() == LOCALE_RU -> value?.curName
        Locale.getDefault().toString() == LOCALE_BY -> value?.curNameBel
        else -> value?.curNameEng
    }
}

@BindingAdapter("android:textIngotLocale")
fun setIngotNameFromLocale(view: TextView, value: Ingot?) {
    view.text = when {
        Locale.getDefault().toString() == LOCALE_RU -> value?.ingotName
        Locale.getDefault().toString() == LOCALE_BY -> value?.ingotNameBel
        else -> value?.ingotNameEng
    }
}

@BindingAdapter("android:setLinesData")
fun setLinesData(chart: LineChatGradient, data: List<Rate>?) {
    if (data == null || data.isEmpty()) return

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

    dataSet.lineWidth = 5.0f
    dataSet.setDrawValues(false)
    dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    dataSet.setDrawCircles(false)
    //dataSet.setDrawFilled(true)
    dataSet.color = Color.parseColor("#ffffff")
    //dataSet.fillAlpha = 255
    //dataSet.fillColor = Color.parseColor("#009688")

    val xAxisFormatter = DateAxisValueFormatter(startTimestamp)
    val xAxis = chart.xAxis
    xAxis.valueFormatter = xAxisFormatter
    xAxis.setLabelCount(4, true)
    xAxis.setDrawGridLines(false)
    xAxis.position = XAxis.XAxisPosition.BOTTOM

    chart.axisRight.isEnabled = false
    chart.setDrawGridBackground(true)
    chart.setGridGradientColor(
        ContextCompat.getColor(
            chart.context,
            R.color.gradientToolbarStartColor
        ), ContextCompat.getColor(chart.context, R.color.gradientToolbarEndColor)
    )
    chart.setGridCornerRadius(30f)
    chart.axisLeft.setDrawGridLines(false)
    chart.xAxis.setDrawAxisLine(false)
    chart.axisLeft.setDrawAxisLine(false)
    chart.axisLeft.setLabelCount(5,true)
    chart.xAxis.textSize = 10f
    chart.axisLeft.textSize = 12f
    chart.xAxis.labelRotationAngle = -15f
    chart.setExtraOffsets(0f, 0f, 20f, 12f)
    chart.legend.isEnabled = false
    chart.description.isEnabled = false
    if (data.size < 14) {
        chart.animateX(400, Easing.EaseInOutBack)
    } else {
        chart.animateX(700, Easing.Linear)
    }
    chart.data = LineData(dataSet)
    chart.invalidate()
}
