package com.ceaver.assin.markets

import android.content.Context
import android.content.SharedPreferences
import com.ceaver.assin.MyApplication
import com.ceaver.assin.assets.Symbol
import com.google.gson.Gson
import java.util.*

object MarketValuation {

    fun update(title: Title) {
        getSharedPreferences(javaClass.canonicalName).edit().putString(title.symbol.label + title.unit.label, Gson().toJson(title)).apply()
    }

    fun load(symbol: Symbol, unit: Symbol): Optional<Title> {
        val jsonTitle = getSharedPreferences(javaClass.canonicalName).getString(symbol.label + unit.label, null)
        return if (jsonTitle == null) Optional.empty() else Optional.of(Gson().fromJson(jsonTitle, Title::class.java))
    }

    private fun getSharedPreferences(identifier: String): SharedPreferences {
        return MyApplication.appContext!!.getSharedPreferences(identifier, Context.MODE_PRIVATE)
    }

}