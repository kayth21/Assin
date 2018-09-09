package com.ceaver.assin.markets

import androidx.work.Worker
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.exchanges.Exchange

class MarketWorker: Worker() {


    override fun doWork(): Result {
        val symbolName = inputData.getString(Symbol.toString())
        Exchange.getExchanges(Symbol.valueOf(symbolName!!)).first().update(Symbol.valueOf(symbolName!!))
        return Result.SUCCESS
    }

}