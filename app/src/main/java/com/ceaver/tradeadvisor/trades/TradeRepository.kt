package com.ceaver.tradeadvisor.trades

import com.ceaver.tradeadvisor.database.Database
import com.ceaver.tradeadvisor.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus


object TradeRepository {

    fun loadAllTrades() {
        BackgroundThreadExecutor.execute { val trades = getTradeDao().loadAllTrades(); EventBus.getDefault().post(TradeEvents.LoadAll(trades)) }
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

    fun loadTrade(id: Long) {
        BackgroundThreadExecutor.execute { val trade =  getTradeDao().loadTrade(id); EventBus.getDefault().post(TradeEvents.Load(trade)) }
    }

    private fun getTradeDao(): TradeDao {
        return getDatabase().tradeDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}