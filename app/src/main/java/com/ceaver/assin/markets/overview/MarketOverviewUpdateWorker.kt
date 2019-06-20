package com.ceaver.assin.markets.overview

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.markets.MarketRepository

class MarketOverviewUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val marketOverview = MarketRepository.loadMarketOverview()
        MarketOverviewRepository.insertMarketOverview(marketOverview)
        return Result.success()
    }
}