package com.ceaver.assin.markets

import androidx.work.Worker
import com.ceaver.assin.logging.LogRepository

class MarketPartialUpdateWorker : Worker() {

    override fun doWork(): Result {
        val symbolName = inputData.getString("Symbol")!!
        val title = TitleRepository.loadTitleBySymbol(symbolName)
        val result = MarketRepository.loadTitle(title.id)
        if (result.isPresent)
            TitleRepository.update(result.get())
        else {
            LogRepository.insertLog("Failure: Unable to update $symbolName")
        }
        return Result.SUCCESS
    }

}