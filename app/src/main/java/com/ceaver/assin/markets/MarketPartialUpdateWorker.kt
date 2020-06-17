package com.ceaver.assin.markets

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ceaver.assin.logging.LogRepository

class MarketPartialUpdateWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val symbolName = inputData.getString("Symbol")!!
        val index = inputData.getInt("sleep", 0)
        val localTitle = TitleRepository.loadTitleBySymbol(symbolName)
        Thread.sleep((index * 110).toLong()) // avoid more than 10 calls per second on coinpaprika AIP
        val result = MarketRepository.loadTitle(localTitle.id)
        if (result.isPresent) {
            TitleRepository.update(result.get().incrementActiveCounter())
            if (symbolName == "BTC") TitleRepository.update(TitleRepository.loadTitleBySymbol("USD").copy(priceBtc = (1 / result.get().priceUsd!!.toDouble()).toBigDecimal()))
        } else {
            TitleRepository.update(localTitle.decreaseActiveCounter())
            LogRepository.insertLog("Warning: Unable to update $symbolName")
        }
        return Result.success()
    }

}