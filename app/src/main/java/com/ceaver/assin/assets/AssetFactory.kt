package com.ceaver.assin.assets

import com.ceaver.assin.action.Action
import com.ceaver.assin.positions.PositionFactory

object AssetFactory {
    fun fromActions(actions: List<Action>): List<Asset> {
        return PositionFactory.fromActions(actions)
                .filter { it.isOpen() }
                .asSequence()
                .map { Pair(Pair(it.title, it.label), it) }
                .groupBy { it.first }
                .map { Pair(it.key, it.value.map { it.second }) }
                .map {
                    Asset(
                            title = it.first.first,
                            label = it.first.second,
                            quantity = it.second.map { it.quantity }.reduce { x, y -> x + y },
                            current = Asset.Quotes(
                                    valueCrypto = it.second.map { it.current.valueCrypto }.reduce { x, y -> x + y },
                                    valueFiat = it.second.map { it.current.valueFiat }.reduce { x, y -> x + y }),
                            open = Asset.Quotes(
                                    valueCrypto = it.second.map { it.open.valueCrypto }.reduce { x, y -> x + y },
                                    valueFiat = it.second.map { it.open.valueFiat }.reduce { x, y -> x + y })
                    )
                }
                .toList()
    }
}