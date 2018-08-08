package com.ceaver.assin.trades

import android.os.Handler
import android.os.Looper
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

    fun saveTradeAsync(trade: Trade) {
        if (trade.id > 0) updateTradeAsync(trade) else insertTradeAsync(trade)
    }

    fun insertTrade(trade: Trade) {
        getTradeDao().insertTrade(trade); EventBus.getDefault().post(TradeEvents.Insert())
    }

    fun insertTradeAsync(trade: Trade) {
        BackgroundThreadExecutor.execute { insertTrade(trade) }
    }

    fun updateTrade(trade: Trade) {
        getTradeDao().updateTrade(trade); EventBus.getDefault().post(TradeEvents.Update())
    }

    fun updateTradeAsync(trade: Trade) {
        BackgroundThreadExecutor.execute { updateTrade(trade) }
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
        BackgroundThreadExecutor.execute { deleteAllTrades() }
    }

    private fun getTradeDao(): TradeDao {
        return getDatabase().tradeDao()
    }

    private fun getDatabase(): com.ceaver.assin.database.Database {
        return com.ceaver.assin.database.Database.getInstance()
    }
}