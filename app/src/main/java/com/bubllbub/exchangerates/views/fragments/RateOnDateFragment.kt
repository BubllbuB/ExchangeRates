package com.bubllbub.exchangerates.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.SpinnerImageAdapter
import com.bubllbub.exchangerates.databinding.ErFragmentRateOnDateBinding
import com.bubllbub.exchangerates.extensions.initCurrencySpinner
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.bubllbub.exchangerates.extensions.setMinMaxRangeFromCurrency
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.viewmodels.RateOnDateViewModel
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter
import kotlinx.android.synthetic.main.er_fragment_rate_on_date.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

const val LENGTH_ABBREVIATION = 3

class RateOnDateFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentRateOnDateBinding
    private lateinit var datepicker: CalendarDatePickerDialogFragment
    lateinit var currentViewModel: RateOnDateViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_rate_on_date, container, false)
        currentViewModel =
            ViewModelProviders.of(this, viewModelFactory)[RateOnDateViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rateOnDateViewModel = currentViewModel
        binding.executePendingBindings()

        initDatePicker()
        initSpinner()

        val view = binding.root
        setBackDrop(view.app_bar_rate_on_date, view.scroll_view_fragment_third)
        return view
    }

    private fun refreshDate() {
        currentViewModel.currentAbbreviation =
            binding.rateOnDateSpinner.text.toString().substring(0, LENGTH_ABBREVIATION)
        currentViewModel.refresh()
    }

    private fun refreshDateRange() {
        val currencyName =
            binding.rateOnDateSpinner.text.toString().substring(0, LENGTH_ABBREVIATION)
        val currency =
            currentViewModel.currencies.value?.find { it.curAbbreviation == currencyName }
        currency?.let {
            datepicker.setMinMaxRangeFromCurrency(it)
        }
    }

    private fun initDatePicker() {

        datepicker = initWithTodayMaxDate()

        datepicker.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            val date = DateTime().withMillis(cal.timeInMillis).withTimeAtStartOfDay()
            currentViewModel.date.set(date)
            refreshDate()
        }

        binding.selectDateButton.setOnClickListener {
            datepicker.show(this.childFragmentManager, null)
        }
    }

    private fun initSpinner() {
        currentViewModel.getActualList()

        currentViewModel.currencies.observe(this,
            Observer<List<Currency>> { currencies ->
                currencies?.let { list ->
                    val adapter = SpinnerImageAdapter(requireContext(), list)
                    binding.rateOnDateSpinner.setAdapter(adapter)
                    val index =
                        list.indexOfFirst { it.curAbbreviation == currentViewModel.currentAbbreviation }
                    binding.rateOnDateSpinner.initCurrencySpinner(list, index)

                    refreshDateRange()
                    refreshDate()
                }
            })


        binding.rateOnDateSpinner.setOnItemSelectedListener { view, _, _, item ->
            view.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
            view.setCurrencyLeftIcon(item.toString().substring(0, LENGTH_ABBREVIATION))
            refreshDateRange()
            refreshDate()
        }
    }

    private fun initWithTodayMaxDate(): CalendarDatePickerDialogFragment {
        val calendar = Calendar.getInstance()
        val maxDate = MonthAdapter.CalendarDay(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        return CalendarDatePickerDialogFragment()
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setPreselectedDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            .setDateRange(null, maxDate)
            .setThemeCustom(R.style.Widget_ExRates_DatePicker)
    }
}