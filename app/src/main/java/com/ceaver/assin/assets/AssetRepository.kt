package com.ceaver.assin.assets

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.Deposit
import com.ceaver.assin.action.Trade
import com.ceaver.assin.action.Withdraw
import com.ceaver.assin.assets.overview.AssetOverview
import com.ceaver.assin.markets.Title
import java.math.BigDecimal

object AssetRepository {

    fun loadAssetOverviewObserved(): LiveData<AssetOverview> {
        val assetLiveData = loadAllAssetsObserved()
        return Transformations.map(assetLiveData) {
            it.map { AssetOverview(it.valueCrypto, it.valueFiat) }
                    .fold(AssetOverview()) { x, y -> AssetOverview(x.valueCrypto + y.valueCrypto, x.valueFiat + y.valueFiat) }
        }
    }

    fun loadAllAssetsObserved(): LiveData<List<Asset>> {
        return Transformations.map(ActionRepository.loadAllActionsObserved()) {
            val actions = it

            val depositActions = actions.filterIsInstance<Deposit>()
            val tradeActions = actions.filterIsInstance<Trade>()
            val withdrawActions = actions.filterIsInstance<Withdraw>()

            val buyPairsFromDeposits = depositActions.map { Pair(it.title, it.amount) }
            val buyPairsFromTrades = tradeActions.map { Pair(it.buyTitle, it.buyAmount) }
            val sellPairsFromTrades = tradeActions.map { Pair(it.sellTitle, it.sellAmount.unaryMinus()) }
            val sellPairsFromWithdraws = withdrawActions.map { Pair(it.title, it.amount.unaryMinus()) }

            val allPairs = buyPairsFromDeposits + buyPairsFromTrades + sellPairsFromTrades + sellPairsFromWithdraws

            allPairs.groupBy { it.first }
                    .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                    .map {
                        Asset(
                                title = it.first,
                                amount = it.second,
                                valueCrypto = it.first.cryptoQuotes.price.toBigDecimal().times(it.second),
                                valueFiat = it.first.fiatQuotes.price.toBigDecimal().times(it.second))
                    }

        }
    }

    // TODO remove code duplication
    suspend fun loadAllAssets(): List<Asset> {
        val actions = ActionRepository.loadAllActions()

        val depositActions = actions.filterIsInstance<Deposit>()
        val tradeActions = actions.filterIsInstance<Trade>()
        val withdrawActions = actions.filterIsInstance<Withdraw>()

        val buyPairsFromDeposits = depositActions.map { Pair(it.title, it.amount) }
        val buyPairsFromTrades = tradeActions.map { Pair(it.buyTitle, it.buyAmount) }
        val sellPairsFromTrades = tradeActions.map { Pair(it.sellTitle, it.sellAmount.unaryMinus()) }
        val sellPairsFromWithdraws = withdrawActions.map { Pair(it.title, it.amount.unaryMinus()) }

        val allPairs = buyPairsFromDeposits + buyPairsFromTrades + sellPairsFromTrades + sellPairsFromWithdraws

        return allPairs.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            title = it.first,
                            amount = it.second,
                            valueCrypto = it.first.cryptoQuotes.price.toBigDecimal().times(it.second),
                            valueFiat = it.first.fiatQuotes.price.toBigDecimal().times(it.second))
                }

    }

    // TODO remove code duplication
    fun loadAssetObserved(title: Title): LiveData<Asset> {
        return Transformations.map(ActionRepository.loadAllActionsObserved()) {
            val actions = it

            val depositActions = actions.filterIsInstance<Deposit>().filter { it.title == title }
            val tradeActions = actions.filterIsInstance<Trade>().filter { it.sellTitle == title || it.buyTitle == title }
            val withdrawActions = actions.filterIsInstance<Withdraw>().filter { it.title == title }

            val amountsFromDeposits = depositActions.map { it.amount }
            val buyAmountsfromTrades = tradeActions.map { it.buyAmount }
            val sellAmoountsFromTrades = tradeActions.map { it.sellAmount.unaryMinus() }
            val amountsFromWithdraws = withdrawActions.map { it.amount.unaryMinus() }

            val allActions = amountsFromDeposits + buyAmountsfromTrades + sellAmoountsFromTrades + amountsFromWithdraws

            val amount = allActions.fold(BigDecimal.ZERO) { x, y -> x.add(y) }
            Asset(
                    title = title,
                    amount = amount,
                    valueCrypto = title.cryptoQuotes.price.toBigDecimal().times(amount),
                    valueFiat = title.fiatQuotes.price.toBigDecimal().times(amount))
        }
    }
}