package com.ceaver.assin.assets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.trades.TradeRepository

object AssetRepository {

    fun loadAssetOverview(): AssetOverview {
        val allAssets = loadAllAssets()
        return if (allAssets.isEmpty())
            AssetOverview()
        else
            allAssets.map { AssetOverview(it.btcValue, it.usdValue) }.reduce { x, y -> AssetOverview(x.btcValue + y.btcValue, x.usdValue + y.usdValue); }
    }

    fun loadAssetOverviewAsync(callbackInMainThread: Boolean, callback: (AssetOverview) -> Unit) {
        BackgroundThreadExecutor.execute {
            val assetOverview = loadAssetOverview()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(assetOverview) }
            else
                callback.invoke(assetOverview)
        }
    }

    fun loadAllAssets(): List<Asset> {
        val assets = TradeRepository.loadAllTrades()
        val buyPairs = assets.filter { it.buyTitle != null }.map { Pair(it.buyTitle!!, it.buyAmount!!) }
        val sellPairs = assets.filter { it.sellTitle != null }.map { Pair(it.sellTitle!!, it.sellAmount!!.unaryMinus()) }
        val allPairs = buyPairs + sellPairs
        return allPairs.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            it.first.name,
                            it.first.symbol,
                            it.second,
                            it.first.priceBtc!!.toBigDecimal().times(it.second),
                            it.first.priceUsd!!.toBigDecimal().times(it.second))
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