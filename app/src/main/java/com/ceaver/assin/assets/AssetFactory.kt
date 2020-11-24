package com.ceaver.assin.assets

import com.ceaver.assin.action.Action
import com.ceaver.assin.positions.PositionFactory
import java.math.BigDecimal

object AssetFactory {
    fun fromActions(actions: List<Action>): List<Asset> {
        return PositionFactory.fromActions(actions)
                .asSequence()
                .map { Pair(Pair(it.title, it.label), if (it.isOpen()) it.quantity else BigDecimal.ZERO) }.groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }.reduce { x, y -> x + y }) }
                .map {
                    Asset(
                            title = it.first.first,
                            label = it.first.second,
                            quantity = it.second,
                            valueCrypto = it.first.first.cryptoQuotes.price.toBigDecimal().times(it.second),
                            valueFiat = it.first.first.fiatQuotes.price.toBigDecimal().times(it.second))
                }
                .toList()
    }
}