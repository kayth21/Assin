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
                    val newPosition = Position(id = positionId, title = action.title, label = action.label, quantity = action.quantity, openDate = action.date, openValueCrypto = action.valueCrypto, openValueFiat = action.valueFiat)
                    positions.add(newPosition)
                }
                ActionType.WITHDRAW -> {
                    action as Withdraw
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val modifiedPosition = originalPosition.copy(closeDate = action.date, closeValueCrypto = action.valueCrypto, closeValueFiat = action.valueFiat)
                    positions.replace(originalPosition, modifiedPosition)
                }
                ActionType.TRADE -> {
                    action as Trade
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val modifiedPosition = originalPosition.copy(closeDate = action.date, closeValueCrypto = action.valueCrypto, closeValueFiat = action.valueFiat)
                    positions.replace(originalPosition, modifiedPosition)
                    val positionId = positions.size.toBigDecimal().inc()
                    val newPosition = Position(id = positionId, title = action.buyTitle, quantity = action.buyQuantity, label = action.buyLabel, openDate = action.date, openValueCrypto = action.valueCrypto, openValueFiat = action.valueFiat)
                    positions.add(newPosition)
                }
                ActionType.SPLIT -> {
                    action as Split
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val splitPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotOneToLastDecimal(),
                            quantity = action.quantity,
                            openValueCrypto = originalPosition.openValueCrypto.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity),
                            openValueFiat = originalPosition.openValueFiat.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity),
                            closeValueCrypto = if (originalPosition.closeDate == null) null else originalPosition.closeValueCrypto!!.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity),
                            closeValueFiat = if (originalPosition.closeDate == null) null else originalPosition.closeValueFiat!!.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity)
                    )
                    val remainingPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotTwoToLastDecimal(),
                            quantity = action.remaining,
                            openValueCrypto = originalPosition.openValueCrypto.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining),
                            openValueFiat = originalPosition.openValueFiat.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining),
                            closeValueCrypto = if (originalPosition.closeDate == null) null else originalPosition.closeValueCrypto!!.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining),
                            closeValueFiat = if (originalPosition.closeDate == null) null else originalPosition.closeValueFiat!!.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining)
                    )
                    positions.remove(originalPosition)
                    positions.add(splitPosition)
                    positions.add(remainingPosition)
                }
            }
        }
        return positions.toList()
    }
}