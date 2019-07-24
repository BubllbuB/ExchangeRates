package com.bubllbub.exchangerates.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.backdrop.NavigationHost
import com.bubllbub.exchangerates.views.fragments.CurrentRatesFragment
import com.bubllbub.exchangerates.views.fragments.FRAGMENT_FROM_NAVIGATE

class MainActivity : AppCompatActivity(), NavigationHost {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.er_activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.container,
                    CurrentRatesFragment()
                )
                .commit()
        }
    }

    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean, menuNavigate: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        if(menuNavigate) {
            val bundle = Bundle()
            bundle.putBoolean(FRAGMENT_FROM_NAVIGATE, true)
            fragment.arguments = bundle
        }

        transaction.commit()
    }
}
