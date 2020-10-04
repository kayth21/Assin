package com.ceaver.assin.extensions

import android.content.SharedPreferences
import com.ceaver.assin.markets.CryptoTitle
import com.ceaver.assin.markets.FiatTitle
import com.ceaver.assin.markets.Title
import com.google.gson.Gson

fun SharedPreferences.getString(key: String): String {
    return if (contains(key)) getString(key, "")!! else throw IllegalStateException()
}

fun SharedPreferences.getCryptoTitle(key: String) : Title {
    val json = getString(key)
    return Gson().fromJson(json, CryptoTitle::class.java)
}

fun SharedPreferences.getFiatTitle(key: String) : Title {
    val json = getString(key)
    return Gson().fromJson(json, FiatTitle::class.java)
}

fun SharedPreferences.Editor.setTitle(key: String, title: Title): SharedPreferences.Editor {
    return putString(key, Gson().toJson(title))
}