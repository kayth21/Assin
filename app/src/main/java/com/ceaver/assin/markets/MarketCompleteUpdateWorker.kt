package com.ceaver.assin.markets

import androidx.work.Worker

class MarketCompleteUpdateWorker : Worker() {

    override fun doWork(): Result {
        val allTitles = MarketRepository.loadAllTitles()
        TitleRepository.updateAll(allTitles)
        return Result.SUCCESS
    }

}