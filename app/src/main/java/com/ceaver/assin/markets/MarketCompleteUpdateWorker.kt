package com.ceaver.assin.markets

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MarketCompleteUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val allTitles = MarketRepository.loadAllTitles()
        TitleRepository.updateAll(allTitles)
        return Result.success()
    }

}