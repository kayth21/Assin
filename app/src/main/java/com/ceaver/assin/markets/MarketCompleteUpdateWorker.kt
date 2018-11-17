package com.ceaver.assin.markets

import androidx.work.Worker

class MarketCompleteUpdateWorker : Worker() {

    override fun doWork(): Result {
        val allTitles = Coinpaprika.loadAllTitles() // TODO is it MarketRepository's job?
        MarketRepository.updateAll(allTitles)
        return Result.SUCCESS
    }

}