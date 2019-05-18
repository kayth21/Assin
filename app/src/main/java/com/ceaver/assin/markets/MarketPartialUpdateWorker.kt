package com.ceaver.assin.markets

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository

class MarketPartialUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val symbolName = inputData.getString("Symbol")!!
        val title = TitleRepository.loadTitleBySymbol(symbolName)
        val result = MarketRepository.loadTitle(title.id)
        if (result.isPresent)
            TitleRepository.update(result.get())
        else {
            LogRepository.insertLog("Failure: Unable to update $symbolName")
        }
        return Result.success()
    }

}