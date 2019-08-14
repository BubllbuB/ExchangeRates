package com.bubllbub.exchangerates.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.views.fragments.BackDropFragment
import com.bubllbub.exchangerates.views.fragments.ChartRatesFragment
import com.bubllbub.exchangerates.views.fragments.ConverterFragment
import com.bubllbub.exchangerates.views.fragments.CurrentRatesFragment
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class LinearLayoutNavigate(cxt: Context, attrs: AttributeSet) :
    LinearLayout(cxt, attrs) {

    companion object {
        const val NAV_RATES = 1
        const val NAV_CHART = 2
        const val NAV_CONVERTER = 3

        val NAV_RATES_NAME = CurrentRatesFragment::class
        val NAV_CHART_NAME = ChartRatesFragment::class
        val NAV_CONVERTER_NAME = ConverterFragment::class
    }

    private var navFragment: KClass<out BackDropFragment> = CurrentRatesFragment::class
    private var navState = NAV_RATES
        set(state) {
            field = state
            navFragment = when(state) {
                NAV_RATES ->  NAV_RATES_NAME
                NAV_CHART ->  NAV_CHART_NAME
                else -> NAV_CONVERTER_NAME
            }
            invalidate()
        }

    init {
        this.isClickable = true
        setupAttributes(attrs)
        this.setOnClickListener {
        }
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.LinearLayoutNavigate,
            0, 0
        )

        navState = typedArray.getInt(R.styleable.LinearLayoutNavigate_navigateTo, NAV_RATES)

        typedArray.recycle()
    }

    fun getNavigateFragment(): BackDropFragment {
        return navFragment.createInstance()
    }
}