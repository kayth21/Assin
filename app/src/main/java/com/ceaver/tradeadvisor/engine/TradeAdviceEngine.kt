package com.ceaver.tradeadvisor.engine

import com.ceaver.adviceadvisor.advices.AdviceRepository
import com.ceaver.tradeadvisor.advices.Advice
import com.ceaver.tradeadvisor.services.TokenRepository
import com.ceaver.tradeadvisor.threading.BackgroundThreadExecutor
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeRepository
import java.time.LocalDate

object TradeAdviceEngine {

    fun run() {
        TradeRepository.loadAllTrades { onAllTradesLoaded(it) }
    }

    private fun onAllTradesLoaded(trades: List<Trade>) {
        // TODO groupby coinmarketcapId (to avoid multiple network connections for the same token)
        trades.forEach { BackgroundThreadExecutor.execute { TradeAnalyzer(it).analyze() } }
    }

    private class TradeAnalyzer(val trade: Trade) {
        fun analyze() {
            val purchasePrice = trade.purchasePrice
            val currentPrice = TokenRepository.lookupPrice(trade.coinmarketcapId)

            if (currentPrice > 2 * purchasePrice) {
                AdviceRepository.insertAdvice(Advice(0, LocalDate.now()))
            }
        }
    }

}