package com.ceaver.assin.markets

import com.ceaver.assin.markets.overview.MarketOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object MarketRepository {
    suspend fun loadMarketOverview(): MarketOverview = withContext(Dispatchers.IO) {
        return@withContext Coinpaprika.loadGlobalStats()
    }

    suspend fun loadAllTitles(): Set<Title> = withContext(Dispatchers.IO) {
        val allRemoteTitles = Coinpaprika.loadAllTitles()
        val allLocalTitles = TitleRepository.loadAllTitles()
        return@withContext allRemoteTitles.map { remoteTitle ->
            val localTitle = allLocalTitles.find { localTitle -> remoteTitle.id == localTitle.id }
            remoteTitle.copy(active = localTitle?.active ?: -75)
        }.toSet()
    }

    suspend fun loadTitle(id: String): Optional<Title> = withContext(Dispatchers.IO) {
        val remoteTitle = Coinpaprika.load(id)
        val localTitle = TitleRepository.loadTitle(id)
        return@withContext if (remoteTitle.isPresent)
            Optional.of(remoteTitle.get().copy(active = localTitle?.active ?: -75))
        else
            remoteTitle
    }
}
