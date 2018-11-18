package com.ceaver.assin.markets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.database.Database
import com.ceaver.assin.threading.BackgroundThreadExecutor
import java.util.*

object TitleRepository {

    fun updateAll(allTitles: Set<Title>) {
        getTitleDao().insertTitles(allTitles)
    }

    fun update(title: Title) {
        getTitleDao().insertTitle(title)
    }

    fun loadAllTitles(): List<Title> {
        return getTitleDao().loadAllTitles()
    }

    fun loadAllTitlesAsync(callbackInMainThread: Boolean, callback: (List<Title>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val titles = loadAllTitles()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(titles) }
            else
                callback.invoke(titles)
        }
    }

    fun loadTitleBySymbol(symbol: String): Title {
        return getTitleDao().loadTitleBySymbol(symbol);
    }

    fun loadAllSymbolsAsync(callbackInMainThread: Boolean, callback: (List<String>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val symbols = loadAllSymbols()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(symbols) }
            else
                callback.invoke(symbols)
        }
    }

    fun loadAllCryptoSymbolsAsync(callbackInMainThread: Boolean, callback: (List<String>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val symbols = loadAllCryptoSymbols()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(symbols) }
            else
                callback.invoke(symbols)
        }
    }

    fun loadAllCryptoSymbols(): List<String> {
        return getTitleDao().loadCryptoSymbols()
    }

    fun loadAllSymbols(): List<String> {
        val fiatList = listOf("USD", "EUR", "CHF")
        val allCryptoList = loadAllCryptoSymbols()
        val unionList = mutableListOf<String>()
        unionList.addAll(fiatList)
        unionList.addAll(allCryptoList)
        return unionList
    }

    fun lookupPriceAsync(symbol: String, reference: String, callbackInMainThread: Boolean, callback: (Optional<Double>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val price = lookupPrice(symbol, reference)
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(price) }
            else
                callback.invoke(price)
        }
    }

    fun lookupPrice(symbol: String, reference: String): Optional<Double> {
        if (reference == "EUR" || reference == "CHF") {
            TODO("not yet implemented")
        }
        if (symbol == "USD" || symbol == "EUR" || symbol == "CHF") {
            TODO("not yet implemented")
        }
        if (reference == "USD" || reference == "BTC") {
            val title = loadTitleBySymbol(symbol)
//            if (!title.isPresent) return Optional.empty()
            return if (reference == "USD") Optional.of(title.priceUsd) else Optional.of(title.priceBtc)
        }
        // symbol and reference can only be crypto here
        val symbolTitle = loadTitleBySymbol(symbol)
        val referenceTitle = loadTitleBySymbol(reference)
//        if (!symbolTitle.isPresent || !referenceTitle.isPresent) return Optional.empty()
        return Optional.of(symbolTitle.priceBtc / referenceTitle.priceBtc)
    }

    private fun getTitleDao(): TitleDao {
        return getDatabase().titleDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }


}