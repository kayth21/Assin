package com.ceaver.assin.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.ceaver.assin.AssinApplication

fun isConnected(): Boolean {
    val connectivityManager = AssinApplication.appContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
    return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}