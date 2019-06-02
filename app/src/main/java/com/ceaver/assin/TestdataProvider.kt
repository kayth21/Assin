package com.ceaver.assin

import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeEvents
import com.ceaver.assin.trades.TradeRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.time.LocalDate
import java.util.*

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
        TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), "Deposit USD", Optional.of("USD"), Optional.of(50000.0), Optional.empty(), Optional.empty()), false) {}
        TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), "Deposit BTC", Optional.of("BTC"), Optional.of(10.0), Optional.empty(), Optional.empty()), false) {}
        TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), "Buy Shitcoin", Optional.of("LTC"), Optional.of(500.0), Optional.of("BTC"), Optional.of(1.0)), false) {}
        TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), "Withdraw", Optional.empty(), Optional.empty(), Optional.of("USD"), Optional.of(10000.0)), false) {}
    }
}