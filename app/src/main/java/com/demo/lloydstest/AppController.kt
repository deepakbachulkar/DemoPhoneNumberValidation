package com.demo.lloydstest

import android.app.Application
import android.content.Context
import com.demo.lloydstest.utils.Constants
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference

@HiltAndroidApp
class AppController : Application() {
    companion object {
        var mInstance: WeakReference<Context>? = null
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = WeakReference(this.applicationContext)
        Constants.API_KEY = getString(R.string.api_key)
        Constants.BASE_URL = getString(R.string.base_url)
    }
}