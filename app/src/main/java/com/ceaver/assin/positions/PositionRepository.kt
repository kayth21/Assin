package com.ceaver.assin.positions

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.extensions.addZeroDotOneToLastDecimal
import com.ceaver.assin.extensions.addZeroDotTwoToLastDecimal
import com.ceaver.assin.markets.Title
import com.ceaver.assin.threading.BackgroundThreadExecutor
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate

object PositionRepository {

    fun loadPositions(title: Title): List<Position> {
        return loadAllPositions().filter { it.title == title }
    }

    fun loadPositionsAsync(title: Title, callbackInMainThread: Boolean, callback: (List<Position>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val positions = loadPositions(title)
            handleCallback(callbackInMainThread, callback, positions)
        }
    }

    fun loadAllPositions(): List<Position> {
        val positions = mutableListOf<Position>()
        var positionId = BigDecimal.ZERO;
        ActionRepository.loadAllActions().forEach { action ->
            when (action.actionType) {
                ActionType.DEPOSIT -> {
                    positionId = positionId.inc()
                    positions.add(Position(
                            id = positionId,
                            title = action.buyTitle!!,
                            amount = action.buyAmount!!,
                            openDate = action.actionDate,
                            openValueBtc = action.valueBtc!!,
                            openValueUsd = action.valueUsd!!))
                }
                ActionType.WITHDRAW -> {
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val modifiedPosition = originalPosition.copy(closeDate = action.actionDate, closeValueBtc = action.valueBtc, closeValueUsd = action.valueUsd)
                    positions.set(positions.indexOf(originalPosition), modifiedPosition)
                }
                ActionType.TRADE -> { // TODO avoid copy/paste code
                    val position = positions.find { it.id == action.positionId }!!
                    positions.set(positions.indexOf(position), position.copy(closeDate = action.actionDate, closeValueBtc = action.valueBtc, closeValueUsd = action.valueUsd))
                    positionId = positionId.inc()
                    positions.add(Position(
                            id = positionId,
                            title = action.buyTitle!!,
                            amount = action.buyAmount!!,
                            openDate = LocalDate.now(),
                            openValueBtc = action.valueBtc!!,
                            openValueUsd = action.valueUsd!!))
                }
                ActionType.SPLIT -> {
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val splitPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotOneToLastDecimal(),
                            amount = action.splitAmount!!,
                            openValueBtc = originalPosition.openValueBtc.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.splitAmount),
                            openValueUsd = originalPosition.openValueUsd.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.splitAmount)
                            // TOOD closeValueCorrection needed if closed positions can be splitted
                    )
                    val remainingPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotTwoToLastDecimal(),
                            amount = action.splitRemaining!!,
                            openValueBtc = originalPosition.openValueBtc.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.splitRemaining),
                            openValueUsd = originalPosition.openValueUsd.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.splitRemaining)
                            // TOOD closeValueCorrection needed if closed positions can be splitted
                    )
                    positions.remove(originalPosition)
                    positions.add(splitPosition)
                    positions.add(remainingPosition)
                }
            }
        }
        positions.sortedBy { it.id }.forEach { println(it.id.toPlainString() + " " + it.isActive()) }
        return positions.sortedBy { it.id }
    }

    fun loadAllPositionsAsync(callbackInMainThread: Boolean, callback: (List<Position>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val positions = loadAllPositions()
            handleCallback(callbackInMainThread, callback, positions)
        }
    }

    private fun handleCallback(callbackInMainThread: Boolean, callback: (List<Position>) -> Unit, positions: List<Position>) {
        if (callbackInMainThread)
            Handler(Looper.getMainLooper()).post { callback.invoke(positions) }
        else
            callback.invoke(positions)
    }
}