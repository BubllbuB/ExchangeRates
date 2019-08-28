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
import com.bubllbub.exchangerates.databinding.ErFragmentChartRatesBinding
import com.bubllbub.exchangerates.extensions.initCurrencySpinner
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.bubllbub.exchangerates.extensions.setWidthChildFull
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.ui.listeners.RadioToggleCheckedListener
import com.bubllbub.exchangerates.viewmodels.ChartsViewModel
import com.bubllbub.exchangerates.viewmodels.SIX_MONTHS_PERIOD
import com.bubllbub.exchangerates.viewmodels.THREE_MONTHS_PERIOD
import com.bubllbub.exchangerates.viewmodels.TWELVE_MONTHS_PERIOD
import com.bubllbub.exchangerates.workers.NOTIFICATION_CURRENCY
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.er_fragment_chart_rates.view.*
import javax.inject.Inject


class ChartRatesFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentChartRatesBinding

    lateinit var chartViewModel: ChartsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_chart_rates, container, false)
        chartViewModel = ViewModelProviders.of(this, viewModelFactory)[ChartsViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        binding.chartViewModel = chartViewModel
        binding.executePendingBindings()

        arguments?.let {
            chartViewModel.currentAbbreviation = it.getString(NOTIFICATION_CURRENCY, "USD")
        }

        if (chartViewModel.currencies.value.isNullOrEmpty()) {
            chartViewModel.getActualList()
        }

        initSpinner()
        initToggleButtons()

        if (chartViewModel.isLoading.get() == true) {
            chartViewModel.refresh()
        }

        val view = binding.root
        setBackDrop(view.app_bar_chart, view.scroll_view_fragment_second)
        return view
    }

    private fun initToggleButtons() {
        binding.toggleButtonGroup.addOnButtonCheckedListener(object :
            RadioToggleCheckedListener(requireContext()) {
            override fun executeOnCheck(button: MaterialButton) {
                chartViewModel.currentAmountMonths.value =
                    button.text.toString().substringBefore(' ').toInt()
                chartViewModel.refresh()
            }
        })
        binding.toggleButtonGroup.setWidthChildFull(
            requireActivity().windowManager.defaultDisplay,
            requireContext()
        )

        chartViewModel.currentAmountMonths.observe(this,
            Observer { amountMonths ->
                when (amountMonths) {
                    THREE_MONTHS_PERIOD -> {
                        binding.button3Months.isChecked = true
                    }
                    SIX_MONTHS_PERIOD -> {
                        binding.button6Months.isChecked = true
                    }
                    TWELVE_MONTHS_PERIOD -> {
                        binding.button12Months.isChecked = true
                    }
                }
            })
    }

    private fun initSpinner() {
        chartViewModel.currencies.observe(this,
            Observer<List<Currency>> { currencies ->
                currencies?.let { list ->
                    val adapter = SpinnerImageAdapter(requireContext(), list)
                    binding.chartSpinner.setAdapter(adapter)

                    val index =
                        list.indexOfFirst { it.curAbbreviation == chartViewModel.currentAbbreviation }
                    binding.chartSpinner.initCurrencySpinner(list, index)

                }
            })

        binding.chartSpinner.setOnItemSelectedListener { view, _, _, item ->
            view.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
            view.setCurrencyLeftIcon((item as Currency).curAbbreviation)
            chartViewModel.currentId = item.curId
            chartViewModel.currentAbbreviation = item.curAbbreviation
            chartViewModel.refresh()
        }
    }
}