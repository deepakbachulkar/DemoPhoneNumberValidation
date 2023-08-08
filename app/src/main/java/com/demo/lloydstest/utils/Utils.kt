@file:Suppress("DEPRECATION")

package com.demo.lloydstest.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

/** Internet Connection Detector */
fun Context.isNetworkAvailable(): Boolean {
    val service = Context.CONNECTIVITY_SERVICE
    val manager = getSystemService(service) as ConnectivityManager?
    val network = manager?.activeNetworkInfo
    return (network != null)
}

/** Positive Alerter*/
fun Context.showNegativeAlerter(message: String) {
    Snackbar.make(
        (this as Activity).findViewById(android.R.id.content),
        message,
        Snackbar.LENGTH_LONG
    ).show()
}

fun Activity.hideKeyboard() {
    val inputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = this.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    if (message.isNotEmpty()) {
        try {
            Toast.makeText(this, message, duration).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}