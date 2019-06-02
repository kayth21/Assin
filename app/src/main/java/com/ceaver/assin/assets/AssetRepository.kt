package com.ceaver.assin.assets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.trades.TradeRepository

object AssetRepository {

    fun loadAllAssets(): List<Asset> {
        val assets = TradeRepository.loadAllTrades()
        val buyPairs = assets.filter { it.buySymbol.isPresent }.map { Pair(it.buySymbol.get(), it.buyAmount.get()) }
        val sellPairs = assets.filter { it.sellSymbol.isPresent }.map { Pair(it.sellSymbol.get(), it.sellAmount.get().unaryMinus()) }
        val allPairs = buyPairs + sellPairs

        return allPairs.groupBy { it.first }.map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }.map { Asset(it.first, it.second) }
    }

    fun loadAllAssetsAsync(callbackInMainThread: Boolean, callback: (List<Asset>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val assets = loadAllAssets()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(assets) }
            else
                callback.invoke(assets)
        }
    }

}