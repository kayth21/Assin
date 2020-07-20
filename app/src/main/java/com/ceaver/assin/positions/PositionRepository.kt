package com.ceaver.assin.positions

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.action.Action
import com.ceaver.assin.database.Database
import com.ceaver.assin.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus

object PositionRepository {

    fun loadPosition(id: Long): Position {
        return getPositionDao().loadPosition(id)
    }

    fun loadPositionAsync(id: Long, callbackInMainThread: Boolean, callback: (Position) -> Unit) {
        BackgroundThreadExecutor.execute {
            val position = loadPosition(id)
            handleCallback(callbackInMainThread, callback, position)
        }
    }

    fun loadAllPositions(): List<Position> {
        return getPositionDao().loadAllPositions()
    }

    fun loadAllPositionsAsync(callbackInMainThread: Boolean, callback: (List<Position>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val positions = loadAllPositions()
            handleCallback(callbackInMainThread, callback, positions)
        }
    }

    fun insertPosition(action: Action) {
        getPositionDao().insertPosition(Position(action))
        getEventbus().post(PositionEvents.Insert())
    }

    fun insertPositionAsync(action: Action, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            insertPosition(action)
            handleCallback(callbackInMainThread, callback)
        }
    }

    fun insertPositions(positions: List<Position>) {
        getPositionDao().insertPositions(positions)
        getEventbus().post(PositionEvents.Insert())
    }

    fun insertPositionsAsync(positions: List<Position>, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            insertPositions(positions)
            handleCallback(callbackInMainThread, callback)
        }
    }

    fun updatePosition(position: Position) {
        getPositionDao().updatePosition(position)
        getEventbus().post(PositionEvents.Update())
    }

    fun updatePositionAsync(position: Position, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            updatePosition(position)
            handleCallback(callbackInMainThread, callback)
        }
    }

    fun deletePosition(position: Position) {
        getPositionDao().deletePosition(position)
        getEventbus().post(PositionEvents.Delete())
    }

    fun deletePositionAsync(position: Position) {
        BackgroundThreadExecutor.execute {
            deletePosition(position)
        }
    }

    fun deleteAllPositions() {
        getPositionDao().deleteAllPositions()
        getEventbus().post(PositionEvents.DeleteAll())
    }

    fun deleteAllPositionsAsync(callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            deleteAllPositions()
            handleCallback(callbackInMainThread, callback)
        }
    }

    private fun handleCallback(callbackInMainThread: Boolean, callback: () -> Unit) {
        if (callbackInMainThread)
            Handler(Looper.getMainLooper()).post(callback)
        else
            callback.invoke()
    }

    private fun handleCallback(callbackInMainThread: Boolean, callback: (List<Position>) -> Unit, positions: List<Position>) {
        if (callbackInMainThread)
            Handler(Looper.getMainLooper()).post { callback.invoke(positions) }
        else
            callback.invoke(positions)
    }


    private fun handleCallback(callbackInMainThread: Boolean, callback: (Position) -> Unit, position: Position) {
        if (callbackInMainThread)
            Handler(Looper.getMainLooper()).post { callback.invoke(position) }
        else
            callback.invoke(position)
    }

    private fun getPositionDao(): PositionDao = getDatabase().positionDao()

    private fun getDatabase(): Database = Database.getInstance()

    private fun getEventbus() = EventBus.getDefault()
}