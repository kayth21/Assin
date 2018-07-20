package com.ceaver.tradeadvisor.trades

import com.ceaver.tradeadvisor.database.Database
import com.ceaver.tradeadvisor.trades.input.TradeInputActivity
import io.reactivex.Maybe
import org.greenrobot.eventbus.EventBus


object TradeRepository {

    fun loadAllTrades() {
        Thread(Runnable { val trades = getTradeDao().loadTradesFlowable(); EventBus.getDefault().post(TradeEvents.LoadAll(trades)) }).start()
    }

    fun saveTrade(trade: Trade) {
        if (trade.id > 0) updateTrade(trade) else insertTrade(trade)
    }

    fun insertTrade(trade: Trade) {
        Thread(Runnable { getTradeDao().insertTrade(trade); EventBus.getDefault().post(TradeEvents.Insert()) }).start()
    }

    fun updateTrade(trade: Trade) {
        Thread(Runnable { getTradeDao().updateTrade(trade); EventBus.getDefault().post(TradeEvents.Update()) }).start()
    }

    fun deleteTrade(trade: Trade) {
        Thread(Runnable { getTradeDao().deleteTrade(trade); EventBus.getDefault().post(TradeEvents.Delete()) }).start()
    }

    fun deleteAllTrades() {
        Thread(Runnable { getTradeDao().deleteAllTrades(); EventBus.getDefault().post(TradeEvents.DeleteAll()) }).start()
    }

    fun loadTrade(id: Long) {
        Thread(Runnable { val trade =  getTradeDao().loadTrade(id); EventBus.getDefault().post(TradeEvents.Load(trade)) }).start()
    }

    private fun getTradeDao(): TradeDao {
        return getDatabase().tradeDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}