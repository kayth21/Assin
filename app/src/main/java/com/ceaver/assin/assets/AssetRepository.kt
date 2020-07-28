package com.ceaver.assin.assets

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.markets.Title
import com.ceaver.assin.threading.BackgroundThreadExecutor

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
        val assets = ActionRepository.loadAllActions()
        val buyPairs = assets.filter { it.buyTitle != null }.map { Pair(it.buyTitle!!, it.buyAmount!!) }
        val sellPairs = assets.filter { it.sellTitle != null }.map { Pair(it.sellTitle!!, it.sellAmount!!.unaryMinus()) }
        val allPairs = buyPairs + sellPairs
        return allPairs.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            title = it.first,
                            amount = it.second,
                            btcValue = it.first.priceBtc!!.toBigDecimal().times(it.second),
                            usdValue = it.first.priceUsd!!.toBigDecimal().times(it.second))
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

    fun loadAsset(title: Title): Asset {
        val actions = ActionRepository.loadActions(title.symbol)
        val buyActions = actions.filter { it.buyTitle?.symbol == title.symbol }.map { it.buyAmount!! }
        val sellActions = actions.filter { it.sellTitle?.symbol == title.symbol }.map { it.sellAmount!!.unaryMinus() }
        val allActions = buyActions + sellActions
        val amount = allActions.reduce { x, y -> x.add(y) }
        return Asset(
                title = title,
                amount = amount,
                btcValue = title.priceBtc!!.toBigDecimal().times(amount),
                usdValue = title.priceUsd!!.toBigDecimal().times(amount))
    }

    fun loadAssetAsync(title: Title, callbackInMainThread: Boolean, callback: (Asset) -> Unit) {
        BackgroundThreadExecutor.execute {
            val asset = loadAsset(title)
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(asset) }
            else
                callback.invoke(asset)
        }
    }
}