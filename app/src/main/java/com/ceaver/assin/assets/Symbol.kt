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

    fun updateUsdPrice(usdPrice : Double) {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(name + Symbol.USD, usdPrice.toString()).apply()
    }

    fun loadUsdPrice() : Double {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(name + Symbol.USD, "0.0").toDouble()
    }

    fun updateBtcPrice(btcPrice : Double) {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(name + Symbol.BTC, btcPrice.toString()).apply()
    }

    fun loadBtcPrice() : Double {
        val sharedPreferences = MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(name + Symbol.BTC, "0.0").toDouble()
    }

    companion object {
        fun values(titleType: Category): List<Symbol> {
            return Symbol.values().filter { it.titleType == titleType }
        }

    }
}

