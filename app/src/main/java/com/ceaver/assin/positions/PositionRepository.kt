package com.ceaver.assin.positions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.ceaver.assin.action.*
import com.ceaver.assin.extensions.addZeroDotOneToLastDecimal
import com.ceaver.assin.extensions.addZeroDotTwoToLastDecimal
import com.ceaver.assin.markets.Title
import com.ceaver.assin.markets.TitleRepository
import java.math.BigDecimal
import java.math.MathContext

object PositionRepository {

    // TODO remove copy/paste code

    fun loadPositionsObserved(title: Title): LiveData<List<Position>> {
        val allPositionsObserved = loadAllPositionsObserved()
        return MediatorLiveData<List<Position>>()
                .apply {
                    fun update() {
                        val allPositions = allPositionsObserved.value ?: return
                        value = allPositions.filter { it.title == title }
                    }

                    addSource(allPositionsObserved) { update() }
                    update()
                }
    }

    fun loadAllPositionsObserved(): LiveData<List<Position>> {
        val actionsObserved = ActionRepository.loadAllActionsObserved()
        val titlesObserved = TitleRepository.loadActiveCryptoTitles()

        return MediatorLiveData<List<Position>>()
                .apply {
                    fun update() {
                        val actions = actionsObserved.value ?: return
                        val titles = titlesObserved.value ?: return

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
                                            amount = action.amount,
                                            openDate = action.date,
                                            openValueBtc = action.valueBtc,
                                            openValueUsd = action.valueUsd))
                                }
                                ActionType.WITHDRAW -> {
                                    action as Withdraw
                                    val originalPosition = positions.find { it.id == action.positionId }!!
                                    val modifiedPosition = originalPosition.copy(closeDate = action.date, closeValueBtc = action.valueBtc, closeValueUsd = action.valueUsd)
                                    positions.set(positions.indexOf(originalPosition), modifiedPosition)
                                }
                                ActionType.TRADE -> { // TODO avoid copy/paste code
                                    action as Trade
                                    val position = positions.find { it.id == action.positionId }!!
                                    positions.set(positions.indexOf(position), position.copy(closeDate = action.date, closeValueBtc = action.valueBtc, closeValueUsd = action.valueUsd))
                                    positionId = positionId.inc()
                                    positions.add(Position(
                                            id = positionId,
                                            title = action.buyTitle,
                                            amount = action.buyAmount,
                                            openDate = action.date,
                                            openValueBtc = action.valueBtc,
                                            openValueUsd = action.valueUsd))
                                }
                                ActionType.SPLIT -> {
                                    action as Split
                                    val originalPosition = positions.find { it.id == action.positionId }!!
                                    val splitPosition = originalPosition.copy(
                                            id = originalPosition.id.addZeroDotOneToLastDecimal(),
                                            amount = action.amount,
                                            openValueBtc = originalPosition.openValueBtc.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.amount),
                                            openValueUsd = originalPosition.openValueUsd.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.amount)
                                            // TOOD closeValueCorrection needed if closed positions can be splitted
                                    )
                                    val remainingPosition = originalPosition.copy(
                                            id = originalPosition.id.addZeroDotTwoToLastDecimal(),
                                            amount = action.remaining,
                                            openValueBtc = originalPosition.openValueBtc.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.remaining),
                                            openValueUsd = originalPosition.openValueUsd.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.remaining)
                                            // TOOD closeValueCorrection needed if closed positions can be splitted
                                    )
                                    positions.remove(originalPosition)
                                    positions.add(splitPosition)
                                    positions.add(remainingPosition)
                                }
                            }
                        }

                        value = positions
                    }

                    addSource(actionsObserved) { update() }
                    addSource(titlesObserved) { update() }

                    update()
                }
    }

    suspend fun loadPositions(title: Title): List<Position> {
        return loadAllPositions().filter { it.title == title }
    }

    suspend fun loadAllPositions(): List<Position> {
        val positions = mutableListOf<Position>()
        var positionId = BigDecimal.ZERO;
        ActionRepository.loadAllActions().forEach { action -> // TODO
            when (action.getActionType()) {
                ActionType.DEPOSIT -> {
                    action as Deposit
                    positionId = positionId.inc()
                    positions.add(Position(
                            id = positionId,
                            title = action.title,
                            amount = action.amount,
                            openDate = action.date,
                            openValueBtc = action.valueBtc,
                            openValueUsd = action.valueUsd))
                }
                ActionType.WITHDRAW -> {
                    action as Withdraw
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val modifiedPosition = originalPosition.copy(closeDate = action.date, closeValueBtc = action.valueBtc, closeValueUsd = action.valueUsd)
                    positions.set(positions.indexOf(originalPosition), modifiedPosition)
                }
                ActionType.TRADE -> { // TODO avoid copy/paste code
                    action as Trade
                    val position = positions.find { it.id == action.positionId }!!
                    positions.set(positions.indexOf(position), position.copy(closeDate = action.date, closeValueBtc = action.valueBtc, closeValueUsd = action.valueUsd))
                    positionId = positionId.inc()
                    positions.add(Position(
                            id = positionId,
                            title = action.buyTitle,
                            amount = action.buyAmount,
                            openDate = action.date,
                            openValueBtc = action.valueBtc,
                            openValueUsd = action.valueUsd))
                }
                ActionType.SPLIT -> {
                    action as Split
                    val originalPosition = positions.find { it.id == action.positionId }!!
                    val splitPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotOneToLastDecimal(),
                            amount = action.amount,
                            openValueBtc = originalPosition.openValueBtc.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.amount),
                            openValueUsd = originalPosition.openValueUsd.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.amount)
                            // TOOD closeValueCorrection needed if closed positions can be splitted
                    )
                    val remainingPosition = originalPosition.copy(
                            id = originalPosition.id.addZeroDotTwoToLastDecimal(),
                            amount = action.remaining,
                            openValueBtc = originalPosition.openValueBtc.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.remaining),
                            openValueUsd = originalPosition.openValueUsd.divide(originalPosition.amount, MathContext.DECIMAL32).times(action.remaining)
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
}