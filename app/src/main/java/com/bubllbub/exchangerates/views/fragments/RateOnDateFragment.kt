package com.bubllbub.exchangerates.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.SpinnerImageAdapter
import com.bubllbub.exchangerates.databinding.ErFragmentRateOnDateBinding
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.viewmodels.RateOnDateViewModel
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter
import kotlinx.android.synthetic.main.er_fragment_rate_on_date.view.*
import org.joda.time.DateTime
import java.util.*


class RateOnDateFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentRateOnDateBinding
    private lateinit var datepicker: CalendarDatePickerDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_rate_on_date, container, false)
        binding.lifecycleOwner = this
        val viewModel = ViewModelProviders.of(this).get(RateOnDateViewModel::class.java)
        binding.rateOnDateViewModel = viewModel
        binding.executePendingBindings()

        initDatePicker()
        initSpinner()

        val view = binding.root
        setBackDrop(view.app_bar_rate_on_date, view.scroll_view_fragment_third)
        return view
    }

    private fun refreshDate() {
        val currencyName =
            binding.rateOnDateSpinner.text.toString().substring(0, 3)
        binding.rateOnDateViewModel?.refresh(currencyName)
    }

    private fun refreshDateRange() {
        val currencyName =
            binding.rateOnDateSpinner.text.toString().substring(0, 3)
        val currency =
            binding.rateOnDateViewModel?.currencies?.value?.find { it.curAbbreviation == currencyName }
        currency?.let {
            val calendar = Calendar.getInstance()
            calendar.time = it.curDateStart

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

            datepicker.setDateRange(minDate, maxDate)
        }
    }

    private fun initDatePicker() {
        val calendar = Calendar.getInstance()
        val maxDate = MonthAdapter.CalendarDay(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datepicker = CalendarDatePickerDialogFragment()
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setPreselectedDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            .setDateRange(null, maxDate)
            .setThemeCustom(R.style.Widget_ExRates_DatePicker)

        datepicker.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            val date = DateTime().withMillis(cal.timeInMillis).withTimeAtStartOfDay()
            binding.rateOnDateViewModel?.date?.set(date)
            refreshDate()
        }

        binding.selectDateButton.setOnClickListener {
            datepicker.show(this.childFragmentManager, null)
        }
    }

    private fun initSpinner() {
        binding.rateOnDateViewModel?.getActualList()

        binding.rateOnDateViewModel?.currencies?.observe(this,
            Observer<List<Currency>> { currencies ->
                currencies?.let { list ->
                    val sortedList = list.sortedBy { CurrencyRes.valueOf(it.curAbbreviation).ordinal }

                    val adapter = SpinnerImageAdapter(requireContext(), sortedList)
                    binding.rateOnDateSpinner.setAdapter(adapter)
                    binding.rateOnDateSpinner.setCurrencyLeftIcon(sortedList[0].curAbbreviation)
                    refreshDateRange()
                    refreshDate()
                }
            })

        binding.rateOnDateSpinner.background =
            ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
        binding.rateOnDateSpinner.elevation = 0f
        binding.rateOnDateSpinner.popupWindow.elevation = 0f
        binding.rateOnDateSpinner.popupWindow.setBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.spinner_dropdown_bg,
                null
            )
        )

        binding.rateOnDateSpinner.setOnClickListener {
            it.background =
                ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg_opened, null)
        }
        binding.rateOnDateSpinner.setOnNothingSelectedListener {
            it.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
        }
        binding.rateOnDateSpinner.setOnItemSelectedListener { view, position, id, item ->
            view.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
            view.setCurrencyLeftIcon(item.toString().substring(0,3))
            refreshDateRange()
            refreshDate()
        }
    }
}