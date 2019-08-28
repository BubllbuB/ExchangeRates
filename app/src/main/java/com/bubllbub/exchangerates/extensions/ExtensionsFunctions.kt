package com.bubllbub.exchangerates.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.view.Display
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.bubllbub.exchangerates.App
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.objects.Currency
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.jaredrummler.materialspinner.MaterialSpinner
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*


fun TextView.setCurrencyLeftIcon(abbreviation: String) {
    val icon = CurrencyRes.valueOf(abbreviation).symbolRes
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

fun MaterialSpinner.initCurrencySpinner(list: List<Currency>, position: Int = 0) {
    this.setCustomFont(R.font.open_sans_bold)
    this.setCurrencyLeftIcon(list[position].curAbbreviation)

    this.background =
        ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
    this.elevation = 0f
    this.popupWindow.elevation = 0f
    this.popupWindow.setBackgroundDrawable(
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.spinner_dropdown_bg,
            null
        )
    )

    this.setOnClickListener {
        it.background =
            ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg_opened, null)
    }
    this.setOnNothingSelectedListener {
        it.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
    }
}

fun CalendarDatePickerDialogFragment.setMinMaxRangeFromCurrency(curr: Currency) {
    val calendar = Calendar.getInstance()
    calendar.time = curr.curDateStart

    val minDate = if (calendar.get(Calendar.YEAR) < 1995) {
        MonthAdapter.CalendarDay(
            1995,
            Calendar.APRIL,
            1
        )
    } else {
        MonthAdapter.CalendarDay(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    calendar.time = Date()

    val maxDate = MonthAdapter.CalendarDay(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    this.setDateRange(minDate, maxDate)
}

fun MaterialButtonToggleGroup.setWidthChildFull(display: Display, cxt: Context) {
    val size = Point()
    display.getSize(size)

    for (index in 0 until this.childCount) {
        val button = this.getChildAt(index) as MaterialButton
        val params = button.layoutParams
        params.width =
            (size.x - (cxt.resources.getDimension(R.dimen.itemMarginDefault).toInt() + cxt.resources.getDimension(
                R.dimen.containerMarginDefault
            ).toInt()) * 2) / this.childCount
        button.layoutParams = params
    }
}

fun Int.toDp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun TextView.setSpinnerTextCurrency(curr: Currency) {
    when {
        Locale.getDefault().toString() == "ru_RU" -> this.text = this.context.getString(R.string.spinnerText, curr.curAbbreviation, curr.curName)
        Locale.getDefault().toString() == "be_BY" -> this.text = this.context.getString(R.string.spinnerText, curr.curAbbreviation, curr.curNameBel)
        else -> this.text = this.context.getString(R.string.spinnerText, curr.curAbbreviation, curr.curNameEng)
    }
}

fun Currency.stringForSpinner(): String {
    return when {
        Locale.getDefault().toString() == "ru_RU" -> App.instance.getString(R.string.spinnerText, this.curAbbreviation, this.curName)
        Locale.getDefault().toString() == "be_BY" -> App.instance.getString(R.string.spinnerText, this.curAbbreviation, this.curNameBel)
        else -> App.instance.getString(R.string.spinnerText, this.curAbbreviation, this.curNameEng)
    }
}

fun Currency.titleForNotification(): String {
    return when {
        Locale.getDefault().toString() == "ru_RU" -> App.instance.getString(R.string.notificationTitle, this.curName)
        Locale.getDefault().toString() == "be_BY" -> App.instance.getString(R.string.notificationTitle, this.curNameBel)
        else -> App.instance.getString(R.string.notificationTitle, this.curNameEng)
    }
}

fun Disposable.putInCompositeDisposible(compositeDisposable: CompositeDisposable) = compositeDisposable.add(this)