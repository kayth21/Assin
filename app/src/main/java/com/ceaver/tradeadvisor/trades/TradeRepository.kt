package com.ceaver.tradeadvisor.trades

import android.os.Handler
import android.os.Looper
import com.ceaver.tradeadvisor.database.Database
import com.ceaver.tradeadvisor.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus


object TradeRepository {

    fun loadTrade(id: Long, callback: (Trade) -> Unit) {
        BackgroundThreadExecutor.execute { val trade = getTradeDao().loadTrade(id); Handler(Looper.getMainLooper()).post { callback.invoke(trade) } }
    }

    fun loadAllTrades(callback: (List<Trade>) -> Unit) {
        BackgroundThreadExecutor.execute { val trades = getTradeDao().loadAllTrades(); Handler(Looper.getMainLooper()).post { callback.invoke(trades) } }
    }

    fun saveTrade(trade: Trade) {
        if (trade.id > 0) updateTrade(trade) else insertTrade(trade)
    }

    fun insertTrade(trade: Trade) {
        BackgroundThreadExecutor.execute { getTradeDao().insertTrade(trade); EventBus.getDefault().post(TradeEvents.Insert()) }
    }

    fun updateTrade(trade: Trade) {
        BackgroundThreadExecutor.execute { getTradeDao().updateTrade(trade); EventBus.getDefault().post(TradeEvents.Update()) }
    }

    fun deleteTrade(trade: Trade) {
        BackgroundThreadExecutor.execute { getTradeDao().deleteTrade(trade); EventBus.getDefault().post(TradeEvents.Delete()) }
    }

    fun deleteAllTrades() {
        BackgroundThreadExecutor.execute { getTradeDao().deleteAllTrades(); EventBus.getDefault().post(TradeEvents.DeleteAll()) }
    }

    private fun getTradeDao(): TradeDao {
        return getDatabase().tradeDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}