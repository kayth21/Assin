package com.ceaver.tradeadvisor.trades

import com.ceaver.tradeadvisor.database.Database
import javax.inject.Inject

object TradeRepository {

    fun loadTrades(): List<Trade> {
        return getTradeDao().loadTrades()
    }

    fun saveTrade(trade: Trade) {
        if (trade.id > 0) updateTrade(trade) else insertTrade(trade)
    }

    fun insertTrade(trade: Trade) {
        getTradeDao().insertTrade(trade)
    }

    fun updateTrade(trade: Trade) {
        getTradeDao().updateTrade(trade)
    }

    fun deleteTrade(trade: Trade) {
        getTradeDao().deleteTrade(trade)
    }

    fun deleteAllTrades() {
        getTradeDao().deleteAllTrades()
    }

    fun loadTrade(id: Long): Trade {
        return getTradeDao().loadTrade(id)
    }

    private fun getTradeDao(): TradeDao {
        return getDatabase().tradeDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}