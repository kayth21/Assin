package com.ceaver.assin.assets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.trades.TradeRepository

object AssetRepository {

    fun loadAllAssets(): List<Asset> {
        val assets = TradeRepository.loadAllTrades()
        val buyPairs = assets.filter { it.buyTitle.isPresent }.map { Pair(it.buyTitle.get(), it.buyAmount.get()) }
        val sellPairs = assets.filter { it.sellTitle.isPresent }.map { Pair(it.sellTitle.get(), it.sellAmount.get().unaryMinus()) }
        val allPairs = buyPairs + sellPairs
        return allPairs.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            it.first.symbol,
                            it.second,
                            it.first.priceBtc.times(it.second),
                            it.first.priceUsd.times(it.second))
                }
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