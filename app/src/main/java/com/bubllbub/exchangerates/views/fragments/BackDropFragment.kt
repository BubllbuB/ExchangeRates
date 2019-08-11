package com.bubllbub.exchangerates.views.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.bubllbub.exchangerates.R
import com.bubllbub.exchangerates.backdrop.NavigationIconClickListener
import com.bubllbub.exchangerates.views.MainActivity
import kotlinx.android.synthetic.main.er_backdrop.*
import kotlinx.android.synthetic.main.er_main_buttons.*

const val FRAGMENT_FROM_NAVIGATE = "openFromNavigate"

open class BackDropFragment : Fragment() {
    private lateinit var viewAnimate: View
    private lateinit var toolbarAnimate: Toolbar
    private var openFromNavigate = false

    protected fun setBackDrop(toolbar: Toolbar, scrollView: NestedScrollView) {
        viewAnimate = scrollView
        toolbarAnimate = toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener(
            NavigationIconClickListener(
                activity!!,
                scrollView,
                AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(context!!, R.drawable.er_backdrop_menu), // Menu open icon
                ContextCompat.getDrawable(context!!, R.drawable.er_close_menu) // Menu close icon
            )
        )

        openFromNavigate = arguments?.getBoolean(FRAGMENT_FROM_NAVIGATE, false) ?: false

        if (openFromNavigate) {
            animateBackDrop(R.drawable.er_close_menu, 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goToMenu()
        goToMainButtons()

        if (openFromNavigate) {
            animateBackDrop(R.drawable.er_backdrop_menu, 500)
        }
    }

    private fun goToMenu() {
        menuFragmentCurrentRates?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(CurrentRatesFragment())
        }

        menuFragmentChartRates?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(ChartRatesFragment())
        }

        menuFragmentRateOnDate?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(RateOnDateFragment())
        }

        menuFragmentIngots?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(IngotsFragment())
        }

        menuFragmentCalculator?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(ConverterFragment())
        }

        menuFragmentAbout?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(AboutAppFragment())
        }
    }

    private fun goToMainButtons() {
        mainButtonFirst?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(
                mainButtonFirst.getNavigateFragment(),
                menuNavigate = false
            )
        }
        mainButtonSecond?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(
                mainButtonSecond.getNavigateFragment(),
                menuNavigate = false
            )
        }
        mainButtonThird?.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(
                mainButtonThird.getNavigateFragment(),
                menuNavigate = false
            )
        }
    }

    private fun animateBackDrop(icon: Int, duration: Long) {
        toolbarAnimate.setNavigationIcon(icon)

        val translateY = when (icon) {
            R.drawable.er_backdrop_menu -> 0F
            else -> {
                val displayMetrics = DisplayMetrics()
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                val height = displayMetrics.heightPixels

                (height - requireContext().resources.getDimensionPixelSize(R.dimen.er_backdrop_reveal_height)).toFloat()
            }
        }

        val animatorSet = AnimatorSet()
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()


        val animator = ObjectAnimator.ofFloat(viewAnimate, "translationY", translateY)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()

        animatorSet.play(animator)
        animator.start()
    }
}