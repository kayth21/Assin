package com.ceaver.assin.markets

import android.content.Context
import android.content.SharedPreferences
import com.ceaver.assin.MyApplication
import com.ceaver.assin.assets.Category
import com.ceaver.assin.assets.Symbol
import com.google.gson.Gson
import java.util.*

object MarketRepository {

    fun loadAllTitles(): List<Title> {
        return Symbol.values(Category.CRYPTO).map { load(it, if (it == Symbol.BTC) Symbol.USD else Symbol.BTC) }.filter { it.isPresent }.map { it.get() }.toList()
    }

    fun load(symbol: Symbol, reference: Symbol): Optional<Title> {
        val path = MarketPathFinder.findPath(symbol, reference)
        if (path.any { !getSharedPreferences().contains(it.first.label + it.second.label) }) return Optional.empty()
        return path.stream().map { lookup(it.first, it.second) }.map { it.get() }.reduce { left, right -> Title(symbol, left.last * right.last, left.open * right.open, reference) }
    }

    private fun lookup(symbol: Symbol, reference: Symbol): Optional<Title> {
        val jsonTitle = getSharedPreferences().getString(symbol.label + reference.label, null)
        return if (jsonTitle == null) Optional.empty() else Optional.of(Gson().fromJson(jsonTitle, Title::class.java))
    }

    fun update(title: Title) {
        getSharedPreferences().edit().putString(title.symbol.label + title.reference.label, Gson().toJson(title)).apply()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
    }
}
