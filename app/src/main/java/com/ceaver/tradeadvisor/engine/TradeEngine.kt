package com.ceaver.tradeadvisor.engine

import com.ceaver.adviceadvisor.advices.AdviceRepository
import com.ceaver.tradeadvisor.advices.Advice
import com.ceaver.tradeadvisor.services.TokenProvider
import com.ceaver.tradeadvisor.trades.TradeRepository
import java.util.*

object TradeEngine {

    fun run() {
//        for (trade in TradeRepository.loadTrades()) {
//            val purchasePrice = trade.purchasePrice
//            val currentPrice = TokenProvider.lookupPrice(trade.coinmarketcapId)
//
//            if (currentPrice > 2 * purchasePrice) {
//                AdviceRepository.insertAdvice(Advice(0, Date()))
//            }
//        }
    }
}