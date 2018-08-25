package com.ceaver.assin.assets

import android.content.Context
import com.ceaver.assin.MyApplication
import com.ceaver.assin.assets.Category.*

enum class Symbol(val titleType: Category, val label: String) {
    USD(FIAT, "US Dollar"), EUR(FIAT, "Euro"), CHF(FIAT, "Schweizer Franken"),

    BTC(CRYPTO, "Bitcoin"),
    ETH(CRYPTO, "Ether"),
    XRP(CRYPTO, "XRP"),
    LTC(CRYPTO, "Litecoin"),
    BCH(CRYPTO, "Bitcoin Cash");

    fun updateLastUsd(usdPrice : Double) {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("last" + name + Symbol.USD, usdPrice.toString()).apply()
    }

    fun loadLastUsd() : Double {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        return sharedPreferences.getString("last" + name + Symbol.USD, "0.0").toDouble()
    }

    fun updateLastBtc(btcPrice : Double) {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("last" + name + Symbol.BTC, btcPrice.toString()).apply()
    }

    fun loadLastBtc() : Double {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        return sharedPreferences.getString("last" + name + Symbol.BTC, "0.0").toDouble()
    }

    fun updateOpenUsd(usdPrice : Double) {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("open" + name + Symbol.USD, usdPrice.toString()).apply()
    }

    fun loadOpenUsd() : Double {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        return sharedPreferences.getString("open" + name + Symbol.USD, "0.0").toDouble()
    }

    fun updateOpenBtc(btcPrice : Double) {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("open" + name + Symbol.BTC, btcPrice.toString()).apply()
    }

    fun loadOpenBtc() : Double {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        return sharedPreferences.getString("open" + name + Symbol.BTC, "0.0").toDouble()
    }

    companion object {
        fun values(titleType: Category): List<Symbol> {
            return Symbol.values().filter { it.titleType == titleType }
        }

    }
}

