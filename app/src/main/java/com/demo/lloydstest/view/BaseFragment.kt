package com.demo.lloydstest.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController

open class BaseFragment(val layout: Int) : Fragment(layout), BackPressFeature{

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressOperation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        onResumed()
    }

    private fun disableOnBackPressedCallback() {
        onBackPressedCallback.isEnabled = false
    }

    override fun onStop() {
        disableOnBackPressedCallback()
        onBaseStop()
        super.onStop()
    }

    override fun onBackPressOperation() {
        onClickBack()
    }

    override fun onBaseStop() {}

    fun onClickBack() {
        kotlin.runCatching {
            requireView().findNavController().popBackStack()
        }.onFailure { it.printStackTrace() }
    }

    fun View.navigateTo(destination: Int, bundle: Bundle? = null) {
        try {
            this.findNavController().navigate(destination,bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

interface BackPressFeature {
    fun onBackPressOperation()
    fun onBaseStop()
    fun onResumed() {}
}