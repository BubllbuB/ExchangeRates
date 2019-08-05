package com.bubllbub.exchangerates.dialogs

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.DialogRecyclerAdapter
import com.bubllbub.exchangerates.databinding.DialogAddCurrencyBinding
import com.bubllbub.exchangerates.objects.Currency
import com.bubllbub.exchangerates.viewmodels.DialogAddCurrencyViewModel

const val TAG_FAVORITES = "dialogAddFavorites"
const val TAG_CONVERT = "dialogAddConvert"

class AddCurrencyDialog : DialogFragment() {
    private lateinit var binding: DialogAddCurrencyBinding

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
        val viewModel = ViewModelProviders.of(this).get(DialogAddCurrencyViewModel::class.java)
        binding.dialogAddViewModel = viewModel
        binding.executePendingBindings()

        binding.rvDialogAdd.layoutManager = LinearLayoutManager(context)
        val adapter = DialogRecyclerAdapter(mutableListOf())
        adapter.setHasStableIds(true)
        binding.rvDialogAdd.adapter = adapter

        viewModel.currencies.observe(this,
            Observer<List<Currency>> { it?.let { adapter.replaceData(it) } })

        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params = binding.rvDialogAdd.layoutParams
        params.width = (size.x - requireContext().resources.displayMetrics.density * 64).toInt()
        binding.rvDialogAdd.layoutParams = params

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

    override fun onStart() {
        super.onStart()
        binding.dialogAddViewModel?.refresh(this.tag)
    }
}