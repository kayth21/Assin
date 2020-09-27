package com.ceaver.assin.assets

import com.ceaver.assin.action.Action
import com.ceaver.assin.action.Deposit
import com.ceaver.assin.action.Trade
import com.ceaver.assin.action.Withdraw

object AssetFactory {
    fun fromActions(actions: List<Action>): List<Asset> {
        val depositActions = actions.filterIsInstance<Deposit>()
        val tradeActions = actions.filterIsInstance<Trade>()
        val withdrawActions = actions.filterIsInstance<Withdraw>()

        val buyPairsFromDeposits = depositActions.map { Pair(Pair(it.title, it.label), it.quantity) }
        val buyPairsFromTrades = tradeActions.map { Pair(Pair(it.buyTitle, it.buyLabel), it.buyQuantity) }
        val sellPairsFromTrades = tradeActions.map { Pair(Pair(it.sellTitle, it.sellLabel), it.sellQuantity.unaryMinus()) }
        val sellPairsFromWithdraws = withdrawActions.map { Pair(Pair(it.title, it.label), it.quantity.unaryMinus()) }

        val allPairs = buyPairsFromDeposits + buyPairsFromTrades + sellPairsFromTrades + sellPairsFromWithdraws

        return allPairs.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            title = it.first.first,
                            label = it.first.second,
                            quantity = it.second,
                            valueCrypto = it.first.first.cryptoQuotes.price.toBigDecimal().times(it.second),
                            valueFiat = it.first.first.fiatQuotes.price.toBigDecimal().times(it.second))
                }
    }
}