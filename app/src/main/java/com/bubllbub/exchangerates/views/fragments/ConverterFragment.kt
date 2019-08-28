package com.bubllbub.exchangerates.views.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.ConverterRecyclerAdapter
import com.bubllbub.exchangerates.databinding.ErFragmentConverterBinding
import com.bubllbub.exchangerates.dialogs.AddCurrencyDialog
import com.bubllbub.exchangerates.dialogs.TAG_CONVERT
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.ui.recyclerview.SwipeDeleteHelper
import com.bubllbub.exchangerates.ui.widgets.SmartDividerItemDecoration
import com.bubllbub.exchangerates.viewmodels.ConverterViewModel
import kotlinx.android.synthetic.main.er_fragment_converter.view.*
import javax.inject.Inject

const val RECYCLER_POSITION = "recyclerLayoutPosition"
const val RECYCLER_EDITTEXT_SELECTION_POSITION_START = "recyclerEdittextPositionStart"
const val RECYCLER_EDITTEXT_SELECTION_POSITION_END = "recyclerEdittextPositionEnd"

class ConverterFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentConverterBinding

    lateinit var converterViewModel: ConverterViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        binding =
            DataBindingUtil.inflate(inflater, R.layout.er_fragment_converter, container, false)
        converterViewModel =
            ViewModelProviders.of(this, viewModelFactory)[ConverterViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        binding.converterViewModel = converterViewModel
        binding.executePendingBindings()

        binding.rvConverter.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ConverterRecyclerAdapter(
            mutableListOf(),
            object : ConverterRecyclerAdapter.OnConverterCurrencyCalcListener {
                override fun recalculateAmounts(amount: Double, activeCurr: Currency) {
                    converterViewModel.recalculateAmount(amount, activeCurr)
                }
            })
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

        converterViewModel.currencies.observe(this,
            Observer<List<Currency>> {
                it?.let {
                    adapter.replaceData(it)
                }
            }
        )

        val view = binding.root
        setBackDrop(view.app_bar_converter, view.scroll_view_fragment_converter)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        (binding.rvConverter.adapter as ConverterRecyclerAdapter).savedFocusToInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (binding.rvConverter.adapter as ConverterRecyclerAdapter).restoreFocusFromInstanceState(
            savedInstanceState
        )
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