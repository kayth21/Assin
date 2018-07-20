package com.ceaver.tradeadvisor

import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeEvents
import com.ceaver.tradeadvisor.trades.TradeRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

// TODO Temporary Helper - Remove!
object TestdataProvider {

    init {
        EventBus.getDefault().register(this)
    }

    fun cleanDatabaseAndInsertSomeDataAfterwards() {
        TradeRepository.deleteAllTrades()
    }

    @Subscribe
    fun onMessageEvent(event: TradeEvents.DeleteAll) {
        TradeRepository.insertTrade(Trade(0,1, Date(), 5.0, 5.0))
        TradeRepository.insertTrade(Trade(0,1, Date(), 25.0, 1.0))
        TradeRepository.insertTrade(Trade(0,1, Date(), 14.0, 50.0))
        TradeRepository.insertTrade(Trade(0,1, Date(), 115.0, 2.5))
    }
}