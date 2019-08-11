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
import com.bubllbub.exchangerates.databinding.ErFragmentChartRatesBinding
import com.bubllbub.exchangerates.elements.RadioToggleCheckedListener
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.extensions.initCurrencySpinner
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.bubllbub.exchangerates.extensions.setWidthChildFull
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.viewmodels.ChartsViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.er_fragment_chart_rates.view.*
import java.util.*


class ChartRatesFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentChartRatesBinding
    private val finishDate = Date()
    private val startDate = Date()
    private var currentId = 145
    private var currentAbbreviation = "USD"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_chart_rates, container, false)
        binding.lifecycleOwner = this
        val viewModel = ViewModelProviders.of(this).get(ChartsViewModel::class.java)
        binding.chartViewModel = viewModel
        binding.executePendingBindings()

        initSpinner()
        initToggleButtons()

        val view = binding.root
        setBackDrop(view.app_bar_chart, view.scroll_view_fragment_second)
        return view
    }

    private fun refreshChartDate() {
        binding.chartViewModel?.refresh(currentId, currentAbbreviation, startDate, finishDate)
    }

    private fun setupForDates(amountMonths: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = finishDate
        calendar.add(Calendar.MONTH, amountMonths)
        startDate.time = calendar.timeInMillis
    }

    private fun initToggleButtons() {
        binding.toggleButtonGroup.addOnButtonCheckedListener(object :
            RadioToggleCheckedListener(requireContext()) {
            override fun executeOnCheck(button: MaterialButton) {
                setupForDates(button.text.toString().substringBefore(' ').toInt() * -1)
                refreshChartDate()
            }
        })
        binding.button3Months.performClick()
        binding.toggleButtonGroup.setWidthChildFull(
            requireActivity().windowManager.defaultDisplay,
            requireContext()
        )
    }

    private fun initSpinner() {
        binding.chartViewModel?.getActualList()

        binding.chartViewModel?.currencies?.observe(this,
            Observer<List<Currency>> { currencies ->
                currencies?.let { list ->
                    val sortedList =
                        list.sortedBy { CurrencyRes.valueOf(it.curAbbreviation).ordinal }

                    val adapter = SpinnerImageAdapter(requireContext(), sortedList)
                    binding.chartSpinner.setAdapter(adapter)
                    binding.chartSpinner.initCurrencySpinner(sortedList)
                    currentId = sortedList[0].curId
                    currentAbbreviation = sortedList[0].curAbbreviation
                    refreshChartDate()
                }
            })

        binding.chartSpinner.setOnItemSelectedListener { view, position, id, item ->
            view.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
            view.setCurrencyLeftIcon((item as Currency).curAbbreviation)
            currentId = item.curId
            currentAbbreviation = item.curAbbreviation
            refreshChartDate()
        }
    }
}