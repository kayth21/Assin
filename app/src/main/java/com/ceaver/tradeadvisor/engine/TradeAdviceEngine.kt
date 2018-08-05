package com.ceaver.tradeadvisor.engine

import com.ceaver.adviceadvisor.advices.AdviceRepository
import com.ceaver.tradeadvisor.advices.Advice
import com.ceaver.tradeadvisor.services.TokenRepository
import com.ceaver.tradeadvisor.trades.Trade
import com.ceaver.tradeadvisor.trades.TradeEvents
import com.ceaver.tradeadvisor.trades.TradeRepository
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.LocalDate

object TradeAdviceEngine {

    fun wakeup() {
        // hack because object is lazy initialized
    }

    init {
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: EngineEvents.Run) {
        runSynchronized()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: TradeEvents.Update) {
        runSynchronized()
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onMessageEvent(event: TradeEvents.Insert) {
        runSynchronized()
    }

    @Synchronized
    fun runSynchronized() {
        val allTrades = TradeRepository.loadAllTrades()
        allTrades.forEach { TradeAnalyzer(it).analyze() }
    }

    private class TradeAnalyzer(val trade: Trade) {
        fun analyze() {
            val purchasePrice = trade.purchasePrice
            val currentPrice = TokenRepository.lookupPrice(trade.coinmarketcapId)
            val advices = AdviceRepository.loadAdvicesOfTrade(trade.id)

            advices.forEach { advice ->
                if (trade.strategies.contains(advice.strategy).not() || advice.strategy.test(purchasePrice, currentPrice).not()) {
                    AdviceRepository.deleteAdvice(advice)
                }
            }

            trade.strategies.forEach { tradeStrategy ->
                if (advices.stream().noneMatch { advice -> advice.strategy == tradeStrategy } && tradeStrategy.test(purchasePrice, currentPrice)) {
                    AdviceRepository.insertAdvice(Advice(0, trade.id, LocalDate.now(), tradeStrategy))
                }
            }
        }
    }
}