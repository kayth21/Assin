package com.ceaver.assin.markets

import com.ceaver.assin.assets.AssetCategory
import com.ceaver.assin.database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object TitleRepository {

    suspend fun updateAll(allTitles: Set<Title>) = withContext(Dispatchers.IO) {
        getTitleDao().updateTitles(allTitles)
    }

    suspend fun update(title: Title) = withContext(Dispatchers.IO) {
        getTitleDao().updateTitle(title)
    }

    suspend fun insertAll(allTitles: Set<Title>) = withContext(Dispatchers.IO) {
        getTitleDao().insertTitles(allTitles)
    }

    suspend fun insert(title: Title) = withContext(Dispatchers.IO) {
        getTitleDao().insertTitle(title)
    }

    suspend fun loadTitle(id: String): Title? = withContext(Dispatchers.IO) {
        return@withContext getTitleDao().loadTitle(id)
    }

    fun loadTitleReg(id: String): Title? {
        return getTitleDao().loadTitleReg(id)
    }

    suspend fun loadAllTitles(): List<Title> = withContext(Dispatchers.IO) {
        return@withContext getTitleDao().loadAllTitles().filter { it.category == AssetCategory.CRYPTO || it.symbol == "USD" }
    }

    suspend fun loadAllCryptoTitles(): List<Title> = withContext(Dispatchers.IO) {
        return@withContext getTitleDao().loadAllCryptoTitles()
    }

    suspend fun loadActiveTitles(): List<Title> = withContext(Dispatchers.IO) {
        return@withContext getTitleDao().loadActiveTitles()
    }

    suspend fun loadActiveCryptoTitles(): List<Title> = withContext(Dispatchers.IO) {
        return@withContext getTitleDao().loadActiveCryptoTitles()
    }

    suspend fun loadTitleBySymbol(symbol: String): Title = withContext(Dispatchers.IO) {
        return@withContext getTitleDao().loadTitleBySymbol(symbol);
    }

    suspend fun loadAllCryptoSymbols(): List<String> = withContext(Dispatchers.IO) {
        return@withContext getTitleDao().loadCryptoSymbols()
    }

    suspend fun loadAllFiatSymbols(): List<String> = withContext(Dispatchers.IO) {
        return@withContext getTitleDao().loadFiatSymbols().filter { listOf("USD").contains(it) } // TODO allow more FIAT
    }

    suspend fun loadAllSymbols(): List<String> = withContext(Dispatchers.IO) {
        val fiatList = loadAllFiatSymbols()
        val allCryptoList = loadAllCryptoSymbols()
        val unionList = mutableListOf<String>()
        unionList.addAll(fiatList)
        unionList.addAll(allCryptoList)
        return@withContext unionList
    }

    suspend fun lookupPrice(symbol: Title, reference: Title): Optional<Double> = withContext(Dispatchers.IO) {
        if (reference.symbol == "EUR" || reference.symbol == "CHF") {
            TODO("not yet implemented")
        }
        if (symbol.symbol == "USD" || symbol.symbol == "EUR" || symbol.symbol == "CHF") {
            TODO("not yet implemented")
        }
        if (reference.symbol == "USD" || reference.symbol == "BTC") {
            val title = loadTitleBySymbol(symbol.symbol)
//            if (!title.isPresent) return Optional.empty()
            return@withContext if (reference.symbol == "USD") Optional.of(title.priceUsd!!) else Optional.of(title.priceBtc!!)
        }
        // symbol and reference can only be crypto here
        val symbolTitle = loadTitleBySymbol(symbol.symbol)
        val referenceTitle = loadTitleBySymbol(reference.symbol)
//        if (!symbolTitle.isPresent || !referenceTitle.isPresent) return Optional.empty()
        return@withContext Optional.of(symbolTitle.priceBtc!! / referenceTitle.priceBtc!!)
    }

    suspend fun deleteTitle(title: Title) = withContext(Dispatchers.IO) {
        getTitleDao().deleteTitle(title)
    }

    suspend fun deleteTitles(titles: Set<Title>) = withContext(Dispatchers.IO) {
        getTitleDao().deleteTitles(titles)
    }

    private fun getTitleDao(): TitleDao {
        return getDatabase().titleDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }


}