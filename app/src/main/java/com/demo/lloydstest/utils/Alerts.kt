package com.demo.lloydstest.utils

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.demo.lloydstest.databinding.ProgressLayoutBinding

object Alerts {

    //Loader
    private var customDialog: AlertDialog? = null

    fun showProgress() {
        hideProgress()
        getCurrentActivity().apply {
            val customAlertBuilder = AlertDialog.Builder(this)
            val customAlertView =
                ProgressLayoutBinding.inflate(LayoutInflater.from(this), null, false)
            customAlertBuilder.setView(customAlertView.root)
            customAlertBuilder.setCancelable(false)
            customDialog = customAlertBuilder.create()
            customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialog?.window?.setElevation(0f)
            customDialog?.show()
        }
    }

    fun hideProgress() {
        try {
            if (customDialog != null && customDialog?.isShowing!!) {
                customDialog?.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

