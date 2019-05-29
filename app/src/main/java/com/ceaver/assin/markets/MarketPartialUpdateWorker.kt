package com.ceaver.assin.markets

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository

class MarketPartialUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val symbolName = inputData.getString("Symbol")!!
        val index = inputData.getInt("sleep", 0)
        val localTitle = TitleRepository.loadTitleBySymbol(symbolName)
        if (!localTitle.isPresent) {
            LogRepository.insertLog("Warning: Cannot find $symbolName in local database.")
        } else {
            Thread.sleep((index * 110).toLong()) // avoid more than 10 calls per second on coinpaprika AIP
            val result = MarketRepository.loadTitle(localTitle.get().id)
            if (result.isPresent)
                TitleRepository.update(result.get().incrementActiveCounter())
            else {
                TitleRepository.update(localTitle.get().decreaseActiveCounter())
                LogRepository.insertLog("Warning: Unable to update $symbolName")
            }
        }
        return Result.success()
    }

}