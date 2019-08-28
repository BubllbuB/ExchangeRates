package com.bubllbub.exchangerates.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.SpinnerImageAdapter
import com.bubllbub.exchangerates.databinding.ErFragmentRateOnDateBinding
import com.bubllbub.exchangerates.extensions.initCurrencySpinner
import com.bubllbub.exchangerates.extensions.initWithTodayMaxDate
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.bubllbub.exchangerates.extensions.setMinMaxRangeFromCurrency
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.viewmodels.RateOnDateViewModel
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import kotlinx.android.synthetic.main.er_fragment_rate_on_date.view.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

const val LENGTH_ABBREVIATION = 3

class RateOnDateFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentRateOnDateBinding
    private lateinit var datepicker: CalendarDatePickerDialogFragment
    @Inject
    lateinit var currentViewModel: RateOnDateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_rate_on_date, container, false)
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
        val currencyName =
            binding.rateOnDateSpinner.text.toString().substring(0, LENGTH_ABBREVIATION)
        binding.rateOnDateViewModel?.refresh(currencyName)
    }

    private fun refreshDateRange() {
        val currencyName =
            binding.rateOnDateSpinner.text.toString().substring(0, LENGTH_ABBREVIATION)
        val currency =
            binding.rateOnDateViewModel?.currencies?.value?.find { it.curAbbreviation == currencyName }
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
                    val adapter = SpinnerImageAdapter(requireContext(), list)
                    binding.rateOnDateSpinner.setAdapter(adapter)
                    binding.rateOnDateSpinner.initCurrencySpinner(list)

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
}