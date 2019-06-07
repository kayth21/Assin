package com.ceaver.assin.trades

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.database.Database
import com.ceaver.assin.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus


object TradeRepository {

    fun loadTrade(id: Long): Trade {
        return getTradeDao().loadTrade(id)
    }

    fun loadTradeAsync(id: Long, callbackInMainThread: Boolean, callback: (Trade) -> Unit) {
        BackgroundThreadExecutor.execute {
            val trade = getTradeDao().loadTrade(id)
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(trade) }
            else {
                callback.invoke(trade);
            }
        }
    }

    fun loadAllTrades(): List<Trade> {
        return getTradeDao().loadAllTrades()
    }

    fun loadAllTradesAsync(callbackInMainThread: Boolean, callback: (List<Trade>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val trades = loadAllTrades()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(trades) }
            else
                callback.invoke(trades)
        }
    }

    fun saveTrade(trade: Trade) {
        if (trade.id > 0) updateTrade(trade) else insertTrade(trade)
    }

    fun saveTradeAsync(trade: Trade, callbackInMainThread: Boolean, callback: () -> Unit) {
        if (trade.id > 0) updateTradeAsync(trade, callbackInMainThread, callback) else insertTradeAsync(trade, callbackInMainThread, callback)
    }

    fun insertTrade(trade: Trade) {
        getTradeDao().insertTrade(trade); EventBus.getDefault().post(TradeEvents.Insert())
    }

    fun insertTradeAsync(trade: Trade, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { insertTrade(trade); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    fun insertTrades(alerts: List<Trade>) {
        getTradeDao().insertTrades(alerts); EventBus.getDefault().post(TradeEvents.Insert())
    }

    fun insertTradesAsync(trades: List<Trade>, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { insertTrades(trades); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    fun updateTrade(trade: Trade) {
        getTradeDao().updateTrade(trade); EventBus.getDefault().post(TradeEvents.Update())
    }

    fun updateTradeAsync(trade: Trade, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { updateTrade(trade); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    fun deleteTrade(trade: Trade) {
        getTradeDao().deleteTrade(trade); EventBus.getDefault().post(TradeEvents.Delete())
    }

    fun deleteTradeAsync(trade: Trade) {
        BackgroundThreadExecutor.execute { deleteTrade(trade) }
    }

    fun deleteAllTrades() {
        getTradeDao().deleteAllTrades(); EventBus.getDefault().post(TradeEvents.DeleteAll())
    }

    fun deleteAllTradesAsync() {
        deleteAllTradesAsync() {}
    }

    fun deleteAllTradesAsync(callback: () -> Unit) {
        BackgroundThreadExecutor.execute { deleteAllTrades(); callback.invoke() }
    }

    private fun getTradeDao(): TradeDao {
        return getDatabase().tradeDao()
    }

    private fun getDatabase(): com.ceaver.assin.database.Database {
        return Database.getInstance()
    }
}