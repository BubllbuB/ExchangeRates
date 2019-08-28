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
import javax.inject.Inject


class ChartRatesFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentChartRatesBinding

    @Inject
    lateinit var chartViewModel: ChartsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_chart_rates, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.chartViewModel = chartViewModel
        binding.executePendingBindings()

        arguments?.let {
            binding.chartViewModel.currentAbbreviation = it.getString(NOTIFICATION_CURRENCY, "USD")
        }

        initSpinner()
        initToggleButtons()

        val view = binding.root
        setBackDrop(view.app_bar_chart, view.scroll_view_fragment_second)
        return view
    }

    private fun initToggleButtons() {
        binding.toggleButtonGroup.addOnButtonCheckedListener(object :
            RadioToggleCheckedListener(requireContext()) {
            override fun executeOnCheck(button: MaterialButton) {
                binding.chartViewModel.currentAmountMonths =
                    button.text.toString().substringBefore(' ').toInt() * -1
                binding.chartViewModel.refresh()
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


                    val defaultCurr =
                        list.find { it.curAbbreviation == binding.chartViewModel.currentAbbreviation }
                    if (defaultCurr != null) {
                        val index = list.indexOf(defaultCurr)
                        binding.chartSpinner.selectedIndex = index

                        binding.chartSpinner.initCurrencySpinner(list, index)
                        binding.chartViewModel.currentId = list[index].curId
                        binding.chartViewModel.currentAbbreviation = list[index].curAbbreviation
                    } else {
                        binding.chartSpinner.initCurrencySpinner(list)
                        binding.chartViewModel.currentId = list[0].curId
                        binding.chartViewModel.currentAbbreviation = list[0].curAbbreviation
                    }
                    binding.chartViewModel.refresh()
                }
            })

        binding.chartSpinner.setOnItemSelectedListener { view, _, _, item ->
            view.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
            view.setCurrencyLeftIcon((item as Currency).curAbbreviation)
            binding.chartViewModel.currentId = item.curId
            binding.chartViewModel.currentAbbreviation = item.curAbbreviation
            binding.chartViewModel.refresh()
        }
    }
}