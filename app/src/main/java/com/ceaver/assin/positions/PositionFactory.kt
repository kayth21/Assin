package com.ceaver.assin.positions

import com.ceaver.assin.action.*
import com.ceaver.assin.extensions.addZeroDotOneToLastDecimal
import com.ceaver.assin.extensions.addZeroDotTwoToLastDecimal
import com.ceaver.assin.extensions.replace
import java.math.MathContext

object PositionFactory {

    fun fromActions(actions: List<Action>): List<Position> {
        val positions = mutableListOf<Position>()

        actions.forEach { action ->
            when (action.getActionType()) {
                ActionType.DEPOSIT -> {
                    action as Deposit
                    val positionId = positions.size.toBigDecimal().inc()
                    val newPosition = Position(id = positionId, title = action.title, label = action.label, quantity = action.quantity, openQuotes = Position.Quotes(date = action.date, valueCrypto = action.valueCrypto, valueFiat = action.valueFiat))
                    positions.add(newPosition)
                }
                ActionType.WITHDRAW -> {
                    action as Withdraw
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val modifiedPosition = originalPosition.copy(closedQuotes = Position.Quotes(date = action.date, valueCrypto = action.valueCrypto, valueFiat = action.valueFiat))
                    positions.replace(originalPosition, modifiedPosition)
                }
                ActionType.TRADE -> {
                    action as Trade
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val modifiedPosition = originalPosition.copy(closedQuotes = Position.Quotes(date = action.date, valueCrypto = action.valueCrypto, valueFiat = action.valueFiat))
                    positions.replace(originalPosition, modifiedPosition)
                    val positionId = positions.size.toBigDecimal().inc()
                    val newPosition = Position(id = positionId, title = action.buyTitle, quantity = action.buyQuantity, label = action.buyLabel, openQuotes = Position.Quotes(date = action.date, valueCrypto = action.valueCrypto, valueFiat = action.valueFiat))
                    positions.add(newPosition)
                }
                ActionType.SPLIT -> {
                    action as Split
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val splitPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotOneToLastDecimal(),
                            quantity = action.quantity,
                            openQuotes = originalPosition.openQuotes.copy(
                                    valueCrypto = originalPosition.openQuotes.valueCrypto.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity),
                                    valueFiat = originalPosition.openQuotes.valueFiat.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity)),
                            closedQuotes = if (originalPosition.closedQuotes == null) null else originalPosition.closedQuotes.copy(
                                    valueCrypto = originalPosition.closedQuotes.valueCrypto.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity),
                                    valueFiat = originalPosition.closedQuotes.valueFiat.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity)
                            ))
                    val remainingPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotTwoToLastDecimal(),
                            quantity = action.remaining,
                            openQuotes = originalPosition.openQuotes.copy(
                                    valueCrypto = originalPosition.openQuotes.valueCrypto.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining),
                                    valueFiat = originalPosition.openQuotes.valueFiat.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining)),
                            closedQuotes = if (originalPosition.closedQuotes == null) null else originalPosition.closedQuotes.copy(
                                    valueCrypto = originalPosition.closedQuotes.valueCrypto.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining),
                                    valueFiat = originalPosition.closedQuotes.valueFiat.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining)
                            ))
                    positions.remove(originalPosition)
                    positions.add(splitPosition)
                    positions.add(remainingPosition)
                }
            }
        }
        return positions.toList()
    }
}