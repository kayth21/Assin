package com.ceaver.assin

import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeEvents
import com.ceaver.assin.trades.TradeRepository
import com.ceaver.assin.trades.TradeStrategy
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
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2017, 3, 15), "comment", 2500.0, 2.0, hashSetOf(TradeStrategy.HODL, TradeStrategy.BAD_TRADE)), false){}
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2017, 7, 3),"comment",  5000.0, 10.0, hashSetOf(TradeStrategy.HODL, TradeStrategy.BAD_TRADE)), false){}
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2017, 12, 18),"comment",  20000.0, 2.0, hashSetOf(TradeStrategy.ASAP_NO_LOSSES, TradeStrategy.BAD_TRADE)), false){}
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2018, 7, 3), "comment", 5000.0, 10.0, hashSetOf(TradeStrategy.DOUBLE_OUT, TradeStrategy.BAD_TRADE)), false){}
        TradeRepository.insertTradeAsync(Trade(0, 1, LocalDate.of(2018, 5, 2), "comment", 7500.0, 5.0, hashSetOf(TradeStrategy.ASAP_NO_LOSSES, TradeStrategy.BAD_TRADE)), false){}
    }
}