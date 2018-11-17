package com.ceaver.assin.markets

import android.content.Context
import android.content.SharedPreferences
import com.ceaver.assin.MyApplication
import com.google.gson.Gson
import java.util.*

object MarketRepository {

    fun loadAllTitles(): List<Title> {
        val titles = getSharedPreferences().getStringSet("TODO", emptySet())
        return titles.map { lookup(it) }.filter { it.isPresent }.map { it.get() }.toList()
    }

    fun loadAllCryptoSymbols(): List<String> {
        return loadAllTitles().sortedBy { it.rank }.map { it.symbol }
    }

    fun loadAllSymbols(): Set<String> {
        val fiatList = listOf("USD", "EUR", "CHF")
        return fiatList.union(loadAllCryptoSymbols())
    }

    fun lookupPrice(symbol: String, reference: String): Optional<Double> {
        if (reference == "EUR" || reference == "CHF") {
            TODO("not yet implemented")
        }
        if (symbol == "USD" || symbol == "EUR" || symbol == "CHF") {
            TODO("not yet implemented")
        }
        if (reference == "USD" || reference == "BTC") {
            val title = lookup(symbol)
            if (!title.isPresent) return Optional.empty()
            return if (reference == "USD") Optional.of(title.get().priceUsd) else Optional.of(title.get().priceBtc)
        }
        // symbol and reference can only be crypto here
        val symbolTitle = lookup(symbol)
        val referenceTitle = lookup(reference)
        if (!symbolTitle.isPresent || !referenceTitle.isPresent) return Optional.empty()
        return Optional.of(symbolTitle.get().priceBtc / referenceTitle.get().priceBtc)
    }


    private fun lookup(symbol: String): Optional<Title> {
        val jsonTitle = getSharedPreferences().getString(symbol, null)
        return if (jsonTitle == null) Optional.empty() else Optional.of(Gson().fromJson(jsonTitle, Title::class.java))
    }

    fun updateAll(allTitles: Set<Title>) {
        updateSymbols(allTitles)
        allTitles.forEach { update(it) }
    }

    fun updateSymbols(titles: Set<Title>) {
        val titles = titles.map { it.symbol }.toMutableSet()
        titles.addAll(getSharedPreferences().getStringSet("TODO", setOf()))
        getSharedPreferences().edit().putStringSet("TODO", titles).apply()
    }

    fun update(title: Title) {
        getSharedPreferences().edit().putString(title.symbol, Gson().toJson(title)).apply()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return MyApplication.appContext!!.getSharedPreferences(javaClass.canonicalName, Context.MODE_PRIVATE)
    }

}
