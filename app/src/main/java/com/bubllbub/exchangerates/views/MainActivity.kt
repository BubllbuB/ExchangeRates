package com.bubllbub.exchangerates.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.backdrop.NavigationHost
import com.bubllbub.exchangerates.views.fragments.CurrentRatesFragment
import com.bubllbub.exchangerates.views.fragments.FRAGMENT_FROM_NAVIGATE
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity(), NavigationHost {
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

        if (menuNavigate) {
            val bundle = Bundle()
            bundle.putBoolean(FRAGMENT_FROM_NAVIGATE, true)
            fragment.arguments = bundle
        } else {
            transaction.setCustomAnimations(
                R.anim.er_enter_from_right,
                R.anim.er_exit_to_left,
                R.anim.er_enter_from_left,
                R.anim.er_exit_to_right
            )
        }

        transaction.replace(R.id.container, fragment)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}
