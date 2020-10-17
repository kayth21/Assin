package com.ceaver.assin.positions

import com.ceaver.assin.action.*
import com.ceaver.assin.extensions.replace
import java.math.BigDecimal
import java.math.MathContext

object PositionFactory {

    fun fromActions(actions: List<Action>): List<Position> {

        val positions = mutableListOf<Position>()

        fun addPosition(position: Position) {
            positions.add(position)
        }

        fun replacePosition(sourcePosition: Position, position: Position) {
            positions.replace(sourcePosition, position)
        }

        fun removePosition(position: Position) {
            positions.remove(position)
        }

        fun findPosition(positionId: Long): Position {
            return positions.find { it.id == positionId }!!
        }

        fun splitQuote(quote: Position.Quotes, split: BigDecimal, remaining: BigDecimal): Pair<Position.Quotes, Position.Quotes> {
            return Pair(
                    quote.copy(
                            valueCrypto = quote.valueCrypto.divide(split + remaining, MathContext.DECIMAL32).times(split),
                            valueFiat = quote.valueFiat.divide(split + remaining, MathContext.DECIMAL32).times(split)),
                    quote.copy(
                            valueCrypto = quote.valueCrypto.divide(split + remaining, MathContext.DECIMAL32).times(remaining),
                            valueFiat = quote.valueFiat.divide(split + remaining, MathContext.DECIMAL32).times(remaining)))
        }

        actions.forEach { action ->
            when (action.getActionType()) {
                ActionType.DEPOSIT -> {
                    action as Deposit
                    val openQuotes = Position.Quotes(date = action.date, valueCrypto = action.valueCrypto, valueFiat = action.valueFiat)
                    val depositPosition = Position(id = action.id, quantity = action.quantity, title = action.title, label = action.label, open = openQuotes)
                    addPosition(depositPosition)
                }
                ActionType.WITHDRAW -> {
                    action as Withdraw
                    val sourcePosition = findPosition(action.positionId!!)
                    val closeQuotes = Position.Quotes(date = action.date, valueCrypto = action.valueCrypto, valueFiat = action.valueFiat)
                    replacePosition(sourcePosition, sourcePosition.copy(close = closeQuotes))
                }
                ActionType.TRADE -> {
                    action as Trade
                    val sourcePosition = findPosition(action.positionId!!)
                    val quotes = Position.Quotes(date = action.date, valueCrypto = action.valueCrypto, valueFiat = action.valueFiat)
                    replacePosition(sourcePosition, sourcePosition.copy(close = quotes))
                    addPosition(Position(id = action.id, quantity = action.buyQuantity, title = action.buyTitle, label = action.buyLabel, open = quotes))
                }
                ActionType.SPLIT -> {
                    action as Split
                    val sourcePosition = findPosition(action.positionId)

                    removePosition(sourcePosition)
                    val splittedOpenQuotes = splitQuote(sourcePosition.open, action.quantity, sourcePosition.quantity - action.quantity)
                    val splittedCloseQuotes = sourcePosition.close?.let { splitQuote(it, action.quantity, sourcePosition.quantity - action.quantity) }
                    addPosition(Position(id = action.id, quantity = action.quantity, title = sourcePosition.title, label = sourcePosition.label, open = splittedOpenQuotes.first, close = splittedCloseQuotes?.first))
                    addPosition(Position(id = action.id.inv(), quantity = sourcePosition.quantity - action.quantity, title = sourcePosition.title, label = sourcePosition.label, open = splittedOpenQuotes.second, close = splittedCloseQuotes?.second))
                }
                ActionType.MERGE -> {
                    action as Merge
                    val sourcePosition1 = findPosition(action.sourcePositionA)
                    val sourcePosition2 = findPosition(action.sourcePositionB)

                    val closeQuotes1 = Position.Quotes(date = action.date,
                            valueCrypto = action.valueCrypto.divide(sourcePosition1.quantity + sourcePosition2.quantity, MathContext.DECIMAL32).times(sourcePosition1.quantity),
                            valueFiat = action.valueFiat.divide(sourcePosition1.quantity + sourcePosition2.quantity, MathContext.DECIMAL32).times(sourcePosition1.quantity))
                    val closeQuotes2 = Position.Quotes(date = action.date,
                            valueCrypto = action.valueCrypto.divide(sourcePosition1.quantity + sourcePosition2.quantity, MathContext.DECIMAL32).times(sourcePosition2.quantity),
                            valueFiat = action.valueFiat.divide(sourcePosition1.quantity + sourcePosition2.quantity, MathContext.DECIMAL32).times(sourcePosition2.quantity))

                    addPosition(Position(id = action.id, title = action.title, label = action.label, quantity = sourcePosition1.quantity + sourcePosition2.quantity, open = Position.Quotes(action.date, action.valueCrypto, action.valueFiat)))

                    replacePosition(sourcePosition1, sourcePosition1.copy(close = closeQuotes1))
                    replacePosition(sourcePosition2, sourcePosition2.copy(close = closeQuotes2))
                }
                ActionType.MOVE -> {
                    action as Move
                    val sourcePosition = findPosition(action.positionId)
                    replacePosition(sourcePosition, sourcePosition.copy(label = action.targetLabel))
                }
            }

        }
        return positions
    }
}