package com.ceaver.assin.markets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.assets.Category
import com.ceaver.assin.assets.Symbol
import com.ceaver.assin.exchanges.Exchange
import com.ceaver.assin.threading.BackgroundThreadExecutor

object MarketRepository {

    fun loadAllTitles(): List<Title> {
        return Symbol.values(Category.CRYPTO).map { Title(it) }.toList()
    }
}
