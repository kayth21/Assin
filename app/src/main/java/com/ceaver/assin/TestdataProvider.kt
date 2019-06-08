package com.ceaver.assin

import com.ceaver.assin.trades.Trade
import com.ceaver.assin.trades.TradeRepository
import java.time.LocalDate
import java.util.*

// TODO Temporary Helper - Remove!
object TestdataProvider {

    fun cleanDatabaseAndInsertSomeDataAfterwards() {
        TradeRepository.deleteAllTradesAsync {
            TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), Optional.of("USD"), Optional.of(50000.0), Optional.empty(), Optional.empty(), "Deposit USD"), false) {}
            TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), Optional.of("BTC"), Optional.of(10.0), Optional.empty(), Optional.empty(), "Deposit BTC"), false) {}
            TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), Optional.of("LTC"), Optional.of(500.0), Optional.of("BTC"), Optional.of(1.0), "Buy Shitcoin"), false) {}
            TradeRepository.insertTradeAsync(Trade(0, LocalDate.now(), Optional.empty(), Optional.empty(), Optional.of("USD"), Optional.of(10000.0), "Withdraw"), false) {}
        }
    }
}