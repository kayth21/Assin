package com.ceaver.assin.markets

import android.util.Log
import androidx.work.Worker
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.exchanges.Exchange
import com.ceaver.assin.logging.LogRepository

class MarketWorker : Worker() {

    override fun doWork(): Result {
        val symbolName = inputData.getString(Symbol.toString())
        if (Exchange.getExchanges(Symbol.valueOf(symbolName!!)).isEmpty())
            throw IllegalStateException(symbolName)
        try {
            Exchange.getExchanges(Symbol.valueOf(symbolName!!)).first().update(Symbol.valueOf(symbolName!!))
        } catch (e: Exception) {
            LogRepository.insertLog("Failed to update $symbolName")
            Log.e(MarketWorker::class.java.name, "failed to update $symbolName", e)
        }
        return Result.SUCCESS
    }

}