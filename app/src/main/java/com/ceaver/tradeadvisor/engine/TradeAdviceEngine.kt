package com.ceaver.tradeadvisor.engine

import com.ceaver.adviceadvisor.advices.AdviceRepository
import com.ceaver.tradeadvisor.advices.Advice
import com.ceaver.tradeadvisor.services.TokenRepository
import com.ceaver.tradeadvisor.threading.BackgroundThreadExecutor
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeEvents
import com.ceaver.tradeadvisor.trades.TradeRepository
import com.ceaver.tradeadvisor.trades.TradeStrategy
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.time.LocalDate

object TradeAdviceEngine {

    init {
        EventBus.getDefault().register(this)
    }

    @Subscribe
    fun onMessageEvent(event: TradeEvents.Update) {
        run()
    }

    @Subscribe
    fun onMessageEvent(event: TradeEvents.Insert) {
        run()
    }

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
            val advices = AdviceRepository.loadAdvicesFromTrade(trade.id)

            checkExistingAdvicesByStrategy(trade.strategy, advices, purchasePrice, currentPrice)
            checkNewAdviceByStrategy(advices, purchasePrice, currentPrice)
        }

        private fun checkNewAdviceByStrategy(advices: List<Advice>, purchasePrice: Double, currentPrice: Double) {
            if (advices.stream().noneMatch { it.strategy.equals(trade.strategy) } && trade.strategy.test(purchasePrice, currentPrice))
                AdviceRepository.insertAdvice(Advice(0, trade.id, LocalDate.now(), trade.strategy))
        }

        private fun checkExistingAdvicesByStrategy(strategy: TradeStrategy, advices: List<Advice>, purchasePrice: Double, currentPrice: Double) {
            advices.forEach {
                if (!it.strategy.equals(strategy) || !it.strategy.test(purchasePrice, currentPrice))
                    AdviceRepository.deleteAdvice(it)
            }
        }
    }

}