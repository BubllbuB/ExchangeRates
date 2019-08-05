package com.bubllbub.exchangerates.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.adapters.IngotsRecyclerAdapter
import com.bubllbub.exchangerates.databinding.ErFragmentIngotsBinding
import com.bubllbub.exchangerates.objects.Ingot
import com.bubllbub.exchangerates.viewmodels.IngotsViewModel
import kotlinx.android.synthetic.main.er_fragment_ingots.view.*

class IngotsFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentIngotsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.er_fragment_ingots, container, false)
        val viewModel = ViewModelProviders.of(this).get(IngotsViewModel::class.java)
        binding.ingotsViewModel = viewModel
        binding.executePendingBindings()

        binding.rvIngots.layoutManager = LinearLayoutManager(requireContext())
        val adapter = IngotsRecyclerAdapter(arrayListOf(), object: IngotsRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
            }
        })
        binding.rvIngots.adapter = adapter

        viewModel.ingots.observe(this,
            Observer<List<Ingot>> { it?.let{ adapter.replaceData(it)} })


        val view = binding.root
        setBackDrop(view.app_bar_ingots, view.scroll_view_fragment_fourth)
        return view
    }

    override fun onStart() {
        super.onStart()

        binding.ingotsViewModel?.refresh()
    }
}