package com.ceaver.assin.system

import android.content.Context
import com.ceaver.assin.AssinApplication

private const val SHARED_PREFERENCES_KEY = "com.ceaver.assin.system.SystemRepository.SharedPreferences"
private const val IS_INITIALIZED = "com.ceaver.assin.system.SystemRepository.SharedPreferences.isInitialized"

object SystemRepository {

    fun setInitialized(initialized: Boolean) {
        AssinApplication.appContext!!.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).edit().putBoolean(IS_INITIALIZED, initialized).apply()
    }

    fun isInitialized(): Boolean {
        return AssinApplication.appContext!!.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).getBoolean(IS_INITIALIZED, false)
    }
}