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
import com.bubllbub.exchangerates.databinding.ErFragmentChartRatesBinding
import com.bubllbub.exchangerates.extensions.initCurrencySpinner
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.bubllbub.exchangerates.extensions.setWidthChildFull
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.ui.listeners.RadioToggleCheckedListener
import com.bubllbub.exchangerates.viewmodels.ChartsViewModel
import com.bubllbub.exchangerates.workers.NOTIFICATION_CURRENCY
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.er_fragment_chart_rates.view.*
import java.util.*
import javax.inject.Inject


class ChartRatesFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentChartRatesBinding
    private val finishDate = Date()
    private val startDate = Date()
    private var currentId = 145
    private var currentAbbreviation = "USD"

    @Inject
    lateinit var chartViewModel: ChartsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_chart_rates, container, false)
        binding.lifecycleOwner = this
        binding.chartViewModel = chartViewModel
        binding.executePendingBindings()

        arguments?.let {
            currentAbbreviation = it.getString(NOTIFICATION_CURRENCY, "USD")
        }

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
                    val adapter = SpinnerImageAdapter(requireContext(), list)
                    binding.chartSpinner.setAdapter(adapter)


                    val defaultCurr = list.find { it.curAbbreviation == currentAbbreviation }
                    if (defaultCurr != null) {
                        val index = list.indexOf(defaultCurr)
                        binding.chartSpinner.selectedIndex = index

                        binding.chartSpinner.initCurrencySpinner(list, index)
                        currentId = list[index].curId
                        currentAbbreviation = list[index].curAbbreviation
                    } else {
                        binding.chartSpinner.initCurrencySpinner(list)
                        currentId = list[0].curId
                        currentAbbreviation = list[0].curAbbreviation
                    }
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