package com.bubllbub.exchangerates.dialogs

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.DialogRecyclerAdapter
import com.bubllbub.exchangerates.databinding.DialogAddCurrencyBinding
import com.bubllbub.exchangerates.ui.widgets.SmartDividerItemDecoration
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.viewmodels.DialogAddCurrencyViewModel
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

const val TAG_FAVORITES = "dialogAddFavorites"
const val TAG_CONVERT = "dialogAddConvert"

class AddCurrencyDialog : DaggerDialogFragment() {
    private lateinit var binding: DialogAddCurrencyBinding
    @Inject
    lateinit var dialogViewModel: DialogAddCurrencyViewModel
    @Inject
    lateinit var adapter: DialogRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_add_currency,
            null,
            false
        )
        binding.dialogAddViewModel = dialogViewModel
        binding.executePendingBindings()

        binding.rvDialogAdd.layoutManager = LinearLayoutManager(context)
        adapter.setHasStableIds(true)
        binding.rvDialogAdd.adapter = adapter
        binding.rvDialogAdd.addItemDecoration(
            SmartDividerItemDecoration(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.er_recycler_devider
                )
            )
        )

        dialogViewModel.currencies.observe(this,
            Observer<List<Currency>> { it?.let { adapter.replaceData(it) } })

        setFullSize()

        binding.dialogAddConfirm.setOnClickListener {
            adapter.getSelectedCurrency()?.let { curr ->
                when (this.tag) {
                    TAG_FAVORITES -> binding.dialogAddViewModel?.addFavoriteCurrency(curr)
                    else -> binding.dialogAddViewModel?.addConverterCurrency(curr)
                }
            }
            dismiss()
        }

        binding.dialogAddCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun setFullSize() {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params = binding.rvDialogAdd.layoutParams
        params.width =
            size.x - requireContext().resources.getDimension(R.dimen.itemMarginDefault).toInt() * 4
        binding.rvDialogAdd.layoutParams = params
    }

    override fun onStart() {
        super.onStart()
        binding.dialogAddViewModel?.refresh(this.tag)
    }
}