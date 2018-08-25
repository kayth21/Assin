package com.ceaver.assin.markets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.assets.Category
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.exchanges.Exchange
import com.ceaver.assin.threading.BackgroundThreadExecutor

object MarketRepository {

    fun loadAllTitlesAsync(callbackInMainThread: Boolean, callback: (List<Title>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val titles = loadAllTitles()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(titles) }
            else
                callback.invoke(titles)
        }
    }

    private fun loadAllTitles(): List<Title> {
        Exchange.getExchanges(Symbol.BTC).first().update(Symbol.BTC)
        Symbol.values(Category.CRYPTO).filter { it != Symbol.BTC }.stream().forEach{ Exchange.getExchanges(it).first().update(it) }

        return Symbol.values(Category.CRYPTO).map { Title(it) }.toList()
    }


}
