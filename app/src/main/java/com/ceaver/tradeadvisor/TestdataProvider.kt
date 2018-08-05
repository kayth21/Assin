package com.ceaver.tradeadvisor

import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeEvents
import com.ceaver.tradeadvisor.trades.TradeRepository
import com.ceaver.tradeadvisor.trades.TradeStrategy
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
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2017, 3, 15), 2500.0, 2.0, setOf(TradeStrategy.HODL, TradeStrategy.BAD_TRADE)))
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2017, 7, 3), 5000.0, 10.0, setOf(TradeStrategy.HODL, TradeStrategy.BAD_TRADE)))
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2017, 12, 18), 20000.0, 2.0, setOf(TradeStrategy.ASAP_NO_LOSSES, TradeStrategy.BAD_TRADE)))
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2018, 7, 3), 5000.0, 10.0, setOf(TradeStrategy.DOUBLE_OUT, TradeStrategy.BAD_TRADE)))
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2018, 5, 2), 7500.0, 5.0, setOf(TradeStrategy.ASAP_NO_LOSSES, TradeStrategy.BAD_TRADE)))
    }
}