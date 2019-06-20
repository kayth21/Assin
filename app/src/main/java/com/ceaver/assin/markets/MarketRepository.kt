package com.ceaver.assin.markets

import com.ceaver.assin.markets.overview.MarketOverview
import java.util.*

object MarketRepository {
    fun loadMarketOverview(): MarketOverview {
        return Coinpaprika.loadGlobalStats()
    }

    fun loadAllTitles(): Set<Title> {
        val allRemoteTitles = Coinpaprika.loadAllTitles()
        val allLocalTitles = TitleRepository.loadAllTitles()
        return allRemoteTitles.map { remoteTitle ->
            val localTitle = allLocalTitles.find { localTitle -> remoteTitle.id == localTitle.id }
            remoteTitle.copy(active = localTitle?.active ?: -75)
        }.toSet()
    }

    fun loadTitle(id: String): Optional<Title> {
        val remoteTitle = Coinpaprika.load(id)
        val localTitle = TitleRepository.loadTitle(id)
        if (!remoteTitle.isPresent)
            return remoteTitle
        return Optional.of(remoteTitle.get().copy(active = localTitle.active ?: -75)) // TODO! :)
    }
}
