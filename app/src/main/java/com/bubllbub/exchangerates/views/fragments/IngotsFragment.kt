package com.bubllbub.exchangerates.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.IngotsRecyclerAdapter
import com.bubllbub.exchangerates.databinding.ErFragmentIngotsBinding
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.viewmodels.IngotsViewModel
import kotlinx.android.synthetic.main.er_fragment_ingots.view.*
import javax.inject.Inject

class IngotsFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentIngotsBinding
    lateinit var ingotViewModel: IngotsViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var adapter: IngotsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.er_fragment_ingots, container, false)
        ingotViewModel = ViewModelProviders.of(this, viewModelFactory)[IngotsViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        binding.ingotsViewModel = ingotViewModel
        binding.executePendingBindings()

        binding.rvIngots.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngots.adapter = adapter

        ingotViewModel.ingots.observe(this,
            Observer<List<Ingot>> { it?.let { adapter.replaceData(it) } })


        val view = binding.root
        setBackDrop(view.app_bar_ingots, view.scroll_view_fragment_fourth)
        return view
    }

    override fun onStart() {
        super.onStart()
        binding.ingotsViewModel?.refresh()
    }
}