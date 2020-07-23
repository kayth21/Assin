package com.ceaver.assin.positions

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.threading.BackgroundThreadExecutor

object PositionRepository {

    fun loadAllPositions(): List<Position> {
        val positions = mutableListOf<Position>()
        ActionRepository.loadAllActions().forEach { action ->
            when (action.actionType) {
                ActionType.DEPOSIT -> {
                    positions.add(Position(
                            title = action.buyTitle!!,
                            amount = action.buyAmount!!,
                            openPriceBtc = action.buyTitle!!.priceBtc!!.toBigDecimal(),
                            openPriceUsd = action.buyTitle!!.priceUsd!!.toBigDecimal()))
                }
                ActionType.WITHDRAW -> {
                    val position = positions.find { it.id == action.positionId }!!
                    positions.set(positions.indexOf(position), position.copy(closePriceBtc = action.valueInBtc, closePriceUsd = action.valueInUsd))
                }
                ActionType.TRADE -> { // TODO avoid copy/paste code
                    val position = positions.find { it.id == action.positionId }!!
                    positions.set(positions.indexOf(position), position.copy(closePriceBtc = action.valueInBtc, closePriceUsd = action.valueInUsd))
                    positions.add(Position(
                            title = action.buyTitle!!,
                            amount = action.buyAmount!!,
                            openPriceBtc = action.buyTitle!!.priceBtc!!.toBigDecimal(),
                            openPriceUsd = action.buyTitle!!.priceUsd!!.toBigDecimal()))
                }
            }
        }
        return positions
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