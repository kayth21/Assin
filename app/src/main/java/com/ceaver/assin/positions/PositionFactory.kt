package com.ceaver.assin.positions

import com.ceaver.assin.action.*
import com.ceaver.assin.extensions.addZeroDotOneToLastDecimal
import com.ceaver.assin.extensions.addZeroDotTwoToLastDecimal
import java.math.BigDecimal
import java.math.MathContext

object PositionFactory {

    fun fromActions(actions: List<Action>): List<Position> {
        val positions = mutableListOf<Position>()
        var positionId = BigDecimal.ZERO;

        actions.forEach { action -> // TODO
            when (action.getActionType()) {
                ActionType.DEPOSIT -> {
                    action as Deposit
                    positionId = positionId.inc()
                    positions.add(Position(
                            id = positionId,
                            title = action.title,
                            quantity = action.quantity,
                            openDate = action.date,
                            openValueCrypto = action.valueCrypto,
                            openValueFiat = action.valueFiat))
                }
                ActionType.WITHDRAW -> {
                    action as Withdraw
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val modifiedPosition = originalPosition.copy(closeDate = action.date, closeValueCrypto = action.valueCrypto, closeValueFiat = action.valueFiat)
                    positions.set(positions.indexOf(originalPosition), modifiedPosition)
                }
                ActionType.TRADE -> { // TODO avoid copy/paste code
                    action as Trade
                    val position = positions.find { it.id == action.positionId }!!
                    positions.set(positions.indexOf(position), position.copy(closeDate = action.date, closeValueCrypto = action.valueCrypto, closeValueFiat = action.valueFiat))
                    positionId = positionId.inc()
                    positions.add(Position(
                            id = positionId,
                            title = action.buyTitle,
                            quantity = action.buyQuantity,
                            openDate = action.date,
                            openValueCrypto = action.valueCrypto,
                            openValueFiat = action.valueFiat))
                }
                ActionType.SPLIT -> {
                    action as Split
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val splitPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotOneToLastDecimal(),
                            quantity = action.quantity,
                            openValueCrypto = originalPosition.openValueCrypto.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity),
                            openValueFiat = originalPosition.openValueFiat.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.quantity)
                            // TOOD closeValueCorrection needed if closed positions can be splitted
                    )
                    val remainingPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotTwoToLastDecimal(),
                            quantity = action.remaining,
                            openValueCrypto = originalPosition.openValueCrypto.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining),
                            openValueFiat = originalPosition.openValueFiat.divide(originalPosition.quantity, MathContext.DECIMAL32).times(action.remaining)
                            // TOOD closeValueCorrection needed if closed positions can be splitted
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