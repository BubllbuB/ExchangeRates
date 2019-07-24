package com.bubllbub.exchangerates.backdrop

import androidx.fragment.app.Fragment

interface NavigationHost {
    fun navigateTo(fragment: Fragment, addToBackstack: Boolean = false, menuNavigate: Boolean = true)
}