package com.ceaver.assin

import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeEvents
import com.ceaver.assin.trades.TradeRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.time.LocalDate

// TODO Temporary Helper - Remove!
object TestdataProvider {

    init {
        EventBus.getDefault().register(this)
    }

    fun cleanDatabaseAndInsertSomeDataAfterwards() {
        TradeRepository.deleteAllTradesAsync()
    }

    @Subscribe
    fun onMessageEvent(event: TradeEvents.DeleteAll) {
        TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), "Initial Trade", "USD", 100000.0, "", 0.0), false) {}
        TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), "abc", "LTC", 500.0, "BTC", 1.0), false) {}
    }
}