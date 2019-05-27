package com.ceaver.assin.util

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.ceaver.assin.MyApplication

fun isCharging(): Boolean {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter -> MyApplication.appContext!!.registerReceiver(null, ifilter) }
    val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
}