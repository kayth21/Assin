package com.ceaver.assin

import android.app.Application
import android.content.Context
import timber.log.Timber


class AssinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        Timber.plant(Timber.DebugTree())
        Timber.i("onCreate called")
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}