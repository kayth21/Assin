package com.ceaver.assin.positions

import com.ceaver.assin.action.*
import com.ceaver.assin.extensions.replace

object PositionFactory {

    fun fromActions(actions: List<Action>, includingExpiredPositions: Boolean = false): List<Position> {

        val positions = mutableListOf<Position>()

        actions.forEachIndexed { index, action ->
            when (action.getActionType()) {
                ActionType.DEPOSIT -> {
                    positions.add(Position.fromDeposit(index.inc(), action as Deposit))
                }
                ActionType.WITHDRAW -> {
                    action as Withdraw
                    val sourcePosition = positions.single { it.id == action.sourcePositionId }
                    positions.replace(sourcePosition, sourcePosition.withdraw(action))
                }
                ActionType.TRADE -> {
                    action as Trade
                    val sourcePosition = positions.single { it.id == action.sellPositionId }
                    val tradePositions = sourcePosition.trade(index.inc(), action)
                    positions.replace(sourcePosition, tradePositions.first)
                    positions.add(tradePositions.second)
                }
                ActionType.SPLIT -> {
                    action as Split
                    val sourcePosition = positions.single { it.id == action.sourcePositionId }
                    val splitPositions = sourcePosition.split(index.inc(), action)
                    positions.replace(sourcePosition, splitPositions.first)
                    positions.add(splitPositions.second.first)
                    positions.add(splitPositions.second.second)
                }
                ActionType.MERGE -> {
                    action as Merge
                    val sourcePosition1 = positions.single { it.id == action.sourcePositionIdA }
                    val sourcePosition2 = positions.single { it.id == action.sourcePositionIdB }
                    val mergePositions = sourcePosition1.merge(sourcePosition2, index.inc(), action)
                    positions.replace(sourcePosition1, mergePositions.first.first)
                    positions.replace(sourcePosition2, mergePositions.first.second)
                    positions.add(mergePositions.second)
                }
                ActionType.MOVE -> {
                    action as Move
                    val sourcePosition = positions.single { it.id == action.sourcePositionId }
                    positions.replace(sourcePosition, sourcePosition.move(action))
                }
            }
        }
        return if (includingExpiredPositions) positions else positions.filter { !it.expired }
    }
}