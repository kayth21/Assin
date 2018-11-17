package com.ceaver.assin.markets

import androidx.work.Worker
import com.ceaver.assin.logging.LogRepository

class MarketPartialUpdateWorker : Worker() {

    override fun doWork(): Result {
        val symbolName = inputData.getString("Symbol")!!
        val id = MarketRepository.loadAllTitles().single { it.symbol == symbolName }.id // TODO too much work to get the id?
        val title = Coinpaprika.load(id)
        if (title.isPresent)
            MarketRepository.update(title.get())
        else {
            LogRepository.insertLog("Failure: Unable to update $symbolName")
        }
        return Result.SUCCESS
    }

}