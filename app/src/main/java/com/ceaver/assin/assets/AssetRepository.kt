package com.ceaver.assin.assets

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.action.*
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.markets.Title

object AssetRepository {

    fun loadAssetOverviewObserved(): LiveData<AssetOverview> {
        val assetLiveData = loadAllAssetsObserved()
        return Transformations.map(assetLiveData) {
            it.map { AssetOverview(it.valueCrypto, it.valueFiat) }
                    .fold(AssetOverview()) { x, y -> AssetOverview(x.valueCrypto + y.valueCrypto, x.valueFiat + y.valueFiat) }
        }
    }

    fun loadAllAssetsObserved(): LiveData<List<Asset>> {
        return Transformations.map(ActionRepository.loadAllObserved()) {
            transformActionsToAssets(it)
        }
    }

    suspend fun loadAllAssets(): List<Asset> {
        val actions = ActionRepository.loadAll()
        return transformActionsToAssets(actions)
    }

    fun loadAssetObserved(title: Title): LiveData<Asset> {
        return Transformations.map(ActionRepository.loadAllOfTitleObserved(title)) {
            transformActionsToAssets(it).single()
        }
    }

    private fun transformActionsToAssets(actions: List<Action>): List<Asset> {
        val depositActions = actions.filterIsInstance<Deposit>()
        val tradeActions = actions.filterIsInstance<Trade>()
        val withdrawActions = actions.filterIsInstance<Withdraw>()

        val buyPairsFromDeposits = depositActions.map { Pair(it.title, it.quantity) }
        val buyPairsFromTrades = tradeActions.map { Pair(it.buyTitle, it.buyQuantity) }
        val sellPairsFromTrades = tradeActions.map { Pair(it.sellTitle, it.sellQuantity.unaryMinus()) }
        val sellPairsFromWithdraws = withdrawActions.map { Pair(it.title, it.quantity.unaryMinus()) }

        val allPairs = buyPairsFromDeposits + buyPairsFromTrades + sellPairsFromTrades + sellPairsFromWithdraws

        return allPairs.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            title = it.first,
                            quantity = it.second,
                            valueCrypto = it.first.cryptoQuotes.price.toBigDecimal().times(it.second),
                            valueFiat = it.first.fiatQuotes.price.toBigDecimal().times(it.second))
                }
    }
}
