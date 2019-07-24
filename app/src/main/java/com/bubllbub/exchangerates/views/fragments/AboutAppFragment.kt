package com.bubllbub.exchangerates.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.databinding.ErFragmentAboutBinding
import kotlinx.android.synthetic.main.er_fragment_about.view.*

class AboutAppFragment : BackDropFragment() {
    private lateinit var binding: ErFragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.er_fragment_about, container, false)

        val view = binding.root
        setBackDrop(view.app_bar_about, view.scroll_view_fragment_seventh)
        return view
    }
}