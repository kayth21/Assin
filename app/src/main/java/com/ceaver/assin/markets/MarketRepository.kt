package com.ceaver.assin.markets

import java.util.*

object MarketRepository {
    fun loadAllTitles(): Set<Title> {
        return Coinpaprika.loadAllTitles()
    }

    fun loadTitle(id: String) : Optional<Title> {
        return Coinpaprika.load(id)
    }
}
