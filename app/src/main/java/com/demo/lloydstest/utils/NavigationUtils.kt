package com.demo.lloydstest.utils

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.demo.lloydstest.MainActivity
import com.demo.lloydstest.R


/**
 * Navigate using destination ID
 * */
fun View.navigateWithId(id: Int, bundle: Bundle? = null) = try {
    this.findNavController().navigate(id, bundle)
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * Navigate using Nav Directions
 * */
fun View.navigateWithAction(action: NavDirections) = try {
    this.findNavController().navigate(action)
} catch (e: Exception) {
    e.printStackTrace()
}

/**
 * Navigate to previous screen
 * */
fun View.navigateBack() = try {
    (MainActivity.context.get() as MainActivity).onBackPressed()
//    this.findNavController().navigateUp()
} catch (e: Exception) {
    e.printStackTrace()
}

fun <T : Any?> View.setBackStackPop(key: String, data: T, popBack: Boolean = true) {
    kotlin.runCatching {
        Navigation.findNavController(
            this
        ).previousBackStackEntry?.savedStateHandle?.set(key, data)
        if (popBack)
            findNavController().popBackStack()
    }.onFailure { err -> err.printStackTrace() }
}


fun <T : Any?> Fragment.cleanBackStackPop(key: String) {
    kotlin.runCatching {
        Navigation.findNavController(this.requireView())
            .currentBackStackEntry?.savedStateHandle?.remove<T>(key)
    }.onFailure { err -> err.printStackTrace() }
}

fun <T : Any?> Fragment.getBackStackPop(key: String, result: (T) -> Unit) {
    kotlin.runCatching {
        Navigation.findNavController(this.requireView())
            .currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
            ?.observe(viewLifecycleOwner) {
                result(it)
            }
    }.onFailure { err -> err.printStackTrace() }
}

fun <T : Any?> Fragment.containBackStack(key: String):Boolean {
    kotlin.runCatching {
        return Navigation.findNavController(this.requireView())
            .currentBackStackEntry?.savedStateHandle?.contains(key)?:false
    }.onFailure { err -> err.printStackTrace() }
    return false
}

private fun getCurrentNavGraph(): Int {
    return  R.id.navGraph
}

fun getCurrentActivity(): Activity {
    return MainActivity.context.get() as MainActivity
}

fun View.navigateBackCustomer() = try {
    this.findNavController().navigateUp()
} catch (e: Exception) {
    e.printStackTrace()
}
