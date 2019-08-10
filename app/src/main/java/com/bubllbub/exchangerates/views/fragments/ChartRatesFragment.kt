package com.bubllbub.exchangerates.views.fragments

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.SpinnerImageAdapter
import com.bubllbub.exchangerates.databinding.ErFragmentChartRatesBinding
import com.bubllbub.exchangerates.elements.RadioToggleCheckedListener
import com.bubllbub.exchangerates.enums.CurrencyRes
import com.bubllbub.exchangerates.extensions.setCurrencyLeftIcon
import com.bubllbub.exchangerates.extensions.setCustomFont
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.viewmodels.ChartsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
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
        binding.toggleButtonGroup.addOnButtonCheckedListener(object: RadioToggleCheckedListener() {
            override fun onButtonChecked(
                group: MaterialButtonToggleGroup,
                checkedId: Int,
                isChecked: Boolean
            ) {
                if (isChecked) {
                    for (index in 0 until group.childCount) {
                        val button = group.getChildAt(index) as MaterialButton

                        if (button.id == checkedId) {
                            button.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.toggleButtonColor
                                )
                            )
                            button.setTextColor(Color.parseColor("#ffffff"))
                            if(!checkPrevExecute(checkedId)) {
                                setupForDates(button.text.toString().substringBefore(' ').toInt() * -1)
                                refreshChartDate()
                            }
                        } else {
                            button.setBackgroundColor(0x00000000)
                            button.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.toggleButtonColor
                                )
                            )
                        }
                    }
                }
            }
        })
        binding.button3Months.performClick()

        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params = binding.button3Months.layoutParams
        params.width = (size.x - requireContext().resources.displayMetrics.density * 88).toInt() / 3
        binding.button3Months.layoutParams = params
        binding.button6Months.layoutParams = params
        binding.button12Months.layoutParams = params
    }

    private fun initSpinner() {
        binding.chartViewModel?.getActualList()

        binding.chartViewModel?.currencies?.observe(this,
            Observer<List<Currency>> { currencies ->
                currencies?.let { list ->
                    val sortedList = list.sortedBy { CurrencyRes.valueOf(it.curAbbreviation).ordinal }

                    val adapter = SpinnerImageAdapter(requireContext(), sortedList)
                    binding.chartSpinner.setAdapter(adapter)
                    binding.chartSpinner.setCustomFont(R.font.open_sans_bold)
                    binding.chartSpinner.setCurrencyLeftIcon(sortedList[0].curAbbreviation)
                    currentId = sortedList[0].curId
                    currentAbbreviation = sortedList[0].curAbbreviation
                    refreshChartDate()
                }
            })
        binding.chartSpinner.background =
            ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
        binding.chartSpinner.elevation = 0f
        binding.chartSpinner.popupWindow.elevation = 0f
        binding.chartSpinner.popupWindow.setBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.spinner_dropdown_bg,
                null
            )
        )

        binding.chartSpinner.setOnClickListener {
            it.background =
                ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg_opened, null)
        }
        binding.chartSpinner.setOnNothingSelectedListener {
            it.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
        }
        binding.chartSpinner.setOnItemSelectedListener { view, position, id, item ->
            view.background = ResourcesCompat.getDrawable(resources, R.drawable.spinner_bg, null)
            view.setCurrencyLeftIcon((item as Currency).curAbbreviation)
            currentId = item.curId
            currentAbbreviation = item.curAbbreviation
            refreshChartDate()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}