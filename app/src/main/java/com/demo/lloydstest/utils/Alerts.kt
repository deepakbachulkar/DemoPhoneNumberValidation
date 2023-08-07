package com.demo.lloydstest.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.demo.lloydstest.databinding.ProgressLayoutBinding

object Alerts {


    var dialog: Dialog? = null
    fun View.showAlertWrap(
        @LayoutRes layout: Int,
        cancelable: Boolean = false,
        cancelableOnTouchOutside: Boolean = false,
        viewSend: (View, Dialog) -> Unit
    ) {
        try {
            dialog?.dismiss()
            dialog = Dialog(this.context)
            val view = LayoutInflater.from(this.context).inflate(layout, null)
            dialog?.let { dialog ->
                dialog.setContentView(view)
                dialog.setCancelable(cancelable)
                dialog.setCanceledOnTouchOutside(cancelableOnTouchOutside)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                viewSend(view, dialog)
                dialog.show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



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

