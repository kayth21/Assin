package com.ceaver.assin.assets

import com.ceaver.assin.action.Action
import com.ceaver.assin.action.Deposit
import com.ceaver.assin.action.Trade
import com.ceaver.assin.action.Withdraw

object AssetFactory {
    public fun fromActions(actions: List<Action>): List<Asset> {
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