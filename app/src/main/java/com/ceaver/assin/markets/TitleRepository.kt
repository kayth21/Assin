package com.ceaver.assin.markets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.database.Database
import com.ceaver.assin.threading.BackgroundThreadExecutor
import java.util.*

object TitleRepository {

    fun updateAll(allTitles: Set<Title>) {
        getTitleDao().updateTitles(allTitles)
    }

    fun update(title: Title) {
        getTitleDao().updateTitle(title)
    }

    fun insertAll(allTitles: Set<Title>) {
        getTitleDao().insertTitles(allTitles)
    }

    fun insert(title: Title) {
        getTitleDao().insertTitle(title)
    }

    fun loadTitle(id: String): Title {
        return getTitleDao().loadTitle(id)
    }

    fun loadAllTitles(): List<Title> {
        return getTitleDao().loadAllTitles().filter { it.category == AssetCategory.CRYPTO || it.symbol == "USD" }
    }

    fun loadAllCryptoTitles(): List<Title> {
        return getTitleDao().loadAllCryptoTitles()
    }

    fun loadActiveTitles(): List<Title> {
        return getTitleDao().loadActiveTitles()
    }

    fun loadActiveCryptoTitles(): List<Title> {
        return getTitleDao().loadActiveCryptoTitles()
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

    fun loadAllCryptoTitlesAsync(callbackInMainThread: Boolean, callback: (List<Title>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val titles = loadAllCryptoTitles()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(titles) }
            else
                callback.invoke(titles)
        }
    }

    fun loadActiveTitlesAsync(callbackInMainThread: Boolean, callback: (List<Title>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val titles = loadActiveTitles()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(titles) }
            else
                callback.invoke(titles)
        }
    }


    fun loadActiveCryptoTitlesAsync(callbackInMainThread: Boolean, callback: (List<Title>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val titles = loadActiveCryptoTitles()
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


    fun loadAllFiatSymbolsAsync(callbackInMainThread: Boolean, callback: (List<String>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val symbols = loadAllFiatSymbols()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(symbols) }
            else
                callback.invoke(symbols)
        }
    }

    fun loadAllCryptoSymbols(): List<String> {
        return getTitleDao().loadCryptoSymbols()
    }

    fun loadAllFiatSymbols(): List<String> {
        return getTitleDao().loadFiatSymbols().filter { listOf("USD").contains(it) } // TODO allow more FIAT
    }

    fun loadAllSymbols(): List<String> {
        val fiatList = loadAllFiatSymbols()
        val allCryptoList = loadAllCryptoSymbols()
        val unionList = mutableListOf<String>()
        unionList.addAll(fiatList)
        unionList.addAll(allCryptoList)
        return unionList
    }

    fun lookupPriceAsync(symbol: Title, reference: Title, callbackInMainThread: Boolean, callback: (Optional<Double>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val price = lookupPrice(symbol, reference)
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(price) }
            else
                callback.invoke(price)
        }
    }

    fun lookupPrice(symbol: Title, reference: Title): Optional<Double> {
        if (reference.symbol == "EUR" || reference.symbol == "CHF") {
            TODO("not yet implemented")
        }
        if (symbol.symbol == "USD" || symbol.symbol == "EUR" || symbol.symbol == "CHF") {
            TODO("not yet implemented")
        }
        if (reference.symbol == "USD" || reference.symbol == "BTC") {
            val title = loadTitleBySymbol(symbol.symbol)
//            if (!title.isPresent) return Optional.empty()
            return if (reference.symbol == "USD") Optional.of(title.priceUsd!!) else Optional.of(title.priceBtc!!)
        }
        // symbol and reference can only be crypto here
        val symbolTitle = loadTitleBySymbol(symbol.symbol)
        val referenceTitle = loadTitleBySymbol(reference.symbol)
//        if (!symbolTitle.isPresent || !referenceTitle.isPresent) return Optional.empty()
        return Optional.of(symbolTitle.priceBtc!! / referenceTitle.priceBtc!!)
    }

    fun deleteTitle(title: Title) {
        getTitleDao().deleteTitle(title)
    }

    fun deleteTitles(titles: Set<Title>) {
        getTitleDao().deleteTitles(titles)
    }

    private fun getTitleDao(): TitleDao {
        return getDatabase().titleDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}