package com.ceaver.assin.assets

import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.markets.Title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AssetRepository {

    suspend fun loadAssetOverview(): AssetOverview = withContext(Dispatchers.IO) {
        val allAssets = loadAllAssets()
        return@withContext if (allAssets.isEmpty())
            AssetOverview()
        else
            allAssets.map { AssetOverview(it.btcValue, it.usdValue) }.reduce { x, y -> AssetOverview(x.btcValue + y.btcValue, x.usdValue + y.usdValue); }
    }


    suspend fun loadAllAssets(): List<Asset> = withContext(Dispatchers.IO) {
        val assets = ActionRepository.loadAllActions()
        val buyPairs = assets.filter { it.buyTitle != null }.map { Pair(it.buyTitle!!, it.buyAmount!!) }
        val sellPairs = assets.filter { it.sellTitle != null }.map { Pair(it.sellTitle!!, it.sellAmount!!.unaryMinus()) }
        val allPairs = buyPairs + sellPairs
        return@withContext allPairs.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            title = it.first,
                            amount = it.second,
                            btcValue = it.first.priceBtc!!.toBigDecimal().times(it.second),
                            usdValue = it.first.priceUsd!!.toBigDecimal().times(it.second))
                }
    }

    suspend fun loadAsset(title: Title): Asset = withContext(Dispatchers.IO) {
        val actions = ActionRepository.loadActions(title.symbol)
        val buyActions = actions.filter { it.buyTitle?.symbol == title.symbol }.map { it.buyAmount!! }
        val sellActions = actions.filter { it.sellTitle?.symbol == title.symbol }.map { it.sellAmount!!.unaryMinus() }
        val allActions = buyActions + sellActions
        val amount = allActions.reduce { x, y -> x.add(y) }
        return@withContext Asset(
                title = title,
                amount = amount,
                btcValue = title.priceBtc!!.toBigDecimal().times(amount),
                usdValue = title.priceUsd!!.toBigDecimal().times(amount))
    }
}