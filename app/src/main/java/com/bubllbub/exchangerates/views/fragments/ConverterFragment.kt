package com.bubllbub.exchangerates.views.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.ConverterRecyclerAdapter
import com.bubllbub.exchangerates.databinding.ErFragmentConverterBinding
import com.bubllbub.exchangerates.dialogs.AddCurrencyDialog
import com.bubllbub.exchangerates.dialogs.TAG_CONVERT
import com.bubllbub.exchangerates.elements.SmartDividerItemDecoration
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.recyclerview.SwipeDeleteHelper
import com.bubllbub.exchangerates.viewmodels.ConverterViewModel
import kotlinx.android.synthetic.main.er_fragment_converter.view.*

class ConverterFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentConverterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_converter, container, false)
        val viewModel = ViewModelProviders.of(this).get(ConverterViewModel::class.java)
        binding.converterViewModel = viewModel
        binding.executePendingBindings()

        binding.rvConverter.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ConverterRecyclerAdapter(
            mutableListOf()
        )
        adapter.setHasStableIds(true)
        binding.rvConverter.adapter = adapter
        binding.rvConverter.addItemDecoration(
            SmartDividerItemDecoration(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.er_recycler_devider
                )
            )
        )

        val itemTouchHelper = ItemTouchHelper(
            SwipeDeleteHelper(
                adapter,
                requireContext(),
                binding.scrollViewFragmentConverter,
                object : SwipeDeleteHelper.SwipeDeleteCallback {
                    override fun onDeleteFromSwipe(currency: Currency) {
                        binding.converterViewModel?.deleteConverterCurrency(currency)
                    }

                    override fun onRestoreFromSwipe(currency: Currency) {
                        binding.converterViewModel?.insertConverterCurrency(currency)
                    }
                }
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.rvConverter)

        binding.additionalConverterBtn?.setOnClickListener {
            AddCurrencyDialog().show(childFragmentManager, TAG_CONVERT)
        }

        viewModel.currencies.observe(this,
            Observer<List<Currency>> {
                it?.let { adapter.replaceData(it) }
            }
        )


        val view = binding.root
        setBackDrop(view.app_bar_converter, view.scroll_view_fragment_converter)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.current_rates_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_item -> {
                AddCurrencyDialog().show(childFragmentManager, TAG_CONVERT)
                true
            }
            else -> false
        }
    }

    override fun onStart() {
        super.onStart()
        binding.converterViewModel?.getCurrenciesForConverter()
    }
}