package com.ceaver.assin.markets

import androidx.lifecycle.LiveData
import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.database.Database
import java.util.*

object TitleRepository {

    suspend fun updateAll(allTitles: Set<Title>) {
        getTitleDao().updateTitles(allTitles)
    }

    suspend fun update(title: Title) {
        getTitleDao().updateTitle(title)
    }

    suspend fun insertAll(allTitles: Set<Title>) {
        getTitleDao().insertTitles(allTitles)
    }

    suspend fun insert(title: Title) {
        getTitleDao().insertTitle(title)
    }

    suspend fun loadTitle(id: String): Title? {
        return getTitleDao().loadTitle(id)
    }

    fun loadTitleReg(id: String): Title? {
        return getTitleDao().loadTitleReg(id)
    }

    suspend fun loadAllTitles(): List<Title> {
        return getTitleDao().loadAllTitles().filter { it.category == AssetCategory.CRYPTO || it.symbol == "USD" }
    }

    suspend fun loadAllCryptoTitles(): List<Title> {
        return getTitleDao().loadAllCryptoTitles()
    }

    suspend fun loadActiveTitles(): List<Title> {
        return getTitleDao().loadActiveTitles()
    }

    fun loadActiveCryptoTitles(): LiveData<List<Title>> {
        return getTitleDao().loadActiveCryptoTitles()
    }

    suspend fun loadTitleBySymbol(symbol: String): Title {
        return getTitleDao().loadTitleBySymbol(symbol);
    }

    suspend fun loadAllCryptoSymbols(): List<String> {
        return getTitleDao().loadCryptoSymbols()
    }

    suspend fun loadAllFiatSymbols(): List<String> {
        return getTitleDao().loadFiatSymbols().filter { listOf("USD").contains(it) } // TODO allow more FIAT
    }

    suspend fun loadAllSymbols(): List<String> {
        val fiatList = loadAllFiatSymbols()
        val allCryptoList = loadAllCryptoSymbols()
        val unionList = mutableListOf<String>()
        unionList.addAll(fiatList)
        unionList.addAll(allCryptoList)
        return unionList
    }

    suspend fun lookupPrice(symbol: Title, reference: Title): Optional<Double> {
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

    suspend fun deleteTitle(title: Title) {
        getTitleDao().deleteTitle(title)
    }

    suspend fun deleteTitles(titles: Set<Title>) {
        getTitleDao().deleteTitles(titles)
    }

    suspend fun marketUpdate(titlesToInsert: Set<Title>, titlesToUpdate: Set<Title>, titlesToDelete: Set<Title>) {
        getTitleDao().marketUpdate(titlesToInsert, titlesToUpdate, titlesToDelete)
    }

    private fun getTitleDao(): TitleDao {
        return getDatabase().titleDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}